package org.uma.jmetal.problem.multiobjective.cf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.VectorUtils;

/**
 * Class representing the constrained function CF9: C-type constraint + r=0 + Mixed PF + Ackley
 *
 * Problem defined in Constrained "Multiobjective Optimization: Test Problem Construction and
 * Performance Evaluations", IEEE TEC. Feb. 2021. DOI: https://doi.org/10.1109/TEVC.2020.3011829
 *
 * @author Yi Xiang Email: xiangyi@scut.edu.cn or gzhuxiang_yi@163.com
 * @author Code adapted by Antonio J. Nebro
 */
public class CF9 extends AbstractDoubleProblem {
  private int k; // The parameter determines the number of constraints
  private double alpha = 1.0 / 4.0;
  private double belta = 3.0 / 4.0;

  /** Create a default CF9 problem (3 variables and 3 objectives) */
  public CF9() {
    this(3, 3); // 3 objectives and 3 decision variables
  }

  /**
   * Creates a CF9 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public CF9(Integer numberOfVariables, Integer numberOfObjectives) {
    setNumberOfObjectives(numberOfObjectives);
    setName("CF9");

    if (numberOfObjectives <= 3) {
      k = numberOfObjectives - 1; // k=1,2,...,m-1
    } else if (numberOfObjectives <= 8) {
      k = numberOfObjectives / 2; //
    } else {
      k = 3;
    }

    setNumberOfConstraints(2 * k + 1);

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
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
  public DoubleSolution evaluate(DoubleSolution solution) {
    var x = VectorUtils.toArray(solution.variables());
    var f = new double[getNumberOfObjectives()];
    var constraint = new double[getNumberOfConstraints()];

    /* ----------------------Evaluate objectives (begin)--------------------------*/
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
    var bound = getNumberOfObjectives() - 1;
    for (var i1 = 0; i1 < bound; i1++) {
      var v = 2.0 / Math.PI * Math.atan(Math.sqrt(sx[i1 + 1]) / x[i1]);
      if (theta.length == count) theta = Arrays.copyOf(theta, count * 2);
      theta[count++] = v;
    }
    theta = Arrays.copyOfRange(theta, 0, count);

    // Step 3. Compute T_
    var t = (1 - sx[0]) * (1 - sx[0]); // (1 - XI^2)^2

    // Compute h function. Here is Ackley function
    var OptX = 0.2;
    var sum1 = 0.0;
    var sum2 = 0.0;
    double d = getNumberOfVariables() - getNumberOfObjectives();

    for (var i = getNumberOfObjectives(); i < getNumberOfVariables(); i++) {
      sum1 = sum1 + ((x[i] - OptX) * (x[i] - OptX));
      sum2 = sum2 + Math.cos(2 * Math.PI * (x[i] - OptX));
    }

    sum1 = sum1 / d;
    sum2 = sum2 / d;

    var h = 20 - 20 * Math.exp(-0.2 * Math.sqrt(sum1)) - Math.exp(sum2) + Math.exp(1);

    t = t + h; // Add h to T_

    // Step 4. Specify PF shape: Mixed
    var sumProd = 1.0;

    for (var i = 0; i < getNumberOfObjectives(); i++) {
      if (i != getNumberOfObjectives() - 1) {
        f[i] = 1 - sumProd * Math.cos(Math.PI / 2.0 * theta[i]);
        sumProd *= Math.sin(Math.PI / 2.0 * theta[i]);
      } else {
        f[i] = 1 - sumProd;
      }
    }

    var A = 2.0;

    f[0] = theta[0] - (Math.cos(2 * Math.PI * A * theta[0] + Math.PI / 2.0)) / (2 * A * Math.PI);

    // Step 5. Set objectives
    for (var i = 0; i < getNumberOfObjectives(); i++) {
      solution.objectives()[i] = (1 + t) * f[i];
    }
    /* ----------------------Evaluate objectives (end)--------------------------*/

    /* ----------------------Evaluate C-type constraints (begin)-------------------------*/
    // The first constraint
    constraint[0] = sx[0] + h - 1; //

    // Other constraints, if necessary
    for (var i = 0; i < k; i++) {
      constraint[2 * i + 1] = theta[i] - alpha;
      constraint[2 * i + 2] = -(theta[i] - belta);
    }

    // Set constraints
    for (var i = 0; i < getNumberOfConstraints(); i++) {
      solution.constraints()[i] = constraint[i];
    }

    var overallConstraintViolation = 0.0;
    var numberOfViolatedConstraints = 0;

    // The first constraint
    if (constraint[0] > 0.0) {
      overallConstraintViolation = constraint[0];
      numberOfViolatedConstraints++;
    }

    // Evaluate constraints
    for (var i = 0; i < k; i++) {
      if (constraint[2 * i + 1] > 0.0 && constraint[2 * i + 2] > 0.0) { // Constraints Violated
        numberOfViolatedConstraints += 2;
        overallConstraintViolation += Math.min(constraint[2 * i + 1], constraint[2 * i + 2]);
      }
    }

    ConstraintHandling.overallConstraintViolationDegree(solution, overallConstraintViolation);
    ConstraintHandling.numberOfViolatedConstraints(solution, numberOfViolatedConstraints) ;
    /* ----------------------Evaluate constraints (end)--------------------------*/
    return solution;
  }
}
