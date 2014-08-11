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

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 29/06/13
 * Time: 17:13
 */
public class IntTest extends Int {
  Int integer ;

  @Before
  public void setUp() throws  Exception {
    integer  = new Int(1, 2, 3) ;
  }

  @After
  public void tearDown() throws Exception {
    integer = null ;
  }

  @Test
  public void getValueTest() throws Exception {
    assertEquals(1, (int)integer.getValue()) ;
  }

  @Test
  public void setValueTest() throws Exception {
     integer.setValue(-235);
     assertEquals(-235, (int)integer.getValue());
  }

  @Test
  public void copyTest() throws Exception {
    Int number = (Int)integer.copy() ;
    assertEquals(number.toString(), integer.toString());
  }

  @Test
  public void getLowerBoundTest() throws Exception {
    assertEquals(2, (int)integer.getLowerBound()) ;
  }

  @Test
  public void getUpperBoundTest() throws Exception {
    assertEquals(3, (int)integer.getUpperBound()) ;
  }

  @Test
  public void setLowerBoundTest() throws Exception {
    integer.setLowerBound(4);
    assertEquals(4, (int)integer.getLowerBound());
  }

  @Test
  public void setUpperBoundTest() throws Exception {
    integer.setUpperBound(25);
    assertEquals(25, (int)integer.getUpperBound());
  }
  
  @Test
  public void equalsTest() {
  	Int int2 = new Int(integer) ;
  	assertTrue(int2.equals(integer)) ;
  }
  
  @Test
  public void notEqualsTest() {
  	Int int2 = new Int(2, 2, 3) ;
  	assertFalse(int2.equals(integer)) ;
  	int2 = new Int(1, 5, 9) ;
  	assertFalse(int2.equals(integer)) ;
  }

  @Test
  public void toStringTest() {
    assertEquals("1", integer.toString());
  }
}
