package org.uma.jmetal.qualityindicator;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
