package edu.cnt.common;

public class ConfigData {
	private static ConfigData config;
	private int FileSize;
	private int PieceSize;
	private String Filename;
	private int PreferredNeighbour;
	private int UnChokingInterval;
	private int OptimisticUnchokeInterval;
	public static ConfigData getConfig() {
		return config;
	}
	
	
	
	public static ConfigData getInstance() {
		if(config==null)
			config= new ConfigData();
		return config;
	}
	public ConfigData(){
		ReadConfig rcfg=new ReadConfig(Constants.CONFIGURATION_FILE_NAME);
		FileSize=Integer.parseInt(rcfg.getProperty(Constants.FILE_SIZE));
		PieceSize=Integer.parseInt(rcfg.getProperty(Constants.PIECE_SIZE));
		setPreferredNeighbour(Integer.parseInt(rcfg.getProperty(Constants.NO_OF_PREF_NEIGHBOURS))); 
		setUnChokingInterval(Integer.parseInt(rcfg.getProperty(Constants.UNCHOKING_INTERVAL)));
		setOptimisticUnchokeInterval(Integer.parseInt(rcfg.getProperty(Constants.OPTIMISTIC_UNCHOKE_INTERVAL)));

		Filename=rcfg.getProperty(Constants.FILE_NAME);	
		
		 if((FileSize%PieceSize)!=0)
		        noOfChunks=(FileSize/PieceSize)+1;
		        else
		        noOfChunks=FileSize/PieceSize;	
	}
	
	public int getFileSize() {
		return FileSize;
	}
	public int getPieceSize() {
		return PieceSize;
	}
	public String getFilename() {
		return Filename;
	}
	public int getNoOfChunks() {
		return noOfChunks;
	}
	public int getPreferredNeighbour() {
		return PreferredNeighbour;
	}



	public void setPreferredNeighbour(int preferredNeighbour) {
		PreferredNeighbour = preferredNeighbour;
	}
	public int getUnChokingInterval() {
		return UnChokingInterval;
	}



	public void setUnChokingInterval(int unChokingInterval) {
		UnChokingInterval = unChokingInterval;
	}
	public int getOptimisticUnchokeInterval() {
		return OptimisticUnchokeInterval;
	}



	public void setOptimisticUnchokeInterval(int optimisticUnchokeInterval) {
		OptimisticUnchokeInterval = optimisticUnchokeInterval;
	}
	private int noOfChunks;
}
