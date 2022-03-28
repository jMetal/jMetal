package org.uma.jmetal.problem.multiobjective.cf;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.VectorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Class representing the constrained function CF2: A-type constraint + r = 1/4 + Rosenbrock +
 * Concave PF
 *
 * @author Yi Xiang Email: xiangyi@scut.edu.cn or gzhuxiang_yi@163.com
 * @author Code adapted by Antonio J. Nebro
 */
public class CF2 extends AbstractDoubleProblem {
  private final double r = 1.0 / 4.0; // The parameter determines the radius

  /** Create a default CF2 problem (3 variables and 3 objectives) */
  public CF2() {
    this(3, 3);
  }

  /**
   * Creates a CF2 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public CF2(Integer numberOfVariables, Integer numberOfObjectives) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(2);
    setName("CF2");

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

    // Step 1. Compute squredSum Sx
    double squredSum = 0.0;
    for (int i = getNumberOfObjectives() - 1; i >= 0; i--) {
      squredSum = squredSum + x[i] * x[i];
      sx[i] = squredSum;
    }

    // Step 2. Compute THETA_
    double[] theta = new double[getNumberOfObjectives() - 1];
    for (int i = 0; i < getNumberOfObjectives() - 1; i++) {
      theta[i] = 2.0 / Math.PI * Math.atan(Math.sqrt(sx[i + 1]) / x[i]);
    }

    // Step 3. Compute T_
    double t;
    t = (1 - sx[0]) * (1 - sx[0]); // (1 - XI^2)^2

    // Compute h function. Here is Rosenbrock function.
    double optX = 0.2;
    double h = 0.0;

    for (int i = getNumberOfObjectives(); i < getNumberOfVariables() - 1; i++) {
      h =
          h
              + 100 * (Math.pow((x[i] - optX) * (x[i] - optX) - (x[i + 1] - optX), 2))
              + (x[i] - optX) * (x[i] - optX);
    }

    t = t + h; // Add h to T_

    // Step 4. Specify PF shape: Concave
    double sumProd = 1.0;

    for (int i = 0; i < getNumberOfObjectives(); i++) {
      if (i != getNumberOfObjectives() - 1) {
        f[i] = sumProd * Math.cos(Math.PI / 2.0 * theta[i]);
        sumProd *= Math.sin(Math.PI / 2.0 * theta[i]);
      } else {
        f[i] = sumProd;
      }
    }

    // Step 5. Set objectives
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      solution.objectives()[i] =  (1 + t) * f[i];
    }
    /* ----------------------Evaluate objectives (end)--------------------------*/

    /* ----------------------Evaluate A-type constraints (begin)--------------------------*/
    // The first constraint
    constraint[0] = sx[0] + h - 1; //
    // The second constraint
    constraint[1] = -(sx[0] + h - r); //

    // Set constraints
    IntStream.range(0, getNumberOfConstraints())
        .forEach(i -> solution.constraints()[i] = constraint[i]);

    /* ----------------------Evaluate constraints (end)--------------------------*/
    return solution;
  }
}
