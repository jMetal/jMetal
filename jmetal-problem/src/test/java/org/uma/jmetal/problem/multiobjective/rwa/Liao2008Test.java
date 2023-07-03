package org.uma.jmetal.problem.multiobjective.rwa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;

@DisplayName("Class Liao2008")
class Liao2008Test {
  private DoubleProblem problem;

  @BeforeEach
  void setup() {
    problem = new Liao2008();
  }

  @DisplayName("Its main properties are:")
  @Nested
  class MainProperties {
    @Test
    @DisplayName("Variables: 5")
    void theNumberOfVariablesIsCorrect() {
      assertEquals(5, problem.numberOfVariables());
    }

    @Test
    @DisplayName("Objectives: 3")
    void theNumberOfObjectivesIsCorrect() {
      assertEquals(3, problem.numberOfObjectives());
    }

    @Test
    @DisplayName("Constraints: 0")
    void theNumberOfConstraintsIsCorrect() {
      assertEquals(0, problem.numberOfConstraints());
    }

    @Test
    @DisplayName("Name: Liao2008")
    void theNameIsCorrect() {
      assertEquals("Liao2008", problem.name());
    }
  }

  @Nested
  @DisplayName("Its bounds are:")
  class Bounds {
    @ParameterizedTest
    @DisplayName("Lower bounds: ")
    @CsvSource({
        "0, 1.0",
        "1, 1.0",
        "2, 1.0",
        "3, 1.0",
        "4, 1.0"
    })
    void checkLowerBounds(int boundIndex, double lowerBound) {
      assertEquals(lowerBound, problem.variableBounds().get(boundIndex).getLowerBound());
    }

    @ParameterizedTest
    @DisplayName("Upper bounds: ")
    @CsvSource({
        "0, 3.0",
        "1, 3.0",
        "2, 3.0",
        "3, 3.0",
        "4, 3.0"
    })
    void checkUpperBounds(int boundIndex, double upperBound) {
      assertEquals(upperBound, problem.variableBounds().get(boundIndex).getUpperBound());
    }
  }
}
