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

package org.uma.jmetal.operator.impl.crossover;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.problem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SinglePointCrossoverTest {
  private static final double EPSILON = 0.00000000000001 ;
  private static final int BITS_OF_MOCKED_BINARY_PROBLEM = 7 ;

  @Test
  public void shouldConstructorAssignTheCorrectProbabilityValue() {
    double crossoverProbability = 0.1 ;
    SinglePointCrossover crossover = new SinglePointCrossover(crossoverProbability) ;
    assertEquals(crossoverProbability, (Double) ReflectionTestUtils
        .getField(crossover, "crossoverProbability"), EPSILON) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldConstructorFailWhenPassedANegativeProbabilityValue() {
    double crossoverProbability = -0.1 ;
    new SinglePointCrossover(crossoverProbability) ;
  }

  @Test
  public void shouldGetMutationProbabilityReturnTheRightValue() {
    double crossoverProbability = 0.1 ;
    SinglePointCrossover crossover = new SinglePointCrossover(crossoverProbability) ;
    assertEquals(crossoverProbability, crossover.getCrossoverProbability(), EPSILON) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldExecuteWithNullParameterThrowAnException() {
    SinglePointCrossover crossover = new SinglePointCrossover(0.1) ;

    crossover.execute(null) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldExecuteFailIfTheListContainsOnlyOneSolution() {
    MockBinaryProblem problem = new MockBinaryProblem(1) ;
    SinglePointCrossover crossover = new SinglePointCrossover(0.1) ;
    ArrayList<BinarySolution> solutions = new ArrayList<>(1) ;
    solutions.add(problem.createSolution()) ;

    crossover.execute(solutions) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldExecuteFailIfTheListContainsMoreThanTwoSolutions() {
    MockBinaryProblem problem = new MockBinaryProblem(1) ;
    SinglePointCrossover crossover = new SinglePointCrossover(0.1) ;
    ArrayList<BinarySolution> solutions = new ArrayList<>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    crossover.execute(solutions) ;
  }

  @Test
  public void shouldCrossingTwoVariableSolutionsReturnTheSameSolutionsIfNoBitsAreMutated() {
    int numberOfVariables = 1;

    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double crossoverProbability = 0.01;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(0.02) ;

    SinglePointCrossover crossover = new SinglePointCrossover(crossoverProbability) ;
    BinaryProblem problem = new MockBinaryProblem(numberOfVariables) ;
    ArrayList<BinarySolution> solutions = new ArrayList<>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);

    List<BinarySolution> resultSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0), resultSolutions.get(0)) ;
    assertEquals(solutions.get(1), resultSolutions.get(1)) ;
    verify(randomGenerator, times(1)).nextDouble();
  }

  @Test
  public void shouldCrossingTheFistBitOfTwoSingleVariableSolutionsReturnTheCorrectCrossedSolutions() {
    int numberOfVariables = 1 ;
    int cuttingBit = 0 ;

    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double crossoverProbability = 0.9;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(0.5) ;
    Mockito.when(randomGenerator.nextInt(0, BITS_OF_MOCKED_BINARY_PROBLEM - 1)).thenReturn(cuttingBit) ;

    SinglePointCrossover crossover = new SinglePointCrossover(crossoverProbability) ;
    BinaryProblem problem = new MockBinaryProblem(numberOfVariables) ;
    ArrayList<BinarySolution> solutions = new ArrayList<>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);

    List<BinarySolution> resultSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0).getVariableValue(0).get(0),
        resultSolutions.get(1).getVariableValue(0).get(0)) ;
    assertEquals(solutions.get(1).getVariableValue(0).get(0), resultSolutions.get(0).getVariableValue(0).get(
        0)) ;
    verify(randomGenerator, times(1)).nextDouble();
    verify(randomGenerator, times(1)).nextInt(0, BITS_OF_MOCKED_BINARY_PROBLEM - 1);
  }

  @Test
  public void shouldCrossingTheLastBitOfTwoSingleVariableSolutionsReturnTheCorrectCrossedSolutions() {
    int numberOfVariables = 1 ;
    int cuttingBit = BITS_OF_MOCKED_BINARY_PROBLEM - 1 ;

    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double crossoverProbability = 0.9;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(0.5) ;
    Mockito.when(randomGenerator.nextInt(0,
        BITS_OF_MOCKED_BINARY_PROBLEM - 1)).thenReturn(cuttingBit) ;

    SinglePointCrossover crossover = new SinglePointCrossover(crossoverProbability) ;
    BinaryProblem problem = new MockBinaryProblem(numberOfVariables) ;
    ArrayList<BinarySolution> solutions = new ArrayList<>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);

    List<BinarySolution> resultSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0).getVariableValue(0).get(BITS_OF_MOCKED_BINARY_PROBLEM - 1),
        resultSolutions.get(1).getVariableValue(0).get(BITS_OF_MOCKED_BINARY_PROBLEM - 1)) ;
    assertEquals(solutions.get(1).getVariableValue(0).get(BITS_OF_MOCKED_BINARY_PROBLEM - 1),
        resultSolutions.get(0).getVariableValue(0).get(BITS_OF_MOCKED_BINARY_PROBLEM - 1)) ;
    verify(randomGenerator, times(1)).nextDouble();
    verify(randomGenerator, times(1)).nextInt(0, BITS_OF_MOCKED_BINARY_PROBLEM - 1);
  }

  @Test
  public void shouldCrossingTheBitInTheMiddleOfTwoSingleVariableSolutionsReturnTheCorrectCrossedSolutions() {
    int numberOfVariables = 1 ;
    int cuttingBit = (BITS_OF_MOCKED_BINARY_PROBLEM - 1) / 2 ;

    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double crossoverProbability = 0.9;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(0.5) ;
    Mockito.when(randomGenerator.nextInt(0,
        BITS_OF_MOCKED_BINARY_PROBLEM - 1)).thenReturn(cuttingBit) ;

    SinglePointCrossover crossover = new SinglePointCrossover(crossoverProbability) ;
    BinaryProblem problem = new MockBinaryProblem(numberOfVariables) ;
    ArrayList<BinarySolution> solutions = new ArrayList<>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);

    List<BinarySolution> resultSolutions = crossover.execute(solutions) ;

    //System.out.println("solution 0: " + solutions.get(0).getVariableValueString(0)) ;
    //System.out.println("solution 1: " + solutions.get(1).getVariableValueString(0)) ;
    //System.out.println("solution 2: " + resultSolutions.get(0).getVariableValueString(0)) ;
    //System.out.println("solution 3: " + resultSolutions.get(1).getVariableValueString(0)) ;

    assertEquals(solutions.get(0).getVariableValue(0).get((BITS_OF_MOCKED_BINARY_PROBLEM - 1)/2),
        resultSolutions.get(1).getVariableValue(0).get((BITS_OF_MOCKED_BINARY_PROBLEM - 1)/2)) ;
    assertEquals(solutions.get(1).getVariableValue(0).get((BITS_OF_MOCKED_BINARY_PROBLEM - 1)/2),
        resultSolutions.get(0).getVariableValue(0).get((BITS_OF_MOCKED_BINARY_PROBLEM - 1)/2)) ;
    verify(randomGenerator, times(1)).nextDouble();
    verify(randomGenerator, times(1)).nextInt(0, BITS_OF_MOCKED_BINARY_PROBLEM - 1);
  }

  @Test
  public void shouldCrossingTheFistBitOfSecondVariableReturnTheCorrectCrossedSolutions() {
    int numberOfVariables = 3 ;
    int cuttingBit = BITS_OF_MOCKED_BINARY_PROBLEM ;

    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double crossoverProbability = 0.9;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(0.5) ;
    Mockito.when(randomGenerator.
        nextInt(0, BITS_OF_MOCKED_BINARY_PROBLEM * numberOfVariables - 1)).thenReturn(cuttingBit) ;

    SinglePointCrossover crossover = new SinglePointCrossover(crossoverProbability) ;
    BinaryProblem problem = new MockBinaryProblem(numberOfVariables) ;
    ArrayList<BinarySolution> solutions = new ArrayList<>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);

    List<BinarySolution> resultSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0).getVariableValue(0), resultSolutions.get(0).getVariableValue(0)) ;
    assertEquals(solutions.get(1).getVariableValue(0), resultSolutions.get(1).getVariableValue(0)) ;
    assertEquals(solutions.get(0).getVariableValue(1), resultSolutions.get(1).getVariableValue(1)) ;
    assertEquals(solutions.get(1).getVariableValue(1), resultSolutions.get(0).getVariableValue(1)) ;

    assertEquals(solutions.get(0).getVariableValue(2), resultSolutions.get(1).getVariableValue(2)) ;
    assertEquals(solutions.get(1).getVariableValue(2), resultSolutions.get(0).getVariableValue(2)) ;
    verify(randomGenerator, times(1)).nextDouble();
    verify(randomGenerator, times(1)).nextInt(0, BITS_OF_MOCKED_BINARY_PROBLEM*3 - 1);
  }

  @Test
  public void shouldCrossingTheBitInTheMiddleOfSecondVariableReturnTheCorrectCrossedSolutions() {
    int numberOfVariables = 3 ;
    int cuttingBit = (int) (BITS_OF_MOCKED_BINARY_PROBLEM*1.5);

    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double crossoverProbability = 0.9;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(0.5) ;
    Mockito.when(randomGenerator.
        nextInt(0, BITS_OF_MOCKED_BINARY_PROBLEM * numberOfVariables - 1))
        .thenReturn(cuttingBit) ;

    SinglePointCrossover crossover = new SinglePointCrossover(crossoverProbability) ;
    BinaryProblem problem = new MockBinaryProblem(numberOfVariables) ;
    ArrayList<BinarySolution> solutions = new ArrayList<>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);

    List<BinarySolution> resultSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0).getVariableValue(0), resultSolutions.get(0).getVariableValue(0)) ;
    assertEquals(solutions.get(1).getVariableValue(0), resultSolutions.get(1).getVariableValue(0)) ;

    int cuttingBitInSecondVariable = cuttingBit - BITS_OF_MOCKED_BINARY_PROBLEM ;
    assertEquals(solutions.get(0).getVariableValue(1).get(0, cuttingBitInSecondVariable),
        resultSolutions.get(0).getVariableValue(1).get(0, cuttingBitInSecondVariable)) ;
    assertEquals(solutions.get(1).getVariableValue(1).get(0, cuttingBitInSecondVariable),
        resultSolutions.get(1).getVariableValue(1).get(0, cuttingBitInSecondVariable)) ;

    assertEquals(solutions.get(0).getVariableValue(1).get(cuttingBitInSecondVariable, BITS_OF_MOCKED_BINARY_PROBLEM),
        resultSolutions.get(1).getVariableValue(1).get(cuttingBitInSecondVariable, BITS_OF_MOCKED_BINARY_PROBLEM)) ;
    assertEquals(solutions.get(1).getVariableValue(1).get(cuttingBitInSecondVariable, BITS_OF_MOCKED_BINARY_PROBLEM),
        resultSolutions.get(0).getVariableValue(1).get(cuttingBitInSecondVariable, BITS_OF_MOCKED_BINARY_PROBLEM)) ;

    assertEquals(solutions.get(0).getVariableValue(2), resultSolutions.get(1).getVariableValue(2)) ;
    assertEquals(solutions.get(1).getVariableValue(2), resultSolutions.get(0).getVariableValue(2)) ;
    verify(randomGenerator, times(1)).nextDouble();
    verify(randomGenerator, times(1)).nextInt(0, BITS_OF_MOCKED_BINARY_PROBLEM*3 - 1);
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
        bitsPerVariable[var] = BITS_OF_MOCKED_BINARY_PROBLEM;
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
