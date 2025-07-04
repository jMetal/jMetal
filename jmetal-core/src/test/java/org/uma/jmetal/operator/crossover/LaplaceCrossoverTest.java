package org.uma.jmetal.operator.crossover;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.uma.jmetal.operator.crossover.impl.LaplaceCrossover;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@ExtendWith(MockitoExtension.class)
class LaplaceCrossoverTest {
  private static final double EPSILON = 0.0000000000001;
  private static final double DEFAULT_SCALE = 0.5;
  private static final double CROSSOVER_PROBABILITY = 0.9;

  @Test
  void shouldConstructorAssignTheCorrectProbabilityValue() {
    final double crossoverProbability = 0.25;
    final LaplaceCrossover crossover = new LaplaceCrossover(crossoverProbability);
    assertEquals(crossoverProbability, crossover.crossoverProbability());
  }

  @Test
  void shouldConstructorAssignTheDefaultScaleValue() {
    final LaplaceCrossover crossover = new LaplaceCrossover(CROSSOVER_PROBABILITY);
    assertEquals(DEFAULT_SCALE, crossover.getScale());
  }

  @Test
  void shouldConstructorAssignTheCorrectScaleValue() {
    final double scale = 0.25;
    final LaplaceCrossover crossover =
        new LaplaceCrossover(CROSSOVER_PROBABILITY, scale, new RepairDoubleSolutionWithBoundValue());
    assertEquals(scale, crossover.getScale(), EPSILON);
  }

  @Test
  void shouldConstructorWhenPassedANegativeScaleValueThrowAnException() {
    final double scale = -0.1;
    assertThrows(
        InvalidConditionException.class,
        () -> new LaplaceCrossover(CROSSOVER_PROBABILITY, scale, new RepairDoubleSolutionWithBoundValue()));
  }

  @Test
  void shouldConstructorWhenPassedANullRepairDoubleSolutionThrowAnException() {
    assertThrows(
        NullParameterException.class,
        () -> new LaplaceCrossover(CROSSOVER_PROBABILITY, DEFAULT_SCALE, null));
  }

  @Test
  void shouldExecuteWithDefaultParametersReturnTwoValidSolutions() {
    // Given
    LaplaceCrossover crossover = new LaplaceCrossover(CROSSOVER_PROBABILITY);
    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    
    // Create two parent solutions with different values
    DoubleSolution parent1 = problem.createSolution();
    parent1.variables().set(0, 0.5);
    parent1.variables().set(1, 0.5);
    
    DoubleSolution parent2 = problem.createSolution();
    parent2.variables().set(0, 0.6);
    parent2.variables().set(1, 0.4);
    
    // When
    List<DoubleSolution> children = crossover.execute(List.of(parent1, parent2));
    
    // Then
    assertEquals(2, children.size());
    assertNotSame(children.get(0), children.get(1));
    
    // Check that variables are within bounds
    for (DoubleSolution child : children) {
      for (int i = 0; i < child.variables().size(); i++) {
        double value = child.variables().get(i);
        assertTrue(value >= problem.variableBounds().get(i).getLowerBound() && 
                  value <= problem.variableBounds().get(i).getUpperBound(),
            "Value " + value + " is out of bounds for variable " + i);
      }
    }
  }

  @Test
  void shouldExecuteWithDifferentScaleProduceDifferentResults() {
    // Given
    double scale1 = 0.1;
    double scale2 = 0.9;
    
    LaplaceCrossover crossover1 = new LaplaceCrossover(1.0, scale1, new RepairDoubleSolutionWithBoundValue());
    LaplaceCrossover crossover2 = new LaplaceCrossover(1.0, scale2, new RepairDoubleSolutionWithBoundValue());
    
    // Create two parent solutions with different values
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    DoubleSolution parent1 = problem.createSolution();
    parent1.variables().set(0, 0.5);
    DoubleSolution parent2 = problem.createSolution();
    parent2.variables().set(0, 0.6);
    
    // When
    List<DoubleSolution> children1 = crossover1.execute(List.of(parent1, parent2));
    List<DoubleSolution> children2 = crossover2.execute(List.of(parent1, parent2));
    
    // Then
    assertNotEquals(children1.get(0).variables().get(0), children2.get(0).variables().get(0));
    assertNotEquals(children1.get(1).variables().get(0), children2.get(1).variables().get(0));
  }

