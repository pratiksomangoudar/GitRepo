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
public class FileMerger {

	private String filePath;
	private File file;
	private String outpath;
	private String fileName;
	private int chunks;

	/** To merge Temp files on completion
	 * @param inputPath
	 * @param outPath
	 */
	public FileMerger(String inputPath,String outPath, String name, int noOfChunks) {

		filePath = inputPath;
		file = new File(outPath+name);
		this.outpath=outPath;
		fileName=name;
		this.chunks=noOfChunks;

	}

	public boolean mergeFiles() {
		int count=0;
		try {
			FileInputStream fin;
			FileOutputStream fout=new FileOutputStream(file, true);
			while(count<chunks){
				File tempFile= new File(filePath+count+fileName);
				long readlength=tempFile.length();
				fin= new FileInputStream(tempFile);
				byte[] tempBuffer= new byte[(int) readlength];
				int read = fin.read(tempBuffer, 0, (int)readlength) ;
				fout.write(tempBuffer);
				fout.flush();
				fin.close();
				count++;
			}
			fout.close();
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
//		// TODO Auto-generated method stub
//		FileMerger fm= new FileMerger("./output/", "./input/", "text.pdf", 54);
//		fm.mergeFiles();
//	}

}
