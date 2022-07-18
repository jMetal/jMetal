package org.uma.jmetal.operator.crossover;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.binaryproblem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

public class SinglePointCrossoverTest {
  private static final double EPSILON = 0.00000000000001 ;
  private static final int BITS_OF_MOCKED_BINARY_PROBLEM = 7 ;

  @Test
  public void shouldConstructorAssignTheCorrectProbabilityValue() {
    var crossoverProbability = 0.1 ;
    var crossover = new SinglePointCrossover(crossoverProbability) ;
    assertEquals(crossoverProbability, (Double) ReflectionTestUtils
        .getField(crossover, "crossoverProbability"), EPSILON) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldConstructorFailWhenPassedANegativeProbabilityValue() {
    var crossoverProbability = -0.1 ;
    new SinglePointCrossover(crossoverProbability) ;
  }

  @Test
  public void shouldGetMutationProbabilityReturnTheRightValue() {
    var crossoverProbability = 0.1 ;
    var crossover = new SinglePointCrossover(crossoverProbability) ;
    assertEquals(crossoverProbability, crossover.getCrossoverProbability(), EPSILON) ;
  }

  @Test (expected = NullParameterException.class)
  public void shouldExecuteWithNullParameterThrowAnException() {
    var crossover = new SinglePointCrossover(0.1) ;

    crossover.execute(null) ;
  }

  @Test (expected = InvalidConditionException.class)
  public void shouldExecuteFailIfTheListContainsOnlyOneSolution() {
    var problem = new MockBinaryProblem(1) ;
    var crossover = new SinglePointCrossover(0.1) ;
    var solutions = new ArrayList<BinarySolution>(1) ;
    solutions.add(problem.createSolution()) ;

    crossover.execute(solutions) ;
  }

  @Test (expected = InvalidConditionException.class)
  public void shouldExecuteFailIfTheListContainsMoreThanTwoSolutions() {
    var problem = new MockBinaryProblem(1) ;
    var crossover = new SinglePointCrossover(0.1) ;
    var solutions = new ArrayList<BinarySolution>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    crossover.execute(solutions) ;
  }

  @Test
  public void shouldCrossingTwoVariableSolutionsReturnTheSameSolutionsIfNoBitsAreMutated() {
    var numberOfVariables = 1;

    @SuppressWarnings("unchecked")
	RandomGenerator<Double> crossoverRandomGenerator = mock(RandomGenerator.class) ;
    var crossoverProbability = 0.01;

    Mockito.when(crossoverRandomGenerator.getRandomValue()).thenReturn(0.02) ;

    var crossover = new SinglePointCrossover(crossoverProbability) ;
    BinaryProblem problem = new MockBinaryProblem(numberOfVariables) ;
    var solutions = new ArrayList<BinarySolution>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "crossoverRandomGenerator", crossoverRandomGenerator);

    var resultSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0), resultSolutions.get(0)) ;
    assertEquals(solutions.get(1), resultSolutions.get(1)) ;
    verify(crossoverRandomGenerator, times(1)).getRandomValue();
  }

  @Test
  public void shouldCrossingTheFistBitOfTwoSingleVariableSolutionsReturnTheCorrectCrossedSolutions() {
    var numberOfVariables = 1 ;
    var cuttingBit = 0 ;

    @SuppressWarnings("unchecked")
	RandomGenerator<Double> crossoverRandomGenerator = mock(RandomGenerator.class) ;
    @SuppressWarnings("unchecked")
	BoundedRandomGenerator<Integer> pointRandomGenerator = mock(BoundedRandomGenerator.class) ;
    var crossoverProbability = 0.9;

    Mockito.when(crossoverRandomGenerator.getRandomValue()).thenReturn(0.5) ;
    Mockito.when(pointRandomGenerator.getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM - 1)).thenReturn(cuttingBit) ;

    var crossover = new SinglePointCrossover(crossoverProbability) ;
    BinaryProblem problem = new MockBinaryProblem(numberOfVariables) ;
    var solutions = new ArrayList<BinarySolution>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "crossoverRandomGenerator", crossoverRandomGenerator);
    ReflectionTestUtils.setField(crossover, "pointRandomGenerator", pointRandomGenerator);

    var resultSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0).variables().get(0).get(0),
        resultSolutions.get(1).variables().get(0).get(0)) ;
    assertEquals(solutions.get(1).variables().get(0).get(0), resultSolutions.get(0).variables().get(0).get(
        0)) ;
    verify(crossoverRandomGenerator, times(1)).getRandomValue();
    verify(pointRandomGenerator, times(1)).getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM - 1);
  }

  @Test
  public void shouldCrossingTheLastBitOfTwoSingleVariableSolutionsReturnTheCorrectCrossedSolutions() {
    var numberOfVariables = 1 ;
    var cuttingBit = BITS_OF_MOCKED_BINARY_PROBLEM - 1 ;

    @SuppressWarnings("unchecked")
	RandomGenerator<Double> crossoverRandomGenerator = mock(RandomGenerator.class) ;
    @SuppressWarnings("unchecked")
	BoundedRandomGenerator<Integer> pointRandomGenerator = mock(BoundedRandomGenerator.class) ;
    var crossoverProbability = 0.9;

    Mockito.when(crossoverRandomGenerator.getRandomValue()).thenReturn(0.5) ;
    Mockito.when(pointRandomGenerator.getRandomValue(0,
        BITS_OF_MOCKED_BINARY_PROBLEM - 1)).thenReturn(cuttingBit) ;

    var crossover = new SinglePointCrossover(crossoverProbability) ;
    BinaryProblem problem = new MockBinaryProblem(numberOfVariables) ;
    var solutions = new ArrayList<BinarySolution>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "crossoverRandomGenerator", crossoverRandomGenerator);
    ReflectionTestUtils.setField(crossover, "pointRandomGenerator", pointRandomGenerator);

    var resultSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0).variables().get(0).get(BITS_OF_MOCKED_BINARY_PROBLEM - 1),
        resultSolutions.get(1).variables().get(0).get(BITS_OF_MOCKED_BINARY_PROBLEM - 1)) ;
    assertEquals(solutions.get(1).variables().get(0).get(BITS_OF_MOCKED_BINARY_PROBLEM - 1),
        resultSolutions.get(0).variables().get(0).get(BITS_OF_MOCKED_BINARY_PROBLEM - 1)) ;
    verify(crossoverRandomGenerator, times(1)).getRandomValue();
    verify(pointRandomGenerator, times(1)).getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM - 1);
  }

  @Test
  public void shouldCrossingTheBitInTheMiddleOfTwoSingleVariableSolutionsReturnTheCorrectCrossedSolutions() {
    var numberOfVariables = 1 ;
    var cuttingBit = (BITS_OF_MOCKED_BINARY_PROBLEM - 1) / 2 ;

    @SuppressWarnings("unchecked")
	RandomGenerator<Double> crossoverRandomGenerator = mock(RandomGenerator.class) ;
    @SuppressWarnings("unchecked")
	BoundedRandomGenerator<Integer> pointRandomGenerator = mock(BoundedRandomGenerator.class) ;
    var crossoverProbability = 0.9;

    Mockito.when(crossoverRandomGenerator.getRandomValue()).thenReturn(0.5) ;
    Mockito.when(pointRandomGenerator.getRandomValue(0,
        BITS_OF_MOCKED_BINARY_PROBLEM - 1)).thenReturn(cuttingBit) ;

    var crossover = new SinglePointCrossover(crossoverProbability) ;
    BinaryProblem problem = new MockBinaryProblem(numberOfVariables) ;
    var solutions = new ArrayList<BinarySolution>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "crossoverRandomGenerator", crossoverRandomGenerator);
    ReflectionTestUtils.setField(crossover, "pointRandomGenerator", pointRandomGenerator);

    var resultSolutions = crossover.execute(solutions) ;

    //System.out.println("solution 0: " + solutions.get(0).getVariableValueString(0)) ;
    //System.out.println("solution 1: " + solutions.get(1).getVariableValueString(0)) ;
    //System.out.println("solution 2: " + resultSolutions.get(0).getVariableValueString(0)) ;
    //System.out.println("solution 3: " + resultSolutions.get(1).getVariableValueString(0)) ;

    assertEquals(solutions.get(0).variables().get(0).get((BITS_OF_MOCKED_BINARY_PROBLEM - 1)/2),
        resultSolutions.get(1).variables().get(0).get((BITS_OF_MOCKED_BINARY_PROBLEM - 1)/2)) ;
    assertEquals(solutions.get(1).variables().get(0).get((BITS_OF_MOCKED_BINARY_PROBLEM - 1)/2),
        resultSolutions.get(0).variables().get(0).get((BITS_OF_MOCKED_BINARY_PROBLEM - 1)/2)) ;
    verify(crossoverRandomGenerator, times(1)).getRandomValue();
    verify(pointRandomGenerator, times(1)).getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM - 1);
  }

  @Test
  public void shouldCrossingTheFistBitOfSecondVariableReturnTheCorrectCrossedSolutions() {
    var numberOfVariables = 3 ;
    var cuttingBit = BITS_OF_MOCKED_BINARY_PROBLEM ;

    @SuppressWarnings("unchecked")
	RandomGenerator<Double> crossoverRandomGenerator = mock(RandomGenerator.class) ;
    @SuppressWarnings("unchecked")
	BoundedRandomGenerator<Integer> pointRandomGenerator = mock(BoundedRandomGenerator.class) ;
    var crossoverProbability = 0.9;

    Mockito.when(crossoverRandomGenerator.getRandomValue()).thenReturn(0.5) ;
    Mockito.when(pointRandomGenerator.
        getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM * numberOfVariables - 1)).thenReturn(cuttingBit) ;

    var crossover = new SinglePointCrossover(crossoverProbability) ;
    BinaryProblem problem = new MockBinaryProblem(numberOfVariables) ;
    var solutions = new ArrayList<BinarySolution>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "crossoverRandomGenerator", crossoverRandomGenerator);
    ReflectionTestUtils.setField(crossover, "pointRandomGenerator", pointRandomGenerator);

    var resultSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0).variables().get(0), resultSolutions.get(0).variables().get(0)) ;
    assertEquals(solutions.get(1).variables().get(0), resultSolutions.get(1).variables().get(0)) ;
    assertEquals(solutions.get(0).variables().get(1), resultSolutions.get(1).variables().get(1)) ;
    assertEquals(solutions.get(1).variables().get(1), resultSolutions.get(0).variables().get(1)) ;

    assertEquals(solutions.get(0).variables().get(2), resultSolutions.get(1).variables().get(2)) ;
    assertEquals(solutions.get(1).variables().get(2), resultSolutions.get(0).variables().get(2)) ;
    verify(crossoverRandomGenerator, times(1)).getRandomValue();
    verify(pointRandomGenerator, times(1)).getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM*3 - 1);
  }

  @Test
  public void shouldCrossingTheBitInTheMiddleOfSecondVariableReturnTheCorrectCrossedSolutions() {
    var numberOfVariables = 3 ;
    var cuttingBit = (int) (BITS_OF_MOCKED_BINARY_PROBLEM*1.5);

    @SuppressWarnings("unchecked")
	RandomGenerator<Double> crossoverRandomGenerator = mock(RandomGenerator.class) ;
    @SuppressWarnings("unchecked")
	BoundedRandomGenerator<Integer> pointRandomGenerator = mock(BoundedRandomGenerator.class) ;
    var crossoverProbability = 0.9;

    Mockito.when(crossoverRandomGenerator.getRandomValue()).thenReturn(0.5) ;
    Mockito.when(pointRandomGenerator.
        getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM * numberOfVariables - 1))
        .thenReturn(cuttingBit) ;

    var crossover = new SinglePointCrossover(crossoverProbability) ;
    BinaryProblem problem = new MockBinaryProblem(numberOfVariables) ;
    var solutions = new ArrayList<BinarySolution>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "crossoverRandomGenerator", crossoverRandomGenerator);
    ReflectionTestUtils.setField(crossover, "pointRandomGenerator", pointRandomGenerator);

    var resultSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0).variables().get(0), resultSolutions.get(0).variables().get(0)) ;
    assertEquals(solutions.get(1).variables().get(0), resultSolutions.get(1).variables().get(0)) ;

    var cuttingBitInSecondVariable = cuttingBit - BITS_OF_MOCKED_BINARY_PROBLEM ;
    assertEquals(solutions.get(0).variables().get(1).get(0, cuttingBitInSecondVariable),
        resultSolutions.get(0).variables().get(1).get(0, cuttingBitInSecondVariable)) ;
    assertEquals(solutions.get(1).variables().get(1).get(0, cuttingBitInSecondVariable),
        resultSolutions.get(1).variables().get(1).get(0, cuttingBitInSecondVariable)) ;

    assertEquals(solutions.get(0).variables().get(1).get(cuttingBitInSecondVariable, BITS_OF_MOCKED_BINARY_PROBLEM),
        resultSolutions.get(1).variables().get(1).get(cuttingBitInSecondVariable, BITS_OF_MOCKED_BINARY_PROBLEM)) ;
    assertEquals(solutions.get(1).variables().get(1).get(cuttingBitInSecondVariable, BITS_OF_MOCKED_BINARY_PROBLEM),
        resultSolutions.get(0).variables().get(1).get(cuttingBitInSecondVariable, BITS_OF_MOCKED_BINARY_PROBLEM)) ;

    assertEquals(solutions.get(0).variables().get(2), resultSolutions.get(1).variables().get(2)) ;
    assertEquals(solutions.get(1).variables().get(2), resultSolutions.get(0).variables().get(2)) ;
    verify(crossoverRandomGenerator, times(1)).getRandomValue();
    verify(pointRandomGenerator, times(1)).getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM*3 - 1);
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

      for (var var = 0; var < numberOfVariables; var++) {
        bitsPerVariable[var] = BITS_OF_MOCKED_BINARY_PROBLEM;
      }
    }

    @Override
    public int getBitsFromVariable(int index) {
      return bitsPerVariable[index] ;
    }

    @Override
    public List<Integer> getListOfBitsPerVariable() {
        List<Integer> list = new ArrayList<>();
        for (var i : bitsPerVariable) {
            Integer integer = i;
            list.add(integer);
        }
        return list;
    }

    @Override
    public BinarySolution createSolution() {
      return new DefaultBinarySolution(getListOfBitsPerVariable(), getNumberOfObjectives()) ;
    }

    /** Evaluate() method */
    @Override
    public BinarySolution evaluate(BinarySolution solution) {
      solution.objectives()[0] = 0;
      solution.objectives()[1] = 1;

      return solution ;
    }
  }
}
