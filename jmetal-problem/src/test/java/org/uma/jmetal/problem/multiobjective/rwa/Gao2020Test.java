package org.uma.jmetal.problem.multiobjective.rwa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;

  @DisplayName("Class Gao2020")
  class Gao2020Test {

    private DoubleProblem problem;

    @BeforeEach
    void setup() {
      problem = new Gao2020();
    }

    @DisplayName("Its main properties are:")
    @Nested
    class MainProperties {

      @Test
      @DisplayName("Variables: 9")
      void theNumberOfVariablesIsCorrect() {
        assertEquals(9, problem.numberOfVariables());
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
      @DisplayName("Name: Gao2020")
      void theNameIsCorrect() {
        assertEquals("Gao2020", problem.name());
      }
    }

    @Nested
    @DisplayName("Its bounds are:")
    class Bounds {

      @ParameterizedTest
      @DisplayName("Lower bounds: ")
      @CsvSource({
          "0, 40.0",
          "1, 0.35",
          "2, 333.0",
          "3, 20.0",
          "4, 3000.0",
          "5, 0.1",
          "6, 308.0",
          "7, 150.0",
          "8, 0.1"
      })
      void checkLowerBounds(int boundIndex, double lowerBound) {
        assertEquals(lowerBound, problem.variableBounds().get(boundIndex).getLowerBound());
      }

      @ParameterizedTest
      @DisplayName("Upper bounds: ")
      @CsvSource({
          "0, 100.0",
          "1, 0.5",
          "2, 363.0",
          "3, 40.0",
          "4, 4000.0",
          "5, 3.0",
          "6, 328.0",
          "7, 200.0",
          "8, 2.0"
      })
      void checkUpperBounds(int boundIndex, double upperBound) {
        assertEquals(upperBound, problem.variableBounds().get(boundIndex).getUpperBound());
      }
    }
  }
