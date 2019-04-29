package org.uma.jmetal.util;

import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.junit.Assert.assertEquals;

/**
 * Test Class responsible for testing {@link NormalizeUtils}
 * 
 * @author Thiago Ferreira
 * @version 1.0.0
 * @since 2018-12-16
 */
public class NormalizeUtilsTest {
	
	private static final double EPSILON = 0.0000000001 ;
	
	@Test(expected = ReflectiveOperationException.class)
	public void shouldThrowExceptionWhenInitiateThisClass() throws Exception {

		final Class<?> cls = NormalizeUtils.class;

		final Constructor<?> c = cls.getDeclaredConstructors()[0];

		c.setAccessible(true);

		c.newInstance((Object[]) null);
	}
	
	@Test(expected = JMetalException.class)
	public void shouldThrowAnExceptionWhenMinAndMaxValuesAreTheSame() throws Exception {
		NormalizeUtils.normalize(2, 10, 10);
	}
	
	@Test
	public void shouldReturnTheCorrectNumberWhenRangeIsZeroAndOne() throws Exception {
		assertEquals(0.0, NormalizeUtils.normalize(10, 10, 20), EPSILON);
		assertEquals(0.5, NormalizeUtils.normalize(15, 10, 20), EPSILON);
		assertEquals(1.0, NormalizeUtils.normalize(20, 10, 20), EPSILON);
	}
	
	@Test
	public void shouldReturnTheCorrectNumberWhenRangeIsMinusOneAndPlusOne() throws Exception {
		assertEquals(-1.0, NormalizeUtils.normalize(10, -1, 1, 10, 20), EPSILON);
		assertEquals(0.0, NormalizeUtils.normalize(15, -1, 1, 10, 20), EPSILON);
		assertEquals(1.0, NormalizeUtils.normalize(20, -1, 1, 10, 20), EPSILON);
	}
}