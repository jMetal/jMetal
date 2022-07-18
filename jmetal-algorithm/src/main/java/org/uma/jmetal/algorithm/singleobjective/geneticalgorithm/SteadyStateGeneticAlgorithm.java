package org.uma.jmetal.algorithm.singleobjective.geneticalgorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class SteadyStateGeneticAlgorithm<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, S> {
  private Comparator<S> comparator;
  private int maxEvaluations;
  private int evaluations;

  /**
   * Constructor
   */
  public SteadyStateGeneticAlgorithm(Problem<S> problem, int maxEvaluations, int populationSize,
                                     CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
                                     SelectionOperator<List<S>, S> selectionOperator) {
    super(problem);
    setMaxPopulationSize(populationSize);
    this.maxEvaluations = maxEvaluations;

    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = selectionOperator;

    comparator = new ObjectiveComparator<S>(0);
  }

  @Override protected boolean isStoppingConditionReached() {
    return (evaluations >= maxEvaluations);
  }

  @Override protected List<S> replacement(@NotNull List<S> population, List<S> offspringPopulation) {
    population.sort(comparator);
    var worstSolutionIndex = population.size() - 1;
    if (comparator.compare(population.get(worstSolutionIndex), offspringPopulation.get(0)) > 0) {
      population.remove(worstSolutionIndex);
      population.add(offspringPopulation.get(0));
    }

    return population;
  }

  @Override protected List<S> reproduction(List<S> matingPopulation) {
    @NotNull List<S> offspringPopulation = new ArrayList<>(1);

    List<S> parents = new ArrayList<>(2);
    parents.add(matingPopulation.get(0));
    parents.add(matingPopulation.get(1));

    var offspring = crossoverOperator.execute(parents);
    mutationOperator.execute(offspring.get(0));

    offspringPopulation.add(offspring.get(0));
    return offspringPopulation;
  }

  @Override protected List<S> selection(List<S> population) {
      List<S> matingPopulation = new ArrayList<>(2);
      for (var i = 0; i < 2; i++) {
        var execute = selectionOperator.execute(population);
          matingPopulation.add(execute);
      }

      return matingPopulation;
  }

  @Override protected List<S> evaluatePopulation(List<S> population) {
    for (var solution : population) {
      getProblem().evaluate(solution);
    }

    return population;
  }

  @Override public S getResult() {
    getPopulation().sort(comparator);
    return getPopulation().get(0);
  }

  @Override public void initProgress() {
    evaluations = getMaxPopulationSize();
  }

  @Override public void updateProgress() {
    evaluations++;
  }

  @Override public String getName() {
    return "ssGA" ;
  }

  @Override public String getDescription() {
    return "Steady-State Genetic Algorithm" ;
  }
}
