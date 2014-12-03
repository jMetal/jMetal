package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajnebro on 30/10/14.
 */
public class SteadyStateNSGAII extends NSGAII {
  /**
   * Constructor
   */
  public SteadyStateNSGAII(Problem problem, int maxIterations, int populationSize,
      CrossoverOperator crossoverOperator, MutationOperator mutationOperator,
      SelectionOperator selectionOperator, SolutionListEvaluator evaluator) {
    super(problem, maxIterations, populationSize, crossoverOperator, mutationOperator,
        selectionOperator, evaluator);
  }

  @Override protected List<Solution> selection(List<Solution> population) {
    List<Solution> matingPopulation = new ArrayList<>(2);

    matingPopulation.add(selectionOperator.execute(population));
    matingPopulation.add(selectionOperator.execute(population));

    return matingPopulation;
  }

  @Override protected List<Solution> reproduction(List<Solution> population) {
    List<Solution> offspringPopulation = new ArrayList<>(1);

    List<Solution> parents = new ArrayList<>(2);
    parents.add(population.get(0));
    parents.add(population.get(1));

    List<Solution> offspring = crossoverOperator.execute(parents);

    mutationOperator.execute(offspring.get(0));

    offspringPopulation.add(offspring.get(0));
    return offspringPopulation;
  }
}
