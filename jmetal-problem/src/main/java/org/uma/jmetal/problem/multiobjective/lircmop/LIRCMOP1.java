package org.uma.jmetal.problem.multiobjective.lircmop;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem LIR-CMOP1, defined in: An Improved epsilon-constrained Method in
 * MOEA/D for CMOPs with Large Infeasible Regions Fan, Z., Li, W., Cai, X. et al. Soft Comput
 * (2019). https://doi.org/10.1007/s00500-019-03794-x
 */
@SuppressWarnings("serial")
public class LIRCMOP1 extends AbstractDoubleProblem {
  /** Constructor */
  public LIRCMOP1() {
    this(30);
  }
  /** Constructor */
  public LIRCMOP1(int numberOfVariables) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(2);
    setNumberOfConstraints(2);
    setName("LIRCMOP1");

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

    solution.setObjective(0, x[0] + g1(x));
    solution.setObjective(1, 1 - x[0] * x[0] + g2(x));

    /*
    double sum1 = 0.0, sum2 = 0.0, yj;
    for (int j = 2; j <= getNumberOfVariables(); j++) {
      if (j % 2 == 1) {
        yj = x[j - 1] - Math.sin(0.5 * Math.PI * x[0]);
        sum1 += yj * yj;
      } else {
        yj = x[j - 1] - Math.cos(0.5 * Math.PI * x[0]);
        sum2 += yj * yj;
      }
    }

    System.out.println("sum1: " + sum1) ;
    System.out.println("sum2: " + sum2) ;
    System.out.println("g1  : " + g1(x)) ;
    System.out.println("g2  : " + g2(x)) ;
    */
    evaluateConstraints(solution);
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
    double[] x = new double[getNumberOfVariables()];
    for (int i = 0; i < getNumberOfVariables(); i++) {
      x[i] = solution.getVariable(i);
    }

    final double a = 0.51;
    final double b = 0.5;

    solution.setConstraint(0, (a - g1(x)) * (g1(x) - b));
    solution.setConstraint(1, (a - g2(x)) * (g2(x) - b));
  }

  protected double g1(double[] x) {
    double result = 0.0;
    for (int i = 2; i < getNumberOfVariables(); i += 2) {
      result += Math.pow(x[i] - Math.sin(0.5 * Math.PI * x[0]), 2.0);
    }
    return result;
  }

  protected double g2(double[] x) {
    double result = 0.0;
    for (int i = 1; i < getNumberOfVariables(); i += 2) {
      result += Math.pow(x[i] - Math.cos(0.5 * Math.PI * x[0]), 2.0);
    }

    return result;
  }
}
