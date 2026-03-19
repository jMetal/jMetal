package org.uma.jmetal.problem.multiobjective;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem CONV4_2F, a four-objective problem with 4 variables. Variables are
 * bounded in [-3.0, 3.0]
 */
public class Conv4_2F extends AbstractDoubleProblem {
  private final double[][] A;
  private final double[] sigma;
  private final double[] onesVec;

  public Conv4_2F() {
    numberOfObjectives(4);
    numberOfConstraints(0);
    name("CONV4_2F");

    List<Double> lowerLimit = Arrays.asList(-3.0, -3.0, -3.0, -3.0);
    List<Double> upperLimit = Arrays.asList(3.0, 3.0, 3.0, 3.0);

    variableBounds(lowerLimit, upperLimit);

    // Initialize A matrix (canonical basis vectors)
    this.A =
        new double[][] {
          {1, 0, 0, 0},
          {0, 1, 0, 0},
          {0, 0, 1, 0},
          {0, 0, 0, 1}
        };
    this.onesVec = new double[] {1, 1, 1, 1};

    // Calculate sigma
    double[] a1 = A[0];
    double[] a4 = A[3];

    double[] phi1 = new double[4];
    double[] phi4 = new double[4];

    for (int j = 0; j < 4; j++) {
      phi1[j] = j == 0 ? 0 : squaredEuclideanDistance(a1, A[j]);
      phi4[j] = j == 3 ? 0 : squaredEuclideanDistance(a4, A[j]);
    }

    this.sigma = new double[4];
    for (int i = 0; i < 4; i++) {
      this.sigma[i] = phi4[i] - phi1[i];
    }
  }

  private double squaredEuclideanDistance(double[] a, double[] b) {
    double sum = 0;
    for (int i = 0; i < a.length; i++) {
      sum += Math.pow(a[i] - b[i], 2);
    }
    return sum;
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = solution.variables().stream().mapToDouble(Double::doubleValue).toArray();

    double[] f = new double[4];

    // First formulation
    for (int i = 0; i < 4; i++) {
      f[i] = 0;
      for (int j = 0; j < 4; j++) {
        f[i] += Math.pow(x[j] - A[i][j], 2);
      }
    }

    // Second formulation (product)
    double product = 1.0;
    for (int j = 0; j < 4; j++) {
      product *= (1.0 - x[j]);
    }

    // Combine formulations
    for (int i = 0; i < 4; i++) {
      f[i] = f[i] + product * sigma[i];
    }

    for (int i = 0; i < 4; i++) {
      solution.objectives()[i] = f[i];
    }

    return solution;
  }
}
