//  GeneralizedSpread.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
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
import java.util.Arrays;

/**
 * This class implements the generalized spread metric for two or more dimensions.
 * It can be used also as command line program just by typing. 
 * $ java jmetal.qualityIndicator.GeneralizedSpread <solutionFrontFile> <trueFrontFile> <numberOfObjectives>
 * Reference: A. Zhou, Y. Jin, Q. Zhang, B. Sendhoff, and E. Tsang
 *           Combining model-based and genetics-based offspring generation for 
 *           multi-objective optimization using a convergence criterion, 
 *           2006 IEEE Congress on Evolutionary Computation, 2006, pp. 3234-3241.
 */
public class GeneralizedSpread {
  
  static jmetal.qualityIndicator.util.MetricsUtil utils_;  // MetricsUtil provides some 
                                                  // utilities for implementing
                                                  // the metric
  
  /**
   * Constructor
   * Creates a new instance of GeneralizedSpread
   */
  public GeneralizedSpread() {
    utils_ = new jmetal.qualityIndicator.util.MetricsUtil();
  } // GeneralizedSpread
  
  
  
  /**
   *  Calculates the generalized spread metric. Given the 
   *  pareto front, the true pareto front as <code>double []</code>
   *  and the number of objectives, the method return the value for the
   *  metric.
   *  @param paretoFront The pareto front.
   *  @param paretoTrueFront The true pareto front.
   *  @param numberOfObjectives The number of objectives.
   *  @return the value of the generalized spread metric
   **/
  public double generalizedSpread(double [][] paretoFront,
                                  double [][] paretoTrueFront,                                         
                                  int numberOfObjectives) {
    
    /**
     * Stores the maximum values of true pareto front.
     */
    double [] maximumValue;
    
    /**
     * Stores the minimum values of the true pareto front.
     */
    double [] minimumValue;
    
    /**
     * Stores the normalized front.
     */
    double [][] normalizedFront;
    
    /**
     * Stores the normalized true Pareto front.
     */ 
    double [][] normalizedParetoFront;

    // STEP 1. Obtain the maximum and minimum values of the Pareto front
    maximumValue = utils_.getMaximumValues(paretoTrueFront,numberOfObjectives);
    minimumValue = utils_.getMinimumValues(paretoTrueFront,numberOfObjectives);
    
    normalizedFront = utils_.getNormalizedFront(paretoFront,
                                                maximumValue,
                                                minimumValue);
    
    // STEP 2. Get the normalized front and true Pareto fronts
    normalizedParetoFront = utils_.getNormalizedFront(paretoTrueFront,
                                                      maximumValue,
                                                      minimumValue);
    
    // STEP 3. Find extremal values
    double [][] extremValues = new double[numberOfObjectives][numberOfObjectives];
    for (int i = 0; i < numberOfObjectives; i++) {
      Arrays.sort(normalizedParetoFront,new jmetal.qualityIndicator.util.ValueComparator(i));
      for (int j = 0; j < numberOfObjectives; j++) {
        extremValues[i][j] = normalizedParetoFront[normalizedParetoFront.length-1][j];
      }
    }
    
    int numberOfPoints     = normalizedFront.length;
    int numberOfTruePoints = normalizedParetoFront.length;
    
    
    // STEP 4. Sorts the normalized front
    Arrays.sort(normalizedFront,new jmetal.qualityIndicator.util.LexicoGraphicalComparator());
    
    // STEP 5. Calculate the metric value. The value is 1.0 by default
    if (utils_.distance(normalizedFront[0],normalizedFront[normalizedFront.length-1])==0.0) {
      return 1.0;
    } else {
      
      double dmean = 0.0;
      
      // STEP 6. Calculate the mean distance between each point and its nearest neighbor
      for (int i = 0; i < normalizedFront.length; i++) {
        dmean += utils_.distanceToNearestPoint(normalizedFront[i],normalizedFront);
      }
      
      dmean = dmean / (numberOfPoints);
      
      // STEP 7. Calculate the distance to extremal values
      double dExtrems = 0.0;
      for (int i = 0; i < extremValues.length; i++) {
        dExtrems += utils_.distanceToClosedPoint(extremValues[i],normalizedFront);
      }
      
      // STEP 8. Computing the value of the metric
      double mean = 0.0;
      for (int i = 0; i < normalizedFront.length; i++) {
        mean += Math.abs(utils_.distanceToNearestPoint(normalizedFront[i],normalizedFront) - 
                         dmean);
      }
      
      double value = (dExtrems + mean) / (dExtrems + (numberOfPoints*dmean));
      return value;
      
    }
  } // generalizedSpread
     
  /**
   * This class can be invoked from the command line. Three params are required:
   * 1) the name of the file containing the front,  
   * 2) the name of the file containig the true Pareto front
   * 3) the number of objectives
   */
  public static void main(String args[]) {
    if (args.length < 3) {
      System.err.println("Error using GeneralizedSpread. " +
                         "Usage: \n java GeneralizedSpread" +
                         " <SolutionFrontFile> " +
                         " <TrueFrontFile> + <numberOfObjectives>");
      System.exit(1);
    }
    
    //Create a new instance of the metric
    GeneralizedSpread qualityIndicator = new GeneralizedSpread();
    //Read the front from the files
    double [][] solutionFront = qualityIndicator.utils_.readFront(args[0]);
    double [][] trueFront     = qualityIndicator.utils_.readFront(args[1]);
    
    //Obtain delta value
    double value = qualityIndicator.generalizedSpread(solutionFront,
                                             trueFront,
                                            (new Integer(args[2])).intValue());
    
    System.out.println(value);  
  }  // main
} // GeneralizedSpread
