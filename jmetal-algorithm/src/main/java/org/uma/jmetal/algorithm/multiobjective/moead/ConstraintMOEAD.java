package org.uma.jmetal.algorithm.multiobjective.moead;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.algorithm.multiobjective.moead.util.ViolationThresholdComparator;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * This class implements a constrained version of the MOEAD algorithm based on the one presented in
  the paper: "An adaptive constraint handling approach embedded MOEA/D". DOI: 10.1109/CEC.2012.6252868

 * @author Antonio J. Nebro
 * @author Juan J. Durillo
 * @version 1.0
 */
@SuppressWarnings("serial")
public class ConstraintMOEAD extends AbstractMOEAD<DoubleSolution>  {

  private DifferentialEvolutionCrossover differentialEvolutionCrossover ;
  private ViolationThresholdComparator<DoubleSolution> violationThresholdComparator ;

  public ConstraintMOEAD(Problem<DoubleSolution> problem,
      int populationSize,
      int resultPopulationSize,
      int maxEvaluations,
      MutationOperator<DoubleSolution> mutation,
      CrossoverOperator<DoubleSolution> crossover,
      FunctionType functionType,
      String dataDirectory,
      double neighborhoodSelectionProbability,
      int maximumNumberOfReplacedSolutions,
      int neighborSize) {
    super(problem, populationSize, resultPopulationSize, maxEvaluations, crossover, mutation, functionType,
        dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions,
        neighborSize);

    differentialEvolutionCrossover = (DifferentialEvolutionCrossover)crossoverOperator ;
    violationThresholdComparator = new ViolationThresholdComparator<DoubleSolution>() ;
  }

  @Override public void run() {
    initializeUniformWeight();
    initializeNeighborhood();
    initializePopulation();
    idealPoint.update(population);

    violationThresholdComparator.updateThreshold(population);

    evaluations = populationSize ;

    do {
      var permutation = new int[populationSize];
      MOEADUtils.randomPermutation(permutation, populationSize);

      for (var i = 0; i < populationSize; i++) {
        var subProblemId = permutation[i];

        @NotNull NeighborType neighborType = chooseNeighborType() ;
        @NotNull List<DoubleSolution> parents = parentSelection(subProblemId, neighborType) ;

        differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
        var children = differentialEvolutionCrossover.execute(parents);

        var child = children.get(0) ;
        mutationOperator.execute(child);
        problem.evaluate(child);

        evaluations++;

        idealPoint.update(child.objectives());
        updateNeighborhood(child, subProblemId, neighborType);
      }

      violationThresholdComparator.updateThreshold(population);

    } while (evaluations < maxEvaluations);
  }

  public void initializePopulation() {
    for (var i = 0; i < populationSize; i++) {
      var newSolution = (DoubleSolution)problem.createSolution() ;

      problem.evaluate(newSolution);
      population.add(newSolution);
    }
  }

  @Override
  protected void updateNeighborhood(DoubleSolution individual, int subproblemId, NeighborType neighborType) {
    int size;

    var time = 0;

    if (neighborType == NeighborType.NEIGHBOR) {
      size = neighborhood[subproblemId].length;
    } else {
      size = population.size();
    }
    var perm = new int[size];

    MOEADUtils.randomPermutation(perm, size);

    for (var i = 0; i < size; i++) {
      int k;
      if (neighborType == NeighborType.NEIGHBOR) {
        k = neighborhood[subproblemId][perm[i]];
      } else {
        k = perm[i];
      }

      var f1 = fitnessFunction(population.get(k), lambda[k]);
      var f2 = fitnessFunction(individual, lambda[k]);

      if (violationThresholdComparator.needToCompare(population.get(k), individual)) {
        var flag = violationThresholdComparator.compare(population.get(k), individual);
        if (flag == 1) {
          population.set(k, (DoubleSolution) individual.copy());
        } else if (flag == 0) {
          if (f2 < f1) {
            population.set(k, (DoubleSolution) individual.copy());
            time++;
          }
        }
      } else {
        if (f2 < f1) {
          population.set(k, (DoubleSolution) individual.copy());
          time++;
        }
      }

      if (time >= maximumNumberOfReplacedSolutions) {
        return;
      }
    }
  }

  @Override public @NotNull String getName() {
    return "cMOEAD" ;
  }

  @Override public @NotNull String getDescription() {
    return "Multi-Objective Evolutionary Algorithm based on Decomposition with constraints support" ;
  }
}
