package org.uma.jmetal.problem.multiobjective.re;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem RE21. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class RE21 extends AbstractDoubleProblem {

  /** Constructor */
  public RE21() {
    int numberOfVariables = 4;
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);
    setName("RE21");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Double> upperLimit;

    double f = 10;
    double sigma = 10;
    double tmpVar = (f / sigma);

      ArrayList<Double> doubles = new ArrayList<>(numberOfVariables);
      for (int i = 0; i < numberOfVariables; i++) {
          Double aDouble = 3 * tmpVar;
          doubles.add(aDouble);
      }
      upperLimit = doubles;

    lowerLimit.add(0, tmpVar);
    lowerLimit.add(1, Math.sqrt(2.0) * tmpVar);
    lowerLimit.add(2, Math.sqrt(2.0) * tmpVar);
    lowerLimit.add(3, tmpVar);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(@NotNull DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);
    double x4 = solution.variables().get(3);

    double f = 10;
    double e = 200000;
    double l = 200;

    solution.objectives()[0] = l * ((2 * x1) + Math.sqrt(2.0) * x2 + Math.sqrt(x3) + x4);
    solution.objectives()[1] =
        ((f * l) / e)
            * ((2.0 / x1)
                + (2.0 * Math.sqrt(2.0) / x2)
                - (2.0 * Math.sqrt(2.0) / x3)
                + (2.0 / x4));

    return solution;
  }
}
