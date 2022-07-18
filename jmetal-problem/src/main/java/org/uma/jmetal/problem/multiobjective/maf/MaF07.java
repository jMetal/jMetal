package org.uma.jmetal.problem.multiobjective.maf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem MaF07
 */
@SuppressWarnings("serial")
public class MaF07 extends AbstractDoubleProblem {

  /**
   * Default constructor
   */
  public MaF07() {
    this(22, 3) ;
  }

  /**
   * Creates a MaF07 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public MaF07(Integer numberOfVariables,
      Integer numberOfObjectives) {
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF07");

    List<Double> lower = new ArrayList<>(numberOfVariables), upper = new ArrayList<>(
        numberOfVariables);

    IntStream.range(0, numberOfVariables).forEach(i -> {
      lower.add(0.0);
      upper.add(1.0);
    });

    setVariableBounds(lower, upper);
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    int numberOfVariables = solution.variables().size();
    int numberOfObjectives = solution.objectives().length;

    double[] x;
    double[] f;

      x = IntStream.range(0, numberOfVariables).mapToDouble(i -> solution.variables().get(i)).toArray();

    // evaluate g,h
    double g, h, sub1;
      g = Arrays.stream(x, numberOfObjectives - 1, numberOfVariables).sum();
    g = 1 + 9 * g / (numberOfVariables - numberOfObjectives + 1);
    sub1 = 1 + g;
      h = IntStream.range(0, numberOfObjectives - 1).mapToDouble(i -> (x[i] * (1 + Math.sin(3 * Math.PI * x[i])) / sub1)).sum();
    h = numberOfObjectives - h;
    // evaluate f1,...,m-1,m
      f = Arrays.stream(x, 0, numberOfObjectives).toArray();
    f[numberOfObjectives - 1] = h * sub1;

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.objectives()[i] = f[i];
    }
    return solution ;
  }
}
