package org.uma.jmetal.qualityindicator.util;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.GenericDoubleSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotEquals;

public class FastHypervolumeTest {
  private FastHypervolume fastHV ;

  @Before public void setup() {
    fastHV = new FastHypervolume() ;
  }

  @Test public void simpleTest() {
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

    double hv = fastHV.computeHypervolume(list) ;

    assertNotEquals(0, hv) ;
  }


  @Test public void simpleTest2() {
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

    fastHV.computeHVContributions(list); ;
  }



  /**
   * Mock class representing a binary problem
   */
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
      return new GenericDoubleSolution(this) ;
    }

    /** Evaluate() method */
    @Override
    public void evaluate(DoubleSolution solution) {
      solution.setObjective(0, 0.0);
      solution.setObjective(1, 1.0);
    }
  }
}
