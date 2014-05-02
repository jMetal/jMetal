package jmetal.util;

import java.io.*;

public class FileUtils {
  static public void appendObjectToFile(String fileName, Object object) {
  	FileOutputStream fos;
    try {
	    fos = new FileOutputStream(fileName, true);
	    OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
	    BufferedWriter bw      = new BufferedWriter(osw)        ;
	                      
	    bw.write(object.toString());
	    bw.newLine();
	    bw.close();
    } catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    } catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
  }
  
  static public void createEmtpyFile(String fileName) {
  	FileOutputStream fos;
    try {
	    fos = new FileOutputStream(fileName, false);
	    OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
	    BufferedWriter bw      = new BufferedWriter(osw)        ;
	                      
	    bw.close();
    } catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    } catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
  }
}
