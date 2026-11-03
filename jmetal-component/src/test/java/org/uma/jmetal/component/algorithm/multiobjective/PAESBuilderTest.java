package org.uma.jmetal.component.algorithm.multiobjective;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.PAESReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.impl.PAESSelection;
import org.uma.jmetal.component.catalogue.ea.variation.impl.MutationOnlyVariation;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("Unit tests for class PAESBuilder")
class PAESBuilderTest {
  private static final int ARCHIVE_SIZE = 100;
  private ZDT1 problem;
  private PolynomialMutation mutation;
  private BoundedArchive<DoubleSolution> archive;

  @BeforeEach
  void setUp() {
    problem = new ZDT1();
    mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);
    archive = new CrowdingDistanceArchive<>(ARCHIVE_SIZE);
  }

  @Nested
  @DisplayName("When build is called")
  class WhenBuildIsCalled {
    @Test
    @DisplayName("given default settings, then a non-null algorithm named PAES is returned")
    void givenDefaultSettings_whenBuild_thenNonNullAlgorithmNamedPAES() {
      EvolutionaryAlgorithm<DoubleSolution> algorithm =
          new PAESBuilder<>(problem, ARCHIVE_SIZE, mutation, archive).build();

      assertThat(algorithm).isNotNull();
      assertThat(algorithm.name()).isEqualTo("PAES");
    }

    @Test
    @DisplayName("given default settings, then PAES catalogue components are wired")
    void givenDefaultSettings_whenBuild_thenPAESComponentsAreWired() {
      EvolutionaryAlgorithm<DoubleSolution> algorithm =
          new PAESBuilder<>(problem, ARCHIVE_SIZE, mutation, archive).build();

      assertThat(algorithm.variation()).isInstanceOf(MutationOnlyVariation.class);
      assertThat(algorithm.selection()).isInstanceOf(PAESSelection.class);
      assertThat(algorithm.replacement()).isInstanceOf(PAESReplacement.class);
    }

    @Test
    @DisplayName("given a (1+1) ES, then the variation produces a single offspring")
    void givenOnePlusOneStrategy_whenBuild_thenVariationProducesSingleOffspring() {
      EvolutionaryAlgorithm<DoubleSolution> algorithm =
          new PAESBuilder<>(problem, ARCHIVE_SIZE, mutation, archive).build();

      assertThat(algorithm.variation().offspringPopulationSize()).isEqualTo(1);
      assertThat(algorithm.variation().matingPoolSize()).isEqualTo(1);
    }
  }

  @Nested
  @DisplayName("When setters are used")
  class WhenSettersAreUsed {
    @Test
    @DisplayName("given a custom name, then the built algorithm uses it")
    void givenCustomName_whenBuild_thenNameIsUsed() {
      EvolutionaryAlgorithm<DoubleSolution> algorithm =
          new PAESBuilder<>(problem, ARCHIVE_SIZE, mutation, archive)
              .setName("MyPAES")
              .build();

      assertThat(algorithm.name()).isEqualTo("MyPAES");
    }

    @Test
    @DisplayName("given a custom termination, then the built algorithm uses it")
    void givenCustomTermination_whenBuild_thenTerminationIsUsed() {
      Termination termination = new TerminationByEvaluations(1000);

      EvolutionaryAlgorithm<DoubleSolution> algorithm =
          new PAESBuilder<>(problem, ARCHIVE_SIZE, mutation, archive)
              .setTermination(termination)
              .build();

      assertThat(algorithm.termination()).isSameAs(termination);
    }
  }

  @Nested
  @DisplayName("When result is called")
  class WhenResultIsCalled {
    @Test
    @DisplayName("given an archive with solutions, then result returns the archive contents")
    void givenArchiveWithSolutions_whenResult_thenReturnsArchiveContents() {
      EvolutionaryAlgorithm<DoubleSolution> algorithm =
          new PAESBuilder<>(problem, ARCHIVE_SIZE, mutation, archive).build();

      DoubleSolution solution = problem.createSolution();
      problem.evaluate(solution);
      archive.add(solution);

      assertThat(algorithm.result()).isEqualTo(archive.solutions());
      assertThat(algorithm.result()).contains(solution);
    }
  }

  @Nested
  @DisplayName("When the problem has constraints")
  class WhenTheProblemHasConstraints {
    private DoubleSolution solution(double f1, double f2, double constraint) {
      DoubleSolution solution =
          new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), 2, 1);
      solution.objectives()[0] = f1;
      solution.objectives()[1] = f2;
      solution.constraints()[0] = constraint;
      return solution;
    }

    @Test
    @DisplayName(
        "given a feasible current solution, when an infeasible mutant that dominates it in the"
            + " objectives is compared, then the current solution stays")
    void givenAFeasibleCurrentSolution_whenAnInfeasibleBetterMutantIsCompared_thenItStays() {
      // Arrange
      EvolutionaryAlgorithm<DoubleSolution> algorithm =
          new PAESBuilder<>(problem, ARCHIVE_SIZE, mutation, archive).build();
      DoubleSolution current = solution(0.5, 0.5, 0.0);
      List<DoubleSolution> population = new ArrayList<>(List.of(current));

      // Act
      List<DoubleSolution> result =
          algorithm.replacement().replace(population, List.of(solution(0.1, 0.1, -1.0)));

      // Assert
      assertThat(result).containsExactly(current);
    }
  }

  @Nested
  @DisplayName("When an initial solution is given")
  class WhenAnInitialSolutionIsGiven {
    @Test
    @DisplayName("given an initial solution, when the population is created, then it is a copy of it")
    void givenAnInitialSolution_whenThePopulationIsCreated_thenItIsACopyOfIt() {
      // Arrange
      DoubleSolution initialSolution = problem.createSolution();
      EvolutionaryAlgorithm<DoubleSolution> algorithm =
          new PAESBuilder<>(problem, ARCHIVE_SIZE, mutation, archive, initialSolution).build();

      // Act
      List<DoubleSolution> population = algorithm.createInitialPopulation().create();

      // Assert
      assertThat(population).hasSize(1);
      assertThat(population.get(0)).isNotSameAs(initialSolution);
      assertThat(population.get(0).variables()).isEqualTo(initialSolution.variables());
    }

    @Test
    @DisplayName("given an initial solution, when the algorithm runs, then the solution is not modified")
    void givenAnInitialSolution_whenTheAlgorithmRuns_thenTheSolutionIsNotModified() {
      // Arrange
      DoubleSolution initialSolution = problem.createSolution();
      List<Double> variables = new ArrayList<>(initialSolution.variables());
      EvolutionaryAlgorithm<DoubleSolution> algorithm =
          new PAESBuilder<>(problem, ARCHIVE_SIZE, mutation, archive, initialSolution)
              .setTermination(new TerminationByEvaluations(200))
              .build();

      // Act
      algorithm.run();

      // Assert
      assertThat(initialSolution.variables()).isEqualTo(variables);
      assertThat(initialSolution.objectives()).containsOnly(0.0);
      assertThat(algorithm.result()).isNotEmpty().doesNotContain(initialSolution);
    }

    @Test
    @DisplayName("given a null initial solution, when the builder is created, then an exception is thrown")
    void givenANullInitialSolution_whenTheBuilderIsCreated_thenAnExceptionIsThrown() {
      // Act & Assert
      assertThatThrownBy(() -> new PAESBuilder<>(problem, ARCHIVE_SIZE, mutation, archive, null))
          .isInstanceOf(NullParameterException.class);
    }
  }
}
