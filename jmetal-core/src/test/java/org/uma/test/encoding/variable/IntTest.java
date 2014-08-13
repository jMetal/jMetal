//  IntTest.java
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

import org.uma.jmetal.encoding.variable.Int;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.encoding.variable.Permutation;
import org.uma.jmetal.encoding.variable.Real;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 29/06/13
 * Time: 17:13
 */
public class IntTest {
  Int integerVariable ;

  @Before
  public void setUp() throws  Exception {
    integerVariable  = new Int(1, 2, 3) ;
  }

  @After
  public void tearDown() throws Exception {
    integerVariable = null ;
  }

  @Test
  public void getValueTest() throws Exception {
    assertEquals(1, (int)integerVariable.getValue()) ;
  }

  @Test
  public void setValueTest() throws Exception {
     integerVariable.setValue(-235);
     assertEquals(-235, (int)integerVariable.getValue());
  }

  @Test
  public void copyTest() throws Exception {
    Int number = (Int)integerVariable.copy() ;
    assertEquals(number.toString(), integerVariable.toString());
  }

  @Test
  public void getLowerBoundTest() throws Exception {
    assertEquals(2, integerVariable.getLowerBound()) ;
  }

  @Test
  public void getUpperBoundTest() throws Exception {
    assertEquals(3, integerVariable.getUpperBound()) ;
  }

  @Test
  public void setLowerBoundTest() throws Exception {
    integerVariable.setLowerBound(4);
    assertEquals(4, (int)integerVariable.getLowerBound());
  }

  @Test
  public void setUpperBoundTest() throws Exception {
    integerVariable.setUpperBound(25);
    assertEquals(25, (int)integerVariable.getUpperBound());
  }
  
  @Test
  public void equalsTest() {
  	Int int2 = new Int(integerVariable) ;
  	assertTrue(int2.equals(integerVariable)) ;
  }
  
  @Test
  public void notEqualsTest() {
  	Int int2 = new Int(2, 2, 3) ;
  	assertFalse(int2.equals(integerVariable)) ;
  	int2 = new Int(1, 5, 9) ;
  	assertFalse(int2.equals(integerVariable)) ;
  }

  @Test
  public void comparisonToNullTest() {
    assertFalse(integerVariable.equals(null)) ;
  }

  @Test
  public void comparisonWithIncompatibleClassTest() {
    assertFalse(integerVariable.equals(new Permutation(25))) ;
  }

  @Test
  public void comparisonWithDifferentLowerBoundTest() {
    Int int2 = new Int(integerVariable) ;
    int2.setLowerBound(-123456);
    assertFalse(integerVariable.equals(int2)) ;
  }

  @Test
  public void comparisonWithDifferentUpperBoundTest() {
    Int int2 = new Int(integerVariable) ;
    int2.setUpperBound(+123456);
    assertFalse(integerVariable.equals(int2)) ;
  }

  @Test
  public void hashCodeTest() {
    Int real2 = new Int(integerVariable) ;
    assertEquals(integerVariable.hashCode(), real2.hashCode()) ;
    real2 = new Int(1, 5, 9) ;
    assertNotEquals(integerVariable.hashCode(), real2.hashCode()) ;
  }

  @Test
  public void toStringTest() {
    assertEquals("1", integerVariable.toString());
  }
}
