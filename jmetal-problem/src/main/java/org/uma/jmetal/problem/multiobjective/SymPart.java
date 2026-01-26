package org.uma.jmetal.problem.multiobjective;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem SYM_PART, a bi-objective problem with 2 variables. Variables are
 * bounded in [-0.5, 0.5]
 */
public class SymPart extends AbstractDoubleProblem {
  // Constants
  private static final double A = 1.0;
  private static final double B = 10.0;
  private static final double C = 8.0;

  public SymPart() {
    numberOfObjectives(2);
    numberOfConstraints(0);
    name("SYM-PART");

    List<Double> lowerLimit = Arrays.asList(-0.5, -0.5);
    List<Double> upperLimit = Arrays.asList(0.5, 0.5);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);

    double t1 = x2 - B * x1 * x1 + C * x1 - A;
    double t2 = x2 - 1.0;

    double f1 = Math.pow(x1 - A * (t1 < 0 ? 1 : 0), 2) + Math.pow(x2, 2);
    double f2 = Math.pow(x1 - A * (t2 < 0 ? 1 : 0), 2) + Math.pow(x2 - B, 2);

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;

    return solution;
  }
}
