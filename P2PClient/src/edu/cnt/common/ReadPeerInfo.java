package edu.cnt.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import edu.cnt.peers.PeerHandler;


public class ReadPeerInfo {
	
	private static ReadPeerInfo peerConfReader=null;
	private InetAddress IP;
	private String ipStr=null;
	
	public void parseFile()
	{
		try{
			BufferedReader configFileReader =  new BufferedReader(new InputStreamReader(new FileInputStream(Constants.PEERINFO_FILE_NAME)));
			
			PeerHandler.IpPeerMap=new LinkedHashMap<String, String>();
			PeerHandler.IpPortmap=new LinkedHashMap<String, String>();
			PeerHandler.peerIPMap= new HashMap<String, String>();
			//IP=getSelfIP();
			//ipStr=IP.getHostAddress();
			String line=configFileReader.readLine();
			
			while(line!=null)
			{
				String tokens[] = line.trim().split(" ");
				if(PeerHandler.hostPeerID.equals(tokens[0]))
				{
					if(tokens[3].equals("1"))
					PeerHandler.isFilePresent=true;
				}
				java.net.InetAddress inetAdd =
						java.net.InetAddress.getByName(tokens[1]);
				tokens[1]=inetAdd.getHostAddress();
				System.out.println("Config Read IP "+tokens[1]);
				PeerHandler.IpPeerMap.put(tokens[1],tokens[0]);
				PeerHandler.peerIPMap.put(tokens[0], tokens[1]);
				PeerHandler.IpPortmap.put(tokens[1],tokens[2]);
				//System.out.println(tokens[0] +" IP "+ tokens[1] + " Port" + tokens[2] + "isthere" + tokens[3] + "  variable " + PeerHandler.isFilePresent);
				line=configFileReader.readLine();
			}
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//return true;
	}

//	private InetAddress getSelfIP()
//	{
//		InetAddress IP=null;
//		try {
//			IP = InetAddress.getLocalHost();
//			//System.out.println("IP of my system is := "+IP.getHostAddress());
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return IP;
//	}
	/*public static void main(String args[])
	{
		ReadPeerInfo r1=new ReadPeerInfo();
		r1.parseFile();
	}*/
}
