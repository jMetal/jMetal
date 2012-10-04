//  Osyczka2.java
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

import jmetal.core.*;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;
import jmetal.util.Configuration.*;

/**
 * Class representing problem Oyczka2
 */
public class Osyczka2 extends Problem{
 /**
  * Constructor.
  * Creates a default instance of the Osyczka2 problem.
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public Osyczka2(String solutionType) throws ClassNotFoundException {
    numberOfVariables_  = 6;
    numberOfObjectives_ = 2;
    numberOfConstraints_= 6;
    problemName_        = "Osyczka2";
    
    lowerLimit_ = new double[numberOfVariables_];
    upperLimit_ = new double[numberOfVariables_];           
    //Fill lower and upper limits
    lowerLimit_[0] = 0.0;
    lowerLimit_[1] = 0.0;
    lowerLimit_[2] = 1.0;
    lowerLimit_[3] = 0.0;
    lowerLimit_[4] = 1.0;
    lowerLimit_[5] = 0.0;
        
    upperLimit_[0] = 10.0;
    upperLimit_[1] = 10.0;
    upperLimit_[2] = 5.0;
    upperLimit_[3] = 6.0;
    upperLimit_[4] = 5.0;
    upperLimit_[5] = 10.0;
    //
        
    if (solutionType.compareTo("BinaryReal") == 0)
    	solutionType_ = new BinaryRealSolutionType(this) ;
    else if (solutionType.compareTo("Real") == 0)
    	solutionType_ = new RealSolutionType(this) ;
    else {
    	System.out.println("Error: solution type " + solutionType + " invalid") ;
    	System.exit(-1) ;
    }    
  } // Osyczka2
    
  /** 
  * Evaluates a solution 
  * @param solution The solution to evaluate
   * @throws JMException 
  */  
  public void evaluate(Solution solution) throws JMException {
    Variable [] decisionVariables  = solution.getDecisionVariables();     
  
    double [] f = new double[numberOfObjectives_];
    
    double x1,x2,x3,x4,x5,x6;
    x1 = decisionVariables[0].getValue();
    x2 = decisionVariables[1].getValue();
    x3 = decisionVariables[2].getValue();
    x4 = decisionVariables[3].getValue();
    x5 = decisionVariables[4].getValue();
    x6 = decisionVariables[5].getValue();                
    f[0] = - (25.0*(x1-2.0)*(x1-2.0) + 
                  (x2-2.0)*(x2-2.0) + 
                  (x3-1.0)*(x3-1.0) + 
                  (x4-4.0)*(x4-4.0)+
                  (x5-1.0)*(x5-1.0));
                
    f[1] = x1*x1 + x2*x2 + x3*x3 + x4*x4 + x5*x5 + x6*x6;
    
    solution.setObjective(0,f[0]);
    solution.setObjective(1,f[1]);
  } // evaluate
   
  /** 
   * Evaluates the constraint overhead of a solution 
   * @param solution The solution
   * @throws JMException 
   */  
 public void evaluateConstraints(Solution solution) throws JMException {
    double [] constraint = new double[this.getNumberOfConstraints()];
    Variable[] decisionVariables = solution.getDecisionVariables();
        
    double x1,x2,x3,x4,x5,x6;
    x1 = decisionVariables[0].getValue();
    x2 = decisionVariables[1].getValue();
    x3 = decisionVariables[2].getValue();
    x4 = decisionVariables[3].getValue();
    x5 = decisionVariables[4].getValue();
    x6 = decisionVariables[5].getValue();
        
    constraint[0] = (x1 + x2)/2.0 - 1.0;
    constraint[1] = (6.0 - x1 - x2)/6.0;
    constraint[2] = (2.0 - x2 + x1)/2.0;
    constraint[3] = (2.0 - x1 + 3.0*x2)/2.0;
    constraint[4] = (4.0 - (x3-3.0)*(x3-3.0) - x4)/4.0;
    constraint[5] = ((x5-3.0)*(x5-3.0) +x6 - 4.0)/4.0;

    double total = 0.0;
    int number = 0;
    for (int i = 0; i < this.getNumberOfConstraints(); i++)
      if (constraint[i]<0.0){
        total+=constraint[i];
        number++;
      }        
       
    solution.setOverallConstraintViolation(total);    
    solution.setNumberOfViolatedConstraint(number);
  } // evaluateConstraints 
} // Osyczka2

