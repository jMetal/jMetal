package org.uma.jmetal.problem.multiobjective.rwa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;

@DisplayName("Class RWA3")
class RWA3Test {
  private DoubleProblem problem;

  @BeforeEach
  void setup() {
    problem = new RWA3();
  }

  @DisplayName("Its main properties are:")
  @Nested
  class MainProperties {
    @Test
    @DisplayName("Variables: 3")
    void theNumberOfVariablesIsCorrect() {
      assertEquals(3, problem.numberOfVariables());
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
    @DisplayName("Name: Ganesan2013")
    void theNameIsCorrect() {
      assertEquals("RWA3", problem.name());
    }
  }

  @Nested
  @DisplayName("Its bounds are:")
  class Bounds {
    @ParameterizedTest
    @DisplayName("Lower bounds: ")
    @CsvSource({
        "0, 0.25",
        "1, 10000.0",
        "2, 600.0"
    })
    void checkLowerBounds(int boundIndex, double lowerBound) {
      assertEquals(lowerBound, problem.variableBounds().get(boundIndex).getLowerBound());
    }

    @ParameterizedTest
    @DisplayName("Upper bounds: ")
    @CsvSource({
        "0, 0.55",
        "1, 20000.0",
        "2, 1100.0"
    })
    void checkUpperBounds(int boundIndex, double upperBound) {
      assertEquals(upperBound, problem.variableBounds().get(boundIndex).getUpperBound());
    }
  }
}
