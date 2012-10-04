//  LZ09_F7.java
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

package jmetal.problems.LZ09;

import java.util.Vector;

import jmetal.core.*;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;
import jmetal.util.Configuration.*;

/** 
 * Class representing problem LZ09_F7 
 */
public class LZ09_F7 extends Problem {   
	LZ09 LZ09_ ; 
 /** 
  * Creates a default LZ09_F7 problem (10 variables and 2 objectives)
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public LZ09_F7(String solutionType) throws ClassNotFoundException {
    this(solutionType, 21, 3, 21);
  } // LZ09_F7
  
  /** 
   * Creates a LZ09_F7 problem instance
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   * @param solutionType The solution type must "Real" or "BinaryReal". 
   */
   public LZ09_F7(String solutionType,
                  Integer ptype, 
                  Integer dtype,
                  Integer ltype) throws ClassNotFoundException {
     numberOfVariables_  = 10;
     numberOfObjectives_ = 2;
     numberOfConstraints_= 0;
     problemName_        = "LZ09_F7";
         
   	 LZ09_  = new LZ09(numberOfVariables_, 
   			               numberOfObjectives_, 
   			               ptype, 
   			               dtype, 
   			               ltype) ;

     lowerLimit_ = new double[numberOfVariables_];
     upperLimit_ = new double[numberOfVariables_];        
     for (int var = 0; var < numberOfVariables_; var++){
       lowerLimit_[var] = 0.0;
       upperLimit_[var] = 1.0;
     } //for
         
     if (solutionType.compareTo("BinaryReal") == 0)
    	 solutionType_ = new BinaryRealSolutionType(this) ;
     else if (solutionType.compareTo("Real") == 0)
    	 solutionType_ = new RealSolutionType(this) ;
     else {
     	System.out.println("Error: solution type " + solutionType + " invalid") ;
     	System.exit(-1) ;
     }                     
   } // LZ09_F7
   
   /** 
    * Evaluates a solution 
    * @param solution The solution to evaluate
     * @throws JMException 
    */    
    public void evaluate(Solution solution) throws JMException {
      Variable[] gen  = solution.getDecisionVariables();
      
      Vector<Double> x = new Vector<Double>(numberOfVariables_) ;
      Vector<Double> y = new Vector<Double>(numberOfObjectives_);
          
      for (int i = 0; i < numberOfVariables_; i++) {
      	x.addElement(gen[i].getValue());
      	y.addElement(0.0) ;
      } // for
        
      LZ09_.objective(x, y) ;
      
      for (int i = 0; i < numberOfObjectives_; i++)
        solution.setObjective(i, y.get(i)); 
    } // evaluate
} // LZ09_F7
