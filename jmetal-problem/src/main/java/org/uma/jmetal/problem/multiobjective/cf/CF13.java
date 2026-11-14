package org.uma.jmetal.problem.multiobjective.cf;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.VectorUtils;

/**
 * Class representing the constrained function CF13: D-type constraint + r=0 + concave PF + Griewank
 *
 * Problem defined in Y. Zhou, Y. Xiang and X. He, "Constrained Multiobjective Optimization: Test
 * Problem Construction and Performance Evaluations", IEEE Transactions on Evolutionary Computation
 * 25(1):172-186, 2021. DOI: https://doi.org/10.1109/TEVC.2020.3011829
 *
 * The constraints follow the convention of jMetal: a negative value means a violated constraint.
 * The first one, {@code 1 - (S + h) >= 0}, bounds the sum S of the squares of the first M variables
 * plus the distance function h. Each of the other k is the D-type constraint of one of the first k
 * angles theta_i (scaled to [0, 1]),
 * {@code min(max(1/10 - theta_i, theta_i - 2/5), max(7/10 - theta_i, theta_i - 4/5)) >= 0}, which
 * is violated when theta_i lies in (1/10, 2/5) or in (7/10, 4/5). As in the paper, k is M - 1 up to
 * three objectives, M / 2 (rounded down) from four to eight and 3 from nine, so the problem has
 * k + 1 constraints.
 *
 * @author Yi Xiang Email: xiangyi@scut.edu.cn or gzhuxiang_yi@163.com
 * @author Code adapted by Antonio J. Nebro
 */
public class CF13 extends AbstractDoubleProblem {
  private int k; // The parameter determines the number of constraints
  private double alpha = 1.0 / 4.0;
  private double belta = 3.0 / 4.0;

  /** Create a default CF13 problem (13 variables and 3 objectives) */
  public CF13() {
    this(13, 3);
  }

  /**
   * Creates a CF13 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public CF13(Integer numberOfVariables, Integer numberOfObjectives) {
    numberOfObjectives(numberOfObjectives);
    name("CF12");

    if (numberOfObjectives <= 3) {
      k = numberOfObjectives - 1; // k=1,2,...,m-1
    } else if (numberOfObjectives <= 8) {
      k = numberOfObjectives / 2; //
    } else {
      k = 3;
    }

    numberOfConstraints(k + 1);

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Double> upperLimit = new ArrayList<>(numberOfVariables);

    IntStream.range(0, numberOfVariables).forEach(i -> lowerLimit.add(0.0 + 1e-10));
    IntStream.range(0, numberOfVariables).forEach(i -> upperLimit.add(1.0 - 1e-10));

    variableBounds(lowerLimit, upperLimit);
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   */
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = VectorUtils.toArray(solution.variables());
    double[] f = new double[numberOfObjectives()];
    double[] constraint = new double[numberOfConstraints()];

    /* ----------------------Evaluate objectives (begin)------------------------- */
    double[] sx = new double[numberOfObjectives()]; // Cumulative squared sum

    // Step 1. Compute squredSum Sx
    double squredSum = 0.0;
    for (int i = numberOfObjectives() - 1; i >= 0; i--) {
      squredSum = squredSum + x[i] * x[i];
      sx[i] = squredSum;
    }

    // Step 2. Compute THETA_
    double[] theta = new double[numberOfObjectives() - 1];
    for (int i = 0; i < numberOfObjectives() - 1; i++) {
      theta[i] = 2.0 / Math.PI * Math.atan(Math.sqrt(sx[i + 1]) / x[i]);
    }

    // Step 3. Compute T_
    double t;
    t = (1 - sx[0]) * (1 - sx[0]); // (1 - XI^2)^2

    // Compute h function. Here is Griewank function
    double OptX = 0.2;
    double sum2 = 0.0;

    for (int i = numberOfObjectives(); i < numberOfVariables(); i++) {
      sum2 = sum2 + ((x[i] - OptX) * (x[i] - OptX));
    }

    double prod = 1.0;

    for (int i = numberOfObjectives(); i < numberOfVariables(); i++) {
      prod =
          prod
              * Math.cos(10 * Math.PI * (x[i] - OptX) / Math.sqrt(i + 1 - numberOfObjectives()));
    }

    double h = 5 * (1 + sum2 - prod);

    t = t + h; // Add h to T_

    // Step 4. Specify PF shape: Concave
    double sumProd = 1.0;

    for (int i = 0; i < numberOfObjectives(); i++) {
      if (i != numberOfObjectives() - 1) {
        f[i] = sumProd * Math.cos(Math.PI / 2.0 * theta[i]);
        sumProd *= Math.sin(Math.PI / 2.0 * theta[i]);
      } else {
        f[i] = sumProd;
      }
    } // for

    // Step 5. Set objectives
    for (int i = 0; i < numberOfObjectives(); i++) {
      solution.objectives()[i] = (1 + t) * f[i];
    }
    /* ----------------------Evaluate objectives (end)--------------------------*/

    /** ----------------------Evaluate D-type constraints (begin)-------------------------- */
    // The first constraint
    constraint[0] = sx[0] + h - 1; //

    // Other constraints, if necessary: one per angle, violated when it lies in (1/10, 2/5) or
    // in (7/10, 4/5)
    for (int i = 0; i < k; i++) {
      constraint[i + 1] =
          Math.max(
              Math.min(theta[i] - 1.0 / 10.0, 4.0 / 10.0 - theta[i]),
              Math.min(theta[i] - 7.0 / 10.0, 8.0 / 10.0 - theta[i]));
    }

    // Set constraints
    IntStream.range(0, numberOfConstraints())
        .forEach(i -> solution.constraints()[i] = -constraint[i]);
    /* ----------------------Evaluate constraints (end)--------------------------*/
    return solution;
  }
}
