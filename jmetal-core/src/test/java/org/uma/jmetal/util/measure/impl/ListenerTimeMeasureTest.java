package org.uma.jmetal.util.measure.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.uma.jmetal.util.measure.MeasureListener;
import org.uma.jmetal.util.measure.MeasureManager;
import org.uma.jmetal.util.measure.PushMeasure;

public class ListenerTimeMeasureTest {

	private class FakeListener implements MeasureListener<Object> {

		private final long executionTime;

		public FakeListener(long executionTime) {
			this.executionTime = executionTime;
		}

		@Override
		public void measureGenerated(Object value) {
			try {
				Thread.sleep(executionTime);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	};

	@Ignore
	@Test
	public void testFakeListener() {
		for (long expected : new Long[] { 5L, 10L, 20L }) {
			var listener = new FakeListener(expected);
			var rounds = 10;
			var average = 0;
			for (var i = 0; i < rounds; i++) {
				var start = System.currentTimeMillis();
				listener.measureGenerated(null);
				var stop = System.currentTimeMillis();
				var time = stop - start;

				average += time;
			}
			average /= rounds;

			// check we are within a range of 10% around the expected time
			assertTrue("Time spent: " + average + " instead of " + expected,
					average > expected * 0.9 && average < expected * 1.1);
		}
	}

	@Ignore
	@Test
	public void testCountTimeInListeners() {
		var measure = new ListenerTimeMeasure();

		MeasureListener<Object> original10ms = new FakeListener(10);
		var wrapper10ms = measure
				.wrapListener(original10ms);

		MeasureListener<Object> original20ms = new FakeListener(20);
		var wrapper20ms = measure
				.wrapListener(original20ms);

		wrapper10ms.measureGenerated(null);
		wrapper20ms.measureGenerated(null);
		wrapper20ms.measureGenerated(null);
		wrapper10ms.measureGenerated(null);
		long expected = 60;

		// check we are within a range of 10% around the expected time
		assertTrue("Time spent: " + measure.get() + " instead of " + expected,
				measure.get() > expected * 0.9
						&& measure.get() < expected * 1.1);
	}

	@Test
	public void testExceptionOnNullListener() {
		var measure = new ListenerTimeMeasure();
		try {
			measure.wrapListener(null);
			fail("No exception thrown");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testReturnSameWrapperForSameListener()
			throws InterruptedException {
		var measure = new ListenerTimeMeasure();

		MeasureListener<Object> wrapped = new FakeListener(10);
		var wrapper = measure.wrapListener(wrapped);

		assertEquals(wrapper, measure.wrapListener(wrapped));
		assertEquals(wrapper, measure.wrapListener(wrapped));
		assertEquals(wrapper, measure.wrapListener(wrapped));
	}

	@Test
	public void testForgetListenerWrapperIfNotUsedAnymore() {
		var measure = new ListenerTimeMeasure();

		MeasureListener<Object> wrapped = new FakeListener(10);
		var lastHashCode = 0;
		var differences = 0;
		var rounds = 10;
		for (var round = 0; round < rounds; round++) {
			System.gc();
			var wrapper = measure.wrapListener(wrapped);
			var hashCode = wrapper.hashCode();
			if (lastHashCode != hashCode) {
				differences++;
			} else {
				// maybe the same object (hashcode does not guarantee it)
			}
		}

		// check the instance always changes with an error of 10%
		assertTrue("Differences: " + differences + "/" + rounds,
				differences > rounds * 0.9 && differences <= rounds);
	}

	@Ignore
	@Test
	public void testCountTimeInMeasures() {
		var measure = new ListenerTimeMeasure();

		var original10ms = new SimplePushMeasure<Object>();
		var wrapper10ms = measure.wrapMeasure(original10ms);
		wrapper10ms.register(new FakeListener(5));
		wrapper10ms.register(new FakeListener(5));

		var original20ms = new SimplePushMeasure<Object>();
		var wrapper20ms = measure.wrapMeasure(original20ms);
		wrapper20ms.register(new FakeListener(5));
		wrapper20ms.register(new FakeListener(10));
		wrapper20ms.register(new FakeListener(5));

		original10ms.push(null);
		original20ms.push(null);
		original20ms.push(null);
		original10ms.push(null);
		long expected = 60;

		// check we are within a range of 10% around the expected time
		assertTrue("Time spent: " + measure.get() + " instead of " + expected,
				measure.get() > expected * 0.9
						&& measure.get() < expected * 1.1);
	}

	@Test
	public void testExceptionOnNullMeasure() {
		var measure = new ListenerTimeMeasure();
		try {
			measure.wrapMeasure(null);
			fail("No exception thrown");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testReturnSameWrapperForSameMeasure() {
		var measure = new ListenerTimeMeasure();

		var wrapped = new SimplePushMeasure<Object>();
		var wrapper = measure.wrapMeasure(wrapped);

		assertEquals(wrapper, measure.wrapMeasure(wrapped));
		assertEquals(wrapper, measure.wrapMeasure(wrapped));
		assertEquals(wrapper, measure.wrapMeasure(wrapped));
	}

	@Test
	public void testForgetMeasureWrapperIfNotUsedAnymore() {
		var measure = new ListenerTimeMeasure();

		var wrapped = new SimplePushMeasure<Object>();
		var lastHashCode = 0;
		var differences = 0;
		var rounds = 10;
		for (var round = 0; round < rounds; round++) {
			System.gc();
			var wrapper = measure.wrapMeasure(wrapped);
			var hashCode = wrapper.hashCode();
			if (lastHashCode != hashCode) {
				differences++;
			} else {
				// maybe the same object (hashcode does not guarantee it)
			}
		}

		// check the instance always changes with an error of 10%
		assertTrue("Differences: " + differences + "/" + rounds,
				differences > rounds * 0.9 && differences <= rounds);
	}

	@Test
	public void testSameNameAndDescriptionThanOriginalMeasure() {
		var measure = new ListenerTimeMeasure();

		{
			var name = "measure 1";
			String description = null;
			PushMeasure<Object> original = new SimplePushMeasure<>(name,
					description);
			var wrapper = measure.wrapMeasure(original);
			assertEquals(name, wrapper.getName());
			assertEquals(description, wrapper.getDescription());
		}

		{
			var name = "measure 2";
			var description = "Some description";
			PushMeasure<Object> original = new SimplePushMeasure<>(name,
					description);
			var wrapper = measure.wrapMeasure(original);
			assertEquals(name, wrapper.getName());
			assertEquals(description, wrapper.getDescription());
		}

		{
			String name = null;
			var description = "Unidentified Java Object";
			PushMeasure<Object> original = new SimplePushMeasure<>(name,
					description);
			var wrapper = measure.wrapMeasure(original);
			assertEquals(name, wrapper.getName());
			assertEquals(description, wrapper.getDescription());
		}
	}

	@Ignore
	@Test
	public void testCountTimeInManager() {
		var measure = new ListenerTimeMeasure();

		var measure1 = new SimplePushMeasure<Object>();
		var measure2 = new SimplePushMeasure<Object>();
		var wrapped = new SimpleMeasureManager();
		wrapped.setPushMeasure(1, measure1);
		wrapped.setPushMeasure(2, measure2);
		var wrapper = measure.wrapManager(wrapped, null);

		var measure1With10ms = wrapper.getPushMeasure(1);
		measure1With10ms.register(new FakeListener(5));
		measure1With10ms.register(new FakeListener(5));

		var measure2With20ms = wrapper.getPushMeasure(2);
		measure2With20ms.register(new FakeListener(5));
		measure2With20ms.register(new FakeListener(10));
		measure2With20ms.register(new FakeListener(5));

		measure1.push(null);
		measure2.push(null);
		measure2.push(null);
		measure1.push(null);
		long expected = 60;

		// check we are within a range of 10% around the expected time
		assertTrue("Time spent: " + measure.get() + " instead of " + expected,
				measure.get() > expected * 0.9
						&& measure.get() < expected * 1.1);
	}

	@Test
	public void testExceptionOnNullManager() {
		var measure = new ListenerTimeMeasure();
		try {
			measure.wrapManager(null, null);
			fail("No exception thrown");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testAdditionalKeyProvidedByManager() {
		var measure = new ListenerTimeMeasure();
		var key = "measure";

		var wrapped = new SimpleMeasureManager();
		var wrapper = measure.wrapManager(wrapped, key);

		assertTrue(wrapper.getMeasureKeys().contains(key));
	}

	@Test
	public void testAdditionalKeyForWrappedManagerReturnCurrentMeasure() {
		var measure = new ListenerTimeMeasure();
		var key = "measure";

		var wrapped = new SimpleMeasureManager();
		var wrapper = measure.wrapManager(wrapped, key);

		assertEquals(measure, wrapper.getPullMeasure(key));
	}

	@Test
	@SuppressWarnings("serial")
	public void testAdditionalKeyForWrappedManagerRejectAlreadyUsedKeys() {
		var measure = new ListenerTimeMeasure();

		var pull = new SimplePullMeasure<Object>() {

			@Override
			public Object get() {
				return null;
			}
		};
		var push = new SimplePushMeasure<Object>();
		var wrapped = new SimpleMeasureManager();
		wrapped.setPullMeasure(1, pull);
		wrapped.setPullMeasure(2, pull);
		wrapped.setPushMeasure(3, push);

		var counter = 0;
		for (var key : wrapped.getMeasureKeys()) {
			try {
				measure.wrapManager(wrapped, key);
				fail("No exception thrown for key " + key);
			} catch (IllegalArgumentException e) {
				counter++;
			}
		}
		assertEquals(3, counter);
	}

	@Test
	public void testResetToZeroWhenNoListenerIsRunning() {
		var measure = new ListenerTimeMeasure();

		MeasureListener<Object> original10ms = new FakeListener(10);
		var wrapper10ms = measure
				.wrapListener(original10ms);

		MeasureListener<Object> original20ms = new FakeListener(20);
		var wrapper20ms = measure
				.wrapListener(original20ms);

		wrapper10ms.measureGenerated(null);
		wrapper20ms.measureGenerated(null);
		wrapper20ms.measureGenerated(null);
		wrapper10ms.measureGenerated(null);

		measure.reset();
		assertEquals(0, (long) measure.get());
	}

	@Ignore
	@Test
	public void testResetToCurrentTimeWhenListenerIsRunning() {
		final var measure = new ListenerTimeMeasure();

		MeasureListener<Object> original50ms = new FakeListener(50);
		var wrapper50ms = measure
				.wrapListener(original50ms);

		MeasureListener<Object> original50msWithReset = value -> {
            try {
                Thread.sleep(25);
                measure.reset();
                Thread.sleep(25);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
		var wrapper50msWithReset = measure
				.wrapListener(original50msWithReset);

		wrapper50ms.measureGenerated(null);
		wrapper50msWithReset.measureGenerated(null);
		wrapper50ms.measureGenerated(null);
		long expected = 75;

		// check we are within a range of 10% around the expected time
		assertTrue("Time spent: " + measure.get() + " instead of " + expected,
				measure.get() > expected * 0.9
						&& measure.get() < expected * 1.1);
	}
}
