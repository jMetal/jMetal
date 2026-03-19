package org.uma.jmetal.util.referencepoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ReferencePointGeneratorTest {

  @Test
  void generatedTwoLayerReferencePointsShouldRemainOnTheUnitSimplex() {
    List<double[]> referencePoints = ReferencePointGenerator.generateTwoLayers(3, 9, 8);

    for (double[] point : referencePoints) {
      double sum = 0.0;
      for (double coordinate : point) {
        sum += coordinate;
      }

      assertEquals(1.0, sum, 1.0e-10, "Reference point should sum to 1.0");
    }
  }

  @Test
  void generatedTwoLayerReferencePointsShouldShiftTheInnerLayerTowardTheSimplexCenter() {
    List<double[]> referencePoints = ReferencePointGenerator.generateTwoLayers(3, 9, 8);

    assertTrue(
        containsPoint(referencePoints, new double[] {2.0 / 3.0, 1.0 / 6.0, 1.0 / 6.0}, 1.0e-12),
        "The inner layer should contain the simplex-centered shift of a boundary direction.");
    assertTrue(
        containsPoint(referencePoints, new double[] {1.0 / 6.0, 1.0 / 6.0, 2.0 / 3.0}, 1.0e-12),
        "The inner layer should preserve symmetric simplex-centered directions.");
  }

  private boolean containsPoint(List<double[]> points, double[] expectedPoint, double tolerance) {
    for (double[] point : points) {
      if (point.length != expectedPoint.length) {
        continue;
      }

      boolean matches = true;
      for (int i = 0; i < point.length; i++) {
        if (Math.abs(point[i] - expectedPoint[i]) > tolerance) {
          matches = false;
          break;
        }
      }

      if (matches) {
        return true;
      }
    }

    return false;
  }
}
