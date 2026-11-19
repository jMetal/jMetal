package org.uma.jmetal.algorithm.dynamic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.observer.Observer;
import org.uma.jmetal.util.restartstrategy.RestartStrategy;

@DisplayName("Unit tests for class DynamicAlgorithm")
class DynamicAlgorithmTest {
  private static final int POPULATION_SIZE = 10;

  private StepwiseAlgorithm<DoubleSolution> algorithm;
  private DynamicProblem<DoubleSolution, Integer> problem;
  private RestartStrategy<DoubleSolution> restartStrategy;
  private List<Map<String, Object>> notifications;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    algorithm = mock(StepwiseAlgorithm.class);
    problem = mock(DynamicProblem.class);
    restartStrategy = mock(RestartStrategy.class);
    when(algorithm.initialize()).thenReturn(POPULATION_SIZE);
    when(algorithm.step()).thenReturn(POPULATION_SIZE);
    when(algorithm.restart(any(), any())).thenReturn(POPULATION_SIZE);
    when(algorithm.result()).thenReturn(front(0.0));
    when(algorithm.name()).thenReturn("Algorithm");
    notifications = new ArrayList<>();
  }

  private DynamicAlgorithm<DoubleSolution> dynamicAlgorithm(int iterationsPerRound) {
    var dynamicAlgorithm =
        new DynamicAlgorithm<>("Dynamic", algorithm, problem, restartStrategy, iterationsPerRound);
    dynamicAlgorithm.observable().register((observable, data) -> notifications.add(data));
    return dynamicAlgorithm;
  }

  /** A front of two solutions, (shift, 1) and (1, shift). */
  private static List<DoubleSolution> front(double shift) {
    List<DoubleSolution> front = new ArrayList<>();
    for (double[] objectives : new double[][] {{shift, 1.0}, {1.0, shift}}) {
      DoubleSolution solution = new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), 2, 0);
      solution.objectives()[0] = objectives[0];
      solution.objectives()[1] = objectives[1];
      front.add(solution);
    }
    return front;
  }

  private List<Object> events() {
    return notifications.stream().map(data -> data.get("EVENT")).toList();
  }

  @Nested
  @DisplayName("When it runs")
  class WhenItRuns {
    @Test
    @DisplayName("given a stopping condition, then it stops when it is met and counts all the evaluations")
    void givenAStoppingCondition_thenItStopsAndCountsTheEvaluations() {
      var dynamicAlgorithm = dynamicAlgorithm(100);
      dynamicAlgorithm.stoppingCondition(DynamicStoppingCondition.afterIterations(5));

      dynamicAlgorithm.run();

      verify(algorithm, times(5)).step();
      assertThat(dynamicAlgorithm.status().iterations()).isEqualTo(5);
      assertThat(dynamicAlgorithm.status().evaluations()).isEqualTo(6L * POPULATION_SIZE);
    }

    @Test
    @DisplayName("given a stop request during an iteration, then it stops at the end of the iteration")
    void givenAStopRequestDuringAnIteration_thenItStopsAtTheEndOfTheIteration() {
      var dynamicAlgorithm = dynamicAlgorithm(100);
      when(algorithm.step())
          .thenReturn(POPULATION_SIZE)
          .thenReturn(POPULATION_SIZE)
          .thenAnswer(
              invocation -> {
                dynamicAlgorithm.requestStop();
                return POPULATION_SIZE;
              });

      dynamicAlgorithm.run();

      verify(algorithm, times(3)).step();
    }

    @Test
    @DisplayName("given no stopping condition, when a stop is requested from another thread, then the run ends")
    void givenNoStoppingCondition_whenAStopIsRequestedFromAnotherThread_thenTheRunEnds()
        throws InterruptedException {
      var dynamicAlgorithm = dynamicAlgorithm(100);
      Thread run = new Thread(dynamicAlgorithm);
      run.start();

      Thread.sleep(50);
      dynamicAlgorithm.requestStop();
      run.join(TimeUnit.SECONDS.toMillis(10));

      assertThat(run.isAlive()).isFalse();
    }
  }

  @Nested
  @DisplayName("When the problem changes")
  class WhenTheProblemChanges {
    @Test
    @DisplayName("given a change, then the search is restarted once and the re-evaluations are counted")
    void givenAChange_thenTheSearchIsRestartedOnceAndTheReEvaluationsAreCounted() {
      when(problem.hasChanged()).thenReturn(false).thenReturn(true).thenReturn(false);
      var dynamicAlgorithm = dynamicAlgorithm(100);
      dynamicAlgorithm.stoppingCondition(DynamicStoppingCondition.afterIterations(3));

      dynamicAlgorithm.run();

      verify(algorithm, times(1)).restart(restartStrategy, problem);
      verify(problem, times(1)).clearChanged();
      assertThat(dynamicAlgorithm.status().problemChanges()).isEqualTo(1);
      assertThat(dynamicAlgorithm.status().evaluations()).isEqualTo(5L * POPULATION_SIZE);
    }

    @Test
    @DisplayName("given no change, then the search is never restarted")
    void givenNoChange_thenTheSearchIsNeverRestarted() {
      var dynamicAlgorithm = dynamicAlgorithm(2);
      dynamicAlgorithm.stoppingCondition(DynamicStoppingCondition.afterIterations(10));

      dynamicAlgorithm.run();

      verify(algorithm, never()).restart(any(), any());
    }

    @Test
    @DisplayName("given the time is driven by the iterations, then the problem receives the iteration counter")
    void givenTheTimeIsDrivenByTheIterations_thenTheProblemReceivesTheIterationCounter() {
      var dynamicAlgorithm = dynamicAlgorithm(100);
      dynamicAlgorithm
          .timeDrivenByIterations(true)
          .stoppingCondition(DynamicStoppingCondition.afterIterations(3));

      dynamicAlgorithm.run();

      InOrder order = inOrder(problem);
      order.verify(problem).update(1);
      order.verify(problem).update(2);
      order.verify(problem).update(3);
    }

    @Test
    @DisplayName("given an external update, then it is applied after the current iteration")
    void givenAnExternalUpdate_thenItIsAppliedAfterTheCurrentIteration() {
      var dynamicAlgorithm = dynamicAlgorithm(100);
      dynamicAlgorithm.stoppingCondition(DynamicStoppingCondition.afterIterations(1));
      dynamicAlgorithm.updateProblem(7);

      dynamicAlgorithm.run();

      InOrder order = inOrder(algorithm, problem);
      order.verify(algorithm).step();
      order.verify(problem).update(7);
    }

    @Test
    @DisplayName("given problem changes are notified, then the front is notified before the restart")
    void givenProblemChangesAreNotified_thenTheFrontIsNotifiedBeforeTheRestart() {
      when(problem.hasChanged()).thenReturn(true).thenReturn(false);
      @SuppressWarnings("unchecked")
      Observer<Map<String, Object>> observer = mock(Observer.class);
      var dynamicAlgorithm = dynamicAlgorithm(100);
      dynamicAlgorithm
          .notifyProblemChanges(true)
          .stoppingCondition(DynamicStoppingCondition.afterIterations(2));
      dynamicAlgorithm.observable().register(observer);

      dynamicAlgorithm.run();

      InOrder order = inOrder(observer, algorithm);
      order.verify(observer).update(any(), any());
      order.verify(algorithm).restart(restartStrategy, problem);
      assertThat(events()).containsExactly(DynamicAlgorithm.PROBLEM_CHANGE);
    }
  }

  @Nested
  @DisplayName("When a round ends")
  class WhenARoundEnds {
    @Test
    @DisplayName("given a threshold of 0, then every round is published with the status of the run")
    void givenAThresholdOfZero_thenEveryRoundIsPublished() {
      var dynamicAlgorithm = dynamicAlgorithm(2);
      dynamicAlgorithm.stoppingCondition(DynamicStoppingCondition.afterIterations(6));

      dynamicAlgorithm.run();

      assertThat(events()).containsExactly(
          DynamicAlgorithm.ROUND_END, DynamicAlgorithm.ROUND_END, DynamicAlgorithm.ROUND_END);
      assertThat(notifications.get(2))
          .containsEntry("ROUNDS", 3)
          .containsEntry("ITERATIONS", 6L)
          .containsEntry("EVALUATIONS", 7 * POPULATION_SIZE);
      assertThat((List<?>) notifications.get(2).get("POPULATION")).hasSize(2);
    }

    @Test
    @DisplayName("given a threshold, then a front equal to the last one published is not published")
    void givenAThreshold_thenAFrontEqualToTheLastOnePublishedIsNotPublished() {
      when(algorithm.result()).thenReturn(front(0.0), front(0.0), front(0.5));
      var dynamicAlgorithm = dynamicAlgorithm(1);
      dynamicAlgorithm
          .frontChangeThreshold(new InvertedGenerationalDistance(), 0.01)
          .stoppingCondition(DynamicStoppingCondition.afterIterations(3));

      dynamicAlgorithm.run();

      assertThat(notifications).hasSize(2);
      assertThat(notifications.get(1)).containsEntry("ROUNDS", 3);
    }

    @Test
    @DisplayName("given the problem changes at the end of a round, then the front is published before the restart")
    void givenTheProblemChangesAtTheEndOfARound_thenTheFrontIsPublishedBeforeTheRestart() {
      when(problem.hasChanged()).thenReturn(true).thenReturn(false);
      @SuppressWarnings("unchecked")
      Observer<Map<String, Object>> observer = mock(Observer.class);
      var dynamicAlgorithm = dynamicAlgorithm(1);
      dynamicAlgorithm.stoppingCondition(DynamicStoppingCondition.afterIterations(1));
      dynamicAlgorithm.observable().register(observer);

      dynamicAlgorithm.run();

      InOrder order = inOrder(observer, algorithm);
      order.verify(observer).update(any(), any());
      order.verify(algorithm).restart(restartStrategy, problem);
    }

    @Test
    @DisplayName("given a restart, then the round is not reset")
    void givenARestart_thenTheRoundIsNotReset() {
      when(problem.hasChanged()).thenReturn(false, true, false);
      var dynamicAlgorithm = dynamicAlgorithm(3);
      dynamicAlgorithm.stoppingCondition(DynamicStoppingCondition.afterIterations(3));

      dynamicAlgorithm.run();

      assertThat(dynamicAlgorithm.status().rounds()).isEqualTo(1);
      verify(problem, never()).update(anyInt());
    }
  }
}
