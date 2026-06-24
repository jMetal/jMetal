package org.uma.jmetal.component.algorithm.multiobjective;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;

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
}
