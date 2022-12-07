package org.uma.jmetal.problem.multiobjective.UF;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/** Class representing problem CEC2009_UF4 */
@SuppressWarnings("serial")
public class UF4 extends AbstractDoubleProblem {

  /** Constructor. Creates a default instance of problem CEC2009_UF4 (30 decision variables) */
  public UF4() {
    this(30);
  }

  /**
   * Creates a new instance of problem CEC2009_UF4.
   *
   * @param numberOfVariables Number of variables.
   */
  public UF4(Integer numberOfVariables) {
    numberOfObjectives(2);
    numberOfConstraints(0);
    name("UF4");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Double> upperLimit = new ArrayList<>(numberOfVariables);

    lowerLimit.add(0.0);
    upperLimit.add(1.0);
    for (int i = 1; i < numberOfVariables; i++) {
      lowerLimit.add(-2.0);
      upperLimit.add(2.0);
    }

    variableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = new double[numberOfVariables()];
    for (int i = 0; i < solution.variables().size(); i++) {
      x[i] = solution.variables().get(i);
    }

    int count1, count2;
    double sum1, sum2, yj, hj;
    sum1 = sum2 = 0.0;
    count1 = count2 = 0;

    for (int j = 2; j <= numberOfVariables(); j++) {
      yj = x[j - 1] - Math.sin(6.0 * Math.PI * x[0] + j * Math.PI / numberOfVariables());
      hj = Math.abs(yj) / (1.0 + Math.exp(2.0 * Math.abs(yj)));
      if (j % 2 == 0) {
        sum2 += hj;
        count2++;
      } else {
        sum1 += hj;
        count1++;
      }
    }

    solution.objectives()[0] = x[0] + 2.0 * sum1 / (double) count1;
    solution.objectives()[1] = 1.0 - x[0] * x[0] + 2.0 * sum2 / (double) count2;

    return solution;
  }
}
