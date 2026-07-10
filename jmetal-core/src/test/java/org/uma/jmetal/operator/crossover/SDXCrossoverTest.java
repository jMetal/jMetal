package org.uma.jmetal.operator.crossover;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.uma.jmetal.operator.crossover.impl.SDXCrossover;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.InvalidProbabilityValueException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * Unit tests for the {@link SDXCrossover} operator. They check that the operator does not return
 * invalid values and that its parameters are validated and stored correctly.
 *
 * @author Alejandro Santiago
 */
public class SDXCrossoverTest {
  private static final double EPSILON = 0.00000000000001;

  @Test
  public void shouldConstructorAssignTheCorrectProbabilityValue() {
    double crossoverProbability = 0.1;
    double f = 0.5;
    SDXCrossover crossover = new SDXCrossover(crossoverProbability, f);
    assertEquals(crossoverProbability, crossover.crossoverProbability(), EPSILON);
  }

  @Test
  public void shouldConstructorAssignTheCorrectFValue() {
    double f = 0.75;
    SDXCrossover crossover = new SDXCrossover(0.1, f);
    assertEquals(f, crossover.f(), EPSILON);
  }

  @Test
  public void shouldConstructorFailWhenPassedANegativeProbabilityValue() {
    assertThrows(InvalidProbabilityValueException.class, () -> new SDXCrossover(-1.1, 0.5));
  }

  @Test
  public void shouldConstructorFailWhenPassedAProbabilityValueGreaterThanOne() {
    assertThrows(InvalidProbabilityValueException.class, () -> new SDXCrossover(1.1, 0.5));
  }

  @Test
  public void shouldReturnTwoRequiredParentsAndTwoGeneratedChildren() {
    SDXCrossover crossover = new SDXCrossover(0.9, 0.5);
    assertEquals(2, crossover.numberOfRequiredParents());
    assertEquals(2, crossover.numberOfGeneratedChildren());
  }

  @Test
  public void shouldExecuteWithNullParameterThrowAnException() {
    SDXCrossover crossover = new SDXCrossover(0.1, 0.5);
    assertThrows(NullParameterException.class, () -> crossover.execute(null));
  }

  @Test
  public void shouldExecuteWithInvalidSolutionListSizeThrowAnException() {
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    SDXCrossover crossover = new SDXCrossover(0.1, 0.5);

    assertThrows(
        InvalidConditionException.class,
        () ->
            crossover.execute(
                Arrays.asList(
                    problem.createSolution(),
                    problem.createSolution(),
                    problem.createSolution())));
  }

  @Test
  public void shouldCrossingReturnTheSameVariablesWhenNoCrossoverIsAppliedPerVariable() {
    @SuppressWarnings("unchecked")
    RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class);
    // First value gates the (single) variable crossover -> 1.0 > probability, so it is skipped;
    // second value gates the swap -> 1.0 > 0.5, so no swap.
    Mockito.when(randomGenerator.getRandomValue()).thenReturn(1.0, 1.0);

    SDXCrossover crossover =
        new SDXCrossover(0.9, 0.5, new RepairDoubleSolutionWithBoundValue(), randomGenerator);
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    List<DoubleSolution> solutions =
        Arrays.asList(problem.createSolution(), problem.createSolution());

    List<DoubleSolution> newSolutions = crossover.execute(solutions);

    assertEquals(
        solutions.get(0).variables().get(0), newSolutions.get(0).variables().get(0), EPSILON);
    assertEquals(
        solutions.get(1).variables().get(0), newSolutions.get(1).variables().get(0), EPSILON);
  }

  @Test
  public void shouldCrossingTwoSingleVariableSolutionsReturnValidSolutions() {
    @SuppressWarnings("unchecked")
    RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class);
    // gate crossover (<=0.9), no equal-value reinit needed, gate swap (>0.5).
    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.2, 0.6);

    SDXCrossover crossover =
        new SDXCrossover(0.9, 0.5, new RepairDoubleSolutionWithBoundValue(), randomGenerator);
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    List<DoubleSolution> solutions =
        Arrays.asList(problem.createSolution(), problem.createSolution());

    List<DoubleSolution> newSolutions = crossover.execute(solutions);

    Bounds<Double> bounds = solutions.get(0).getBounds(0);
    assertThat(
        newSolutions.get(0).variables().get(0),
        Matchers.greaterThanOrEqualTo(bounds.getLowerBound()));
    assertThat(
        newSolutions.get(0).variables().get(0),
        Matchers.lessThanOrEqualTo(bounds.getUpperBound()));
    assertThat(
        newSolutions.get(1).variables().get(0),
        Matchers.greaterThanOrEqualTo(bounds.getLowerBound()));
    assertThat(
        newSolutions.get(1).variables().get(0),
        Matchers.lessThanOrEqualTo(bounds.getUpperBound()));
  }

  @Test
  public void shouldProducedOffspringAlwaysBeWithinBoundsForManyVariables() {
    DoubleProblem problem = new FakeDoubleProblem(20, 2, 0);
    SDXCrossover crossover = new SDXCrossover(0.9, 0.5);
    List<DoubleSolution> solutions =
        Arrays.asList(problem.createSolution(), problem.createSolution());

    List<DoubleSolution> newSolutions = crossover.execute(solutions);

    for (DoubleSolution offspring : newSolutions) {
      for (int i = 0; i < offspring.variables().size(); i++) {
        Bounds<Double> bounds = offspring.getBounds(i);
        assertThat(offspring.variables().get(i), Matchers.greaterThanOrEqualTo(bounds.getLowerBound()));
        assertThat(offspring.variables().get(i), Matchers.lessThanOrEqualTo(bounds.getUpperBound()));
      }
    }
  }
}
