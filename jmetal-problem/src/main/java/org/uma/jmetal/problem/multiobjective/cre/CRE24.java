package org.uma.jmetal.problem.multiobjective.cre;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;

/**
 * Class representing problem CRE24. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class CRE24 extends AbstractDoubleProblem {

  /** Constructor */
  public CRE24() {
    setNumberOfVariables(7);
    setNumberOfObjectives(2);
    setNumberOfConstraints(11);
    setName("CRE24");

    List<Double> lowerLimit = List.of(2.6, 0.7, 17.0, 7.3, 7.3, 2.9, 5.0);
    List<Double> upperLimit = List.of(3.6, 0.8, 28.0, 8.3, 8.3, 3.9, 5.5);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.getVariable(0);
    double x2 = solution.getVariable(1);
    double x3 = Math.rint(solution.getVariable(2));
    double x4 = solution.getVariable(3);
    double x5 = solution.getVariable(4);
    double x6 = solution.getVariable(5);
    double x7 = solution.getVariable(6);

    solution.setObjective(0, 0.7854 * x1 * (x2 * x2) * (((10.0 * x3 * x3) / 3.0) + (14.933 * x3) - 43.0934)
            - 1.508 * x1 * (x6 * x6 + x7 * x7)
            + 7.477 * (x6 * x6 * x6 + x7 * x7 * x7)
            + 0.7854 * (x4 * x6 * x6 + x5 * x7 * x7));

    double tmpVar = Math.pow((745.0 * x4) / (x2 * x3), 2.0)  + 1.69 * 1e7;
    solution.setObjective(1, Math.sqrt(tmpVar) / (0.1 * x6 * x6 * x6));

    evaluateConstraints(solution);

    return solution;
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
    double[] constraint = new double[this.getNumberOfConstraints()];

    double x1 = solution.getVariable(0);
    double x2 = solution.getVariable(1);
    double x3 = Math.rint(solution.getVariable(2));
    double x4 = solution.getVariable(3);
    double x5 = solution.getVariable(4);
    double x6 = solution.getVariable(5);
    double x7 = solution.getVariable(6);

    constraint[0] = -(1.0 / (x1 * x2 * x2 * x3)) + 1.0 / 27.0;
    constraint[1] = -(1.0 / (x1 * x2 * x2 * x3 * x3)) + 1.0 / 397.5 ;
    constraint[2] = -(x4 * x4 * x4) / (x2 * x3 * x6 * x6 * x6 * x6) + 1.0 / 1.93;
    constraint[3] = -(x5 * x5 * x5) / (x2 * x3 * x7 * x7 * x7 * x7) + 1.0 / 1.93;
    constraint[4] =  -(x2 * x3) + 40.0;
    constraint[5] = -(x1 / x2) + 12.0;
    constraint[6] = -5.0 + (x1 / x2);
    constraint[7] = -1.9 + x4 - 1.5 * x6;
    constraint[8] = -1.9 + x5 - 1.1 * x7;
    constraint[9] = -solution.getObjective(1) + 1300.0 ;

    double tmpVar = Math.pow((745.0 * x5) / (x2 * x3), 2.0) + 1.575 * 1e8;
    constraint[10] = -Math.sqrt(tmpVar) / (0.1 * x7 * x7 * x7) + 1100.0 ;

    for (int i = 0; i < getNumberOfConstraints(); i++) {
      if (constraint[i] < 0.0) {
        constraint[i] = -constraint[i];
      } else {
        constraint[i] = 0;
      }
    }

    for (int i = 0; i < getNumberOfConstraints(); i++) {
      solution.setConstraint(i, constraint[i]);
    }
  }
}