  @Test
  void shouldExecuteWithProbabilityZeroReturnParents() {
    // Given
    LaplaceCrossover crossover = new LaplaceCrossover(0.0);
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    
    DoubleSolution parent1 = problem.createSolution();
    parent1.variables().set(0, 0.5);
    DoubleSolution parent2 = problem.createSolution();
    parent2.variables().set(0, 0.6);
    
    // When
    List<DoubleSolution> children = crossover.execute(List.of(parent1, parent2));
    
    // Then
    assertEquals(parent1.variables().get(0), children.get(0).variables().get(0));
    assertEquals(parent2.variables().get(0), children.get(1).variables().get(0));
  }

  @Test
  void shouldExecuteWithIdenticalParentsReturnIdenticalChildren() {
    // Given
    LaplaceCrossover crossover = new LaplaceCrossover(1.0);
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    
    DoubleSolution parent1 = problem.createSolution();
    parent1.variables().set(0, 0.5);
    DoubleSolution parent2 = problem.createSolution();
    parent2.variables().set(0, 0.5); // Same as parent1
    
    // When
    List<DoubleSolution> children = crossover.execute(List.of(parent1, parent2));
    
    // Then
    assertEquals(children.get(0).variables().get(0), children.get(1).variables().get(0));
  }

  @Test
  void shouldExecuteWithBoundaryValuesWorkCorrectly() {
    // Given
    LaplaceCrossover crossover = new LaplaceCrossover(1.0, 0.5, new RepairDoubleSolutionWithBoundValue());
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    
    // Test with values at the boundaries
    DoubleSolution parent1 = problem.createSolution();
    parent1.variables().set(0, 0.0);
    DoubleSolution parent2 = problem.createSolution();
    parent2.variables().set(0, 1.0);
    
    // When
    List<DoubleSolution> children = crossover.execute(List.of(parent1, parent2));
    
    // Then
    for (DoubleSolution child : children) {
      double value = child.variables().get(0);
      assertTrue(value >= 0.0 && value <= 1.0, "Value " + value + " is out of bounds");
    }
  }

  @Test
  void shouldExecuteWithMultipleVariablesWorkCorrectly() {
    // Given
    LaplaceCrossover crossover = new LaplaceCrossover(1.0);
    
    // Create a problem with 3 variables with different bounds
    DoubleProblem problem = new FakeDoubleProblem(3, 2, 0) {
      @Override
      public DoubleSolution evaluate(DoubleSolution solution) {
        // No need to implement for this test
        return solution;
      }
    };
    
    DoubleSolution parent1 = problem.createSolution();
    parent1.variables().set(0, 0.5);
    parent1.variables().set(1, 0.0);
    parent1.variables().set(2, 5.0);
    
    DoubleSolution parent2 = problem.createSolution();
    parent2.variables().set(0, 0.6);
    parent2.variables().set(1, 0.5);
    parent2.variables().set(2, 4.0);
    
    // When
    List<DoubleSolution> children = crossover.execute(List.of(parent1, parent2));
    
    // Then
    assertEquals(2, children.size());
    
    for (DoubleSolution child : children) {
      for (int i = 0; i < child.variables().size(); i++) {
        double value = child.variables().get(i);
        assertTrue(value >= problem.variableBounds().get(i).getLowerBound() && 
                  value <= problem.variableBounds().get(i).getUpperBound(),
            "Value " + value + " is out of bounds for variable " + i);
      }
    }
  }
}
