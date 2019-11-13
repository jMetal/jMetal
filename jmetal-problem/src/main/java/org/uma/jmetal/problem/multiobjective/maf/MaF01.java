package org.uma.jmetal.problem.multiobjective.maf;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem MaF01
 */
@SuppressWarnings("serial")
public class MaF01 extends AbstractDoubleProblem {

  /**
   * Default constructor
   */
  public MaF01() {
    this(12, 3) ;
  }

  /**
   * Creates a MaF01 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public MaF01(Integer numberOfVariables,
      Integer numberOfObjectives) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF01");

    List<Double> lower = new ArrayList<>(getNumberOfVariables()), upper = new ArrayList<>(
        getNumberOfVariables());

    for (int var = 0; var < numberOfVariables; var++) {
      lower.add(0.0);
      upper.add(1.0);
    }

    setLowerLimit(lower);
    setUpperLimit(upper);
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   */
  @Override
  public void evaluate(DoubleSolution solution) {
    int numberOfVariables = solution.getNumberOfVariables();
    int numberOfObjectives = solution.getNumberOfObjectives();

    double[] x = new double[numberOfVariables];
    double[] f = new double[numberOfObjectives];

    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = solution.getVariableValue(i);
    }

    double g = 0, subf1 = 1, subf3;
    for (int j = numberOfObjectives - 1; j < numberOfVariables; j++) {
      g += (Math.pow(x[j] - 0.5, 2));
    }
    subf3 = 1 + g;

    f[numberOfObjectives - 1] = x[0] * subf3;
    for (int i = numberOfObjectives - 2; i > 0; i--) {
      subf1 *= x[numberOfObjectives - i - 2];
      f[i] = subf3 * (1 - subf1 * (1 - x[numberOfObjectives - i - 1]));
    }
    f[0] = (1 - subf1 * x[numberOfObjectives - 2]) * subf3;

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.setObjective(i, f[i]);
    }
  }
}
