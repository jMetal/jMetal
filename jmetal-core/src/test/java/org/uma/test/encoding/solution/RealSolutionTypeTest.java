package org.uma.test.encoding.solution;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.Variable;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.encoding.variable.Real;
import org.uma.jmetal.problem.multiobjective.Kursawe;

import static org.junit.Assert.*;

public class RealSolutionTypeTest {
  static final int PROBLEM_VARIABLES = 3 ;
  static final double EPSILON = 0.000000000001 ;
  Problem problem;
  Solution solution;
  RealSolutionType solutionType;

  @Before
  public void setUp() throws Exception {
    problem = new Kursawe("Real", PROBLEM_VARIABLES) ;
    solution = new Solution(problem) ;
    solutionType = new RealSolutionType(problem) ;
    ((Real)solution.getDecisionVariables()[0]).setValue(1.0);
    ((Real)solution.getDecisionVariables()[1]).setValue(-2.23);
    ((Real)solution.getDecisionVariables()[2]).setValue(25.325);
  }

  @After
  public void tearDown() throws Exception {
    problem = null ;
    solution = null ;
  }

  @Test
  public void testGetProblem() throws Exception {
    assertEquals(problem, solution.getType().getProblem()) ;
  }

  @Test
  public void testCreateVariables() throws Exception {
    Variable[] variables = new RealSolutionType(problem).createVariables() ;
    assertNotNull(variables) ;
    assertEquals(PROBLEM_VARIABLES, variables.length);
    assertNotEquals(PROBLEM_VARIABLES-1, variables.length);
  }

  @Test
  public void testGetRealValue() throws Exception {
    assertEquals(1.0, solutionType.getRealValue(solution, 0), EPSILON);
    assertEquals(-2.23, solutionType.getRealValue(solution, 1), EPSILON);
    assertEquals(25.325, solutionType.getRealValue(solution, 2), EPSILON);
    assertNotEquals(0, solutionType.getRealValue(solution, 0), EPSILON);
  }

  @Test
  public void testSetRealValue() throws Exception {
    solutionType.setRealValue(solution, 0, -134);
    solutionType.setRealValue(solution, 1, 2134.6);
    solutionType.setRealValue(solution, 2, 0.5);
    assertEquals(-134, solutionType.getRealValue(solution, 0), EPSILON);
    assertEquals(2134.6, solutionType.getRealValue(solution, 1), EPSILON);
    assertEquals(0.5, solutionType.getRealValue(solution, 2), EPSILON);
  }

  @Test
  public void testGetNumberOfRealVariables() throws Exception {
    assertEquals(PROBLEM_VARIABLES, solutionType.getNumberOfRealVariables(solution));
  }

  @Test
  public void testGetRealUpperBound() throws Exception {
     for (int i = 0; i < problem.getNumberOfVariables(); i++) {
       assertEquals(problem.getUpperLimit(i), solutionType.getRealUpperBound(solution, i), EPSILON);
     }
  }

  @Test
  public void testGetRealLowerBound() throws Exception {
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      assertEquals(problem.getLowerLimit(i), solutionType.getRealLowerBound(solution, i), EPSILON);
    }
  }

  @Test
  public void testCopyVariables() throws Exception {
    Variable[] vars = solutionType.copyVariables(solution.getDecisionVariables()) ;
    assertEquals(PROBLEM_VARIABLES, vars.length);
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      assertEquals(((Real)vars[i]).getValue(), solutionType.getRealValue(solution, i), EPSILON);
    }
  }

  @Test (expected = ArrayIndexOutOfBoundsException.class)
  public void testAccessingOutOfBoundsVariable() throws Exception {
    solutionType.getRealValue(solution, 5) ;
  }

  @Test (expected = ArrayIndexOutOfBoundsException.class)
  public void testWritingOutOfBoundsVariable() throws Exception {
    solutionType.setRealValue(solution, 5, 1.0) ;
  }

  @Test (expected = ArrayIndexOutOfBoundsException.class)
  public void testAccessingNegativeIndexVariable() throws Exception {
    solutionType.getRealValue(solution, -1) ;
  }

  @Test (expected = ArrayIndexOutOfBoundsException.class)
  public void testWritingNegativeIndexVariable() throws Exception {
    solutionType.setRealValue(solution, -1, 4.0) ;
  }
}
