//  Kursawe.java
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
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;
import jmetal.util.wrapper.XReal;

/**
 * Class representing problem Kursawe
 */
public class Kursawe extends Problem {  
    
  /** 
   * Constructor.
   * Creates a default instance of the Kursawe problem.
   * @param solutionType The solution type must "Real", "BinaryReal, and "ArrayReal". 
   */
  public Kursawe(String solutionType) throws ClassNotFoundException {
    this(solutionType, 3);
  } // Kursawe
  
  /** 
   * Constructor.
   * Creates a new instance of the Kursawe problem.
   * @param numberOfVariables Number of variables of the problem 
   * @param solutionType The solution type must "Real", "BinaryReal, and "ArrayReal". 
   */
  public Kursawe(String solutionType, Integer numberOfVariables) {
    numberOfVariables_   = numberOfVariables;
    numberOfObjectives_  = 2                            ;
    numberOfConstraints_ = 0                            ;
    problemName_         = "Kursawe"                    ;
        
    upperLimit_ = new double[numberOfVariables_] ;
    lowerLimit_ = new double[numberOfVariables_] ;
       
    for (int i = 0; i < numberOfVariables_; i++) {
      lowerLimit_[i] = -5.0 ;
      upperLimit_[i] = 5.0  ;
    } // for
        
    if (solutionType.compareTo("BinaryReal") == 0)
    	solutionType_ = new BinaryRealSolutionType(this) ;
    else if (solutionType.compareTo("Real") == 0)
    	solutionType_ = new RealSolutionType(this) ;
    else if (solutionType.compareTo("ArrayReal") == 0)
    	solutionType_ = new ArrayRealSolutionType(this) ;
    else {
    	System.out.println("Error: solution type " + solutionType + " invalid") ;
    	System.exit(-1) ;
    }
  } // Kursawe
    
  /** 
  * Evaluates a solution 
  * @param solution The solution to evaluate
   * @throws JMException 
  */
  public void evaluate(Solution solution) throws JMException {
		XReal vars = new XReal(solution) ;
        
    double aux, xi, xj           ; // auxiliary variables
    double [] fx = new double[2] ; // function values     
    double [] x = new double[numberOfVariables_] ;
    for (int i = 0 ; i < numberOfVariables_; i++)
    	x[i] = vars.getValue(i) ;
    
    fx[0] = 0.0 ; 
    for (int var = 0; var < numberOfVariables_ - 1; var++) {        
      xi = x[var] *  x[var];
      xj = x[var+1] * x[var+1] ;
      aux = (-0.2) * Math.sqrt(xi + xj);
      fx[0] += (-10.0) * Math.exp(aux);
    } // for
        
    fx[1] = 0.0;
        
    for (int var = 0; var < numberOfVariables_ ; var++) {
      fx[1] += Math.pow(Math.abs(x[var]), 0.8) + 
           5.0 * Math.sin(Math.pow(x[var], 3.0));
    } // for
    
    solution.setObjective(0, fx[0]);
    solution.setObjective(1, fx[1]);
  } // evaluate
} // Kursawe
