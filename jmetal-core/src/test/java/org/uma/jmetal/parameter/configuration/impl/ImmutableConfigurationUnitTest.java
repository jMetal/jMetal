package org.uma.jmetal.parameter.configuration.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.uma.jmetal.parameter.Parameter;
import org.uma.jmetal.parameter.impl.SimpleParameter;

public class ImmutableConfigurationUnitTest {

	@Test
	public void testGetParameter() {
		Parameter<Integer> parameter = new SimpleParameter<>("");
		Integer value = 3;
		ImmutableConfigurationUnit<Integer> unit = new ImmutableConfigurationUnit<>(
				parameter, value);
		assertEquals(parameter, unit.getParameter());
	}

	@Test
	public void testGetValue() {
		Parameter<Integer> parameter = new SimpleParameter<>("");
		Integer value = 3;
		ImmutableConfigurationUnit<Integer> unit = new ImmutableConfigurationUnit<>(
				parameter, value);
		assertEquals(value, unit.getValue());
	}

	@Test
	public void testIsApplied() {
		Parameter<Integer> parameter = new SimpleParameter<>("");
		Integer value = 3;
		ImmutableConfigurationUnit<Integer> unit = new ImmutableConfigurationUnit<>(
				parameter, value);

		parameter.set(5);
		assertFalse(unit.isApplied());

		parameter.set(3);
		assertTrue(unit.isApplied());

		parameter.set(null);
		assertFalse(unit.isApplied());
	}

	@Test
	public void testApply() {
		Parameter<Integer> parameter = new SimpleParameter<>("");
		Integer value = 3;
		ImmutableConfigurationUnit<Integer> unit = new ImmutableConfigurationUnit<>(
				parameter, value);

		parameter.set(5);
		unit.apply();
		assertEquals(value, unit.getValue());
		assertTrue(unit.isApplied());

		parameter.set(3);
		unit.apply();
		assertEquals(value, unit.getValue());
		assertTrue(unit.isApplied());

		parameter.set(null);
		unit.apply();
		assertEquals(value, unit.getValue());
		assertTrue(unit.isApplied());
	}

}
