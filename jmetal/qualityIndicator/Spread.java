//  Spread.java
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
 * This class implements the spread quality indicator. 
 * It can be used also as command line program just by typing:
 *  "java jmetal.qualityIndicator.Spread <solutionFrontFile> <trueFrontFile>". 
 * This metric is only applicable to two bi-objective problems.
 * Reference: Deb, K., Pratap, A., Agarwal, S., Meyarivan, T.: A fast and 
 *            elitist multiobjective genetic algorithm: NSGA-II. IEEE Trans. 
 *            on Evol. Computation 6 (2002) 182-197
 */
public class Spread {

  static jmetal.qualityIndicator.util.MetricsUtil utils_;//utils_ is used to access to 
                                                //the MetricsUtil funcionalities
  
  /** 
   * Constructor.
   * Creates a new instance of a Spread object 
   */
  public Spread() {
    utils_ = new jmetal.qualityIndicator.util.MetricsUtil();
  } // Delta
  
  
  /** Calculates the Spread metric. Given the front, the true pareto front as 
   * <code>double []</code>, and the number of objectives, 
   * the method returns the value of the metric.
   *  @param front The front.
   *  @param trueParetoFront The true pareto front.
   *  @param numberOfObjectives The number of objectives.
   */
  public double spread(double [][] front, 
                       double [][] trueParetoFront,
                       int         numberOfObjectives) {
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
    double [][] normalizedFront ;

    /**
     * Stores the normalized true Pareto front.
     */ 
    double [][] normalizedParetoFront ; 

    // STEP 1. Obtain the maximum and minimum values of the Pareto front
    maximumValue = utils_.getMaximumValues(trueParetoFront, numberOfObjectives);
    minimumValue = utils_.getMinimumValues(trueParetoFront, numberOfObjectives);

    // STEP 2. Get the normalized front and true Pareto fronts
    normalizedFront = utils_.getNormalizedFront(front, 
    		                                        maximumValue, 
    		                                        minimumValue);
    normalizedParetoFront = utils_.getNormalizedFront(trueParetoFront, 
    		                                              maximumValue,
    		                                              minimumValue);

    // STEP 3. Sort normalizedFront and normalizedParetoFront;
    Arrays.sort(normalizedFront,
    		    new jmetal.qualityIndicator.util.LexicoGraphicalComparator());
    Arrays.sort(normalizedParetoFront,
    		    new jmetal.qualityIndicator.util.LexicoGraphicalComparator());

    int numberOfPoints     = normalizedFront.length;
//    int numberOfTruePoints = normalizedParetoFront.length;

    // STEP 4. Compute df and dl (See specifications in Deb's description of 
    // the metric)
    double df = utils_.distance(normalizedFront[0],normalizedParetoFront[0]);
    double dl = utils_.distance(normalizedFront[normalizedFront.length-1],
    		       normalizedParetoFront[normalizedParetoFront.length-1]);

    double mean = 0.0;
    double diversitySum = df + dl;

    // STEP 5. Calculate the mean of distances between points i and (i - 1). 
    // (the poins are in lexicografical order)
    for (int i = 0; i < (normalizedFront.length-1); i++) {
      mean += utils_.distance(normalizedFront[i],normalizedFront[i+1]);
    } // for

    mean = mean / (double)(numberOfPoints - 1);

    // STEP 6. If there are more than a single point, continue computing the 
    // metric. In other case, return the worse value (1.0, see metric's 
    // description).
    if (numberOfPoints > 1) {
      for (int i = 0; i < (numberOfPoints -1); i++) {
        diversitySum += Math.abs(utils_.distance(normalizedFront[i],
        		                 normalizedFront[i+1]) - mean);
      } // for
      return diversitySum / (df + dl + (numberOfPoints-1)*mean);
    } 
    else 
    	return 1.0;
  } // spread

  /**
   * This class can be invoqued from the command line. Three params are required:
   * 1) the name of the file containing the front,  
   * 2) the name of the file containig the true Pareto front
   * 3) the number of objectives
   */
  public static void main(String args[]) {
	if (args.length < 2) {
      System.err.println("Spread::Main: Error using Spread. Usage: \n java " +
			             "Spread <FrontFile> <TrueFrontFile>  " +
		                 "<numberOfObjectives>");
      System.exit(1);
	} // if

	// STEP 1. Create a new instance of the metric
	Spread qualityIndicator = new Spread();

	// STEP 2. Read the fronts from the files
	double [][] solutionFront = qualityIndicator.utils_.readFront(args[0]);
	double [][] trueFront     = qualityIndicator.utils_.readFront(args[1]);

	// STEP 3. Obtain the metric value
	double value = qualityIndicator.spread(solutionFront,trueFront,2);

	System.out.println(value);  
  } // Main
} // Spread
