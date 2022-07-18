package org.uma.jmetal.problem.multiobjective.maf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem MaF03, convex DTLZ3
 */
@SuppressWarnings("serial")
public class MaF03 extends AbstractDoubleProblem {

  /**
   * Default constructor
   */
  public MaF03() {
    this(12, 3) ;
  }

  /**
   * Creates a MaF03 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public MaF03(Integer numberOfVariables,
      Integer numberOfObjectives) {
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF03");

    List<Double> lower = new ArrayList<>(numberOfVariables), upper = new ArrayList<>(
        numberOfVariables);

      for (var i = 0; i < numberOfVariables; i++) {
          lower.add(0.0);
          upper.add(1.0);
      }

      setVariableBounds(lower, upper);
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {

    var numberOfVariables = solution.variables().size();
    var numberOfObjectives = solution.objectives().length;

    var f = new double[numberOfObjectives];

    var arr = new double[10];
    var count = 0;
      for (var i2 = 0; i2 < numberOfVariables; i2++) {
          double v1 = solution.variables().get(i2);
          if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
          arr[count++] = v1;
      }
      arr = Arrays.copyOfRange(arr, 0, count);
    var x = arr;

    var g = 0.0;
      for (var i1 = numberOfObjectives - 1; i1 < numberOfVariables; i1++) {
        var v = (Math.pow(x[i1] - 0.5, 2) - Math.cos(20 * Math.PI * (x[i1] - 0.5)));
          g += v;
      }
      // evaluate g
      g = 100 * (numberOfVariables - numberOfObjectives + 1 + g);
    double subf1 = 1, subf3 = 1 + g;
    // evaluate fm,fm-1,...2,f1
    f[numberOfObjectives - 1] = Math.pow(Math.sin(Math.PI * x[0] / 2) * subf3, 2);
    // f=(subf1*subf2*subf3)^4
    for (var i = numberOfObjectives - 2; i > 0; i--) {
      subf1 *= Math.cos(Math.PI * x[numberOfObjectives - i - 2] / 2);
      f[i] = Math.pow(subf1 * Math.sin(Math.PI * x[numberOfObjectives - i - 1] / 2) * subf3, 4);
    }
    f[0] = Math.pow(subf1 * Math.cos(Math.PI * x[numberOfObjectives - 2] / 2) * subf3, 4);

    for (var i = 0; i < numberOfObjectives; i++) {
      solution.objectives()[i] = f[i];
    }

    return solution ;
  }
}
