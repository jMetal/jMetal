package org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.impl.ImprovedEpsilonCriterion;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;

@DisplayName("Unit tests for class ImprovedEpsilonCriterion")
class ImprovedEpsilonCriterionTest {

  private static DoubleSolution solutionWithViolation(double violation) {
    DoubleSolution solution = new FakeDoubleProblem(2, 2, 1).createSolution();
    // The violation degree is negative in jMetal, and zero if the solution is feasible
    ConstraintHandling.overallConstraintViolationDegree(solution, -violation);
    ConstraintHandling.numberOfViolatedConstraints(solution, violation > 0 ? 1 : 0);
    return solution;
  }

  private static List<DoubleSolution> population(int size) {
    List<DoubleSolution> population = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      population.add(solutionWithViolation(i + 1));
    }
    return population;
  }

  @ParameterizedTest(name = "population of {0}")
  @ValueSource(ints = {1, 2, 19, 20, 21, 60, 100})
  @DisplayName("given a population of any size, when the criterion is updated, then it does not fail")
  void givenAPopulationOfAnySize_whenTheCriterionIsUpdated_thenItDoesNotFail(int size) {
    var criterion = new ImprovedEpsilonCriterion<DoubleSolution>(10);
    List<DoubleSolution> population = population(size);

    criterion.update(population, solutionWithViolation(0.5));

    assertThat(criterion.replaces(solutionWithViolation(0.0), 1.0, population.get(0), 2.0)).isTrue();
  }

  @Test
  @DisplayName("given a new solution with less violation, when it is outside the epsilon level, then it replaces the current one")
  void givenALessViolatedSolution_whenItIsOutsideTheEpsilonLevel_thenItReplaces() {
    var criterion = new ImprovedEpsilonCriterion<DoubleSolution>(10);
    DoubleSolution current = solutionWithViolation(5.0);
    DoubleSolution better = solutionWithViolation(1.0);

    // The aggregation value of the new solution is worse, but its violation is smaller
    assertThat(criterion.replaces(better, 10.0, current, 1.0)).isTrue();
    assertThat(criterion.replaces(current, 1.0, better, 10.0)).isFalse();
  }

  @Test
  @DisplayName("given solutions with the same violation, then the aggregation value decides")
  void givenTheSameViolation_whenComparing_thenTheAggregationValueDecides() {
    var criterion = new ImprovedEpsilonCriterion<DoubleSolution>(10);

    assertThat(criterion.replaces(solutionWithViolation(2.0), 1.0, solutionWithViolation(2.0), 3.0)).isTrue();
    assertThat(criterion.replaces(solutionWithViolation(2.0), 3.0, solutionWithViolation(2.0), 1.0)).isFalse();
  }
}
