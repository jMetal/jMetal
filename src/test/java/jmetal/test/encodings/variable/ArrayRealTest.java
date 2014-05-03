package jmetal.test.encodings.variable;

import jmetal.core.Problem;
import jmetal.encodings.variable.ArrayReal;
import jmetal.problems.Fonseca;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 28/06/13
 * Time: 07:50
 */
public class ArrayRealTest {
  static final double EPSILON = 0.0000000000001 ;
  ArrayReal arrayReal_ ;
  Problem problem_ ;

  @Before
  public void setUp() throws Exception {
    problem_ = new Fonseca("Real") ;
    arrayReal_ = new ArrayReal(problem_.getNumberOfVariables(), problem_) ;
  }

  @After
  public void tearDown() throws Exception {
    problem_ = null;
    arrayReal_ = null ;
  }

  @Test
  public void testDeepCopy() throws Exception {

  }

  @Test
  public void testGetLength() throws Exception {

  }

  @Test
  public void testGetValue() throws Exception {

  }

  @Test
  public void testSetValue() throws Exception {

  }

  @Test
  public void testGetLowerBound() throws Exception {

  }

  @Test
  public void testGetUpperBound() throws Exception {

  }

  @Test
  public void testToString() throws Exception {

  }
}
