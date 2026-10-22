package org.uma.jmetal.algorithm.multiobjective.nsgaiii.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("Unit tests for the normalization of class EnvironmentalSelection of NSGA-III")
class EnvironmentalSelectionNormalizationTest {
  private static final double EPSILON = 1e-9;

  private static DoubleSolution solution(double... objectives) {
    DoubleSolution solution =
        new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), objectives.length, 0);
    System.arraycopy(objectives, 0, solution.objectives(), 0, objectives.length);
    return solution;
  }

  private static EnvironmentalSelection<DoubleSolution> selection(
      List<List<DoubleSolution>> fronts, int numberOfObjectives) {
    return new EnvironmentalSelection<>(fronts, 1, new ArrayList<>(), numberOfObjectives);
  }

  /** The line f1 + f2 = 1 translated by the shift: its extreme points are the first and last. */
  private static List<DoubleSolution> line(double shift) {
    return new ArrayList<>(
        List.of(
            solution(shift, 1.0 + shift),
            solution(0.5 + shift, 0.5 + shift),
            solution(1.0 + shift, shift)));
  }

  private static List<Double> translated(
      EnvironmentalSelection<DoubleSolution> selection, DoubleSolution solution) {
    return selection.getAttribute(solution);
  }

  @Nested
  @DisplayName("When translating the objectives")
  class WhenTranslatingTheObjectives {
    @Test
    @DisplayName("given fronts, then the ideal point is the minimum of the first front")
    void givenFronts_whenTranslating_thenTheIdealPointIsTheMinimumOfTheFirstFront() {
      // Arrange: the second front has lower values in the second objective, which are ignored
      DoubleSolution a = solution(2.0, 5.0);
      DoubleSolution b = solution(4.0, 3.0);
      DoubleSolution c = solution(5.0, 1.0);
      var selection = selection(List.of(List.of(a, b), List.of(c)), 2);

      // Act
      List<Double> ideal = selection.translateObjectives(List.of(a, b, c));

      // Assert
      assertEquals(List.of(2.0, 3.0), ideal);
      assertEquals(List.of(0.0, 2.0), translated(selection, a));
      assertEquals(List.of(2.0, 0.0), translated(selection, b));
      assertEquals(List.of(3.0, -2.0), translated(selection, c));
    }

    @Test
    @DisplayName("given negative objectives, then the translated ones of the first front are not")
    void givenNegativeObjectives_whenTranslating_thenTheFirstFrontIsNotNegative() {
      // Arrange
      DoubleSolution a = solution(-8.0, -4.0);
      DoubleSolution b = solution(-6.0, -7.0);
      var selection = selection(List.of(List.of(a, b)), 2);

      // Act
      List<Double> ideal = selection.translateObjectives(List.of(a, b));

      // Assert
      assertEquals(List.of(-8.0, -7.0), ideal);
      assertEquals(List.of(0.0, 3.0), translated(selection, a));
      assertEquals(List.of(2.0, 0.0), translated(selection, b));
    }
  }

  @Nested
  @DisplayName("When solving the system of the hyperplane")
  class WhenSolvingTheSystemOfTheHyperplane {
    private final EnvironmentalSelection<DoubleSolution> selection =
        selection(List.of(List.of()), 2);

    private List<List<Double>> matrix(double[]... rows) {
      List<List<Double>> matrix = new ArrayList<>();
      for (double[] row : rows) {
        List<Double> list = new ArrayList<>();
        for (double value : row) {
          list.add(value);
        }
        matrix.add(list);
      }
      return matrix;
    }

    @Test
    @DisplayName("given a system of two equations, then the solution is found")
    void givenTwoEquations_whenSolving_thenTheSolutionIsFound() {
      // Act
      List<Double> x =
          selection.guassianElimination(
              matrix(new double[] {2, 1}, new double[] {1, 3}), List.of(3.0, 5.0));

      // Assert
      assertEquals(0.8, x.get(0), EPSILON);
      assertEquals(1.4, x.get(1), EPSILON);
    }

    @Test
    @DisplayName("given a system of three equations, then the solution is found")
    void givenThreeEquations_whenSolving_thenTheSolutionIsFound() {
      // Arrange: x = 1, y = 2, z = 3
      List<List<Double>> a =
          matrix(new double[] {1, 1, 1}, new double[] {0, 2, 5}, new double[] {2, 5, -1});

      // Act
      List<Double> x = selection.guassianElimination(a, List.of(6.0, 19.0, 9.0));

      // Assert
      assertEquals(1.0, x.get(0), EPSILON);
      assertEquals(2.0, x.get(1), EPSILON);
      assertEquals(3.0, x.get(2), EPSILON);
    }

    @Test
    @DisplayName("given a singular system, then the solution is not finite")
    void givenASingularSystem_whenSolving_thenTheSolutionIsNotFinite() {
      // Act
      List<Double> x =
          selection.guassianElimination(
              matrix(new double[] {1, 1}, new double[] {2, 2}), List.of(1.0, 2.0));

      // Assert
      assertFalse(Double.isFinite(x.get(0)) && Double.isFinite(x.get(1)));
    }
  }

  @Nested
  @DisplayName("When constructing the hyperplane")
  class WhenConstructingTheHyperplane {
    @ParameterizedTest(name = "front translated by {0}")
    @CsvSource({"0.0", "1.0", "100.0", "-50.0"})
    @DisplayName("given a translated front, then the intercepts are the ideal point plus one")
    void givenATranslatedFront_whenConstructing_thenTheInterceptsAreTheIdealPointPlusOne(
        double shift) {
      // Arrange: the hyperplane f1 + f2 = 1 has the intercept 1 with each translated axis
      List<DoubleSolution> front = line(shift);
      var selection = selection(List.of(front), 2);
      List<Double> ideal = selection.translateObjectives(front);

      // Act
      List<Double> intercepts =
          selection.constructHyperplane(front, List.of(front.get(2), front.get(0)));

      // Assert
      assertEquals(shift + 1.0, intercepts.get(0), EPSILON);
      assertEquals(shift + 1.0, intercepts.get(1), EPSILON);
      assertEquals(1.0, intercepts.get(0) - ideal.get(0), EPSILON);
    }

    @Test
    @DisplayName("given a hyperplane with other intercepts, then they are those of the plane")
    void givenOtherIntercepts_whenConstructing_thenTheyAreThoseOfThePlane() {
      // Arrange: x / 2 + y / 4 = 1, translated by (10, -3)
      DoubleSolution onX = solution(12.0, -3.0);
      DoubleSolution onY = solution(10.0, 1.0);
      DoubleSolution middle = solution(11.0, -1.0);
      List<DoubleSolution> front = new ArrayList<>(List.of(onX, onY, middle));
      var selection = selection(List.of(front), 2);
      selection.translateObjectives(front);

      // Act
      List<Double> intercepts = selection.constructHyperplane(front, List.of(onX, onY));

      // Assert
      assertEquals(12.0, intercepts.get(0), EPSILON);
      assertEquals(1.0, intercepts.get(1), EPSILON);
    }

    @Test
    @DisplayName("given three objectives, then the intercepts of the simplex are found")
    void givenThreeObjectives_whenConstructing_thenTheInterceptsOfTheSimplexAreFound() {
      // Arrange: the unit simplex translated by (5, 5, 5)
      DoubleSolution x = solution(6.0, 5.0, 5.0);
      DoubleSolution y = solution(5.0, 6.0, 5.0);
      DoubleSolution z = solution(5.0, 5.0, 6.0);
      List<DoubleSolution> front = new ArrayList<>(List.of(x, y, z));
      var selection = selection(List.of(front), 3);
      selection.translateObjectives(front);

      // Act
      List<Double> intercepts = selection.constructHyperplane(front, List.of(x, y, z));

      // Assert
      for (double intercept : intercepts) {
        assertEquals(6.0, intercept, EPSILON);
      }
    }

    @Test
    @DisplayName("given duplicated extreme points, then the largest coordinates are used")
    void givenDuplicatedExtremePoints_whenConstructing_thenTheLargestCoordinatesAreUsed() {
      // Arrange
      DoubleSolution a = solution(3.0, 7.0);
      DoubleSolution b = solution(5.0, 2.0);
      List<DoubleSolution> front = new ArrayList<>(List.of(a, b));
      var selection = selection(List.of(front), 2);
      selection.translateObjectives(front);

      // Act
      List<Double> intercepts = selection.constructHyperplane(front, List.of(a, a));

      // Assert: the coordinates of the extreme points in their own objectives
      assertEquals(3.0, intercepts.get(0), EPSILON);
      assertEquals(7.0, intercepts.get(1), EPSILON);
    }

    @Test
    @DisplayName("given objectives that have not been translated, then it is rejected")
    void givenUntranslatedObjectives_whenConstructing_thenItIsRejected() {
      // Arrange
      List<DoubleSolution> front = line(0.0);
      var selection = selection(List.of(front), 2);

      // Act & Assert
      assertThrows(
          JMetalException.class,
          () -> selection.constructHyperplane(front, List.of(front.get(2), front.get(0))));
    }
  }

  @Nested
  @DisplayName("When normalizing the objectives")
  class WhenNormalizingTheObjectives {
    @ParameterizedTest(name = "front translated by {0}")
    @CsvSource({"0.0", "1.0", "100.0", "-50.0"})
    @DisplayName("given a translated front, then its extreme points are the unit vectors")
    void givenATranslatedFront_whenNormalizing_thenItsExtremePointsAreTheUnitVectors(
        double shift) {
      // Arrange
      List<DoubleSolution> front = line(shift);
      var selection = selection(List.of(front), 2);
      List<Double> ideal = selection.translateObjectives(front);
      List<Double> intercepts =
          selection.constructHyperplane(front, List.of(front.get(2), front.get(0)));

      // Act
      selection.normalizeObjectives(front, intercepts, ideal);

      // Assert
      assertArrayEquals(
          new double[] {0.0, 1.0},
          translated(selection, front.get(0)).stream().mapToDouble(Double::doubleValue).toArray(),
          EPSILON);
      assertArrayEquals(
          new double[] {0.5, 0.5},
          translated(selection, front.get(1)).stream().mapToDouble(Double::doubleValue).toArray(),
          EPSILON);
      assertArrayEquals(
          new double[] {1.0, 0.0},
          translated(selection, front.get(2)).stream().mapToDouble(Double::doubleValue).toArray(),
          EPSILON);
    }

    @Test
    @DisplayName("given an intercept equal to the ideal point, then the values stay finite")
    void givenAnInterceptEqualToTheIdealPoint_whenNormalizing_thenTheValuesStayFinite() {
      // Arrange: the second objective is constant, so its translated values are zero
      DoubleSolution a = solution(0.0, 4.0);
      DoubleSolution b = solution(1.0, 4.0);
      List<DoubleSolution> front = new ArrayList<>(List.of(a, b));
      var selection = selection(List.of(front), 2);
      List<Double> ideal = selection.translateObjectives(front);

      // Act
      selection.normalizeObjectives(front, List.of(1.0, 4.0), ideal);

      // Assert
      for (DoubleSolution solution : front) {
        for (double value : translated(selection, solution)) {
          assertTrue(Double.isFinite(value));
        }
      }
    }
  }

  @Nested
  @DisplayName("When computing the perpendicular distance to a direction")
  class WhenComputingThePerpendicularDistance {
    private final EnvironmentalSelection<DoubleSolution> selection =
        selection(List.of(List.of()), 2);

    @Test
    @DisplayName("given a point on the direction, then the distance is zero")
    void givenAPointOnTheDirection_whenComputing_thenTheDistanceIsZero() {
      // Act & Assert
      assertEquals(
          0.0, selection.perpendicularDistance(List.of(1.0, 1.0), List.of(3.0, 3.0)), EPSILON);
    }

    @Test
    @DisplayName("given a point out of the direction, then the distance is the perpendicular one")
    void givenAPointOutOfTheDirection_whenComputing_thenTheDistanceIsThePerpendicularOne() {
      // Act & Assert
      assertEquals(
          4.0, selection.perpendicularDistance(List.of(1.0, 0.0), List.of(3.0, 4.0)), EPSILON);
      assertEquals(
          Math.sqrt(0.5),
          selection.perpendicularDistance(List.of(1.0, 1.0), List.of(1.0, 0.0)),
          EPSILON);
    }

    @Test
    @DisplayName("given a direction of another length, then the distance is the same")
    void givenADirectionOfAnotherLength_whenComputing_thenTheDistanceIsTheSame() {
      // Act & Assert
      assertEquals(
          selection.perpendicularDistance(List.of(1.0, 1.0), List.of(1.0, 0.0)),
          selection.perpendicularDistance(List.of(5.0, 5.0), List.of(1.0, 0.0)),
          EPSILON);
    }
  }

  @Nested
  @DisplayName("When selecting solutions from the critical front")
  class WhenSelectingSolutions {
    private List<Integer> selectedIndices(
        double scale1, double scale2, double shift1, double shift2) {
      double[][] points = {{0.0, 1.0}, {0.2, 0.8}, {0.5, 0.5}, {0.7, 0.3}, {1.0, 0.0}};
      List<DoubleSolution> front = new ArrayList<>();
      for (double[] point : points) {
        front.add(solution(point[0] * scale1 + shift1, point[1] * scale2 + shift2));
      }
      List<ReferencePoint<DoubleSolution>> referencePoints = new ArrayList<>();
      new ReferencePoint<DoubleSolution>().generateReferencePoints(referencePoints, 2, 2);
      var selection = new EnvironmentalSelection<>(List.of(front), 3, referencePoints, 2);

      JMetalRandom.getInstance().setSeed(23);
      List<DoubleSolution> selected = selection.execute(new ArrayList<>(front));

      return selected.stream().map(front::indexOf).sorted().toList();
    }

    @Test
    @DisplayName("given a front, then the closest point to each reference point is selected")
    void givenAFront_whenSelecting_thenTheClosestPointToEachReferencePointIsSelected() {
      // Act & Assert
      assertEquals(List.of(0, 2, 4), selectedIndices(1.0, 1.0, 0.0, 0.0));
    }

    @ParameterizedTest(name = "shifts ({0}, {1})")
    @CsvSource({"1.0, 1.0", "100.0, 100.0", "-50.0, 7.0", "0.0, -1000.0"})
    @DisplayName("given a translated front, then the same solutions are selected")
    void givenATranslatedFront_whenSelecting_thenTheSameSolutionsAreSelected(
        double shift1, double shift2) {
      // Act & Assert
      assertEquals(List.of(0, 2, 4), selectedIndices(1.0, 1.0, shift1, shift2));
    }

    @ParameterizedTest(name = "scales ({0}, {1})")
    @CsvSource({"1000.0, 1.0", "1.0, 0.001", "0.5, 0.5", "10.0, 0.1"})
    @DisplayName("given a scaled front, then the same solutions are selected")
    void givenAScaledFront_whenSelecting_thenTheSameSolutionsAreSelected(
        double scale1, double scale2) {
      // Act & Assert
      assertEquals(List.of(0, 2, 4), selectedIndices(scale1, scale2, 0.0, 0.0));
    }

    @Test
    @DisplayName("given a scaled and translated front, then the same solutions are selected")
    void givenAScaledAndTranslatedFront_whenSelecting_thenTheSameSolutionsAreSelected() {
      // Act & Assert
      assertEquals(List.of(0, 2, 4), selectedIndices(250.0, 0.004, -9000.0, -17.0));
    }
  }
}
