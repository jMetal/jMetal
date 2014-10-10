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
import org.uma.jmetal.encoding.variable.Binary;

import static org.junit.Assert.assertTrue;

/**
 * Created by Antonio J. Nebro on 12/08/14.
 */
public class BinaryTest {
  Binary binaryVariable ;

  @Before
  public void setUp() throws  Exception {
    binaryVariable = new Binary(10) ;
  }

  @Test
  public void equalsTest() {
    Binary bin = new Binary(binaryVariable) ;
    assertTrue(bin.equals(binaryVariable)) ;
  }

  @Test
  public void copyTest() {
    Binary bin = (Binary)binaryVariable.copy() ;
    assertTrue(bin.equals(binaryVariable)) ;
  }

  @After
  public void tearDown() throws Exception {
    binaryVariable = null ;
  }

}
