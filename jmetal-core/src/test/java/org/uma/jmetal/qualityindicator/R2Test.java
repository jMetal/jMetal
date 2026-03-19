package org.uma.jmetal.qualityindicator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.R2;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * Unit tests for the R2 quality indicator.
 *
 * @author Antonio J. Nebro
 */
class R2Test {
  private static final double EPSILON = 1e-10;

  @Nested
  @DisplayName("Constructor tests")
  class ConstructorTests {

    @Test
    @DisplayName("Default constructor creates valid instance")
    void defaultConstructorCreatesValidInstance() {
      R2 r2 = new R2();
      assertNotNull(r2);
      assertEquals("R2", r2.name());
      assertEquals("R2 Quality Indicator", r2.description());
    }

    @Test
    @DisplayName("Constructor with null reference front throws exception")
    void constructorWithNullReferenceFrontThrowsException() {
      assertThrows(NullParameterException.class, () -> new R2(null));
    }

    @Test
    @DisplayName("Constructor with empty reference front throws exception")
    void constructorWithEmptyReferenceFrontThrowsException() {
      double[][] emptyFront = new double[0][0];
      assertThrows(InvalidConditionException.class, () -> new R2(emptyFront));
    }

    @Test
    @DisplayName("Constructor with reference front creates valid instance")
    void constructorWithReferenceFrontCreatesValidInstance() {
      double[][] referenceFront = {{0.0, 1.0}, {0.5, 0.5}, {1.0, 0.0}};
      R2 r2 = new R2(referenceFront);
      
      assertNotNull(r2);
      assertNotNull(r2.getWeightVectors());
      assertNotNull(r2.getIdealPoint());
    }

    @Test
    @DisplayName("Constructor with custom number of vectors works correctly")
    void constructorWithCustomNumberOfVectorsWorksCorrectly() {
      double[][] referenceFront = {{0.0, 1.0}, {0.5, 0.5}, {1.0, 0.0}};
      R2 r2 = new R2(referenceFront, 50);
      
      assertNotNull(r2);
      assertNotNull(r2.getWeightVectors());
      assertTrue(r2.getWeightVectors().length >= 50);
    }

    @Test
    @DisplayName("Constructor with invalid number of vectors throws exception")
    void constructorWithInvalidNumberOfVectorsThrowsException() {
      double[][] referenceFront = {{0.0, 1.0}, {0.5, 0.5}, {1.0, 0.0}};
      assertThrows(InvalidConditionException.class, () -> new R2(referenceFront, 0));
    }

    @Test
    @DisplayName("Constructor with custom weight vectors and utility function works correctly")
    void constructorWithCustomWeightVectorsAndUtilityFunctionWorksCorrectly() {
      double[][] referenceFront = {{0.0, 1.0}, {1.0, 0.0}};
      double[][] weightVectors = {{1.0, 0.0}, {0.5, 0.5}, {0.0, 1.0}};
      
      R2 r2 = new R2(referenceFront, weightVectors, R2.UtilityFunction.WEIGHTED_SUM);
      
      assertNotNull(r2);
      assertEquals(3, r2.getWeightVectors().length);
      assertEquals(R2.UtilityFunction.WEIGHTED_SUM, r2.getUtilityFunction());
    }
  }

  @Nested
  @DisplayName("Compute tests")
  class ComputeTests {

    @Test
    @DisplayName("Compute with null front throws exception")
    void computeWithNullFrontThrowsException() {
      double[][] referenceFront = {{0.0, 1.0}, {1.0, 0.0}};
      R2 r2 = new R2(referenceFront);
      
      assertThrows(NullParameterException.class, () -> r2.compute(null));
    }

    @Test
    @DisplayName("Compute with empty front throws exception")
    void computeWithEmptyFrontThrowsException() {
      double[][] referenceFront = {{0.0, 1.0}, {1.0, 0.0}};
      R2 r2 = new R2(referenceFront);
      
      double[][] emptyFront = new double[0][0];
      assertThrows(InvalidConditionException.class, () -> r2.compute(emptyFront));
    }

    @Test
    @DisplayName("Compute returns zero when front equals reference front at origin")
    void computeReturnsZeroWhenFrontAtOrigin() {
      double[][] referenceFront = {{0.0, 0.0}};
      double[][] weightVectors = {{0.5, 0.5}};
      
      R2 r2 = new R2(referenceFront, weightVectors, R2.UtilityFunction.TCHEBYCHEFF);
      double[][] front = {{0.0, 0.0}};
      
      assertEquals(0.0, r2.compute(front), EPSILON);
    }

