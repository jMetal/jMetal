package org.uma.jmetal.problem.multiobjective.cdtlz;

import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

/**
 * Problem C1-DTLZ1, defined in:
 * Jain, H. and K. Deb.  "An Evolutionary Many-Objective Optimization Algorithm Using Reference-Point-Based
 * Nondominated Sorting Approach, Part II: Handling Constraints and Extending to an Adaptive Approach."
 * EEE Transactions on Evolutionary Computation, 18(4):602-622, 2014.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class C1_DTLZ1 extends DTLZ1 implements ConstrainedProblem<DoubleSolution> {
  /**
   * Constructor
   * @param numberOfVariables
   * @param numberOfObjectives
   */
  public C1_DTLZ1(int numberOfVariables, int numberOfObjectives) {
    super(numberOfVariables, numberOfObjectives) ;

    setNumberOfConstraints(1);
  }

  @Override
  public void evaluate(DoubleSolution solution) {
    super.evaluate(solution);
  }

  @Override
  public void evaluateConstraints(DoubleSolution solution) {
    double[] constraint = new double[this.getNumberOfConstraints()];

    double sum = 0 ;
    for (int i = 0; i < getNumberOfObjectives() - 2; i++) {
      sum += solution.getObjective(i) / 0.5 ;
    }

    constraint[0] = 1.0 - solution.getObjective(getNumberOfObjectives()-1) - sum ;
  }
}
