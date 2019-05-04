package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.IntegerDoubleProblem;
import org.uma.jmetal.solution.IntegerDoubleSolution;
import org.uma.jmetal.util.IndexBounder;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

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
    super(variablesInitializer(problem, JMetalRandom.getInstance()), problem.getNumberOfObjectives()) ;

    numberOfIntegerVariables = problem.getNumberOfIntegerVariables() ;
    numberOfDoubleVariables = problem.getNumberOfDoubleVariables() ;
    this.bounder = problem;
  }

  /** Copy constructor */
  public DefaultIntegerDoubleSolution(DefaultIntegerDoubleSolution solution) {
    super(solution) ;
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
  
  private static List<Number> variablesInitializer(IntegerDoubleProblem<?> problem, JMetalRandom randomGenerator) {
    int numberOfIntegerVariables = problem.getNumberOfIntegerVariables();
    int numberOfDoubleVariables = problem.getNumberOfDoubleVariables();
    List<Number> variables = new ArrayList<>(numberOfIntegerVariables+numberOfDoubleVariables);
    for (int i = 0; i < numberOfIntegerVariables; i++) {
      variables.add(randomGenerator.nextInt((Integer) problem.getLowerBound(i), (Integer) problem.getUpperBound(i)));
    }
    for (int i = 0; i < numberOfDoubleVariables; i++) {
      variables.add(randomGenerator.nextDouble((Double) problem.getLowerBound(i), (Double) problem.getUpperBound(i)));
    }
    return variables;
  }
}
