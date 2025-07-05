package org.uma.jmetal.operator.crossover;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.uma.jmetal.operator.crossover.impl.FuzzyRecombinationCrossover;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@ExtendWith(MockitoExtension.class)
class FuzzyRecombinationCrossoverTest {
  private static final double EPSILON = 0.0000000000001;
  private static final double DEFAULT_ALPHA = 1.0;
  private static final double CROSSOVER_PROBABILITY = 0.9;

  @Test
  void shouldConstructorAssignTheCorrectProbabilityValue() {
    final double crossoverProbability = 0.25;
    final FuzzyRecombinationCrossover crossover =
        new FuzzyRecombinationCrossover(crossoverProbability);
    assertEquals(crossoverProbability, crossover.crossoverProbability());
  }

  @Test
  void shouldConstructorAssignTheDefaultAlphaValue() {
    final FuzzyRecombinationCrossover crossover =
        new FuzzyRecombinationCrossover(CROSSOVER_PROBABILITY);
    assertEquals(DEFAULT_ALPHA, crossover.alpha(), EPSILON);
  }

  @Test
  void shouldConstructorAssignTheCorrectAlphaValue() {
    final double alpha = 0.5;
    final FuzzyRecombinationCrossover crossover =
        new FuzzyRecombinationCrossover(
            CROSSOVER_PROBABILITY, alpha, new RepairDoubleSolutionWithBoundValue());
    assertEquals(alpha, crossover.alpha(), EPSILON);
  }

  @Test
  void shouldConstructorWhenPassedANegativeAlphaValueThrowAnException() {
    final double alpha = -0.1;
    assertThrows(
        InvalidConditionException.class,
        () ->
            new FuzzyRecombinationCrossover(
                CROSSOVER_PROBABILITY, alpha, new RepairDoubleSolutionWithBoundValue()));
  }

  @Test
  void shouldConstructorWhenPassedZeroAlphaValueThrowAnException() {
    final double alpha = 0.0;
    assertThrows(
        InvalidConditionException.class,
        () ->
            new FuzzyRecombinationCrossover(
                CROSSOVER_PROBABILITY, alpha, new RepairDoubleSolutionWithBoundValue()));
  }

  @Test
  void shouldConstructorWhenPassedNullRepairDoubleSolutionThrowAnException() {
    assertThrows(
        NullParameterException.class,
        () -> new FuzzyRecombinationCrossover(CROSSOVER_PROBABILITY, DEFAULT_ALPHA, null));
  }

  @Test
  void shouldConstructorWhenPassedNullRandomGeneratorThrowAnException() {
    assertThrows(
        NullParameterException.class,
        () ->
            new FuzzyRecombinationCrossover(
                CROSSOVER_PROBABILITY,
                DEFAULT_ALPHA,
                new RepairDoubleSolutionWithBoundValue(),
                null));
  }

  @Test
  void shouldExecuteWithTwoParentsReturnTwoOffspring() {
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    FuzzyRecombinationCrossover crossover =
        new FuzzyRecombinationCrossover(CROSSOVER_PROBABILITY);

    DoubleSolution parent1 = problem.createSolution();
    DoubleSolution parent2 = problem.createSolution();
    
    parent1.variables().set(0, 1.0);
    parent2.variables().set(0, 2.0);

    List<DoubleSolution> parents = List.of(parent1, parent2);
    List<DoubleSolution> offspring = crossover.execute(parents);

    assertNotNull(offspring);
    assertEquals(2, offspring.size());
    assertNotSame(parent1, offspring.get(0));
    assertNotSame(parent2, offspring.get(1));
  }

  @Test
  void shouldExecuteWithIdenticalParentsReturnOffspringWithSameValues() {
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    // Using a small alpha value to minimize variation
    FuzzyRecombinationCrossover crossover =
        new FuzzyRecombinationCrossover(1.0, 0.1, new RepairDoubleSolutionWithBoundValue());

    DoubleSolution parent1 = problem.createSolution();
    DoubleSolution parent2 = problem.createSolution();
    
    double value = 1.5;
    parent1.variables().set(0, value);
    parent2.variables().set(0, value);

    List<DoubleSolution> parents = List.of(parent1, parent2);
    List<DoubleSolution> offspring = crossover.execute(parents);

    assertEquals(value, offspring.get(0).variables().get(0), EPSILON);
    assertEquals(value, offspring.get(1).variables().get(0), EPSILON);
  }

  @Test
  void shouldExecuteWithDifferentVariableValuesRespectBounds() {
    DoubleProblem problem = new FakeDoubleProblem(3, 2, 0);
    FuzzyRecombinationCrossover crossover =
        new FuzzyRecombinationCrossover(1.0, 1.5, new RepairDoubleSolutionWithBoundValue());

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
        assertTrue(value >= lowerBound && value <= upperBound,
            String.format("Value %.2f is out of bounds [%.2f, %.2f]", 
                value, lowerBound, upperBound));
      }
    }
  }

  @Test
  void shouldExecuteWithAlphaGreaterThanOneExpandRange() {
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    double alpha = 2.0;
    FuzzyRecombinationCrossover crossover =
        new FuzzyRecombinationCrossover(1.0, alpha, new RepairDoubleSolutionWithBoundValue());

    // Create two parents with values 1.0 and 2.0
    DoubleSolution parent1 = problem.createSolution();
    DoubleSolution parent2 = problem.createSolution();
    
    parent1.variables().set(0, 1.0);
    parent2.variables().set(0, 2.0);

    List<DoubleSolution> parents = List.of(parent1, parent2);
    List<DoubleSolution> offspring = crossover.execute(parents);

    // With alpha=2.0, the range should be expanded to [1-1, 2+1] = [0, 3]
    double child1Value = offspring.get(0).variables().get(0);
    double child2Value = offspring.get(1).variables().get(0);
    
    assertTrue(child1Value >= 0.0 && child1Value <= 3.0);
    assertTrue(child2Value >= 0.0 && child2Value <= 3.0);
  }

  @Test
  void shouldGetNumberOfRequiredParentsReturnTheRightValue() {
    FuzzyRecombinationCrossover crossover =
        new FuzzyRecombinationCrossover(CROSSOVER_PROBABILITY);
    assertEquals(2, crossover.numberOfRequiredParents());
  }

  @Test
  void shouldGetNumberOfGeneratedChildrenReturnTheRightValue() {
    FuzzyRecombinationCrossover crossover =
        new FuzzyRecombinationCrossover(CROSSOVER_PROBABILITY);
    assertEquals(2, crossover.numberOfGeneratedChildren());
  }
}
