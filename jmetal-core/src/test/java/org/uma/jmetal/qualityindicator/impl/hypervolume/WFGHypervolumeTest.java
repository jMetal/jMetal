package org.uma.jmetal.qualityindicator.impl.hypervolume;

import org.junit.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.WFGHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.impl.ArrayFront;
import org.uma.jmetal.util.point.impl.ArrayPoint;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by ajnebro on 17/12/15.
 */
public class WFGHypervolumeTest {
  /**
   * CASE 1: solution set -> front composed of the points [0.25, 0.75] and [0.75, 0.25]. Reference point: [1.0, 1.0]
   */
  @Test
  public void shouldEvaluateWorkProperlyCase1() throws FileNotFoundException {
    DoubleProblem problem = new MockDoubleProblem(2) ;

    List<DoubleSolution> frontToEvaluate = new ArrayList<>() ;

    DoubleSolution solution = problem.createSolution() ;
    solution.setObjective(0, 0.25);
    solution.setObjective(1, 0.75);
    frontToEvaluate.add(solution) ;

    solution = problem.createSolution() ;
    solution.setObjective(0, 0.75);
    solution.setObjective(1, 0.25);
    frontToEvaluate.add(solution) ;

    Front referenceFront = new ArrayFront(1, 2) ;
    referenceFront.setPoint(0, new ArrayPoint(new double[]{1.0, 1.0}));

    WFGHypervolume<DoubleSolution> hypervolume = new WFGHypervolume<>(referenceFront) ;
    double result = hypervolume.evaluate(frontToEvaluate) ;

    assertEquals(0.25*0.75 + 0.25*0.5, result, 0.0001) ;
  }

  /**
   * CASE 2: solution set -> front composed of the points [0.25, 0.75], [0.75, 0.25] and [0.5, 0.5].
   * Reference point: [1.0, 1.0]
   */
  @Test
  public void shouldEvaluateWorkProperlyCase2() throws FileNotFoundException {
    DoubleProblem problem = new MockDoubleProblem(2) ;

    List<DoubleSolution> frontToEvaluate = new ArrayList<>() ;

    DoubleSolution solution = problem.createSolution() ;
    solution.setObjective(0, 0.25);
    solution.setObjective(1, 0.75);
    frontToEvaluate.add(solution) ;

    solution = problem.createSolution() ;
    solution.setObjective(0, 0.75);
    solution.setObjective(1, 0.25);
    frontToEvaluate.add(solution) ;

    solution = problem.createSolution() ;
    solution.setObjective(0, 0.5);
    solution.setObjective(1, 0.5);
    frontToEvaluate.add(solution) ;

    Front referenceFront = new ArrayFront(1, 2) ;
    referenceFront.setPoint(0, new ArrayPoint(new double[]{1.0, 1.0}));

    WFGHypervolume<DoubleSolution> hypervolume = new WFGHypervolume<>(referenceFront) ;
    double result = hypervolume.evaluate(frontToEvaluate) ;

    assertEquals(0.25*0.75 + 0.25*0.5 + 0.25*0.25, result, 0.0001) ;
  }

  /**
   * CASE 3: solution set -> front composed of the points [0.25, 0.75], [0.75, 0.25] and [0.5, 0.5].
   * Reference point: [1.5, 1.5]
   */
  /*
  @Test
  public void shouldEvaluateWorkProperlyCase3() throws FileNotFoundException {
    DoubleProblem problem = new MockDoubleProblem(2) ;

    List<DoubleSolution> frontToEvaluate = new ArrayList<>() ;

    DoubleSolution solution = problem.createSolution() ;
    solution.setObjective(0, 0.25);
    solution.setObjective(1, 0.75);
    frontToEvaluate.add(solution) ;

    solution = problem.createSolution() ;
    solution.setObjective(0, 0.75);
    solution.setObjective(1, 0.25);
    frontToEvaluate.add(solution) ;

    solution = problem.createSolution() ;
    solution.setObjective(0, 0.5);
    solution.setObjective(1, 0.5);
    frontToEvaluate.add(solution) ;

    Front referenceFront = new ArrayFront(1, 2) ;
    referenceFront.setPoint(0, new ArrayPoint(new double[]{1.5, 1.5}));

    WFGHypervolume<DoubleSolution> hypervolume = new WFGHypervolume<>(referenceFront) ;
    double result = hypervolume.evaluate(frontToEvaluate) ;

    //WFGHypervolume<DoubleSolution> hypervolume = new WFGHypervolume<>() ;
    //double result = hypervolume.computeHypervolume(frontToEvaluate, new ArrayPoint(new double[]{1.5, 1.5})) ;

    assertEquals((1.5 - 0.75) * (1.5 - 0.25) + (0.75 - 0.5) * (1.5 - 0.5) + (0.5 - 0.25) * (1.5 - 0.75), result, 0.0001) ;
  }
*/
  /**
   * CASE 4: solution set -> front obtained from the ZDT1.rf file. Reference point: [1.0, 1,0]
   * @throws FileNotFoundException
   */
  @Test
  public void shouldEvaluateWorkProperlyCase4() throws FileNotFoundException {
    Front storeFront = new ArrayFront("../resources/referenceFrontsCSV/ZDT1.csv") ;

    DoubleProblem problem = new MockDoubleProblem(2) ;

    List<DoubleSolution> frontToEvaluate = new ArrayList<>() ;
    for (int i = 0 ; i < storeFront.getNumberOfPoints(); i++) {
      DoubleSolution solution = problem.createSolution() ;
      solution.setObjective(0, storeFront.getPoint(i).getValue(0));
      solution.setObjective(1, storeFront.getPoint(i).getValue(1));
      frontToEvaluate.add(solution) ;
    }

    Front referenceFront = new ArrayFront(1, 2) ;
    referenceFront.setPoint(0, new ArrayPoint(new double[]{1.0, 1.0}));

    WFGHypervolume<DoubleSolution> hypervolume = new WFGHypervolume<>(referenceFront) ;
    double result = hypervolume.evaluate(frontToEvaluate) ;

    assertEquals(0.6661, result, 0.0001) ;
  }

  /**
   * Mock class representing a binary problem
   */
  @SuppressWarnings("serial")
  private class MockDoubleProblem extends AbstractDoubleProblem {
    /** Constructor */
    public MockDoubleProblem(Integer numberOfObjectives) {
      setNumberOfVariables(10);
      setNumberOfObjectives(numberOfObjectives);

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(-4.0);
        upperLimit.add(4.0);
      }

      setVariableBounds(lowerLimit, upperLimit);
    }

    /** Evaluate() method */
    @Override
    public void evaluate(DoubleSolution solution) {
      for (int i = 0; i < getNumberOfObjectives(); i++) {
        solution.setObjective(i, 1.0);
      }
    }
  }
}