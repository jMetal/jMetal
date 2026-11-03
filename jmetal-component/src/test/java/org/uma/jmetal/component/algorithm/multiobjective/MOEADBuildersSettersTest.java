package org.uma.jmetal.component.algorithm.multiobjective;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.sequencegenerator.impl.CyclicIntegerSequence;

@DisplayName("The setters of the builders of MOEA/D and MOEA/D-DE")
class MOEADBuildersSettersTest {
  private static final int POPULATION_SIZE = 100;

  private static MOEADBuilder<DoubleSolution> moead() {
    var problem = new ZDT1();
    return new MOEADBuilder<>(
        problem,
        POPULATION_SIZE,
        new SBXCrossover(1.0, 20.0),
        new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0),
        "",
        new CyclicIntegerSequence(POPULATION_SIZE),
        false);
  }

  private static MOEADDEBuilder moeadDe() {
    var problem = new ZDT1();
    return new MOEADDEBuilder(
        problem,
        POPULATION_SIZE,
        1.0,
        0.5,
        new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0),
        "",
        new CyclicIntegerSequence(POPULATION_SIZE),
        false);
  }

  /**
   * The index of the sequence stays at the first solution, because only the replacement advances
   * it, so the parents of the solution 0 are chosen from its neighborhood or from the population.
   */
  private static int distinctParentsOfTheFirstSolution(
      EvolutionaryAlgorithm<DoubleSolution> algorithm) {
    JMetalRandom.getInstance().setSeed(1);
    List<DoubleSolution> population = algorithm.createInitialPopulation().create();
    Set<DoubleSolution> parents = new HashSet<>();
    for (int i = 0; i < 2000; i++) {
      parents.addAll(algorithm.selection().select(population));
    }
    return parents.size();
  }

  @Nested
  @DisplayName("MOEA/D")
  class MOEADTests {
    @Test
    @DisplayName("given a neighborhood size, when built, then the parents come from a neighborhood of that size")
    void givenANeighborhoodSize_whenBuilt_thenTheParentsComeFromThatNeighborhood() {
      var algorithm = moead().setNeighborhoodSize(6).setNeighborhoodSelectionProbability(1.0).build();

      // The neighborhood of six solutions, which includes the first one, and the current solution
      assertThat(distinctParentsOfTheFirstSolution(algorithm)).isLessThanOrEqualTo(6);
    }

    @Test
    @DisplayName("given a neighborhood selection probability of zero, when built, then the parents come from the whole population")
    void givenAZeroProbability_whenBuilt_thenTheParentsComeFromThePopulation() {
      var algorithm = moead().setNeighborhoodSize(6).setNeighborhoodSelectionProbability(0.0).build();

      assertThat(distinctParentsOfTheFirstSolution(algorithm)).isGreaterThan(50);
    }

    @Test
    @DisplayName("given a selection that is not of the kind of MOEA/D, when built, then an exception names the problem")
    void givenAnotherSelection_whenBuilt_thenAnExceptionIsRaised() {
      var builder = moead().setSelection(new NaryTournamentSelection<>(2, 2, (a, b) -> 0));

      assertThrows(InvalidConditionException.class, builder::build);
    }

    @Test
    @DisplayName("given a builder, when it builds two algorithms, then they do not share selection or replacement")
    void givenABuilder_whenItBuildsTwoAlgorithms_thenTheyDoNotShareComponents() {
      var builder = moead();

      var first = builder.build();
      var second = builder.build();

      assertThat(first.selection()).isNotSameAs(second.selection());
      assertThat(first.replacement()).isNotSameAs(second.replacement());
    }
  }

  @Nested
  @DisplayName("MOEA/D-DE")
  class MOEADDETests {
    @Test
    @DisplayName("given a neighborhood size, when built, then the parents come from a neighborhood of that size")
    void givenANeighborhoodSize_whenBuilt_thenTheParentsComeFromThatNeighborhood() {
      var algorithm = moeadDe().setNeighborhoodSize(6).setNeighborhoodSelectionProbability(1.0).build();

      assertThat(distinctParentsOfTheFirstSolution(algorithm)).isLessThanOrEqualTo(6);
    }

    @Test
    @DisplayName("given a neighborhood selection probability of zero, when built, then the parents come from the whole population")
    void givenAZeroProbability_whenBuilt_thenTheParentsComeFromThePopulation() {
      var algorithm = moeadDe().setNeighborhoodSize(6).setNeighborhoodSelectionProbability(0.0).build();

      assertThat(distinctParentsOfTheFirstSolution(algorithm)).isGreaterThan(50);
    }

    @Test
    @DisplayName("given a builder, when it builds two algorithms, then they do not share selection, variation or replacement")
    void givenABuilder_whenItBuildsTwoAlgorithms_thenTheyDoNotShareComponents() {
      var builder = moeadDe();

      var first = builder.build();
      var second = builder.build();

      assertThat(first.selection()).isNotSameAs(second.selection());
      assertThat(first.variation()).isNotSameAs(second.variation());
      assertThat(first.replacement()).isNotSameAs(second.replacement());
    }
  }
}
