package org.uma.jmetal.algorithm.singleobjective.geneticalgorithm;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class GenerationalGeneticAlgorithm<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, S> {
  private Comparator<S> comparator;
  private int maxEvaluations;
  private int evaluations;

  private SolutionListEvaluator<S> evaluator;

  /**
   * Constructor
   */
  public GenerationalGeneticAlgorithm(Problem<S> problem, int maxEvaluations, int populationSize,
      CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator) {
    super(problem);
    this.maxEvaluations = maxEvaluations;
    this.setMaxPopulationSize(populationSize);

    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = selectionOperator;

    this.evaluator = evaluator;

    comparator = new ObjectiveComparator<S>(0);
  }

  @Override protected boolean isStoppingConditionReached() {
    return (evaluations >= maxEvaluations);
  }

  @Override protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
    Collections.sort(population, comparator);
    offspringPopulation.add(population.get(0));
    offspringPopulation.add(population.get(1));
    Collections.sort(offspringPopulation, comparator) ;
    offspringPopulation.remove(offspringPopulation.size() - 1);
    offspringPopulation.remove(offspringPopulation.size() - 1);

    return offspringPopulation;
  }

  @Override protected List<S> evaluatePopulation(List<S> population) {
    population = evaluator.evaluate(population, getProblem());

    return population;
  }

  @Override public S getResult() {
    Collections.sort(getPopulation(), comparator) ;
    return getPopulation().get(0);
  }

  @Override public void initProgress() {
    evaluations = getMaxPopulationSize();
  }

  @Override public void updateProgress() {
    evaluations += getMaxPopulationSize();
  }

  @Override public String getName() {
    return "gGA" ;
  }

  @Override public String getDescription() {
    return "Generational Genetic Algorithm" ;
  }
}
