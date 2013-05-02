package edu.cnt.message.handlers;

import edu.cnt.bitfield.BitFieldHandler;
import edu.cnt.common.Constants;
import edu.cnt.message.HandshakeMessage;
import edu.cnt.message.Messages;
import edu.cnt.message.P2PMessage;
import edu.cnt.peers.PeerHandler;
import edu.cnt.socket.SocketSender;

public class MessageGenerator {

	
	private MessageGenerator messageGenerator;
	public  MessageGenerator() {
		
		
	}
	
	public MessageGenerator getInstance() {
		if(messageGenerator==null)
			messageGenerator= new MessageGenerator();
		return messageGenerator;
	}
	
	public static HandshakeMessage generateHandshakeMessage(){
		HandshakeMessage handshake= new HandshakeMessage(new byte[10], PeerHandler.hostPeerID);
		return handshake;
		
	}
	
	/**
	 * @return
	 */
	public static P2PMessage generateBitFieldMessage(){
		//BitField.convertToByteArray(BitField.bitField);
		P2PMessage msg = new P2PMessage(Constants.MESSAGE_TYPE_BITFIELD, 0);
		return msg;
	}
	
	/**
	 * @param file
	 * @return
	 */
	public static P2PMessage generatePieceMessage(int seq){
		
		
		P2PMessage message= new P2PMessage(Constants.MESSAGE_TYPE_PIECE, seq);
		return message;
		
	}
	
	

	public static Messages generateInterestedMessage() {
		P2PMessage message= new P2PMessage(Constants.MESSAGE_TYPE_INTERESTED, 0);
		return message;
	}
	public static Messages generateNotInterestedMessage() {
		P2PMessage message= new P2PMessage(Constants.MESSAGE_TYPE_NOT_INTERESTED, 0);
		return message;
	}
	public static Messages generateChokeMessage() {
		P2PMessage message= new P2PMessage(Constants.MESSAGE_TYPE_CHOKE, 0);
		return message;
	}
	public static Messages generateUnchokeMessage() {
		P2PMessage message= new P2PMessage(Constants.MESSAGE_TYPE_UNCHOKE, 0);
		return message;
	}
	
	public static Messages generateHaveMessage(int seq1) {
		P2PMessage message= new P2PMessage(Constants.MESSAGE_TYPE_HAVE, seq1);
		return message;
	}
	
	public static Messages generateRequestMessage(String iP) {
		int seq = BitFieldHandler.processBitField(iP);
		System.out.println("Requested packet  ---- "+seq);
		P2PMessage message= new P2PMessage(Constants.MESSAGE_TYPE_REQUEST, seq);
		return message;
	}
	
	
	public static void sendMessage(String iP, Messages message) {
		SocketSender.sendMessage(iP, message);
		
	}

	

	

	

	

	
}
