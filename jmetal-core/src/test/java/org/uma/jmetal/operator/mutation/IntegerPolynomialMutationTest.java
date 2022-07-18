package org.uma.jmetal.operator.mutation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.operator.mutation.impl.IntegerPolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.impl.FakeIntegerProblem;
import org.uma.jmetal.problem.integerproblem.IntegerProblem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

public class IntegerPolynomialMutationTest {
  private static final double EPSILON = 0.00000000000001 ;

  @Test
  public void shouldConstructorWithoutParameterAssignTheDefaultValues() {
    var mutation = new IntegerPolynomialMutation() ;
    assertEquals(0.01, (Double) ReflectionTestUtils
        .getField(mutation, "mutationProbability"), EPSILON) ;
    assertEquals(20.0, (Double) ReflectionTestUtils
        .getField(mutation, "distributionIndex"), EPSILON) ;
  }

  @Test
  public void shouldConstructorWithProblemAndDistributionIndexParametersAssignTheCorrectValues() {
    IntegerProblem problem = new FakeIntegerProblem(4, 2, 0) ;
    var mutation = new IntegerPolynomialMutation(problem, 10.0) ;
    assertEquals(1.0/problem.getNumberOfVariables(), (Double) ReflectionTestUtils
        .getField(mutation, "mutationProbability"), EPSILON) ;
    assertEquals(10.0, (Double) ReflectionTestUtils
        .getField(mutation, "distributionIndex"), EPSILON) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectProbabilityValue() {
    var mutationProbability = 0.1 ;
    var mutation = new IntegerPolynomialMutation(mutationProbability, 2.0) ;
    assertEquals(mutationProbability, (Double) ReflectionTestUtils
        .getField(mutation, "mutationProbability"), EPSILON) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectDistributionIndex() {
    var distributionIndex = 15.0 ;
    var mutation = new IntegerPolynomialMutation(0.1, distributionIndex) ;
    assertEquals(distributionIndex, (Double) ReflectionTestUtils
        .getField(mutation, "distributionIndex"), EPSILON) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldConstructorFailWhenPassedANegativeProbabilityValue() {
    var mutationProbability = -0.1 ;
    new IntegerPolynomialMutation(mutationProbability, 2.0) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldConstructorFailWhenPassedANegativeDistributionIndex() {
    var distributionIndex = -0.1 ;
    new IntegerPolynomialMutation(0.1, distributionIndex) ;
  }

  @Test
  public void shouldGetMutationProbabilityReturnTheRightValue() {
    var mutation = new IntegerPolynomialMutation(0.1, 20.0) ;
    assertEquals(0.1, mutation.getMutationProbability(), EPSILON) ;
  }

  @Test
  public void shouldGetDistributionIndexReturnTheRightValue() {
    var mutation = new IntegerPolynomialMutation(0.1, 30.0) ;
    assertEquals(30.0, mutation.getDistributionIndex(), EPSILON) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldExecuteWithNullParameterThrowAnException() {
    var mutation = new IntegerPolynomialMutation(0.1, 20.0) ;

    mutation.execute(null) ;
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnTheSameSolutionIfProbabilityIsZero() {
    var mutationProbability = 0.0;
    var distributionIndex = 20.0 ;

    var mutation = new IntegerPolynomialMutation(mutationProbability, distributionIndex) ;
    IntegerProblem problem = new FakeIntegerProblem(1,2,0) ;
    var solution = problem.createSolution() ;
    var oldSolution = (IntegerSolution)solution.copy() ;

    mutation.execute(solution) ;

    assertEquals(oldSolution, solution) ;
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnTheSameSolutionIfItIsNotMutated() {
    @SuppressWarnings("unchecked")
    RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    var mutationProbability = 0.1;
    var distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(1.0) ;

    var mutation = new IntegerPolynomialMutation(mutationProbability, distributionIndex) ;
    IntegerProblem problem = new FakeIntegerProblem(1, 2, 0) ;
    var solution = problem.createSolution() ;
    var oldSolution = (IntegerSolution)solution.copy() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertEquals(oldSolution, solution) ;
    verify(randomGenerator, times(1)).getRandomValue();
  }

  @Ignore
  @Test
  public void shouldMutateASingleVariableSolutionReturnAValidSolution() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    var mutationProbability = 0.1;
    var distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.005, 0.6) ;

    var mutation = new IntegerPolynomialMutation(mutationProbability, distributionIndex) ;
    IntegerProblem problem = new FakeIntegerProblem(1,2,0) ;
    var solution = problem.createSolution() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    var bounds = solution.getBounds(0);
    assertThat(solution.variables().get(0), Matchers.greaterThanOrEqualTo(bounds.getLowerBound()));
    assertThat(solution.variables().get(0), Matchers.lessThanOrEqualTo(bounds.getUpperBound())) ;
    verify(randomGenerator, times(2)).getRandomValue();
  }

  @Ignore
  @Test
  public void shouldMutateASingleVariableSolutionReturnAnotherValidSolution() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    var mutationProbability = 0.1;
    var distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.005, 0.1) ;

    var mutation = new IntegerPolynomialMutation(mutationProbability, distributionIndex) ;
    IntegerProblem problem = new FakeIntegerProblem(1, 2, 0) ;
    var solution = problem.createSolution() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    var bounds = solution.getBounds(0);
    assertThat(solution.variables().get(0), Matchers.greaterThanOrEqualTo(bounds.getLowerBound())) ;
    assertThat(solution.variables().get(0), Matchers.lessThanOrEqualTo(bounds.getUpperBound())) ;
    verify(randomGenerator, times(2)).getRandomValue();
  }

  @Ignore
  @Test
  public void shouldMutateASingleVariableSolutionWithSameLowerAndUpperBoundsReturnTheBoundValue() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    var mutationProbability = 0.1;
    var distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.005, 0.1) ;

    var mutation = new IntegerPolynomialMutation(mutationProbability, distributionIndex) ;

    var problem = new FakeIntegerProblem(1, 2, 0) ;
    ReflectionTestUtils.setField(problem, "lowerLimit", Arrays.asList(new Integer[]{1}));
    ReflectionTestUtils.setField(problem, "upperLimit", Arrays.asList(new Integer[]{1}));

    var solution = problem.createSolution() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertEquals(1, (long)solution.variables().get(0));

    //int expectedValue = 1 ;
    //assertTrue(expectedValue == solution.variables().get(0)); ;
  }
}
