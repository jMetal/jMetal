package org.uma.jmetal.operator.crossover;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.uma.jmetal.operator.crossover.impl.ArithmeticCrossover;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

@ExtendWith(MockitoExtension.class)
class ArithmeticCrossoverTest {
  private static final double EPSILON = 0.0000000000001;
  private static final double CROSSOVER_PROBABILITY = 0.9;

  @Test
  void shouldConstructorAssignTheCorrectProbabilityValue() {
    final double crossoverProbability = 0.25;
    final ArithmeticCrossover crossover = new ArithmeticCrossover(crossoverProbability);
    assertEquals(crossoverProbability, crossover.crossoverProbability());
  }

  @Test
  void shouldConstructorWhenPassedNullRepairDoubleSolutionThrowAnException() {
    assertThrows(
        NullParameterException.class,
        () -> new ArithmeticCrossover(CROSSOVER_PROBABILITY, null));
  }

  @Test
  void shouldConstructorWhenPassedNullRandomGeneratorThrowAnException() {
    assertThrows(
        NullParameterException.class,
        () ->
            new ArithmeticCrossover(
                CROSSOVER_PROBABILITY,
                new RepairDoubleSolutionWithBoundValue(),
                null));
  }

  @Test
  void shouldExecuteWithTwoParentsReturnTwoOffspring() {
    // Mock random generator to return values for both crossover probability and alpha
    @SuppressWarnings("unchecked")
    RandomGenerator<Double> randomGenerator = Mockito.mock(RandomGenerator.class);
    
    // Set up the mock to return specific values in sequence
    Mockito.when(randomGenerator.getRandomValue())
        .thenReturn(0.1)  // First call: crossover probability check (0.1 < 0.9, so apply crossover)
        .thenReturn(0.5); // Second call: alpha value for arithmetic crossover

    // Create the crossover with the mock random generator
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0, 0.0, 5.0);
    RepairDoubleSolution repair = new RepairDoubleSolutionWithBoundValue();
    ArithmeticCrossover crossover = new ArithmeticCrossover(
        CROSSOVER_PROBABILITY, 
        repair,
        randomGenerator
    );

    // Create parent solutions with known values
    DoubleSolution parent1 = problem.createSolution();
    DoubleSolution parent2 = problem.createSolution();
    parent1.variables().set(0, 1.0);
    parent2.variables().set(0, 2.0);

    System.out.println("=== Test: shouldExecuteWithTwoParentsReturnTwoOffspring ===");
    System.out.println("Parent 1: " + parent1.variables().get(0));
    System.out.println("Parent 2: " + parent2.variables().get(0));

    // Execute crossover
    List<DoubleSolution> parents = List.of(parent1, parent2);
    List<DoubleSolution> offspring = crossover.execute(parents);

    // Verify the results
    assertNotNull(offspring);
    assertEquals(2, offspring.size(), "Should produce exactly 2 offspring");
    
    // Verify the random generator was called exactly twice
    Mockito.verify(randomGenerator, Mockito.times(2)).getRandomValue();
    
    // Get the actual values from the offspring
    double child1Value = offspring.get(0).variables().get(0);
    double child2Value = offspring.get(1).variables().get(0);
    
    System.out.println("Child 1: " + child1Value);
    System.out.println("Child 2: " + child2Value);
    
    // With alpha=0.5, both children should be the average of parents (1.5)
    double expectedValue = 1.5;
    System.out.println("Expected value: " + expectedValue);
    
