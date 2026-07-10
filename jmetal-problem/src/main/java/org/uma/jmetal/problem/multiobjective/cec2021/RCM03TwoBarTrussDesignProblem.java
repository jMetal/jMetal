package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class RCM03TwoBarTrussDesignProblem extends AbstractDoubleProblem {
  public RCM03TwoBarTrussDesignProblem() {
    numberOfObjectives(2);
    numberOfConstraints(3);
    name("TwoBarTrussDesignProblem");

    List<Double> lowerLimit = Arrays.asList(1e-5, 1e-5, 1.0);
    List<Double> upperLimit = Arrays.asList(100.0, 100.0, 3.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);

    solution.objectives()[0] = x1 * Math.sqrt(16 + x3 * x3) + x2 * Math.sqrt(1 + x3 * x3);
    solution.objectives()[1] = (20 * Math.sqrt(16 + x3 * x3)) / (x3 * x1);

    double g1 = solution.objectives()[0] - 0.1;
    double g2 = solution.objectives()[1] - 1e5;
    double g3 = (80 * Math.sqrt(1 + x3 * x3)) / (x3 * x2) - 1e5;

    solution.constraints()[0] = -g1;
    solution.constraints()[1] = -g2;
    solution.constraints()[2] = -g3;

    return solution ;
  }

}
