//  ZDT4.java
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

import jmetal.core.*;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;
import jmetal.util.wrapper.XReal;

/**
 * Class representing problem ZDT4
 */
public class ZDT4 extends Problem{
     
 /**
  * Constructor.
  * Creates a default instance of problem ZDT4 (10 decision variables)
  * @param solutionType The solution type must "Real", "BinaryReal, and "ArrayReal". 
  */
  public ZDT4(String solutionType) throws ClassNotFoundException {
    this(solutionType, 10); // 10 variables by default
  } // ZDT4
  
 /** 
  * Creates a instance of problem ZDT4.
  * @param numberOfVariables Number of variables.
  * @param solutionType The solution type must "Real", "BinaryReal, and "ArrayReal". 
  */
  public ZDT4(String solutionType, Integer numberOfVariables) throws ClassNotFoundException {
    numberOfVariables_  = numberOfVariables.intValue();
    numberOfObjectives_ = 2;
    numberOfConstraints_= 0;
    problemName_        = "ZDT4";

    upperLimit_ = new double[numberOfVariables_];
    lowerLimit_ = new double[numberOfVariables_];
        
    lowerLimit_[0] = 0.0;
    upperLimit_[0] = 1.0;
    for (int var = 1; var < numberOfVariables_; var++){
      lowerLimit_[var] = -5.0;
      upperLimit_[var] =  5.0;
    } //for
        
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
  } //ZDT4

  /** 
  * Evaluates a solution 
  * @param solution The solution to evaluate
   * @throws JMException 
  */    
  public void evaluate(Solution solution) throws JMException {
		XReal x = new XReal(solution) ;
    
    double [] f = new double[numberOfObjectives_] ; 
    f[0]        = x.getValue(0)     ;
    double g    = this.evalG(x)                 ;
    double h    = this.evalH(f[0],g)              ;
    f[1]        = h * g                           ;   
    
    solution.setObjective(0,f[0]);
    solution.setObjective(1,f[1]);
  } //evaluate
    
  /**
  * Returns the value of the ZDT4 function G.
  * @param decisionVariables The decision variables of the solution to 
  * evaluate.
   * @throws JMException 
  */  
  public double evalG(XReal x) throws JMException{
    double g = 0.0;
    for (int var = 1; var < numberOfVariables_; var++)
      g += Math.pow(x.getValue(var),2.0) + 
          - 10.0 * Math.cos(4.0*Math.PI*x.getValue(var));
    
    double constante = 1.0 + 10.0*(numberOfVariables_ - 1);
    return g + constante;
  } // evalG
    
  /**
  * Returns the value of the ZDT4 function H.
  * @param f First argument of the function H.
  * @param g Second argument of the function H.
  */
  public double evalH(double f, double g){
    return 1.0 - Math.sqrt(f/g);
  } // evalH      
} // ZDT4
