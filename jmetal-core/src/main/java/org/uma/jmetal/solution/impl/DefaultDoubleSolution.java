package org.uma.jmetal.solution.impl;

import java.util.ArrayList;
import java.util.List;

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
    super(variablesInitializer(problem, JMetalRandom.getInstance()), problem.getNumberOfObjectives()) ;
    this.bounder = problem;
  }

  /** Copy constructor */
  public DefaultDoubleSolution(DefaultDoubleSolution solution) {
    super(solution) ;
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
  
  private static List<Double> variablesInitializer(DoubleProblem problem, JMetalRandom randomGenerator) {
    int numberOfVariables = problem.getNumberOfVariables();
    List<Double> variables = new ArrayList<>(numberOfVariables);
    for (int i = 0; i < numberOfVariables; i++) {
      variables.add(randomGenerator.nextDouble(problem.getLowerBound(i), problem.getUpperBound(i)));
    }
    return variables;
  }
}
