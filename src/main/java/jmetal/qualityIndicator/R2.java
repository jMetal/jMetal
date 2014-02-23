//  R2.java
//
//  Author:
//       Juan J. Durillo <juanjo.durillo@gmail.com>
//
//  Copyright (c) 2013 Juan J. Durillo
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

package jmetal.qualityIndicator;

import jmetal.core.SolutionSet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class R2 {

  public jmetal.qualityIndicator.util.MetricsUtil utils_;
  double [][] matrix_ = null;
  double [][] lambda_ = null;
  int    nObj_         = 0;
  
  /**
  * Constructor
  * Creates a new instance of the R2 indicator for a problem with two objectives
  * and 100 lambda vectors
  */
  public R2() {
    utils_ = new jmetal.qualityIndicator.util.MetricsUtil();
    
    // by default it creates an R2 indicator for a two dimensions probllem and
    // uses only 100 weight vectors for the R2 computation
    nObj_ = 2;
    // generating the weights
    lambda_ = new double[100][2];
    for (int n = 0; n < 100; n++) {
        double a = 1.0 * n / (100 - 1); 
        lambda_[n][0] = a;
        lambda_[n][1] = 1 - a;
    } // for
    
    
  } // R2
  
  
  
  /**
  * Constructor
  * Creates a new instance of the R2 indicator for a problem with two objectives
  * and N lambda vectors
  */
  public R2(int nVectors) {
    utils_ = new jmetal.qualityIndicator.util.MetricsUtil();
    
    // by default it creates an R2 indicator for a two dimensions probllem and
    // uses only <code>nVectors</code> weight vectors for the R2 computation
    nObj_ = 2;
    // generating the weights
    lambda_ = new double[nVectors][2];
    for (int n = 0; n < nVectors; n++) {
        double a = 1.0 * n / (nVectors - 1);
        lambda_[n][0] = a;
        lambda_[n][1] = 1 - a;
    } // for
    
    
  } // R2
  
   
  
  /**
  * Constructor
  * Creates a new instance of the R2 indicator for nDimensiosn
  * It loads the weight vectors from the file fileName
  */
  public R2(int nObj, String file) {
    utils_ = new jmetal.qualityIndicator.util.MetricsUtil();
    // A file is indicated, the weights are taken from there
    
    // by default it creates an R2 indicator for a two dimensions probllem and
    // uses only <code>nVectors</code> weight vectors for the R2 computation
    nObj_ = nObj;
    // generating the weights
    
    // reading weights
    try {
        // Open the file
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        int numberOfObjectives = 0;
        int i = 0;
        int j = 0;
        String aux = br.readLine();
        LinkedList<double[]> list = new LinkedList<double[]>();
        while (aux != null) {
            StringTokenizer st = new StringTokenizer(aux);
            j = 0;
            numberOfObjectives = st.countTokens();
            double [] vector = new double[nObj];
            while (st.hasMoreTokens()) {
                double value = new Double(st.nextToken());
                vector[j++] = value;                                        
            }
            list.add(vector);
            aux = br.readLine();                
        }
        br.close();
            
        // convert the LinkedList into a vector
        lambda_ = new double[list.size()][];            
        int index = 0;
        for (double[] aList : list) lambda_[index++] = aList;
            
            
    } catch (Exception e) {
        System.out.println("initUniformWeight: failed when reading for file: " + file);
        e.printStackTrace();
    }                
  } // R2
  
  
  
   /**
   * Returns the R2 indicator value of a given front
   * @param front The front 
   * @param trueParetoFront The true Pareto front
   * @param numberOfObjectives The number of objectives
   * @param lambda A vector containing the lambda vectors for R2
   */
  private double 
  R2Withouth(double [][] approximation,double [][] paretoFront, int index) 
  {
        
    /**
     * Stores the maximum values of true Pareto front.
     */
    double [] maximumValue ;
    
    /**
     * Stores the minimum values of the true Pareto front.
     */
    double [] minimumValue ;
    
    /**
     * Stores the normalized front.
     */
    double [][] normalizedApproximation ;
    
    /**
     * Stores the normalized true Pareto front.
     */ 
    double [][] normalizedParetoFront ; 
    
    // STEP 1. Obtain the maximum and minimum values of the Pareto front
    maximumValue = utils_.getMaximumValues(paretoFront, nObj_);
    minimumValue = utils_.getMinimumValues(paretoFront, nObj_);
    
    // STEP 2. Get the normalized front and true Pareto fronts
    normalizedApproximation       = utils_.getNormalizedFront(approximation, 
                                                              maximumValue, 
                                                              minimumValue);
    normalizedParetoFront = utils_.getNormalizedFront(paretoFront, 
    		                                          maximumValue,
    		                                          minimumValue);
    
    // STEP 3. compute all the matrix of tchebicheff values if it is null
    //if (matrix_ == null) {
        matrix_ = new double[approximation.length][lambda_.length];
        for (int i = 0; i < approximation.length; i++) {
            for (int j = 0; j < lambda_.length; j++) {
                matrix_[i][j] = lambda_[j][0] * Math.abs(normalizedApproximation[i][0]); 
                for (int n = 1; n < nObj_; n++) {
                    matrix_[i][j] = Math.max(matrix_[i][j], lambda_[j][n] * Math.abs(normalizedApproximation[i][n]));
                }
            }
        }
    //}
    
        
    // STEP45. Compute the R2 value withouth the point
    double sumWithout = 0.0;     
    for (int i = 0; i < lambda_.length; i++) {
        
        double tmp;
        if (index != 0) {
            tmp = matrix_[0][i];
        } else {
            tmp = matrix_[1][i];
        }
        for (int j = 0; j < approximation.length; j++) {
            if ( j != index) {
                tmp = Math.min(tmp, matrix_[j][i]);
            }
        }
        sumWithout += tmp;
    }
    
    // STEP 5. Return the R2 value
    return sumWithout / (double) lambda_.length;
            
  } // R2
  
   /**
   * Returns the element contributing the most to the R2 indicator
   * @param front The front 
   * @param trueParetoFront The true Pareto front
   * @param numberOfObjectives The number of objectives
   * @param lambda A vector containing the lambda vectors for R2
   */
  public int 
  getBest(double [][] approximation,double [][] paretoFront) 
  {
        int     index_best = -1;
        double  value = Double.NEGATIVE_INFINITY;
        
        for (int i = 0; i < approximation.length; i++) {
            double aux = this.R2Withouth(approximation, paretoFront, i);
            if (aux > value) {
                index_best = i;
                value = aux;
            }
        }  
            
        return index_best;
  } // getBest 
  

   /**
   * Returns the element contributing the less to the R2
   * @param front The front 
   * @param trueParetoFront The true Pareto front
   * @param numberOfObjectives The number of objectives
   * @param lambda A vector containing the lambda vectors for R2
   */
  public int 
  getWorst(double [][] approximation,double [][] paretoFront) 
  {
        int     index_worst = -1;
        double  value = Double.POSITIVE_INFINITY;
        
        for (int i = 0; i < approximation.length; i++) {
            double aux = this.R2Withouth(approximation, paretoFront, i);
            if (aux < value) {
                index_worst = i;
                value = aux;
            }
        }  
            
        return index_worst;
  } // getWorst
  

   /**
   * Returns the element contributing the most to the R2
   * @param front A solution set     
   */
  public int 
  getBest(SolutionSet set) 
  {
      double [][] approximationFront = set.writeObjectivesToMatrix();
      double [][] trueFront          = set.writeObjectivesToMatrix();

      return this.getBest(approximationFront, trueFront);
  } // getWorst
  
  
   /**
   * Returns the element contributing the less to the R2
   * @param front The front 
   * @param trueParetoFront The true Pareto front      
   */
  public int 
  getWorst(SolutionSet set) 
  {
      double [][] approximationFront = set.writeObjectivesToMatrix();
      double [][] trueFront          = set.writeObjectivesToMatrix();

      return this.getWorst(approximationFront, trueFront);
  } // getWorst
  
  
  
  
   /**
   * Returns the element contributing the most to the R2 indicator
   * @param front The front 
   * @param trueParetoFront The true Pareto front
   * @param numberOfObjectives The number of objectives
   * @param lambda A vector containing the lambda vectors for R2
   */
  public int []
  getNBest(double [][] approximation,double [][] paretoFront, int N) 
  {
        int  []    index_bests = new int[approximation.length];
        double [] values       = new double[approximation.length];
                                
        
        for (int i = 0; i < approximation.length; i++) {
            values[i]      = this.R2Withouth(approximation, paretoFront, i);
            index_bests[i] = i;
        } // for
        
        
        // sorting the values and index_bests
        for (int i  = 0; i < approximation.length; i++) {
            for (int j = i; j < approximation.length; j++) {
                if (values[j] < values[i]) {
                    double aux = values[j];
                    values[j]  = values[i];
                    values[i]  = aux;
                    
                    int aux_index = index_bests[j];
                    index_bests[j]   = index_bests[i];
                    index_bests[i]   = aux_index;
                }
            }
        }
            
        int [] res = new int[N];
        for (int i = 0; i< N; i++) {
            res[i] = index_bests[i];
        }
        
        return res;
  } // getBest
            
       
  /**
   * Returns the indexes of the N best solutions according to this indicator
   * @param SolutionSet the solution set for which the best solutions are computed
   */
  public int []
  getNBest(SolutionSet set, int N) {
      double [][] approximationFront = set.writeObjectivesToMatrix();
      double [][] trueFront          = set.writeObjectivesToMatrix();
      
      return this.getNBest(approximationFront,trueFront,N);

  } // getNBest
  
  
  
   /**
   * Returns the R2 indicator value of a given front
   * @param front The front 
   * @param trueParetoFront The true Pareto front
   * @param numberOfObjectives The number of objectives
   * @param lambda A vector containing the lambda vectors for R2
   */
  public double 
  R2(double [][] approximation,double [][] paretoFront) 
  {
        
    /**
     * Stores the maximum values of true pareto front.
     */
    double [] maximumValue ;
    
    /**
     * Stores the minimum values of the true pareto front.
     */
    double [] minimumValue ;
    
    /**
     * Stores the normalized front.
     */
    double [][] normalizedApproximation ;
    
    /**
     * Stores the normalized true Pareto front.
     */ 
    double [][] normalizedParetoFront ; 
    
    // STEP 1. Obtain the maximum and minimum values of the Pareto front
    maximumValue = utils_.getMaximumValues(paretoFront, nObj_);
    minimumValue = utils_.getMinimumValues(paretoFront, nObj_);
    
    // STEP 2. Get the normalized front and true Pareto fronts
    normalizedApproximation       = utils_.getNormalizedFront(approximation, 
                                                              maximumValue, 
                                                              minimumValue);
    normalizedParetoFront = utils_.getNormalizedFront(paretoFront, 
    		                                          maximumValue,
    		                                          minimumValue);
    
    // STEP 3. compute all the matrix of tchebicheff values if it is null
    //if (matrix_ == null) {
        matrix_ = new double[approximation.length][lambda_.length];
        for (int i = 0; i < approximation.length; i++) {
            for (int j = 0; j < lambda_.length; j++) {
                matrix_[i][j] = lambda_[j][0] * Math.abs(normalizedApproximation[i][0]); 
                for (int n = 1; n < nObj_; n++) {
                    matrix_[i][j] = Math.max(matrix_[i][j], lambda_[j][n] * Math.abs(normalizedApproximation[i][n]));
                }
            }
        }
    //}
    
    // STEP 4. The matrix is not null. Compute the R2 value
    double sum = 0.0;
    for (int i = 0; i < lambda_.length; i++) {
        double tmp = matrix_[0][i];
        for (int j = 1; j < approximation.length; j++) {
            tmp = Math.min(tmp, matrix_[j][i]);
        }
        sum += tmp;
    }
    
    // STEP 5. Return the R2 value
    return sum / (double) lambda_.length;
            
  } // R2

  /**
   * Returns the R2 indicator of a given population, using as a reference
   * point 0, 0. Normalization is using taking into account the population itself
   * @param set
   * @return 
   */
  public double
  R2(SolutionSet set) {
      double [][] approximationFront = set.writeObjectivesToMatrix();
      double [][] trueFront          = set.writeObjectivesToMatrix();
      
      return this.R2(approximationFront, trueFront);
      
  } // R2
  
  
  
     /**
   * Returns the R2 indicator value of a given front
   * @param front The front 
   * @param trueParetoFront The true Pareto front
   * @param numberOfObjectives The number of objectives
   * @param lambda A vector containing the lambda vectors for R2
   */
  public double 
  R2Without(SolutionSet set, int index) 
  {
      double [][] approximationFront = set.writeObjectivesToMatrix();
      double [][] trueFront          = set.writeObjectivesToMatrix();      
      return this.R2(approximationFront, trueFront);                    
  } // R2ContributionWithout

  
  
  
  /**
   * This class can be call from the command line. At least three parameters 
   * are required:
   * 1) the name of the file containing the front,  
   * 2) the number of objectives
   * 2) a file containing the reference point / the Optimal Pareto front for normalizing
   * 3) the file containing the weight vector
   */
  public static void main(String args[]) {
    if (args.length < 3) {
      System.err.println("Error using R2. Usage: \n java jmetal.qualityIndicator.Hypervolume " +
                         "<SolutionFrontFile> " +
                         "<TrueFrontFile> " + "<getNumberOfObjectives>");
      System.exit(1);
    }
    
    //Create a new instance of the metric
    R2 qualityIndicator;
    //Read the front from the files
    
    
    int nObj                            = new Integer(args[2]);
    
    

    
    if (nObj==2 && args.length==3) {
      qualityIndicator = new R2();
    
    } else {
      qualityIndicator = new R2(nObj,args[3]);        
    }
    
    
    double [][] approximationFront      = qualityIndicator.utils_.readFront(args[0]);
    
    double [][] paretoFront             = qualityIndicator.utils_.readFront(args[1]);
    
    
    //Obtain delta value
    double value = qualityIndicator.R2(approximationFront,paretoFront); 
                                  
    
    System.out.println(value);  
    
    System.out.println(qualityIndicator.R2Withouth(approximationFront,paretoFront,1)); 
    System.out.println(qualityIndicator.R2Withouth(approximationFront,paretoFront,15)); 
    System.out.println(qualityIndicator.R2Withouth(approximationFront,paretoFront,25)); 
    System.out.println(qualityIndicator.R2Withouth(approximationFront,paretoFront,75)); 
    
  } // main
} // R2
