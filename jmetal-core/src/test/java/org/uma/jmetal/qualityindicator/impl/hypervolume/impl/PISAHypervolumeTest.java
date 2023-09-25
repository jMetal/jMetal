package org.uma.jmetal.qualityindicator.impl.hypervolume.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.uma.jmetal.util.VectorUtils;

public class PISAHypervolumeTest {
  private final double EPSILON = 0.00000001;

  @Test
  public void shouldConstructorWithReferencePointCreateAValidInstance() {
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
  public void shouldEvaluateWorkProperlyCase1() throws IOException {
    double[][] referenceFront = new double[][] {{1.0, 0.1}, {0.0, 1.0}};

    double[][] storedFront =
        VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ",");

    var hypervolume = new PISAHypervolume(referenceFront);
    double result = hypervolume.compute(storedFront);

    Assert.assertEquals(0.6661, result, 0.0001);
  }

    /**
     * CASE 2: solution set -> front obtained from the ZDT1.csv file. Reference front: the contents of the same file
     *
     * @throws FileNotFoundException
     */
    @Test
    public void shouldEvaluateWorkProperlyCase2() throws IOException {
        double[][] referenceFront = VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ",") ;

        double[][] storedFront =
                VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ",");

        var hypervolume = new PISAHypervolume(referenceFront);
        double result = hypervolume.compute(storedFront);

        Assert.assertEquals(0.6661, result, 0.0001);
    }

    /**
     * CASE 3: The hypervolume of a front composed of a point equals to the reference point is zero
     *
     * @throws FileNotFoundException
     */
    @Test
    public void shouldEvaluateWorkProperlyCase3()  {
        double[][] referenceFront = {{1.0, 0.1}, {0.0, 1.0}};

        double[][] front = new double[][] {{1.0, 0.0},{0.0, 1.0}} ;

        var hypervolume = new PISAHypervolume(referenceFront);
        double result = hypervolume.compute(front);

        Assert.assertEquals(0, result, 0.0001);
    }
}
