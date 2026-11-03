package org.uma.jmetal.util.restartstrategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetal.util.restartstrategy.impl.DefaultRestartStrategy;
import org.uma.jmetal.util.restartstrategy.impl.RemoveFirstNSolutions;
import org.uma.jmetal.util.restartstrategy.impl.RemoveNRandomSolutions;
import org.uma.jmetal.util.restartstrategy.impl.RemoveNSolutionsAccordingToTheCrowdingDistance;
import org.uma.jmetal.util.restartstrategy.impl.RemoveNSolutionsAccordingToTheHypervolumeContribution;

@DisplayName("Unit tests for the restart strategies")
class RestartStrategiesTest {
  private static final int POPULATION_SIZE = 10;

  private DynamicProblem<DoubleSolution, Object> problem;

  @SuppressWarnings("unchecked")
  @BeforeEach
  void setUp() {
    problem = mock(DynamicProblem.class);
    when(problem.createSolution()).thenAnswer(invocation -> solution(5.0, 5.0));
  }

  private static DoubleSolution solution(double f1, double f2) {
    DoubleSolution solution = new FakeDoubleProblem(2, 2, 0).createSolution();
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    return solution;
  }

  /** Ten non-dominated solutions on the line f1 + f2 = 1. */
  private static List<DoubleSolution> population() {
    return IntStream.range(0, POPULATION_SIZE)
        .mapToObj(i -> solution(i / 9.0, 1.0 - i / 9.0))
        .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
  }

  private static Supplier<RemoveSolutionsStrategy<DoubleSolution>> crowding(int n) {
    return () -> new RemoveNSolutionsAccordingToTheCrowdingDistance<>(n);
  }

  private static Supplier<RemoveSolutionsStrategy<DoubleSolution>> hypervolume(int n) {
    return () -> new RemoveNSolutionsAccordingToTheHypervolumeContribution<>(n);
  }

  @ParameterizedTest(name = "{0} solutions removed")
  @ValueSource(ints = {1, 3, 9})
  @DisplayName("given a strategy based on an archive, when restarting, then the population keeps its size")
  void givenArchiveStrategies_whenRestarting_thenThePopulationKeepsItsSize(int n) {
    for (var strategy : List.of(crowding(n).get(), hypervolume(n).get())) {
      // Arrange
      List<DoubleSolution> population = population();
      var restart = new DefaultRestartStrategy<>(strategy, new CreateNRandomSolutions<>());

      // Act
      restart.restart(population, problem);

      // Assert
      assertThat(population).as(strategy.getClass().getSimpleName()).hasSize(POPULATION_SIZE);
    }
  }

  @Test
  @DisplayName("given dominated solutions, when removing by crowding distance, then the dominated ones count as removed")
  void givenDominatedSolutions_whenRemoving_thenTheDominatedOnesCountAsRemoved() {
    // Arrange: two dominated solutions in a population of ten
    List<DoubleSolution> population = population();
    population.set(4, solution(0.9, 0.9));
    population.set(5, solution(0.8, 0.8));

    // Act
    int removed = new RemoveNSolutionsAccordingToTheCrowdingDistance<DoubleSolution>(1)
        .remove(population, problem);

    // Assert: the 8 non-dominated solutions fit in the 9 that remain, so 2 were removed
    assertThat(removed).isEqualTo(POPULATION_SIZE - population.size()).isEqualTo(2);
  }

  @Test
  @DisplayName("given a list, when removing the first solutions, then they are the first ones")
  void givenAList_whenRemovingTheFirstSolutions_thenTheyAreTheFirstOnes() {
    List<DoubleSolution> population = population();
    DoubleSolution fourth = population.get(3);

    int removed = new RemoveFirstNSolutions<DoubleSolution>(3).remove(population, problem);

    assertThat(removed).isEqualTo(3);
    assertThat(population).hasSize(7);
    assertThat(population.get(0)).isSameAs(fourth);
  }

  @Test
  @DisplayName("given more solutions to remove than the list has, when removing randomly, then an exception is raised")
  void givenTooManySolutionsToRemove_whenRemovingRandomly_thenAnExceptionIsRaised() {
    List<DoubleSolution> population = population();

    assertThrows(
        InvalidConditionException.class,
        () -> new RemoveNRandomSolutions<DoubleSolution>(POPULATION_SIZE + 1)
            .remove(population, problem));
    assertThat(population).hasSize(POPULATION_SIZE);
  }

  @Test
  @DisplayName("given solutions to remove, when removing randomly, then that number is removed")
  void givenSolutionsToRemove_whenRemovingRandomly_thenThatNumberIsRemoved() {
    List<DoubleSolution> population = population();

    int removed = new RemoveNRandomSolutions<DoubleSolution>(POPULATION_SIZE).remove(population, problem);

    assertThat(removed).isEqualTo(POPULATION_SIZE);
    assertThat(population).isEmpty();
  }
}
