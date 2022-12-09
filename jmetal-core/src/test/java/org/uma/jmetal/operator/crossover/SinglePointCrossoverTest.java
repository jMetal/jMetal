package org.uma.jmetal.operator.crossover;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.binaryproblem.impl.FakeBinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
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
    assertEquals(crossoverProbability, crossover.crossoverProbability(), EPSILON) ;
  }

  @Test (expected = NullParameterException.class)
  public void shouldExecuteWithNullParameterThrowAnException() {
    SinglePointCrossover crossover = new SinglePointCrossover(0.1) ;

    crossover.execute(null) ;
  }

  @Test (expected = InvalidConditionException.class)
  public void shouldExecuteFailIfTheListContainsOnlyOneSolution() {
    var problem = new FakeBinaryProblem(1, BITS_OF_MOCKED_BINARY_PROBLEM) ;
    SinglePointCrossover crossover = new SinglePointCrossover(0.1) ;
    ArrayList<BinarySolution> solutions = new ArrayList<>(1) ;
    solutions.add(problem.createSolution()) ;

    crossover.execute(solutions) ;
  }

  @Test (expected = InvalidConditionException.class)
  public void shouldExecuteFailIfTheListContainsMoreThanTwoSolutions() {
    var problem = new FakeBinaryProblem(1, BITS_OF_MOCKED_BINARY_PROBLEM) ;
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

    @SuppressWarnings("unchecked")
	RandomGenerator<Double> crossoverRandomGenerator = mock(RandomGenerator.class) ;
    double crossoverProbability = 0.01;

    Mockito.when(crossoverRandomGenerator.getRandomValue()).thenReturn(0.02) ;

    SinglePointCrossover crossover = new SinglePointCrossover(crossoverProbability) ;
    BinaryProblem problem = new FakeBinaryProblem(numberOfVariables, 4) ;
    ArrayList<BinarySolution> solutions = new ArrayList<>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "crossoverRandomGenerator", crossoverRandomGenerator);

    List<BinarySolution> resultSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0), resultSolutions.get(0)) ;
    assertEquals(solutions.get(1), resultSolutions.get(1)) ;
    verify(crossoverRandomGenerator, times(1)).getRandomValue();
  }

  @Test
  public void shouldCrossingTheFistBitOfTwoSingleVariableSolutionsReturnTheCorrectCrossedSolutions() {
    int numberOfVariables = 1 ;
    int cuttingBit = 0 ;

    @SuppressWarnings("unchecked")
	RandomGenerator<Double> crossoverRandomGenerator = mock(RandomGenerator.class) ;
    @SuppressWarnings("unchecked")
	BoundedRandomGenerator<Integer> pointRandomGenerator = mock(BoundedRandomGenerator.class) ;
    double crossoverProbability = 0.9;

    Mockito.when(crossoverRandomGenerator.getRandomValue()).thenReturn(0.5) ;
    Mockito.when(pointRandomGenerator.getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM - 1)).thenReturn(cuttingBit) ;

    SinglePointCrossover crossover = new SinglePointCrossover(crossoverProbability) ;
    BinaryProblem problem = new FakeBinaryProblem(numberOfVariables, BITS_OF_MOCKED_BINARY_PROBLEM) ;
    ArrayList<BinarySolution> solutions = new ArrayList<>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "crossoverRandomGenerator", crossoverRandomGenerator);
    ReflectionTestUtils.setField(crossover, "pointRandomGenerator", pointRandomGenerator);

    List<BinarySolution> resultSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0).variables().get(0).get(0),
        resultSolutions.get(1).variables().get(0).get(0)) ;
    assertEquals(solutions.get(1).variables().get(0).get(0), resultSolutions.get(0).variables().get(0).get(
        0)) ;
    verify(crossoverRandomGenerator, times(1)).getRandomValue();
    verify(pointRandomGenerator, times(1)).getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM - 1);
  }

  @Test
  public void shouldCrossingTheLastBitOfTwoSingleVariableSolutionsReturnTheCorrectCrossedSolutions() {
    int numberOfVariables = 1 ;
    int cuttingBit = BITS_OF_MOCKED_BINARY_PROBLEM - 1 ;

    @SuppressWarnings("unchecked")
	RandomGenerator<Double> crossoverRandomGenerator = mock(RandomGenerator.class) ;
    @SuppressWarnings("unchecked")
	BoundedRandomGenerator<Integer> pointRandomGenerator = mock(BoundedRandomGenerator.class) ;
    double crossoverProbability = 0.9;

    Mockito.when(crossoverRandomGenerator.getRandomValue()).thenReturn(0.5) ;
    Mockito.when(pointRandomGenerator.getRandomValue(0,
        BITS_OF_MOCKED_BINARY_PROBLEM - 1)).thenReturn(cuttingBit) ;

    SinglePointCrossover crossover = new SinglePointCrossover(crossoverProbability) ;
    BinaryProblem problem = new FakeBinaryProblem(numberOfVariables, BITS_OF_MOCKED_BINARY_PROBLEM) ;
    ArrayList<BinarySolution> solutions = new ArrayList<>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "crossoverRandomGenerator", crossoverRandomGenerator);
    ReflectionTestUtils.setField(crossover, "pointRandomGenerator", pointRandomGenerator);

    List<BinarySolution> resultSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0).variables().get(0).get(BITS_OF_MOCKED_BINARY_PROBLEM - 1),
        resultSolutions.get(1).variables().get(0).get(BITS_OF_MOCKED_BINARY_PROBLEM - 1)) ;
    assertEquals(solutions.get(1).variables().get(0).get(BITS_OF_MOCKED_BINARY_PROBLEM - 1),
        resultSolutions.get(0).variables().get(0).get(BITS_OF_MOCKED_BINARY_PROBLEM - 1)) ;
    verify(crossoverRandomGenerator, times(1)).getRandomValue();
    verify(pointRandomGenerator, times(1)).getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM - 1);
  }

  @Test
  public void shouldCrossingTheBitInTheMiddleOfTwoSingleVariableSolutionsReturnTheCorrectCrossedSolutions() {
    int numberOfVariables = 1 ;
    int cuttingBit = (BITS_OF_MOCKED_BINARY_PROBLEM - 1) / 2 ;

    @SuppressWarnings("unchecked")
	RandomGenerator<Double> crossoverRandomGenerator = mock(RandomGenerator.class) ;
    @SuppressWarnings("unchecked")
	BoundedRandomGenerator<Integer> pointRandomGenerator = mock(BoundedRandomGenerator.class) ;
    double crossoverProbability = 0.9;

    Mockito.when(crossoverRandomGenerator.getRandomValue()).thenReturn(0.5) ;
    Mockito.when(pointRandomGenerator.getRandomValue(0,
        BITS_OF_MOCKED_BINARY_PROBLEM - 1)).thenReturn(cuttingBit) ;

    SinglePointCrossover crossover = new SinglePointCrossover(crossoverProbability) ;
    BinaryProblem problem = new FakeBinaryProblem(numberOfVariables, BITS_OF_MOCKED_BINARY_PROBLEM) ;
    ArrayList<BinarySolution> solutions = new ArrayList<>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "crossoverRandomGenerator", crossoverRandomGenerator);
    ReflectionTestUtils.setField(crossover, "pointRandomGenerator", pointRandomGenerator);

    List<BinarySolution> resultSolutions = crossover.execute(solutions) ;

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
    int numberOfVariables = 3 ;
    int cuttingBit = BITS_OF_MOCKED_BINARY_PROBLEM ;

    @SuppressWarnings("unchecked")
	RandomGenerator<Double> crossoverRandomGenerator = mock(RandomGenerator.class) ;
    @SuppressWarnings("unchecked")
	BoundedRandomGenerator<Integer> pointRandomGenerator = mock(BoundedRandomGenerator.class) ;
    double crossoverProbability = 0.9;

    Mockito.when(crossoverRandomGenerator.getRandomValue()).thenReturn(0.5) ;
    Mockito.when(pointRandomGenerator.
        getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM * numberOfVariables - 1)).thenReturn(cuttingBit) ;

    SinglePointCrossover crossover = new SinglePointCrossover(crossoverProbability) ;
    BinaryProblem problem = new FakeBinaryProblem(numberOfVariables, BITS_OF_MOCKED_BINARY_PROBLEM) ;
    ArrayList<BinarySolution> solutions = new ArrayList<>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "crossoverRandomGenerator", crossoverRandomGenerator);
    ReflectionTestUtils.setField(crossover, "pointRandomGenerator", pointRandomGenerator);

    List<BinarySolution> resultSolutions = crossover.execute(solutions) ;

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
    int numberOfVariables = 3 ;
    int cuttingBit = (int) (BITS_OF_MOCKED_BINARY_PROBLEM*1.5);

    @SuppressWarnings("unchecked")
	RandomGenerator<Double> crossoverRandomGenerator = mock(RandomGenerator.class) ;
    @SuppressWarnings("unchecked")
	BoundedRandomGenerator<Integer> pointRandomGenerator = mock(BoundedRandomGenerator.class) ;
    double crossoverProbability = 0.9;

    Mockito.when(crossoverRandomGenerator.getRandomValue()).thenReturn(0.5) ;
    Mockito.when(pointRandomGenerator.
        getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM * numberOfVariables - 1))
        .thenReturn(cuttingBit) ;

    SinglePointCrossover crossover = new SinglePointCrossover(crossoverProbability) ;
    BinaryProblem problem = new FakeBinaryProblem(numberOfVariables, BITS_OF_MOCKED_BINARY_PROBLEM) ;
    ArrayList<BinarySolution> solutions = new ArrayList<>(3) ;
    solutions.add(problem.createSolution()) ;
    solutions.add(problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "crossoverRandomGenerator", crossoverRandomGenerator);
    ReflectionTestUtils.setField(crossover, "pointRandomGenerator", pointRandomGenerator);

    List<BinarySolution> resultSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0).variables().get(0), resultSolutions.get(0).variables().get(0)) ;
    assertEquals(solutions.get(1).variables().get(0), resultSolutions.get(1).variables().get(0)) ;

    int cuttingBitInSecondVariable = cuttingBit - BITS_OF_MOCKED_BINARY_PROBLEM ;
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
}
