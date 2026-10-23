package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.constrainthandling.ConstrainedDominance;

@DisplayName("Unit tests for class ConstraintHandlingReplacement")
class ConstraintHandlingReplacementTest {
  private List<List<DoubleSolution>> updates;
  private List<Double> progresses;
  private ConstrainedDominance<DoubleSolution> technique;
  private List<DoubleSolution> population;
  private List<DoubleSolution> offspring;
  private List<DoubleSolution> replaced;
  private Replacement<DoubleSolution> replacement;

  private static DoubleSolution solution() {
    return new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), 2, 1);
  }

  @BeforeEach
  void setUp() {
    updates = new ArrayList<>();
    progresses = new ArrayList<>();
    technique =
        new ConstrainedDominance<>() {
          @Override
          public void update(List<DoubleSolution> solutions, double progress) {
            updates.add(solutions);
            progresses.add(progress);
          }
        };
    population = List.of(solution(), solution());
    offspring = List.of(solution());
    replaced = List.of(solution(), solution());
    replacement = (currentPopulation, offspringPopulation) -> replaced;
  }

  @Nested
  @DisplayName("When replacing")
  class WhenReplacing {
    @Test
    @DisplayName("given a technique, then it is updated with the union and the progress first")
    void givenATechnique_whenReplacing_thenItIsUpdatedWithTheUnionAndTheProgressFirst() {
      // Arrange
      var progress = new AtomicReference<>(0.25);
      AtomicReference<Integer> updatesWhenReplacing = new AtomicReference<>();
      Replacement<DoubleSolution> recording =
          (currentPopulation, offspringPopulation) -> {
            updatesWhenReplacing.set(updates.size());
            return replaced;
          };
      var constraintHandling =
          new ConstraintHandlingReplacement<>(technique, recording, progress::get);

      // Act
      List<DoubleSolution> result = constraintHandling.replace(population, offspring);

      // Assert
      assertThat(result).isSameAs(replaced);
      assertThat(updatesWhenReplacing.get()).isEqualTo(1);
      assertThat(updates.getFirst()).containsExactly(population.get(0), population.get(1),
          offspring.get(0));
      assertThat(progresses).containsExactly(0.25);
    }

    @Test
    @DisplayName("given a period, then the technique is updated once every period replacements")
    void givenAPeriod_whenReplacing_thenTheTechniqueIsUpdatedOnceEveryPeriodReplacements() {
      // Arrange
      double[] progress = {0.0};
      var constraintHandling =
          new ConstraintHandlingReplacement<>(technique, replacement, () -> progress[0], 3);

      // Act
      for (int i = 0; i < 7; i++) {
        progress[0] = i / 10.0;
        constraintHandling.replace(population, offspring);
      }

      // Assert: at the replacements 0, 3 and 6
      assertThat(progresses).containsExactly(0.0, 0.3, 0.6);
    }

    @Test
    @DisplayName("given a function of the candidates, then the technique is updated with them")
    void givenAFunctionOfTheCandidates_whenReplacing_thenTheTechniqueIsUpdatedWithThem() {
      // Arrange
      List<DoubleSolution> others = List.of(solution());
      var constraintHandling =
          new ConstraintHandlingReplacement<>(
              technique, replacement, () -> 0.5, 1, (current, offspringPopulation) -> others);

      // Act
      constraintHandling.replace(population, offspring);

      // Assert
      assertThat(updates).containsExactly(others);
      assertThat(constraintHandling.technique()).isSameAs(technique);
    }
  }
}
