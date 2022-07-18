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
    int numberOfVariables = 3 ;
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);
    setName("Fonseca");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    @NotNull List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    for (int i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(-4.0);
      upperLimit.add(4.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }



  /** Evaluate() method */
  @Override
  public @NotNull DoubleSolution evaluate(DoubleSolution solution) {



    int numberOfVariables = getNumberOfVariables() ;

    double @NotNull [] f = new double[solution.objectives().length];
      double @NotNull [] x = new double[10];
      int count = 0;
      for (int i2 = 0; i2 < numberOfVariables; i2++) {
          double v1 = solution.variables().get(i2);
          if (x.length == count) x = Arrays.copyOf(x, count * 2);
          x[count++] = v1;
      }
      x = Arrays.copyOfRange(x, 0, count);

      double sum1 = 0.0;
      for (int i1 = 0; i1 < numberOfVariables; i1++) {
          double v = StrictMath.pow(x[i1] - (1.0 / StrictMath.sqrt(numberOfVariables)), 2.0);
          sum1 += v;
      }
      double exp1 = StrictMath.exp((-1.0) * sum1);
    f[0] = 1 - exp1;

      double sum2 = 0.0;
      for (int i = 0; i < numberOfVariables; i++) {
          double pow = StrictMath.pow(x[i] + (1.0 / StrictMath.sqrt(numberOfVariables)), 2.0);
          sum2 += pow;
      }
      double exp2 = StrictMath.exp((-1.0) * sum2);
    f[1] = 1 - exp2;

    solution.objectives()[0] = f[0];
    solution.objectives()[1] = f[1];

    return solution ;
  }
}
