package org.uma.jmetal.problem.multiobjective.cre;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;

/**
 * Class representing problem CRE51. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class CRE51 extends AbstractDoubleProblem {

  /** Constructor */
  public CRE51() {
    setNumberOfVariables(3);
    setNumberOfObjectives(5);
    setNumberOfConstraints(7);
    setName("CRE51");

    List<Double> lowerLimit = List.of(0.01, 0.01, 0.01);
    List<Double> upperLimit = List.of(0.45, 0.10, 0.10);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = new double[getNumberOfVariables()];

    for (int i = 0; i < getNumberOfVariables(); i++) {
      x[i] = solution.variables().get(i);
    }

    solution.setObjective(0, 106780.37 * (x[1] + x[2]) + 61704.67);
    solution.setObjective(1, 3000 * x[0]);
    solution.setObjective(2, 305700 * 2289 * x[1] / Math.pow(0.06 * 2289, 0.65));
    solution.setObjective(3, 250 * 2289 * Math.exp(-39.75 * x[1] + 9.9 * x[2] + 2.74));
    solution.setObjective(4, 25 * (1.39 / (x[0] * x[1]) + 4940 * x[2] - 80));

    evaluateConstraints(solution, x);

    return solution;
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution, double[] x) {
    double[] constraint = new double[this.getNumberOfConstraints()];

    constraint[0] = 1 - (0.00139 / (x[0] * x[1]) + 4.94 * x[2] - 0.08);
    constraint[1] = 1 - (0.000306 / (x[0] * x[1]) + 1.082 * x[2] - 0.0986);
    constraint[2] = 50000 - (12.307 / (x[0] * x[1]) + 49408.24 * x[2] + 4051.02);
    constraint[3] = 16000 - (2.098 / (x[0] * x[1]) + 8046.33 * x[2] - 696.71);
    constraint[4] = 10000 - (2.138 / (x[0] * x[1]) + 7883.39 * x[2] - 705.04);
    constraint[5] = 2000 - (0.417 * x[0] * x[1] + 1721.26 * x[2] - 136.54);
    constraint[6] = 550 - (0.164 / (x[0] * x[1]) + 631.13 * x[2] - 54.48);

    for (int i = 0; i < getNumberOfConstraints(); i++) {
      if (constraint[i] < 0.0) {
        constraint[i] = -constraint[i];
      } else {
        constraint[i] = 0;
      }
    }

    for (int i = 0; i < getNumberOfConstraints(); i++) {
      solution.constraints()[i] = constraint[i];
    }
  }
}
