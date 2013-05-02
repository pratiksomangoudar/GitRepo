/**
 * 
 */
package edu.cnt.message.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;

import edu.cnt.bitfield.BitFieldHandler;
import edu.cnt.common.ConfigData;
import edu.cnt.common.Constants;
import edu.cnt.common.LogManager;
import edu.cnt.filehandler.FileHandler;
import edu.cnt.message.Messages;
import edu.cnt.message.P2PMessage;
import edu.cnt.peers.PeerHandler;

/**
 * @author pratiksomanagoudar
 *
 */
public class MessageProcessor implements Runnable{
	public static Vector<MessageContainer> messageQueue= new Vector<MessageContainer>();
	public static HashMap<String, Boolean> handShakeSentMap = new HashMap<String, Boolean>();
	public static HashMap<String, Boolean> bitFieldMap = new HashMap<String, Boolean>();
	public static HashMap<String, Boolean> handShakeReceivedMap= new HashMap<String, Boolean>();


	public synchronized MessageContainer getNextMessage(){
		if(messageQueue.size() != 0) {
			MessageContainer mc = messageQueue.get(0);
			messageQueue.remove(0);
			System.out.println("returning node");
			return mc;
		}
		return null;
	}
	public static synchronized void addMessage(MessageContainer mc){
		System.out.println("adding container");
		messageQueue.add(mc);
	}
	private boolean isCompleted = false;
	private int count =0;


	public void processIncomingMessage() {

	}


