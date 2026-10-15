package org.uma.jmetal.problem.multiobjective.rwa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("Class RWA5")
class RWA5Test {

  private DoubleProblem problem;

    @BeforeEach
    void setup() {
      problem = new RWA5();
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
        assertEquals("RWA5", problem.name());
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

    @Nested
    @DisplayName("Its objectives match the reference C implementation (MO-RWA, Gao2020):")
    class Evaluation {

      @ParameterizedTest
      @DisplayName("Objective values: ")
      @CsvSource({
          "40.0, 0.35, 333.0, 20.0, 3000.0, 0.1, 308.0, 150.0, 0.1, 15661026.251024999, 9142503.3456149995, -55808.296513465",
          "100.0, 0.5, 363.0, 40.0, 4000.0, 3.0, 328.0, 200.0, 2.0, 27720222.637500003, 19209387.995499998, -107177.9621015",
          "70.0, 0.425, 348.0, 30.0, 3500.0, 1.55, 318.0, 175.0, 1.05, 21261026.737381257, 13743262.215203751, -79578.085333241252"
      })
      void checkObjectiveValues(double a, double b, double c, double d, double e, double f,
          double g, double h, double j, double f1, double f2, double f3) {
        DoubleSolution solution = problem.createSolution();
        double[] variables = {a, b, c, d, e, f, g, h, j};
        for (int i = 0; i < variables.length; i++) {
          solution.variables().set(i, variables[i]);
        }

        problem.evaluate(solution);

        assertEquals(f1, solution.objectives()[0], 1e-6);
        assertEquals(f2, solution.objectives()[1], 1e-6);
        assertEquals(f3, solution.objectives()[2], 1e-6);
      }
    }
  }
