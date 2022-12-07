package org.uma.jmetal.problem.multiobjective.cf;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.VectorUtils;

/**
 * Class representing the constrained function CF4: A-type constraint + r = 3/4 + Griewank + Mixed
 * PF
 *
 * Problem defined in Constrained "Multiobjective Optimization: Test Problem Construction and
 * Performance Evaluations", IEEE TEC. Feb. 2021. DOI: https://doi.org/10.1109/TEVC.2020.3011829
 *
 * @author Yi Xiang Email: xiangyi@scut.edu.cn or gzhuxiang_yi@163.com
 * @author Code adapted by Antonio J. Nebro
 */
public class CF4 extends AbstractDoubleProblem {
  private final double r = 3.0 / 4.0; // The parameter determines the radius

  /** Create a default CF4 problem (3 variables and 3 objectives) */
  public CF4() {
    this(3, 3);
  }

  /**
   * Creates a CF4 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public CF4(Integer numberOfVariables, Integer numberOfObjectives) {
    numberOfObjectives(numberOfObjectives);
    numberOfConstraints(2);
    name("CF4");

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

    /* ----------------------Evaluate objectives (begin)--------------------------*/
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

    // Step 4. Specify PF shape: Mixed
    double sumProd = 1.0;

    for (int i = 0; i < numberOfObjectives(); i++) {
      if (i != numberOfObjectives() - 1) {
        f[i] = 1 - sumProd * Math.cos(Math.PI / 2.0 * theta[i]);
        sumProd *= Math.sin(Math.PI / 2.0 * theta[i]);
      } else {
        f[i] = 1 - sumProd;
      }
    }

    double A = 2.0;

    f[0] = theta[0] - (Math.cos(2 * Math.PI * A * theta[0] + Math.PI / 2.0)) / (2 * A * Math.PI);

    // Step 5. Set objectives
    for (int i = 0; i < numberOfObjectives(); i++) {
      solution.objectives()[i] = (1 + t) * f[i];
    }
    /* ----------------------Evaluate objectives (end)--------------------------*/

    /* ----------------------Evaluate A-type constraints (begin)-------------------------- */
    // The first constraint
    constraint[0] = sx[0] + h - 1; //
    // The second constraint
    constraint[1] = -(sx[0] + h - r); //

    // Set constraints
    IntStream.range(0, numberOfConstraints())
        .forEach(i -> solution.constraints()[i] = constraint[i]);
    /* ----------------------Evaluate constraints (end)--------------------------*/
    return solution;
  }
}
