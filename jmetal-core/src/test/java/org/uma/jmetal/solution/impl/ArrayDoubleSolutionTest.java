package org.uma.jmetal.solution.impl;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ArrayDoubleSolutionTest {
  private DoubleProblem problem ;

  @Before
  public void setup() {
    problem = new MockedDoubleProblem() ;
  }


  @Test
  public void shouldConstructorCreateAnObject() {
    DoubleSolution solution = problem.createSolution() ;

    assertNotNull(solution);
  }

  @Test
  public void shouldCopyConstructorCreateAnIdenticalSolution() {
    DoubleSolution solution = problem.createSolution() ;

    assertEquals(solution, solution.copy());
  }

  @Test
  public void shouldGetLowerBoundReturnTheRightValue() {
    DoubleSolution solution = problem.createSolution() ;

    assertEquals(problem.getLowerBound(0), solution.getLowerBound(0));
    assertEquals(problem.getLowerBound(1), solution.getLowerBound(1));
    assertEquals(problem.getLowerBound(2), solution.getLowerBound(2));
  }

  @Test
  public void shouldGetUpperBoundReturnTheRightValue() {
    DoubleSolution solution = problem.createSolution() ;

    assertEquals(problem.getUpperBound(0), solution.getUpperBound(0));
    assertEquals(problem.getUpperBound(1), solution.getUpperBound(1));
    assertEquals(problem.getUpperBound(2), solution.getUpperBound(2));
  }

  @SuppressWarnings("serial")
  private class MockedDoubleProblem extends AbstractDoubleProblem {
    public MockedDoubleProblem() {
      setNumberOfVariables(3);
      setNumberOfObjectives(2);
      setNumberOfConstraints(0);

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

      lowerLimit.add(-4.0);
      lowerLimit.add(-3.0);
      lowerLimit.add(-2.0);
      upperLimit.add(4.0);
      upperLimit.add(5.0);
      upperLimit.add(6.0);

      setLowerLimit(lowerLimit);
      setUpperLimit(upperLimit);
    }

    @Override
    public void evaluate(DoubleSolution solution) {
    }

    @Override
    public DoubleSolution createSolution() {
      return new ArrayDoubleSolution(this)  ;
    }
  }
}