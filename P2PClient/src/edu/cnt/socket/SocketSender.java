package edu.cnt.socket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;

import edu.cnt.common.Constants;
import edu.cnt.common.LogManager;
import edu.cnt.message.Messages;
import edu.cnt.message.P2PMessage;
import edu.cnt.peers.PeerHandler;

/**
 * @author pratiksomanagoudar
 *
 */
public class SocketSender {

	public static void sendMessage(String IP, Messages message) {
		ObjectOutputStream os;
		try {
			/// LOGGER CHANGE
			
			P2PMessage p2p=null;
//			if(message.getMessageType().equals(Constants.PEER2PEERMESSAGE)){
//				p2p= (P2PMessage)message;
//			LogManager.getInstance().logger.log(Level.INFO, "Peer "+PeerHandler.hostPeerID
//					+" makes a connection to Peer "+PeerHandler.IpPeerMap.get(IP) + " ##### " +p2p.getP2PMessageType());
//			
//		}else
			LogManager.getInstance().logger.log(Level.INFO, "Peer "+PeerHandler.hostPeerID
					+" makes a connection to Peer "+PeerHandler.IpPeerMap.get(IP));
		
			Socket socket = new Socket(IP,Integer.parseInt((String)PeerHandler.IpPortmap.get(IP)));
			os = new ObjectOutputStream(socket.getOutputStream());
			System.out.println(" Sending ........."+message.getMessageType()+"........."+IP+":"+(String)PeerHandler.IpPortmap.get(IP));
			os.writeObject(message);
			os.flush();
			os.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
