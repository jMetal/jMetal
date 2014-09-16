//  Srinivas.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro, Juan J. Durillo
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

package org.uma.jmetal3.problem.multiobjective;

import org.uma.jmetal3.encoding.DoubleSolution;
import org.uma.jmetal3.encoding.attributes.AlgorithmAttributes;
import org.uma.jmetal3.encoding.impl.DoubleSolutionImpl;
import org.uma.jmetal3.problem.ConstrainedProblem;
import org.uma.jmetal3.problem.impl.ContinuousProblemImpl;

import java.util.ArrayList;

/** Class representing problem Srinivas */
public class Srinivas extends ContinuousProblemImpl<DoubleSolution> implements ConstrainedProblem<DoubleSolution> {

  /** Constructor */
  public Srinivas()  {
    setNumberOfVariables(2);
    setNumberOfObjectives(2);
    setNumberOfConstraints(2);
    setName("Srinivas");

    ArrayList<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    ArrayList<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-4.0);
      upperLimit.add(4.0);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  @Override
  public DoubleSolution createSolution(AlgorithmAttributes attr) {
    DoubleSolution solution = new DoubleSolutionImpl(this, attr) ;

    return solution ;
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution)  {
    double[] f = new double[solution.getNumberOfVariables()];

    double x1 = (double) solution.getVariableValue(0);
    double x2 = (double) solution.getVariableValue(1);
    f[0] = 2.0 + (x1 - 2.0) * (x1 - 2.0) + (x2 - 1.0) * (x2 - 1.0);
    f[1] = 9.0 * x1 - (x2 - 1.0) * (x2 - 1.0);

    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }

  /** EvaluateConstraints() method  */
  @Override
  public void evaluateConstraints(DoubleSolution solution)  {
    double[] constraint = new double[this.getNumberOfConstraints()];

    double x1 = solution.getVariableValue(0) ;
    double x2 = solution.getVariableValue(1) ;

    constraint[0] = 1.0 - (x1 * x1 + x2 * x2) / 225.0;
    constraint[1] = (3.0 * x2 - x1) / 10.0 - 1.0;

    double total = 0.0;
    for (int i = 0; i < this.getNumberOfConstraints(); i++) {
      if (constraint[i] < 0.0) {
        total += constraint[i];
      }
    }

    solution.setOverallConstraintViolationDegree(total);
  }
}
