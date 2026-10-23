package org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.constrainthandling.ConstrainedDominance;
import org.uma.jmetal.util.constrainthandling.EpsilonConstrained;

@DisplayName("Unit tests for class ConstraintHandlingCriterion")
class ConstraintHandlingCriterionTest {

  private static DoubleSolution solution(double violation) {
    var solution = new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), 2, 1);
    solution.constraints()[0] = -violation;
    return solution;
  }

  @Nested
  @DisplayName("When deciding a replacement")
  class WhenDecidingAReplacement {
    @ParameterizedTest(name = "violations {0} and {1}, aggregation values {2} and {3}")
    @CsvSource({
      "0.0, 0.0, 1.0, 2.0",
      "0.0, 0.0, 2.0, 1.0",
      "0.0, 0.0, 1.0, 1.0",
      "0.0, 0.5, 9.0, 1.0",
      "0.5, 0.0, 1.0, 9.0",
      "0.2, 0.5, 9.0, 1.0",
      "0.5, 0.2, 1.0, 9.0",
      "0.5, 0.5, 1.0, 2.0",
      "0.5, 0.5, 2.0, 1.0"
    })
    @DisplayName("given CDP, then it decides as the feasibility rules")
    void givenTheConstrainedDominationPrinciple_whenDeciding_thenItDecidesAsTheFeasibilityRules(
        double newViolation, double currentViolation, double newValue, double currentValue) {
      // Arrange
      var criterion = new ConstraintHandlingCriterion<>(new ConstrainedDominance<>(), () -> 0.5);
      var newSolution = solution(newViolation);
      var currentSolution = solution(currentViolation);

      // Act
      boolean replaces = criterion.replaces(newSolution, newValue, currentSolution, currentValue);

      // Assert
      assertThat(replaces)
          .isEqualTo(
              new FeasibilityRulesCriterion<DoubleSolution>()
                  .replaces(newSolution, newValue, currentSolution, currentValue));
    }

    @Test
    @DisplayName("given epsilon, then a solution within epsilon is compared by aggregation value")
    void givenEpsilon_whenDeciding_thenASolutionWithinEpsilonIsComparedByAggregationValue() {
      // Arrange: initial epsilon 1.0, the largest violation, and 0.5 at the middle of the run
      var technique = new EpsilonConstrained<DoubleSolution>(1.0, 1.0, 1.0);
      var criterion = new ConstraintHandlingCriterion<>(technique, () -> 0.5);
      criterion.update(List.of(solution(0.0), solution(1.0)), solution(0.0));

      // Act & Assert
      assertThat(criterion.replaces(solution(0.4), 1.0, solution(0.0), 2.0)).isTrue();
      assertThat(criterion.replaces(solution(0.6), 1.0, solution(0.0), 2.0)).isFalse();
    }
  }

  @Nested
  @DisplayName("When updating")
  class WhenUpdating {
    @Test
    @DisplayName("given a population, then the technique is updated once per generation")
    void givenAPopulation_whenUpdating_thenTheTechniqueIsUpdatedOncePerGeneration() {
      // Arrange
      List<Double> progresses = new ArrayList<>();
      double[] progress = {0.0};
      var technique =
          new ConstrainedDominance<DoubleSolution>() {
            @Override
            public void update(List<DoubleSolution> population, double runProgress) {
              progresses.add(runProgress);
            }
          };
      var criterion = new ConstraintHandlingCriterion<>(technique, () -> progress[0]);
      List<DoubleSolution> population = List.of(solution(0), solution(0), solution(0));

      // Act: seven new solutions with a population of three
      for (int i = 0; i < 7; i++) {
        progress[0] = i / 10.0;
        criterion.update(population, solution(0));
      }

      // Assert
      assertThat(progresses).containsExactly(0.0, 0.3, 0.6);
      assertThat(criterion.technique()).isSameAs(technique);
    }
  }
}
