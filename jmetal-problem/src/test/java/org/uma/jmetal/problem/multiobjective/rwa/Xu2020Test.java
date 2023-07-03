package org.uma.jmetal.problem.multiobjective.rwa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;

@DisplayName("Class Xu2020")
class Xu2020Test {
  private DoubleProblem problem;

  @BeforeEach
  void setup() {
    problem = new Xu2020();
  }

  @DisplayName("Its main properties are:")
  @Nested
  class MainProperties {
    @Test
    @DisplayName("Variables: 4")
    void theNumberOfVariablesIsCorrect() {
      assertEquals(4, problem.numberOfVariables());
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
    @DisplayName("Name: Xu2020")
    void theNameIsCorrect() {
      assertEquals("Xu2020", problem.name());
    }
  }

  @Nested
  @DisplayName("Its bounds are:")
  class Bounds {
    @ParameterizedTest
    @DisplayName("Lower bounds: ")
    @CsvSource({
        "0, 12.56",
        "1, 0.02",
        "2, 1.0",
        "3, 0.5"
    })
    void checkLowerBounds(int boundIndex, double lowerBound) {
      assertEquals(lowerBound, problem.variableBounds().get(boundIndex).getLowerBound());
    }

    @ParameterizedTest
    @DisplayName("Upper bounds: ")
    @CsvSource({
        "0, 25.12",
        "1, 0.06",
        "2, 5.0",
        "3, 2.0"
    })
    void checkUpperBounds(int boundIndex, double upperBound) {
      assertEquals(upperBound, problem.variableBounds().get(boundIndex).getUpperBound());
    }
  }
}
