package org.uma.jmetal.problem.singleobjective;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.problem.singleobjective.cec2005competitioncode.Benchmark;
import org.uma.jmetal.problem.singleobjective.cec2005competitioncode.TestFunc;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing for solving the CEC2005 competition problems.
 */
@SuppressWarnings("serial")
public class CEC2005Problem extends AbstractDoubleProblem {
  TestFunc testFunction;

  /** Constructor */
  public CEC2005Problem(int problemID, int numberOfVariables) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(1);
    setNumberOfConstraints(0) ;
    setName("CEC2005");

    Benchmark cec2005ProblemFactory = new Benchmark();
    testFunction = cec2005ProblemFactory.testFunctionFactory(problemID, numberOfVariables);

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

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

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution) {
    int numberOfVariables = getNumberOfVariables() ;

    double[] x = new double[numberOfVariables] ;

    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = solution.getVariableValue(i) ;
    }
    double result;
    result = testFunction.f(x);

    solution.setObjective(0, result);
  }
}

