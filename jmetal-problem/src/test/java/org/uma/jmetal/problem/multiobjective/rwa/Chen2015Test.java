package org.uma.jmetal.problem.multiobjective.rwa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;

@DisplayName("Class Chen2015")
class Chen2015Test {
  private DoubleProblem problem;

  @BeforeEach
  void setup() {
    problem = new Chen2015();
  }

  @DisplayName("Its main properties are:")
  @Nested
  class MainProperties {
    @Test
    @DisplayName("Variables: 6")
    void theNumberOfVariablesIsCorrect() {
      assertEquals(6, problem.numberOfVariables());
    }

    @Test
    @DisplayName("Objectives: 5")
    void theNumberOfObjectivesIsCorrect() {
      assertEquals(5, problem.numberOfObjectives());
    }

    @Test
    @DisplayName("Constraints: 0")
    void theNumberOfConstraintsIsCorrect() {
      assertEquals(0, problem.numberOfConstraints());
    }

    @Test
    @DisplayName("Name: Chen2015")
    void theNameIsCorrect() {
      assertEquals("Chen2015", problem.name());
    }
  }

  @Nested
  @DisplayName("Its bounds are:")
  class Bounds {
    @ParameterizedTest
    @DisplayName("Lower bounds: ")
    @CsvSource({
        "0, 17.5",
        "1, 17.5",
        "2, 2.0",
        "3, 2.0",
        "4, 5.0",
        "5, 5.0"
    })
    void checkLowerBounds(int boundIndex, double lowerBound) {
      assertEquals(lowerBound, problem.variableBounds().get(boundIndex).getLowerBound());
    }

    @ParameterizedTest
    @DisplayName("Upper bounds: ")
    @CsvSource({
        "0, 22.5",
        "1, 22.5",
        "2, 3.0",
        "3, 3.0",
        "4, 7.0",
        "5, 6.0"
    })
    void checkUpperBounds(int boundIndex, double upperBound) {
      assertEquals(upperBound, problem.variableBounds().get(boundIndex).getUpperBound());
    }
  }
}
