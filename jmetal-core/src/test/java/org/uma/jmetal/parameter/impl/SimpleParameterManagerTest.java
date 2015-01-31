package org.uma.jmetal.parameter.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;
import org.mockito.asm.tree.analysis.Value;
import org.uma.jmetal.parameter.Parameter;

public class SimpleParameterManagerTest {

	@Test
	public void testStartEmpty() {
		SimpleParameterManager manager = new SimpleParameterManager();
		assertTrue(manager.getParameters().isEmpty());
	}

	@Test
	public void testAddGetParameters() {
		SimpleParameterManager manager = new SimpleParameterManager();
		SimpleParameter<?> parameter1 = new SimpleParameter<>();
		SimpleParameter<?> parameter2 = new SimpleParameter<>();
		SimpleParameter<?> parameter3 = new SimpleParameter<>();

		manager.addParameter(parameter1);
		assertEquals(1, manager.getParameters().size());
		assertTrue(manager.getParameters().contains(parameter1));
		assertFalse(manager.getParameters().contains(parameter2));
		assertFalse(manager.getParameters().contains(parameter3));

		manager.addParameter(parameter2);
		assertEquals(2, manager.getParameters().size());
		assertTrue(manager.getParameters().contains(parameter1));
		assertTrue(manager.getParameters().contains(parameter2));
		assertFalse(manager.getParameters().contains(parameter3));

		manager.addParameter(parameter3);
		assertEquals(3, manager.getParameters().size());
		assertTrue(manager.getParameters().contains(parameter1));
		assertTrue(manager.getParameters().contains(parameter2));
		assertTrue(manager.getParameters().contains(parameter3));
	}

	@Test
	public void testRemoveParameter() {
		SimpleParameterManager manager = new SimpleParameterManager();
		SimpleParameter<?> parameter1 = new SimpleParameter<>();
		SimpleParameter<?> parameter2 = new SimpleParameter<>();
		SimpleParameter<?> parameter3 = new SimpleParameter<>();

		manager.addParameter(parameter1);
		manager.addParameter(parameter2);
		manager.addParameter(parameter3);
		assertEquals(3, manager.getParameters().size());
		assertTrue(manager.getParameters().contains(parameter1));
		assertTrue(manager.getParameters().contains(parameter2));
		assertTrue(manager.getParameters().contains(parameter3));

		manager.removeParameter(parameter1);
		assertEquals(2, manager.getParameters().size());
		assertFalse(manager.getParameters().contains(parameter1));
		assertTrue(manager.getParameters().contains(parameter2));
		assertTrue(manager.getParameters().contains(parameter3));

		manager.removeParameter(parameter2);
		assertEquals(1, manager.getParameters().size());
		assertFalse(manager.getParameters().contains(parameter1));
		assertFalse(manager.getParameters().contains(parameter2));
		assertTrue(manager.getParameters().contains(parameter3));

		manager.removeParameter(parameter3);
		assertEquals(0, manager.getParameters().size());
		assertFalse(manager.getParameters().contains(parameter1));
		assertFalse(manager.getParameters().contains(parameter2));
		assertFalse(manager.getParameters().contains(parameter3));
	}

	@Test
	public void testAddMultipleParameters() {
		SimpleParameter<?> parameter1 = new SimpleParameter<>();
		SimpleParameter<?> parameter2 = new SimpleParameter<>();
		SimpleParameter<?> parameter3 = new SimpleParameter<>();

		SimpleParameterManager manager = new SimpleParameterManager();
		manager.addAllParameters(Arrays.asList(parameter1, parameter2,
				parameter3));
		assertEquals(3, manager.getParameters().size());
		assertTrue(manager.getParameters().contains(parameter1));
		assertTrue(manager.getParameters().contains(parameter2));
		assertTrue(manager.getParameters().contains(parameter3));
	}

	@Test
	public void testRemoveMultipleParameters() {
		SimpleParameter<?> parameter1 = new SimpleParameter<>();
		SimpleParameter<?> parameter2 = new SimpleParameter<>();
		SimpleParameter<?> parameter3 = new SimpleParameter<>();

		SimpleParameterManager manager = new SimpleParameterManager();
		manager.addAllParameters(Arrays.asList(parameter1, parameter2,
				parameter3));
		assertEquals(3, manager.getParameters().size());
		assertTrue(manager.getParameters().contains(parameter1));
		assertTrue(manager.getParameters().contains(parameter2));
		assertTrue(manager.getParameters().contains(parameter3));

		manager.removeAllParameters(Arrays.asList(parameter1, parameter3));
		assertEquals(1, manager.getParameters().size());
		assertFalse(manager.getParameters().contains(parameter1));
		assertTrue(manager.getParameters().contains(parameter2));
		assertFalse(manager.getParameters().contains(parameter3));

		manager.removeAllParameters(Arrays.asList(parameter1, parameter2));
		assertEquals(0, manager.getParameters().size());
	}

	@Test
	public void testIteratorRecall() {
		SimpleParameter<Value> parameter1 = new SimpleParameter<Value>();
		SimpleParameter<Value> parameter2 = new SimpleParameter<Value>();
		SimpleParameter<Value> parameter3 = new SimpleParameter<Value>();
		SimpleParameter<Value> parameter4 = new SimpleParameter<Value>();

		SimpleParameterManager manager = new SimpleParameterManager();
		manager.addParameter(parameter1);
		manager.addParameter(parameter2);
		manager.addParameter(parameter3);
		manager.addParameter(parameter4);
		manager.removeParameter(parameter3);

		Collection<Parameter<?>> remainingParameters = new LinkedList<>();
		remainingParameters.add(parameter1);
		remainingParameters.add(parameter2);
		remainingParameters.add(parameter4);
		for (Parameter<?> parameter : manager) {
			remainingParameters.remove(parameter);
		}
		assertTrue(remainingParameters.toString(),
				remainingParameters.isEmpty());
	}

	@Test
	public void testIteratorPrecision() {
		SimpleParameter<Value> parameter1 = new SimpleParameter<Value>();
		SimpleParameter<Value> parameter2 = new SimpleParameter<Value>();
		SimpleParameter<Value> parameter3 = new SimpleParameter<Value>();
		SimpleParameter<Value> parameter4 = new SimpleParameter<Value>();

		SimpleParameterManager manager = new SimpleParameterManager();
		manager.addParameter(parameter1);
		manager.addParameter(parameter2);
		manager.addParameter(parameter3);
		manager.addParameter(parameter4);
		manager.removeParameter(parameter3);

		Collection<Parameter<?>> expectedParameters = new LinkedList<>();
		expectedParameters.add(parameter1);
		expectedParameters.add(parameter2);
		expectedParameters.add(parameter4);
		for (Parameter<?> parameter : manager) {
			assertTrue("" + parameter, expectedParameters.contains(parameter));
		}
	}

}
