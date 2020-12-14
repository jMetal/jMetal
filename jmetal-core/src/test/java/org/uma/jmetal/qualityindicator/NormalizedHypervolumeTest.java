package org.uma.jmetal.qualityindicator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.impl.ArrayFront;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class NormalizedHypervolumeTest {
  public static double EPSILON = 0.0000000001;

  private static String frontDirectory;
  private static String resourcesDirectory;

  @BeforeAll
  public static void startup() throws IOException {
    Properties jMetalProperties = new Properties();
    jMetalProperties.load(new FileInputStream("../jmetal.properties"));

    resourcesDirectory = "../" + jMetalProperties.getProperty("resourcesDirectory");
    frontDirectory =
        resourcesDirectory + "/" + jMetalProperties.getProperty("referenceFrontsDirectory");
  }

  @Test
  public void shouldConstructorWithReferencePointCreateAValidInstance() {
    var normalizedHypervolume = new NormalizedHypervolume(new double[] {1.0, 1.0});

    Assertions.assertNotNull(normalizedHypervolume);
  }

  @Test
  public void shouldEvaluateReturnZeroIfTheReferenceFrontIsEvaluatedWithItself() throws IOException {
    var relativeHypervolume = new NormalizedHypervolume("../resources/referenceFrontsCSV/ZDT1.csv");
    double[][] front = VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ",");

    Assertions.assertEquals(0.0, relativeHypervolume.compute(front), EPSILON);
  }

  /*
  @Test
  @Ignore
  public void shouldEvaluateReturnZeroIfTheReferenceFrontIsEvaluatedWithAReferencePoint()
      throws FileNotFoundException {
    var normalizedHypervolume = new NormalizedHypervolume<PointSolution>(new double[] {1.0, 1.0});

    Front frontToEvaluate = new ArrayFront(frontDirectory +"/ZDT1.pf");
    assertEquals(
        0.0,
            normalizedHypervolume.evaluate(FrontUtils.convertFrontToSolutionList(frontToEvaluate)),
        EPSILON);
  }

   */
}
