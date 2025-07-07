package org.uma.jmetal.operator.crossover;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.uma.jmetal.operator.crossover.impl.BLXAlphaBetaCrossover;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidProbabilityValueException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.JavaRandomGenerator;

@ExtendWith(MockitoExtension.class)
class BLXAlphaBetaCrossoverTest {
  private static final double EPSILON = 0.0000000000001;
  private static final double CROSSOVER_PROBABILITY = 0.9;
  private static final double ALPHA = 0.5;
  private static final double BETA = 0.5;

  @Test
  void shouldConstructorAssignTheCorrectProbabilityValue() {
    BLXAlphaBetaCrossover crossover = new BLXAlphaBetaCrossover(0.5, ALPHA, BETA);
    assertEquals(0.5, crossover.crossoverProbability(), EPSILON);
  }

  @Test
  void shouldConstructorAssignTheCorrectAlphaValue() {
    BLXAlphaBetaCrossover crossover = new BLXAlphaBetaCrossover(CROSSOVER_PROBABILITY, 0.7, BETA);
    assertEquals(0.7, crossover.getAlpha(), EPSILON);
  }

  @Test
  void shouldConstructorAssignTheCorrectBetaValue() {
    BLXAlphaBetaCrossover crossover = new BLXAlphaBetaCrossover(CROSSOVER_PROBABILITY, ALPHA, 0.8);
    assertEquals(0.8, crossover.getBeta(), EPSILON);
  }

  @Test
  void shouldConstructorWhenPassedNegativeAlphaValueThrowAnException() {
    assertThrows(
        org.uma.jmetal.util.errorchecking.exception.InvalidConditionException.class,
        () -> new BLXAlphaBetaCrossover(CROSSOVER_PROBABILITY, -0.1, BETA));
  }

  @Test
  void shouldConstructorWhenPassedNegativeBetaValueThrowAnException() {
    assertThrows(
        org.uma.jmetal.util.errorchecking.exception.InvalidConditionException.class,
        () -> new BLXAlphaBetaCrossover(CROSSOVER_PROBABILITY, ALPHA, -0.1));
  }

  @Test
  void shouldConstructorWhenPassedProbabilityValueLessThanZeroThrowAnException() {
    assertThrows(InvalidProbabilityValueException.class, () -> new BLXAlphaBetaCrossover(-0.1, ALPHA, BETA));
  }

  @Test
  void shouldConstructorWhenPassedProbabilityValueGreaterThanOneThrowAnException() {
    assertThrows(InvalidProbabilityValueException.class, () -> new BLXAlphaBetaCrossover(1.1, ALPHA, BETA));
  }

