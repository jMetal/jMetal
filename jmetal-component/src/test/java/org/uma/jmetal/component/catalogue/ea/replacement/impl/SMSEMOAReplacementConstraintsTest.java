package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

@DisplayName("Unit tests for class SMSEMOAReplacement on problems with constraints")
class SMSEMOAReplacementConstraintsTest {

  private static DoubleSolution solution(
      double f1, double f2, int numberOfConstraints, double violation) {
    DoubleSolution solution =
        new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), 2, numberOfConstraints);
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    if (numberOfConstraints > 0) {
      solution.constraints()[0] = violation;
    }
    return solution;
  }

  private static SMSEMOAReplacement<DoubleSolution> constrainedReplacement() {
    return new SMSEMOAReplacement<>(
        new FastNonDominatedSortRanking<>(new DominanceWithConstraintsComparator<>()));
  }

  @Nested
  @DisplayName("When the ranking compares with the constrained-domination principle")
  class WhenTheRankingComparesWithConstrainedDomination {
    @Test
    @DisplayName(
        "given a feasible population, when an infeasible offspring that dominates it in the"
            + " objectives is inserted, then the offspring is removed")
    void givenAFeasiblePopulation_whenABetterInfeasibleOffspringIsInserted_thenItIsRemoved() {
      // Arrange
      SMSEMOAReplacement<DoubleSolution> replacement = constrainedReplacement();
      List<DoubleSolution> population =
          List.of(solution(0.2, 0.8, 1, 0.0), solution(0.5, 0.5, 1, 0.0), solution(0.8, 0.2, 1, 0.0));
      DoubleSolution infeasible = solution(0.1, 0.1, 1, -1.0);

      // Act
      List<DoubleSolution> result = replacement.replace(population, List.of(infeasible));

      // Assert
      assertThat(result).containsExactlyInAnyOrderElementsOf(population);
    }

    @Test
    @DisplayName(
        "given an infeasible population, when offspring are inserted, then the most violated"
            + " solution is removed each time")
    void givenAnInfeasiblePopulation_whenOffspringAreInserted_thenTheMostViolatedIsRemoved() {
      // Arrange
      SMSEMOAReplacement<DoubleSolution> replacement = constrainedReplacement();
      DoubleSolution slightlyViolated = solution(0.9, 0.9, 1, -0.1);
      DoubleSolution badlyViolated = solution(0.1, 0.1, 1, -5.0);
      List<DoubleSolution> population = List.of(slightlyViolated, badlyViolated);
      DoubleSolution feasible = solution(1.0, 1.0, 1, 0.0);

      // Act
      List<DoubleSolution> result = replacement.replace(population, List.of(feasible));

      // Assert
      assertThat(result).containsExactlyInAnyOrder(feasible, slightlyViolated);
    }
  }

  @Nested
  @DisplayName("When choosing how to update the fronts")
  class WhenChoosingHowToUpdateTheFronts {
    @Test
    @DisplayName("given solutions without constraints, then the incremental update is kept")
    void givenSolutionsWithoutConstraints_whenChecked_thenTheyDoNotUseConstraints() {
      // Arrange
      SMSEMOAReplacement<DoubleSolution> replacement = constrainedReplacement();

      // Act & Assert
      assertThat(replacement.usesConstraints(solution(0.5, 0.5, 0, 0.0))).isFalse();
    }

    @Test
    @DisplayName("given solutions with constraints, then the population is ranked in full")
    void givenSolutionsWithConstraints_whenChecked_thenTheyUseConstraints() {
      // Arrange
      SMSEMOAReplacement<DoubleSolution> replacement = constrainedReplacement();

      // Act & Assert
      assertThat(replacement.usesConstraints(solution(0.5, 0.5, 1, 0.0))).isTrue();
    }
  }
}
