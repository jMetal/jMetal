package org.uma.jmetal.qualityindicator.impl.hypervolume;

import org.junit.Test;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.impl.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetal.util.point.impl.ArrayPoint;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

public class PISAHypervolumeTest {
  private final double EPSILON = 0.00000001 ;
  @Test
  public void shouldConstructorWithReferencePointCreateAValidInstance() {
    PISAHypervolume<DoubleSolution> hypervolume = new PISAHypervolume<>(new double[]{1.0, 1.0}) ;

    Front referenceFront = hypervolume.getReferenceParetoFront() ;
    assertEquals(2, referenceFront.getNumberOfPoints());
    assertEquals(2,referenceFront.getPointDimensions());
    assertEquals(1.0, hypervolume.getReferenceParetoFront().getPoint(0).getValue(0), EPSILON);
    assertEquals(0.0, hypervolume.getReferenceParetoFront().getPoint(0).getValue(1), EPSILON);
    assertEquals(0.0, hypervolume.getReferenceParetoFront().getPoint(1).getValue(0), EPSILON);
    assertEquals(1.0, hypervolume.getReferenceParetoFront().getPoint(1).getValue(1), EPSILON);
   }

  /**
   * CASE 1: solution set -> front obtained from the ZDT1.rf file. Reference front: [0,1], [1,0]
   * @throws FileNotFoundException
   */
  @Test
  public void shouldEvaluateWorkProperlyCase1() throws FileNotFoundException {
    Front referenceFront = new ArrayFront(2, 2) ;
    referenceFront.setPoint(0, new ArrayPoint(new double[]{1.0, 0.0}));
    referenceFront.setPoint(0, new ArrayPoint(new double[]{0.0, 1.0}));

    Front storedFront = new ArrayFront("../resources/referenceFrontsCSV/ZDT1.csv") ;

    Hypervolume<PointSolution> hypervolume = new PISAHypervolume<>(referenceFront) ;
    double result = hypervolume.evaluate(FrontUtils.convertFrontToSolutionList(storedFront)) ;

    assertEquals(0.6661, result, 0.0001) ;
  }

  @Test
  public void shouldEvaluateWorkProperlyCase2() throws FileNotFoundException {
    Front storedFront = new ArrayFront("../resources/referenceFrontsCSV/ZDT1.csv") ;

    Hypervolume<PointSolution> hypervolume = new PISAHypervolume<>(new double[]{1.0, 1.0}) ;
    double result = hypervolume.evaluate(FrontUtils.convertFrontToSolutionList(storedFront)) ;

    assertEquals(0.6661, result, 0.0001) ;
  }
}