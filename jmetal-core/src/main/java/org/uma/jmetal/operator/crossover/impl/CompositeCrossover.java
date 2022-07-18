package org.uma.jmetal.operator.crossover.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class allows to apply a list of crossover operator on the solutions belonging to a list of
 * {@link CompositeSolution} objects. It is required that the operators be compatible with the
 * solutions inside the composite solutions.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class CompositeCrossover implements CrossoverOperator<CompositeSolution> {
  private List<CrossoverOperator<Solution<?>>> operators;
  private double crossoverProbability = 1.0;

  public CompositeCrossover(List<?> operators) {
    Check.notNull(operators);
    Check.collectionIsNotEmpty(operators);

    this.operators = new ArrayList<>();
    for (Object operator : operators) {
      Check.that(
              operator instanceof CrossoverOperator,
              "The operator list does not contain an object implementing class CrossoverOperator");
      this.operators.add((CrossoverOperator<Solution<?>>) operator);
    }
  }

  /* Getters */
  @Override
  public double getCrossoverProbability() {
    return crossoverProbability;
  }

  /** Execute() method */
  @Override
  public List<CompositeSolution> execute(List<CompositeSolution> solutions) {
    Check.notNull(solutions);
    Check.that(solutions.size() == 2, "The number of parents is not two: " + solutions.size());

    @NotNull List<Solution<?>> offspring1 = new ArrayList<>();
    @NotNull List<Solution<?>> offspring2 = new ArrayList<>();
    var numberOfSolutionsInCompositeSolution = solutions.get(0).variables().size();
    for (var i = 0; i < numberOfSolutionsInCompositeSolution; i++) {
      var parents =
          Arrays.asList(solutions.get(0).variables().get(i), solutions.get(1).variables().get(i));
      var children = operators.get(i).execute(parents);
      offspring1.add(children.get(0));
      offspring2.add(children.get(1));
    }

    List<CompositeSolution> result = new ArrayList<>();
    result.add(new CompositeSolution(offspring1));
    result.add(new CompositeSolution(offspring2));
    return result;
  }

  public int getNumberOfRequiredParents() {
    return 2;
  }

  public int getNumberOfGeneratedChildren() {
    return 2;
  }

  public List<CrossoverOperator<Solution<?>>> getOperators() {
    return operators ;
  }
}
