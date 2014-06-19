package org.uma.test.encoding.solution;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.Variable;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.problem.Kursawe;

import static org.junit.Assert.*;

public class RealSolutionTypeTest {
  static final int PROBLEM_VARIABLES = 3 ;
  static final double EPSILON = 0.000000000001 ;
  Problem problem_ ;
  Solution solution_ ;
  RealSolutionType solutionType_ ;

  @Before
  public void setUp() throws Exception {
    problem_ = new Kursawe("Real", PROBLEM_VARIABLES) ;
    solution_ = new Solution(problem_) ;
    solutionType_ = new RealSolutionType(problem_) ;
    solution_.getDecisionVariables()[0].setValue(1.0);
    solution_.getDecisionVariables()[1].setValue(-2.23);
    solution_.getDecisionVariables()[2].setValue(25.325);
  }

  @After
  public void tearDown() throws Exception {
    problem_ = null ;
    solution_ = null ;
  }

  @Test
  public void testGetProblem() throws Exception {
    assertEquals(problem_, solution_.getType().getProblem()) ;
  }

  @Test
  public void testCreateVariables() throws Exception {
    Variable[] variables = new RealSolutionType(problem_).createVariables() ;
    assertNotNull(variables) ;
    assertEquals(PROBLEM_VARIABLES, variables.length);
    assertNotEquals(PROBLEM_VARIABLES-1, variables.length);
  }

  @Test
  public void testGetRealValue() throws Exception {
    assertEquals(1.0, solutionType_.getRealValue(solution_, 0), EPSILON);
    assertEquals(-2.23, solutionType_.getRealValue(solution_, 1), EPSILON);
    assertEquals(25.325, solutionType_.getRealValue(solution_, 2), EPSILON);
    assertNotEquals(0, solutionType_.getRealValue(solution_, 0), EPSILON);
  }

  @Test
  public void testSetRealValue() throws Exception {
    solutionType_.setRealValue(solution_, 0, -134);
    solutionType_.setRealValue(solution_, 1, 2134.6);
    solutionType_.setRealValue(solution_, 2, 0.5);
    assertEquals(-134, solutionType_.getRealValue(solution_, 0), EPSILON);
    assertEquals(2134.6, solutionType_.getRealValue(solution_, 1), EPSILON);
    assertEquals(0.5, solutionType_.getRealValue(solution_, 2), EPSILON);
  }

  @Test
  public void testGetNumberOfRealVariables() throws Exception {
    assertEquals(PROBLEM_VARIABLES, solutionType_.getNumberOfRealVariables(solution_));
  }

  @Test
  public void testGetRealUpperBound() throws Exception {
     for (int i = 0; i < problem_.getNumberOfVariables(); i++) {
       assertEquals(problem_.getUpperLimit(i), solutionType_.getRealUpperBound(solution_, i), EPSILON);
     }
  }

  @Test
  public void testGetRealLowerBound() throws Exception {
    for (int i = 0; i < problem_.getNumberOfVariables(); i++) {
      assertEquals(problem_.getLowerLimit(i), solutionType_.getRealLowerBound(solution_, i), EPSILON);
    }
  }

  @Test
  public void testCopyVariables() throws Exception {
    Variable[] vars = solutionType_.copyVariables(solution_.getDecisionVariables()) ;
    assertEquals(PROBLEM_VARIABLES, vars.length);
    for (int i = 0; i < problem_.getNumberOfVariables(); i++) {
      assertEquals(vars[i].getValue(), solutionType_.getRealValue(solution_, i), EPSILON);
    }
  }
}
