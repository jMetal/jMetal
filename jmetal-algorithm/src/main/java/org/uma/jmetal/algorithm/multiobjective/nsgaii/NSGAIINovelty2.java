package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.RankingAndCrowdingSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.distance.impl.DistanceBetweenSolutionAndKNearestNeighbors;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class NSGAIINovelty2<S extends Solution<?>> extends AbstractGeneticAlgorithm<DoubleSolution, List<DoubleSolution>> {
  protected final int maxEvaluations;
  private DistanceBetweenSolutionAndKNearestNeighbors<DoubleSolution, List<DoubleSolution>> distance ;

  protected final SolutionListEvaluator<DoubleSolution> evaluator;

  protected int evaluations;
  protected Comparator<DoubleSolution> dominanceComparator ;

  /**
   * Constructor
   */
  public NSGAIINovelty2(Problem<DoubleSolution> problem, int maxEvaluations, int populationSize,
                        CrossoverOperator<DoubleSolution> crossoverOperator, MutationOperator<DoubleSolution> mutationOperator,
                        SelectionOperator<List<DoubleSolution>, DoubleSolution> selectionOperator, SolutionListEvaluator<DoubleSolution> evaluator) {
    this(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, selectionOperator,
        new DominanceComparator<DoubleSolution>(), evaluator);
  }
  /**
   * Constructor
   */
  public NSGAIINovelty2(Problem<DoubleSolution> problem, int maxEvaluations, int populationSize,
                        CrossoverOperator<DoubleSolution> crossoverOperator, MutationOperator<DoubleSolution> mutationOperator,
                        SelectionOperator<List<DoubleSolution>, DoubleSolution> selectionOperator,
                        Comparator<DoubleSolution> dominanceComparator,
                        SolutionListEvaluator<DoubleSolution> evaluator) {
    super(problem);
    this.maxEvaluations = maxEvaluations;
    setMaxPopulationSize(populationSize); ;

    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = selectionOperator;

    this.evaluator = evaluator;
    this.dominanceComparator = dominanceComparator ;

    this.distance = new DistanceBetweenSolutionAndKNearestNeighbors<>(20) ;
  }

  @Override protected void initProgress() {
    evaluations = getMaxPopulationSize();
  }

  @Override protected void updateProgress() {
    evaluations += getMaxPopulationSize() ;
  }

  @Override protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations;
  }

  @Override protected List<DoubleSolution> evaluatePopulation(List<DoubleSolution> population) {
    population = evaluator.evaluate(population, getProblem());
    for (DoubleSolution solution: population) {
      solution.setObjective(2, -1* distance.getDistance(solution, population));
    }

    return population;
  }

  @Override protected List<DoubleSolution> replacement(List<DoubleSolution> population, List<DoubleSolution> offspringPopulation) {
    List<DoubleSolution> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population);
    jointPopulation.addAll(offspringPopulation);

    RankingAndCrowdingSelection<DoubleSolution> rankingAndCrowdingSelection ;
    rankingAndCrowdingSelection = new RankingAndCrowdingSelection<DoubleSolution>(getMaxPopulationSize(), dominanceComparator) ;

    return rankingAndCrowdingSelection.execute(jointPopulation) ;
  }

  @Override public List<DoubleSolution> getResult() {
    return getNonDominatedSolutions(getPopulation());
  }

  protected List<DoubleSolution> getNonDominatedSolutions(List<DoubleSolution> solutionList) {
    return SolutionListUtils.getNondominatedSolutions(solutionList);
  }

  @Override public String getName() {
    return "NSGAII" ;
  }

  @Override public String getDescription() {
    return "Nondominated Sorting Genetic Algorithm version II" ;
  }
}
