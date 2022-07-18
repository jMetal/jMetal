package org.uma.jmetal.problem.multiobjective;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/** Class representing problem Fonseca */
@SuppressWarnings("serial")
public class Fonseca extends AbstractDoubleProblem {

  /** Constructor */
  public Fonseca()  {
    var numberOfVariables = 3 ;
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);
    setName("Fonseca");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    @NotNull List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    for (var i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(-4.0);
      upperLimit.add(4.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }



  /** Evaluate() method */
  @Override
  public @NotNull DoubleSolution evaluate(DoubleSolution solution) {


    var numberOfVariables = getNumberOfVariables() ;

    var f = new double[solution.objectives().length];
    var x = new double[10];
    var count = 0;
      for (var i2 = 0; i2 < numberOfVariables; i2++) {
          double v1 = solution.variables().get(i2);
          if (x.length == count) x = Arrays.copyOf(x, count * 2);
          x[count++] = v1;
      }
      x = Arrays.copyOfRange(x, 0, count);

    var sum1 = 0.0;
      for (var i1 = 0; i1 < numberOfVariables; i1++) {
        var v = StrictMath.pow(x[i1] - (1.0 / StrictMath.sqrt(numberOfVariables)), 2.0);
          sum1 += v;
      }
    var exp1 = StrictMath.exp((-1.0) * sum1);
    f[0] = 1 - exp1;

    var sum2 = 0.0;
      for (var i = 0; i < numberOfVariables; i++) {
        var pow = StrictMath.pow(x[i] + (1.0 / StrictMath.sqrt(numberOfVariables)), 2.0);
          sum2 += pow;
      }
    var exp2 = StrictMath.exp((-1.0) * sum2);
    f[1] = 1 - exp2;

    solution.objectives()[0] = f[0];
    solution.objectives()[1] = f[1];

    return solution ;
  }
}
