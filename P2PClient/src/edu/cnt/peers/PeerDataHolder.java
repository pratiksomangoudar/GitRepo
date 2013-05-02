package edu.cnt.peers;

import java.util.ArrayList;
import java.util.Collections;

public class PeerDataHolder implements Comparable<PeerDataHolder> {
	private String peerIP;
	private String peerID;
	private long data;
	
	
	
	public PeerDataHolder(String peerIP, long data) {
		super();
		this.peerIP = peerIP;
		this.peerID= PeerHandler.IpPeerMap.get(peerIP);
		this.data = data;
	}
	/**
	 * @return the peerIP
	 */
	public String getPeerIP() {
		return peerIP;
	}
	/**
	 * @param peerIP the peerIP to set
	 */
	public void setPeerIP(String peerIP) {
		this.peerIP = peerIP;
	}
	/**
	 * @return the peerID
	 */
	public String getPeerID() {
		return peerID;
	}
	/**
	 * @param peerID the peerID to set
	 */
	public void setPeerID(String peerID) {
		this.peerID = peerID;
	}
	/**
	 * @return the data
	 */
	public Long getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(Long data) {
		this.data = data;
	}
	@Override
	public int compareTo(PeerDataHolder other) {
		// TODO Auto-generated method stub
		return (int) ((int) this.data-other.getData());
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return peerIP;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.peerIP.equals(obj);
	}

}
