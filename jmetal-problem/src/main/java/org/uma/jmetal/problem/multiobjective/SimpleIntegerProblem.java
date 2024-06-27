package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.integerproblem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SimpleIntegerProblem extends AbstractIntegerProblem {
  public SimpleIntegerProblem() {
    int numberOfVariables = 2;
    numberOfObjectives(2);
    numberOfConstraints(3);
    name("Example");

    List<Integer> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Integer> upperLimit = new ArrayList<>(numberOfVariables);

    for (int i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(0);
      upperLimit.add(20);
    }

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public IntegerSolution evaluate(IntegerSolution solution) {
    int[] f = new int[solution.variables().size()];

    int x1 = solution.variables().get(0);
    int x2 = solution.variables().get(1);
    f[0] = -1 * (x1 + x2);
    f[1] = x1 + 3 * x2;

    solution.objectives()[0] = f[0];
    solution.objectives()[1] = f[1];

    evaluateConstraints(solution);
    return solution;
  }

  public void evaluateConstraints(IntegerSolution solution) {
    double[] constraint = new double[this.numberOfConstraints()];

    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);

    constraint[0] = -2 * x1 - 3 * x2 + 30 ;
    constraint[1] = -3 * x1 - 2 * x2 + 30 ;
    constraint[2] = -x1 + x2 + 5.5;

    IntStream.range(0, numberOfConstraints())
        .forEach(i -> solution.constraints()[i] = constraint[i]);
  }
}
