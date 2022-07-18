package org.uma.jmetal.problem.multiobjective.cf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.VectorUtils;

/**
 * Class representing the constrained function CF3: A-type constraint + r = 1/2 + Convex PF +
 * Ackley
 *
 * Problem defined in Constrained "Multiobjective Optimization: Test Problem Construction and
 * Performance Evaluations", IEEE TEC. Feb. 2021. DOI: https://doi.org/10.1109/TEVC.2020.3011829
 *
 * @author Yi Xiang Email: xiangyi@scut.edu.cn or gzhuxiang_yi@163.com
 * @author Code adapted by Antonio J. Nebro
 */
public class CF3 extends AbstractDoubleProblem {

  private final double r = 1.0 / 2.0; // The parameter determines the radius

  /**
   * Create a default CF3 problem (3 variables and 3 objectives)
   */
  public CF3() {
    this(3, 3);
  }

  /**
   * Creates a CF3 problem instance
   *
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public CF3(Integer numberOfVariables, Integer numberOfObjectives) {
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(2);
    setName("CF3");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    @NotNull List<Double> upperLimit = new ArrayList<>(numberOfVariables);

    for (int i1 = 0; i1 < numberOfVariables; i1++) {
      lowerLimit.add(0.0 + 1e-10);
    }
    for (int i = 0; i < numberOfVariables; i++) {
      upperLimit.add(1.0 - 1e-10);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   */
  public @NotNull DoubleSolution evaluate(@NotNull DoubleSolution solution) {
    double @NotNull [] x = VectorUtils.toArray(solution.variables());
    double @NotNull [] f = new double[getNumberOfObjectives()];
    double @NotNull [] constraint = new double[getNumberOfConstraints()];

    /* ----------------------Evaluate objectives (begin)--------------------------*/
    double @NotNull [] sx = new double[getNumberOfObjectives()]; // Cumulative squared sum

    // Step 1. Compute squredSum Sx
    double squredSum = 0.0;
    for (int i = getNumberOfObjectives() - 1; i >= 0; i--) {
      squredSum = squredSum + x[i] * x[i];
      sx[i] = squredSum;
    }

    // Step 2. Compute THETA_
    double @NotNull [] theta = new double[10];
    int count = 0;
    int bound1 = getNumberOfObjectives() - 1;
    for (int i1 = 0; i1 < bound1; i1++) {
      double v = 2.0 / Math.PI * Math.atan(Math.sqrt(sx[i1 + 1]) / x[i1]);
      if (theta.length == count) theta = Arrays.copyOf(theta, count * 2);
      theta[count++] = v;
    }
    theta = Arrays.copyOfRange(theta, 0, count);

    // Step 3. Compute T_
    double t;
    t = (1 - sx[0]) * (1 - sx[0]); // (1 - XI^2)^2

    // Compute h function. Here is Ackley function
    double OptX = 0.2;
    double sum1 = 0.0;
    double sum2 = 0.0;
    double d = getNumberOfVariables() - getNumberOfObjectives();

    for (int i = getNumberOfObjectives(); i < getNumberOfVariables(); i++) {
      sum1 = sum1 + ((x[i] - OptX) * (x[i] - OptX));
      sum2 = sum2 + Math.cos(2 * Math.PI * (x[i] - OptX));
    }

    sum1 = sum1 / d;
    sum2 = sum2 / d;

    double h = 20 - 20 * Math.exp(-0.2 * Math.sqrt(sum1)) - Math.exp(sum2) + Math.exp(1);

    t = t + h; // Add h to T_

    // Step 4. Specify PF shape: Convex
    double sumProd = 1.0;

    for (int i = 0; i < getNumberOfObjectives(); i++) {
      if (i != getNumberOfObjectives() - 1) {
        f[i] = 1 - sumProd * Math.cos(Math.PI / 2.0 * theta[i]);
        sumProd *= Math.sin(Math.PI / 2.0 * theta[i]);
      } else {
        f[i] = 1 - sumProd;
      }
    }

    // Step 5. Set objectives
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      solution.objectives()[i] = (1 + t) * f[i];
    }
    /* ----------------------Evaluate objectives (end)--------------------------*/

    /* ----------------------Evaluate A-type constraints (begin)--------------------------*/
    // The first constraint
    constraint[0] = sx[0] + h - 1;
    // The second constraint
    constraint[1] = -(sx[0] + h - r);

    // Set constraints
    int bound = getNumberOfConstraints();
    for (int i = 0; i < bound; i++) {
      solution.constraints()[i] = constraint[i];
    }
    /* ----------------------Evaluate constraints (end)--------------------------*/
    return solution;
  }
}
