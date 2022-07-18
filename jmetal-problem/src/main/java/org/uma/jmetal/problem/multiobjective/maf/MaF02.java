package org.uma.jmetal.problem.multiobjective.maf;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem MaF02, DTLZ2BZ
 */
@SuppressWarnings("serial")
public class MaF02 extends AbstractDoubleProblem {

  public int const2;

  /**
   * Default constructor
   */
  public MaF02() {
    this(12, 3) ;
  }

  /**
   * Creates a MaF02 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public MaF02(Integer numberOfVariables,
      Integer numberOfObjectives) {
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF02");

    const2 = (int) Math
        .floor((numberOfVariables - numberOfObjectives + 1) / (double) numberOfObjectives);

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
    double[] f = new double[numberOfObjectives];

      x = IntStream.range(0, numberOfVariables).mapToDouble(i -> solution.variables().get(i)).toArray();

    double[] g = new double[numberOfObjectives];
    double[] thet = new double[numberOfObjectives - 1];
    int lb, ub;
    // evaluate g, thet
    for (int i = 0; i < numberOfObjectives - 1; i++) {
      g[i] = 0;
      lb = numberOfObjectives + i * const2;
      ub = numberOfObjectives + (i + 1) * const2 - 1;
      for (int j = lb - 1; j < ub; j++) {
        g[i] += Math.pow(x[j] / 2 - 0.25, 2);
      }
      thet[i] = Math.PI / 2 * (x[i] / 2 + 0.25);
    }
    lb = numberOfObjectives + (numberOfObjectives - 1) * const2;
    ub = numberOfVariables;
    for (int j = lb - 1; j < ub; j++) {
      g[numberOfObjectives - 1] += Math.pow(x[j] / 2 - 0.25, 2);
    }
    // evaluate fm,fm-1,...,2,f1
    f[numberOfObjectives - 1] = Math.sin(thet[0]) * (1 + g[numberOfObjectives - 1]);
    double subf1 = 1;
    // fi=cos(thet1)cos(thet2)...cos(thet[m-i])*sin(thet(m-i+1))*(1+g[i]),fi=subf1*subf2*subf3
    for (int i = numberOfObjectives - 2; i > 0; i--) {
      subf1 *= Math.cos(thet[numberOfObjectives - i - 2]);
      f[i] = subf1 * Math.sin(thet[numberOfObjectives - i - 1]) * (1 + g[i]);
    }
    f[0] = subf1 * Math.cos(thet[numberOfObjectives - 2]) * (1 + g[0]);

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.objectives()[i] = f[i];
    }
    return solution ;
  }
}
