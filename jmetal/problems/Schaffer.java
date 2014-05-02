//  Schaffer.java
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
 * Class representing problem Schaffer
 */
public class Schaffer extends Problem {    

 /**
  * Constructor.
  * Creates a default instance of problem Schaffer
  * @param solutionType The solution type must "Real" or "BinaryReal".
  */
  public Schaffer(String solutionType) {
    numberOfVariables_   = 1;
    numberOfObjectives_  = 2;
    numberOfConstraints_ = 0;
    problemName_         = "Schaffer";
        
    lowerLimit_ = new double[numberOfVariables_];
    upperLimit_ = new double[numberOfVariables_];        
    lowerLimit_[0] = -100000;
    upperLimit_[0] =  100000;
    
    if (solutionType.compareTo("BinaryReal") == 0)
    	solutionType_ = new BinaryRealSolutionType(this) ;
    else if (solutionType.compareTo("Real") == 0)
    	solutionType_ = new RealSolutionType(this) ;
    else {
    	System.out.println("Error: solution type " + solutionType + " invalid") ;
    	System.exit(-1) ;
    }  
  } //Schaffer

    
  /** 
  * Evaluates a solution 
  * @param solution The solution to evaluate
   * @throws JMException 
  */
  public void evaluate(Solution solution) throws JMException {
    Variable[] variable  = solution.getDecisionVariables();
    
    double [] f = new double[numberOfObjectives_];
    f[0] = variable[0].getValue() * variable[0].getValue();
    
    f[1] = (variable[0].getValue() - 2.0) * 
           (variable[0].getValue() - 2.0);
        
    solution.setObjective(0,f[0]);
    solution.setObjective(1,f[1]);
  } //evaluate    
} //Schaffer
