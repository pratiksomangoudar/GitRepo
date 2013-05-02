package edu.cnt.peers;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;
import java.util.logging.Level;

import edu.cnt.common.ConfigData;
import edu.cnt.common.LogManager;
import edu.cnt.message.Messages;
import edu.cnt.message.handlers.MessageGenerator;

/**
 * @author pratiksomanagoudar
 *
 */
public class OptimisticUnchoke  extends TimerTask{



	private ArrayList<String> interested;
	private boolean isFirstRun = true;

	public String anyItem(ArrayList<String> interested)
	{
		String str;

		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(interested.size());
		str = interested.get(index);
		return str;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		ArrayList<String> unchoked=PeerHandler.getUnChokedPeers();
		ArrayList<String> possibleOptimistic = new ArrayList<String>();
		interested=PeerHandler.getInterestedArray();

		if(unchoked!=null && interested!=null){ 
			if((interested.size()-unchoked.size())>0)	{

				for (int i = 0; i < interested.size(); i++) {
					{
						if(!unchoked.contains(interested.get(i))){
							possibleOptimistic.add(interested.get(i));
						}
					}
				}
				if(possibleOptimistic.size()!=0){
					String optimistic = anyItem(possibleOptimistic);
					String previousOptimistic = PeerHandler.getOptimisticPeer();
					PeerHandler.setOptimisticPeer(optimistic);
System.out.println("Optimistic Peers are ...>>>>>>>>>... "+ PeerHandler.getOptimisticPeer());
					LogManager.getInstance().logger.log(Level.INFO, "Peer "+PeerHandler.hostPeerID
							+" has optimistically unchoked neighbor "+PeerHandler.IpPeerMap.get(PeerHandler.getOptimisticPeer()));
					if(isFirstRun){
						
							Messages msg= MessageGenerator.generateUnchokeMessage();
							MessageGenerator.sendMessage(optimistic, msg);
							System.out.println("Sent UNCHOKE (VIA OPTIMISTIC) to ------->"+optimistic);
							PeerHandler.setOptimisticPeer(optimistic);

						isFirstRun=false;
					}
					else{
						if(!previousOptimistic.equals(optimistic)){
							
						
							Messages msg2= MessageGenerator.generateChokeMessage();
							MessageGenerator.sendMessage(previousOptimistic, msg2);
							System.out.println("Sent CHOKE (VIA OPTIMISTIC) to ------->"+previousOptimistic);
						
							
							Messages msg= MessageGenerator.generateUnchokeMessage();
							MessageGenerator.sendMessage(optimistic, msg);
							System.out.println("Sent UNCHOKE (VIA OPTIMISTIC) to ------->"+optimistic);
						
							
						}
						
					}
					
				}
			}
		}


		//}


	}
}
