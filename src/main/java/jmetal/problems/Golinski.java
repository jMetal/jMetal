//  Golinski.java
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
 * Class representing problem Golinski.
 */
public class Golinski extends Problem{

  // defining lowerLimits and upperLimits for the problem
  public static final double [] LOWERLIMIT = {2.6, 0.7, 17.0, 7.3, 7.3, 2.9, 5.0};
  public static final double [] UPPERLIMIT = {3.6, 0.8, 28.0, 8.3, 8.3, 3.9, 5.5};                          
    
 /** 
  * Constructor.
  * Creates a defalut instance of the Golinski problem.
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public Golinski(String solutionType) {
    numberOfVariables_   = 7 ;
    numberOfObjectives_  = 2 ;
    numberOfConstraints_ = 11;
    problemName_         = "Golinski";
        
    upperLimit_ = new double[numberOfVariables_];
    lowerLimit_ = new double[numberOfVariables_];
    for (int var = 0; var < numberOfVariables_; var++){
      lowerLimit_[var] =  LOWERLIMIT[var];
      upperLimit_[var] =  UPPERLIMIT[var];
    } // for
        
    if (solutionType.compareTo("BinaryReal") == 0)
    	solutionType_ = new BinaryRealSolutionType(this) ;
    else if (solutionType.compareTo("Real") == 0)
    	solutionType_ = new RealSolutionType(this) ;
    else {
    	System.out.println("Error: solution type " + solutionType + " invalid") ;
    	System.exit(-1) ;
    }  
  } //Golinski
  
  /**
   * Evaluates a solution.
   * @param solution The solution to evaluate.
   * @throws JMException 
   */
  public void evaluate(Solution solution) throws JMException {         
    double x1,x2,x3,x4,x5,x6,x7;
    x1 = solution.getDecisionVariables()[0].getValue();
    x2 = solution.getDecisionVariables()[1].getValue();
    x3 = solution.getDecisionVariables()[2].getValue();
    x4 = solution.getDecisionVariables()[3].getValue();
    x5 = solution.getDecisionVariables()[4].getValue();
    x6 = solution.getDecisionVariables()[5].getValue();
    x7 = solution.getDecisionVariables()[6].getValue();
        
    double f1 = 0.7854 * x1 *x2 *x2 * ((10*x3*x3)/3.0 + 14.933*x3 - 43.0934) - 
                1.508*x1*(x6*x6 + x7*x7)+7.477*(x6*x6*x6 + x7*x7*x7) + 
                0.7854*(x4*x6*x6 + x5*x7*x7);        
    
    double aux = 745.0 * x4 / (x2 * x3);
    double f2 = Math.sqrt((aux*aux)+1.69e7) / (0.1*x6*x6*x6);
    
    solution.setObjective(0,f1);    
    solution.setObjective(1,f2);
  } // evaluate
  
  /** 
   * Evaluates the constraint overhead of a solution 
   * @param solution The solution
   * @throws JMException 
   */  
 public void evaluateConstraints(Solution solution) throws JMException {
    double [] constraint = new double[numberOfConstraints_];
    double x1,x2,x3,x4,x5,x6,x7;
        
    x1 = solution.getDecisionVariables()[0].getValue();
    x2 = solution.getDecisionVariables()[1].getValue();
    x3 = solution.getDecisionVariables()[2].getValue();
    x4 = solution.getDecisionVariables()[3].getValue();
    x5 = solution.getDecisionVariables()[4].getValue();
    x6 = solution.getDecisionVariables()[5].getValue();
    x7 = solution.getDecisionVariables()[6].getValue();
        
        
    constraint[0] = -((1.0/(x1*x2*x2*x3)) -(1.0/27.0));
    constraint[1] = -((1.0/(x1*x2*x2*x3*x3))-(1.0/397.5));
    constraint[2] = -((x4*x4*x4)/(x2*x3*x3*x6*x6*x6*x6)-(1.0/1.93));
    constraint[3] = -((x5*x5*x5)/(x2*x3*x7*x7*x7*x7)- (1.0/1.93));
    constraint[4] = -(x2*x3 - 40.0);
    constraint[5] = -((x1/x2) - 12.0);
    constraint[6] = -(5.0 - (x1/x2));
    constraint[7] = -(1.9 - x4 + 1.5*x6);
    constraint[8] = -(1.9 - x5 + 1.1*x7);
    
    double aux = 745.0 * x4 / (x2 * x3);
    double f2 = java.lang.Math.sqrt((aux*aux)+1.69e7) / (0.1*x6*x6*x6);
    constraint[9] = -(f2 - 1300);
    double a = 745.0*x5/(x2*x3);
    double b = 1.575e8;
    constraint[10] = -(java.lang.Math.sqrt(a*a+b)/(0.1*x7*x7*x7)-1100.0);

    double total = 0.0;
    int number = 0;
    for (int i = 0; i < numberOfConstraints_; i++) {
      if (constraint[i]<0.0){
        total+=constraint[i];
        number++;
      }
    }
        
    solution.setOverallConstraintViolation(total);    
    solution.setNumberOfViolatedConstraint(number);        
  } // evaluateConstraints
} // Golinski

