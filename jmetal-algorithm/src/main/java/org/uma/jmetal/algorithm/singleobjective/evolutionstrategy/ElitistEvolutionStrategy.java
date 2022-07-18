package org.uma.jmetal.algorithm.singleobjective.evolutionstrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.impl.AbstractEvolutionStrategy;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

/**
 * Class implementing a (mu + lambda) Evolution Strategy (lambda must be divisible by mu)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class ElitistEvolutionStrategy<S extends Solution<?>> extends AbstractEvolutionStrategy<S, S> {
  private int mu;
  private int lambda;
  private int maxEvaluations;
  private int evaluations;
  private MutationOperator<S> mutation;

  private Comparator<S> comparator;

  /**
   * Constructor
   */
  public ElitistEvolutionStrategy(Problem<S> problem, int mu, int lambda, int maxEvaluations,
      MutationOperator<S> mutation) {
    super(problem) ;
    this.mu = mu;
    this.lambda = lambda;
    this.maxEvaluations = maxEvaluations;
    this.mutation = mutation;

    comparator = new ObjectiveComparator<S>(0);
  }

  @Override protected void initProgress() {
    evaluations = mu;
  }

  @Override protected void updateProgress() {
    evaluations += lambda;
  }

  @Override protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations;
  }

  @Override protected List<S> createInitialPopulation() {
    List<S> population = new ArrayList<>(mu);
    var bound = mu;
    for (var i = 0; i < bound; i++) {
      var solution = getProblem().createSolution();
      population.add(solution);
    }

    return population;
  }

  @Override protected List<S> evaluatePopulation(List<S> population) {
    for (var solution : population) {
      getProblem().evaluate(solution);
    }

    return population;
  }

  @Override protected List<S> selection(List<S> population) {
    return population;
  }

  @SuppressWarnings("unchecked")
  @Override protected List<S> reproduction(List<S> population) {
    List<S> offspringPopulation = new ArrayList<>(lambda + mu);
    for (var i = 0; i < mu; i++) {
      for (var j = 0; j < lambda / mu; j++) {
        var offspring = (S)population.get(i).copy();
        mutation.execute(offspring);
        offspringPopulation.add(offspring);
      }
    }

    return offspringPopulation;
  }

  @Override protected @NotNull List<S> replacement(List<S> population,
                                                   List<S> offspringPopulation) {
    for (var i = 0; i < mu; i++) {
      offspringPopulation.add(population.get(i));
    }

    Collections.sort(offspringPopulation, comparator) ;

    List<S> newPopulation = new ArrayList<>(mu);
    var bound = mu;
    for (var i = 0; i < bound; i++) {
      var s = offspringPopulation.get(i);
      newPopulation.add(s);
    }
    return newPopulation;
  }

  @Override public S getResult() {
    return getPopulation().get(0);
  }

  @Override public String getName() {
    return "ElitistEA" ;
  }

  @Override public String getDescription() {
    return "Elitist Evolution Strategy Algorithm, i.e, (mu + lambda) EA" ;
  }
}
