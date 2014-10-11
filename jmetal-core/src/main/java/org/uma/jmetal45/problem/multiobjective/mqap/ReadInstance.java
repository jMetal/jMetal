//  ReadInstance.java
//
//  Author:
//       Juan J. Durillo <juan@dps.uibk.ac.at>
//
//  Copyright (c) 2011 Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal45.problem.multiobjective.mqap;

import org.uma.jmetal45.util.JMetalLogger;

import java.io.*;
import java.util.StringTokenizer;
import java.util.logging.Level;


/**
 * @author Juan J. Durillo
 * @version 1.0
 *          This class provides some functionalities for reading mQAP from file
 */
public class ReadInstance {
  private String fileName;
  private int [][] matrixA;
  private int [][][] matricesB;
  private int facilities = -1;
  private int objectives = -1;

  private String singleObjectiveFirstLine_ = "";

  /**
   * @param name the name of the file
   * @author Juan J. Durillo
   * Creates a new ReadInstance for the mQAP problem
   */
  public ReadInstance(String name) {
    fileName = name;
  }

  /**
   * Reads the instance from file
   * This method should be called for reading all the data from file.
   */
  public void loadInstance() {
    try {
      File archivo = new File(fileName);
      FileReader fr = null;
      BufferedReader br = null;
      fr = new FileReader(archivo);
      br = new BufferedReader(fr);

      // File reading
      String line;
      // reading the first line (special case)
      line = br.readLine();     

      // first line must contain the number of utilities and objectives
      StringTokenizer st = new StringTokenizer(line);
      String newLine = "";
      // looking for the tokens facilities and objectives
      while (st.hasMoreTokens()) {
        String newToken = st.nextToken();
        if (newToken.toUpperCase().contains("FACILITIES")) {
          newLine += newToken + " ";
          String aux = "";
          do {
            try {
              aux = st.nextToken();
              facilities = new Integer(aux);
              newLine += aux + " ";
            } catch (NumberFormatException ne) {
              JMetalLogger.logger.log(Level.WARNING, "Number Format Exception", ne);
              newLine += aux + " ";
              continue;
            }
          } while (facilities < 0);
        } else if (newToken.toUpperCase().contains("OBJECTIVES")) {
          newLine += newToken + " ";
          String aux = "";
          do {
            try {
              aux = st.nextToken();
              objectives = new Integer(aux);
              newLine += "1 ";
            } catch (NumberFormatException ne) {
              JMetalLogger.logger.log(Level.WARNING, "NumberFormatException", ne);
              newLine += aux + " ";
              continue;
            }
          } while (objectives < 0);
        } else {
          newLine += newToken + " "; 
        }         
      }
      //System.out.println(newLine);
      singleObjectiveFirstLine_ = newLine;

      // reading A matrix (discarding empty lines on the way)
      matrixA = new int[facilities][facilities];
      line = br.readLine();
      while (line.isEmpty()) {
        line = br.readLine();
      }

      for (int i = 0; i < facilities; i++) {
        st = new StringTokenizer(line);
        for (int j = 0; j < facilities; j++) {
          matrixA[i][j] = new Integer(st.nextToken());
        }
        line = br.readLine();
      }

      // reading B matrices (discarding empty lines on the way)
      matricesB = new int[objectives][facilities][facilities];
      for (int k = 0; k < objectives; k++) {
        while (line.isEmpty()) {
          line = br.readLine();
        }
        for (int i = 0; i < facilities; i++) {
          st = new StringTokenizer(line);
          for (int j = 0; j < facilities; j++) {
            matricesB[k][i][j] = new Integer(st.nextToken());
          }
          line = br.readLine();
        }
      }

      for (int i = 0; i < facilities; i++) {
        for (int j = 0; j < facilities; j++) {
        }
      }
      br.close();
    } catch (FileNotFoundException ex) {
      java.util.logging.Logger.getLogger(ReadInstance.class.getName())
      .log(Level.SEVERE, "The file doesn't exist", ex);
    } catch (IOException ex2) {
      java.util.logging.Logger.getLogger(ReadInstance.class.getName())
      .log(Level.SEVERE, "Error reading from file", ex2);
    }
  } 

