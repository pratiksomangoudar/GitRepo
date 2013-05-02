package edu.cnt.common;


/**
 * @author pratiksomanagoudar
 *
 */
public class Constants {

	public static final String PEER2PEERMESSAGE = "P2PMessage";
	public static final String HANDSHAKE = "HSMessage";
	public static final String FILE_INPUT_PATH = "/cise/homes/akaranth/peer_";
	//public static final String FILE_OUTPUT_PATH = "./peer_";
	public static final String FILE_SEPERATOR = "/";
	public static final String FILE_NAME = "FileName";
	public static final int CHUNKSIZE=256;
	
	public static final int MESSAGE_TYPE_LENGTH = 1;
	
	public static final String CONFIGURATION_FILE_NAME = "Common.cfg";
	public static final String FILE_SIZE = "FileSize";
	public static final String PIECE_SIZE = "PieceSize";
	
	public static final int MY_RECEIVER_PORT = 6000;
	public static final int MY_SENDER_PORT = 6000;
	public static final String MY_PEERID = "1002";
	
	public static final char MESSAGE_TYPE_BITFIELD = '5';
	public static final char MESSAGE_TYPE_INTERESTED = '2';
	public static final char MESSAGE_TYPE_NOT_INTERESTED = '3';
	public static final char MESSAGE_TYPE_PIECE = '7';
	public static final char MESSAGE_TYPE_CHOKE='0';
	public static final char MESSAGE_TYPE_UNCHOKE='1';
	public static final char MESSAGE_TYPE_REQUEST = '6';
	public static final char MESSAGE_TYPE_HAVE = '4';
	public static final String PEERINFO_FILE_NAME = "peerInfo.cfg";
	public static final String NO_OF_PREF_NEIGHBOURS = "NumberOfPreferredNeighbors";
	public static final String UNCHOKING_INTERVAL = "UnchokingInterval";
	public static final String OPTIMISTIC_UNCHOKE_INTERVAL= "OptimisticUnchokingInterval";
	
}
