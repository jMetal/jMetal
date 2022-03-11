package org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.singleobjective.geneticalgorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.binarysolution.BinarySolution;

/**
 * Created by ajnebro on 27/10/15.
 */
public class GenerationalGeneticAlgorithmTestIT {

  @Test
  public void shouldTheAlgorithmReturnTheCorrectSolutionWhenSolvingProblemOneMax() {
    int NUMBER_OF_BITS = 512 ;
    Algorithm<BinarySolution> algorithm;
    BinaryProblem problem = new OneMax(NUMBER_OF_BITS) ;

    CrossoverOperator<BinarySolution> crossoverOperator = new SinglePointCrossover(0.9) ;
    MutationOperator<BinarySolution> mutationOperator = new BitFlipMutation(1.0 / problem.getBitsFromVariable(0)) ;
    SelectionOperator<List<BinarySolution>, BinarySolution> selectionOperator = new BinaryTournamentSelection<BinarySolution>();

    algorithm = new GeneticAlgorithmBuilder<BinarySolution>(problem, crossoverOperator, mutationOperator)
            .setPopulationSize(50)
            .setMaxEvaluations(50000)
            .setSelectionOperator(selectionOperator)
            .build() ;

    algorithm.run();

    BinarySolution solution = algorithm.getResult() ;
    assertEquals(NUMBER_OF_BITS, -1 * (int)solution.objectives()[0]) ;
  }
}