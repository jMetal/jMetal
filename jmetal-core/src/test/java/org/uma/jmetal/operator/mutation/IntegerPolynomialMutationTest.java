package org.uma.jmetal.operator.mutation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.uma.jmetal.solution.doublesolution.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.operator.mutation.impl.IntegerPolynomialMutation;
import org.uma.jmetal.problem.integerproblem.IntegerProblem;
import org.uma.jmetal.problem.integerproblem.impl.FakeIntegerProblem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

public class IntegerPolynomialMutationTest {
  private static final double EPSILON = 0.00000000000001 ;

  @Test
  public void shouldConstructorWithoutParameterAssignTheDefaultValues() {
    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation() ;
    assertEquals(0.01, mutation.mutationProbability(), EPSILON) ;
    assertEquals(20.0, mutation.getDistributionIndex(), EPSILON) ;
  }

  @Test
  public void shouldConstructorWithProblemAndDistributionIndexParametersAssignTheCorrectValues() {
    IntegerProblem problem = new FakeIntegerProblem(4, 2, 0) ;
    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(problem, 10.0) ;
    assertEquals(1.0/problem.numberOfVariables(), mutation.mutationProbability(), EPSILON) ;
    assertEquals(10.0, mutation.getDistributionIndex(), EPSILON) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectProbabilityValue() {
    double mutationProbability = 0.1 ;
    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(mutationProbability, 2.0) ;
    assertEquals(mutationProbability, mutation.mutationProbability(), EPSILON) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectDistributionIndex() {
    double distributionIndex = 15.0 ;
    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(0.1, distributionIndex) ;
    assertEquals(distributionIndex, mutation.getDistributionIndex(), EPSILON) ;
  }

  @Test
  public void shouldConstructorFailWhenPassedANegativeProbabilityValue() {
    double mutationProbability = -0.1 ;
    assertThrows(JMetalException.class, () -> new IntegerPolynomialMutation(mutationProbability, 2.0));
  }

  @Test
  public void shouldConstructorFailWhenPassedANegativeDistributionIndex() {
    double distributionIndex = -0.1 ;
    assertThrows(JMetalException.class, () -> new IntegerPolynomialMutation(0.1, distributionIndex));
  }

  @Test
  public void shouldGetMutationProbabilityReturnTheRightValue() {
    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(0.1, 20.0) ;
    assertEquals(0.1, mutation.mutationProbability(), EPSILON) ;
  }

  @Test
  public void shouldGetDistributionIndexReturnTheRightValue() {
    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(0.1, 30.0) ;
    assertEquals(30.0, mutation.getDistributionIndex(), EPSILON) ;
  }

  @Test
  public void shouldExecuteWithNullParameterThrowAnException() {
    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(0.1, 20.0) ;

    assertThrows(JMetalException.class, () -> mutation.execute(null));
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnTheSameSolutionIfProbabilityIsZero() {
    double mutationProbability = 0.0;
    double distributionIndex = 20.0 ;

    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(mutationProbability, distributionIndex) ;
    IntegerProblem problem = new FakeIntegerProblem(1,2,0) ;
    IntegerSolution solution = problem.createSolution() ;
    IntegerSolution oldSolution = (IntegerSolution)solution.copy() ;

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

    // create mutation with a custom random generator to control behavior in test
    IntegerProblem problem = new FakeIntegerProblem(1, 2, 0) ;
    IntegerSolution solution = problem.createSolution() ;
    IntegerSolution oldSolution = (IntegerSolution)solution.copy() ;

    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(mutationProbability, distributionIndex, new RepairDoubleSolutionWithBoundValue(), randomGenerator) ;

    mutation.execute(solution) ;

    assertEquals(oldSolution, solution) ;
    verify(randomGenerator, times(1)).getRandomValue();
  }

  @Disabled
  @Test
  public void shouldMutateASingleVariableSolutionReturnAValidSolution() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.005, 0.6) ;

    // create mutation with a custom random generator to control behavior in test
    IntegerProblem problem = new FakeIntegerProblem(1,2,0) ;
    IntegerSolution solution = problem.createSolution() ;

    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(mutationProbability, distributionIndex, new RepairDoubleSolutionWithBoundValue(), randomGenerator) ;

    mutation.execute(solution) ;

    Bounds<Integer> bounds = solution.getBounds(0);
    assertThat(solution.variables().get(0), Matchers.greaterThanOrEqualTo(bounds.getLowerBound()));
    assertThat(solution.variables().get(0), Matchers.lessThanOrEqualTo(bounds.getUpperBound())) ;
    verify(randomGenerator, times(2)).getRandomValue();
  }

  @Disabled
  @Test
  public void shouldMutateASingleVariableSolutionReturnAnotherValidSolution() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.005, 0.1) ;

    // create mutation with a custom random generator to control behavior in test
    IntegerProblem problem = new FakeIntegerProblem(1, 2, 0) ;
    IntegerSolution solution = problem.createSolution() ;

    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(mutationProbability, distributionIndex, new RepairDoubleSolutionWithBoundValue(), randomGenerator) ;

    mutation.execute(solution) ;

    Bounds<Integer> bounds = solution.getBounds(0);
    assertThat(solution.variables().get(0), Matchers.greaterThanOrEqualTo(bounds.getLowerBound())) ;
    assertThat(solution.variables().get(0), Matchers.lessThanOrEqualTo(bounds.getUpperBound())) ;
    verify(randomGenerator, times(2)).getRandomValue();
  }

  @Disabled
  @Test
  public void shouldMutateASingleVariableSolutionWithSameLowerAndUpperBoundsReturnTheBoundValue() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.005, 0.1) ;

    FakeIntegerProblem problem = new FakeIntegerProblem(1, 2, 0, 1, 1) ;

    IntegerSolution solution = problem.createSolution() ;

    IntegerPolynomialMutation mutation = new IntegerPolynomialMutation(mutationProbability, distributionIndex, new RepairDoubleSolutionWithBoundValue(), randomGenerator) ;

    mutation.execute(solution) ;

    assertEquals(1, (long)solution.variables().get(0));

    //int expectedValue = 1 ;
    //assertTrue(expectedValue == solution.variables().get(0)); ;
  }
}
