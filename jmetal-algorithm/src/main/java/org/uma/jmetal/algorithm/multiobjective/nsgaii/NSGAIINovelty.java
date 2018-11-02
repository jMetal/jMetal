package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.distance.impl.DistanceBetweenSolutionAndKNearestNeighbors;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.Comparator;
import java.util.List;

/**
 * This class shows a version of NSGA-II having a stopping condition depending on run-time
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class NSGAIINovelty extends NSGAII<DoubleSolution> {
  private DistanceBetweenSolutionAndKNearestNeighbors<DoubleSolution, List<DoubleSolution>> distance ;

  /**
   * Constructor
   */
  public NSGAIINovelty(Problem<DoubleSolution> problem, int populationSize,
                       int maxEvaluations,
                       CrossoverOperator<DoubleSolution> crossoverOperator,
                       MutationOperator<DoubleSolution> mutationOperator,
                       SelectionOperator<List<DoubleSolution>, DoubleSolution> selectionOperator,
                       Comparator<DoubleSolution> dominanceComparator) {
    super(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator,
        selectionOperator, dominanceComparator, new SequentialSolutionListEvaluator<>());
    this.distance = new DistanceBetweenSolutionAndKNearestNeighbors<>(70) ;
  }

  @Override protected List<DoubleSolution> evaluatePopulation(List<DoubleSolution> population) {
    population = evaluator.evaluate(population, getProblem());
    for (DoubleSolution solution: population) {
      solution.setObjective(2, -1* distance.getDistance(solution, population));
    }

    return population;
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

  @Override public String getName() {
    return "NovNSGAII" ;
  }

  @Override public String getDescription() {
    return "NovNSGAII" ;
  }
}
