//  Viennet4.java
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
 * Class representing problem Viennet4
 */
public class Viennet4 extends Problem{           
  
 /** 
  * Constructor.
  * Creates a default instance of the Viennet4 problem.
  * @param solutionType The solution type must "Real" or "BinaryReal".
  */
  public Viennet4(String solutionType) {
    numberOfVariables_   = 2 ;
    numberOfObjectives_  = 3 ;
    numberOfConstraints_ = 3 ;
    problemName_         = "Viennet4";
        
    upperLimit_ = new double[numberOfVariables_];
    lowerLimit_ = new double[numberOfVariables_];
    for (int var = 0; var < numberOfVariables_; var++){
      lowerLimit_[var] =  -4.0;
      upperLimit_[var] =   4.0;
    } // for
        
    if (solutionType.compareTo("BinaryReal") == 0)
    	solutionType_ = new BinaryRealSolutionType(this) ;
    else if (solutionType.compareTo("Real") == 0)
    	solutionType_ = new RealSolutionType(this) ;
    else {
    	System.out.println("Error: solution type " + solutionType + " invalid") ;
    	System.exit(-1) ;
    }  
  } //Viennet4
  
  
  /** 
   * Evaluates a solution
   * @param solution The solution to evaluate
   * @throws JMException 
   */  
  public void evaluate(Solution solution) throws JMException {          
    double [] x = new double[numberOfVariables_];
    double [] f = new double[numberOfObjectives_];
        
    for (int i = 0; i < numberOfVariables_; i++) {
      x[i] = solution.getDecisionVariables()[i].getValue();
    }
        
    f[0] = (x[0]-2.0)*(x[0]-2.0)/2.0 + 
             (x[1]+1.0)*(x[1]+1.0)/13.0 + 3.0;
        
    f[1] = (x[0]+ x[1]-3.0)*(x[0]+x[1]-3.0)/175.0 +
             (2.0*x[1]-x[0])*(2.0*x[1]-x[0])/17.0 -13.0;
        
    f[2] = (3.0*x[0]-2.0*x[1]+4.0)*(3.0*x[0]-2.0*x[1]+4.0)/8.0 + 
             (x[0]-x[1]+1.0)*(x[0]-x[1]+1.0)/27.0 + 15.0;
        
        
    for (int i = 0; i < numberOfObjectives_; i++) {
      solution.setObjective(i,f[i]);        
    }
  } // evaluate


  /** 
   * Evaluates the constraint overhead of a solution 
   * @param solution The solution
   * @throws JMException 
   */  
  public void evaluateConstraints(Solution solution) throws JMException {
    double [] constraint = new double[numberOfConstraints_];
        
    double x1 = solution.getDecisionVariables()[0].getValue();
    double x2 = solution.getDecisionVariables()[1].getValue();
        
    constraint[0] = -x2 - (4.0 * x1) + 4.0  ;
    constraint[1] = x1 + 1.0 ;
    constraint[2] = x2 - x1 + 2.0 ;
        
    int number = 0;
    double total = 0.0;
    for (int i = 0; i < numberOfConstraints_; i++) {
      if (constraint[i]<0.0){
        number++;
        total+=constraint[i];
      }
    }
    solution.setOverallConstraintViolation(total);    
    solution.setNumberOfViolatedConstraint(number);
  } // evaluateConstraints
} // Viennet4

