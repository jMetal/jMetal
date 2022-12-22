package org.uma.jmetal.operator.crossover;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.binaryproblem.impl.FakeBinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.InvalidProbabilityValueException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

class SinglePointCrossoverTest {

  private static final double EPSILON = 0.00000000000001;
  private static final int BITS_OF_MOCKED_BINARY_PROBLEM = 7;

  @Test
  void shouldConstructorAssignTheCorrectProbabilityValue() {
    double crossoverProbability = 0.1;
    var crossover = new SinglePointCrossover<>(crossoverProbability);
    assertEquals(crossoverProbability, crossover.crossoverProbability(), EPSILON);
  }

  @Test
  void shouldConstructorFailWhenPassedANegativeProbabilityValue() {
    double crossoverProbability = -0.1;
    assertThrows(InvalidProbabilityValueException.class,
        () -> new SinglePointCrossover<>(crossoverProbability));
  }

  @Test
  void shouldGetMutationProbabilityReturnTheRightValue() {
    double crossoverProbability = 0.1;
    var crossover = new SinglePointCrossover<>(crossoverProbability);
    assertEquals(crossoverProbability, crossover.crossoverProbability(), EPSILON);
  }

  @Test
  void shouldExecuteWithNullParameterThrowAnException() {
    var crossover = new SinglePointCrossover<>(0.1);

    assertThrows(NullParameterException.class, () -> crossover.execute(null));
  }

  @Test
  void shouldExecuteFailIfTheListContainsOnlyOneSolution() {
    var problem = new FakeBinaryProblem(1, BITS_OF_MOCKED_BINARY_PROBLEM);
    var crossover = new SinglePointCrossover<>(0.1);
    List<BinarySolution> solutions = new ArrayList<>(1);
    solutions.add(problem.createSolution());

    assertThrows(InvalidConditionException.class, () -> crossover.execute(solutions));
  }

  @Test
  void shouldExecuteFailIfTheListContainsMoreThanTwoSolutions() {
    var problem = new FakeBinaryProblem(1, BITS_OF_MOCKED_BINARY_PROBLEM);
    var crossover = new SinglePointCrossover<>(0.1);
    ArrayList<BinarySolution> solutions = new ArrayList<>(3);
    solutions.add(problem.createSolution());
    solutions.add(problem.createSolution());
    solutions.add(problem.createSolution());

    assertThrows(InvalidConditionException.class, () -> crossover.execute(solutions));
  }

  @Test
  void shouldCrossingTwoVariableSolutionsReturnTheSameSolutionsIfNoBitsAreMutated() {
    int numberOfVariables = 1;

    RandomGenerator<Double> crossoverRandomGenerator = mock(RandomGenerator.class);
    double crossoverProbability = 0.01;

    Mockito.when(crossoverRandomGenerator.getRandomValue()).thenReturn(0.02);

    var crossover = new SinglePointCrossover<>(crossoverProbability, crossoverRandomGenerator);
    BinaryProblem problem = new FakeBinaryProblem(numberOfVariables, 4);
    ArrayList<BinarySolution> solutions = new ArrayList<>(3);
    solutions.add(problem.createSolution());
    solutions.add(problem.createSolution());

    List<BinarySolution> resultSolutions = crossover.execute(solutions);

    assertEquals(solutions.get(0), resultSolutions.get(0));
    assertEquals(solutions.get(1), resultSolutions.get(1));
    verify(crossoverRandomGenerator, times(1)).getRandomValue();
  }

  @Test
  void shouldCrossingTheFistBitOfTwoSingleVariableSolutionsReturnTheCorrectCrossedSolutions() {
    int numberOfVariables = 1;
    int cuttingBit = 0;

    RandomGenerator<Double> crossoverRandomGenerator = mock(RandomGenerator.class);
    BoundedRandomGenerator<Integer> pointRandomGenerator = mock(BoundedRandomGenerator.class);
    double crossoverProbability = 0.9;

    Mockito.when(crossoverRandomGenerator.getRandomValue()).thenReturn(0.5);
    Mockito.when(pointRandomGenerator.getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM - 1))
        .thenReturn(cuttingBit);

    var crossover = new SinglePointCrossover<>(crossoverProbability, crossoverRandomGenerator,
        pointRandomGenerator);
    BinaryProblem problem = new FakeBinaryProblem(numberOfVariables, BITS_OF_MOCKED_BINARY_PROBLEM);
    ArrayList<BinarySolution> solutions = new ArrayList<>(3);
    solutions.add(problem.createSolution());
    solutions.add(problem.createSolution());

    List<BinarySolution> resultSolutions = crossover.execute(solutions);

