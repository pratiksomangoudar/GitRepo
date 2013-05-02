package edu.cnt.common;

import java.util.Properties;


public class ReadConfig {

	/**
	 * @param args
	 */
	   Properties configFile;
	   public ReadConfig(String filename)
	   {
		configFile = new java.util.Properties();
		try {
		  configFile.load(this.getClass().getClassLoader().getResourceAsStream(filename));
		}catch(Exception eta){
		    eta.printStackTrace();
		}
	   }
	 
	   public String getProperty(String key)
	   {
		String value = this.configFile.getProperty(key);
		return value;
	   }
//	   public static void main(String args[])
//	   {
//		   ReadConfig Rf=new ReadConfig("Common.cfg");
//		   System.out.println(Rf.getProperty("FileName"));
//		   System.out.println(Rf.getProperty("FileSize"));
//	   }
}
