package org.uma.jmetal.component.catalogue.common.solutionscreation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.CauchySolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.NegativeValueException;
import org.uma.jmetal.util.errorchecking.exception.NonPositiveValueException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

class CauchySolutionsCreationTest {

  @Nested
  @DisplayName("Constructor tests")
  class ConstructorTests {
    @Test
    void nullProblemThrowsException() {
      assertThrows(
          NullParameterException.class,
          () -> new CauchySolutionsCreation(null, 10));
    }

    @Test
    void negativeNumberOfSolutionsThrowsException() {
      DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      assertThrows(
          NonPositiveValueException.class,
          () -> new CauchySolutionsCreation(problem, -1));
    }

    @Test
    void nonPositiveScaleFactorThrowsException() {
      DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      assertThrows(
          NonPositiveValueException.class,
          () -> new CauchySolutionsCreation(problem, 10, -0.5));
    }

    @Test
    void validParametersCreateInstance() {
      DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      CauchySolutionsCreation creation = new CauchySolutionsCreation(problem, 10, 0.15);

      assertNotNull(creation);
      assertEquals(10, creation.getNumberOfSolutionsToCreate());
      assertEquals(0.15, creation.getScaleFactor(), 0.0001);
    }
  }

  @Nested
  @DisplayName("Create tests")
  class CreateTests {
    @Test
    void createReturnsCorrectNumberOfSolutions() {
      DoubleProblem problem = new FakeDoubleProblem(3, 2, 0);
      int populationSize = 15;
      CauchySolutionsCreation creation = new CauchySolutionsCreation(problem, populationSize);

      List<DoubleSolution> solutions = creation.create();

      assertNotNull(solutions);
      assertEquals(populationSize, solutions.size());
    }

    @Test
    void solutionsAreWithinBounds() {
      DoubleProblem problem = new FakeDoubleProblem(3, 2, 0);
      int populationSize = 100;
      CauchySolutionsCreation creation = new CauchySolutionsCreation(problem, populationSize, 2.0); // High scale to
                                                                                                    // force clamping

      List<DoubleSolution> solutions = creation.create();

      for (DoubleSolution solution : solutions) {
        for (int i = 0; i < problem.numberOfVariables(); i++) {
          double value = solution.variables().get(i);
          double lowerBound = problem.variableBounds().get(i).getLowerBound();
          double upperBound = problem.variableBounds().get(i).getUpperBound();

          assertTrue(value >= lowerBound);
          assertTrue(value <= upperBound);
        }
      }
    }
  }
}
