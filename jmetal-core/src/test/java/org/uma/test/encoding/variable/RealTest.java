//  RealTest.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.test.encoding.variable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.encoding.variable.Permutation;
import org.uma.jmetal.encoding.variable.Real;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 29/06/13
 * Time: 17:13
 */
public class RealTest {
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
    assertEquals(0.5, realVariable.getValue(), EPSILON) ;
  }

  @Test
  public void setValueTest() throws Exception {
     double oldValue = realVariable.getValue() ;
     realVariable.setValue(0.364);
     assertEquals(0.364, realVariable.getValue(), EPSILON);
  }

  @Test
  public void copyTest() throws Exception {
    Real real = (Real)realVariable.copy() ;
    assertEquals(real.toString(), real.toString());
  }

  @Test
  public void getLowerBoundTest() throws Exception {
    assertEquals(-1.0, realVariable.getLowerBound(), EPSILON) ;
  }

  @Test
  public void getUpperBoundTest() throws Exception {
    assertEquals(1.0, realVariable.getUpperBound(), EPSILON) ;
  }

  @Test
  public void setLowerBoundTest() throws Exception {
  	realVariable.setLowerBound(0.325);
    assertEquals(0.325, realVariable.getLowerBound(), EPSILON);
  }

  @Test
  public void setUpperBoundTest() throws Exception {
  	realVariable.setUpperBound(-254.0354);
    assertEquals(-254.0354, realVariable.getUpperBound(), EPSILON);
  }
  
  @Test
  public void equalsTest() {
  	Real real2 = new Real(realVariable) ;
  	assertTrue(real2.equals(realVariable)) ;
  }

  @Test
  public void notEqualsTest1() {
    Real real2 = new Real(2, 2, 3) ;
    assertFalse(real2.equals(realVariable)) ;
    real2 = new Real(1, 5, 9) ;
    assertFalse(real2.equals(realVariable)) ;
  }

  @Test
  public void comparisonToNullTest() {
    assertFalse(realVariable.equals(null)) ;
  }

  @Test
  public void comparisonWithIncompatibleClassTest() {
    assertFalse(realVariable.equals(new Permutation(25))) ;
  }

  @Test
  public void comparisonWithDifferentLowerBoundTest() {
    Real real2 = new Real(realVariable) ;
    real2.setLowerBound(-123456);
    assertFalse(realVariable.equals(real2)) ;
  }

  @Test
  public void comparisonWithDifferentUpperBoundTest() {
    Real real2 = new Real(realVariable) ;
    real2.setUpperBound(+123456);
    assertFalse(realVariable.equals(real2)) ;
  }

  @Test
  public void hashCodeTest() {
    Real real2 = new Real(realVariable) ;
    assertEquals(realVariable.hashCode(), real2.hashCode()) ;
    real2 = new Real(1, 5, 9) ;
    assertNotEquals(realVariable.hashCode(), real2.hashCode()) ;
  }

  @Test
  public void toStringTest() {
    assertEquals("0.5", realVariable.toString());
  }
}
