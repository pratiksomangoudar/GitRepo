package edu.cnt.peers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.logging.Level;

import edu.cnt.bitfield.BitFieldHandler;
import edu.cnt.common.ConfigData;
import edu.cnt.common.LogManager;
import edu.cnt.message.Messages;
import edu.cnt.message.handlers.MessageGenerator;

/**
 * @author pratiksomanagoudar
 *
 */
public class UnchokeTask  extends TimerTask{

	private ArrayList<String> interested;
	private ArrayList<String> unChoke; 
	private static boolean isFirstRun= true;
	@Override
	public void run() {
		unChoke= new ArrayList<String>();
		int neighboursNeeded= ConfigData.getInstance().getPreferredNeighbour();
		//get interested
		interested = PeerHandler.getInterestedArray();
		if(interested != null && interested.size()!=0){
			if(PeerHandler.isFilePresent || BitFieldHandler.isTransferComplete() ){
				// All files are present so unchoke using random scheme
				if(interested.size()>neighboursNeeded){
					for (int i = 0; i < neighboursNeeded; i++) {
						String ip = anyItem(interested);	
						unChoke.add(ip);
					}

				}
				else{
					unChoke=interested;
				}
			}
			else{
				if(interested.size()>neighboursNeeded){
					//EDIT

					//1 substract from intereseted
					// 2 initialize the data map.
					unChoke.addAll(PeerHandler.getPreferredNeighbour(neighboursNeeded));

				}
				else{
					unChoke=interested;
				}
			}

			System.out.println("UNCHOKED PEERS ARE >>>>>>>>>>>>"+unChoke.toString());
		ArrayList <String> ip = PeerHandler.getUnChokedPeers();
		ArrayList <String> peerinfo = new ArrayList<String>();
		for (int i = 0; i < ip.size(); i++) {
			peerinfo.add(PeerHandler.IpPeerMap.get(ip.get(i)));
		}
		System.out.println("Unchoked Peers are -----------"+unChoke.toString());
		LogManager.getInstance().logger.log(Level.INFO, "Peer "+PeerHandler.hostPeerID
				+" has preferred neighbours "+ peerinfo.toString());
		PeerHandler.initializePeerData();
			if(isFirstRun){
				for (String unChoke_ip : unChoke) {
					Messages msg= MessageGenerator.generateUnchokeMessage();
					MessageGenerator.sendMessage(unChoke_ip, msg);
					System.out.println("Sent UNCHOKE (AFTER ALGO) to ------->"+unChoke_ip);
				}
				isFirstRun=false;
			}
			else{
				ArrayList<String> previousUnchoked = PeerHandler.getUnChokedPeers();
				PeerHandler.setUnChokedPeers(unChoke);
				if(previousUnchoked!=null){
					
					for(String chokeIp: previousUnchoked){
						if(!unChoke.contains(chokeIp)&& !chokeIp.equals(PeerHandler.getOptimisticPeer())){
							Messages msg= MessageGenerator.generateChokeMessage();
							MessageGenerator.sendMessage(chokeIp, msg);
							System.out.println("Sent CHOKE (AFTER ALGO) to ------->"+chokeIp);

							
						}
					}
					for (String unChoke_ip : unChoke) {
						if((!previousUnchoked.contains(unChoke_ip) )&& (!unChoke_ip.equals(PeerHandler.getOptimisticPeer()))){
							Messages msg= MessageGenerator.generateUnchokeMessage();
							MessageGenerator.sendMessage(unChoke_ip, msg);

							
							System.out.println("Sent UNCHOKE (AFTER ALGO) to ------->"+unChoke_ip);
						}
					}

				}
			}
		}
		
		//EDIT
		
		
		
		

	}

	public String anyItem(ArrayList<String> interested)
	{
		String str;
		while(true){
			Random randomGenerator = new Random();
			int index = randomGenerator.nextInt(interested.size());
			str = interested.get(index);
			if(!unChoke.contains(str)){
				break;
			}
		}
		return str;
	}


}


