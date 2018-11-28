package org.uma.jmetal.qualityindicator.impl.hypervolume;

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
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PISAHypervolumeTest {
  /**
   * CASE 1: solution set -> front obtained from the ZDT1.rf file. Reference front: [0,1], [1,0]
   * @throws FileNotFoundException
   */
  @Test
  public void shouldEvaluateWorkProperlyCase1() throws FileNotFoundException {
    Front referenceFront = new ArrayFront(2, 2) ;
    referenceFront.setPoint(0, new ArrayPoint(new double[]{1.0, 0.0}));
    referenceFront.setPoint(0, new ArrayPoint(new double[]{0.0, 1.0}));

    Front storeFront = new ArrayFront("/pareto_fronts/ZDT1.pf") ;

    DoubleProblem problem = new MockDoubleProblem(2) ;

    List<DoubleSolution> frontToEvaluate = new ArrayList<>() ;
    for (int i = 0 ; i < storeFront.getNumberOfPoints(); i++) {
      DoubleSolution solution = problem.createSolution() ;
      solution.setObjective(0, storeFront.getPoint(i).getValue(0));
      solution.setObjective(1, storeFront.getPoint(i).getValue(1));
      frontToEvaluate.add(solution) ;
    }

    Hypervolume<DoubleSolution> hypervolume = new PISAHypervolume<>(referenceFront) ;
    double result = hypervolume.evaluate(frontToEvaluate) ;

    assertEquals(0.6661, result, 0.0001) ;
  }

  private class MockDoubleProblem extends AbstractDoubleProblem {

    /** Constructor */
    public MockDoubleProblem(Integer numberOfVariables) {
      setNumberOfVariables(numberOfVariables);
      setNumberOfObjectives(2);

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
      solution.setObjective(0, 0.0);
      solution.setObjective(1, 1.0);
    }
  }
}