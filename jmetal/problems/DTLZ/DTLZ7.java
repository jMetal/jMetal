//  DTLZ7.java
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

package jmetal.problems.DTLZ;

import jmetal.core.*;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;
import jmetal.util.Configuration.*;

/**
 * Class representing problem DTLZ7
 */
public class DTLZ7 extends Problem{
   
 /**
  * Creates a default DTLZ7 problem instance (22 variables and 3 objectives)
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public DTLZ7(String solutionType) throws ClassNotFoundException {
    this(solutionType, 22,3);
  } // DTLZ7
    
 /**
  * Creates a new DTLZ7 problem instance
  * @param numberOfVariables Number of variables
  * @param numberOfObjectives Number of objective functions
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public DTLZ7(String  solutionType,
  		         Integer numberOfVariables,
  		         Integer numberOfObjectives) throws ClassNotFoundException {
    numberOfVariables_  = numberOfVariables.intValue();
    numberOfObjectives_ = numberOfObjectives.intValue();
    numberOfConstraints_= 0;
    problemName_        = "DTLZ7";
        
    lowerLimit_ = new double[numberOfVariables_];
    upperLimit_ = new double[numberOfVariables_];
    for (int var = 0; var < numberOfVariables_; var++){
      lowerLimit_[var] = 0.0;
      upperLimit_[var] = 1.0;
    }
        
    if (solutionType.compareTo("BinaryReal") == 0)
    	solutionType_ = new BinaryRealSolutionType(this) ;
    else if (solutionType.compareTo("Real") == 0)
    	solutionType_ = new RealSolutionType(this) ;
    else {
    	System.out.println("Error: solution type " + solutionType + " invalid") ;
    	System.exit(-1) ;
    }            
  }

  /** 
  * Evaluates a solution 
  * @param solution The solution to evaluate
   * @throws JMException 
  */        
  public void evaluate(Solution solution) throws JMException {
    Variable[] gen  = solution.getDecisionVariables();

    double [] x = new double[numberOfVariables_];
    double [] f = new double[numberOfObjectives_];
    int k = numberOfVariables_ - numberOfObjectives_ + 1;
            
    for (int i = 0; i < numberOfVariables_; i++)
      x[i] = gen[i].getValue();
        
    //Calculate g
    double g = 0.0;
    for (int i = this.numberOfVariables_ - k; i < numberOfVariables_; i++)
      g += x[i] ;
        
    g = 1 + (9.0 * g) / k;
    //<-
                
    //Calculate the value of f1,f2,f3,...,fM-1 (take acount of vectors start at 0)
    for (int i = 0; i < numberOfObjectives_-1; i++)
      f[i] = x[i];
    //<-
        
    //->Calculate fM
    double h = 0.0;
    for (int i = 0; i < numberOfObjectives_ -1; i++)
      h += (f[i]/(1.0 + g))*(1 + Math.sin(3.0 * Math.PI * f[i]));
       
    h = numberOfObjectives_ - h;
        
    f[numberOfObjectives_-1] = (1 + g) * h;
    //<-
        
    //-> Setting up the value of the objetives
    for (int i = 0; i < numberOfObjectives_; i++)
      solution.setObjective(i,f[i]);        
    //<-
  } // evaluate
}

