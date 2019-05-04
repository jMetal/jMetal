package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.IntegerDoubleProblem;
import org.uma.jmetal.solution.IntegerDoubleSolution;
import org.uma.jmetal.util.IndexBounder;
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
  private final IndexBounder<Number> bounder;

  /** Constructor */
  public DefaultIntegerDoubleSolution(IntegerDoubleProblem<?> problem) {
    super(problem, variableInitializer(problem)) ;

    numberOfIntegerVariables = problem.getNumberOfIntegerVariables() ;
    numberOfDoubleVariables = problem.getNumberOfDoubleVariables() ;
    this.bounder = problem;
  }

  /** Copy constructor */
  public DefaultIntegerDoubleSolution(DefaultIntegerDoubleSolution solution) {
    super(solution.problem, solution) ;
    this.bounder = solution.bounder;
  }

  @Override
  public Number getUpperBound(int index) {
    return bounder.getUpperBound(index);
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
    return bounder.getLowerBound(index) ;
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