  /**
   * @param weights vector containing the weights for the aggregative approach
   * @author Juan J. Durillo
   * This methods creates a single-objective instance using a weighted aggregative approach
   * and write all the information to file
   */
  public void createSingleObjectiveInstance(int[] weights) {
    // safe comprobation: is the number of weights == objectives?
    if (weights.length != objectives) {
      java.util.logging.Logger.getLogger(ReadInstance.class.getName()).log(Level.SEVERE,
          "The number of weights and number of objectives don't match");
    }

    // generating the intance
    try {
      String name = "";
      for (int k = 0; k < objectives; k++) {
        name += "_" + weights[k];
      }
      System.out.println(name);
      System.out.println(fileName +name);
      FileOutputStream fos   = new FileOutputStream(fileName +name)     ;
      OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
      BufferedWriter bw      = new BufferedWriter(osw)        ;

      bw.write(facilities + "");
      bw.newLine();
      bw.newLine();

      // writting matrix_a
      String line;
      for (int i = 0; i < facilities; i++) {
        line = "";
        for (int j = 0; j < facilities; j++) {
          line += matrixA[i][j] + " ";
        }
        bw.write(line);
        bw.newLine();
      }
      bw.newLine();

      // writting aggregate veresion of matrix_b
      for (int i = 0; i < facilities; i++) {
        line = "";
        for (int j = 0; j < facilities; j++) {
          int aggregateValue = 0;
          for (int k = 0; k < objectives; k++) {
            aggregateValue += weights[k] * matricesB[k][i][j];
          }
          line += aggregateValue + " ";
        }
        bw.write(line);
        bw.newLine();
      }

      bw.close();

    } catch (FileNotFoundException ex) {
      java.util.logging.Logger.getLogger(ReadInstance.class.getName())
      .log(Level.SEVERE, "The file cannot be created", ex);
    } catch (IOException ex1) {
      java.util.logging.Logger.getLogger(ReadInstance.class.getName())
      .log(Level.SEVERE, "Error writting in the file", ex1);
    }
  }

  int [][] getMatrixA() {
    return matrixA;
  }

  int [][][] getMatricesB() {
    return matricesB;
  }

  int getNumberOfObjectives() {
    return objectives;
  }

  int getNumberOfFacilities() {
    return facilities;
  }

  public static void main(String [] args) {
    ReadInstance ri = new ReadInstance(args[0]);
    ri.loadInstance();

    if (args.length > 1) {
      // second argument should indicate if we do want to generte the aggregatives
      if (args[1].toUpperCase().contains("Y")) {
        if (ri.getNumberOfObjectives() > 3) {
          java.util.logging.Logger.getLogger(ReadInstance.class.getName()).log(Level.SEVERE, "Aggregation approach with more than 2 objectives not implemented yet");
        } else if (ri.getNumberOfObjectives() == 3) {                                       
          try {
            // Open the file
            String dataFile = "/home/juan/Dropbox/workingOn/qap/W3D_100.dat";
            FileInputStream fis = new FileInputStream(dataFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            int numberOfObjectives = 0;
            String aux = br.readLine();
            while (aux != null) {
              StringTokenizer st = new StringTokenizer(aux);        
              numberOfObjectives = st.countTokens();
              int [] weight = new int[numberOfObjectives];
              for (int k = 0; k < numberOfObjectives; k++) {
                weight[k] =       (new Integer(st.nextToken()).intValue());
                System.out.println(weight[k]);
              }                                                
              ri.createSingleObjectiveInstance(weight);


              aux = br.readLine();          
            }
            br.close();
          } catch (Exception e) {
            JMetalLogger.logger.log(
                Level.SEVERE,
                "initializeUniformWeight: failed when reading for file containing the weight",
                e);
          }
        } else {
          int a = 100, b = 0;
          while (a >= 0) {
            int [] weight = {a, b};
            ri.createSingleObjectiveInstance(weight);
            a--;
            b = 100 - a;
          }
        }
      }
    }
  }
}
