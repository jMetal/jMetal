package org.uma.jmetal.problem.multiobjective.lircmop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

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
    setNumberOfObjectives(2);
    setNumberOfConstraints(2);
    setName("LIRCMOP1");

    @NotNull List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Double> upperLimit = new ArrayList<>(numberOfVariables);

    for (var i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(@NotNull DoubleSolution solution) {
      var x = new double[10];
      var count = 0;
      var bound = getNumberOfVariables();
      for (var i = 0; i < bound; i++) {
          double v = solution.variables().get(i);
          if (x.length == count) x = Arrays.copyOf(x, count * 2);
          x[count++] = v;
      }
      x = Arrays.copyOfRange(x, 0, count);

      solution.objectives()[0] = x[0] + g1(x);
    solution.objectives()[1] = 1 - x[0] * x[0] + g2(x);

    evaluateConstraints(solution);
    return solution ;
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
      var x = new double[10];
      var count = 0;
      var bound = getNumberOfVariables();
      for (var i = 0; i < bound; i++) {
          double v = solution.variables().get(i);
          if (x.length == count) x = Arrays.copyOf(x, count * 2);
          x[count++] = v;
      }
      x = Arrays.copyOfRange(x, 0, count);

      final var a = 0.51;
    final var b = 0.5;

    solution.constraints()[0] = (a - g1(x)) * (g1(x) - b);
    solution.constraints()[1] = (a - g2(x)) * (g2(x) - b);
  }

  protected double g1(double[] x) {
      var result = IntStream.iterate(2, i -> i < getNumberOfVariables(), i -> i + 2).mapToDouble(i -> Math.pow(x[i] - Math.sin(0.5 * Math.PI * x[0]), 2.0)).sum();
      return result;
  }

  protected double g2(double[] x) {
      var result = IntStream.iterate(1, i -> i < getNumberOfVariables(), i -> i + 2).mapToDouble(i -> Math.pow(x[i] - Math.cos(0.5 * Math.PI * x[0]), 2.0)).sum();

      return result;
  }
}
