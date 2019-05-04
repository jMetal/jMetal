package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.IntegerDoubleProblem;
import org.uma.jmetal.solution.IntegerDoubleSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.function.Function;

/**
 * Defines an implementation of a class for solutions having integers and doubles
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultIntegerDoubleSolution
    extends AbstractGenericSolution<Number, IntegerDoubleProblem<?>>
    implements IntegerDoubleSolution {

  private int numberOfIntegerVariables ;
  private int numberOfDoubleVariables ;

  /** Constructor */
  public DefaultIntegerDoubleSolution(IntegerDoubleProblem<?> problem) {
    super(problem, variableInitializer(problem)) ;

    numberOfIntegerVariables = problem.getNumberOfIntegerVariables() ;
    numberOfDoubleVariables = problem.getNumberOfDoubleVariables() ;
  }

  /** Copy constructor */
  public DefaultIntegerDoubleSolution(DefaultIntegerDoubleSolution solution) {
    super(solution.problem, solution) ;
  }

  @Override
  public Number getUpperBound(int index) {
    return problem.getUpperBound(index);
  }

  @Override
  public int getNumberOfIntegerVariables() {
    return numberOfIntegerVariables;
  }

  @Override
  public int getNumberOfDoubleVariables() {
    return numberOfDoubleVariables;
  }

  @Override
  public Number getLowerBound(int index) {
    return problem.getLowerBound(index) ;
  }

  @Override
  public DefaultIntegerDoubleSolution copy() {
    return new DefaultIntegerDoubleSolution(this);
  }

  @Override
  public String getVariableValueString(int index) {
    return getVariableValue(index).toString() ;
  }
  
  private static Function<Integer, Number> variableInitializer(IntegerDoubleProblem<?> problem) {
    return i -> {
      Number lowerBound = problem.getLowerBound(i);
      Number upperBound = problem.getUpperBound(i);
      JMetalRandom randomGenerator = JMetalRandom.getInstance();
      if (i < problem.getNumberOfIntegerVariables()) {
        return randomGenerator.nextInt((Integer) lowerBound, (Integer) upperBound);
      } else {
        return randomGenerator.nextDouble((Double) lowerBound, (Double) upperBound);
      }
    };
  }
}
