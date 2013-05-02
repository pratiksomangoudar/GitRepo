/**
 * 
 */
package edu.cnt.process;

import edu.cnt.message.Messages;
import edu.cnt.message.handlers.MessageGenerator;
import edu.cnt.message.handlers.MessageProcessor;
import edu.cnt.peers.PeerHandler;

/**
 * @author pratiksomanagoudar
 *
 */
public class HandshakeHandler implements Runnable {

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while(checkHandShakesReceived()){


			for(String IP: PeerHandler.IpPeerMap.keySet()){
				if(IP.equals(PeerHandler.hostIP)){
					break;
				}
				if(!MessageProcessor.handShakeReceivedMap.containsKey(IP) || !MessageProcessor.handShakeReceivedMap.get(IP)){
					Messages msg = MessageGenerator.generateHandshakeMessage();
					MessageGenerator.sendMessage(IP, msg);
					MessageProcessor.handShakeSentMap.put(IP, true);
				}
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean checkHandShakesReceived() {
		for(String IP: PeerHandler.IpPeerMap.keySet()){
			if(IP.equals(PeerHandler.hostIP)){
				break;
			}
			if(!MessageProcessor.handShakeReceivedMap.containsKey(IP) || !MessageProcessor.handShakeReceivedMap.get(IP)){
				return true;
			}
			
		}
		return false;
	}
}
