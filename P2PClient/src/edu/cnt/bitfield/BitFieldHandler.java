package edu.cnt.bitfield;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import edu.cnt.common.ConfigData;
import edu.cnt.common.Constants;
import edu.cnt.filehandler.FileHandler;
import edu.cnt.peers.PeerHandler;

public class BitFieldHandler {
	
	private static int bitField[];
	private static HashMap<String, int[]> peerBitFieldMap = new HashMap<String, int[]>();
	
	
	public synchronized static int[] getBitField() {
		return bitField;
	}

	public synchronized void setBitField(int[] bitField) {
		this.bitField = bitField;
	}

	
	public synchronized void setBitFieldBit(int seq){
		bitField[seq]=1;
	}
//	public static  byte[] getBitFieldByteArray() {
//		return bitFieldByteArray;
//	}
//
//	public void setBitFieldByteArray(byte[] bitFieldByteArray) {
//		this.bitFieldByteArray = bitFieldByteArray;
//	}

	public synchronized static  void generateBitField()
	{
		ConfigData config= ConfigData.getInstance();
		bitField = new int[ ConfigData.getInstance().getNoOfChunks()];
		FileHandler fileHandler=new FileHandler(Constants.FILE_INPUT_PATH+PeerHandler.hostPeerID+File.separator, config.getFilename());
		
		for (int i = 0; i < ConfigData.getInstance().getNoOfChunks(); i++) {
		if(fileHandler.isFilePresent(i)){
			bitField[i]=1;
		}
		else{
			bitField[i]=0;
		}
		}
		
		
		
//		int FileSize = config.getFileSize();
//		int PieceSize = config.getPieceSize();
//		String Filename = config.getFilename();
//		
//        int noOfChunks= config.getNoOfChunks();
//		if((FileSize%PieceSize)!=0)
//        noOfChunks=(FileSize/PieceSize)+1;
//        else
//        noOfChunks=FileSize/PieceSize;	
//        bitField=new int[noOfChunks];
//        FileHandler fh=new FileHandler(Constants.FILE_INPUT_PATH+PeerHandler.hostPeerID+Constants.FILE_SEPERATOR,Filename);
//        for(int i=0;i<noOfChunks-1;i++)
//        {
//        	if(fh.getFileBySeq(i)!=null)
//        	bitField[i]=1;
//        	else
//            bitField[i]=0; 	
//
//        }
       // convertToByteArray(bitField);
	}
	
//	public static void convertToByteArray(int bitf[])
//	{
//		ConfigData config= ConfigData.getInstance();
//		
//		int noOfChunks= config.getNoOfChunks();
//		System.out.println(noOfChunks);
//		int y_roundoff=noOfChunks;
//		String x=new String();
//		int[] byteInt = new int[(noOfChunks/8)+1];
//		bitFieldByteArray= new byte[(noOfChunks/8)+9];
//		int j=0;
//		for(int i=0;i<noOfChunks;i++)
//		{
//			x=x + bitField[i];
//			if((i+1)%8==0)
//			{
//				byteInt[j++]=Integer.parseInt(x,2);
//				x="";
//			}
//			if(noOfChunks%8!=0 && i==noOfChunks-1)
//			{
//			   while(y_roundoff%8!=0)
//			   { 
//				   x=x + 0;
//				   y_roundoff++;
//			   }
//			   byteInt[j]=Integer.parseInt(x,2);
//			}
//		}
//		for(int i=0;i<byteInt.length;i++){
//			bitFieldByteArray[i]=(byte)byteInt[i];
//		}
//	}
	
//	public static void main(String[] args) {
//		BitField bits= new BitField();
//		int [] b = {0,0,1,1,0,0,0,1,1};
//		
//		bits.setBitField(b);
//		bits.convertToByteArray(b);
//		
//		System.out.println(bits.getBitFieldByteArray()[0] +"    "+bits.getBitFieldByteArray()[1]);
//	}
	public synchronized static void addOtherBitField(String IP,int[] otherBF ){
		peerBitFieldMap.put(IP, otherBF) ;
	}
	public synchronized static int processBitField(String IP) {
		int []  otherBitField =  peerBitFieldMap.get(IP);
		ArrayList<Integer> sequence= new ArrayList<Integer>();
		for (int i = 0; i < otherBitField.length; i++) {
			if(otherBitField[i]==1 && bitField[i]==0){
				if( PeerHandler.isNotRequestedSequence(i)){
					sequence.add(i);
				}
			}
		}
		if(sequence.size()==0)
			return -1;
		else{
			Random rand = new Random();
			int seq=rand.nextInt(sequence.size());
			return sequence.get(seq);
		}
	}
	
//	public static synchronized int getRequestedSeq(String IP){
//		int[] peerBitfield = peerBitFieldMap.get(IP);
//		ArrayList<Integer> array= new ArrayList<Integer>(); 
//		for (int i = 0; i < peerBitfield.length; i++) {
//			if(peerBitfield[i]==1 && bitField[i]==0){
//				array.add(i);
//			}
//		}
//		if(array.isEmpty()){
//			return -1;
//		}
//		Random rand =  new Random();
//		return array.get(rand.nextInt(array.size()));
//		
//	}

	public static HashMap<String, int[]> getPeerBitFieldMap() {
		return peerBitFieldMap;
	}

	public synchronized static void setSetSelfBitFieldBySequence(int seq1) {
		bitField[seq1] = 1;
		System.out.println("Bitfield Modified -- set bit "+seq1+" to 1");
	}
	
	
	public synchronized static void setOtherBitFieldBySequence(String ip, int seq1){
		int[] peerBitfield = peerBitFieldMap.get(ip);
		peerBitfield[seq1] = 1;
		peerBitFieldMap.put(ip, peerBitfield);
	}
	
	public synchronized static boolean isTransferComplete(){
		for (int i = 0; i < bitField.length; i++) {
			if(bitField[i]==0)
				return false;
		}
		return true;
		
	}
	public synchronized static boolean isBitfieldPresent(String IP){
		return peerBitFieldMap.containsKey(IP);
		
	}

	public synchronized static int countDownloadedData(){
		int count =0;
		for (int i = 0; i < bitField.length; i++) {
			if(bitField[i]==1)
				count++;
		}
		return count;
		
	}
	
	public  synchronized static boolean OthersCompleted() {
		for (int[] bits : peerBitFieldMap.values()) {
			for (int i = 0; i < bits.length; i++) {
				if(bits[i]==0){
					return false;
				}
			}
		}
		System.out.println("Others also Completed......!!!");
		return true;
		
	}
//	public static void main(String[] args) {
//		BitFieldHandler.generateBitField();
//		System.out.println(Constants.FILE_INPUT_PATH+PeerHandler.hostPeerID+File.separator+BitFieldHandler.bitField[7]);
//	}
}
