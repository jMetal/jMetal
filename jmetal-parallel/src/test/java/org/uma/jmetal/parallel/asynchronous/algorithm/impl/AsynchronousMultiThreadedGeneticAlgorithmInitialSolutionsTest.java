package org.uma.jmetal.parallel.asynchronous.algorithm.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("Unit tests for the initial solutions of AsynchronousMultiThreadedGeneticAlgorithm")
class AsynchronousMultiThreadedGeneticAlgorithmInitialSolutionsTest {

  /** ZDT1 that counts how many random solutions it creates. */
  private static final class CountingZdt1 extends ZDT1 {
    final AtomicInteger randomSolutions = new AtomicInteger();

    CountingZdt1() {
      super(5);
    }

    @Override
    public DoubleSolution createSolution() {
      randomSolutions.incrementAndGet();
      return super.createSolution();
    }
  }

  private static AsynchronousMultiThreadedNSGAII<DoubleSolution> nsgaII(
      DoubleProblem problem, int cores, int populationSize, int evaluations) {
    return new AsynchronousMultiThreadedNSGAII<>(
        cores,
        problem,
        populationSize,
        new SBXCrossover(0.9, 20.0),
        new PolynomialMutation(0.2, 20.0),
        new TerminationByEvaluations(evaluations));
  }

  @Test
  @DisplayName("given an initial solutions creation, when running, then no random solution is made")
  void givenInitialSolutionsCreation_whenRunning_thenNoRandomSolutionIsCreated() {
    // Arrange: more cores than solutions, so tasks are needed before parents can be selected
    CountingZdt1 problem = new CountingZdt1();
    List<DoubleSolution> seeds = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      seeds.add(new ZDT1(5).createSolution());
    }
    AtomicInteger creations = new AtomicInteger();
    var algorithm = nsgaII(problem, 8, 4, 200);
    algorithm.setInitialSolutionsCreation(
        () -> {
          creations.incrementAndGet();
          return seeds.stream().map(seed -> (DoubleSolution) seed.copy()).toList();
        });

    // Act
    algorithm.run();

    // Assert
    assertEquals(0, problem.randomSolutions.get());
    assertTrue(creations.get() >= 1);
    assertEquals(4, algorithm.result().size());
  }

  @Test
  @DisplayName("given no creation, when creating the initial tasks, then they are random")
  void givenNoCreation_whenCreatingInitialTasks_thenTheyAreRandom() {
    // Arrange
    CountingZdt1 problem = new CountingZdt1();

    // Act
    int tasks = nsgaII(problem, 1, 6, 10).createInitialTasks().size();

    // Assert
    assertEquals(6, tasks);
    assertEquals(6, problem.randomSolutions.get());
  }

  @Test
  @DisplayName("given an empty creation, when creating the initial tasks, then it fails")
  void givenEmptyCreation_whenCreatingInitialTasks_thenItFails() {
    // Arrange
    var algorithm = nsgaII(new CountingZdt1(), 1, 6, 10);
    algorithm.setInitialSolutionsCreation(List::of);

    // Act & Assert
    assertThrows(RuntimeException.class, algorithm::createInitialTasks);
  }
}
