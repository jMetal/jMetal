//  Rastrigin.java
//
//  Author:
//       Esteban López-Camacho <esteban@lcc.uma.es>
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

package jmetal.problems.singleObjective;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

public class Rastrigin extends Problem {

  /**
   * Constructor
   * Creates a default instance of the Rastrigin problem
   * @param numberOfVariables Number of variables of the problem
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public Rastrigin(String solutionType, Integer numberOfVariables)  throws ClassNotFoundException {
    numberOfVariables_   = numberOfVariables ;
    numberOfObjectives_  = 1;
    numberOfConstraints_ = 0;
    problemName_         = "Rastrigin";

    upperLimit_ = new double[numberOfVariables_];
    lowerLimit_ = new double[numberOfVariables_];
    for (int var = 0; var < numberOfVariables_; var++){
      lowerLimit_[var] = -5.12;
      upperLimit_[var] = 5.12;
    } // for

    if (solutionType.compareTo("BinaryReal") == 0)
      solutionType_ = new BinaryRealSolutionType(this) ;
    else if (solutionType.compareTo("Real") == 0)
      solutionType_ = new RealSolutionType(this) ;
    else {
      System.out.println("Error: solution type " + solutionType + " invalid") ;
      System.exit(-1) ;
    }

  } // Rastrigin

  /**
   * Evaluates a solution
   * @param solution The solution to evaluate
   * @throws jmetal.util.JMException
   */
  public void evaluate(Solution solution) throws JMException {
    Variable[] decisionVariables  = solution.getDecisionVariables();

    double result = 0.0;
    double a = 10.0;
    double w = 2*Math.PI;

    double [] x = new double[numberOfVariables_];

    for (int i = 0; i < numberOfVariables_; i++) {
      x[i] = decisionVariables[i].getValue() ;
    }

    for (int i = 0; i < numberOfVariables_; i++) {
      result += x[i]*x[i] - a*Math.cos(w*x[i]);
    }
    result += a*numberOfVariables_;

    solution.setObjective(0, result);
  } // evaluate

} // Rastrigin

