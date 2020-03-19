package org.uma.jmetal.operator.mutation;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.binaryproblem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.checking.exception.NullParameterException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BitFlipMutationTest {
  private static final double EPSILON = 0.00000000000001 ;
  private static final int NUMBER_OF_BITS_OF_MOCKED_BINARY_PROBLEM = 4 ;

  @Test
  public void shouldConstructorAssignTheCorrectProbabilityValue() {
    double mutationProbability = 0.1 ;
    BitFlipMutation mutation = new BitFlipMutation(mutationProbability) ;
    assertEquals(mutationProbability, (Double) ReflectionTestUtils
        .getField(mutation, "mutationProbability"), EPSILON) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldConstructorFailWhenPassedANegativeProbabilityValue() {
    double mutationProbability = -0.1 ;
    new BitFlipMutation(mutationProbability) ;
  }

  @Test
  public void shouldGetMutationProbabilityReturnTheRightValue() {
    double mutationProbability = 0.1 ;
    BitFlipMutation mutation = new BitFlipMutation(mutationProbability) ;
    assertEquals(mutationProbability, mutation.getMutationProbability(), EPSILON) ;
  }

  @Test (expected = NullParameterException.class)
  public void shouldExecuteWithNullParameterThrowAnException() {
    BitFlipMutation mutation = new BitFlipMutation(0.1) ;

    mutation.execute(null) ;
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnTheSameSolutionIfNoBitsAreMutated() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double mutationProbability = 0.01;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.02, 0.02, 0.02, 0.02) ;

    BitFlipMutation mutation = new BitFlipMutation(mutationProbability) ;
    BinaryProblem problem = new MockBinaryProblem(1) ;
    BinarySolution solution = problem.createSolution() ;
    BinarySolution oldSolution = (BinarySolution)solution.copy() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertEquals(oldSolution, solution) ;
    verify(randomGenerator, times(4)).getRandomValue();
  }

  @Test
  public void shouldMutateASingleVariableSolutionWhenASingleBitIsMutated() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double mutationProbability = 0.01;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.02, 0.0, 0.02, 0.02) ;

    BitFlipMutation mutation = new BitFlipMutation(mutationProbability) ;
    BinaryProblem problem = new MockBinaryProblem(1) ;
    BinarySolution solution = problem.createSolution() ;
    BinarySolution oldSolution = (BinarySolution)solution.copy() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertNotEquals(oldSolution.getVariable(0).get(1), solution.getVariable(0).get(1)) ;
    verify(randomGenerator, times(4)).getRandomValue();
  }

  @Test
  public void shouldMutateATwoVariableSolutionReturnTheSameSolutionIfNoBitsAreMutated() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double mutationProbability = 0.01;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.02, 0.02, 0.02, 0.02, 0.2, 0.2, 0.2, 0.2) ;

    BitFlipMutation mutation = new BitFlipMutation(mutationProbability) ;
    BinaryProblem problem = new MockBinaryProblem(2) ;
    BinarySolution solution = problem.createSolution() ;
    BinarySolution oldSolution = (BinarySolution)solution.copy() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertEquals(oldSolution, solution) ;
    verify(randomGenerator, times(8)).getRandomValue();
  }

  @Test
  public void shouldMutateATwoVariableSolutionWhenTwoBitsAreMutated() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double mutationProbability = 0.01;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.01, 0.02, 0.02, 0.02, 0.02, 0.02, 0.01, 0.02) ;

    BitFlipMutation mutation = new BitFlipMutation(mutationProbability) ;
    BinaryProblem problem = new MockBinaryProblem(2) ;
    BinarySolution solution = problem.createSolution() ;
    BinarySolution oldSolution = (BinarySolution)solution.copy() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertNotEquals(oldSolution.getVariable(0).get(0), solution.getVariable(0).get(0)) ;
    assertNotEquals(oldSolution.getVariable(1).get(2), solution.getVariable(1).get(2)) ;
    verify(randomGenerator, times(8)).getRandomValue();
 }

  /**
   * Mock class representing a binary problem
   */
  @SuppressWarnings("serial")
  private class MockBinaryProblem extends AbstractBinaryProblem {
    private int[] bitsPerVariable ;

    /** Constructor */
    public MockBinaryProblem(Integer numberOfVariables) {
      setNumberOfVariables(numberOfVariables);
      setNumberOfObjectives(2);

      bitsPerVariable = new int[numberOfVariables] ;

      for (int var = 0; var < numberOfVariables; var++) {
        bitsPerVariable[var] = NUMBER_OF_BITS_OF_MOCKED_BINARY_PROBLEM;
      }
    }

    @Override
    public int getBitsFromVariable(int index) {
      return bitsPerVariable[index] ;
    }

    @Override
    public List<Integer> getListOfBitsPerVariable() {
      return Arrays.stream(bitsPerVariable).boxed().collect(Collectors.toList());
    }

    @Override
    public BinarySolution createSolution() {
      return new DefaultBinarySolution(getListOfBitsPerVariable(), getNumberOfObjectives()) ;
    }

    /** Evaluate() method */
    @Override
    public void evaluate(BinarySolution solution) {
      solution.setObjective(0, 0);
      solution.setObjective(1, 1);
    }
  }
  
  @Test
	public void shouldJMetalRandomGeneratorNotBeUsedWhenCustomRandomGeneratorProvided() {
		// Configuration
		double mutationProbability = 0.1;

		BinarySolution solution = new DefaultBinarySolution(Arrays.asList(2), 2) ;

		// Check configuration leads to use default generator by default
		final int[] defaultUses = { 0 };
		JMetalRandom defaultGenerator = JMetalRandom.getInstance();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

		new BitFlipMutation(mutationProbability).execute(solution);
		assertTrue("No use of the default generator", defaultUses[0] > 0);

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final int[] customUses = { 0 };
		new BitFlipMutation(mutationProbability, () -> {
			customUses[0]++;
			return new Random().nextDouble();
		}).execute(solution);
		assertTrue("Default random generator used", defaultUses[0] == 0);
		assertTrue("No use of the custom generator", customUses[0] > 0);
	}
}
