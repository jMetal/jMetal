package org.uma.jmetal.component.catalogue.ea.variation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.variation.impl.DifferentialEvolutionCrossoverVariation;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.sequencegenerator.impl.CyclicIntegerSequence;

@DisplayName("Unit tests for class DifferentialEvolutionCrossoverVariation")
class DifferentialEvolutionCrossoverVariationTest {
  private static final int NUMBER_OF_VARIABLES = 10;
  private static final DoubleProblem PROBLEM = new ZDT1(NUMBER_OF_VARIABLES);

  private static DoubleSolution solution(double value) {
    DoubleSolution solution = PROBLEM.createSolution();
    for (int i = 0; i < NUMBER_OF_VARIABLES; i++) {
      solution.variables().set(i, value);
    }
    return solution;
  }

  private static List<DoubleSolution> solutions(int size, double first) {
    List<DoubleSolution> solutions = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      solutions.add(solution(first + i / 100.0));
    }
    return solutions;
  }

  private static int sameVariables(DoubleSolution first, DoubleSolution second) {
    int same = 0;
    for (int i = 0; i < NUMBER_OF_VARIABLES; i++) {
      if (first.variables().get(i).equals(second.variables().get(i))) {
        same++;
      }
    }
    return same;
  }

  @Test
  @DisplayName("given an offspring population of one, when varying, then the offspring is the current solution except one variable")
  void givenOneOffspring_whenVarying_thenItIsTheCurrentSolutionExceptOneVariable() {
    // Arrange: with a crossover probability of zero only the variable that is always crossed changes
    var crossover = new DifferentialEvolutionCrossover(0.0, 0.5, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);
    var sequence = new CyclicIntegerSequence(5);
    sequence.generateNext();
    sequence.generateNext();
    var variation = new DifferentialEvolutionCrossoverVariation(1, crossover, sequence);
    List<DoubleSolution> population = solutions(5, 0.1);

    // Act
    List<DoubleSolution> offspring = variation.variate(population, solutions(3, 0.6));

    // Assert
    assertThat(offspring).hasSize(1);
    assertThat(sameVariables(offspring.get(0), population.get(2))).isGreaterThanOrEqualTo(NUMBER_OF_VARIABLES - 1);
  }

  @Test
  @DisplayName("given several offspring, when varying, then each one uses its own parents and its own current solution")
  void givenSeveralOffspring_whenVarying_thenEachOneUsesItsOwnParentsAndCurrentSolution() {
    // Arrange
    var crossover = new DifferentialEvolutionCrossover(0.0, 0.5, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);
    var variation = new DifferentialEvolutionCrossoverVariation(3, crossover, new CyclicIntegerSequence(5));
    List<DoubleSolution> population = solutions(5, 0.1);
    List<DoubleSolution> matingPool = solutions(variation.matingPoolSize(), 0.6);

    // Act
    List<DoubleSolution> offspring = variation.variate(population, matingPool);

    // Assert: the current solutions are the ones of the positions 0, 1 and 2
    assertThat(variation.matingPoolSize()).isEqualTo(9);
    assertThat(offspring).hasSize(3);
    for (int i = 0; i < 3; i++) {
      assertThat(sameVariables(offspring.get(i), population.get(i)))
          .as("offspring %d", i)
          .isGreaterThanOrEqualTo(NUMBER_OF_VARIABLES - 1);
    }
  }
}
