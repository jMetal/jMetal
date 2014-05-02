//  OKA2.java
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
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/**
 * Class representing problem OKA2
 */
public class OKA2 extends Problem {  
   
  
  /** 
   * Constructor.
   * Creates a new instance of the OKA2 problem.
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public OKA2(String solutionType) {
    numberOfVariables_   = 3  ;
    numberOfObjectives_  = 2  ;
    numberOfConstraints_ = 0  ;
    problemName_         = "OKA2"                    ;
        
    upperLimit_ = new double[numberOfVariables_] ;
    lowerLimit_ = new double[numberOfVariables_] ;
       
    lowerLimit_[0] = -Math.PI ;
    upperLimit_[0] = Math.PI  ;    
    for (int i = 1; i < numberOfVariables_; i++) {
      lowerLimit_[i] = -5.0 ;
      upperLimit_[i] = 5.0  ;
    } // for
        
    if (solutionType.compareTo("BinaryReal") == 0)
    	solutionType_ = new BinaryRealSolutionType(this) ;
    else if (solutionType.compareTo("Real") == 0)
    	solutionType_ = new RealSolutionType(this) ;
    else {
    	System.out.println("Error: solution type " + solutionType + " invalid") ;
    	System.exit(-1) ;
    }  
  } // OKA2
    
  /** 
  * Evaluates a solution 
  * @param solution The solution to evaluate
   * @throws JMException 
  */
  public void evaluate(Solution solution) throws JMException {
    Variable[] decisionVariables  = solution.getDecisionVariables();
        
    double [] fx   = new double[numberOfObjectives_] ; // 2 functions
    double [] x    = new double[numberOfVariables_]  ; // 3 variables
   
    for (int i = 0; i < numberOfVariables_; i++)
      x[i] = decisionVariables[i].getValue() ;
    
    fx[0] = x[0] ; 
    
    fx[1] = 1 -Math.pow((x[0]+Math.PI),2)/(4*Math.pow(Math.PI,2)) + 
               Math.pow(Math.abs(x[1] - 5*Math.cos(x[0])),1.0/3.0)+ 
               Math.pow(Math.abs(x[2] - 5*Math.sin(x[0])),1.0/3.0);
        
    solution.setObjective(0, fx[0]);
    solution.setObjective(1, fx[1]);
  } // evaluate
} // OKA2
