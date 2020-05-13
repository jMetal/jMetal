package org.uma.jmetal.qualityindicator;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.impl.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetal.util.point.impl.ArrayPoint;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class NormalizedHypervolumeTest {
  public static double EPSILON = 0.0000000001;

  private static String frontDirectory ;
  private static String resourcesDirectory ;

  @Rule public ExpectedException exception = ExpectedException.none();

  @BeforeClass
  public static void startup() throws IOException {
    Properties jMetalProperties = new Properties() ;
    jMetalProperties.load(new FileInputStream("../jmetal.properties"));

    resourcesDirectory = "../" + jMetalProperties.getProperty("resourcesDirectory") ;
    frontDirectory = resourcesDirectory + "/" +jMetalProperties.getProperty("referenceFrontsDirectory") ;
  }

  @Test
  public void shouldConstructorWithReferencePointCreateAValidInstance() {
    var normalizedHypervolume = new NormalizedHypervolume<>(new double[] {1.0, 1.0});

    assertNotNull(normalizedHypervolume);
  }

  @Test
  public void shouldEvaluateReturnOneInTheSimplestCase() {
    var normalizedHypervolume = new NormalizedHypervolume<PointSolution>(new double[] {1.0, 1.0});

    Front frontToEvaluate = new ArrayFront(1, 2);
    frontToEvaluate.setPoint(0, new ArrayPoint(new double[] {1.0, 1.0}));

    var point = new PointSolution(2);
    point.setObjective(0, 1.0);
    point.setObjective(1, 1.0);
    List<PointSolution> list = List.of(point);

    assertEquals(1.0, normalizedHypervolume.evaluate(list), EPSILON);
  }

  @Test
  public void shouldEvaluateReturnZeroIfTheReferenceFrontIsEvaluatedWithItself()
      throws FileNotFoundException {
    var relativeHypervolume =
        new NormalizedHypervolume<PointSolution>("../resources/referenceFrontsCSV/ZDT1.csv");

    Front frontToEvaluate = new ArrayFront(frontDirectory +"/ZDT1.csv");
    assertEquals(
        0.0,
        relativeHypervolume.evaluate(FrontUtils.convertFrontToSolutionList(frontToEvaluate)),
        EPSILON);
  }

  @Test
  @Ignore
  public void shouldEvaluateReturnZeroIfTheReferenceFrontIsEvaluatedWithAReferencePoint()
      throws FileNotFoundException {
    var normalizedHypervolume = new NormalizedHypervolume<PointSolution>(new double[] {1.0, 1.0});

    Front frontToEvaluate = new ArrayFront(frontDirectory +"/ZDT1.csv");
    assertEquals(
        0.0,
            normalizedHypervolume.evaluate(FrontUtils.convertFrontToSolutionList(frontToEvaluate)),
        EPSILON);
  }
}
