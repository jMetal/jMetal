//  ArrayRealTest.java
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.encoding.variable.ArrayInt;
import org.uma.jmetal.encoding.variable.ArrayReal;
import org.uma.jmetal.problem.Fonseca;
import org.uma.jmetal.problem.Kursawe;
import org.uma.jmetal.problem.Schaffer;
import org.uma.jmetal.problem.zdt.ZDT1;
import org.uma.jmetal.util.JMetalException;
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
	ArrayReal arrayRealVariable ;
	Problem problem ;

	@Before
	public void setUp() throws Exception {
		problem = new ZDT1("ArrayReal", 20) ;
		arrayRealVariable = new ArrayReal(problem.getNumberOfVariables(), problem) ;
	}

	@After
	public void tearDown() throws Exception {
		problem = null;
		arrayRealVariable = null ;
	}

	@Test 
	public void constructorTest1() {
		arrayRealVariable = new ArrayReal() ;

		assertEquals(0, arrayRealVariable.length()) ;
	}

	@Test 
	public void constructorTest2() {
		arrayRealVariable = new ArrayReal(20) ;

		assertEquals(20, arrayRealVariable.length()) ;
		assertEquals(20, arrayRealVariable.getArray().length) ;
	}

	@Test 
	public void constructorTest3() {
		arrayRealVariable = new ArrayReal(4) ;
		arrayRealVariable.setValue(0, 4) ;
		arrayRealVariable.setValue(1, -2) ;
		arrayRealVariable.setValue(2, 2) ;
		arrayRealVariable.setValue(3, 0) ;

		ArrayReal array2 = new ArrayReal(arrayRealVariable) ;

		assertEquals(4, array2.length()) ;
		assertTrue(Arrays.equals(array2.getArray(), arrayRealVariable.getArray())) ;
	}

	@Test
	public void testCopy() throws Exception {
		ArrayReal array = new ArrayReal(arrayRealVariable) ;
		assertTrue(Arrays.equals(array.getArray(), arrayRealVariable.getArray())) ;

		array = (ArrayReal)arrayRealVariable.copy() ;
		assertTrue(Arrays.equals(array.getArray(), arrayRealVariable.getArray())) ;
	}

	@Test
	public void testGetLength() throws Exception {
		assertEquals(20, arrayRealVariable.length()) ;
	}

	@Test
	public void testSetAndGetValue() throws Exception {
		arrayRealVariable.setValue(0, 4.5) ;
		arrayRealVariable.setValue(1, -2.3) ;
		arrayRealVariable.setValue(2, 2.323425) ;
		arrayRealVariable.setValue(3, 0) ;
		arrayRealVariable.setValue(arrayRealVariable.length()-1, -123) ;
		assertEquals(4.5, arrayRealVariable.getValue(0), EPSILON) ;
		assertEquals(-2.3, arrayRealVariable.getValue(1), EPSILON) ;
		assertEquals(2.323425, arrayRealVariable.getValue(2), EPSILON) ;
		assertEquals(0, arrayRealVariable.getValue(3), EPSILON) ;
		assertEquals(-123, arrayRealVariable.getValue(arrayRealVariable.length()-1), EPSILON) ;
	}

	@Test
	public void testGetLowerBound() throws Exception {
		assertEquals(problem.getLowerLimit(0), arrayRealVariable.getLowerBound(0), EPSILON) ;
		assertEquals(problem.getLowerLimit(1), arrayRealVariable.getLowerBound(1), EPSILON) ;
		assertEquals(problem.getLowerLimit(arrayRealVariable.length()-1), 
				arrayRealVariable.getLowerBound(arrayRealVariable.length()-1), EPSILON) ;
	}

	@Test
	public void testGetUpperBound() throws Exception {
		assertEquals(problem.getUpperLimit(0), arrayRealVariable.getUpperBound(0), EPSILON) ;
		assertEquals(problem.getUpperLimit(1), arrayRealVariable.getUpperBound(1), EPSILON) ;
		assertEquals(problem.getUpperLimit(arrayRealVariable.length()-1), 
				arrayRealVariable.getUpperBound(arrayRealVariable.length()-1), EPSILON) ;
	}

	@Test (expected = JMetalException.class)
	public void outOfBoundAccesingTest1() {
		double value ;
		value = arrayRealVariable.getValue(-1) ;
		value = arrayRealVariable.getValue(0) ;
	}

	@Test (expected = JMetalException.class)
	public void outOfBoundAccesingTest2() {
		double value ;
		value = arrayRealVariable.getValue(arrayRealVariable.length()) ;
	}

	@Test (expected = JMetalException.class)
	public void outOfBoundAccesingTest3() {
		double value ;
		value = arrayRealVariable.getLowerBound(-1) ;
	}

	@Test (expected = JMetalException.class)
	public void outOfBoundAccesingTest4() {
		double value ;
		value = arrayRealVariable.getLowerBound(arrayRealVariable.length()) ;
	}

	@Test (expected = JMetalException.class)
	public void outOfBoundAccesingTest5() {
		double value ;
		value = arrayRealVariable.getUpperBound(-1) ;
	}

	@Test (expected = JMetalException.class)
	public void outOfBoundAccesingTest6() {
		double value ;
		value = arrayRealVariable.getUpperBound(arrayRealVariable.length()) ;
	}

	@Test
	public void equalsTest1() {
		ArrayReal array = new ArrayReal(arrayRealVariable) ;
		assertTrue (array.equals(arrayRealVariable)) ;
	}

	@Test
	public void equalsTest2() {
		assertTrue (arrayRealVariable.equals(arrayRealVariable)) ;
	}

	@Test
	public void notEqualsTest1() {
		assertFalse(arrayRealVariable.equals(null)) ;
	}

	@Test
	public void notEqualsTest2() {
		ArrayReal array = new ArrayReal(arrayRealVariable) ;
		array.setValue(0, -13234.234) ;
		assertFalse(array.equals(arrayRealVariable)) ;
	}

	@Test
	public void notEqualsTest3() {
		assertFalse(arrayRealVariable.equals(new ArrayReal())) ;
	}

	@Test
	public void hashCodeTest() {
		ArrayReal array = new ArrayReal(arrayRealVariable) ;
		assertEquals(array.hashCode(), arrayRealVariable.hashCode()) ;
		array.setValue(0, -13234.234) ;
		assertFalse(array.hashCode() == arrayRealVariable.hashCode()) ;
	}

	@Test
	public void testToString() throws Exception {
		problem = new Kursawe("ArrayReal", 3) ;
		arrayRealVariable = new ArrayReal(problem.getNumberOfVariables(), problem) ;
		arrayRealVariable.setValue(0, 1.25) ;
		arrayRealVariable.setValue(1, 2.51) ;
		arrayRealVariable.setValue(2, 3.62) ;
		assertEquals("1.25 2.51 3.62", arrayRealVariable.toString()) ;
	}
}
