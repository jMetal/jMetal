package org.uma.jmetal.problem.multiobjective.dtlz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/** Class representing problem DTLZ7 */
@SuppressWarnings("serial")
public class DTLZ7 extends AbstractDoubleProblem {
  /** Creates a default DTLZ7 problem (22 variables and 3 objectives) */
  public DTLZ7() {
    this(22, 3);
  }

  /**
   * Creates a DTLZ7 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public DTLZ7(Integer numberOfVariables, Integer numberOfObjectives) throws JMetalException {
    setNumberOfObjectives(numberOfObjectives);
    setName("DTLZ7");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    @NotNull List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    for (var i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  public DoubleSolution evaluate(@NotNull DoubleSolution solution) {
    var numberOfVariables = getNumberOfVariables();
    var numberOfObjectives = solution.objectives().length;

    var f = new double[numberOfObjectives];

    var k = getNumberOfVariables() - solution.objectives().length + 1;

    var arr = new double[10];
    var count = 0;
    for (var i1 = 0; i1 < numberOfVariables; i1++) {
      double v1 = solution.variables().get(i1);
      if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
      arr[count++] = v1;
    }
    arr = Arrays.copyOfRange(arr, 0, count);
    var x = arr;

    var g = 0.0;
    for (var j = numberOfVariables - k; j < numberOfVariables; j++) {
      var v = x[j];
      g += v;
    }

    g = 1 + (9.0 * g) / k;

    System.arraycopy(x, 0, f, 0, numberOfObjectives - 1);

    var h = 0.0;
    for (var i = 0; i < numberOfObjectives - 1; i++) {
      h += (f[i] / (1.0 + g)) * (1 + Math.sin(3.0 * Math.PI * f[i]));
    }

    h = numberOfObjectives - h;

    f[numberOfObjectives - 1] = (1 + g) * h;

    for (var i = 0; i < numberOfObjectives; i++) {
      solution.objectives()[i] = f[i];
    }

    return solution;
  }
}
