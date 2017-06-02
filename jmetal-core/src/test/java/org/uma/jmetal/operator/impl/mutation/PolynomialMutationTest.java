package org.uma.jmetal.operator.impl.mutation;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.util.RepairDoubleSolutionAtBounds;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
    DoubleProblem problem = new MockDoubleProblem(4) ;
    PolynomialMutation mutation = new PolynomialMutation(problem, 10.0) ;
    assertEquals(1.0/problem.getNumberOfVariables(), (Double) ReflectionTestUtils
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

  @Test (expected = JMetalException.class)
  public void shouldConstructorFailWhenPassedANegativeProbabilityValue() {
    double mutationProbability = -0.1 ;
    new PolynomialMutation(mutationProbability, 2.0) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldConstructorFailWhenPassedANegativeDistributionIndex() {
    double distributionIndex = -0.1 ;
    new PolynomialMutation(0.1, distributionIndex) ;
  }

  @Test
  public void shouldGetMutationProbabilityReturnTheRightValue() {
    PolynomialMutation mutation = new PolynomialMutation(0.1, 20.0) ;
    assertEquals(0.1, mutation.getMutationProbability(), EPSILON) ;
  }

  @Test
  public void shouldGetDistributionIndexReturnTheRightValue() {
    PolynomialMutation mutation = new PolynomialMutation(0.1, 30.0) ;
    assertEquals(30.0, mutation.getDistributionIndex(), EPSILON) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldExecuteWithNullParameterThrowAnException() {
    PolynomialMutation mutation = new PolynomialMutation(0.1, 20.0) ;

    mutation.execute(null) ;
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnTheSameSolutionIfProbabilityIsZero() {
    double mutationProbability = 0.0;
    double distributionIndex = 20.0 ;

    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, distributionIndex) ;
    DoubleProblem problem = new MockDoubleProblem(1) ;
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
    DoubleProblem problem = new MockDoubleProblem(1) ;
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
    DoubleProblem problem = new MockDoubleProblem(1) ;
    DoubleSolution solution = problem.createSolution() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertThat(solution.getVariableValue(0), Matchers.greaterThanOrEqualTo(
        solution.getLowerBound(0))) ;
    assertThat(solution.getVariableValue(0), Matchers.lessThanOrEqualTo(solution.getUpperBound(0))) ;
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
    DoubleProblem problem = new MockDoubleProblem(1) ;
    DoubleSolution solution = problem.createSolution() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertThat(solution.getVariableValue(0), Matchers.greaterThanOrEqualTo(solution.getLowerBound(0))) ;
    assertThat(solution.getVariableValue(0), Matchers.lessThanOrEqualTo(solution.getUpperBound(0))) ;
    verify(randomGenerator, times(2)).getRandomValue();
  }

  @Test
  public void shouldMutateASingleVariableSolutionWithSameLowerAndUpperBoundsReturnTheBoundValue() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.005, 0.1) ;

    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, distributionIndex) ;

    MockDoubleProblem problem = new MockDoubleProblem(1) ;
    ReflectionTestUtils.setField(problem, "lowerLimit", Arrays.asList(new Double[]{1.0}));
    ReflectionTestUtils.setField(problem, "upperLimit", Arrays.asList(new Double[]{1.0}));

    DoubleSolution solution = problem.createSolution() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertEquals(1.0, solution.getVariableValue(0), EPSILON) ;
  }

  /**
   * Mock class representing a double problem
   */
  @SuppressWarnings("serial")
  private class MockDoubleProblem extends AbstractDoubleProblem {

    /** Constructor */
    public MockDoubleProblem(Integer numberOfVariables) {
      setNumberOfVariables(numberOfVariables);
      setNumberOfObjectives(2);

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(-4.0);
        upperLimit.add(4.0);
      }

      setLowerLimit(lowerLimit);
      setUpperLimit(upperLimit);
    }

    @Override
    public DoubleSolution createSolution() {
      return new DefaultDoubleSolution(this) ;
    }

    /** Evaluate() method */
    @Override
    public void evaluate(DoubleSolution solution) {
      solution.setObjective(0, 0.0);
      solution.setObjective(1, 1.0);
    }
  }
  
	@Test
	public void shouldJMetalRandomGeneratorNotBeUsedWhenCustomRandomGeneratorProvided() {
		// Configuration
		double crossoverProbability = 0.1;
		int alpha = 20;
		RepairDoubleSolutionAtBounds solutionRepair = new RepairDoubleSolutionAtBounds();
		@SuppressWarnings("serial")
		DoubleProblem problem = new AbstractDoubleProblem() {

			@Override
			public void evaluate(DoubleSolution solution) {
				// Do nothing
			}
			
			@Override
			public int getNumberOfVariables() {
				return 5;
			}
			
			@Override
			public Double getLowerBound(int index) {
				return 0.0;
			}
			
			@Override
			public Double getUpperBound(int index) {
				return 10.0;
			}

		};
		DoubleSolution solution = problem.createSolution();

		// Check configuration leads to use default generator by default
		final int[] defaultUses = { 0 };
		JMetalRandom defaultGenerator = JMetalRandom.getInstance();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

		new PolynomialMutation(crossoverProbability, alpha, solutionRepair).execute(solution);
		assertTrue("No use of the default generator", defaultUses[0] > 0);

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final int[] customUses = { 0 };
		new PolynomialMutation(crossoverProbability, alpha, solutionRepair, () -> {
			customUses[0]++;
			return new Random().nextDouble();
		}).execute(solution);
		assertTrue("Default random generator used", defaultUses[0] == 0);
		assertTrue("No use of the custom generator", customUses[0] > 0);
	}
}
