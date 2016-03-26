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

package org.uma.jmetal.problem.multiobjective.cdtlz;

import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

/**
 * Problem C2-DTLZ2, defined in:
 * Jain, H. and K. Deb.  "An Evolutionary Many-Objective Optimization Algorithm Using Reference-Point-Based
 * Nondominated Sorting Approach, Part II: Handling Constraints and Extending to an Adaptive Approach."
 * EEE Transactions on Evolutionary Computation, 18(4):602-622, 2014.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class C2_DTLZ2 extends DTLZ2 implements ConstrainedProblem<DoubleSolution> {
  public OverallConstraintViolation<DoubleSolution> overallConstraintViolationDegree ;
  public NumberOfViolatedConstraints<DoubleSolution> numberOfViolatedConstraints ;

  private double rValue ;
  /**
   * Constructor
   * @param numberOfVariables
   * @param numberOfObjectives
   */
  public C2_DTLZ2(int numberOfVariables, int numberOfObjectives) {
    super(numberOfVariables, numberOfObjectives) ;

    setNumberOfConstraints(1);

    if (getNumberOfObjectives() == 3) {
      rValue = 0.4 ;
    } else {
      rValue = 0.5 ;
    }

    overallConstraintViolationDegree = new OverallConstraintViolation<DoubleSolution>() ;
    numberOfViolatedConstraints = new NumberOfViolatedConstraints<DoubleSolution>() ;
  }

  @Override
  public void evaluateConstraints(DoubleSolution solution) {
    double[] constraint = new double[getNumberOfConstraints()] ;

    double sum2 = 0 ;
    double maxSum1 = Double.MIN_VALUE ;
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      double sum1 = Math.pow(solution.getObjective(i)-1.0, 2.0) - Math.pow(rValue, 2.0) ;
      for (int j = 0; j < getNumberOfObjectives(); j++) {
        if (i != j) {
          sum1 += Math.pow(solution.getObjective(j), 2.0) ;
        }
      }

      maxSum1 = Math.max(maxSum1, sum1) ;

      sum2 += Math.pow((solution.getObjective(i) -
          1.0/Math.sqrt(getNumberOfObjectives())), 2.0)  ;

    }

    sum2 -= Math.pow(rValue, 2.0) ;

    constraint[0] = Math.max(maxSum1, sum2) ;

    double overallConstraintViolation = 0.0;
    int violatedConstraints = 0;
    for (int i = 0; i < getNumberOfConstraints(); i++) {
      if (constraint[i]<0.0){
        overallConstraintViolation+=constraint[i];
        violatedConstraints++;
      }
    }

    overallConstraintViolationDegree.setAttribute(solution, overallConstraintViolation);
    numberOfViolatedConstraints.setAttribute(solution, violatedConstraints);
  }
}
