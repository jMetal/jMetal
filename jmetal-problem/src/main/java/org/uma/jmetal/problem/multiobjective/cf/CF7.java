package org.uma.jmetal.problem.multiobjective.cf;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.VectorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Class representing the constrained function CF7: B-type constraint + r=1/2 + Linear PF + Griewank
 *
 * Problem defined in Constrained "Multiobjective Optimization: Test Problem Construction and
 * Performance Evaluations", IEEE TEC. Feb. 2021. DOI: https://doi.org/10.1109/TEVC.2020.3011829
 *
 * @author Yi Xiang Email: xiangyi@scut.edu.cn or gzhuxiang_yi@163.com
 * @author Code adapted by Antonio J. Nebro
 */
public class CF7 extends AbstractDoubleProblem {
  private int k; // The parameter determines the number of constraints
  private double alpha = 1.0 / 4.0;
  private double belta = 3.0 / 4.0;
  private double r = 1.0 / 2.0; // The parameter determines the radius

  /** Create a default CF7 problem (3 variables and 3 objectives) */
  public CF7() {
    this(3, 3);
  }

  /**
   * Creates a CF7 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public CF7(Integer numberOfVariables, Integer numberOfObjectives) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setName("CF7");

    if (numberOfObjectives <= 3) {
      k = numberOfObjectives - 1; // k=1,2,...,m-1
    } else if (numberOfObjectives <= 8) {
      k = numberOfObjectives / 2;
    } else {
      k = 3;
    }

    setNumberOfConstraints(2 * k + 2);

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Double> upperLimit = new ArrayList<>(numberOfVariables);

    IntStream.range(0, numberOfVariables).forEach(i -> lowerLimit.add(0.0 + 1e-10));
    IntStream.range(0, numberOfVariables).forEach(i -> upperLimit.add(1.0 - 1e-10));

    setVariableBounds(lowerLimit, upperLimit);
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   */
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = VectorUtils.toArray(solution.variables());
    double[] f = new double[getNumberOfObjectives()];
    double[] constraint = new double[getNumberOfConstraints()];

    /* ----------------------Evaluate objectives (begin)--------------------------*/
    double[] sx = new double[getNumberOfObjectives()]; // Cumulative squared sum

    // Step 1. Compute squaredSum Sx
    double squaredSum = 0.0;
    for (int i = getNumberOfObjectives() - 1; i >= 0; i--) {
      squaredSum = squaredSum + x[i] * x[i];
      sx[i] = squaredSum;
    }

    // Step 2. Compute THETA_
    double[] theta = new double[getNumberOfObjectives() - 1];
    for (int i = 0; i < getNumberOfObjectives() - 1; i++) {
      theta[i] = 2.0 / Math.PI * Math.atan(Math.sqrt(sx[i + 1]) / x[i]);
    }

    // Step 3. Compute T_
    double t;
    t = (1 - sx[0]) * (1 - sx[0]); // (1 - XI^2)^2

    // Compute h function. Here is Griewank function
    double optX = 0.2;
    double sum2 = 0.0;

    for (int i = getNumberOfObjectives(); i < getNumberOfVariables(); i++) {
      sum2 = sum2 + ((x[i] - optX) * (x[i] - optX));
    }

    double prod = 1.0;

    for (int i = getNumberOfObjectives(); i < getNumberOfVariables(); i++) {
      prod =
          prod
              * Math.cos(10 * Math.PI * (x[i] - optX) / Math.sqrt(i + 1 - getNumberOfObjectives()));
    }

    double h = 5 * (1 + sum2 - prod);

    t = t + h; // Add h to T_

    // Step 4. Specify PF shape: Linear
    double sumProd = 1.0;

    for (int i = 0; i < getNumberOfObjectives(); i++) {
      if (i != getNumberOfObjectives() - 1) {
        f[i] = sumProd * (1 - theta[i]);
        sumProd *= theta[i];
      } else {
        f[i] = sumProd;
      }
    }

    // Step 5. Set objectives
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      solution.objectives()[i] = (1 + t) * f[i];
    }
    /* ----------------------Evaluate objectives (end)--------------------------*/

    /* ----------------------Evaluate B-type constraints (begin)--------------------------*/
    // The first constraint
    constraint[0] = sx[0] + h - 1;
    // The second constraint
    constraint[1] = -(sx[0] + h - r);

    // Other constraints, if necessary
    for (int i = 0; i < k; i++) {
      constraint[2 * (i + 1)] = theta[i] - belta;
      constraint[2 * (i + 1) + 1] = -(theta[i] - alpha);
    }

    // Set constraints
    IntStream.range(0, getNumberOfConstraints())
        .forEach(i -> solution.constraints()[i] = constraint[i]);
    /* ----------------------Evaluate constraints (end)--------------------------*/
    return solution;
  }
}