    @Test
    @DisplayName("Compute returns positive value for dominated front")
    void computeReturnsPositiveValueForDominatedFront() {
      double[][] referenceFront = {{0.0, 0.0}};
      double[][] weightVectors = {{0.5, 0.5}};
      
      R2 r2 = new R2(referenceFront, weightVectors, R2.UtilityFunction.TCHEBYCHEFF);
      double[][] front = {{1.0, 1.0}};
      
      assertTrue(r2.compute(front) > 0.0);
    }

    @Test
    @DisplayName("Compute with Tchebycheff returns correct value")
    void computeWithTchebycheffReturnsCorrectValue() {
      // Single weight vector (1, 0) pointing along first objective
      double[][] weightVectors = {{1.0, 0.0}};
      double[][] referenceFront = {{0.0, 0.0}};
      
      R2 r2 = new R2(referenceFront, weightVectors, R2.UtilityFunction.TCHEBYCHEFF);
      
      // Front with point (0.5, 0.3)
      // Tchebycheff utility = max(1.0 * |0.5 - 0|, 0.0 * |0.3 - 0|) = 0.5
      double[][] front = {{0.5, 0.3}};
      
      assertEquals(0.5, r2.compute(front), EPSILON);
    }

    @Test
    @DisplayName("Compute with weighted sum returns correct value")
    void computeWithWeightedSumReturnsCorrectValue() {
      double[][] weightVectors = {{0.5, 0.5}};
      double[][] referenceFront = {{0.0, 0.0}};
      
      R2 r2 = new R2(referenceFront, weightVectors, R2.UtilityFunction.WEIGHTED_SUM);
      
      // Front with point (0.4, 0.6)
      // Weighted sum = 0.5 * 0.4 + 0.5 * 0.6 = 0.5
      double[][] front = {{0.4, 0.6}};
      
      assertEquals(0.5, r2.compute(front), EPSILON);
    }

    @Test
    @DisplayName("Compute selects minimum utility across solutions")
    void computeSelectsMinimumUtilityAcrossSolutions() {
      double[][] weightVectors = {{1.0, 0.0}};
      double[][] referenceFront = {{0.0, 0.0}};
      
      R2 r2 = new R2(referenceFront, weightVectors, R2.UtilityFunction.TCHEBYCHEFF);
      
      // Front with two points: (0.8, 0.2) and (0.3, 0.9)
      // Tchebycheff for (0.8, 0.2) with weight (1, 0) = max(1*0.8, 0*0.2) = 0.8
      // Tchebycheff for (0.3, 0.9) with weight (1, 0) = max(1*0.3, 0*0.9) = 0.3
      // Minimum = 0.3
      double[][] front = {{0.8, 0.2}, {0.3, 0.9}};
      
      assertEquals(0.3, r2.compute(front), EPSILON);
    }

    @Test
    @DisplayName("Compute averages over all weight vectors")
    void computeAveragesOverAllWeightVectors() {
      double[][] weightVectors = {{1.0, 0.0}, {0.0, 1.0}};
      double[][] referenceFront = {{0.0, 0.0}};
      
      R2 r2 = new R2(referenceFront, weightVectors, R2.UtilityFunction.TCHEBYCHEFF);
      
      // Front with single point (0.3, 0.7)
      // Tchebycheff with (1, 0) = 0.3
      // Tchebycheff with (0, 1) = 0.7
      // Average = (0.3 + 0.7) / 2 = 0.5
      double[][] front = {{0.3, 0.7}};
      
      assertEquals(0.5, r2.compute(front), EPSILON);
    }
  }

  @Nested
  @DisplayName("Weight vector generation tests")
  class WeightVectorGenerationTests {

    @Test
    @DisplayName("Weight vectors sum to one for 2 objectives")
    void weightVectorsSumToOneFor2Objectives() {
      double[][] referenceFront = {{0.0, 1.0}, {1.0, 0.0}};
      R2 r2 = new R2(referenceFront);
      
      double[][] vectors = r2.getWeightVectors();
      
      for (double[] vector : vectors) {
        assertEquals(2, vector.length);
        double sum = vector[0] + vector[1];
        assertEquals(1.0, sum, EPSILON);
      }
    }

    @Test
    @DisplayName("Weight vectors sum to one for 3 objectives")
    void weightVectorsSumToOneFor3Objectives() {
      double[][] referenceFront = {{0.0, 0.5, 0.5}, {0.5, 0.0, 0.5}, {0.5, 0.5, 0.0}};
      R2 r2 = new R2(referenceFront);
      
      double[][] vectors = r2.getWeightVectors();
      
      for (double[] vector : vectors) {
        assertEquals(3, vector.length);
        double sum = vector[0] + vector[1] + vector[2];
        assertEquals(1.0, sum, EPSILON);
      }
    }

