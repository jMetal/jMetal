package org.uma.jmetal.qualityindicator.impl.hypervolume.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.uma.jmetal.util.VectorUtils;

public class PISAHypervolumeTest {
  private final double EPSILON = 0.00000001;

  @Test
  public void shouldConstructorWithReferencePointCreateAValidInstance() {
    PISAHypervolume hypervolume = new PISAHypervolume(new double[] {1.0, 1.0});

    double[][] referenceFront = hypervolume.getReferenceFront();
    assertEquals(2, referenceFront.length);
    assertEquals(2, referenceFront[0].length);
    assertEquals(1.0, hypervolume.getReferenceFront()[0][0], EPSILON);
    assertEquals(0.0, hypervolume.getReferenceFront()[0][1], EPSILON);
    assertEquals(0.0, hypervolume.getReferenceFront()[1][0], EPSILON);
    assertEquals(1.0, hypervolume.getReferenceFront()[1][1], EPSILON);
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


    @Test
    public void chatgptCodeTest() throws IOException {

      class Hypervolume {
        private final int numberOfObjectives;
        private final double[] referencePoint;

        public Hypervolume(int numberOfObjectives, double[] referencePoint) {
          this.numberOfObjectives = numberOfObjectives;
          this.referencePoint = referencePoint;
        }

        public double calculate(double[][] solutions) {
          double hypervolume = 0.0;

          int numberOfSolutions = solutions.length;
          double[][] normalizedSolutions = new double[numberOfSolutions][numberOfObjectives];
          for (int i = 0; i < numberOfSolutions; i++) {
            for (int j = 0; j < numberOfObjectives; j++) {
              normalizedSolutions[i][j] = (solutions[i][j] - referencePoint[j]) / (1.0 - referencePoint[j]);
            }
          }

          Arrays.sort(normalizedSolutions, (a, b) -> {
            for (int i = 0; i < numberOfObjectives; i++) {
              int comparison = Double.compare(b[i], a[i]);
              if (comparison != 0) {
                return comparison;
              }
            }
            return 0;
          });

          for (int i = 0; i < numberOfSolutions; i++) {
            double volume = 1.0;
            for (int j = 0; j < numberOfObjectives; j++) {
              volume *= normalizedSolutions[i][j];
            }
            hypervolume += volume;
          }

          return hypervolume;
        }
      }

      double[][] storedFront =
          VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ",");

      var hv = new Hypervolume(2, new double[]{1.1, 1.1}) ;
      double result = hv.calculate(storedFront) ;

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
