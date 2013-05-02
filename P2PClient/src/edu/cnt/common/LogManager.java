package edu.cnt.common;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import edu.cnt.peers.PeerHandler;

/**
 * @author akhilkaranth
 *
 */
public class LogManager{
	private static LogManager log;
    public static Logger logger=null;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static LogManager getInstance(){
		if(log==null)
			log= new LogManager();
    	return log; 
    }
	public LogManager(){
		logger=Logger.getLogger("MyLog");
		FileHandler fh;	
		try {
		      // This block configure the logger with handler and formatter
			 String filename="log_peer_" + PeerHandler.hostPeerID + ".log";
			 final DateFormat df=new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS");
		      fh = new FileHandler(filename, true);
		     
		      fh.setFormatter(new Formatter() {
				
				@Override
				public String format(LogRecord record) {
					StringBuffer buf = new StringBuffer(1000);
		            buf.append("\n");
		            
					buf.append(df.format(new Date(record.getMillis()))).append(" - ");
		            buf.append(' ');
		            //buf.append(' ');
		            buf.append(formatMessage(record));
		            return buf.toString();
				}
			});
		      //  SimpleFormatter formatter = new SimpleFormatter();
		      //  formatter.format="%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n";
		      //  fh.setFormatter(formatter);
		      logger.addHandler(fh);
		      logger.setLevel(Level.ALL);
		}  catch (SecurityException e) {
		      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	}
}	

	

