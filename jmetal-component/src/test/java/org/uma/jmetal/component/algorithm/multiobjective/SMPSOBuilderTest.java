package org.uma.jmetal.component.algorithm.multiobjective;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.pso.localbestupdate.LocalBestUpdate;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;

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
}
