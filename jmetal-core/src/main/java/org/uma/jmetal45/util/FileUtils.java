package org.uma.jmetal45.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class FileUtils {
  static public void appendObjectToFile(String fileName, Object object) {
    FileOutputStream fileOutputStream;
    try {
      fileOutputStream = new FileOutputStream(fileName, true);
      OutputStreamWriter osw = new OutputStreamWriter(fileOutputStream);
      BufferedWriter bw = new BufferedWriter(osw);

      bw.write(object.toString());
      bw.newLine();
      bw.close();
    } catch (FileNotFoundException e) {
      JMetalLogger.logger.log(Level.SEVERE, "Error", e);
    } catch (IOException e) {
      JMetalLogger.logger.log(Level.SEVERE, "Error", e);
    }
  }

  static public void createEmtpyFile(String fileName) {
    FileOutputStream fileOutputStream;
    try {
      fileOutputStream = new FileOutputStream(fileName, false);
      OutputStreamWriter osw = new OutputStreamWriter(fileOutputStream);
      BufferedWriter bw = new BufferedWriter(osw);

      bw.close();
    } catch (FileNotFoundException e) {
      JMetalLogger.logger.log(Level.SEVERE, "Error", e);
    } catch (IOException e) {
      JMetalLogger.logger.log(Level.SEVERE, "Error", e);
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

  public static double[][] readFrontIntoArray(String path) {
    try {
      // Open the file
      FileInputStream inputStream = new FileInputStream(path);
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

      List<double[]> list = new ArrayList<double[]>();
      int numberOfObjectives = 0;
      String aux = bufferedReader.readLine();
      while (aux != null) {
        StringTokenizer st = new StringTokenizer(aux);
        int i = 0;
        numberOfObjectives = st.countTokens();
        double[] vector = new double[st.countTokens()];
        while (st.hasMoreTokens()) {
          double value = new Double(st.nextToken());
          vector[i] = value;
          i++;
        }
        list.add(vector);
        aux = bufferedReader.readLine();
      }

      bufferedReader.close();

      double[][] front = new double[list.size()][numberOfObjectives];
      for (int i = 0; i < list.size(); i++) {
        front[i] = list.get(i);
      }
      return front;

    } catch (Exception e) {
      JMetalLogger.logger.log(
        Level.SEVERE,
        "readFront() crashed reading for file: " + path,
        e);
    }
    return new double[0][0];
  }

  public static void deleteFile(String file) {
    File file1 = new File(file);
    if (file1.exists()) {
      JMetalLogger.logger.info("File " + file + " exist.");

      if (file1.isDirectory()) {
        JMetalLogger.logger.info("File " + file + " is a directory. Deleting directory.");
        if (file1.delete()) {
          JMetalLogger.logger.info("Directory successfully deleted.");
        } else {
          JMetalLogger.logger.info("Error deleting directory.");
        }
      } else {
        JMetalLogger.logger.info("File " + file + " is a file. Deleting file.");
        if (file1.delete()) {
          JMetalLogger.logger.info("File successfully deleted.");
        } else {
          JMetalLogger.logger.info("Error deleting file.");
        }
      }
    }
  }
}
