package org.uma.jmetal.problem.multiobjective.dtlz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class representing problem DTLZ4
 */
@SuppressWarnings("serial")
public class DTLZ4 extends AbstractDoubleProblem {
  /**
   * Creates a default DTLZ4 problem (12 variables and 3 objectives)
   */
  public DTLZ4() {
    this(12, 3);
  }

  /**
   * Creates a DTLZ4 problem instance
   *
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public DTLZ4(Integer numberOfVariables, Integer numberOfObjectives) throws JMetalException {
    setNumberOfObjectives(numberOfObjectives);
    setName("DTLZ4");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;


    for (var i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  public DoubleSolution evaluate(DoubleSolution solution) {
    var numberOfVariables = getNumberOfVariables();
    var numberOfObjectives = solution.objectives().length ;
    var alpha = 100.0;

    var k = getNumberOfVariables() - solution.objectives().length + 1;

    var result = new double[10];
    var count1 = 0;
    for (var i3 = 0; i3 < numberOfVariables; i3++) {
      double v2 = solution.variables().get(i3);
      if (result.length == count1) result = Arrays.copyOf(result, count1 * 2);
      result[count1++] = v2;
    }
    result = Arrays.copyOfRange(result, 0, count1);
    var x = result;

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
        f[i] *= java.lang.Math.cos(java.lang.Math.pow(x[j], alpha) * (java.lang.Math.PI / 2.0));
      }
      if (i != 0) {
        var aux = numberOfObjectives - (i + 1);
        f[i] *= java.lang.Math.sin(java.lang.Math.pow(x[aux], alpha) * (java.lang.Math.PI / 2.0));
      }
    }

    for (var i = 0; i < numberOfObjectives; i++) {
      solution.objectives()[i] = f[i];
    }

    return solution ;
  }
}
