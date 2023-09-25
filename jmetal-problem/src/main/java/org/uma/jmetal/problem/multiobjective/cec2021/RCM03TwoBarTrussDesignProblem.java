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

    List<Double> lowerLimit = Arrays.asList(0.05, 0.2, 0.2, 0.35, 3.0);
    List<Double> upperLimit = Arrays.asList(0.5, 0.5, 0.6, 0.5, 6.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);

    solution.objectives()[0] = x1 * (16 + x3 * x3) * Math.sqrt(1 + x3 * x3) + x2 * (1 + x3 * x3);
    solution.objectives()[1] = (20 * (16 + x3 * x3) * Math.sqrt(1 + x3 * x3)) / (x3 * x1);

    solution.constraints()[0] =  solution.objectives()[0] - 0.1;
    solution.constraints()[1] = solution.objectives()[1] - 1e5;
    solution.constraints()[2] =  (80 * (1 + x3 * x3) * Math.sqrt(1 + x3 * x3)) / (x3 * x2) - 1e5;

    return solution ;
  }

}
