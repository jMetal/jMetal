package org.uma.jmetal.algorithm.singleobjective.evolutionstrategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.uma.jmetal.algorithm.impl.AbstractEvolutionStrategy;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

/**
 * Class implementing a (mu , lambda) Evolution Strategy (lambda must be divisible by mu)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class NonElitistEvolutionStrategy<S extends Solution<?>> extends AbstractEvolutionStrategy<S, S> {
  private int mu;
  private int lambda;
  private int maxEvaluations;
  private int evaluations;
  private MutationOperator<S> mutation;

  private Comparator<S> comparator;

  /**
   * Constructor
   */
  public NonElitistEvolutionStrategy(Problem<S> problem, int mu, int lambda, int maxEvaluations,
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
    List<S> population = IntStream.range(0, mu).mapToObj(i -> getProblem().createSolution()).collect(Collectors.toCollection(() -> new ArrayList<>(mu)));

      return population;
  }

  @Override protected List<S> evaluatePopulation(List<S> population) {
    for (S solution : population) {
      getProblem().evaluate(solution);
    }

    return population;
  }

  @Override protected List<S> selection(List<S> population) {
    return population;
    //    List<Solution> matingPopulation = new ArrayList<>(mu) ;
    //    for (Solution solution: population) {
    //      matingPopulation.add(solution.copy()) ;
    //    }
    //    return matingPopulation ;
  }

  @SuppressWarnings("unchecked")
  @Override protected List<S> reproduction(List<S> population) {
    List<S> offspringPopulation = new ArrayList<>(lambda);
    for (int i = 0; i < mu; i++) {
      for (int j = 0; j < lambda / mu; j++) {
        S offspring = (S)population.get(i).copy();
        mutation.execute(offspring);
        offspringPopulation.add(offspring);
      }
    }

    return offspringPopulation;
  }

  @Override protected List<S> replacement(List<S> population,
      List<S> offspringPopulation) {
    offspringPopulation.sort(comparator);

    List<S> newPopulation = IntStream.range(0, mu).mapToObj(offspringPopulation::get).collect(Collectors.toCollection(() -> new ArrayList<>(mu)));
      return newPopulation;
  }

  @Override public S getResult() {
    return getPopulation().get(0);
  }

  @Override public String getName() {
    return "NonElitistEA" ;
  }

  @Override public String getDescription() {
    return "Non Elitist Evolution Strategy Algorithm, i.e, (mu , lambda) EA" ;
  }
}
