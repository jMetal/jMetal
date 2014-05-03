//  FourBarTruss.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2012 Antonio J. Nebro
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
import jmetal.util.wrapper.XReal;

/**
 * Class representing problem FourBarTruss
 */
public class FourBarTruss extends Problem {
	double F_     = 10   ; // 10kN
	double E_     = 200000; // 20000 kN/cm2
	double L_     = 200  ; // 200 cm
	double sigma_ = 10   ; // 10kN/cm2 ;
	
  /**
   * Constructor
   * Creates a default instance of the FourBarTruss problem
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public FourBarTruss(String solutionType) {
    numberOfVariables_  = 4;
    numberOfObjectives_ = 2;
    numberOfConstraints_= 0;
    problemName_        = "FourBarTruss";
        
    lowerLimit_ = new double[numberOfVariables_];
    upperLimit_ = new double[numberOfVariables_];        
    lowerLimit_[0] = F_/sigma_;
    lowerLimit_[1] = Math.sqrt(2.0)*(F_/sigma_) ;    
    lowerLimit_[2] = lowerLimit_[1];        
    lowerLimit_[3] = lowerLimit_[0] ;
    upperLimit_[0] = 3*(F_/sigma_);
    upperLimit_[1] = upperLimit_[0] ;
    upperLimit_[2] = upperLimit_[0] ;
    upperLimit_[3] = upperLimit_[0] ;
        
    if (solutionType.compareTo("BinaryReal") == 0)
      solutionType_ = new BinaryRealSolutionType(this) ;
    else if (solutionType.compareTo("Real") == 0)
    	solutionType_ = new RealSolutionType(this) ;
    else {
    	System.out.println("Error: solution type " + solutionType + " invalid") ;
    	System.exit(-1) ;
    }  
  } // ConstrEx
     
  /** 
  * Evaluates a solution 
  * @param solution The solution to evaluate
   * @throws JMException 
  */
  public void evaluate(Solution solution) throws JMException {
		XReal vars = new XReal(solution) ;
		
    double [] fx = new double[2] ; // function values     
    double [] x = new double[numberOfVariables_] ;
    for (int i = 0 ; i < numberOfVariables_; i++)
    	x[i] = vars.getValue(i) ;
    
    fx[0] = L_*(2*x[0]+ Math.sqrt(2.0)*x[1]+ Math.sqrt(x[2])+x[3]) ;        
    fx[1] = (F_*L_/E_)*(2/x[0] + 2*Math.sqrt(2)/x[1] - 2*Math.sqrt(2)/x[2] + 2/x[3]);        
    
    solution.setObjective(0,fx[0]);
    solution.setObjective(1,fx[1]);
  } // evaluate
} // FourBarTruss