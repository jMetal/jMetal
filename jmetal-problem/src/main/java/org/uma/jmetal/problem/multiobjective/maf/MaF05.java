package org.uma.jmetal.problem.multiobjective.maf;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem MaF05
 */
@SuppressWarnings("serial")
public class MaF05 extends AbstractDoubleProblem {

  public static double const5[];

  /**
   * Default constructor
   */
  public MaF05() {
    this(12, 3);
  }

  /**
   * Creates a MaF05 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public MaF05(Integer numberOfVariables,
      Integer numberOfObjectives) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF05");

    List<Double> lower = new ArrayList<>(getNumberOfVariables()), upper = new ArrayList<>(
        getNumberOfVariables());

    for (int var = 0; var < numberOfVariables; var++) {
      lower.add(0.0);
      upper.add(1.0);
    }

    setLowerLimit(lower);
    setUpperLimit(upper);

    //other constants during the whole process once M&D are defined
    double[] c5 = new double[numberOfObjectives];
    for (int i = 0; i < numberOfObjectives; i++) {
      c5[i] = Math.pow(2, i + 1);
    }
    const5 = c5;
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
    double g = 0;
    // evaluate g
    for (int i = numberOfObjectives - 1; i < numberOfVariables; i++) {
      g += Math.pow(x[i] - 0.5, 2);
    }
    double subf1 = 1, subf3 = 1 + g;
    // evaluate fm,fm-1,...2,f1
    f[numberOfObjectives - 1] =
        2 * Math.pow(Math.sin(Math.PI * Math.pow(x[0], 100) / 2) * subf3, 1);
    // fi=2^i*(subf1*subf2)*(subf3)
    for (int i = numberOfObjectives - 2; i > 0; i--) {
      subf1 *= Math.cos(Math.PI * Math.pow(x[numberOfObjectives - i - 2], 100) / 2);
      f[i] = const5[numberOfObjectives - i - 1] * Math.pow(
          subf1 * Math.sin(Math.PI * Math.pow(x[numberOfObjectives - i - 1], 100) / 2) * subf3, 1);
    }
    f[0] = const5[numberOfObjectives - 1] * Math
        .pow(subf1 * (Math.cos(Math.PI * Math.pow(x[numberOfObjectives - 2], 100) / 2)) * subf3,
            1);

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.setObjective(i, f[i]);
    }

  }
}
