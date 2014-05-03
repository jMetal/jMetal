package jmetal.test.encodings.variable;

import jmetal.encodings.variable.Real;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 29/06/13
 * Time: 17:13
 */
public class RealTest extends Real{
  /**
   * 
   */
  private static final long serialVersionUID = 6960526243909043071L;

  Real real_ ;
  static final double EPSILON = 0.00000000000001;

  @Before
  public void setUp() throws  Exception {
    real_  = new Real(-1.0, 1.0, 0.5) ;
  }

  @After
  public void tearDown() throws Exception {
    real_ = null ;
  }

  @Test
  public void testGetValue() throws Exception {
      Assert.assertEquals("RealTest", 0.5, real_.getValue(), EPSILON) ;
  }

  @Test
  public void testSetValue() throws Exception {
     double oldValue = real_.getValue() ;
     real_.setValue(0.5);
     Assert.assertEquals("RealTest", oldValue, real_.getValue(), EPSILON);
  }

  @Test
  public void testDeepCopy() throws Exception {
    Real real = (Real)real_.deepCopy() ;
    Assert.assertEquals("RealTest", real.toString(), real_.toString());
  }

  @Test
  public void testGetLowerBound() throws Exception {
      Assert.assertEquals("RealTest", -1.0, real_.getLowerBound(), EPSILON) ;
  }

  @Test
  public void testGetUpperBound() throws Exception {
      Assert.assertEquals("RealTest", 1.0, real_.getUpperBound(), EPSILON) ;
  }

  @Test
  public void testSetLowerBound() throws Exception {
    //double oldValue = real_.getValue() ;
    real_.setLowerBound(0.1);
    Assert.assertEquals("RealTest", 0.1, real_.getLowerBound(), EPSILON);
  }

  @Test
  public void testSetUpperBound() throws Exception {
    //double oldValue = real_.getValue() ;
    real_.setUpperBound(2.0);
    Assert.assertEquals("RealTest", 2.0, real_.getUpperBound(), EPSILON);
  }
}
