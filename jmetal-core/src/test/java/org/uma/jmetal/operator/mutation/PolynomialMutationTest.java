package org.uma.jmetal.operator.mutation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.InvalidProbabilityValueException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * Note: this class does check that the polynomial mutation operator does not return invalid
 * values, but not that it works properly (@see PolynomialMutationWorkingTest)
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class PolynomialMutationTest {
  private static final double EPSILON = 0.00000000000001 ;
  
  @Test
  public void shouldConstructorWithoutParameterAssignTheDefaultValues() {
    PolynomialMutation mutation = new PolynomialMutation() ;
    assertEquals(0.01, (Double) ReflectionTestUtils
        .getField(mutation, "mutationProbability"), EPSILON) ;
    assertEquals(20.0, (Double) ReflectionTestUtils
        .getField(mutation, "distributionIndex"), EPSILON) ;
  }

  @Test
  public void shouldConstructorWithProblemAndDistributionIndexParametersAssignTheCorrectValues() {
    DoubleProblem problem = new FakeDoubleProblem(4, 2, 0) ;
    PolynomialMutation mutation = new PolynomialMutation(1.0/problem.numberOfVariables(), 10.0) ;
    assertEquals(1.0/problem.numberOfVariables(), (Double) ReflectionTestUtils
        .getField(mutation, "mutationProbability"), EPSILON) ;
    assertEquals(10.0, (Double) ReflectionTestUtils
        .getField(mutation, "distributionIndex"), EPSILON) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectProbabilityValue() {
    double mutationProbability = 0.1 ;
    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, 2.0) ;
    assertEquals(mutationProbability, (Double) ReflectionTestUtils
        .getField(mutation, "mutationProbability"), EPSILON) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectDistributionIndex() {
    double distributionIndex = 15.0 ;
    PolynomialMutation mutation = new PolynomialMutation(0.1, distributionIndex) ;
    assertEquals(distributionIndex, (Double) ReflectionTestUtils
        .getField(mutation, "distributionIndex"), EPSILON) ;
  }

  @Test
  public void shouldConstructorFailWhenPassedANegativeProbabilityValue() {
    double mutationProbability = -0.1 ;
    assertThrows(InvalidProbabilityValueException.class, () ->new PolynomialMutation(mutationProbability, 2.0)) ;
  }

  @Test
  public void shouldConstructorFailWhenPassedAValueHigherThanOne() {
    double mutationProbability = 1.1 ;
    assertThrows(InvalidProbabilityValueException.class, () ->new PolynomialMutation(mutationProbability, 2.0)) ;
  }

  @Test
  public void shouldConstructorFailWhenPassedANegativeDistributionIndex() {
    double distributionIndex = -0.1 ;
    assertThrows(InvalidConditionException.class, () ->new PolynomialMutation(0.1, distributionIndex)) ;
  }

  @Test
  public void shouldGetMutationProbabilityReturnTheRightValue() {
    PolynomialMutation mutation = new PolynomialMutation(0.1, 20.0) ;
    assertEquals(0.1, mutation.mutationProbability(), EPSILON) ;
  }

  @Test
  public void shouldGetDistributionIndexReturnTheRightValue() {
    PolynomialMutation mutation = new PolynomialMutation(0.1, 30.0) ;
    assertEquals(30.0, mutation.getDistributionIndex(), EPSILON) ;
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnTheSameSolutionIfProbabilityIsZero() {
    double mutationProbability = 0.0;
    double distributionIndex = 20.0 ;

    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, distributionIndex) ;
    DoubleProblem problem = new FakeDoubleProblem(1, 1, 0) ;
    DoubleSolution solution = problem.createSolution() ;
    DoubleSolution oldSolution = (DoubleSolution)solution.copy() ;

    mutation.execute(solution) ;

    assertEquals(oldSolution, solution) ;
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnTheSameSolutionIfItIsNotMutated() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(1.0) ;

    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, distributionIndex) ;
    DoubleProblem problem = new FakeDoubleProblem(1,1,0) ;
    DoubleSolution solution = problem.createSolution() ;
    DoubleSolution oldSolution = (DoubleSolution)solution.copy() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertEquals(oldSolution, solution) ;
    verify(randomGenerator, times(1)).getRandomValue();
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnAValidSolution() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.005, 0.6) ;

    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, distributionIndex) ;
    DoubleProblem problem = new FakeDoubleProblem(1,1,0) ;
    DoubleSolution solution = problem.createSolution() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    Bounds<Double> bounds = solution.getBounds(0);
    assertThat(solution.variables().get(0), Matchers.greaterThanOrEqualTo(bounds.getLowerBound()));
    assertThat(solution.variables().get(0), Matchers.lessThanOrEqualTo(bounds.getUpperBound())) ;
    verify(randomGenerator, times(2)).getRandomValue();
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnAnotherValidSolution() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.005, 0.1) ;

    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, distributionIndex) ;
    DoubleProblem problem = new FakeDoubleProblem(1,1,0) ;
    DoubleSolution solution = problem.createSolution() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    Bounds<Double> bounds = solution.getBounds(0);
    assertThat(solution.variables().get(0), Matchers.greaterThanOrEqualTo(bounds.getLowerBound())) ;
    assertThat(solution.variables().get(0), Matchers.lessThanOrEqualTo(bounds.getUpperBound())) ;
    verify(randomGenerator, times(2)).getRandomValue();
  }

  @Ignore
  @Test
  public void shouldMutateASingleVariableSolutionWithSameLowerAndUpperBoundsReturnTheBoundValue() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.005, 0.1) ;

    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, distributionIndex) ;

    FakeDoubleProblem problem = new FakeDoubleProblem(1, 1, 0) ;
    List<Bounds<Double>> newBounds = new ArrayList<>(List.of(Bounds.create(1.0, 1.0))) ;

    ReflectionTestUtils.setField(problem, "bounds", newBounds);

    DoubleSolution solution = problem.createSolution() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertEquals(1.0, solution.variables().get(0), EPSILON) ;
  }
}
