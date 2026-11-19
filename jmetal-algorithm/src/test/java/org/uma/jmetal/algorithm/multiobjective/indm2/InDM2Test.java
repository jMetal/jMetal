package org.uma.jmetal.algorithm.multiobjective.indm2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.algorithm.dynamic.DynamicStoppingCondition;
import org.uma.jmetal.algorithm.dynamic.InteractiveStepwiseAlgorithm;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.restartstrategy.RestartStrategy;

@DisplayName("Unit tests for class InDM2")
class InDM2Test {
  private static final int POPULATION_SIZE = 10;
  private static final List<List<Double>> NEW_POINTS = List.of(List.of(0.6, 0.4));

  private InteractiveStepwiseAlgorithm<DoubleSolution> algorithm;
  private DynamicProblem<DoubleSolution, Integer> problem;
  private RestartStrategy<DoubleSolution> problemChangeStrategy;
  private RestartStrategy<DoubleSolution> referencePointChangeStrategy;
  private InDM2<DoubleSolution> indm2;
  private List<Map<String, Object>> notifications;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    algorithm = mock(InteractiveStepwiseAlgorithm.class);
    problem = mock(DynamicProblem.class);
    problemChangeStrategy = mock(RestartStrategy.class);
    referencePointChangeStrategy = mock(RestartStrategy.class);
    when(problem.numberOfObjectives()).thenReturn(2);
    when(algorithm.name()).thenReturn("WASFGA");
    when(algorithm.initialize()).thenReturn(POPULATION_SIZE);
    when(algorithm.step()).thenReturn(POPULATION_SIZE);
    when(algorithm.restart(any(), any())).thenReturn(POPULATION_SIZE);
    when(algorithm.result()).thenReturn(List.of());
    when(algorithm.acceptsNumberOfReferencePoints(1)).thenReturn(true);
    when(algorithm.referencePoints()).thenReturn(List.of(List.of(0.0, 0.0)));

    indm2 =
        new InDM2<>(algorithm, problem, problemChangeStrategy, referencePointChangeStrategy, 100);
    indm2.stoppingCondition(DynamicStoppingCondition.afterIterations(2));
    notifications = new ArrayList<>();
    indm2.observable().register((observable, data) -> notifications.add(data));
  }

  @Nested
  @DisplayName("When the reference points change")
  class WhenTheReferencePointsChange {
    @Test
    @DisplayName("given new points, then they are taken after an iteration and the search is restarted with its strategy")
    void givenNewPoints_thenTheyAreTakenAndTheSearchIsRestarted() {
      indm2.updateReferencePoints(NEW_POINTS);

      indm2.run();

      verify(algorithm, times(1)).updateReferencePoints(NEW_POINTS);
      verify(algorithm, times(1)).restart(referencePointChangeStrategy, problem);
      verify(algorithm, never()).restart(problemChangeStrategy, problem);
      assertThat(indm2.status().evaluations()).isEqualTo(4L * POPULATION_SIZE);
    }

    @Test
    @DisplayName("given new points, then the observers receive the event without solutions and the points")
    void givenNewPoints_thenTheObserversReceiveTheEvent() {
      indm2.updateReferencePoints(NEW_POINTS);

      indm2.run();

      assertThat(notifications).hasSize(1);
      assertThat(notifications.get(0))
          .containsEntry("EVENT", InDM2.REFERENCE_POINTS_CHANGE)
          .containsEntry("POPULATION", List.of())
          .containsKey("REFERENCE_POINTS");
    }

    @Test
    @DisplayName("given the problem changes at the same time, then the search is restarted only once")
    void givenTheProblemChangesAtTheSameTime_thenTheSearchIsRestartedOnlyOnce() {
      when(problem.hasChanged()).thenReturn(true).thenReturn(false);
      indm2.updateReferencePoints(NEW_POINTS);

      indm2.run();

      verify(algorithm, times(1)).restart(any(), any());
      verify(algorithm).restart(referencePointChangeStrategy, problem);
      verify(problem).clearChanged();
      assertThat(indm2.status().problemChanges()).isEqualTo(1);
    }

    @Test
    @DisplayName("given only the problem changes, then the search is restarted with the problem strategy")
    void givenOnlyTheProblemChanges_thenTheSearchIsRestartedWithTheProblemStrategy() {
      when(problem.hasChanged()).thenReturn(true).thenReturn(false);

      indm2.run();

      verify(algorithm, times(1)).restart(problemChangeStrategy, problem);
      verify(algorithm, never()).updateReferencePoints(any());
    }
  }

  @Nested
  @DisplayName("When wrong reference points are given")
  class WhenWrongReferencePointsAreGiven {
    @Test
    @DisplayName("given a point without a value per objective, then it raises an exception")
    void givenAPointWithoutAValuePerObjective_thenItRaisesAnException() {
      assertThatThrownBy(() -> indm2.updateReferencePoints(List.of(List.of(0.1, 0.2, 0.3))))
          .isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given a number of points that the algorithm does not accept, then it raises an exception")
    void givenANumberOfPointsThatTheAlgorithmDoesNotAccept_thenItRaisesAnException() {
      assertThatThrownBy(
              () -> indm2.updateReferencePoints(List.of(List.of(0.1, 0.2), List.of(0.3, 0.4))))
          .isInstanceOf(InvalidConditionException.class);
    }
  }

  @Nested
  @DisplayName("When it is built with the builder")
  class WhenItIsBuiltWithTheBuilder {
    @Test
    @DisplayName("given the defaults, then it has 250 iterations per round and the name of the algorithm")
    void givenTheDefaults_thenItHasTheDefaultsOfThePaper() {
      InDM2<DoubleSolution> built = new InDM2Builder<>(algorithm, problem, 100).build();

      assertThat(built.iterationsPerRound()).isEqualTo(250);
      assertThat(built.name()).isEqualTo("InDM2-WASFGA");
    }
  }
}
