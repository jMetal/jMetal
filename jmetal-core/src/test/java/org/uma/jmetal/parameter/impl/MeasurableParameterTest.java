package org.uma.jmetal.parameter.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.parameter.Parameter;

/**
 * Because the {@link MeasurableParameter} implements {@link Parameter},
 * {@link MeasurableParameter} and {@link MeasurableParameter} interfaces, we
 * extend {@link ParameterTest} to execute basic {@link Parameter} tests and add
 * manually the tests of {@link PullMeasureTest} and {@link PushMeasureTest} to
 * execute them too. If other tests are added to {@link ParameterTest}, they
 * will be automatically added here, but for {@link PullMeasureTest} and
 * {@link PushMeasureTest} they have to be added manually.
 */
public class MeasurableParameterTest {

	@Test
	public void testNotifiedWhenRegistered() {
		final Integer[] lastReceived = { null };
		MeasureListener<Integer> listener = new MeasureListener<Integer>() {

			@Override
			public void measureGenerated(Integer value) {
				lastReceived[0] = value;
			}
		};
		MeasurableParameter<Integer> parameter = new MeasurableParameter<>();
		parameter.register(listener);

		parameter.set(3);
		assertEquals(3, (Object) lastReceived[0]);
		parameter.set(null);
		assertEquals(null, (Object) lastReceived[0]);
		parameter.set(5);
		assertEquals(5, (Object) lastReceived[0]);
	}

	@Test
	public void testNotNotifiedWhenUnregistered() {
		final Integer[] lastReceived = { null };
		MeasureListener<Integer> listener = new MeasureListener<Integer>() {

			@Override
			public void measureGenerated(Integer value) {
				lastReceived[0] = value;
			}
		};
		MeasurableParameter<Integer> parameter = new MeasurableParameter<>();
		parameter.register(listener);
		parameter.unregister(listener);

		parameter.set(3);
		assertEquals(null, (Object) lastReceived[0]);
		parameter.set(-45);
		assertEquals(null, (Object) lastReceived[0]);
	}

	@Test
	public void testSetGetAligned() {
		MeasurableParameter<Integer> parameter = new MeasurableParameter<>();

		parameter.set(3);
		assertEquals(3, (Object) parameter.get());
		parameter.set(null);
		assertEquals(null, (Object) parameter.get());
		parameter.set(5);
		assertEquals(5, (Object) parameter.get());
	}

	@Test
	public void testWrappedParameterValueAlignment() {
		Parameter<Integer> original = new SimpleParameter<>();
		MeasurableParameter<Integer> wrapper = new MeasurableParameter<>(
				original);

		wrapper.set(3);
		assertEquals(3, (Object) original.get());
		original.set(5);
		assertEquals(5, (Object) wrapper.get());
	}
}
