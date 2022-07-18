package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class SteadyStateNSGAII<S extends Solution<?>> extends NSGAII<S> {
  /**
   * Constructor
   */
  public SteadyStateNSGAII(Problem<S> problem, int maxEvaluations, int populationSize,
                           CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
                           SelectionOperator<List<S>, S> selectionOperator, Comparator<S> dominanceComparator,
                           SolutionListEvaluator<S> evaluator) {
    super(problem, maxEvaluations, populationSize,100, 100, crossoverOperator, mutationOperator,
        selectionOperator, dominanceComparator, evaluator);
  }

  public SteadyStateNSGAII(Problem<S> problem, int maxEvaluations, int populationSize,
                           CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
                           SelectionOperator<List<S>, S> selectionOperator, Comparator<S> dominanceComparator) {
    super(problem, maxEvaluations, populationSize,100, 100, crossoverOperator, mutationOperator,
            selectionOperator, dominanceComparator, new SequentialSolutionListEvaluator<>());
  }

  @Override protected void updateProgress() {
    evaluations ++ ;
  }

  @Override protected List<S> selection(List<S> population) {
    List<S> matingPopulation = new ArrayList<>(2);

    matingPopulation.add(selectionOperator.execute(population));
    matingPopulation.add(selectionOperator.execute(population));

    return matingPopulation;
  }

  @Override protected List<S> reproduction(@NotNull List<S> population) {
    @NotNull List<S> offspringPopulation = new ArrayList<>(1);

    List<S> parents = new ArrayList<>(2);
    parents.add(population.get(0));
    parents.add(population.get(1));

    var offspring = crossoverOperator.execute(parents);

    mutationOperator.execute(offspring.get(0));

    offspringPopulation.add(offspring.get(0));
    return offspringPopulation;
  }

  @Override public String getName() {
    return "ssNSGAII" ;
  }

  @Override public String getDescription() {
    return "Nondominated Sorting Genetic Algorithm version II. Steady-state version" ;
  }
}
