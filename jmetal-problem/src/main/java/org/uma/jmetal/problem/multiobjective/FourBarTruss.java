package org.uma.jmetal.problem.multiobjective;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem FourBarTruss
 */
@SuppressWarnings("serial")
public class FourBarTruss extends AbstractDoubleProblem {
  private static final double SQRT_2 = Math.sqrt(2.0);

  private final double appliedLoad = 10;
  private final double elasticityModulus = 200000;
  private final double length = 200;
  private final double maximumStress = 10;

  public FourBarTruss() {
    numberOfObjectives(2);
    name("FourBarTruss");

    double lowerBase = appliedLoad / maximumStress;
    double upperBase = 3.0 * lowerBase;

    List<Double> lowerLimit = Arrays.asList(
        lowerBase,
        lowerBase * SQRT_2,
        lowerBase * SQRT_2,
        lowerBase);

    List<Double> upperLimit = Arrays.asList(
        upperBase, upperBase, upperBase, upperBase);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    List<Double> x = solution.variables();

    double volume = length * (2 * x.get(0) + SQRT_2 * x.get(1) + SQRT_2 * x.get(2) + x.get(3));
    double displacement = (appliedLoad * length / elasticityModulus) *
        (2.0 / x.get(0) + 2.0 * SQRT_2 / x.get(1) - 2.0 * SQRT_2 / x.get(2) + 2.0 / x.get(3));

    solution.objectives()[0] = volume;
    solution.objectives()[1] = displacement;

    return solution;
  }
}
