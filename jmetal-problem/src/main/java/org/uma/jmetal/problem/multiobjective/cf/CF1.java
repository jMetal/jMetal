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
 * Class representing the constrained function CF1: A-type constraint + r = 0 + Sphere + Linear PF
 *
 * Problem defined in Constrained "Multiobjective Optimization: Test Problem Construction and
 * Performance Evaluations", IEEE TEC. Feb. 2021. DOI: https://doi.org/10.1109/TEVC.2020.3011829
 *
 * @author Yi Xiang Email: xiangyi@scut.edu.cn or gzhuxiang_yi@163.com
 * @author Code adapted by Antonio J. Nebro
 */
public class CF1 extends AbstractDoubleProblem {
  /**
   * Create a default CF1 problem (3 variables and 3 objectives)
   */
  public CF1(String solutionType) {
    this(3, 3);
  }

  /**
   * Creates a CF1 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public CF1(Integer numberOfVariables, Integer numberOfObjectives) {
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(1);
    setName("CF1");

    @NotNull List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Double> upperLimit = new ArrayList<>(numberOfVariables);

    for (var i1 = 0; i1 < numberOfVariables; i1++) {
      lowerLimit.add(0.0 + 1e-10);
    }
    for (var i = 0; i < numberOfVariables; i++) {
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
    var x = VectorUtils.toArray(solution.variables());
    var f = new double[getNumberOfObjectives()];
    var constraint = new double[getNumberOfConstraints()];

    /* ----------------------Evaluate objectives (begin)-------------------------- */
    var sx = new double[getNumberOfObjectives()]; // Cumulative squared sum

    // Step 1. Compute squredSum Sx
    var squredSum = 0.0;
    for (var i = getNumberOfObjectives() - 1; i >= 0; i--) {
      squredSum = squredSum + x[i] * x[i];
      sx[i] = squredSum;
    }

    // Step 2. Compute THETA_
    var theta = new double[10];
    var count = 0;
    var bound2 = getNumberOfObjectives() - 1;
    for (var i2 = 0; i2 < bound2; i2++) {
      var v1 = 2.0 / Math.PI * Math.atan(Math.sqrt(sx[i2 + 1]) / x[i2]);
      if (theta.length == count) theta = Arrays.copyOf(theta, count * 2);
      theta[count++] = v1;
    }
    theta = Arrays.copyOfRange(theta, 0, count);

    // Step 3. Compute T_
    var t = (1 - sx[0]) * (1 - sx[0]); // (1 - XI^2)^2

    // Compute h function. Here is Sphere function
    var optX = 0.2;
    var h = 0.0;
    var bound1 = getNumberOfVariables();
    for (var i1 = getNumberOfObjectives(); i1 < bound1; i1++) {
      var v = ((x[i1] - optX) * (x[i1] - optX));
      h += v;
    }

    t = t + h; // Add h to T_

    // Step 4. Specify PF shape: Linear
    var sumProd = 1.0;

    for (var i = 0; i < getNumberOfObjectives(); i++) {
      if (i != getNumberOfVariables() - 1) {
        f[i] = sumProd * (1 - theta[i]);
        sumProd *= theta[i];
      } else {
        f[i] = sumProd;
      }
    }

    // Step 5. Set objectives
    for (var i = 0; i < getNumberOfObjectives(); i++) {
      solution.objectives()[i] = (1 + t) * f[i];
    }
    /* ----------------------Evaluate objectives (end)--------------------------*/

    /* ----------------------Evaluate A-type constraints (begin)--------------------------*/
    // The first constraint
    constraint[0] = sx[0] + h - 1; //
    // Other constraints, if necessary

    // Set constraints
    var bound = getNumberOfConstraints();
    for (var i = 0; i < bound; i++) {
      solution.constraints()[i] = constraint[i];
    }

    /* ----------------------Evaluate constraints (end)--------------------------*/
    return solution;
  }
}
