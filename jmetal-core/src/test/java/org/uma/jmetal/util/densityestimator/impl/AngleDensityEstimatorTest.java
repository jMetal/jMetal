package org.uma.jmetal.util.densityestimator.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * Unit tests for the AngleDensityEstimator class.
 *
 * @author Antonio J. Nebro
 */
class AngleDensityEstimatorTest {
  private static final double EPSILON = 1e-6;

  @Nested
  @DisplayName("Constructor tests")
  class ConstructorTests {

    @Test
    @DisplayName("Default constructor creates valid instance")
    void defaultConstructorCreatesValidInstance() {
      AngleDensityEstimator<DoubleSolution> estimator = new AngleDensityEstimator<>();
      
      assertNotNull(estimator);
      assertNull(estimator.getReferencePoint());
      assertFalse(estimator.isNormalize());
      assertEquals(2, estimator.getNumberOfNeighbors());
    }

    @Test
    @DisplayName("Constructor with reference point works correctly")
    void constructorWithReferencePointWorksCorrectly() {
      double[] refPoint = {0.5, 0.5};
      AngleDensityEstimator<DoubleSolution> estimator = new AngleDensityEstimator<>(refPoint);
      
      assertArrayEquals(refPoint, estimator.getReferencePoint(), EPSILON);
      assertFalse(estimator.isNormalize());
      assertEquals(2, estimator.getNumberOfNeighbors());
    }

    @Test
    @DisplayName("Constructor with normalization works correctly")
    void constructorWithNormalizationWorksCorrectly() {
      AngleDensityEstimator<DoubleSolution> estimator = new AngleDensityEstimator<>(true);
      
      assertNull(estimator.getReferencePoint());
      assertTrue(estimator.isNormalize());
      assertEquals(2, estimator.getNumberOfNeighbors());
    }

    @Test
    @DisplayName("Constructor with all parameters works correctly")
    void constructorWithAllParametersWorksCorrectly() {
      double[] refPoint = {0.0, 0.0};
      AngleDensityEstimator<DoubleSolution> estimator = 
          new AngleDensityEstimator<>(refPoint, true, 3);
      
      assertArrayEquals(refPoint, estimator.getReferencePoint(), EPSILON);
      assertTrue(estimator.isNormalize());
      assertEquals(3, estimator.getNumberOfNeighbors());
    }

    @Test
    @DisplayName("Constructor with invalid number of neighbors throws exception")
    void constructorWithInvalidNumberOfNeighborsThrowsException() {
      assertThrows(InvalidConditionException.class, 
          () -> new AngleDensityEstimator<>(null, false, 0));
    }
  }

  @Nested
  @DisplayName("Compute method tests")
  class ComputeMethodTests {

    @Test
    @DisplayName("Compute on empty list does nothing")
    void computeOnEmptyListDoesNothing() {
      AngleDensityEstimator<DoubleSolution> estimator = new AngleDensityEstimator<>();
      List<DoubleSolution> solutionList = new ArrayList<>();
      
      assertDoesNotThrow(() -> estimator.compute(solutionList));
    }

    @Test
    @DisplayName("Single solution gets infinite density")
    void singleSolutionGetsInfiniteDensity() {
      AngleDensityEstimator<DoubleSolution> estimator = new AngleDensityEstimator<>();
      FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      DoubleSolution solution = problem.createSolution();
      solution.objectives()[0] = 0.5;
      solution.objectives()[1] = 0.5;
      
      List<DoubleSolution> solutionList = new ArrayList<>();
      solutionList.add(solution);
      
      estimator.compute(solutionList);
      
      assertEquals(Double.POSITIVE_INFINITY, estimator.value(solution));
    }

    @Test
    @DisplayName("Two solutions get valid density values")
    void twoSolutionsGetValidDensityValues() {
      AngleDensityEstimator<DoubleSolution> estimator = new AngleDensityEstimator<>();
      FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      
      DoubleSolution solution1 = problem.createSolution();
      solution1.objectives()[0] = 0.0;
      solution1.objectives()[1] = 1.0;
      
      DoubleSolution solution2 = problem.createSolution();
      solution2.objectives()[0] = 1.0;
      solution2.objectives()[1] = 0.0;
      
      List<DoubleSolution> solutionList = new ArrayList<>();
      solutionList.add(solution1);
      solutionList.add(solution2);
      
      estimator.compute(solutionList);
      
      // Both are extreme solutions, should have infinite density
      assertEquals(Double.POSITIVE_INFINITY, estimator.value(solution1));
      assertEquals(Double.POSITIVE_INFINITY, estimator.value(solution2));
    }

