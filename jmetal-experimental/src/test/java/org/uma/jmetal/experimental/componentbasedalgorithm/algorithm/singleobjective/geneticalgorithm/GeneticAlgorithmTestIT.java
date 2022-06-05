package org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.singleobjective.geneticalgorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.termination.Termination;
import org.uma.jmetal.util.termination.impl.TerminationByEvaluations;

public class GeneticAlgorithmTestIT {

  @Test
  public void shouldTheAlgorithmReturnTheCorrectSolutionWhenSolvingProblemOneMax() {
    int NUMBER_OF_BITS = 256 ;
    GeneticAlgorithm<BinarySolution> algorithm;
    BinaryProblem problem = new OneMax(NUMBER_OF_BITS) ;

    int populationSize = 100;
    int offspringPopulationSize = populationSize;

    NaryTournamentSelection<BinarySolution> selection =
            new NaryTournamentSelection<>(2, new ObjectiveComparator<>(0));

    MutationOperator<BinarySolution> mutation =
            new BitFlipMutation(1.0 / problem.getBitsFromVariable(0));

    CrossoverOperator<BinarySolution> crossover = new SinglePointCrossover(0.9);

    Termination termination = new TerminationByEvaluations(50000);

    algorithm =
            new GeneticAlgorithm<BinarySolution>(
                    problem,
                    populationSize,
                    offspringPopulationSize,
                    selection,
                    crossover,
                    mutation,
                    termination);
    algorithm.run();

    BinarySolution solution = algorithm.getResult().get(0) ;
    assertEquals(NUMBER_OF_BITS, -1 * (int)solution.objectives()[0]) ;
  }

  @Test
  public void shouldASteadyStateVersionReturnTheCorrectSolutionWhenSolvingProblemOneMax() {
    int NUMBER_OF_BITS = 256 ;
    GeneticAlgorithm<BinarySolution> algorithm;
    BinaryProblem problem = new OneMax(NUMBER_OF_BITS) ;

    int populationSize = 100;
    int offspringPopulationSize = 1;

    NaryTournamentSelection<BinarySolution> selection =
            new NaryTournamentSelection<>(2, new ObjectiveComparator<>(0));

    MutationOperator<BinarySolution> mutation =
            new BitFlipMutation(1.0 / problem.getBitsFromVariable(0));

    CrossoverOperator<BinarySolution> crossover = new SinglePointCrossover(0.9);

    Termination termination = new TerminationByEvaluations(50000);

    algorithm =
            new GeneticAlgorithm<BinarySolution>(
                    problem,
                    populationSize,
                    offspringPopulationSize,
                    selection,
                    crossover,
                    mutation,
                    termination);
    algorithm.run();

    BinarySolution solution = algorithm.getResult().get(0) ;
    assertEquals(NUMBER_OF_BITS, -1 * (int)solution.objectives()[0]) ;
  }
}