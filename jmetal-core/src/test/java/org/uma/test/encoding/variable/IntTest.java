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

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 29/06/13
 * Time: 17:13
 */
public class IntTest extends Int {
  Int integer_ ;

  @Before
  public void setUp() throws  Exception {
    integer_  = new Int(1, 2, 3) ;
  }

  @After
  public void tearDown() throws Exception {
    integer_ = null ;
  }

  @Test
  public void testGetValue() throws Exception {
    assertEquals("IntTest", 1, (int)integer_.getValue()) ;
  }

  @Test
  public void testSetValue() throws Exception {
     integer_.setValue(-235);
     assertEquals("IntTest", -235, (int)integer_.getValue());
  }

  @Test
  public void testDeepCopy() throws Exception {
    Int integer = (Int)integer_.deepCopy() ;
    assertEquals("IntTest", integer.toString(), integer_.toString());
  }

  @Test
  public void testGetLowerBound() throws Exception {
    assertEquals("IntTest", 2, (int)integer_.getLowerBound()) ;
  }

  @Test
  public void testGetUpperBound() throws Exception {
    assertEquals("IntTest", 3, (int)integer_.getUpperBound()) ;
  }

  @Test
  public void testSetLowerBound() throws Exception {
    integer_.setLowerBound(4);
    assertEquals("IntTest", 4, (int)integer_.getLowerBound());
  }

  @Test
  public void testSetUpperBound() throws Exception {
    integer_.setUpperBound(25);
    assertEquals("IntTest", 25, (int)integer_.getUpperBound());
  }

  @Test
  public void testToString() {
    assertEquals("IntTest", "1", integer_.toString());
  }
}
