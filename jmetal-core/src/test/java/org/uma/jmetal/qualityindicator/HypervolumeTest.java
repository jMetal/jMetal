
package org.uma.jmetal.qualityindicator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */

class HypervolumeTest {

  private static final double EPSILON = 1e-13;

  @Nested
  @DisplayName("PISAHypervolume Tests")
  class GivenPISAHypervolume {
  @Test
  @DisplayName("Throws NullPointerException when reference front is null")
  void whenReferenceFrontIsNull_thenThrowsNullPointerException() {
      double[][] referenceFront = null;
      Assertions.assertThrows(NullPointerException.class, () -> new PISAHypervolume(referenceFront));
    }

  @Test
  @DisplayName("Throws IllegalArgumentException when front is null")
  void whenFrontIsNull_thenThrowsIllegalArgumentException() {
      double[][] referenceFront = new double[0][0];
      double[][] front = null;
      Assertions.assertThrows(IllegalArgumentException.class, () -> new PISAHypervolume(referenceFront).compute(front));
    }

  @Test
  @DisplayName("Returns correct hypervolume for single point (2 objectives)")
  void whenFrontIsSinglePoint2Objectives_thenReturnsCorrectHypervolume() {
      double[][] referenceFront = new double[][]{{1.0, 1.0}};
      double[][] front = new double[][]{{0.5, 0.5}};
      PISAHypervolume hv = new PISAHypervolume(referenceFront);
      double result = hv.compute(front);
      Assertions.assertTrue(result > 0.0);
    }

  @Test
  @DisplayName("Returns correct hypervolume for single point (3 objectives)")
  void whenFrontIsSinglePoint3Objectives_thenReturnsCorrectHypervolume() {
      double[][] referenceFront = new double[][]{{1.0, 1.0, 1.0}};
      double[][] front = new double[][]{{0.5, 0.5, 0.5}};
      PISAHypervolume hv = new PISAHypervolume(referenceFront);
      double result = hv.compute(front);
      Assertions.assertTrue(result > 0.0);
    }

  @Test
  @DisplayName("Returns correct hypervolume for single point (5 objectives)")
  void whenFrontIsSinglePoint5Objectives_thenReturnsCorrectHypervolume() {
      double[][] referenceFront = new double[][]{{1.0, 1.0, 1.0, 1.0, 1.0}};
      double[][] front = new double[][]{{0.5, 0.5, 0.5, 0.5, 0.5}};
      PISAHypervolume hv = new PISAHypervolume(referenceFront);
      double result = hv.compute(front);
      Assertions.assertTrue(result > 0.0);
    }

  @Test
  @DisplayName("Returns zero when front is empty")
  void whenFrontIsEmpty_thenReturnsZero() {
      double[][] referenceFront = new double[][]{{1.0, 1.0}};
      double[][] front = new double[0][2];
      PISAHypervolume hv = new PISAHypervolume(referenceFront);
      double result = hv.compute(front);
      Assertions.assertEquals(0.0, result, EPSILON);
    }

  @Test
  @DisplayName("Returns correct hypervolume for dominated points")
  void whenFrontHasDominatedPoints_thenReturnsCorrectHypervolume() {
      double[][] referenceFront = new double[][]{{1.0, 1.0}};
      double[][] front = new double[][]{{0.5, 0.5}, {0.6, 0.6}};
      PISAHypervolume hv = new PISAHypervolume(referenceFront);
      double result = hv.compute(front);
      Assertions.assertTrue(result > 0.0);
    }

  @Test
  @DisplayName("Returns correct hypervolume for extreme points")
  void whenFrontHasExtremePoints_thenReturnsCorrectHypervolume() {
      double[][] referenceFront = new double[][]{{1.0, 1.0}};
      double[][] front = new double[][]{{1.0, 1.0}, {0.0, 0.0}};
      PISAHypervolume hv = new PISAHypervolume(referenceFront);
      double result = hv.compute(front);
      Assertions.assertTrue(result >= 0.0);
    }

  @Test
  @DisplayName("Returns zero for degenerate front")
  void whenFrontIsDegenerate_thenReturnsZero() {
      double[][] referenceFront = new double[][]{{1.0, 1.0}};
      double[][] front = new double[][]{{1.0, 1.0}};
      PISAHypervolume hv = new PISAHypervolume(referenceFront);
      double result = hv.compute(front);
      Assertions.assertEquals(0.0, result, EPSILON);
    }
  }
}