	public void run() {
		while(true) {
			if(isCompleted){
				
				System.out.println("MINE IS COMPLETED --- others are ");
				for (int[] bits : BitFieldHandler.getPeerBitFieldMap().values()) {
					
					for (int i = 0; i < bits.length; i++) {
						System.out.print(" "+bits[i]);
					}
					System.out.println("\n");
					
				}
				
				
				if(BitFieldHandler.OthersCompleted()){
					System.out.println("OTHERS IS ALSO COMPLETED");
					break;
				}
				
			}
			
			
			MessageContainer container = this.getNextMessage();

			if(container != null) {
				Messages msg = container.getMsg();
				String IP= container.getIP();

				if(!IP.equals(PeerHandler.hostIP)){


					if(msg.getMessageType().equalsIgnoreCase(Constants.HANDSHAKE)  && handShakeSentMap.get(container.getIP()) !=null &&handShakeSentMap.get(container.getIP()) == true) {
						System.out.println("HANDSHAKE Message Received from <-----------"+IP);
						// hand shake received as reply to hand shake
						// send bitfield
						


						if(bitFieldMap.get(IP)==null || bitFieldMap.get(IP)==false ){
							Messages message = MessageGenerator.generateBitFieldMessage();
							bitFieldMap.put(IP, true);
							handShakeReceivedMap.put(IP,true);
							MessageGenerator.sendMessage(IP, message);
							System.out.println("BITFIELD Message Sent to -------->"+IP);

						}
					}
					if(msg.getMessageType().equalsIgnoreCase(Constants.HANDSHAKE)  && handShakeSentMap.get(container.getIP()) == null) {
						// hand shake 
						// reply by handshake
						handShakeSentMap.put(IP, true);
						handShakeReceivedMap.put(IP,true);
						System.out.println("HANDSHAKE Message Received from"+IP);
						Messages handshakeMSG = MessageGenerator.generateHandshakeMessage();
						MessageGenerator.sendMessage(IP, handshakeMSG);
					}


					if(msg.getMessageType().equalsIgnoreCase(Constants.PEER2PEERMESSAGE)) {	

						P2PMessage dMsg = (P2PMessage)msg;
						//System.out.println("P2PMessageFound   --- "+dMsg.getP2PMessageType());
						switch (dMsg.getP2PMessageType()) {
						case Constants.MESSAGE_TYPE_BITFIELD:
							// Bit field message received

							System.out.println("BITFIELD Message received from <---------- "+IP);
							//store bitfields for other peers
							BitFieldHandler.addOtherBitField(IP, dMsg.getPayload().getBitData());
							// send your Bitfield
							if(bitFieldMap.get(IP)==null){
								Messages message = MessageGenerator.generateBitFieldMessage();
								MessageGenerator.sendMessage(IP, message);
								bitFieldMap.put(IP, true);
								System.out.println("BITFIELD Message Sent to -------->"+IP);
							}
							System.out.println("Process Bitfields here");

							int[] bitfld= dMsg.getPayload().getBitData();
							for (int i = 0; i < bitfld.length; i++) {
								System.out.print(" "+bitfld[i]);
							}
							System.out.println("\n");

							// check if interested by getting a sequence number
							int seq = BitFieldHandler.processBitField(IP);
							//if seq no seq required sent 'not interested' else interested
							if(seq >= 0){
								System.out.println("Send INTERESTED to ------->"+IP);
								Messages message = MessageGenerator.generateInterestedMessage();
								MessageGenerator.sendMessage(IP, message);
								PeerHandler.addPeerToSentInterested(IP);
							}
							else{
								System.out.println("Send NOT INTERESTED --------->"+IP);
								Messages message = MessageGenerator.generateNotInterestedMessage();
								MessageGenerator.sendMessage(IP, message);
								PeerHandler.removePeerToSentInterested(IP);

							}
							if(!PeerHandler.haveBroadCastPeers.contains(IP)){
								PeerHandler.haveBroadCastPeers.add(IP);
							}
							break;


						case Constants.MESSAGE_TYPE_INTERESTED:


							System.out.println("INTERESTED Message Received from <---------"+IP);
							// add interested peers to map
							LogManager.getInstance().logger.log(Level.INFO, "Peer "+PeerHandler.hostPeerID
									+" received the 'interested' message from "+PeerHandler.IpPeerMap.get(IP));
							PeerHandler.addInteredtedPeer(IP);

							boolean unchoke= calculateChoke(IP);
							if(unchoke){
								System.out.println("Send UNCHOKE to -------->"+IP);
								Messages message = MessageGenerator.generateUnchokeMessage();
								MessageGenerator.sendMessage(IP, message);
							}
							else{
								System.out.println("Send CHOKE to ------->"+IP);
								Messages message = MessageGenerator.generateChokeMessage();
								MessageGenerator.sendMessage(IP, message);
							}
							break;

						case Constants.MESSAGE_TYPE_NOT_INTERESTED:
							System.out.println("NOT INTERESTED message received from <---------"+IP);
							LogManager.getInstance().logger.log(Level.INFO, "Peer "+PeerHandler.hostPeerID
									+" received the 'not interested' message from "+PeerHandler.IpPeerMap.get(IP));
							PeerHandler.removeInteredtedPeer(IP);
							
							if(PeerHandler.isFilePresent || BitFieldHandler.isTransferComplete() && PeerHandler.getInterestedArray().size()==0){
								isCompleted= true;
							}
							
							break;	


						case Constants.MESSAGE_TYPE_CHOKE:
							System.out.println("CHOKE Message Received from <-----------"+IP);
							// REMOVE  REQUEST FROM QUEUE
							LogManager.getInstance().logger.log(Level.INFO, "Peer "+PeerHandler.hostPeerID
									+" is choked by "+PeerHandler.IpPeerMap.get(IP));

							PeerHandler.removeRequestFromTracker(IP);
							break;

						case Constants.MESSAGE_TYPE_UNCHOKE:
							System.out.println("UNCHOKE Message Received from <----------"+IP);

							LogManager.getInstance().logger.log(Level.INFO, "Peer "+PeerHandler.hostPeerID
									+" is unchoked by "+PeerHandler.IpPeerMap.get(IP));
							if (PeerHandler.noRequestsSentToIP(IP)) {
							Messages request = MessageGenerator.generateRequestMessage(IP);

							P2PMessage p2pmm = (P2PMessage) request; 

							// if files needed send request 
							if(p2pmm.getPayload().getPieceIndex()<0){
								request = MessageGenerator.generateNotInterestedMessage();
								System.out.println("Not Interested Sent to ---->"+IP);
								PeerHandler.removePeerToSentInterested(IP);
							}
							else{
								PeerHandler.addRequestToTracker(IP,p2pmm.getPayload().getPieceIndex() );

								System.out.println("Request Sent for "+p2pmm.getPayload().getPieceIndex()+" to ----"+IP);
							}
							MessageGenerator.sendMessage(IP, request);
							}
							else{
								System.out.println("EXECUTION FAILED DUE TO REQUEST STUCK");
							}
							break;

						case Constants.MESSAGE_TYPE_REQUEST:
							int seqn =dMsg.getPayload().getPieceIndex();
							System.out.println("REQUEST Message Received from <--------------"+IP+" for-----"+seqn);
							if(PeerHandler.isUnchokedPeer(IP) || PeerHandler.getOptimisticPeer().equals(IP)){

								Messages piece = MessageGenerator.generatePieceMessage(seqn);
								MessageGenerator.sendMessage(IP, piece);
								// Packet message send
								System.out.println("Sending PEICE Message"+((P2PMessage)piece).getPayload().getPieceIndex() +" to -------->"+IP);
							}
							else{
								System.out.println("Not replied to Request Message----!!!!!! for "+seqn+ " when interested peers is "+PeerHandler.isUnchokedPeer(IP) +" and optimistic "+PeerHandler.getOptimisticPeer().equals(IP));
								System.out.println("Send CHOKE to ------->"+IP);
								Messages message = MessageGenerator.generateChokeMessage();
								MessageGenerator.sendMessage(IP, message);
							}
							break;	
						case Constants.MESSAGE_TYPE_PIECE:

							System.out.println("piece Message Received <<-----------from "+IP+" for-----"+dMsg.getPayload().getPieceIndex());
							FileHandler fileHandler=new FileHandler(Constants.FILE_INPUT_PATH+PeerHandler.hostPeerID+File.separator, ConfigData.getInstance().getFilename());
							byte[] filebytes = dMsg.getPayload().getData();
							int seq1= dMsg.getPayload().getPieceIndex();
							fileHandler.writeFileWithBytes(filebytes, seq1);
							BitFieldHandler.setSetSelfBitFieldBySequence(seq1);
							PeerHandler.removeRequestFromTracker(IP);
							///EDIT

							PeerHandler.updatePeerDataReceived(IP, dMsg);
							Messages have = MessageGenerator.generateHaveMessage(seq1);
							LogManager.getInstance().logger.log(Level.INFO, "Peer "+PeerHandler.hostPeerID
									+" has downloaded the piece "+dMsg.getPayload().getPieceIndex()+" from "+PeerHandler.IpPeerMap.get(IP)+"\n" +
									"Now the number of pieces it has is "+ BitFieldHandler.countDownloadedData());


							for (String ip : PeerHandler.haveBroadCastPeers) {

								MessageGenerator.sendMessage(ip, have);
								System.out.println("HAVE Sent for "+seq1+" to ---->>"+ip);
							}
							if(BitFieldHandler.isTransferComplete()){

								FileHandler fileHandler2=new FileHandler(Constants.FILE_INPUT_PATH+PeerHandler.hostPeerID+File.separator, ConfigData.getInstance().getFilename());
								fileHandler2.mergeFile(ConfigData.getInstance().getNoOfChunks());
								// Inform other peers that you are not interested
								ArrayList<String> deleteIP = new ArrayList<String>(); 
								for (String ip : PeerHandler.getPeerInterested()) {
									Messages request2 = MessageGenerator.generateNotInterestedMessage();
									MessageGenerator.sendMessage(ip, request2);	
									deleteIP.add(ip);
								}
								for(String ip:deleteIP){
									PeerHandler.removePeerToSentInterested(ip);
								}
								LogManager.getInstance().logger.log(Level.INFO, "Peer "+PeerHandler.hostPeerID
										+" has downloaded the complete file.");
								
									System.out.println("COMPLETE-----Hurray!!!!!!!!");
									isCompleted  = true;
									
								
							}
							
						if (PeerHandler.noRequestsSentToIP(IP)) {
							
						
							Messages request1 = MessageGenerator.generateRequestMessage(IP);

							P2PMessage p2pmm1 = (P2PMessage) request1; 

							// if files needed send request 
							if(p2pmm1.getPayload().getPieceIndex()<0){
								request1 = MessageGenerator.generateNotInterestedMessage();
								System.out.println("Not Interested Sent to ---->"+IP);
								PeerHandler.removePeerToSentInterested(IP);
							}
							else{
								PeerHandler.addRequestToTracker(IP,p2pmm1.getPayload().getPieceIndex() );

								System.out.println("Request Sent for "+p2pmm1.getPayload().getPieceIndex()+" to ----"+IP);
							}
							MessageGenerator.sendMessage(IP, request1);
						}
							break;

						case Constants.MESSAGE_TYPE_HAVE:
							LogManager.getInstance().logger.log(Level.INFO, "Peer "+PeerHandler.hostPeerID
									+" received the 'have' message from "+PeerHandler.IpPeerMap.get(IP) + " for the piece "+dMsg.getPayload().getPieceIndex());
							if(bitFieldMap.containsKey(IP)&& BitFieldHandler.isBitfieldPresent(IP)){
								System.out.println("HAVE Message Recieved from <----------"+IP);
								BitFieldHandler.setOtherBitFieldBySequence(IP, dMsg.getPayload().getPieceIndex());
								int seq2 = BitFieldHandler.processBitField(IP);
								
								if(seq2 >= 0){
									System.out.println("Send INTERESTED  to -------->"+IP);
									Messages message = MessageGenerator.generateInterestedMessage();
									MessageGenerator.sendMessage(IP, message);
									PeerHandler.addPeerToSentInterested(IP);
								}
								else{
									System.out.println("Send NOT INTERESTED  to--------->"+IP);
									Messages message = MessageGenerator.generateNotInterestedMessage();
									MessageGenerator.sendMessage(IP, message);
									PeerHandler.removePeerToSentInterested(IP);
								}
							}
						default:
							break;
						}
					}
				}
			}
		}
		
		System.out.println("Closing Message Processor......");
	}
	private boolean calculateChoke(String iP) {
		if(PeerHandler.getUnChokedPeers()!=null){
			return PeerHandler.getUnChokedPeers().contains(iP) || PeerHandler.getOptimisticPeer().equals(iP);
		}
		return false;

	}


}

