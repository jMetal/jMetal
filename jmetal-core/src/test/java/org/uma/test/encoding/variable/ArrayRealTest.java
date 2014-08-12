package org.uma.test.encoding.variable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.uma.jmetal.core.Problem;
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
	ArrayReal arrayReal ;
	Problem problem ;

	@Before
	public void setUp() throws Exception {
		problem = new ZDT1("ArrayReal", 20) ;
		arrayReal = new ArrayReal(problem.getNumberOfVariables(), problem) ;
	}

	@After
	public void tearDown() throws Exception {
		problem = null;
		arrayReal = null ;
	}

	@Test 
	public void defaultConstructorTest() {
		arrayReal = new ArrayReal() ;
		
		assertEquals(0, arrayReal.length()) ;
	}
	
	@Test
	public void testCopy() throws Exception {
		ArrayReal array = new ArrayReal(arrayReal) ;
		assertTrue(Arrays.equals(array.getArray(), arrayReal.getArray())) ;
		
		array = (ArrayReal)arrayReal.copy() ;
		assertTrue(Arrays.equals(array.getArray(), arrayReal.getArray())) ;
	}

	@Test
	public void testGetLength() throws Exception {
		assertEquals(20, arrayReal.length()) ;
	}

	@Test
	public void testSetAndGetValue() throws Exception {
		arrayReal.setValue(0, 4.5) ;
		arrayReal.setValue(1, -2.3) ;
		arrayReal.setValue(2, 2.323425) ;
		arrayReal.setValue(3, 0) ;
		arrayReal.setValue(arrayReal.length()-1, -123) ;
		assertEquals(4.5, arrayReal.getValue(0), EPSILON) ;
		assertEquals(-2.3, arrayReal.getValue(1), EPSILON) ;
		assertEquals(2.323425, arrayReal.getValue(2), EPSILON) ;
		assertEquals(0, arrayReal.getValue(3), EPSILON) ;
		assertEquals(-123, arrayReal.getValue(arrayReal.length()-1), EPSILON) ;
	}

	@Test
	public void testGetLowerBound() throws Exception {
		assertEquals(problem.getLowerLimit(0), arrayReal.getLowerBound(0), EPSILON) ;
		assertEquals(problem.getLowerLimit(1), arrayReal.getLowerBound(1), EPSILON) ;
		assertEquals(problem.getLowerLimit(arrayReal.length()-1), 
				arrayReal.getLowerBound(arrayReal.length()-1), EPSILON) ;
	}

	@Test
	public void testGetUpperBound() throws Exception {
		assertEquals(problem.getUpperLimit(0), arrayReal.getUpperBound(0), EPSILON) ;
		assertEquals(problem.getUpperLimit(1), arrayReal.getUpperBound(1), EPSILON) ;
		assertEquals(problem.getUpperLimit(arrayReal.length()-1), 
				arrayReal.getUpperBound(arrayReal.length()-1), EPSILON) ;
	}
	
	@Test (expected = JMetalException.class)
	public void outOfBoundAccesingTest1() {
		double value ;
		value = arrayReal.getValue(-1) ;
		value = arrayReal.getValue(0) ;
	}

	@Test (expected = JMetalException.class)
	public void outOfBoundAccesingTest2() {
		double value ;
		value = arrayReal.getValue(arrayReal.length()) ;
	}

	@Test (expected = JMetalException.class)
	public void outOfBoundAccesingTest3() {
		double value ;
		value = arrayReal.getLowerBound(-1) ;
	}
	
	@Test (expected = JMetalException.class)
	public void outOfBoundAccesingTest4() {
		double value ;
		value = arrayReal.getLowerBound(arrayReal.length()) ;
	}

	@Test (expected = JMetalException.class)
	public void outOfBoundAccesingTest5() {
		double value ;
		value = arrayReal.getUpperBound(-1) ;
	}
	
	@Test (expected = JMetalException.class)
	public void outOfBoundAccesingTest6() {
		double value ;
		value = arrayReal.getUpperBound(arrayReal.length()) ;
	}
	
	@Test
	public void equalsTest() {
		ArrayReal array = new ArrayReal(arrayReal) ;
		assertTrue (array.equals(arrayReal)) ;
	}
	
	@Test
	public void notEqualsTest() {
		ArrayReal array = new ArrayReal(arrayReal) ;
		array.setValue(0, -13234) ;
		assertFalse(array.equals(arrayReal)) ;
	}
	
	@Test
	public void testToString() throws Exception {
		problem = new Kursawe("ArrayReal", 3) ;
		arrayReal = new ArrayReal(problem.getNumberOfVariables(), problem) ;
		arrayReal.setValue(0, 1.25) ;
		arrayReal.setValue(1, 2.51) ;
		arrayReal.setValue(2, 3.62) ;
    assertEquals("1.25 2.51 3.62", arrayReal.toString()) ;
	}
}
