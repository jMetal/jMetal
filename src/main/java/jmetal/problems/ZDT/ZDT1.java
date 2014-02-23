//  ZDT1.java
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

package jmetal.problems.ZDT;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;
import jmetal.util.wrapper.XReal;

/**
 * Class representing problem ZDT1
 */
public class ZDT1 extends Problem {
    
 /** 
  * Constructor.
  * Creates a default instance of problem ZDT1 (30 decision variables)
  * @param solutionType The solution type must "Real", "BinaryReal, and "ArrayReal". 
	* ArrayReal, or ArrayRealC".
  */
  public ZDT1(String solutionType) throws ClassNotFoundException {
    this(solutionType, 30); // 30 variables by default
  } // ZDT1
  
 /**
  * Creates a new instance of problem ZDT1.
  * @param numberOfVariables Number of variables.
  * @param solutionType The solution type must "Real", "BinaryReal, and "ArrayReal". 
  */
  public ZDT1(String solutionType, Integer numberOfVariables) {
    numberOfVariables_  = numberOfVariables;
    numberOfObjectives_ =  2;
    numberOfConstraints_=  0;
    problemName_        = "ZDT1";

    upperLimit_ = new double[numberOfVariables_];
    lowerLimit_ = new double[numberOfVariables_];

    // Establishes upper and lower limits for the variables
    for (int var = 0; var < numberOfVariables_; var++)
    {
      lowerLimit_[var] = 0.0;
      upperLimit_[var] = 1.0;
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
  } // ZDT1
    
  /** 
   * Evaluates a solution.
   * @param solution The solution to evaluate.
   * @throws JMException 
   */
  public void evaluate(Solution solution) throws JMException {
		XReal x = new XReal(solution) ;
 		
    double [] f = new double[numberOfObjectives_]  ;
    f[0]        = x.getValue(0)     ;
    double g    = this.evalG(x)                 ;
    double h    = this.evalH(f[0],g)              ;
    f[1]        = h * g                           ;
    
    solution.setObjective(0,f[0]);
    solution.setObjective(1,f[1]);
  } // evaluate
    
  /**
   * Returns the value of the ZDT1 function G.
   * @param  x Solution
   * @throws JMException
   */
  private double evalG(XReal x) throws JMException {
    double g = 0.0;        
    for (int i = 1; i < x.getNumberOfDecisionVariables();i++)
      g += x.getValue(i);
    double constant = (9.0 / (numberOfVariables_-1));
    g = constant * g;
    g = g + 1.0;
    return g;
  } // evalG
    
  /**
   * Returns the value of the ZDT1 function H.
   * @param f First argument of the function H.
   * @param g Second argument of the function H.
   */
  public double evalH(double f, double g) {
    double h = 0.0;
    h = 1.0 - java.lang.Math.sqrt(f/g);
    return h;        
  } // evalH
} // ZDT1
