package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.IntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.IndexBounder;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Defines an implementation of an integer solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultIntegerSolution
    extends AbstractGenericSolution<Integer, IntegerProblem>
    implements IntegerSolution {

  private final IndexBounder<Integer> bounder;

  /** Constructor */
  public DefaultIntegerSolution(IntegerProblem problem) {
    super(problem) ;
    this.bounder = problem;

    initializeIntegerVariables(problem.getNumberOfVariables(), JMetalRandom.getInstance());
  }

  /** Copy constructor */
  public DefaultIntegerSolution(DefaultIntegerSolution solution) {
    super(solution.problem, solution) ;
    this.bounder = solution.bounder;
  }

  @Override
  public Integer getUpperBound(int index) {
    return bounder.getUpperBound(index);
  }

  @Override
  public Integer getLowerBound(int index) {
    return bounder.getLowerBound(index) ;
  }

  @Override
  public DefaultIntegerSolution copy() {
    return new DefaultIntegerSolution(this);
  }

  @Override
  public String getVariableValueString(int index) {
    return getVariableValue(index).toString() ;
  }
  
  private void initializeIntegerVariables(int numberOfVariables, JMetalRandom randomGenerator) {
    for (int i = 0 ; i < numberOfVariables; i++) {
      Integer value = randomGenerator.nextInt(getLowerBound(i), getUpperBound(i));
      setVariableValue(i, value) ;
    }
  }
}