  @Test
  void shouldConstructorWhenPassedNullRandomGeneratorThrowAnException() {
    assertThrows(
        NullParameterException.class,
        () -> new BLXAlphaBetaCrossover(CROSSOVER_PROBABILITY, ALPHA, BETA, null));
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldExecuteWithTwoParentsReturnTwoOffspring() {
    // Mock random generator to control crossover application and alpha/beta calculations
    RandomGenerator<Double> randomGenerator = Mockito.mock(RandomGenerator.class);
    // First call: crossover probability check (0.1 < 0.9, so apply crossover)
    // Second call: first variable value for first child
    // Third call: second variable value for second child
    Mockito.when(randomGenerator.getRandomValue())
        .thenReturn(0.1)  // Will apply crossover (0.1 < 0.9)
        .thenReturn(0.4)  // First child value (between 0.5 and 1.5)
        .thenReturn(0.6); // Second child value (between 0.5 and 1.5)

    // Problem with bounds [0.0, 5.0] to allow our test values
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0, 0.0, 5.0);
    BLXAlphaBetaCrossover crossover =
        new BLXAlphaBetaCrossover(CROSSOVER_PROBABILITY, ALPHA, BETA, randomGenerator);

    // Create parent solutions with known values
    DoubleSolution parent1 = problem.createSolution();
    DoubleSolution parent2 = problem.createSolution();
    parent1.variables().set(0, 1.0);
    parent2.variables().set(0, 2.0);

    List<DoubleSolution> parents = List.of(parent1, parent2);
    List<DoubleSolution> offspring = crossover.execute(parents);

    // Verify the results
    assertNotNull(offspring);
    assertEquals(2, offspring.size(), "Should produce exactly 2 offspring");
    
    // With alpha=0.5, beta=0.5, and parents at 1.0 and 2.0:
    // - Range should be [1.0 - 0.5*1.0, 2.0 + 0.5*1.0] = [0.5, 2.5]
    // - With mock returning 0.4 and 0.6, we expect:
    //   - First child: 0.5 + 0.4 * (2.5 - 0.5) = 1.3
    //   - Second child: 0.5 + 0.6 * (2.5 - 0.5) = 1.7
    assertEquals(1.3, offspring.get(0).variables().get(0), EPSILON);
    assertEquals(1.7, offspring.get(1).variables().get(0), EPSILON);
    
    // Verify the random generator was called exactly three times
    Mockito.verify(randomGenerator, Mockito.times(3)).getRandomValue();
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldExecuteWithDifferentAlphaBetaValues() {
    // Mock random generator
    RandomGenerator<Double> randomGenerator = Mockito.mock(RandomGenerator.class);
    // First call: crossover probability check (apply crossover)
    // Second call: first variable value for first child
    Mockito.when(randomGenerator.getRandomValue())
        .thenReturn(0.1)  // Will apply crossover (0.1 < 0.9)
        .thenReturn(0.5); // Value within [0.5, 2.5]

    // Problem with bounds [0.0, 5.0]
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0, 0.0, 5.0);
    
    // Test with alpha=0.25, beta=0.75
    // For parents 1.0 and 2.0, range should be [1.0-0.25*1.0, 2.0+0.75*1.0] = [0.75, 2.75]
    BLXAlphaBetaCrossover crossover =
        new BLXAlphaBetaCrossover(CROSSOVER_PROBABILITY, 0.25, 0.75, randomGenerator);

    DoubleSolution parent1 = problem.createSolution();
    DoubleSolution parent2 = problem.createSolution();
    parent1.variables().set(0, 1.0);
    parent2.variables().set(0, 2.0);

    List<DoubleSolution> offspring = crossover.execute(List.of(parent1, parent2));
    
    // With mock returning 0.5, we expect: 0.75 + 0.5 * (2.75 - 0.75) = 1.75
    double expectedValue = 0.75 + 0.5 * (2.75 - 0.75);
    assertEquals(expectedValue, offspring.get(0).variables().get(0), EPSILON);
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldExecuteWithCrossoverProbabilityRespected() {
    // Mock random generator to control crossover application
    RandomGenerator<Double> randomGenerator = Mockito.mock(RandomGenerator.class);
    // First call is for crossover probability check (0.95 > 0.9, so no crossover)
    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.95);

    // Problem with bounds [0.0, 5.0]
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0, 0.0, 5.0);
    BLXAlphaBetaCrossover crossover =
        new BLXAlphaBetaCrossover(CROSSOVER_PROBABILITY, ALPHA, BETA, randomGenerator);

    // Create parent solutions with known values
    DoubleSolution parent1 = problem.createSolution();
    DoubleSolution parent2 = problem.createSolution();
    parent1.variables().set(0, 1.0);
    parent2.variables().set(0, 2.0);

    List<DoubleSolution> parents = List.of(parent1, parent2);
    List<DoubleSolution> offspring = crossover.execute(parents);

    // Since random value (0.95) > crossover probability (0.9), crossover should NOT be applied
    // Offspring should be exact copies of parents
    assertEquals(1.0, offspring.get(0).variables().get(0), EPSILON);
    assertEquals(2.0, offspring.get(1).variables().get(0), EPSILON);
    
    // Verify the random generator was called exactly once (only for probability check)
    Mockito.verify(randomGenerator, Mockito.times(1)).getRandomValue();
  }

  @Test
  void shouldGetNumberOfRequiredParentsReturnTheRightValue() {
    BLXAlphaBetaCrossover crossover = new BLXAlphaBetaCrossover(CROSSOVER_PROBABILITY, ALPHA, BETA);
    assertEquals(2, crossover.numberOfRequiredParents());
  }

  @Test
  void shouldGetNumberOfGeneratedChildrenReturnTheRightValue() {
    BLXAlphaBetaCrossover crossover = new BLXAlphaBetaCrossover(CROSSOVER_PROBABILITY, ALPHA, BETA);
    assertEquals(2, crossover.numberOfGeneratedChildren());
  }
}
