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

package jmetal.problems.mqap;

import java.io.*;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *  @author Juan J. Durillo
 *  @version 1.0
 *  This class provides some functionalities for reading mQAP from file
 */
public class ReadInstance {
  
  private String fileName_;         // name of the file containing the instance
  private int [][]   a_matrix;   // a matrix describing the problem
  private int [][][] b_matrixs;  // k b matrixs describing the problem
  private int facilities_ = -1;          // number of facilities
  private int objectives_ = -1;          // number of objectives  
  private String singleObjectiveFirstLine_ = "";
  
  /**
   * @author Juan J. Durillo
   * Creates a new ReadInstance for the mQAP problem
   * @param name: the name of the file
   */
  public ReadInstance(String name) {
    fileName_ = name;
  } // ReadInstance
  
  
  /**
   * Reads the instance from file
   * This method should be called for reading all the data from file.
   */
  public void loadInstance() {
    
    try {
      File archivo = new File(fileName_);
      FileReader fr = null;
      BufferedReader br = null;        
      fr = new FileReader (archivo);
      br = new BufferedReader(fr);

     // File reading
     String line;
     line = br.readLine(); // reading the first line (special case)     
     
     // first line must contain the number of utilities and objectives
     StringTokenizer st = new StringTokenizer(line);
     String newLine = "";
     // looking for the tokens facilities and objectives
     while (st.hasMoreTokens()) {
       String newToken = st.nextToken();
       if (newToken.toUpperCase().contains("FACILITIES")) {// facilities found
         newLine += newToken + " ";
         String aux = "";
         do {
          try {
            aux = st.nextToken();
            facilities_ = new Integer(aux);
            newLine += aux + " ";
          } catch (NumberFormatException ne) {
            newLine += aux + " ";
            continue;            
          }
         } while (facilities_ < 0);       
       } else if (newToken.toUpperCase().contains("OBJECTIVES")) {
         newLine += newToken + " ";
         String aux = "";
         do {
          try {
            aux = st.nextToken();
            objectives_ = new Integer(aux);
            newLine += "1 ";
          } catch (NumberFormatException ne) {
            newLine += aux + " ";
            continue;            
          }
         } while (objectives_ < 0);       
       } else {
        newLine += newToken + " "; 
       }         
     }
     //System.out.println(newLine);
     singleObjectiveFirstLine_ = newLine;
     
     // reading A matrix (discarding empty lines on the way)
     a_matrix = new int[facilities_][facilities_];
     line = br.readLine();
     while (line.isEmpty())
       line = br.readLine();
     
     for (int i = 0; i < facilities_; i++) {
       st = new StringTokenizer(line);
       for (int j = 0; j < facilities_; j++) {
         a_matrix[i][j] = new Integer(st.nextToken());
       }
       line = br.readLine();
     }
              
     // reading B matrixes (discarding empty lines on the way)
     b_matrixs = new int[objectives_][facilities_][facilities_];
     for (int k = 0; k < objectives_; k++) {
       while (line.isEmpty())
         line = br.readLine();
       for (int i = 0; i < facilities_; i++) {
         st = new StringTokenizer(line);
         for (int j = 0; j < facilities_; j++) {
           b_matrixs[k][i][j] = new Integer(st.nextToken());
         }
         line = br.readLine();
       }
     }
     
     
     // comprobation
     for (int i = 0; i < facilities_; i++) {        
        for (int j = 0; j < facilities_; j++) {
          //  System.out.print(b_matrixs[0][i][j]+" ");             
      }
      //System.out.println();
     }
     
     
     // at this point the instances has been read
    } catch (FileNotFoundException ex) {
      Logger.getLogger(ReadInstance.class.getName()).log(Level.SEVERE, "The file doesn't exist", ex);
    } catch (IOException ex2) {
      Logger.getLogger(ReadInstance.class.getName()).log(Level.SEVERE, "Error reading from file", ex2);
    }                          
  } // loadInstance
  
  /**
   * @author Juan J. Durillo
   * This methods creates a single-objective instance using a weighted aggregative approach
   * and write all the information to file
   * @param vector: a vector containing the weights for the aggregative approach
   */
  public void createSingleObjectiveInstance(int [] weights) {
    // safe comprobation: is the number of weights == objectives_?
    if (weights.length != objectives_) {
      Logger.getLogger(ReadInstance.class.getName()).log(Level.SEVERE,
              "The number of weights and number of objectives don't match");
        
    } // safe comprobation
    
    // generating the intance
    try {
      String name = "";
      for (int k = 0; k < objectives_; k++) {
        name += "_"+weights[k];
      }
      System.out.println(name);
      System.out.println(fileName_+name);
      FileOutputStream fos   = new FileOutputStream(fileName_+name)     ;
      OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
      BufferedWriter bw      = new BufferedWriter(osw)        ;
                       
      
      // generating a new first line with weight information
      /* Fran's sofware does not need this, but the number of facilities
      String newFirstLine = "";
      for (int k = 0; k < objectives_; k++) {
        newFirstLine += " weight " + k + " " + weights[k]; 
      }
      bw.write(singleObjectiveFirstLine_+ newFirstLine);
      bw.newLine(); */
      bw.write(facilities_ + "");
      bw.newLine();
      bw.newLine();
      
      
      // writting matrix_a
      String line ;
      for (int i = 0; i < facilities_; i++) {
        line = "";
        for (int j = 0; j < facilities_;j++) {
          line += a_matrix[i][j] + " ";
        }
        bw.write(line);
        bw.newLine();
      }
      bw.newLine();
      
      // writting aggregate veresion of matrix_b
      for (int i = 0; i < facilities_; i++) {
        line = "";
        for (int j = 0; j < facilities_; j++) {
           int aggregateValue = 0;
           for (int k = 0; k < objectives_; k++) {
            aggregateValue += weights[k] * b_matrixs[k][i][j];
           }
           line += aggregateValue + " ";
        }
        bw.write(line);
        bw.newLine();
      }

      bw.close();
                              
    } catch (FileNotFoundException ex) {
      Logger.getLogger(ReadInstance.class.getName()).log(Level.SEVERE, "The file cannot be created", ex);
    } catch (IOException ex1) {
      Logger.getLogger(ReadInstance.class.getName()).log(Level.SEVERE, "Error writting in the file", ex1);
    }        
  }
   
  int [][] get_a_Matrix() {
    return a_matrix;
  }
  
  int [][][] get_b_Matrixs() {
    return b_matrixs;
  }
  
  int getNumberOfObjectives() {
    return objectives_;
  }
  
  int getNumberOfFacilities() {
    return facilities_;
  }
      
  public static void main(String [] args) {
    ReadInstance ri = new ReadInstance(args[0]);
    ri.loadInstance();
    
    if (args.length > 1) {
      // second argument should indicate if we do want to generte the aggregatives
      if (args[1].toUpperCase().contains("Y")) {
        if (ri.getNumberOfObjectives() > 3) {
          Logger.getLogger(ReadInstance.class.getName()).log(Level.SEVERE, "Aggregative approache with more than 2 objectives not implemented yet");          
        } else if (ri.getNumberOfObjectives() == 3) {                                       
            System.out.println("3 objetivos");
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
                System.out.println("initUniformWeight: failed when reading for file containing the weight");
                e.printStackTrace();
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
    //int [] weights = {5, 5};
    //ri.createSingleObjectiveInstance(weights);
  }
}
