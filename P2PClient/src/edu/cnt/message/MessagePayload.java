package edu.cnt.message;

import java.io.File;
import java.io.Serializable;

import edu.cnt.common.ConfigData;
import edu.cnt.common.Constants;
import edu.cnt.filehandler.FileHandler;
import edu.cnt.peers.PeerHandler;


/**
 * @author pratiksomanagoudar
 *
 */
public class MessagePayload implements Serializable {
	private byte[] fileData;

	//private byte[] bitData;
	private int[] bitData;
	private int pieceIndex;

	/**
	 * @return
	 */
	public byte[] getData() {
		return fileData;
	}

	/**
	 * @param data
	 */
	public void setData(File file) {
		FileHandler fileHandler=new FileHandler(Constants.FILE_INPUT_PATH+PeerHandler.hostPeerID+File.separator, ConfigData.getInstance().getFilename());

		this.fileData = fileHandler.getFileBytes(file);
		
	}

	

	/**
	 * @param bits
	 */
	public MessagePayload(int[] bits) {
		setBitData(bits);
	}

	public MessagePayload(File file, int seq) {
		FileHandler fileHandler=new FileHandler(Constants.FILE_INPUT_PATH+PeerHandler.hostPeerID+File.separator, ConfigData.getInstance().getFilename());

		this.fileData = fileHandler.getFileBytes(file);
		this.setPieceIndex(seq);
	}
	public MessagePayload(int seq) {
		this.setPieceIndex(seq);
	}

	public int[] getBitData() {
		return bitData;
	}

	public void setBitData(int[] bitData) {
		this.bitData = bitData;
	}

	public int getPieceIndex() {
		return pieceIndex;
	}

	public void setPieceIndex(int pieceIndex) {
		this.pieceIndex = pieceIndex;
	}
}
