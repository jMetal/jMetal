package org.uma.jmetal.algorithm.multiobjective.nsgaiii;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Unit tests for NSGA-III algorithm and builder.
 */
class NSGAIIITest {

  @Test
  void buildWithoutCrossoverShouldThrowException() {
    var problem = ProblemFactory.<DoubleSolution>loadProblem(
        "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1");

    var builder = new NSGAIIIBuilder<>(problem)
        .setMutationOperator(new PolynomialMutation(0.1, 20.0))
        .setSelectionOperator(new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>()));

    assertThrows(JMetalException.class, builder::build);
  }

  @Test
  void buildWithoutMutationShouldThrowException() {
    var problem = ProblemFactory.<DoubleSolution>loadProblem(
        "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1");

    var builder = new NSGAIIIBuilder<>(problem)
        .setCrossoverOperator(new SBXCrossover(0.9, 20.0))
        .setSelectionOperator(new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>()));

    assertThrows(JMetalException.class, builder::build);
  }

  @Test
  void buildWithoutSelectionShouldThrowException() {
    var problem = ProblemFactory.<DoubleSolution>loadProblem(
        "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1");

    var builder = new NSGAIIIBuilder<>(problem)
        .setCrossoverOperator(new SBXCrossover(0.9, 20.0))
        .setMutationOperator(new PolynomialMutation(0.1, 20.0));

    assertThrows(JMetalException.class, builder::build);
  }

  @Test
  void buildWithAllOperatorsShouldSucceed() {
    var problem = ProblemFactory.<DoubleSolution>loadProblem(
        "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1");

    var algorithm = new NSGAIIIBuilder<>(problem)
        .setCrossoverOperator(new SBXCrossover(0.9, 20.0))
        .setMutationOperator(new PolynomialMutation(0.1, 20.0))
        .setSelectionOperator(new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>()))
        .build();

    assertNotNull(algorithm);
  }

  @Test
  void singleLayerReferencePointsShouldWork() {
    var problem = ProblemFactory.<DoubleSolution>loadProblem(
        "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1");

    var algorithm = new NSGAIIIBuilder<>(problem)
        .setCrossoverOperator(new SBXCrossover(0.9, 20.0))
        .setMutationOperator(new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0))
        .setSelectionOperator(new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>()))
        .setNumberOfDivisions(3)
        .setMaxIterations(5)
        .build();

    algorithm.run();
    List<DoubleSolution> result = algorithm.result();

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }

  @Test
  void twoLayerReferencePointsShouldWork() {
    var problem = ProblemFactory.<DoubleSolution>loadProblem(
        "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1");

    var algorithm = new NSGAIIIBuilder<>(problem)
        .setCrossoverOperator(new SBXCrossover(0.9, 20.0))
        .setMutationOperator(new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0))
        .setSelectionOperator(new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>()))
        .setNumberOfDivisions(3)
        .setSecondLayerDivisions(2)
        .setMaxIterations(5)
        .build();

    algorithm.run();
    List<DoubleSolution> result = algorithm.result();

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }
}
