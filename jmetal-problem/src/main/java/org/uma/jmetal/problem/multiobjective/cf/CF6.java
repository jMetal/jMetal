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
 * Class representing the constrained function CF6: B-type constraint + r=1/4 + Mixed PF + Sphere
 *
 * Problem defined in Constrained "Multiobjective Optimization: Test Problem Construction and
 * Performance Evaluations", IEEE TEC. Feb. 2021. DOI: https://doi.org/10.1109/TEVC.2020.3011829
 *
 * @author Yi Xiang Email: xiangyi@scut.edu.cn or gzhuxiang_yi@163.com
 * @author Code adapted by Antonio J. Nebro
 */
public class CF6 extends AbstractDoubleProblem {
  private int k; // The parameter determines the number of constraints
  private double alpha = 1.0 / 4.0;
  private double belta = 3.0 / 4.0;
  private double r = 1.0 / 4.0; // The parameter determines the radius

  /** Create a default CF6 problem (3 variables and 3 objectives) */
  public CF6() {
    this(3, 3); // 3 objectives and 3 decision variables
  }

  /**
   * Creates a CF3 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public CF6(Integer numberOfVariables, Integer numberOfObjectives) {
    setNumberOfObjectives(numberOfObjectives);
    setName("CF6");

    if (numberOfObjectives <= 3) {
      k = numberOfObjectives - 1; // k=1,2,...,m-1
    } else if (numberOfObjectives <= 8) {
      k = numberOfObjectives / 2; //
    } else {
      k = 3;
    }

    setNumberOfConstraints(2 * k + 2);

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
  public DoubleSolution evaluate(@NotNull DoubleSolution solution) {
    double @NotNull [] x = VectorUtils.toArray(solution.variables());
    double[] f = new double[getNumberOfObjectives()];
    double[] constraint = new double[getNumberOfConstraints()];

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
    int bound2 = getNumberOfObjectives() - 1;
    for (int i2 = 0; i2 < bound2; i2++) {
      double v1 = 2.0 / Math.PI * Math.atan(Math.sqrt(sx[i2 + 1]) / x[i2]);
      if (theta.length == count) theta = Arrays.copyOf(theta, count * 2);
      theta[count++] = v1;
    }
    theta = Arrays.copyOfRange(theta, 0, count);

    // Step 3. Compute T_
    double t;
    t = (1 - sx[0]) * (1 - sx[0]); // (1 - XI^2)^2

    // Compute h function. Here is Sphere function
    double optX = 0.2;
    double h = 0.0;
    int bound1 = getNumberOfVariables();
    for (int i1 = getNumberOfObjectives(); i1 < bound1; i1++) {
      double v = ((x[i1] - optX) * (x[i1] - optX));
      h += v;
    }

    t = t + h; // Add h to T_

    // Step 4. Specify PF shape: Mixed
    double sumProd = 1.0;

    for (int i = 0; i < getNumberOfObjectives(); i++) {
      if (i != getNumberOfObjectives() - 1) {
        f[i] = 1 - sumProd * Math.cos(Math.PI / 2.0 * theta[i]);
        sumProd *= Math.sin(Math.PI / 2.0 * theta[i]);
      } else {
        f[i] = 1 - sumProd;
      }
    }

    double A = 2.0;

    f[0] = theta[0] - (Math.cos(2 * Math.PI * A * theta[0] + Math.PI / 2.0)) / (2 * A * Math.PI);

    // Step 5. Set objectives
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      solution.objectives()[i] = (1 + t) * f[i];
    }
    /* ----------------------Evaluate objectives (end)--------------------------*/

    /* ----------------------Evaluate B-type constraints (begin)-------------------------- */
    // The first constraint
    constraint[0] = sx[0] + h - 1; //
    // The second constraint
    constraint[1] = -(sx[0] + h - r); //

    // Other constraints, if necessary
    for (int i = 0; i < k; i++) {
      constraint[2 * (i + 1)] = theta[i] - belta;
      constraint[2 * (i + 1) + 1] = -(theta[i] - alpha);
    }

    // Set constraints
    int bound = getNumberOfConstraints();
    for (int i = 0; i < bound; i++) {
      solution.constraints()[i] = constraint[i];
    }
    /* ----------------------Evaluate constraints (end)--------------------------*/
    return solution;
  }
}
