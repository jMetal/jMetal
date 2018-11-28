package org.uma.jmetal.qualityindicator.impl.hypervolume;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.qualityindicator.impl.Hypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.point.impl.ArrayPoint;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by ajnebro on 17/12/15.
 */
public class WFGHypervolumeTest {
  private Hypervolume<DoubleSolution> hypervolume ;

  @Before
  public void setup() {
    hypervolume = new WFGHypervolume<>() ;
  }

  @Test
  public void simpleTest() {
    DoubleProblem problem = new MockDoubleProblem(2) ;

    DoubleSolution solution = problem.createSolution() ;
    solution.setObjective(0, 0.0);
    solution.setObjective(1, 1.0);

    DoubleSolution solution2 = problem.createSolution() ;
    solution2.setObjective(0, -1.0);
    solution2.setObjective(1, 2.0);

    DoubleSolution solution3 = problem.createSolution() ;
    solution3.setObjective(0, -2.0);
    solution3.setObjective(1, 1.5);

    List<DoubleSolution> list = Arrays.asList(solution, solution2, solution3) ;

    double hv = hypervolume.evaluate(list) ;

    assertNotEquals(0, hv) ;
  }


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

    WFGHypervolume<DoubleSolution> hypervolume = new WFGHypervolume<>() ;
    double result = hypervolume.computeHypervolume(frontToEvaluate, new ArrayPoint(new double[]{1.0, 1.0})) ;

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

    WFGHypervolume<DoubleSolution> hypervolume = new WFGHypervolume<>() ;
    double result = hypervolume.computeHypervolume(frontToEvaluate, new ArrayPoint(new double[]{1.0, 1.0})) ;

    assertEquals(0.25*0.75 + 0.25*0.5 + 0.25*0.25, result, 0.0001) ;
  }

  /**
   * CASE 3: solution set -> front composed of the points [0.25, 0.75], [0.75, 0.25] and [0.5, 0.5].
   * Reference point: [1.5, 1.5]
   */
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

    WFGHypervolume<DoubleSolution> hypervolume = new WFGHypervolume<>() ;
    double result = hypervolume.computeHypervolume(frontToEvaluate, new ArrayPoint(new double[]{1.5, 1.5})) ;

    assertEquals((1.5 - 0.75) * (1.5 - 0.25) + (0.75 - 0.5) * (1.5 - 0.5) + (0.5 - 0.25) * (1.5 - 0.75), result, 0.0001) ;
  }

  /**
   * CASE 4: solution set -> front obtained from the ZDT1.rf file. Reference point: [1.0, 1,0]
   * @throws FileNotFoundException
   */
  @Test
  public void shouldEvaluateWorkProperlyCase4() throws FileNotFoundException {
    Front storeFront = new ArrayFront("/pareto_fronts/ZDT1.pf") ;

    DoubleProblem problem = new MockDoubleProblem(2) ;

    List<DoubleSolution> frontToEvaluate = new ArrayList<>() ;
    for (int i = 0 ; i < storeFront.getNumberOfPoints(); i++) {
      DoubleSolution solution = problem.createSolution() ;
      solution.setObjective(0, storeFront.getPoint(i).getValue(0));
      solution.setObjective(1, storeFront.getPoint(i).getValue(1));
      frontToEvaluate.add(solution) ;
    }

    WFGHypervolume<DoubleSolution> hypervolume = new WFGHypervolume<>() ;
    double result = hypervolume.computeHypervolume(frontToEvaluate, new ArrayPoint(new double[]{1.0, 1.0})) ;

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

      setLowerLimit(lowerLimit);
      setUpperLimit(upperLimit);
    }

    @Override
    public DoubleSolution createSolution() {
      return new DefaultDoubleSolution(this) ;
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