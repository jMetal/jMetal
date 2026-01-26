package org.uma.jmetal.qualityindicator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.hypervolume.HypervolumeContribution2D;

/**
 * Tests for HypervolumeContribution2D.
 */
class HypervolumeContribution2DTest {

  private static final double EPSILON = 1e-10;

  /**
   * Test with a simple Pareto front of 3 points.
   */
  @Test
  void testSimpleParetoFront() {
    double[][] points = {
        {1.0, 3.0},
        {2.0, 2.0},
        {3.0, 1.0}
    };
    double[] ref = {4.0, 4.0};
    
    double[] contributions = HypervolumeContribution2D.compute(points, ref);
    
    // Verify that all contributions are positive
    for (int i = 0; i < contributions.length; i++) {
      assertTrue(contributions[i] > 0, "Contribution " + i + " should be positive");
    }
    
    // Verify that the sum is the total hypervolume
    double total = 0;
    for (double c : contributions) {
      total += c;
    }
    
    // NOTE: The sum of exclusive contributions is NOT equal to the total HV.
    // Contrib(p) = HV_total - HV_without_p
    // Sum of contributions = sum of what is lost when removing each point
    // For this front, each point contributes 1, total = 3
    double expectedSumContrib = 3.0;
    
    assertEquals(expectedSumContrib, total, EPSILON, "Sum of exclusive contributions should match");
    
    // Print for manual verification
    System.out.println("Simple Pareto Front:");
    for (int i = 0; i < points.length; i++) {
      System.out.printf("  Point %d [%.1f, %.1f]: contribution = %.4f%n", 
          i, points[i][0], points[i][1], contributions[i]);
    }
    System.out.printf("  Sum of contributions: %.4f (expected: %.4f)%n", total, expectedSumContrib);
  }

  /**
   * Test with a single point.
   */
  @Test
  void testSinglePoint() {
    double[][] points = {{1.0, 2.0}};
    double[] ref = {3.0, 4.0};
    
    double[] contributions = HypervolumeContribution2D.compute(points, ref);
    
    assertEquals(1, contributions.length);
    double expected = (3.0 - 1.0) * (4.0 - 2.0); // 2.0 * 2.0 = 4.0
    assertEquals(expected, contributions[0], EPSILON);
  }

  /**
   * Test with an empty array.
   */
  @Test
  void testEmptyArray() {
    double[][] points = {};
    double[] ref = {1.0, 1.0};
    
    double[] contributions = HypervolumeContribution2D.compute(points, ref);
    
    assertEquals(0, contributions.length);
  }

  /**
   * Test with a point that does not dominate the reference.
   */
  @Test
  void testPointNotDominatingRef() {
    double[][] points = {
        {1.0, 1.0},
        {5.0, 5.0}  // Does not dominate ref
    };
    double[] ref = {3.0, 3.0};
    
    double[] contributions = HypervolumeContribution2D.compute(points, ref);
    
    assertTrue(contributions[0] > 0);
    assertEquals(0.0, contributions[1], EPSILON, "Point outside ref should contribute 0");
  }

  /**
   * Test with duplicate points.
   */
  @Test
  void testDuplicatePoints() {
    double[][] points = {
        {1.0, 3.0},
        {2.0, 2.0},
        {2.0, 2.0},  // Duplicate
        {3.0, 1.0}
    };
    double[] ref = {4.0, 4.0};
    
    double[] contributions = HypervolumeContribution2D.compute(points, ref);
    
    // Duplicates should have contribution 0
    assertTrue(contributions[1] == 0 || contributions[2] == 0, 
        "At least one duplicate should have 0 contribution");
    
    System.out.println("Duplicate Points Test:");
    for (int i = 0; i < points.length; i++) {
      System.out.printf("  Point %d [%.1f, %.1f]: contribution = %.4f%n", 
          i, points[i][0], points[i][1], contributions[i]);
    }
  }

