package org.uma.jmetal.problem.multiobjective;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/** Class representing problem Srinivas */
@SuppressWarnings("serial")
public class Srinivas extends AbstractDoubleProblem {

  /** Constructor */
  public Srinivas() {
    int numberOfVariables = 2 ;
    numberOfObjectives(2);
    numberOfConstraints(2);
    name("Srinivas");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Double> upperLimit = new ArrayList<>(numberOfVariables);

    for (int i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(-20.0);
      upperLimit.add(20.0);
    }

    variableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] f = new double[solution.variables().size()];

    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    f[0] = 2.0 + (x1 - 2.0) * (x1 - 2.0) + (x2 - 1.0) * (x2 - 1.0);
    f[1] = 9.0 * x1 - (x2 - 1.0) * (x2 - 1.0);

    solution.objectives()[0] = f[0];
    solution.objectives()[1] = f[1];

    evaluateConstraints(solution);
    return solution;
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
    double[] constraint = new double[this.numberOfConstraints()];

    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);

    constraint[0] = 1.0 - (x1 * x1 + x2 * x2) / 225.0;
    constraint[1] = (3.0 * x2 - x1) / 10.0 - 1.0;

    IntStream.range(0, numberOfConstraints())
        .forEach(i -> solution.constraints()[i] = constraint[i]);
  }
}
