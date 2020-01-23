package org.uma.jmetal.operator.crossover.impl;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class allows to apply a SBX crossover operator using two parent solutions (Integer encoding)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class CompositeCrossoverOperator implements CrossoverOperator<CompositeSolution> {
  private List<CrossoverOperator<Solution<?>>> operators ;
  private double crossoverProbability = 1.0 ;

  /** Constructor */
  public CompositeCrossoverOperator(List<CrossoverOperator<Solution<?>>> operators) {
    Check.isNotNull(operators);
    Check.collectionIsNotEmpty(operators);

	  this.operators = operators ;
  }

  /* Getters */
  @Override
  public double getCrossoverProbability() {
    return crossoverProbability;
  }

  /* Setters */
  public void setCrossoverProbability(double crossoverProbability) {
    this.crossoverProbability = crossoverProbability;
  }

  /** Execute() method */
  @Override
  public List<CompositeSolution> execute(List<CompositeSolution> solutions) {
    Check.isNotNull(solutions);
    Check.that(solutions.size() == 2, "There must be two parents instead of " + solutions.size());

    List<List<Solution<?>>> offspring = new ArrayList<>() ;
    List<Solution<?>> offspring1= new ArrayList<>() ;
    List<Solution<?>> offspring2= new ArrayList<>() ;
    int numberOfSolutionsInCompositeSolution = solutions.get(0).getNumberOfVariables() ;
    for (int i = 0; i < numberOfSolutionsInCompositeSolution; i++) {
      List<Solution<?>> parents = Arrays.asList(solutions.get(0).getVariable(i), solutions.get(1).getVariable(i)) ;
      List<Solution<?>> children = operators.get(0).execute(parents);
      offspring1.add(children.get(0)) ;
      offspring2.add(children.get(1)) ;
    }

    List<CompositeSolution> result = new ArrayList<>() ;
    result.add(new CompositeSolution(offspring1)) ;
    result.add(new CompositeSolution(offspring2)) ;
    return result ;
  }

  public int getNumberOfRequiredParents() {
    return 2 ;
  }

  public int getNumberOfGeneratedChildren() {
    return 2 ;
  }
}
