//  Water.java
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

package jmetal.problems;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/**
 * Class representing problem Water
 */
public class Water extends Problem {
  
  // defining the lower and upper limits
  public static final double [] LOWERLIMIT = {0.01, 0.01, 0.01};
  public static final double [] UPPERLIMIT = {0.45, 0.10, 0.10};                          

 /**
  * Constructor.
  * Creates a default instance of the Water problem.
  * @param solutionType The solution type must "Real" or "BinaryReal".
  */
  public Water(String solutionType) {
    numberOfVariables_   = 3 ;
    numberOfObjectives_  = 5 ;
    numberOfConstraints_ = 7 ;
    problemName_         = "Water";
	        
    upperLimit_ = new double[numberOfVariables_];
    lowerLimit_ = new double[numberOfVariables_];
    upperLimit_ = new double[numberOfVariables_];
    lowerLimit_ = new double[numberOfVariables_];
    for (int var = 0; var < numberOfVariables_; var++){
      lowerLimit_[var] = LOWERLIMIT[var];
      upperLimit_[var] = UPPERLIMIT[var];
    } // for
	        
    if (solutionType.compareTo("BinaryReal") == 0)
      solutionType_ = new BinaryRealSolutionType(this) ;
    else if (solutionType.compareTo("Real") == 0)
    	solutionType_ = new RealSolutionType(this) ;
    else {
    	System.out.println("Error: solution type " + solutionType + " invalid") ;
    	System.exit(-1) ;
    }  
 } // Water
	
  /**
   * Evaluates a solution
   * @param solution The solution to evaluate
   * @throws JMException 
   */
  public void evaluate(Solution solution) throws JMException {         
    double [] x = new double[3] ; // 3 decision variables
    double [] f = new double[5] ; // 5 functions
    x[0] = solution.getDecisionVariables()[0].getValue();
    x[1] = solution.getDecisionVariables()[1].getValue();
    x[2] = solution.getDecisionVariables()[2].getValue();

    
    // First function
    f[0] = 106780.37 * (x[1] + x[2]) + 61704.67 ;
    // Second function
    f[1] = 3000 * x[0] ;
    // Third function
    f[2] = 305700 * 2289 * x[1] / Math.pow(0.06*2289, 0.65) ;
    // Fourth function
    f[3] = 250 * 2289 * Math.exp(-39.75*x[1]+9.9*x[2]+2.74) ;
    // Third function
    f[4] = 25 * (1.39 /(x[0]*x[1]) + 4940*x[2] -80) ;
             
    solution.setObjective(0,f[0]);    
    solution.setObjective(1,f[1]);
    solution.setObjective(2,f[2]);
    solution.setObjective(3,f[3]);
    solution.setObjective(4,f[4]);
  } // evaluate

  /** 
   * Evaluates the constraint overhead of a solution 
   * @param solution The solution
   * @throws JMException 
   */  
  public void evaluateConstraints(Solution solution) throws JMException {
    double [] constraint = new double[7]; // 7 constraints
    double [] x          = new double[3]; // 3 objectives
        
    x[0] = solution.getDecisionVariables()[0].getValue();
    x[1] = solution.getDecisionVariables()[1].getValue();
    x[2] = solution.getDecisionVariables()[2].getValue();
 
    constraint[0] = 1 - (0.00139/(x[0]*x[1])+4.94*x[2]-0.08)             ;
    constraint[1] = 1 - (0.000306/(x[0]*x[1])+1.082*x[2]-0.0986)         ;
    constraint[2] = 50000 - (12.307/(x[0]*x[1]) + 49408.24*x[2]+4051.02) ;
    constraint[3] = 16000 - (2.098/(x[0]*x[1])+8046.33*x[2]-696.71)      ;
    constraint[4] = 10000 - (2.138/(x[0]*x[1])+7883.39*x[2]-705.04)      ;
    constraint[5] = 2000 - (0.417*x[0]*x[1] + 1721.26*x[2]-136.54)       ;
    constraint[6] = 550 - (0.164/(x[0]*x[1])+631.13*x[2]-54.48) ;
    
    double total = 0.0;
    int number = 0;
    for (int i = 0; i < numberOfConstraints_; i++) {
      if (constraint[i]<0.0){
        total+=constraint[i];
        number++;
      } // int
    } // for
        
    solution.setOverallConstraintViolation(total);    
    solution.setNumberOfViolatedConstraint(number);        
  } // evaluateConstraints   
} // Water
