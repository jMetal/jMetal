//  OKA1.java
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
 * Class representing problem OKA1
 */
public class OKA1 extends Problem {  
   
  
  /** 
   * Constructor.
   * Creates a new instance of the OKA2 problem.
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public OKA1(String solutionType) throws ClassNotFoundException {
    numberOfVariables_   = 2  ;
    numberOfObjectives_  = 2  ;
    numberOfConstraints_ = 0  ;
    problemName_         = "OKA1" ;
    
    upperLimit_ = new double[numberOfVariables_] ;
    lowerLimit_ = new double[numberOfVariables_] ;
       
    lowerLimit_[0] = 6 * Math.sin(Math.PI/12.0) ;
    upperLimit_[0] = 6 * Math.sin(Math.PI/12.0) + 2 * Math.PI * Math.cos(Math.PI/12.0) ;    
    lowerLimit_[1] = -2 * Math.PI * Math.sin(Math.PI/12.0) ;
    upperLimit_[1] = 6 * Math.cos(Math.PI/12.0) ;    
        
    if (solutionType.compareTo("BinaryReal") == 0)
    	solutionType_ = new BinaryRealSolutionType(this) ;
    else if (solutionType.compareTo("Real") == 0)
    	solutionType_ = new RealSolutionType(this) ;
    else {
    	System.out.println("Error: solution type " + solutionType + " invalid") ;
    	System.exit(-1) ;
    }  
  } // OKA1
    
  /** 
  * Evaluates a solution 
  * @param solution The solution to evaluate
   * @throws JMException 
  */
  public void evaluate(Solution solution) throws JMException {
    Variable[] decisionVariables  = solution.getDecisionVariables();
    
    double [] fx = new double[numberOfObjectives_] ; // 2 functions
    double [] x  = new double[numberOfVariables_]  ; // 2 variables
   
    for (int i = 0; i < numberOfVariables_; i++)
      x[i] = decisionVariables[i].getValue() ;
    
    double x0 = Math.cos(Math.PI/12.0)*x[0] - Math.sin(Math.PI/12.0)*x[1] ;
    double x1 = Math.sin(Math.PI/12.0)*x[0] + Math.cos(Math.PI/12.0)*x[1] ;
    
    fx[0] = x0 ;
    fx[1] = Math.sqrt(2 * Math.PI) - Math.sqrt(Math.abs(x0)) +
            2 * Math.pow(Math.abs(x1 - 3 * Math.cos(x0) - 3), 1.0/3.0) ;
        
    solution.setObjective(0, fx[0]);
    solution.setObjective(1, fx[1]);
  } // evaluate
} // OKA1
