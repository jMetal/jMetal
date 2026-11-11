package org.uma.jmetal.component.catalogue.common.termination;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIBuilder;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByAnyOf;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByUserRequest;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("Unit tests for class TerminationByUserRequest")
class TerminationByUserRequestTest {
  private TerminationByUserRequest termination;
  private Map<String, Object> status;

  @BeforeEach
  void setUp() {
    termination = new TerminationByUserRequest();
    status = new HashMap<>();
  }

  @Nested
  @DisplayName("When it is checked")
  class WhenItIsChecked {
    @Test
    @DisplayName("given no request, then it is not met")
    void givenNoRequest_thenItIsNotMet() {
      assertThat(termination.isMet(status)).isFalse();
      assertThat(termination.stopRequested()).isFalse();
    }

    @Test
    @DisplayName("given a request, then it is met")
    void givenARequest_thenItIsMet() {
      termination.requestStop();

      assertThat(termination.isMet(status)).isTrue();
      assertThat(termination.stopRequested()).isTrue();
    }

    @Test
    @DisplayName("given two requests, then it is still met")
    void givenTwoRequests_thenItIsStillMet() {
      termination.requestStop();
      termination.requestStop();

      assertThat(termination.isMet(status)).isTrue();
    }

    @Test
    @DisplayName("given a request from another thread, then it is met")
    void givenARequestFromAnotherThread_thenItIsMet() throws InterruptedException {
      Thread userInterface = new Thread(termination::requestStop);
      userInterface.start();
      userInterface.join();

      assertThat(termination.isMet(status)).isTrue();
    }

    @Test
    @DisplayName("given any status, then the progress is empty")
    void givenAnyStatus_thenTheProgressIsEmpty() {
      assertThat(termination.progress(status)).isEmpty();
    }
  }

  @Nested
  @DisplayName("When an algorithm runs in another thread")
  class WhenAnAlgorithmRunsInAnotherThread {
    @Test
    @DisplayName("given a request while it runs, then it stops before its budget")
    void givenARequestWhileItRuns_thenItStopsBeforeItsBudget() throws Exception {
      int maximumNumberOfEvaluations = Integer.MAX_VALUE;
      var problem = new ZDT1();
      EvolutionaryAlgorithm<DoubleSolution> algorithm =
          new NSGAIIBuilder<>(
                  problem,
                  20,
                  20,
                  new SBXCrossover(0.9, 20.0),
                  new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0))
              .setTermination(
                  new TerminationByAnyOf(
                      termination, new TerminationByEvaluations(maximumNumberOfEvaluations)))
              .build();
      CountDownLatch running = new CountDownLatch(1);
      algorithm.observable().register((observable, data) -> running.countDown());

      CompletableFuture<Void> run = CompletableFuture.runAsync(algorithm::run);
      assertThat(running.await(10, TimeUnit.SECONDS)).isTrue();
      termination.requestStop();
      run.get(10, TimeUnit.SECONDS);

      assertThat(algorithm.numberOfEvaluations()).isLessThan(maximumNumberOfEvaluations);
      assertThat(algorithm.result()).hasSize(20);
    }
  }
}
