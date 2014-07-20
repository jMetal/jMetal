package org.uma.jmetal.util;

import java.io.*;
import java.util.logging.Level;

public class FileUtils {
  static public void appendObjectToFile(String fileName, Object object) {
    FileOutputStream fos;
    try {
      fos = new FileOutputStream(fileName, true);
      OutputStreamWriter osw = new OutputStreamWriter(fos);
      BufferedWriter bw = new BufferedWriter(osw);

      bw.write(object.toString());
      bw.newLine();
      bw.close();
    } catch (FileNotFoundException e) {
      Configuration.logger.log(Level.SEVERE, "Error", e);
    } catch (IOException e) {
      Configuration.logger.log(Level.SEVERE, "Error", e);
    }
  }

  static public void createEmtpyFile(String fileName) {
    FileOutputStream fos;
    try {
      fos = new FileOutputStream(fileName, false);
      OutputStreamWriter osw = new OutputStreamWriter(fos);
      BufferedWriter bw = new BufferedWriter(osw);

      bw.close();
    } catch (FileNotFoundException e) {
      Configuration.logger.log(Level.SEVERE, "Error", e);
    } catch (IOException e) {
      Configuration.logger.log(Level.SEVERE, "Error", e);
    }
  }
 /*
 FIXME: To be implemented
  static public SolutionSet readFunctionValuesFromFile(String fileName)
    throws IOException {
    File file = new File(fileName);
    FileReader fileReader = new FileReader(file);
    BufferedReader bufferedReader = new BufferedReader(fileReader);

    int numberOfObjecives ;
    SolutionSet solutionSet ;

    String line = bufferedReader.readLine();
    if (line == null) {
      solutionSet = new SolutionSet(0) ;
    } else {
      String[] values = line.split(" ") ;
      numberOfObjecives = values.length ;

      while (line != null) {

      }
    }


    return solutionSet ;
  }
  */
}
