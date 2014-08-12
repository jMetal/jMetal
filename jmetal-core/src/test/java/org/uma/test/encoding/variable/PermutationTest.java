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

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.encoding.variable.ArrayInt;
import org.uma.jmetal.encoding.variable.Int;
import org.uma.jmetal.encoding.variable.Permutation;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

/**
 * Created by Antonio J. Nebro on 12/08/14.
 */
public class PermutationTest {
  private Permutation permutationVariable ;

  @Before
  public void setUp() throws  Exception {
    permutationVariable  = new Permutation(10) ;
  }

  @Test
  public void getLengthTest() {
    assertEquals(10, permutationVariable.getLength()) ;
    assertEquals(0, new Permutation().getLength()) ;
  }

  @Test
  public void constructorTest1() {
    int[] intArray = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9} ;

    Arrays.sort(permutationVariable.getVector()) ;
    assertArrayEquals(intArray, permutationVariable.getVector());
  }

  @Test
  public void constructorTest2() {
    Permutation permutation = new Permutation(permutationVariable) ;
    assertTrue(permutationVariable.equals(permutation));
  }

  @Test
  public void hashCodeTest() {
    Permutation permutation = new Permutation(permutationVariable) ;
    assertEquals(permutationVariable.hashCode(), permutation.hashCode());
    permutation = new Permutation(10) ;
    assertNotEquals(permutationVariable.hashCode(), permutation.hashCode());
  }

  @Test
  public void copyTest() {
    Permutation permutation = (Permutation)permutationVariable.copy() ;
    assertTrue(permutationVariable.equals(permutation));
    assertEquals(permutationVariable.hashCode(), permutation.hashCode());
  }

  @Test
  public void comparisonToNullTest() {
    assertFalse(permutationVariable.equals(null)) ;
  }

  @Test
  public void comparisonWithIncompatibleClassTest() {
    assertFalse(permutationVariable.equals(new ArrayInt(25))) ;
  }

  @Test
  public void comparisonWithItselfTest() {
    assertTrue(permutationVariable.equals(permutationVariable)) ;
  }

  @Test
  public void comparisonWithOtherVariableTest() {
    Permutation permutation = new Permutation(10) ;
    assertFalse(permutationVariable.equals(permutation)); ;
  }

  @After
  public void tearDown() throws Exception {
    permutationVariable = null ;
  }
}
