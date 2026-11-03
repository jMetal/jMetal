package org.uma.jmetal.parallel.asynchronous.algorithm.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

@DisplayName("Runs of the asynchronous NSGA-II")
class AsynchronousMultiThreadedNSGAIIRunTest {
  private static final int NUMBER_OF_CORES = 3;
  private static final int POPULATION_SIZE = 20;

  /** A problem that counts the evaluations that the workers make. */
  private static class CountingProblem extends ZDT1 {
    final AtomicInteger evaluations = new AtomicInteger();

    @Override
    public DoubleSolution evaluate(DoubleSolution solution) {
      evaluations.incrementAndGet();
      return super.evaluate(solution);
    }
  }

  /** The same object twice in the population, and not two solutions with the same values. */
  private static java.util.Set<DoubleSolution> distinctObjects(List<DoubleSolution> solutions) {
    java.util.Set<DoubleSolution> objects = Collections.newSetFromMap(new IdentityHashMap<>());
    objects.addAll(solutions);
    return objects;
  }

  private static AsynchronousMultiThreadedNSGAII<DoubleSolution> algorithm(
      ZDT1 problem, int evaluations) {
    return new AsynchronousMultiThreadedNSGAII<>(
        NUMBER_OF_CORES,
        problem,
        POPULATION_SIZE,
        new SBXCrossover(0.9, 20.0),
        new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0),
        new TerminationByEvaluations(evaluations));
  }

  @Test
  @DisplayName("given a budget equal to the population size, when running, then each solution is evaluated once")
  void givenABudgetEqualToThePopulationSize_whenRunning_thenEachSolutionIsEvaluatedOnce() {
    CountingProblem problem = new CountingProblem();
    var algorithm = algorithm(problem, POPULATION_SIZE);

    assertTimeoutPreemptively(Duration.ofSeconds(60), algorithm::run);

    // Besides the evaluations that are counted, the workers may be computing the tasks that were
    // submitted when the last results arrived: one per worker at most
    assertThat(problem.evaluations.get())
        .isBetween(POPULATION_SIZE, POPULATION_SIZE + NUMBER_OF_CORES);
    assertThat(distinctObjects(algorithm.result())).hasSize(POPULATION_SIZE);
  }

  @Test
  @DisplayName("given a longer budget, when running, then the evaluations stay in the budget and the population has no repeated solution")
  void givenALongerBudget_whenRunning_thenTheEvaluationsStayInTheBudget() {
    CountingProblem problem = new CountingProblem();
    var algorithm = algorithm(problem, 300);

    assertTimeoutPreemptively(Duration.ofSeconds(60), algorithm::run);

    List<DoubleSolution> result = algorithm.result();
    assertThat(problem.evaluations.get()).isBetween(300, 300 + NUMBER_OF_CORES);
    assertThat(result).hasSize(POPULATION_SIZE);
    assertThat(distinctObjects(result)).hasSize(POPULATION_SIZE);
  }

  @Test
  @DisplayName("given a problem whose evaluation fails, when running, then the run raises an exception and does not wait forever")
  void givenAFailingProblem_whenRunning_thenTheRunRaisesAnException() {
    var problem =
        new ZDT1() {
          @Override
          public DoubleSolution evaluate(DoubleSolution solution) {
            throw new IllegalStateException("the evaluation failed");
          }
        };
    var algorithm = algorithm(problem, 100);

    assertTimeoutPreemptively(
        Duration.ofSeconds(60), () -> assertThrows(JMetalException.class, algorithm::run));
  }
}