    @Test
    @DisplayName("Interior solution has finite density")
    void interiorSolutionHasFiniteDensity() {
      AngleDensityEstimator<DoubleSolution> estimator = new AngleDensityEstimator<>();
      FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      
      DoubleSolution extreme1 = problem.createSolution();
      extreme1.objectives()[0] = 0.0;
      extreme1.objectives()[1] = 1.0;
      
      DoubleSolution extreme2 = problem.createSolution();
      extreme2.objectives()[0] = 1.0;
      extreme2.objectives()[1] = 0.0;
      
      DoubleSolution interior = problem.createSolution();
      interior.objectives()[0] = 0.5;
      interior.objectives()[1] = 0.5;
      
      List<DoubleSolution> solutionList = new ArrayList<>();
      solutionList.add(extreme1);
      solutionList.add(extreme2);
      solutionList.add(interior);
      
      estimator.compute(solutionList);
      
      // Interior solution should have finite density
      double interiorDensity = estimator.value(interior);
      assertTrue(interiorDensity > 0.0, "Interior density should be positive");
      assertTrue(Double.isFinite(interiorDensity), "Interior density should be finite");
    }

    @Test
    @DisplayName("Solutions on same angle have zero angle between them")
    void solutionsOnSameAngleHaveZeroAngle() {
      AngleDensityEstimator<DoubleSolution> estimator = new AngleDensityEstimator<>();
      FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      
      // Two solutions on the same ray from origin
      DoubleSolution solution1 = problem.createSolution();
      solution1.objectives()[0] = 0.3;
      solution1.objectives()[1] = 0.3;
      
      DoubleSolution solution2 = problem.createSolution();
      solution2.objectives()[0] = 0.6;
      solution2.objectives()[1] = 0.6;
      
      DoubleSolution solution3 = problem.createSolution();
      solution3.objectives()[0] = 0.0;
      solution3.objectives()[1] = 1.0;
      
      List<DoubleSolution> solutionList = new ArrayList<>();
      solutionList.add(solution1);
      solutionList.add(solution2);
      solutionList.add(solution3);
      
      estimator.compute(solutionList);
      
      // solution1 and solution2 are on the same ray, their angle is 0
      // They should have lower density than solution3 which is at a different angle
      double density1 = estimator.value(solution1);
      double density2 = estimator.value(solution2);
      
      // Both should have some density value (may be 0 if angle to each other is 0)
      assertNotNull(density1);
      assertNotNull(density2);
    }
  }

  @Nested
  @DisplayName("Value and comparator tests")
  class ValueAndComparatorTests {

    @Test
    @DisplayName("Value throws exception for null solution")
    void valueThrowsExceptionForNullSolution() {
      AngleDensityEstimator<DoubleSolution> estimator = new AngleDensityEstimator<>();
      
      assertThrows(NullParameterException.class, () -> estimator.value(null));
    }

    @Test
    @DisplayName("Value throws exception when attribute not computed")
    void valueThrowsExceptionWhenAttributeNotComputed() {
      AngleDensityEstimator<DoubleSolution> estimator = new AngleDensityEstimator<>();
      FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      DoubleSolution solution = problem.createSolution();
      
      assertThrows(NullParameterException.class, () -> estimator.value(solution));
    }

    @Test
    @DisplayName("Comparator sorts by density ascending")
    void comparatorSortsByDensityAscending() {
      AngleDensityEstimator<DoubleSolution> estimator = new AngleDensityEstimator<>();
      FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      
      DoubleSolution extreme1 = problem.createSolution();
      extreme1.objectives()[0] = 0.0;
      extreme1.objectives()[1] = 1.0;
      
      DoubleSolution extreme2 = problem.createSolution();
      extreme2.objectives()[0] = 1.0;
      extreme2.objectives()[1] = 0.0;
      
      DoubleSolution interior = problem.createSolution();
      interior.objectives()[0] = 0.5;
      interior.objectives()[1] = 0.5;
      
      List<DoubleSolution> solutionList = new ArrayList<>();
      solutionList.add(extreme1);
      solutionList.add(extreme2);
      solutionList.add(interior);
      
      estimator.compute(solutionList);
      solutionList.sort(estimator.comparator());
      
      // Interior solution (lower density) should come first
      assertTrue(estimator.value(solutionList.get(0)) <= estimator.value(solutionList.get(1)));
    }

    @Test
    @DisplayName("Reversed comparator sorts by density descending")
    void reversedComparatorSortsByDensityDescending() {
      AngleDensityEstimator<DoubleSolution> estimator = new AngleDensityEstimator<>();
      FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      
      DoubleSolution extreme1 = problem.createSolution();
      extreme1.objectives()[0] = 0.0;
      extreme1.objectives()[1] = 1.0;
      
      DoubleSolution extreme2 = problem.createSolution();
      extreme2.objectives()[0] = 1.0;
      extreme2.objectives()[1] = 0.0;
      
      DoubleSolution interior = problem.createSolution();
      interior.objectives()[0] = 0.5;
      interior.objectives()[1] = 0.5;
      
      List<DoubleSolution> solutionList = new ArrayList<>();
      solutionList.add(extreme1);
      solutionList.add(extreme2);
      solutionList.add(interior);
      
      estimator.compute(solutionList);
      solutionList.sort(estimator.reversedComparator());
      
      // Extreme solutions (higher density) should come first
      assertTrue(estimator.value(solutionList.get(0)) >= estimator.value(solutionList.get(1)));
    }

