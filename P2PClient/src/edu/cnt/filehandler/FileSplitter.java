package edu.cnt.filehandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author pratiksomanagoudar
 *
 */
public class FileSplitter {

	private String filePath;
	private long chunkSize;
	private String outpath;
	private File file;
	private long length;
	private String fileName;
	
	public FileSplitter(String inputPath,String outPath, long chunksize) {
		filePath = inputPath;
		chunkSize = chunksize;
		file = new File(filePath);
		this.outpath=outPath;
		length= file.length();
		fileName=file.getName();
	}
	public boolean splitFile(){
		int fileCount=0;
		int readLength=(int) chunkSize;
		long length= this.length;
		int sizeRead=0;
		try {
			FileInputStream fin = new FileInputStream(file);
			FileOutputStream fout;
			while (length>0)
			{
				if (length <= chunkSize) {
		            readLength=(int) length;
		        }
				byte[] tempbuffer= new byte[readLength];
				sizeRead=fin.read(tempbuffer, 0, (int)readLength);
				String tempFileName=outpath+fileCount+fileName;
				fout= new FileOutputStream(new File(tempFileName));
				fout.write(tempbuffer);
				fout.flush();
				fout.close();
				fileCount++;
				length-=sizeRead;
			}
			fin.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} 
		
		return true;
		
	}
	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		FileSplitter fs= new FileSplitter("./input/text.pdf", "./output/", 256*1024);
//		fs.splitFile();
//	}

}
