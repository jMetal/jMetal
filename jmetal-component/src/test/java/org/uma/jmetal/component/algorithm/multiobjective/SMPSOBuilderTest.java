package org.uma.jmetal.component.algorithm.multiobjective;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.pso.localbestupdate.LocalBestUpdate;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.constrainthandling.ConstrainedDominance;

@DisplayName("Unit tests for class SMPSOBuilder")
class SMPSOBuilderTest {
  @Nested
  @DisplayName("When setting the local best update")
  class WhenSettingTheLocalBestUpdate {
    @Test
    @DisplayName("given a local best update, when running, then the algorithm uses it")
    void givenALocalBestUpdate_whenRunning_thenTheAlgorithmUsesIt() {
      // Arrange
      AtomicInteger calls = new AtomicInteger();
      LocalBestUpdate localBestUpdate =
          (swarm, localBest) -> {
            calls.incrementAndGet();
            return localBest;
          };
      var smpso =
          new SMPSOBuilder(new ZDT1(5), 10)
              .setTermination(new TerminationByEvaluations(30))
              .setLocalBestUpdate(localBestUpdate)
              .build();

      // Act
      smpso.run();

      // Assert: one update per iteration after the first evaluation of the swarm
      assertThat(calls.get()).isEqualTo(2);
    }
  }

  @Nested
  @DisplayName("When setting the archive")
  class WhenSettingTheArchive {
    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("given an archive, when building, then the global best is chosen with the comparator of that archive")
    void givenAnArchive_whenBuilding_thenTheGlobalBestUsesItsComparator() {
      // Arrange
      BoundedArchive<DoubleSolution> archive = mock(BoundedArchive.class);
      Comparator<DoubleSolution> comparator = (a, b) -> 0;
      when(archive.comparator()).thenReturn(comparator);

      // Act
      new SMPSOBuilder(new ZDT1(5), 10).setArchive(archive).build();

      // Assert: the builder had created the selection with the comparator of its default archive
      verify(archive, atLeastOnce()).comparator();
    }
  }

  @Nested
  @DisplayName("When the termination of the algorithm is replaced after building it")
  class WhenTheTerminationIsReplaced {
    @Test
    @DisplayName("given a constraint-handling technique, when running, then the progress follows the new termination")
    void givenATechnique_whenRunning_thenTheProgressFollowsTheNewTermination() {
      // Arrange: the builder has a termination of 25000 evaluations, and the algorithm 100
      List<Double> progress = new ArrayList<>();
      var technique =
          new ConstrainedDominance<DoubleSolution>() {
            @Override
            public void update(List<DoubleSolution> population, double progressOfTheRun) {
              progress.add(progressOfTheRun);
            }
          };
      var smpso =
          new SMPSOBuilder(new ZDT1(5), 10).setConstraintHandlingTechnique(technique).build();
      smpso.termination(new TerminationByEvaluations(100));

      // Act
      smpso.run();

      // Assert
      assertThat(progress).isNotEmpty();
      assertThat(progress.get(progress.size() - 1)).isGreaterThan(0.5).isLessThanOrEqualTo(1.0);
    }
  }
}
