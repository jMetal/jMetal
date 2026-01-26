package org.uma.jmetal.qualityindicator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.VectorUtils;

public class PISAHypervolumeTest {
  private final double EPSILON = 0.00000001;

  @Test
  void shouldConstructorWithReferencePointCreateAValidInstance() {
    PISAHypervolume hypervolume = new PISAHypervolume(new double[] {1.0, 1.0});

    double[][] referenceFront = hypervolume.referenceFront();
    assertEquals(2, referenceFront.length);
    assertEquals(2, referenceFront[0].length);
    assertEquals(1.0, hypervolume.referenceFront()[0][0], EPSILON);
    assertEquals(0.0, hypervolume.referenceFront()[0][1], EPSILON);
    assertEquals(0.0, hypervolume.referenceFront()[1][0], EPSILON);
    assertEquals(1.0, hypervolume.referenceFront()[1][1], EPSILON);
  }

  /**
   * CASE 1: solution set -> front obtained from the ZDT1.csv file. Reference front: [0,1], [1,0]
   *
   * @throws FileNotFoundException
   */
  @Test
  void shouldEvaluateWorkProperlyCase1() throws IOException {
    double[][] referenceFront = new double[][] {{1.0, 0.1}, {0.0, 1.0}};

    double[][] storedFront =
        VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ",");

    var hypervolume = new PISAHypervolume(referenceFront);
    double result = hypervolume.compute(storedFront);

    assertEquals(0.6661, result, 0.0001);
  }

  /**
   * CASE 2: solution set -> front obtained from the ZDT1.csv file. Reference front: the contents of
   * the same file
   *
   * @throws FileNotFoundException
   */
  @Test
  void shouldEvaluateWorkProperlyCase2() throws IOException {
    double[][] referenceFront =
        VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ",");

    double[][] storedFront =
        VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ",");

    var hypervolume = new PISAHypervolume(referenceFront);
    double result = hypervolume.compute(storedFront);

    assertEquals(0.6661, result, 0.0001);
  }

  /**
   * CASE 3: The hypervolume of a front composed of a point equals to the reference point is zero
   *
   * @throws FileNotFoundException
   */
  @Test
  void shouldEvaluateWorkProperlyCase3() {
    double[][] referenceFront = {{1.0, 0.1}, {0.0, 1.0}};

    double[][] front = new double[][] {{1.0, 0.0}, {0.0, 1.0}};

    var hypervolume = new PISAHypervolume(referenceFront);
    double result = hypervolume.compute(front);

    assertEquals(0, result, 0.0001);
  }

  @Test
  void shouldEvaluateWorkProperlyCase4() {
    double[][] referenceFront = {{1.0, 0.0}, {0.0, 1.0}};

    double[][] front = new double[][] {{1.0, 0.0}, {0.0, 1.0}, {0.5, 0.5}};

    var hypervolume = new PISAHypervolume(referenceFront);
    double result = hypervolume.compute(front);

    assertEquals(0.25, result, 0.0001);
  }

  @Test
  void shouldEvaluateWorkProperlyCase5() {
    double[][] referenceFront = {{1.0, 0.0}, {0.0, 1.0}};

    double[][] front =
        new double[][] {
          {1.0, 0.0},
          {0.0, 1.0},
          {0.1, 0.683772},
          {0.2, 0.552786},
          {0.3, 0.452277},
          {0.4, 0.367544},
          {0.5, 0.292893},
          {0.6, 0.225403},
          {0.7, 0.16334},
          {0.8, 0.105573},
          {0.9, 0.0513167}
        };
    var hypervolume = new PISAHypervolume(referenceFront);
    double result = hypervolume.compute(front);

    assertEquals(0.610509, result, 0.0001);
  }

  // Tests for computeHypervolumeContribution()
  
  @Test
  void shouldComputeHypervolumeContributionForSinglePoint() {
    double[] referencePoint = {1.0, 1.0};
    double[][] front = {{0.5, 0.5}};
    
    var hypervolume = new PISAHypervolume(referencePoint);
    double[] contributions = hypervolume.computeHypervolumeContribution(front);
    
    assertEquals(1, contributions.length);
    // The contribution of a single point equals the total hypervolume
    double totalHV = hypervolume.compute(front);
    assertEquals(totalHV, contributions[0], EPSILON);
  }
  
  @Test
  void shouldComputeHypervolumeContributionForTwoPoints2D() {
    double[] referencePoint = {1.0, 1.0};
    double[][] front = {{0.5, 0.5}, {0.25, 0.75}};
    
    var hypervolume = new PISAHypervolume(referencePoint);
    double[] contributions = hypervolume.computeHypervolumeContribution(front);
    
    assertEquals(2, contributions.length);
    // All contributions should be positive
    assertTrue(contributions[0] > 0);
    assertTrue(contributions[1] > 0);
    // Sum of contributions may not equal total HV due to overlap, so just check they're reasonable
    double totalHV = hypervolume.compute(front);
    assertTrue(contributions[0] < totalHV);
    assertTrue(contributions[1] < totalHV);
  }
  
  @Test
  void shouldComputeHypervolumeContributionForLShapedFront() {
    double[] referencePoint = {1.0, 1.0};
    double[][] front = {{0.2, 0.8}, {0.5, 0.5}, {0.8, 0.2}};
    
    var hypervolume = new PISAHypervolume(referencePoint);
    double[] contributions = hypervolume.computeHypervolumeContribution(front);
    
    assertEquals(3, contributions.length);
    // All contributions should be positive
    for (double contribution : contributions) {
      assertTrue(contribution > 0, "All contributions should be positive");
    }
  }
  
  @Test
  void shouldComputeHypervolumeContributionFor3DFront() {
    double[] referencePoint = {1.0, 1.0, 1.0};
    double[][] front = {{0.5, 0.5, 0.5}, {0.3, 0.6, 0.7}};
    
    var hypervolume = new PISAHypervolume(referencePoint);
    double[] contributions = hypervolume.computeHypervolumeContribution(front);
    
    assertEquals(2, contributions.length);
    // All contributions should be positive
    assertTrue(contributions[0] > 0);
    assertTrue(contributions[1] > 0);
  }
}
