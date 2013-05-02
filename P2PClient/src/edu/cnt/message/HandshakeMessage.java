package edu.cnt.message;
import java.io.Serializable;

import edu.cnt.common.Constants;


/**
 * @author pratiksomanagoudar
 *
 */

public class HandshakeMessage implements Messages,Serializable{
	private String header = "CEN5501C2008SPRING";
	private byte[] zeroBits;
	private String peerID;
	public HandshakeMessage(byte[] zeroBits, String peerId) {
		this.zeroBits=zeroBits;
		this.peerID=peerId;
	}
	/**
	 * @return
	 */
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public byte[] getBitFields() {
		return zeroBits;
	}
	public void setBitFields(byte[] bitFields) {
		this.zeroBits = bitFields;
	}
	public String getPeerID() {
		return peerID;
	}
	public void setPeerID(String peerID) {
		this.peerID = peerID;
	}
	@Override
	public String getMessageType() {
		// TODO Auto-generated method stub
		return Constants.HANDSHAKE;
	}

	/**
	 * @param args
	 */
	

}
