package edu.cnt.peers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

import edu.cnt.common.Constants;
import edu.cnt.common.SortedArrayList;
import edu.cnt.message.P2PMessage;

/**
 * @author pratiksomanagoudar
 *
 */
public class PeerHandler {
	private static ArrayList<String> interestedPeers = new ArrayList<String>();
	private static HashMap<String, Integer> requestTracker = new HashMap<String, Integer>();
	public static boolean isFilePresent;
	public static LinkedHashMap<String,String> IpPeerMap=null;
	public static LinkedHashMap<String,String> IpPortmap=null;
	public static HashMap<String, String> peerIPMap =  new HashMap<String, String>();
	public static String hostIP;
	public static int hostPort;
	public static String hostPeerID;
	private static String optimisticPeer = new String();
	private static SortedArrayList<PeerDataHolder> sortedDataPeers= new SortedArrayList<PeerDataHolder>();
	//public static Map<String, Long> peerDataMap=  null;

	//public static ArrayList<Peers> peer =  new ArrayList<Peers>(); 
	/**
	 * 
	 */
	private static ArrayList<String> unChokedPeers = new ArrayList<String>();
	public static boolean isFirstStart = true;
	public static ArrayList<String> haveBroadCastPeers= new ArrayList<String>();
	private static Vector<String> peerInterested = new Vector<String>();
	/**
	 * @return the peerInterested
	 */
	public synchronized static Vector<String> getPeerInterested() {
		return peerInterested;
	}

	public static void addInteredtedPeer(String IP){
		interestedPeers.add(IP);
	}

	public static synchronized void removeInteredtedPeer(String IP){
		if(interestedPeers.contains(IP)){
			interestedPeers.remove(IP);

		}

	}
	
	public synchronized static boolean noRequestsSentToIP(String IP) {
		return !requestTracker.containsKey(IP);
	}
	
	
	public static synchronized ArrayList<String>	getInterestedArray(){
		return interestedPeers;
	}

	public synchronized static void addRequestToTracker(String IP, Integer seq){

		requestTracker.put(IP, seq);
		System.out.println("ADDED REQUEST"+ seq+" IN TRACKER FOR "+IP );
	}
	public synchronized static void removeRequestFromTracker(String IP){

		if(requestTracker.containsKey(IP)){
			requestTracker.remove(IP);
			System.out.println("REMOVE REQUEST IN TRACKER FOR "+IP );

		}

	}

	public static void setSelfDetails() {
			//hostPeerID= Constants.MY_PEERID;
			hostIP = peerIPMap.get(hostPeerID); 
			hostPort = Integer.parseInt((String)IpPortmap.get(hostIP));

	}
	public synchronized static void updatePeerDataReceived(String IP, P2PMessage msg){
		for(PeerDataHolder peer : sortedDataPeers){
			if(peer.equals(IP)){
				peer.setData(peer.getData()+msg.getMsgLength());
			}
		}
	}

	public static synchronized ArrayList< String> getPreferredNeighbour(int size){
		ArrayList<String> neighbours =  new ArrayList<String>();
		int count=sortedDataPeers.size()-1;
		while(neighbours.size()!=size|| count < 0){
			PeerDataHolder PD= sortedDataPeers.get(count);
			if(interestedPeers.contains(PD.getPeerIP())){
				neighbours.add(PD.getPeerIP());
		}
		count--;
		}
		return neighbours;	
	}
	public static void initializePeerData() {
		sortedDataPeers = new SortedArrayList<PeerDataHolder>();
		for (String ip : PeerHandler.IpPeerMap.keySet()) {
			if(!ip.equals(hostIP))
			sortedDataPeers.insertSorted(new PeerDataHolder(ip, 0));
		}		
	}

	public static synchronized ArrayList<String> getUnChokedPeers() {
		return unChokedPeers;
	}

	public static synchronized void setUnChokedPeers(ArrayList<String> unChokedPeers) {
		PeerHandler.unChokedPeers = unChokedPeers;
	}
	
	public static synchronized boolean isNotRequestedSequence(int seq){
	return !requestTracker.containsValue(seq);	
	}
	public static synchronized boolean isUnchokedPeer(String IP){
		return unChokedPeers.contains(IP);
		
	}
	
	public static synchronized void addPeerToSentInterested(String IP) {
		if(!peerInterested.contains(IP)&&!IP.equals(hostIP)){
			peerInterested.add(IP);
		}
	}
	public static synchronized void removePeerToSentInterested(String IP) {
		if(peerInterested.contains(IP)){
			peerInterested.remove(IP);
		}
	}

	public synchronized static String getOptimisticPeer() {
		return optimisticPeer;
	}

	public synchronized static void setOptimisticPeer(String optimisticPeer) {
		PeerHandler.optimisticPeer = optimisticPeer;
	}
}
