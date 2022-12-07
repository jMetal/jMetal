package org.uma.jmetal.problem.multiobjective;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/** Class representing problem Water */
@SuppressWarnings("serial")
public class Water extends AbstractDoubleProblem {
  // defining the lower and upper limits
  public static final Double[] LOWERLIMIT = {0.01, 0.01, 0.01};
  public static final Double[] UPPERLIMIT = {0.45, 0.10, 0.10};

  /** Constructor. Creates a default instance of the Water problem. */
  public Water() {
    numberOfObjectives(5);
    numberOfConstraints(7);
    name("Water");

    List<Double> lowerLimit = Arrays.asList(LOWERLIMIT);
    List<Double> upperLimit = Arrays.asList(UPPERLIMIT);

    variableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] fx = new double[solution.objectives().length];
    double[] x = new double[solution.variables().size()];
    for (int i = 0; i < solution.variables().size(); i++) {
      x[i] = solution.variables().get(i);
    }

    fx[0] = 106780.37 * (x[1] + x[2]) + 61704.67;
    fx[1] = 3000 * x[0];
    fx[2] = 305700 * 2289 * x[1] / Math.pow(0.06 * 2289, 0.65);
    fx[3] = 250 * 2289 * Math.exp(-39.75 * x[1] + 9.9 * x[2] + 2.74);
    fx[4] = 25 * (1.39 / (x[0] * x[1]) + 4940 * x[2] - 80);

    solution.objectives()[0] = fx[0];
    solution.objectives()[1] = fx[1];
    solution.objectives()[2] = fx[2];
    solution.objectives()[3] = fx[3];
    solution.objectives()[4] = fx[4];

    evaluateConstraints(solution);
    return solution ;
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
    double[] constraint = new double[numberOfConstraints()];
    double[] x = new double[solution.variables().size()];
    for (int i = 0; i < solution.variables().size(); i++) {
      x[i] = solution.variables().get(i);
    }

    constraint[0] = 1 - (0.00139 / (x[0] * x[1]) + 4.94 * x[2] - 0.08);
    constraint[1] = 1 - (0.000306 / (x[0] * x[1]) + 1.082 * x[2] - 0.0986);
    constraint[2] = 50000 - (12.307 / (x[0] * x[1]) + 49408.24 * x[2] + 4051.02);
    constraint[3] = 16000 - (2.098 / (x[0] * x[1]) + 8046.33 * x[2] - 696.71);
    constraint[4] = 10000 - (2.138 / (x[0] * x[1]) + 7883.39 * x[2] - 705.04);
    constraint[5] = 2000 - (0.417 * x[0] * x[1] + 1721.26 * x[2] - 136.54);
    constraint[6] = 550 - (0.164 / (x[0] * x[1]) + 631.13 * x[2] - 54.48);

    for (int i = 0; i < numberOfConstraints(); i++) {
      solution.constraints()[i] = constraint[i];
    }
  }
}
