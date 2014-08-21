//  ArrayIntTest.java
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
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.encoding.variable.ArrayInt;
import org.uma.jmetal.encoding.variable.ArrayReal;
import org.uma.jmetal.problem.multiobjective.Kursawe;
import org.uma.jmetal.problem.multiobjective.NMMin;
import org.uma.jmetal.util.JMetalException;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 28/06/13
 * Time: 07:50
 */
public class ArrayIntTest {
	ArrayInt arrayIntVariable ;
	Problem problem ;

	@Before
	public void setUp() throws Exception {
		problem = new NMMin("ArrayInt", 25, 100, -100, -1000, +1000) ;
		arrayIntVariable = new ArrayInt(problem.getNumberOfVariables(), problem) ;
	}

	@After
	public void tearDown() throws Exception {
		problem = null;
		arrayIntVariable = null ;
	}

	@Test 
	public void constructorTest1() {
		arrayIntVariable = new ArrayInt() ;
		
		assertEquals(0, arrayIntVariable.length()) ;
	}
	
	@Test 
	public void constructorTest2() {
		arrayIntVariable = new ArrayInt(20) ;
		
		assertEquals(20, arrayIntVariable.length()) ;
		assertEquals(20, arrayIntVariable.getArray().length) ;
	}
	
	@Test 
	public void constructorTest3() {
		arrayIntVariable = new ArrayInt(4) ;
		arrayIntVariable.setValue(0, 4) ;
		arrayIntVariable.setValue(1, -2) ;
		arrayIntVariable.setValue(2, 2) ;
		arrayIntVariable.setValue(3, 0) ;
		
		ArrayInt array2 = new ArrayInt(arrayIntVariable) ;
		
		assertEquals(4, array2.length()) ;
		assertTrue(Arrays.equals(array2.getArray(), arrayIntVariable.getArray())) ;
	}
	
	@Test
	public void copyTest() throws Exception {
		ArrayInt array = new ArrayInt(arrayIntVariable) ;
		assertTrue(Arrays.equals(array.getArray(), arrayIntVariable.getArray())) ;
		
		array = (ArrayInt)arrayIntVariable.copy() ;
		assertTrue(Arrays.equals(array.getArray(), arrayIntVariable.getArray())) ;
	}

	@Test
	public void getLengthTest() throws Exception {
		assertEquals(25, arrayIntVariable.length()) ;
	}

	@Test
	public void setAndGetValueTest() throws Exception {
		arrayIntVariable.setValue(0, 4) ;
		arrayIntVariable.setValue(1, -2) ;
		arrayIntVariable.setValue(2, 2) ;
		arrayIntVariable.setValue(3, 0) ;
		arrayIntVariable.setValue(arrayIntVariable.length()-1, -123) ;
		assertEquals(4, arrayIntVariable.getValue(0)) ;
		assertEquals(-2, arrayIntVariable.getValue(1)) ;
		assertEquals(2, arrayIntVariable.getValue(2)) ;
		assertEquals(0, arrayIntVariable.getValue(3)) ;
		assertEquals(-123, arrayIntVariable.getValue(arrayIntVariable.length()-1)) ;
	}

	@Test
	public void getLowerBoundTest() throws Exception {
		assertEquals((int)problem.getLowerLimit(0), arrayIntVariable.getLowerBound(0)) ;
		assertEquals((int)problem.getLowerLimit(1), arrayIntVariable.getLowerBound(1)) ;
		assertEquals((int)problem.getLowerLimit(arrayIntVariable.length()-1), 
				arrayIntVariable.getLowerBound(arrayIntVariable.length()-1)) ;
	}

	@Test
	public void getUpperBoundTest() throws Exception {
		assertEquals((int)problem.getUpperLimit(0), arrayIntVariable.getUpperBound(0)) ;
		assertEquals((int)problem.getUpperLimit(1), arrayIntVariable.getUpperBound(1)) ;
		assertEquals((int)problem.getUpperLimit(arrayIntVariable.length()-1), 
				arrayIntVariable.getUpperBound(arrayIntVariable.length()-1)) ;
	}
	
	@Test (expected = JMetalException.class)
	public void outOfBoundsAccesingTest1() {
		double value ;
		value = arrayIntVariable.getValue(-1) ;
	}

	@Test (expected = JMetalException.class)
	public void outOfBoundsAccesingTest2() {
		double value ;
		value = arrayIntVariable.getValue(arrayIntVariable.length()) ;
	}

	@Test (expected = JMetalException.class)
	public void outOfBoundsAccesingTest3() {
		double value ;
		value = arrayIntVariable.getLowerBound(-1) ;
	}
	
	@Test (expected = JMetalException.class)
	public void outOfBoundsAccesingTest4() {
		double value ;
		value = arrayIntVariable.getLowerBound(arrayIntVariable.length()) ;
	}

	@Test (expected = JMetalException.class)
	public void outOfBoundsAccesingTest5() {
		double value ;
		value = arrayIntVariable.getUpperBound(-1) ;
	}
	
	@Test (expected = JMetalException.class)
	public void outOfBoundsAccesingTest6() {
		double value ;
		value = arrayIntVariable.getUpperBound(arrayIntVariable.length()) ;
	}
	
	@Test (expected = JMetalException.class)
	public void outOfBoundsAccesingTest7() {
		arrayIntVariable.setValue(-1, 4) ;
 	}
	
	@Test (expected = JMetalException.class)
	public void outOfBoundsAccesingTest8() {
		arrayIntVariable.setValue(arrayIntVariable.length(), 4) ;
 	}
	
	@Test
	public void equalsTest1() {
		ArrayInt array = new ArrayInt(arrayIntVariable) ;
		assertTrue (array.equals(arrayIntVariable)) ;
	}
	
	@Test
	public void equalsTest2() {
		assertTrue (arrayIntVariable.equals(arrayIntVariable)) ;
	}
	
	@Test
	public void notEqualsTest1() {
		assertFalse(arrayIntVariable.equals(null)) ;
	}
	
	@Test
	public void notEqualsTest2() {
		ArrayInt array = new ArrayInt(arrayIntVariable) ;
		array.setValue(0, -13234) ;
		assertFalse(array.equals(arrayIntVariable)) ;
	}
	
	@Test
	public void notEqualsTest3() {
		assertFalse(arrayIntVariable.equals(new ArrayReal())) ;
	}
	
	@Test
	public void hashCodeTest() {
		ArrayInt array = new ArrayInt(arrayIntVariable) ;
		assertEquals(array.hashCode(), arrayIntVariable.hashCode()) ;
		array.setValue(0, -13234) ;
		assertFalse(array.hashCode() == arrayIntVariable.hashCode()) ;
	}
	
	@Test
	public void testToString() throws Exception {
		problem = new Kursawe("ArrayReal", 3) ;
		arrayIntVariable = new ArrayInt(problem.getNumberOfVariables(), problem) ;
		arrayIntVariable.setValue(0, 1) ;
		arrayIntVariable.setValue(1, 2) ;
		arrayIntVariable.setValue(2, 3) ;
    assertEquals("1 2 3", arrayIntVariable.toString()) ;
	}
}