    // Verify the values match our expectations
    assertEquals(expectedValue, child1Value, EPSILON, 
        String.format("Expected child 1 to be %.2f but was %.2f", expectedValue, child1Value));
    assertEquals(expectedValue, child2Value, EPSILON,
        String.format("Expected child 2 to be %.2f but was %.2f", expectedValue, child2Value));
  }

  @Test
  void shouldExecuteWithDifferentAlphaValues() {
    // Mock random generator to return values for both crossover probability and alpha
    @SuppressWarnings("unchecked")
    RandomGenerator<Double> randomGenerator = Mockito.mock(RandomGenerator.class);
    // First call: crossover probability check (return value < 1.0 to apply crossover)
    // Second call: alpha value for arithmetic crossover
    Mockito.when(randomGenerator.getRandomValue())
        .thenReturn(0.5)   // Will apply crossover (0.5 < 1.0)
        .thenReturn(0.25); // alpha = 0.25

    // Use bounds that can accommodate the test values (1.0 and 3.0)
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0, 0.0, 5.0);
    ArithmeticCrossover crossover =
        new ArithmeticCrossover(
            1.0, // Always apply crossover
            new RepairDoubleSolutionWithBoundValue(),
            randomGenerator);

    DoubleSolution parent1 = problem.createSolution();
    DoubleSolution parent2 = problem.createSolution();
    
    parent1.variables().set(0, 1.0);
    parent2.variables().set(0, 3.0);

    List<DoubleSolution> parents = List.of(parent1, parent2);
    List<DoubleSolution> offspring = crossover.execute(parents);

    // With alpha=0.25:
    // child1 = 0.25*1.0 + 0.75*3.0 = 2.5
    // child2 = 0.75*1.0 + 0.25*3.0 = 1.5
    assertEquals(2.5, offspring.get(0).variables().get(0), EPSILON);
    assertEquals(1.5, offspring.get(1).variables().get(0), EPSILON);
  }

  @Test
  void shouldExecuteWithDifferentVariableValuesRespectBounds() {
    // Use bounds that can accommodate the test values (1.0-4.0)
    DoubleProblem problem = new FakeDoubleProblem(3, 2, 0, 0.0, 5.0);
    ArithmeticCrossover crossover =
        new ArithmeticCrossover(1.0, new RepairDoubleSolutionWithBoundValue());

    // Create two parents with different values
    DoubleSolution parent1 = problem.createSolution();
    DoubleSolution parent2 = problem.createSolution();
    
    parent1.variables().set(0, 1.0);
    parent1.variables().set(1, 2.0);
    parent1.variables().set(2, 3.0);
    
    parent2.variables().set(0, 2.0);
    parent2.variables().set(1, 3.0);
    parent2.variables().set(2, 4.0);

    List<DoubleSolution> parents = List.of(parent1, parent2);
    List<DoubleSolution> offspring = crossover.execute(parents);

    // Check that all variable values are within bounds
    for (int i = 0; i < 2; i++) {
      DoubleSolution child = offspring.get(i);
      for (int j = 0; j < 3; j++) {
        double value = child.variables().get(j);
        double lowerBound = child.getBounds(j).getLowerBound();
        double upperBound = child.getBounds(j).getUpperBound();
        assertTrue(
            value >= lowerBound && value <= upperBound,
            String.format(
                "Value %.2f is out of bounds [%.2f, %.2f]", value, lowerBound, upperBound));
      }
    }
  }

  @Test
  void shouldExecuteWithCrossoverProbabilityRespected() {
    // Mock random generator to control crossover application
    @SuppressWarnings("unchecked")
    RandomGenerator<Double> randomGenerator = Mockito.mock(RandomGenerator.class);
    // First call is for crossover probability check (0.4 < 0.5, so crossover is applied)
    // Second call is for alpha value (0.5)
    Mockito.when(randomGenerator.getRandomValue())
        .thenReturn(0.4) // Will apply crossover (0.4 < 0.5)
        .thenReturn(0.5); // alpha = 0.5

    // Use bounds that can accommodate the test values (1.0 and 2.0)
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0, 0.0, 5.0);
    // Crossover probability = 0.5
    ArithmeticCrossover crossover =
        new ArithmeticCrossover(0.5, new RepairDoubleSolutionWithBoundValue(), randomGenerator);

    DoubleSolution parent1 = problem.createSolution();
    DoubleSolution parent2 = problem.createSolution();
    
    parent1.variables().set(0, 1.0);
    parent2.variables().set(0, 2.0);

    List<DoubleSolution> parents = List.of(parent1, parent2);
    List<DoubleSolution> offspring = crossover.execute(parents);

    // Since random value (0.4) < crossover probability (0.5), crossover should be applied
    // With alpha=0.5, both children should be the average of parents (1.5)
    double expectedValue = 1.5;
    assertEquals(expectedValue, offspring.get(0).variables().get(0), EPSILON,
        "First offspring should be the average of parents");
    assertEquals(expectedValue, offspring.get(1).variables().get(0), EPSILON,
        "Second offspring should be the average of parents");
    
    // Verify the random generator was called exactly twice
    Mockito.verify(randomGenerator, Mockito.times(2)).getRandomValue();
  }

  @Test
  void shouldGetNumberOfRequiredParentsReturnTheRightValue() {
    ArithmeticCrossover crossover = new ArithmeticCrossover(CROSSOVER_PROBABILITY);
    assertEquals(2, crossover.numberOfRequiredParents());
  }

  @Test
  void shouldGetNumberOfGeneratedChildrenReturnTheRightValue() {
    ArithmeticCrossover crossover = new ArithmeticCrossover(CROSSOVER_PROBABILITY);
    assertEquals(2, crossover.numberOfGeneratedChildren());
  }
}