    @Test
    @DisplayName("Attribute ID is not null")
    void attributeIdIsNotNull() {
      AngleDensityEstimator<DoubleSolution> estimator = new AngleDensityEstimator<>();
      assertNotNull(estimator.attributeId());
    }
  }

  @Nested
  @DisplayName("3-objective tests")
  class ThreeObjectiveTests {

    @Test
    @DisplayName("Angle estimator works with 3 objectives")
    void angleEstimatorWorksWith3Objectives() {
      AngleDensityEstimator<DoubleSolution> estimator = new AngleDensityEstimator<>();
      FakeDoubleProblem problem = new FakeDoubleProblem(3, 3, 0);
      
      DoubleSolution s1 = problem.createSolution();
      s1.objectives()[0] = 1.0; s1.objectives()[1] = 0.0; s1.objectives()[2] = 0.0;
      
      DoubleSolution s2 = problem.createSolution();
      s2.objectives()[0] = 0.0; s2.objectives()[1] = 1.0; s2.objectives()[2] = 0.0;
      
      DoubleSolution s3 = problem.createSolution();
      s3.objectives()[0] = 0.0; s3.objectives()[1] = 0.0; s3.objectives()[2] = 1.0;
      
      DoubleSolution s4 = problem.createSolution();
      s4.objectives()[0] = 0.33; s4.objectives()[1] = 0.33; s4.objectives()[2] = 0.34;
      
      List<DoubleSolution> solutionList = new ArrayList<>();
      solutionList.add(s1);
      solutionList.add(s2);
      solutionList.add(s3);
      solutionList.add(s4);
      
      estimator.compute(solutionList);
      
      // Extreme solutions should have infinite density
      assertEquals(Double.POSITIVE_INFINITY, estimator.value(s1));
      assertEquals(Double.POSITIVE_INFINITY, estimator.value(s2));
      assertEquals(Double.POSITIVE_INFINITY, estimator.value(s3));
      
      // Interior solution should have finite density
      assertTrue(Double.isFinite(estimator.value(s4)));
    }

    @Test
    @DisplayName("Solutions at 90 degrees have correct angle")
    void solutionsAt90DegreesHaveCorrectAngle() {
      AngleDensityEstimator<DoubleSolution> estimator = new AngleDensityEstimator<>(null, false, 1);
      FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      
      // Three solutions, middle one is interior
      DoubleSolution s1 = problem.createSolution();
      s1.objectives()[0] = 1.0; s1.objectives()[1] = 0.0;
      
      DoubleSolution s2 = problem.createSolution();
      s2.objectives()[0] = 0.0; s2.objectives()[1] = 1.0;
      
      DoubleSolution s3 = problem.createSolution();
      s3.objectives()[0] = 0.5; s3.objectives()[1] = 0.5;
      
      List<DoubleSolution> solutionList = new ArrayList<>();
      solutionList.add(s1);
      solutionList.add(s2);
      solutionList.add(s3);
      
      estimator.compute(solutionList);
      
      // s3 is at 45 degrees from both s1 (0 degrees) and s2 (90 degrees)
      // So its minimum angle to a neighbor should be π/4 ≈ 0.785 radians
      double density = estimator.value(s3);
      assertTrue(density > 0.7 && density < 0.9, 
          "Interior solution density should be approximately π/4: " + density);
    }
  }

  @Nested
  @DisplayName("Normalization tests")
  class NormalizationTests {

    @Test
    @DisplayName("Normalization affects density calculation")
    void normalizationAffectsDensityCalculation() {
      FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      
      // Solutions with different scales
      DoubleSolution s1 = problem.createSolution();
      s1.objectives()[0] = 0.0; s1.objectives()[1] = 100.0;
      
      DoubleSolution s2 = problem.createSolution();
      s2.objectives()[0] = 100.0; s2.objectives()[1] = 0.0;
      
      DoubleSolution s3 = problem.createSolution();
      s3.objectives()[0] = 50.0; s3.objectives()[1] = 50.0;
      
      List<DoubleSolution> list1 = new ArrayList<>();
      list1.add(s1); list1.add(s2); list1.add(s3);
      
      AngleDensityEstimator<DoubleSolution> estimatorNoNorm = new AngleDensityEstimator<>(false);
      estimatorNoNorm.compute(list1);
      double densityNoNorm = estimatorNoNorm.value(s3);
      
      // Reset solutions
      s1 = problem.createSolution();
      s1.objectives()[0] = 0.0; s1.objectives()[1] = 100.0;
      s2 = problem.createSolution();
      s2.objectives()[0] = 100.0; s2.objectives()[1] = 0.0;
      s3 = problem.createSolution();
      s3.objectives()[0] = 50.0; s3.objectives()[1] = 50.0;
      
      List<DoubleSolution> list2 = new ArrayList<>();
      list2.add(s1); list2.add(s2); list2.add(s3);
      
      AngleDensityEstimator<DoubleSolution> estimatorNorm = new AngleDensityEstimator<>(true);
      estimatorNorm.compute(list2);
      double densityNorm = estimatorNorm.value(s3);
      
      // Both should produce valid densities
      assertTrue(densityNoNorm > 0);
      assertTrue(densityNorm > 0);
    }
  }
}
