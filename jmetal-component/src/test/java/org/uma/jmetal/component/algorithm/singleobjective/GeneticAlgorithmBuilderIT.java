package org.uma.jmetal.component.algorithm.singleobjective;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.binarysolution.BinarySolution;

class GeneticAlgorithmBuilderIT {

  @Test
  void AGenerationalGeneticAlgorithmReturnTheCorrectSolutionWhenSolvingProblemOneMax() {
    var number_of_bits = 512 ;
    BinaryProblem problem = new OneMax(number_of_bits) ;

    var crossoverProbability = 0.9;
    var crossover = new SinglePointCrossover(crossoverProbability);

    var mutationProbability = 1.0 / problem.getTotalNumberOfBits() ;
    var mutation = new BitFlipMutation(mutationProbability);

    var populationSize = 100;
    var offspringPopulationSize = populationSize;

    Termination termination = new TerminationByEvaluations(25000);

    var geneticAlgorithm = new GeneticAlgorithmBuilder<>(
        "GGA",
        problem,
        populationSize,
        offspringPopulationSize,
        crossover,
        mutation)
        .setTermination(termination)
        .build();


    geneticAlgorithm.run();


    var solution = geneticAlgorithm.getResult().get(0) ;
    assertEquals(number_of_bits, -1 * (int)solution.objectives()[0]) ;
  }

  @Test
  void ASteadyStateGeneticAlgorithmReturnTheCorrectSolutionWhenSolvingProblemOneMax() {
    var number_of_bits = 512 ;
    BinaryProblem problem = new OneMax(number_of_bits) ;

    var crossoverProbability = 0.9;
    var crossover = new SinglePointCrossover(crossoverProbability);

    var mutationProbability = 1.0 / problem.getTotalNumberOfBits() ;
    var mutation = new BitFlipMutation(mutationProbability);

    var populationSize = 100;
    var offspringPopulationSize = 1;

    Termination termination = new TerminationByEvaluations(25000);

    var geneticAlgorithm = new GeneticAlgorithmBuilder<>(
        "GGA",
        problem,
        populationSize,
        offspringPopulationSize,
        crossover,
        mutation)
        .setTermination(termination)
        .build();


    geneticAlgorithm.run();


    var solution = geneticAlgorithm.getResult().get(0) ;
    assertEquals(number_of_bits, -1 * (int)solution.objectives()[0]) ;
  }
}