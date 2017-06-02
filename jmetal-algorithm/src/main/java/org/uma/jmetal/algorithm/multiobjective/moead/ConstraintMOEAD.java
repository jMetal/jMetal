package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.comparator.impl.ViolationThresholdComparator;

import java.util.List;

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
    initializeIdealPoint();

    violationThresholdComparator.updateThreshold(population);

    evaluations = populationSize ;

    do {
      int[] permutation = new int[populationSize];
      MOEADUtils.randomPermutation(permutation, populationSize);

      for (int i = 0; i < populationSize; i++) {
        int subProblemId = permutation[i];

        NeighborType neighborType = chooseNeighborType() ;
        List<DoubleSolution> parents = parentSelection(subProblemId, neighborType) ;

        differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
        List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);

        DoubleSolution child = children.get(0) ;
        mutationOperator.execute(child);
        problem.evaluate(child);
        if (problem instanceof ConstrainedProblem) {
          ((ConstrainedProblem<DoubleSolution>) problem).evaluateConstraints(child);
        }
        evaluations++;

        updateIdealPoint(child);
        updateNeighborhood(child, subProblemId, neighborType);
      }

      violationThresholdComparator.updateThreshold(population);

    } while (evaluations < maxEvaluations);
  }

  public void initializePopulation() {
    for (int i = 0; i < populationSize; i++) {
      DoubleSolution newSolution = (DoubleSolution)problem.createSolution() ;

      problem.evaluate(newSolution);
      if (problem instanceof ConstrainedProblem) {
        ((ConstrainedProblem<DoubleSolution>) problem).evaluateConstraints(newSolution);
      }
      population.add(newSolution);
    }
  }

  @Override
  protected void updateNeighborhood(DoubleSolution individual, int subproblemId, NeighborType neighborType) {
    int size;
    int time;

    time = 0;

    if (neighborType == NeighborType.NEIGHBOR) {
      size = neighborhood[subproblemId].length;
    } else {
      size = population.size();
    }
    int[] perm = new int[size];

    MOEADUtils.randomPermutation(perm, size);

    for (int i = 0; i < size; i++) {
      int k;
      if (neighborType == NeighborType.NEIGHBOR) {
        k = neighborhood[subproblemId][perm[i]];
      } else {
        k = perm[i];
      }
      double f1, f2;

      f1 = fitnessFunction(population.get(k), lambda[k]);
      f2 = fitnessFunction(individual, lambda[k]);

      if (violationThresholdComparator.needToCompare(population.get(k), individual)) {
        int flag = violationThresholdComparator.compare(population.get(k), individual);
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

  @Override public String getName() {
    return "cMOEAD" ;
  }

  @Override public String getDescription() {
    return "Multi-Objective Evolutionary Algorithm based on Decomposition with constraints support" ;
  }
}
