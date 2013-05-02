package edu.cnt.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

import edu.cnt.common.Constants;
import edu.cnt.common.LogManager;
import edu.cnt.message.Messages;
import edu.cnt.message.handlers.MessageContainer;
import edu.cnt.message.handlers.MessageProcessor;
import edu.cnt.peers.PeerHandler;

/**
 * @author pratiksomanagoudar
 *
 */
public class SocketReceiver implements Runnable {

	private ServerSocket serverSocket;
	private ObjectInputStream ois;

	public SocketReceiver() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public void startReciever() throws IOException{
		System.out.println("Reciever Port Set to : "+PeerHandler.hostPort);
		ServerSocket serverSocket = new ServerSocket(PeerHandler.hostPort);
		while(true) {
			try {
				Socket clientSocket = new Socket();
				ObjectInputStream ois;
				clientSocket = serverSocket.accept();
				
				String clientIp = clientSocket.getRemoteSocketAddress().toString();
				clientIp = clientIp.substring(1, clientIp.indexOf(':'));
				System.out.println(clientIp);
				LogManager.getInstance().logger.log(Level.INFO, "Peer "+PeerHandler.hostPeerID
						+" is connected from Peer "+PeerHandler.IpPeerMap.get(clientIp));
				while(true){
					//System.out.println(clientSocket.getInputStream().available());
				if(clientSocket.getInputStream().available() != 0) {
					ois = new ObjectInputStream(clientSocket.getInputStream());
					
					Messages msg = (Messages)ois.readObject();
					System.out.println("Receiving..........");
					MessageContainer mc = new MessageContainer();
					mc.setIP(clientIp);
					mc.setMsg(msg);
					MessageProcessor.addMessage(mc);
					ois.close();
					clientSocket.close();
					break;
				}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}


	@Override
	public void run() {
		try {
			startReciever();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}