    assertEquals(solutions.get(0).variables().get(0).get(0),
        resultSolutions.get(1).variables().get(0).get(0));
    assertEquals(solutions.get(1).variables().get(0).get(0),
        resultSolutions.get(0).variables().get(0).get(
            0));
    verify(crossoverRandomGenerator, times(1)).getRandomValue();
    verify(pointRandomGenerator, times(1)).getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM - 1);
  }

  @Test
  void shouldCrossingTheLastBitOfTwoSingleVariableSolutionsReturnTheCorrectCrossedSolutions() {
    int numberOfVariables = 1;
    int cuttingBit = BITS_OF_MOCKED_BINARY_PROBLEM - 1;

    RandomGenerator<Double> crossoverRandomGenerator = mock(RandomGenerator.class);
    BoundedRandomGenerator<Integer> pointRandomGenerator = mock(BoundedRandomGenerator.class);
    double crossoverProbability = 0.9;

    Mockito.when(crossoverRandomGenerator.getRandomValue()).thenReturn(0.5);
    Mockito.when(pointRandomGenerator.getRandomValue(0,
        BITS_OF_MOCKED_BINARY_PROBLEM - 1)).thenReturn(cuttingBit);

    var crossover = new SinglePointCrossover<>(crossoverProbability, crossoverRandomGenerator,
        pointRandomGenerator);
    BinaryProblem problem = new FakeBinaryProblem(numberOfVariables, BITS_OF_MOCKED_BINARY_PROBLEM);
    ArrayList<BinarySolution> solutions = new ArrayList<>(3);
    solutions.add(problem.createSolution());
    solutions.add(problem.createSolution());

    List<BinarySolution> resultSolutions = crossover.execute(solutions);

    assertEquals(solutions.get(0).variables().get(0).get(BITS_OF_MOCKED_BINARY_PROBLEM - 1),
        resultSolutions.get(1).variables().get(0).get(BITS_OF_MOCKED_BINARY_PROBLEM - 1));
    assertEquals(solutions.get(1).variables().get(0).get(BITS_OF_MOCKED_BINARY_PROBLEM - 1),
        resultSolutions.get(0).variables().get(0).get(BITS_OF_MOCKED_BINARY_PROBLEM - 1));
    verify(crossoverRandomGenerator, times(1)).getRandomValue();
    verify(pointRandomGenerator, times(1)).getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM - 1);
  }

  @Test
  void shouldCrossingTheBitInTheMiddleOfTwoSingleVariableSolutionsReturnTheCorrectCrossedSolutions() {
    int numberOfVariables = 1;
    int cuttingBit = (BITS_OF_MOCKED_BINARY_PROBLEM - 1) / 2;

    @SuppressWarnings("unchecked")
    RandomGenerator<Double> crossoverRandomGenerator = mock(RandomGenerator.class);
    @SuppressWarnings("unchecked")
    BoundedRandomGenerator<Integer> pointRandomGenerator = mock(BoundedRandomGenerator.class);
    double crossoverProbability = 0.9;

    Mockito.when(crossoverRandomGenerator.getRandomValue()).thenReturn(0.5);
    Mockito.when(pointRandomGenerator.getRandomValue(0,
        BITS_OF_MOCKED_BINARY_PROBLEM - 1)).thenReturn(cuttingBit);

    var crossover = new SinglePointCrossover<>(crossoverProbability, crossoverRandomGenerator,
        pointRandomGenerator);
    BinaryProblem problem = new FakeBinaryProblem(numberOfVariables, BITS_OF_MOCKED_BINARY_PROBLEM);
    ArrayList<BinarySolution> solutions = new ArrayList<>(3);
    solutions.add(problem.createSolution());
    solutions.add(problem.createSolution());

    List<BinarySolution> resultSolutions = crossover.execute(solutions);

    assertEquals(solutions.get(0).variables().get(0).get((BITS_OF_MOCKED_BINARY_PROBLEM - 1) / 2),
        resultSolutions.get(1).variables().get(0).get((BITS_OF_MOCKED_BINARY_PROBLEM - 1) / 2));
    assertEquals(solutions.get(1).variables().get(0).get((BITS_OF_MOCKED_BINARY_PROBLEM - 1) / 2),
        resultSolutions.get(0).variables().get(0).get((BITS_OF_MOCKED_BINARY_PROBLEM - 1) / 2));
    verify(crossoverRandomGenerator, times(1)).getRandomValue();
    verify(pointRandomGenerator, times(1)).getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM - 1);
  }

  @Test
  void shouldCrossingTheFistBitOfSecondVariableReturnTheCorrectCrossedSolutions() {
    int numberOfVariables = 3;
    int cuttingBit = BITS_OF_MOCKED_BINARY_PROBLEM;

    @SuppressWarnings("unchecked")
    RandomGenerator<Double> crossoverRandomGenerator = mock(RandomGenerator.class);
    @SuppressWarnings("unchecked")
    BoundedRandomGenerator<Integer> pointRandomGenerator = mock(BoundedRandomGenerator.class);
    double crossoverProbability = 0.9;

    Mockito.when(crossoverRandomGenerator.getRandomValue()).thenReturn(0.5);
    Mockito.when(pointRandomGenerator.
            getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM * numberOfVariables - 1))
        .thenReturn(cuttingBit);

    var crossover = new SinglePointCrossover<>(crossoverProbability, crossoverRandomGenerator,
        pointRandomGenerator);
    BinaryProblem problem = new FakeBinaryProblem(numberOfVariables, BITS_OF_MOCKED_BINARY_PROBLEM);
    ArrayList<BinarySolution> solutions = new ArrayList<>(3);
    solutions.add(problem.createSolution());
    solutions.add(problem.createSolution());

    List<BinarySolution> resultSolutions = crossover.execute(solutions);

    assertEquals(solutions.get(0).variables().get(0), resultSolutions.get(0).variables().get(0));
    assertEquals(solutions.get(1).variables().get(0), resultSolutions.get(1).variables().get(0));
    assertEquals(solutions.get(0).variables().get(1), resultSolutions.get(1).variables().get(1));
    assertEquals(solutions.get(1).variables().get(1), resultSolutions.get(0).variables().get(1));

    assertEquals(solutions.get(0).variables().get(2), resultSolutions.get(1).variables().get(2));
    assertEquals(solutions.get(1).variables().get(2), resultSolutions.get(0).variables().get(2));
    verify(crossoverRandomGenerator, times(1)).getRandomValue();
    verify(pointRandomGenerator, times(1)).getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM * 3 - 1);
  }

  @Test
  void shouldCrossingTheBitInTheMiddleOfSecondVariableReturnTheCorrectCrossedSolutions() {
    int numberOfVariables = 3;
    int cuttingBit = (int) (BITS_OF_MOCKED_BINARY_PROBLEM * 1.5);

    @SuppressWarnings("unchecked")
    RandomGenerator<Double> crossoverRandomGenerator = mock(RandomGenerator.class);
    @SuppressWarnings("unchecked")
    BoundedRandomGenerator<Integer> pointRandomGenerator = mock(BoundedRandomGenerator.class);
    double crossoverProbability = 0.9;

    Mockito.when(crossoverRandomGenerator.getRandomValue()).thenReturn(0.5);
    Mockito.when(pointRandomGenerator.
            getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM * numberOfVariables - 1))
        .thenReturn(cuttingBit);

    var crossover = new SinglePointCrossover<>(crossoverProbability, crossoverRandomGenerator,
        pointRandomGenerator);
    BinaryProblem problem = new FakeBinaryProblem(numberOfVariables, BITS_OF_MOCKED_BINARY_PROBLEM);
    ArrayList<BinarySolution> solutions = new ArrayList<>(3);
    solutions.add(problem.createSolution());
    solutions.add(problem.createSolution());

    List<BinarySolution> resultSolutions = crossover.execute(solutions);

    assertEquals(solutions.get(0).variables().get(0), resultSolutions.get(0).variables().get(0));
    assertEquals(solutions.get(1).variables().get(0), resultSolutions.get(1).variables().get(0));

    int cuttingBitInSecondVariable = cuttingBit - BITS_OF_MOCKED_BINARY_PROBLEM;
    assertEquals(solutions.get(0).variables().get(1).get(0, cuttingBitInSecondVariable),
        resultSolutions.get(0).variables().get(1).get(0, cuttingBitInSecondVariable));
    assertEquals(solutions.get(1).variables().get(1).get(0, cuttingBitInSecondVariable),
        resultSolutions.get(1).variables().get(1).get(0, cuttingBitInSecondVariable));

    assertEquals(solutions.get(0).variables().get(1)
            .get(cuttingBitInSecondVariable, BITS_OF_MOCKED_BINARY_PROBLEM),
        resultSolutions.get(1).variables().get(1)
            .get(cuttingBitInSecondVariable, BITS_OF_MOCKED_BINARY_PROBLEM));
    assertEquals(solutions.get(1).variables().get(1)
            .get(cuttingBitInSecondVariable, BITS_OF_MOCKED_BINARY_PROBLEM),
        resultSolutions.get(0).variables().get(1)
            .get(cuttingBitInSecondVariable, BITS_OF_MOCKED_BINARY_PROBLEM));

    assertEquals(solutions.get(0).variables().get(2), resultSolutions.get(1).variables().get(2));
    assertEquals(solutions.get(1).variables().get(2), resultSolutions.get(0).variables().get(2));
    verify(crossoverRandomGenerator, times(1)).getRandomValue();
    verify(pointRandomGenerator, times(1)).getRandomValue(0, BITS_OF_MOCKED_BINARY_PROBLEM * 3 - 1);
  }
}
