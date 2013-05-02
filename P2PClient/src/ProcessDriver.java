

import java.io.File;
import java.util.Timer;

import edu.cnt.bitfield.BitFieldHandler;
import edu.cnt.common.ConfigData;
import edu.cnt.common.Constants;
import edu.cnt.common.ReadPeerInfo;
import edu.cnt.filehandler.FileSplitter;
import edu.cnt.message.Messages;
import edu.cnt.message.handlers.MessageGenerator;
import edu.cnt.message.handlers.MessageProcessor;
import edu.cnt.peers.OptimisticUnchoke;
import edu.cnt.peers.PeerHandler;
import edu.cnt.peers.UnchokeTask;
import edu.cnt.process.HandshakeHandler;
import edu.cnt.socket.SocketReceiver;

/**
 * @author pratiksomanagoudar
 *
 */
public class ProcessDriver {
	
	private static ConfigData config;
	
	public static void main(String[] args) throws InterruptedException {
		try{
		String hostID = args[0];
		System.out.println("Peer info sent  ::: : "+hostID);
PeerHandler.hostPeerID= hostID;
		}
catch(Exception e){
System.out.println("Arguments not rec");
}
		
		
		initialise();
	MessageProcessor messageProcessor = new MessageProcessor();
	Thread processor = new Thread(messageProcessor);
	processor.start();
	
	SocketReceiver receiver = new SocketReceiver();
	Thread receiverThread = new Thread(receiver);
	receiverThread.start();
	
	HandshakeHandler handlerThread  = new HandshakeHandler();
	Thread handshakeThread = new Thread(handlerThread);
	handshakeThread.start();
	
	processor.join();
	System.out.println("Closing other threads.......");
	receiverThread.stop();
	handshakeThread.stop();
	
	System.out.println("Exiting application.........");
	System.exit(0);
	
	

}

	private static void initialise() {
		initialiseConfig();
		initialiseDataStack();
		intialiseFiles();
		initialiseUnchokeAlgo();
	}

	

	private static void initialiseDataStack() {
		//EDIT
		PeerHandler.initializePeerData();
		
	}

	private static void initialiseConfig() {
		config = ConfigData.getInstance();
		new ReadPeerInfo().parseFile();
		PeerHandler.setSelfDetails();
	}

	private static void intialiseFiles() {

		System.out.println(PeerHandler.hostPeerID +"   "+ PeerHandler.isFilePresent);
		if(PeerHandler.isFilePresent){

		System.out.println("IS FILE PRESESNT" +Constants.FILE_INPUT_PATH+PeerHandler.hostPeerID+File.separator+config.getFilename() );

		FileSplitter fs= new FileSplitter
				(Constants.FILE_INPUT_PATH+PeerHandler.hostPeerID+File.separator+config.getFilename(), 
						Constants.FILE_INPUT_PATH+PeerHandler.hostPeerID+File.separator, 
						config.getPieceSize());
		fs.splitFile();
		
		
		}

		System.out.println("Generating bit field ..... ");
		BitFieldHandler.generateBitField();
		int [] a =BitFieldHandler.getBitField();
		for (int i = 0; i < a.length; i++) {
			System.out.print(" "+a[i]);
		}
		System.out.println("\n");
	}
	private static void initialiseUnchokeAlgo() {
		// TODO Auto-generated method stub
		Timer t1= new Timer();
		Timer t2= new Timer();
		System.out.println( ConfigData.getInstance().getUnChokingInterval()*1000);
		t1.schedule(new UnchokeTask(), 0, ConfigData.getInstance().getUnChokingInterval()*1000);
		t2.schedule(new OptimisticUnchoke(), 0, ConfigData.getInstance().getOptimisticUnchokeInterval()*1000);
	}
}
