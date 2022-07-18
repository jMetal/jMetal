package org.uma.jmetal.qualityindicator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.util.VectorUtils;

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
    var jMetalProperties = new Properties();
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
  public void shouldEvaluateReturnZeroIfTheReferenceFrontIsEvaluatedWithItself()
      throws IOException {
    var relativeHypervolume =
        new NormalizedHypervolume(
            VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ","));
    var front = VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ",");

    Assertions.assertEquals(0.0, relativeHypervolume.compute(front), EPSILON);
  }
}
