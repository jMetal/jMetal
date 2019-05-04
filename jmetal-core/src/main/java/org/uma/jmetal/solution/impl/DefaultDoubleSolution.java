package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.IndexBounder;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Defines an implementation of a double solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultDoubleSolution 
    extends AbstractGenericSolution<Double, DoubleProblem>
    implements DoubleSolution {

  private final IndexBounder<Double> bounder;

  /** Constructor */
  public DefaultDoubleSolution(DoubleProblem problem) {
    super(problem) ;
    this.bounder = problem;

    initializeDoubleVariables(JMetalRandom.getInstance());
  }

  /** Copy constructor */
  public DefaultDoubleSolution(DefaultDoubleSolution solution) {
    super(solution.problem, solution) ;
    this.bounder = solution.bounder;
  }

  @Override
  public Double getUpperBound(int index) {
    return bounder.getUpperBound(index);
  }

  @Override
  public Double getLowerBound(int index) {
    return bounder.getLowerBound(index) ;
  }

  @Override
  public DefaultDoubleSolution copy() {
    return new DefaultDoubleSolution(this);
  }

  @Override
  public String getVariableValueString(int index) {
    return getVariableValue(index).toString() ;
  }
  
  private void initializeDoubleVariables(JMetalRandom randomGenerator) {
    for (int i = 0 ; i < problem.getNumberOfVariables(); i++) {
      Double value = randomGenerator.nextDouble(getLowerBound(i), getUpperBound(i)) ;
      setVariableValue(i, value) ;
    }
  }
}