    @Test
    @DisplayName("Weight vector components are non-negative")
    void weightVectorComponentsAreNonNegative() {
      double[][] referenceFront = {{0.0, 1.0}, {1.0, 0.0}};
      R2 r2 = new R2(referenceFront);
      
      double[][] vectors = r2.getWeightVectors();
      
      for (double[] vector : vectors) {
        for (double component : vector) {
          assertTrue(component >= 0.0, "Component should be non-negative: " + component);
        }
      }
    }
  }

  @Nested
  @DisplayName("Ideal point tests")
  class IdealPointTests {

    @Test
    @DisplayName("Ideal point is computed correctly")
    void idealPointIsComputedCorrectly() {
      double[][] referenceFront = {{0.1, 0.9}, {0.5, 0.5}, {0.9, 0.1}};
      R2 r2 = new R2(referenceFront);
      
      double[] idealPoint = r2.getIdealPoint();
      
      assertEquals(2, idealPoint.length);
      assertEquals(0.1, idealPoint[0], EPSILON);
      assertEquals(0.1, idealPoint[1], EPSILON);
    }

    @Test
    @DisplayName("Custom ideal point can be set")
    void customIdealPointCanBeSet() {
      double[][] referenceFront = {{0.0, 1.0}, {1.0, 0.0}};
      R2 r2 = new R2(referenceFront);
      
      double[] customIdeal = {-0.1, -0.1};
      r2.setIdealPoint(customIdeal);
      
      assertArrayEquals(customIdeal, r2.getIdealPoint(), EPSILON);
    }
  }

  @Nested
  @DisplayName("Property tests")
  class PropertyTests {

    @Test
    @DisplayName("isTheLowerTheIndicatorValueTheBetter returns true")
    void isTheLowerTheIndicatorValueTheBetterReturnsTrue() {
      R2 r2 = new R2();
      assertTrue(r2.isTheLowerTheIndicatorValueTheBetter());
    }

    @Test
    @DisplayName("name returns R2")
    void nameReturnsR2() {
      R2 r2 = new R2();
      assertEquals("R2", r2.name());
    }

    @Test
    @DisplayName("description returns correct value")
    void descriptionReturnsCorrectValue() {
      R2 r2 = new R2();
      assertEquals("R2 Quality Indicator", r2.description());
    }

    @Test
    @DisplayName("newInstance returns new R2 instance")
    void newInstanceReturnsNewR2Instance() {
      R2 r2 = new R2();
      R2 newInstance = (R2) r2.newInstance();
      
      assertNotNull(newInstance);
      assertNotSame(r2, newInstance);
    }

    @Test
    @DisplayName("Utility function can be changed")
    void utilityFunctionCanBeChanged() {
      R2 r2 = new R2();
      r2.setUtilityFunction(R2.UtilityFunction.WEIGHTED_SUM);
      
      assertEquals(R2.UtilityFunction.WEIGHTED_SUM, r2.getUtilityFunction());
    }
  }

  @Nested
  @DisplayName("3-objective tests")
  class ThreeObjectiveTests {

    @Test
    @DisplayName("R2 works correctly with 3 objectives")
    void r2WorksCorrectlyWith3Objectives() {
      double[][] referenceFront = {
          {0.0, 0.5, 0.5},
          {0.5, 0.0, 0.5},
          {0.5, 0.5, 0.0}
      };
      
      R2 r2 = new R2(referenceFront);
      
      double[][] front = {
          {0.1, 0.6, 0.6},
          {0.6, 0.1, 0.6},
          {0.6, 0.6, 0.1}
      };
      
      double value = r2.compute(front);
      
      assertTrue(value > 0.0, "R2 value should be positive for dominated front");
    }

    @Test
    @DisplayName("Better front has lower R2 value")
    void betterFrontHasLowerR2Value() {
      double[][] referenceFront = {
          {0.0, 0.5, 0.5},
          {0.5, 0.0, 0.5},
          {0.5, 0.5, 0.0}
      };
      
      double[][] weightVectors = {
          {1.0, 0.0, 0.0},
          {0.0, 1.0, 0.0},
          {0.0, 0.0, 1.0},
          {0.33, 0.33, 0.34}
      };
      
      R2 r2 = new R2(referenceFront, weightVectors, R2.UtilityFunction.TCHEBYCHEFF);
      
      double[][] goodFront = {
          {0.1, 0.55, 0.55},
          {0.55, 0.1, 0.55},
          {0.55, 0.55, 0.1}
      };
      
      double[][] badFront = {
          {0.3, 0.7, 0.7},
          {0.7, 0.3, 0.7},
          {0.7, 0.7, 0.3}
      };
      
      double goodValue = r2.compute(goodFront);
      double badValue = r2.compute(badFront);
      
      assertTrue(goodValue < badValue, 
          "Better front should have lower R2 value. Good: " + goodValue + ", Bad: " + badValue);
    }
  }
}
