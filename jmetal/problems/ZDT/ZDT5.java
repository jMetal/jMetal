//  ZDT5.java
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
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.variable.Binary;

/**
 * Class representing problem ZDT5
 */
public class ZDT5 extends Problem {
     
 /**
  * Creates a default instance of problem ZDT5 (11 decision variables).
  * This problem allows only "Binary" representations.
  */
  public ZDT5(String solutionType) throws ClassNotFoundException {
    this(solutionType, 11); // 11 variables by default
  } // ZDT5
  
 /** 
  * Creates a instance of problem ZDT5
  * @param numberOfVariables Number of variables.
  * This problem allows only "Binary" representations.
  */
  public ZDT5(String solutionType, Integer numberOfVariables) {
    numberOfVariables_  = numberOfVariables;
    numberOfObjectives_ = 2;
    numberOfConstraints_= 0;
    problemName_        = "ZDT5";    
    
    length_ = new int[numberOfVariables_];
    length_[0] = 30;
    for (int var = 1; var < numberOfVariables_; var++) {
      length_[var] = 5;
    }
        
    solutionType_ = new BinarySolutionType(this) ; 
    
    // All the variables of this problem are Binary
    //variableType_ = new Class[numberOfVariables_];
    //for (int var = 0; var < numberOfVariables_; var++){
    //  variableType_[var] = Class.forName("jmetal.base.encodings.variable.Binary") ;
    //} // for
  } //ZDT5

  /** 
  * Evaluates a solution 
  * @param solution The solution to evaluate
  */    
  public void evaluate(Solution solution) {        
    double [] f = new double[numberOfObjectives_] ; 
    f[0]        = 1 + u((Binary)solution.getDecisionVariables()[0]);
    double g    = evalG(solution.getDecisionVariables())                 ;
    double h    = evalH(f[0],g)              ;
    f[1]        = h * g                           ;   
    
    solution.setObjective(0,f[0]);
    solution.setObjective(1,f[1]);
  } //evaluate
    
  /**
  * Returns the value of the ZDT5 function G.
  * @param decisionVariables The decision variables of the solution to 
  * evaluate.
  */
  public double evalG(Variable[] decisionVariables) {
    double res = 0.0;
    for (int var = 1; var < numberOfVariables_; var++) {
      res += evalV(u((Binary)decisionVariables[var]));
    }
    
    return res;
  } // evalG
  
  /**
   * Returns the value of the ZDT5 function V.
   * @param value The parameter of V function.
   */
  public double evalV(double value) {
    if (value < 5.0) {
      return 2.0 + value;
    } else {
      return 1.0;
    }    
  } // evalV
  
  /**
  * Returns the value of the ZDT5 function H.
  * @param f First argument of the function H.
  * @param g Second argument of the function H.
  */
  public double evalH(double f, double g) {
    return 1 / f;
  } // evalH
  
  /**
   * Returns the u value defined in ZDT5 for a encodings.variable.
   * @param variable The binary encodings.variable
   */
  private double u(Binary variable) {
    return variable.bits_.cardinality();
  } // u
} // ZDT5
