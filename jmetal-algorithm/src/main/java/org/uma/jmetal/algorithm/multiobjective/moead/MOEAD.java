package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by ajnebro on 26/12/14.
 */
public class MOEAD extends AbstractMOEAD<DoubleSolution> {
  private DifferentialEvolutionCrossover differentialEvolutionCrossover ;
  public MOEAD(Problem problem,
      int populationSize,
      int resultPopulationSize,
      int maxEvaluations,
      MutationOperator mutation,
      FunctionType functionType,
      String dataDirectory,
      double neighborhoodSelectionProbability,
      int maximumNumberOfReplacedSolutions,
      int neighborSize) {
    super(problem, populationSize, resultPopulationSize, maxEvaluations, mutation, functionType,
        dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions,
        neighborSize);

    crossoverOperator = new DifferentialEvolutionCrossover() ;
    differentialEvolutionCrossover = (DifferentialEvolutionCrossover)crossoverOperator ;
  }

  @Override public void run() {
    initializePopulation() ;
    initializeUniformWeight();
    initializeNeighborhood();
    initializeIdealPoint() ;

    evaluations = populationSize ;
    do {
      int[] permutation = new int[populationSize];
      Utils.randomPermutation(permutation, populationSize);

      for (int i = 0; i < populationSize; i++) {
        int subProblemId = permutation[i];

        NeighborType neighborType = chooseNeighborType() ;
        List<DoubleSolution> parents = parentSelection(subProblemId, neighborType) ;

        differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
        List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);

        DoubleSolution child = children.get(0) ;
        mutationOperator.execute(child);
        problem.evaluate(child);

        evaluations++;

        updateIdealPoint(child);
        updateNeighborhood(child, subProblemId, neighborType);
      }
    } while (evaluations < maxEvaluations);

  }

  protected void initializePopulation() {
    for (int i = 0; i < populationSize; i++) {
      DoubleSolution newSolution = (DoubleSolution)problem.createSolution();

      problem.evaluate(newSolution);
      population.add(newSolution);
    }
  }

  @Override public List<Solution> getResult() {
    return null;
  }
}
