package org.uma.jmetal.algorithm.singleobjective.geneticalgorithm;

import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SinglePointCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.AlgorithmRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by ajnebro on 27/10/15.
 */
public class SteadyStateGeneticAlgorithmTestIT {

  @Test
  public void shouldTheAlgorithmReturnTheCorrectSolutionWhenSolvingProblemOneMax() {
    int NUMBER_OF_BITS = 512 ;
    Algorithm<BinarySolution> algorithm;
    BinaryProblem problem = new OneMax(NUMBER_OF_BITS) ;

    CrossoverOperator<BinarySolution> crossoverOperator = new SinglePointCrossover(0.9) ;
    MutationOperator<BinarySolution> mutationOperator = new BitFlipMutation(1.0 / problem.getNumberOfBits(0)) ;
    SelectionOperator<List<BinarySolution>, BinarySolution> selectionOperator = new BinaryTournamentSelection<BinarySolution>();

    algorithm = new GeneticAlgorithmBuilder<BinarySolution>(problem, crossoverOperator, mutationOperator)
            .setVariant(GeneticAlgorithmBuilder.GeneticAlgorithmVariant.STEADY_STATE)
            .setPopulationSize(50)
            .setMaxEvaluations(25000)
            .setSelectionOperator(selectionOperator)
            .build() ;

    new AlgorithmRunner.Executor(algorithm).execute() ;

    BinarySolution solution = algorithm.getResult() ;
    assertEquals(NUMBER_OF_BITS, -1 * (int)solution.getObjective(0)) ;
  }
}