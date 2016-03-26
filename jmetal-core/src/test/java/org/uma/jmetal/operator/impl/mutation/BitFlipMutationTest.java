//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.operator.impl.mutation;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.problem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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

  @Test (expected = JMetalException.class)
  public void shouldExecuteWithNullParameterThrowAnException() {
    BitFlipMutation mutation = new BitFlipMutation(0.1) ;

    mutation.execute(null) ;
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnTheSameSolutionIfNoBitsAreMutated() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double mutationProbability = 0.01;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(0.02, 0.02, 0.02, 0.02) ;

    BitFlipMutation mutation = new BitFlipMutation(mutationProbability) ;
    BinaryProblem problem = new MockBinaryProblem(1) ;
    BinarySolution solution = problem.createSolution() ;
    BinarySolution oldSolution = (BinarySolution)solution.copy() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertEquals(oldSolution, solution) ;
    verify(randomGenerator, times(4)).nextDouble();
  }

  @Test
  public void shouldMutateASingleVariableSolutionWhenASingleBitIsMutated() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double mutationProbability = 0.01;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(0.02, 0.0, 0.02, 0.02) ;

    BitFlipMutation mutation = new BitFlipMutation(mutationProbability) ;
    BinaryProblem problem = new MockBinaryProblem(1) ;
    BinarySolution solution = problem.createSolution() ;
    BinarySolution oldSolution = (BinarySolution)solution.copy() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertNotEquals(oldSolution.getVariableValue(0).get(1), solution.getVariableValue(0).get(1)) ;
    verify(randomGenerator, times(4)).nextDouble();
  }

  @Test
  public void shouldMutateATwoVariableSolutionReturnTheSameSolutionIfNoBitsAreMutated() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double mutationProbability = 0.01;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(0.02, 0.02, 0.02, 0.02, 0.2, 0.2, 0.2, 0.2) ;

    BitFlipMutation mutation = new BitFlipMutation(mutationProbability) ;
    BinaryProblem problem = new MockBinaryProblem(2) ;
    BinarySolution solution = problem.createSolution() ;
    BinarySolution oldSolution = (BinarySolution)solution.copy() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertEquals(oldSolution, solution) ;
    verify(randomGenerator, times(8)).nextDouble();
  }

  @Test
  public void shouldMutateATwoVariableSolutionWhenTwoBitsAreMutated() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double mutationProbability = 0.01;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(0.01, 0.02, 0.02, 0.02, 0.02, 0.02, 0.01, 0.02) ;

    BitFlipMutation mutation = new BitFlipMutation(mutationProbability) ;
    BinaryProblem problem = new MockBinaryProblem(2) ;
    BinarySolution solution = problem.createSolution() ;
    BinarySolution oldSolution = (BinarySolution)solution.copy() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertNotEquals(oldSolution.getVariableValue(0).get(0), solution.getVariableValue(0).get(0)) ;
    assertNotEquals(oldSolution.getVariableValue(1).get(2), solution.getVariableValue(1).get(2)) ;
    verify(randomGenerator, times(8)).nextDouble();
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
    protected int getBitsPerVariable(int index) {
      return bitsPerVariable[index] ;
    }

    @Override
    public BinarySolution createSolution() {
      return new DefaultBinarySolution(this) ;
    }

    /** Evaluate() method */
    @Override
    public void evaluate(BinarySolution solution) {
      solution.setObjective(0, 0);
      solution.setObjective(1, 1);
    }
  }
}
