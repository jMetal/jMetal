package org.uma.jmetal.problem.multiobjective.lircmop;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Class representing problem LIR-CMOP13, defined in: An Improved epsilon-constrained Method in
 * MOEA/D for CMOPs with Large Infeasible Regions Fan, Z., Li, W., Cai, X. et al. Soft Comput
 * (2019). https://doi.org/10.1007/s00500-019-03794-x
 */
@SuppressWarnings("serial")
public class LIRCMOP13 extends AbstractDoubleProblem {
  /** Constructor */
  public LIRCMOP13() {
    this(30);
  }
  /** Constructor */
  public LIRCMOP13(int numberOfVariables) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(3);
    setNumberOfConstraints(2);
    setName("LIRCMOP13");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution) {
    double[] x = new double[getNumberOfVariables()];
    for (int i = 0; i < getNumberOfVariables(); i++) {
      x[i] = solution.getVariable(i);
    }

    solution.setObjective(
        0, (1.7057 + g1(x)) * cos(0.5 * Math.PI * x[0]) * cos(0.5 * Math.PI + x[1]));
    solution.setObjective(
        1, (1.7057 + g1(x)) * cos(0.5 * Math.PI * x[0]) * sin(0.5 * Math.PI + x[1]));
    solution.setObjective(2, (1.7057 + g1(x)) * sin(0.5 * Math.PI + x[0]));

    evaluateConstraints(solution);
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
    double[] constraint = new double[getNumberOfConstraints()];

    double f = 0;
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      f += Math.pow(solution.getObjective(i), 2);
    }
    constraint[0] = (f - 9) * (f - 4);
    constraint[1] = (f - 1.9 * 1.9) * (f - 1.8 * 1.8);

    solution.setConstraint(0, constraint[0]);
    solution.setConstraint(1, constraint[1]);
  }

  protected double g1(double[] x) {
    double result = 0.0;
    for (int i = 2; i < getNumberOfVariables(); i += 2) {
      result += 10 * Math.pow(x[i] - 0.5, 2.0);
    }
    return result;
  }
}
