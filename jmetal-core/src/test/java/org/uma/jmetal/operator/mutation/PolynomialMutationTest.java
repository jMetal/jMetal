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
    var mutation = new PolynomialMutation() ;
    assertEquals(0.01, (Double) ReflectionTestUtils
        .getField(mutation, "mutationProbability"), EPSILON) ;
    assertEquals(20.0, (Double) ReflectionTestUtils
        .getField(mutation, "distributionIndex"), EPSILON) ;
  }

  @Test
  public void shouldConstructorWithProblemAndDistributionIndexParametersAssignTheCorrectValues() {
    DoubleProblem problem = new FakeDoubleProblem(4, 2, 0) ;
    var mutation = new PolynomialMutation(1.0/problem.getNumberOfVariables(), 10.0) ;
    assertEquals(1.0/problem.getNumberOfVariables(), (Double) ReflectionTestUtils
        .getField(mutation, "mutationProbability"), EPSILON) ;
    assertEquals(10.0, (Double) ReflectionTestUtils
        .getField(mutation, "distributionIndex"), EPSILON) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectProbabilityValue() {
    var mutationProbability = 0.1 ;
    var mutation = new PolynomialMutation(mutationProbability, 2.0) ;
    assertEquals(mutationProbability, (Double) ReflectionTestUtils
        .getField(mutation, "mutationProbability"), EPSILON) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectDistributionIndex() {
    var distributionIndex = 15.0 ;
    var mutation = new PolynomialMutation(0.1, distributionIndex) ;
    assertEquals(distributionIndex, (Double) ReflectionTestUtils
        .getField(mutation, "distributionIndex"), EPSILON) ;
  }

  @Test (expected = InvalidProbabilityValueException.class)
  public void shouldConstructorFailWhenPassedANegativeProbabilityValue() {
    var mutationProbability = -0.1 ;
    new PolynomialMutation(mutationProbability, 2.0) ;
  }

  @Test (expected = InvalidProbabilityValueException.class)
  public void shouldConstructorFailWhenPassedAValueHigherThanOne() {
    var mutationProbability = 1.1 ;
    new PolynomialMutation(mutationProbability, 2.0) ;
  }

  @Test (expected = InvalidConditionException.class)
  public void shouldConstructorFailWhenPassedANegativeDistributionIndex() {
    var distributionIndex = -0.1 ;
    new PolynomialMutation(0.1, distributionIndex) ;
  }

  @Test
  public void shouldGetMutationProbabilityReturnTheRightValue() {
    var mutation = new PolynomialMutation(0.1, 20.0) ;
    assertEquals(0.1, mutation.getMutationProbability(), EPSILON) ;
  }

  @Test
  public void shouldGetDistributionIndexReturnTheRightValue() {
    var mutation = new PolynomialMutation(0.1, 30.0) ;
    assertEquals(30.0, mutation.getDistributionIndex(), EPSILON) ;
  }

  @Test (expected = NullParameterException.class)
  public void shouldExecuteWithNullParameterThrowAnException() {
    var mutation = new PolynomialMutation(0.1, 20.0) ;

    mutation.execute(null) ;
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnTheSameSolutionIfProbabilityIsZero() {
    var mutationProbability = 0.0;
    var distributionIndex = 20.0 ;

    var mutation = new PolynomialMutation(mutationProbability, distributionIndex) ;
    DoubleProblem problem = new FakeDoubleProblem(1, 1, 0) ;
    var solution = problem.createSolution() ;
    var oldSolution = (DoubleSolution)solution.copy() ;

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

    var mutation = new PolynomialMutation(mutationProbability, distributionIndex) ;
    DoubleProblem problem = new FakeDoubleProblem(1,1,0) ;
    var solution = problem.createSolution() ;
    var oldSolution = (DoubleSolution)solution.copy() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertEquals(oldSolution, solution) ;
    verify(randomGenerator, times(1)).getRandomValue();
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnAValidSolution() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    var mutationProbability = 0.1;
    var distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.005, 0.6) ;

    var mutation = new PolynomialMutation(mutationProbability, distributionIndex) ;
    DoubleProblem problem = new FakeDoubleProblem(1,1,0) ;
    var solution = problem.createSolution() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    var bounds = solution.getBounds(0);
    assertThat(solution.variables().get(0), Matchers.greaterThanOrEqualTo(bounds.getLowerBound()));
    assertThat(solution.variables().get(0), Matchers.lessThanOrEqualTo(bounds.getUpperBound())) ;
    verify(randomGenerator, times(2)).getRandomValue();
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnAnotherValidSolution() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    var mutationProbability = 0.1;
    var distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.005, 0.1) ;

    var mutation = new PolynomialMutation(mutationProbability, distributionIndex) ;
    DoubleProblem problem = new FakeDoubleProblem(1,1,0) ;
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

    var mutation = new PolynomialMutation(mutationProbability, distributionIndex) ;

    var problem = new FakeDoubleProblem(1, 1, 0) ;
    ReflectionTestUtils.setField(problem, "lowerLimit", Arrays.asList(new Double[]{1.0}));
    ReflectionTestUtils.setField(problem, "upperLimit", Arrays.asList(new Double[]{1.0}));

    var solution = problem.createSolution() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertEquals(1.0, solution.variables().get(0), EPSILON) ;
  }
}
