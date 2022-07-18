package org.uma.jmetal.problem.multiobjective.cre;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem CRE21. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
public class CRE21 extends AbstractDoubleProblem {

  /** Constructor */
  public CRE21() {
    setNumberOfObjectives(2);
    setNumberOfConstraints(3);
    setName("CRE21");

    @NotNull List<Double> lowerLimit = List.of(0.01, 0.01, 0.01);
    @NotNull List<Double> upperLimit = List.of(0.45, 0.10, 0.10);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);

    solution.objectives()[0] = x1 * Math.sqrt(16.0 + (x3 * x3)) + x2 * Math.sqrt(1.0 + x3 * x3);
    solution.objectives()[1] = (20.0 * Math.sqrt(16.0 + (x3 * x3))) / (x1 * x3);

    evaluateConstraints(solution);

    return solution;
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
    var constraint = new double[this.getNumberOfConstraints()];

    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);

    constraint[0] = 0.1 - solution.objectives()[0];
    constraint[1] = 100000.0 - -solution.objectives()[1];
    constraint[2] = 100000 - ((80.0 * Math.sqrt(1.0 + x3 * x3)) / (x3 * x2));

    for (var i = 0; i < getNumberOfConstraints(); i++) {
      if (constraint[i] < 0.0) {
        constraint[i] = -constraint[i];
      } else {
        constraint[i] = 0;
      }
    }

    for (var i = 0; i < getNumberOfConstraints(); i++) {
      solution.constraints()[i] = constraint[i];
    }
  }
}
