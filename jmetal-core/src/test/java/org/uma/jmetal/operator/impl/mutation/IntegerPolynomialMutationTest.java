package org.uma.jmetal.operator.impl.mutation;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.problem.IntegerProblem;
import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.impl.DefaultIntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class IntegerPolynomialMutationTest {
  private static final double EPSILON = 0.00000000000001 ;

  @Test
  public void shouldConstructorWithoutParameterAssignTheDefaultValues() {
    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation() ;
    assertEquals(0.01, (Double) ReflectionTestUtils
        .getField(mutation, "mutationProbability"), EPSILON) ;
    assertEquals(20.0, (Double) ReflectionTestUtils
        .getField(mutation, "distributionIndex"), EPSILON) ;
  }

  @Test
  public void shouldConstructorWithProblemAndDistributionIndexParametersAssignTheCorrectValues() {
    IntegerProblem problem = new MockIntegerProblem(4) ;
    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(problem, 10.0) ;
    assertEquals(1.0/problem.getNumberOfVariables(), (Double) ReflectionTestUtils
        .getField(mutation, "mutationProbability"), EPSILON) ;
    assertEquals(10.0, (Double) ReflectionTestUtils
        .getField(mutation, "distributionIndex"), EPSILON) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectProbabilityValue() {
    double mutationProbability = 0.1 ;
    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(mutationProbability, 2.0) ;
    assertEquals(mutationProbability, (Double) ReflectionTestUtils
        .getField(mutation, "mutationProbability"), EPSILON) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectDistributionIndex() {
    double distributionIndex = 15.0 ;
    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(0.1, distributionIndex) ;
    assertEquals(distributionIndex, (Double) ReflectionTestUtils
        .getField(mutation, "distributionIndex"), EPSILON) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldConstructorFailWhenPassedANegativeProbabilityValue() {
    double mutationProbability = -0.1 ;
    new IntegerPolynomialMutation(mutationProbability, 2.0) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldConstructorFailWhenPassedANegativeDistributionIndex() {
    double distributionIndex = -0.1 ;
    new IntegerPolynomialMutation(0.1, distributionIndex) ;
  }

  @Test
  public void shouldGetMutationProbabilityReturnTheRightValue() {
    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(0.1, 20.0) ;
    assertEquals(0.1, mutation.getMutationProbability(), EPSILON) ;
  }

  @Test
  public void shouldGetDistributionIndexReturnTheRightValue() {
    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(0.1, 30.0) ;
    assertEquals(30.0, mutation.getDistributionIndex(), EPSILON) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldExecuteWithNullParameterThrowAnException() {
    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(0.1, 20.0) ;

    mutation.execute(null) ;
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnTheSameSolutionIfProbabilityIsZero() {
    double mutationProbability = 0.0;
    double distributionIndex = 20.0 ;

    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(mutationProbability, distributionIndex) ;
    IntegerProblem problem = new MockIntegerProblem(1) ;
    IntegerSolution solution = problem.createSolution() ;
    IntegerSolution oldSolution = (IntegerSolution)solution.copy() ;

    mutation.execute(solution) ;

    assertEquals(oldSolution, solution) ;
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnTheSameSolutionIfItIsNotMutated() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(1.0) ;

    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(mutationProbability, distributionIndex) ;
    IntegerProblem problem = new MockIntegerProblem(1) ;
    IntegerSolution solution = problem.createSolution() ;
    IntegerSolution oldSolution = (IntegerSolution)solution.copy() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertEquals(oldSolution, solution) ;
    verify(randomGenerator, times(1)).nextDouble();
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnAValidSolution() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(0.005, 0.6) ;

    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(mutationProbability, distributionIndex) ;
    IntegerProblem problem = new MockIntegerProblem(1) ;
    IntegerSolution solution = problem.createSolution() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertThat(solution.getVariableValue(0), Matchers
        .greaterThanOrEqualTo(solution.getLowerBound(0))) ;
    assertThat(solution.getVariableValue(0), Matchers.lessThanOrEqualTo(solution.getUpperBound(0))) ;
    verify(randomGenerator, times(2)).nextDouble();
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnAnotherValidSolution() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(0.005, 0.1) ;

    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(mutationProbability, distributionIndex) ;
    IntegerProblem problem = new MockIntegerProblem(1) ;
    IntegerSolution solution = problem.createSolution() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertThat(solution.getVariableValue(0), Matchers.greaterThanOrEqualTo(solution.getLowerBound(0))) ;
    assertThat(solution.getVariableValue(0), Matchers.lessThanOrEqualTo(solution.getUpperBound(0))) ;
    verify(randomGenerator, times(2)).nextDouble();
  }

  @Test
  public void shouldMutateASingleVariableSolutionWithSameLowerAndUpperBoundsReturnTheBoundValue() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(0.005, 0.1) ;

    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(mutationProbability, distributionIndex) ;

    MockIntegerProblem problem = new MockIntegerProblem(1) ;
    ReflectionTestUtils.setField(problem, "lowerLimit", Arrays.asList(new Integer[]{1}));
    ReflectionTestUtils.setField(problem, "upperLimit", Arrays.asList(new Integer[]{1}));

    IntegerSolution solution = problem.createSolution() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertEquals(1, (long)solution.getVariableValue(0));

    //int expectedValue = 1 ;
    //assertTrue(expectedValue == solution.getVariableValue(0)); ;
  }

  /**
   * Mock class representing an Integer problem
   */
  @SuppressWarnings("serial")
  private class MockIntegerProblem extends AbstractIntegerProblem {

    /** Constructor */
    public MockIntegerProblem(Integer numberOfVariables) {
      setNumberOfVariables(numberOfVariables);
      setNumberOfObjectives(2);

      List<Integer> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
      List<Integer> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(-4);
        upperLimit.add(4);
      }

      setLowerLimit(lowerLimit);
      setUpperLimit(upperLimit);
    }

    @Override
    public IntegerSolution createSolution() {
      return new DefaultIntegerSolution(this) ;
    }

    /** Evaluate() method */
    @Override
    public void evaluate(IntegerSolution solution) {
      solution.setObjective(0, 4);
      solution.setObjective(1, 2);
    }
  }
}
