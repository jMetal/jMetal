package org.uma.jmetal.problem.singleobjective;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.problem.singleobjective.cec2005competitioncode.Benchmark;
import org.uma.jmetal.problem.singleobjective.cec2005competitioncode.TestFunc;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class representing for solving the CEC2005 competition problems.
 */
@SuppressWarnings("serial")
public class CEC2005Problem extends AbstractDoubleProblem {
  TestFunc testFunction;

  /** Constructor */
  public CEC2005Problem(int problemID, int numberOfVariables) {
    setNumberOfObjectives(1);
    setNumberOfConstraints(0) ;
    setName("CEC2005");

    Benchmark cec2005ProblemFactory = new Benchmark();
    testFunction = cec2005ProblemFactory.testFunctionFactory(problemID, numberOfVariables);

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    double ulimit = 0;
    double llimit = 0;

    switch (problemID) {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 14:
        ulimit = 100;
        llimit = -100;
        break;
      case 7:
      case 25:
        ulimit = Double.MAX_VALUE;
        llimit = Double.MIN_VALUE;
        break;
      case 8:
        ulimit = 32;
        llimit = -32;
        break;
      case 9:
      case 10:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      case 20:
      case 21:
      case 22:
      case 23:
      case 24:
        ulimit = 5;
        llimit = -5;
        break;
      case 11:
        ulimit = 0.5;
        llimit = -0.5;
        break;
      case 12:
        ulimit = Math.PI;
        llimit = -Math.PI;
        break;
      case 13:
        ulimit = 3;
        llimit = 1;
        break;
      default:
        throw new JMetalException("Invalid problem value");
    }

    for (int i = 0; i < this.getNumberOfVariables(); i++) {
      lowerLimit.add(llimit);
      upperLimit.add(ulimit);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(@NotNull DoubleSolution solution) {
    int numberOfVariables = getNumberOfVariables() ;

    double @NotNull [] x = new double[10];
    int count = 0;
    for (int i = 0; i < numberOfVariables; i++) {
      double v = solution.variables().get(i);
      if (x.length == count) x = Arrays.copyOf(x, count * 2);
      x[count++] = v;
    }
    x = Arrays.copyOfRange(x, 0, count);

    double result;
    result = testFunction.f(x);

    solution.objectives()[0] = result;

    return solution ;
  }
}

