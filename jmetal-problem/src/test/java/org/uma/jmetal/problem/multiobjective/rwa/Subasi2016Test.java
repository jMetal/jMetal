package org.uma.jmetal.problem.multiobjective.rwa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;

@DisplayName("Class Subasi2016")
class Subasi2016Test {
  private DoubleProblem problem;

  @BeforeEach
  void setup() {
    problem = new Subasi2016();
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
    @DisplayName("Objectives: 2")
    void theNumberOfObjectivesIsCorrect() {
      assertEquals(2, problem.numberOfObjectives());
    }

    @Test
    @DisplayName("Constraints: 0")
    void theNumberOfConstraintsIsCorrect() {
      assertEquals(0, problem.numberOfConstraints());
    }

    @Test
    @DisplayName("Name: Subasi2016")
    void theNameIsCorrect() {
      assertEquals("Subasi2016", problem.name());
    }
  }

  @Nested
  @DisplayName("Its bounds are:")
  class Bounds {
    @ParameterizedTest
    @DisplayName("Lower bounds: ")
    @CsvSource({
        "0, 20.0",
        "1, 6.0",
        "2, 20.0",
        "3, 0.0",
        "4, 8000.0"
    })
    void checkLowerBounds(int boundIndex, double lowerBound) {
      assertEquals(lowerBound, problem.variableBounds().get(boundIndex).getLowerBound());
    }

    @ParameterizedTest
    @DisplayName("Upper bounds: ")
    @CsvSource({
        "0, 60.0",
        "1, 15.0",
        "2, 40.0",
        "3, 30.0",
        "4, 25000.0"
    })
    void checkUpperBounds(int boundIndex, double upperBound) {
      assertEquals(upperBound, problem.variableBounds().get(boundIndex).getUpperBound());
    }
  }
}
