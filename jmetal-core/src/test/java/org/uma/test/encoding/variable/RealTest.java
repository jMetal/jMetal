package org.uma.test.encoding.variable;

import org.uma.jmetal.encoding.variable.Int;
import org.uma.jmetal.encoding.variable.Real;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 29/06/13
 * Time: 17:13
 */
public class RealTest extends Real{
  Real realVariable ;
  static final double EPSILON = 0.00000000000001;

  @Before
  public void setUp() throws  Exception {
  	realVariable = new Real(0.5, -1.0, 1.0) ;
  }

  @After
  public void tearDown() throws Exception {
  	realVariable = null ;
  }

  @Test
  public void getValueTest() throws Exception {
    assertEquals("RealTest", 0.5, realVariable.getValue(), EPSILON) ;
  }

  @Test
  public void setValueTest() throws Exception {
     double oldValue = realVariable.getValue() ;
     realVariable.setValue(0.364);
     assertEquals("RealTest", 0.364, realVariable.getValue(), EPSILON);
  }

  @Test
  public void copyTest() throws Exception {
    Real real = (Real)realVariable.copy() ;
    assertEquals("RealTest", real.toString(), real.toString());
  }

  @Test
  public void getLowerBoundTest() throws Exception {
    assertEquals("RealTest", -1.0, realVariable.getLowerBound(), EPSILON) ;
  }

  @Test
  public void getUpperBoundTest() throws Exception {
    assertEquals("RealTest", 1.0, realVariable.getUpperBound(), EPSILON) ;
  }

  @Test
  public void setLowerBoundTest() throws Exception {
  	realVariable.setLowerBound(0.325);
    assertEquals("RealTest", 0.325, realVariable.getLowerBound(), EPSILON);
  }

  @Test
  public void setUpperBoundTest() throws Exception {
  	realVariable.setUpperBound(-254.0354);
    assertEquals("RealTest", -254.0354, realVariable.getUpperBound(), EPSILON);
  }
  
  @Test
  public void equalsTest() {
  	Real real2 = new Real(realVariable) ;
  	assertTrue(real2.equals(realVariable)) ;
  }

  @Test
  public void notEqualsTest() {
  	Real real2 = new Real(2, 2, 3) ;
  	assertFalse(real2.equals(realVariable)) ;
  	real2 = new Real(1, 5, 9) ;
  	assertFalse(real2.equals(realVariable)) ;
  }
  
  @Test
  public void toStringTest() {
    assertEquals("RealTest", "0.5", realVariable.toString());
  }
}
