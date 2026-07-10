package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem PowerDistributionSystemPlanningProblem (RCM50)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 *
 * Economic/emission dispatch of 6 generators, with a quadratic transmission-loss term computed
 * from the B-loss-coefficient matrix. Equality constraint is relaxed to an inequality with
 * tolerance {@link #EQUALITY_CONSTRAINT_EPSILON}.
 */
public class RCM50PowerDistributionSystemPlanningProblem extends AbstractDoubleProblem {
  private static final double EQUALITY_CONSTRAINT_EPSILON = 1e-4;

  private static final double[][] B = {
      {140, 17, 15, 19, 26, 22},
      {17, 60, 13, 16, 15, 20},
      {15, 13, 65, 17, 24, 19},
      {19, 16, 17, 71, 30, 25},
      {26, 15, 24, 30, 69, 32},
      {22, 20, 19, 25, 32, 85}};

  private static final double[] A_COST = {
      756.7988, 451.3251, 1243.5311, 1049.9977, 1356.6592, 1658.5696};
  private static final double[] B_COST = {38.5390, 46.1591, 38.3055, 40.3965, 38.2704, 36.3278};
  private static final double[] C_COST = {0.15247, 0.10587, 0.03546, 0.02803, 0.01799, 0.02111};
  private static final double[] ALPHA = {13.8593, 13.8593, 40.2669, 40.2669, 42.8955, 42.8955};
  private static final double[] BETA = {0.32767, 0.32767, -0.54551, -0.54551, -0.51116, -0.51116};
  private static final double[] GAMMA = {0.00419, 0.00419, 0.00683, 0.00683, 0.00461, 0.00461};

  public RCM50PowerDistributionSystemPlanningProblem() {
    numberOfObjectives(2);
    numberOfConstraints(1);
    name("PowerDistributionSystemPlanningProblem");

    List<Double> lowerLimit = Arrays.asList(10.0, 10.0, 35.0, 35.0, 125.0, 130.0);
    List<Double> upperLimit = Arrays.asList(125.0, 150.0, 210.0, 225.0, 315.0, 325.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    int n = numberOfVariables();
    double pd = 1200;

    double pl = 0.0;
    for (int i = 0; i < n; i++) {
      double pi = solution.variables().get(i);
      for (int j = 0; j < n; j++) {
        double pj = solution.variables().get(j);
        pl += pi * (B[i][j] * 1e-6) * pj;
      }
    }

    double f1 = 0.0;
    double f2 = 0.0;
    double sumP = 0.0;
    for (int i = 0; i < n; i++) {
      double pi = solution.variables().get(i);
      f1 += A_COST[i] + B_COST[i] * pi + C_COST[i] * pi * pi;
      f2 += ALPHA[i] + BETA[i] * pi + GAMMA[i] * pi * pi;
      sumP += pi;
    }

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;

    double h1 = sumP - pd - pl;
    solution.constraints()[0] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h1);

    return solution;
  }
}
