package org.uma.jmetal.operator.crossover;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.crossover.impl.TwoPointCrossover;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.problem.integerproblem.IntegerProblem;
import org.uma.jmetal.problem.integerproblem.impl.FakeIntegerProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TwoPointCrossoverTest {
  @Test
  void theConstructorWorksForDoubleSolutions() {
    // Arrange
    double probability = 0.5;

    // Act
    CrossoverOperator<DoubleSolution> crossover =
        new TwoPointCrossover<DoubleSolution, Double>(probability);

    // Assert
    assertEquals(probability, crossover.crossoverProbability());
  }

  @Test
  void recombineTwoDoubleSolutionsWithProbabilityEqualsToZeroReturnsTheParents() {
    // Arrange
    DoubleProblem problem = new FakeDoubleProblem(10, 2, 0);
    DoubleSolution parent1 = problem.createSolution();
    DoubleSolution parent2 = problem.createSolution();

    var crossover = new TwoPointCrossover<DoubleSolution, Double>(0.0);

    // Act
    List<DoubleSolution> children = crossover.execute(List.of(parent1, parent2));

    // Assert
    assertEquals(parent1, children.get(0));
    assertEquals(parent2, children.get(1));
  }

  @Test
  void recombineTwoDoubleSolutionsWithProbabilityEqualsToOneReturnsChildrenDifferentToTheParents() {
    // Arrange
    DoubleProblem problem = new FakeDoubleProblem(10, 2, 0);
    DoubleSolution parent1 = problem.createSolution();
    DoubleSolution parent2 = problem.createSolution();

    var crossover = new TwoPointCrossover<DoubleSolution, Double>(1.0);

    // Act
    List<DoubleSolution> children = crossover.execute(List.of(parent1, parent2));

    // Assert
    assertNotEquals(parent1, children.get(0));
    assertNotEquals(parent2, children.get(1));
  }

  @Test
  void recombineTwoIntegerSolutionsWithProbabilityEqualsToZeroReturnsTheParents() {
    // Arrange
    IntegerProblem problem = new FakeIntegerProblem(10, 2, 0);
    var parent1 = problem.createSolution();
    var parent2 = problem.createSolution();

    var crossover = new TwoPointCrossover<IntegerSolution, Integer>(0.0);

    // Act
    List<IntegerSolution> children = crossover.execute(List.of(parent1, parent2));

    // Assert
    assertEquals(parent1, children.get(0));
    assertEquals(parent2, children.get(1));
  }
}
