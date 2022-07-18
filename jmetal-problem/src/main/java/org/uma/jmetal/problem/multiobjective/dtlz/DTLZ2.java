package org.uma.jmetal.problem.multiobjective.dtlz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class representing problem DTLZ2
 */
@SuppressWarnings("serial")
public class DTLZ2 extends AbstractDoubleProblem {
  /**
   * Creates a default DTLZ2 problem (12 variables and 3 objectives)
   */
  public DTLZ2() {
    this(12, 3);
  }

  /**
   * Creates a DTLZ2 problem instance
   *
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public DTLZ2(Integer numberOfVariables, Integer numberOfObjectives) throws JMetalException {
    setNumberOfObjectives(numberOfObjectives);
    setName("DTLZ2");

    @NotNull List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
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
    var x = new double[10];
    var count1 = 0;
    for (var i3 = 0; i3 < numberOfVariables; i3++) {
      double v2 = solution.variables().get(i3);
      if (x.length == count1) x = Arrays.copyOf(x, count1 * 2);
      x[count1++] = v2;
    }
    x = Arrays.copyOfRange(x, 0, count1);

    var k = getNumberOfVariables() - solution.objectives().length + 1;

    var g = 0.0;
    for (var i2 = numberOfVariables - k; i2 < numberOfVariables; i2++) {
      var v1 = (x[i2] - 0.5) * (x[i2] - 0.5);
      g += v1;
    }

    var arr = new double[10];
    var count = 0;
    for (var i1 = 0; i1 < numberOfObjectives; i1++) {
      var v = 1.0 + g;
      if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
      arr[count++] = v;
    }
    arr = Arrays.copyOfRange(arr, 0, count);
    var f = arr;

    for (var i = 0; i < numberOfObjectives; i++) {
      for (var j = 0; j < numberOfObjectives - (i + 1); j++) {
        f[i] *= Math.cos(x[j] * 0.5 * Math.PI);
      }
      if (i != 0) {
        var aux = numberOfObjectives - (i + 1);
        f[i] *= Math.sin(x[aux] * 0.5 * Math.PI);
      }
    }

    for (var i = 0; i < numberOfObjectives; i++) {
      solution.objectives()[i] = f[i];
    }

    return solution ;
  }
}