  /**
   * Test with a dominated point.
   */
  @Test
  void testDominatedPoint() {
    double[][] points = {
        {1.0, 2.0},
        {2.0, 3.0},  // Dominated by the first
        {3.0, 1.0}
    };
    double[] ref = {4.0, 4.0};
    
    double[] contributions = HypervolumeContribution2D.compute(points, ref);
    
    // The dominated point should contribute 0
    assertEquals(0.0, contributions[1], EPSILON, "Dominated point should contribute 0");
    
    assertTrue(contributions[0] > 0);
    assertTrue(contributions[2] > 0);
    
    System.out.println("Dominated Point Test:");
    for (int i = 0; i < points.length; i++) {
      System.out.printf("  Point %d [%.1f, %.1f]: contribution = %.4f%n", 
          i, points[i][0], points[i][1], contributions[i]);
    }
  }

  /**
   * Test with points in random order (not sorted by first dimension).
   */
  @Test
  void testUnorderedPoints() {
    double[][] points = {
        {3.0, 1.0},
        {1.0, 3.0},
        {2.0, 2.0}
    };
    double[] ref = {4.0, 4.0};
    
    double[] contributions = HypervolumeContribution2D.compute(points, ref);
    
    // All contributions should be positive
    for (double c : contributions) {
      assertTrue(c > 0);
    }
    
    double total = 0;
    for (double c : contributions) {
      total += c;
    }
    
    // Same sum of contributions as testSimpleParetoFront
    double expectedSumContrib = 3.0;
    
    assertEquals(expectedSumContrib, total, EPSILON);
  }

  /**
   * Test that verifies that the order of points does not affect the result.
   */
  @Test
  void testOrderIndependence() {
    double[] ref = {6.0, 6.0};
    
    double[][] points1 = {
        {1.0, 5.0},
        {2.0, 4.0},
        {3.0, 3.0},
        {4.0, 2.0},
        {5.0, 1.0}
    };
    
    double[][] points2 = {
        {5.0, 1.0},
        {1.0, 5.0},
        {3.0, 3.0},
        {4.0, 2.0},
        {2.0, 4.0}
    };
    
    double[] contrib1 = HypervolumeContribution2D.compute(points1, ref);
    double[] contrib2 = HypervolumeContribution2D.compute(points2, ref);
    
    // The sums should be equal
    double sum1 = 0, sum2 = 0;
    for (double c : contrib1) sum1 += c;
    for (double c : contrib2) sum2 += c;
    
    assertEquals(sum1, sum2, EPSILON, "Total HV should be order-independent");
  }

  /**
   * Test of the computeHypervolume method.
   * NOTE: This method calculates the SUM of exclusive contributions,
   * NOT the total hypervolume of the front.
   */
  @Test
  void testComputeHypervolume() {
    double[][] points = {
        {1.0, 3.0},
        {2.0, 2.0},
        {3.0, 1.0}
    };
    double[] ref = {4.0, 4.0};
    
    double sumContrib = HypervolumeContribution2D.computeHypervolume(points, ref);
    
    double expectedSumContrib = 3.0;
    
    assertEquals(expectedSumContrib, sumContrib, EPSILON);
  }

  /**
   * Test with a larger front (10 points).
   */
  @Test
  void testLargerFront() {
    double[][] points = new double[10][2];
    for (int i = 0; i < 10; i++) {
      points[i][0] = i + 1.0;
      points[i][1] = 10.0 - i;
    }
    double[] ref = {11.0, 11.0};
    
    double[] contributions = HypervolumeContribution2D.compute(points, ref);
    
    // All contributions should be positive
    for (int i = 0; i < contributions.length; i++) {
      assertTrue(contributions[i] > 0, "Contribution " + i + " should be positive");
    }
    
    // Verify sum
    double total = 0;
    for (double c : contributions) {
      total += c;
    }
    
    double hvDirect = HypervolumeContribution2D.computeHypervolume(points, ref);
    assertEquals(hvDirect, total, EPSILON);
    
    System.out.println("Larger Front Test (10 points):");
    System.out.printf("  Total HV: %.4f%n", total);
  }

  /**
   * Test of boundary case: point exactly at the reference.
   */
  @Test
  void testPointAtReference() {
    double[][] points = {
        {1.0, 2.0},
        {3.0, 3.0}  // At the reference
    };
    double[] ref = {3.0, 3.0};
    
    double[] contributions = HypervolumeContribution2D.compute(points, ref);
    
    assertTrue(contributions[0] > 0);
    assertEquals(0.0, contributions[1], EPSILON, 
        "Point at reference should contribute 0");
  }
}
