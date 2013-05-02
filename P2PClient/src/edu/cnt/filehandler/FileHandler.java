/**
 * 
 */
package edu.cnt.filehandler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author pratiksomanagoudar
 *
 */

public class FileHandler {

	private String filepath;
	private String fileName;


	/**
	 * 
	 */
	public FileHandler(String path,String name) {
		this.filepath= path;

		this.fileName= name;
	}

	public boolean isFilePresent(int seq){
		File file= new File(filepath+seq+fileName);

		return file.exists();
	}


	public File getFileBySeq(int seqNumber) {
		File file= new File(filepath+seqNumber+fileName);
		if(file.exists())
			return file;
		else
			return null;

	}
	/**
	 * @param args
	 */public byte[] getFileBytes(File file) {

		 byte[] byteArray = new byte[(int)file.length()];

		 FileInputStream fin;
		 try {
			 fin = new FileInputStream(file);
			 BufferedInputStream bin = new BufferedInputStream(fin);  
			 bin.read(byteArray, 0, byteArray.length);
			 bin.close();
			 fin.close();

		 } catch (FileNotFoundException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 } catch (IOException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }  
		 return byteArray;

	 }

	 public boolean writeFileBySequence(File file ,int seq,int chunksize) {



		 try {
			 FileOutputStream fos = new FileOutputStream(filepath+seq+fileName);
			 ObjectOutputStream os = new ObjectOutputStream(fos);
			 os.writeObject(file);
			 os.close();
			 fos.close();
		 } catch (IOException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
			 return false;
		 }

		 return true;

	 }

	 public void writeFileWithBytes(byte[] bytes , int seq){
		 FileOutputStream fos;
		 try {
			 fos = new FileOutputStream(filepath+seq+fileName);
			 fos.write(bytes);
			 fos.close();
		 } catch (FileNotFoundException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 } catch (IOException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 };
	 }

	 public boolean mergeFile(int noOfChucks){
		 FileMerger fm= new FileMerger(filepath, filepath, fileName, noOfChucks);
		return fm.mergeFiles();
	 }
}
