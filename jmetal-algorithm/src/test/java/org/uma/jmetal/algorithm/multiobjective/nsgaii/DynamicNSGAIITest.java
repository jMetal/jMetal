package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.algorithm.dynamic.DynamicAlgorithm;
import org.uma.jmetal.algorithm.dynamic.DynamicStoppingCondition;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.multiobjective.fda.FDA2;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetal.util.restartstrategy.impl.DefaultRestartStrategy;
import org.uma.jmetal.util.restartstrategy.impl.RemoveNRandomSolutions;

@DisplayName("Unit tests for class DynamicNSGAII")
class DynamicNSGAIITest {
  private static final int POPULATION_SIZE = 20;

  private FDA2 problem;
  private DynamicNSGAII<DoubleSolution> algorithm;

  @BeforeEach
  void setUp() {
    JMetalRandom.getInstance().setSeed(1);
    problem = new FDA2(31, 2, 2, 10);
    algorithm =
        new DynamicNSGAII<>(
            problem,
            POPULATION_SIZE,
            POPULATION_SIZE,
            new SBXCrossover(0.9, 20.0),
            new PolynomialMutation(1.0 / 31, 20.0),
            new BinaryTournamentSelection<>(),
            new SequentialSolutionListEvaluator<>(),
            new DefaultRestartStrategy<>(
                new RemoveNRandomSolutions<>(POPULATION_SIZE / 2), new CreateNRandomSolutions<>()),
            10);
  }

  @Nested
  @DisplayName("When it runs with the defaults")
  class WhenItRunsWithTheDefaults {
    @Test
    @DisplayName("given a problem that changes every 2 generations, then it ends after 30 changes")
    void givenAProblemThatChangesEveryTwoGenerations_thenItEndsAfterThirtyChanges() {
      algorithm.run();

      assertThat(algorithm.status().problemChanges()).isEqualTo(30);
      assertThat(algorithm.status().iterations()).isEqualTo(60);
      assertThat(algorithm.status().evaluations())
          .isEqualTo(POPULATION_SIZE + 60L * POPULATION_SIZE + 30L * POPULATION_SIZE);
      assertThat(problem.time()).isEqualTo(3.0);
      assertThat(algorithm.result()).isNotEmpty();
    }

    @Test
    @DisplayName("given problem changes are notified, then the observers receive the front before each change")
    void givenProblemChangesAreNotified_thenTheObserversReceiveTheFrontBeforeEachChange() {
      List<Map<String, Object>> notifications = new ArrayList<>();
      algorithm.notifyProblemChanges(true);
      algorithm.observable().register((observable, data) -> notifications.add(data));

      algorithm.run();

      assertThat(
              notifications.stream()
                  .filter(data -> DynamicAlgorithm.PROBLEM_CHANGE.equals(data.get("EVENT"))))
          .hasSize(30);
    }

    @Test
    @DisplayName("given another stopping condition, then it ends when it is met")
    void givenAnotherStoppingCondition_thenItEndsWhenItIsMet() {
      algorithm.stoppingCondition(DynamicStoppingCondition.afterIterations(15));

      algorithm.run();

      assertThat(algorithm.status().iterations()).isEqualTo(15);
      assertThat(algorithm.status().problemChanges()).isEqualTo(7);
    }
  }
}
