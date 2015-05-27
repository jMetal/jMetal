package org.uma.jmetal.parameter.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimpleParameterTest {

	@Test
	public void testSetGetAligned() {
		SimpleParameter<Integer> parameter = new SimpleParameter<>();

		parameter.set(3);
		assertEquals(3, (Object) parameter.get());
		parameter.set(null);
		assertEquals(null, (Object) parameter.get());
		parameter.set(5);
		assertEquals(5, (Object) parameter.get());
	}
}
