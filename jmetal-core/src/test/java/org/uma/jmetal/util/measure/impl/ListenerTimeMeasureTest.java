package org.uma.jmetal.util.measure.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.measure.MeasureListener;
import org.uma.jmetal.util.measure.MeasureManager;
import org.uma.jmetal.util.measure.PushMeasure;

class ListenerTimeMeasureTest {

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

	@Disabled
	@Test
  void testFakeListener() {
		for (long expected : new Long[] { 5L, 10L, 20L }) {
			FakeListener listener = new FakeListener(expected);
			int rounds = 10;
			int average = 0;
			for (int i = 0; i < rounds; i++) {
				long start = System.currentTimeMillis();
				listener.measureGenerated(null);
				long stop = System.currentTimeMillis();
				long time = stop - start;

				average += time;
			}
			average /= rounds;

			// check we are within a range of 10% around the expected time
			Assertions.assertTrue(average > expected * 0.9 && average < expected * 1.1,
					"Time spent: " + average + " instead of " + expected);
		}
	}

	@Disabled
	@Test
  void testCountTimeInListeners() {
		ListenerTimeMeasure measure = new ListenerTimeMeasure();

		MeasureListener<Object> original10ms = new FakeListener(10);
		MeasureListener<Object> wrapper10ms = measure
				.wrapListener(original10ms);

		MeasureListener<Object> original20ms = new FakeListener(20);
		MeasureListener<Object> wrapper20ms = measure
				.wrapListener(original20ms);

		wrapper10ms.measureGenerated(null);
		wrapper20ms.measureGenerated(null);
		wrapper20ms.measureGenerated(null);
		wrapper10ms.measureGenerated(null);
		long expected = 60;

		// check we are within a range of 10% around the expected time
		Assertions.assertTrue(measure.get() > expected * 0.9
				&& measure.get() < expected * 1.1,
				"Time spent: " + measure.get() + " instead of " + expected);
	}

	@Test
  void testExceptionOnNullListener() {
		ListenerTimeMeasure measure = new ListenerTimeMeasure();
		try {
			measure.wrapListener(null);
			Assertions.fail("No exception thrown");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
  void testReturnSameWrapperForSameListener()
			throws InterruptedException {
		ListenerTimeMeasure measure = new ListenerTimeMeasure();

		MeasureListener<Object> wrapped = new FakeListener(10);
		MeasureListener<Object> wrapper = measure.wrapListener(wrapped);

		Assertions.assertEquals(wrapper, measure.wrapListener(wrapped));
		Assertions.assertEquals(wrapper, measure.wrapListener(wrapped));
		Assertions.assertEquals(wrapper, measure.wrapListener(wrapped));
	}

	@Test
  void testForgetListenerWrapperIfNotUsedAnymore() {
		ListenerTimeMeasure measure = new ListenerTimeMeasure();

		MeasureListener<Object> wrapped = new FakeListener(10);
		int lastHashCode = 0;
		int differences = 0;
		int rounds = 10;
		for (int round = 0; round < rounds; round++) {
			System.gc();
			MeasureListener<Object> wrapper = measure.wrapListener(wrapped);
			int hashCode = wrapper.hashCode();
			if (lastHashCode != hashCode) {
				differences++;
			} else {
				// maybe the same object (hashcode does not guarantee it)
			}
		}

		// check the instance always changes with an error of 10%
		Assertions.assertTrue(differences > rounds * 0.9 && differences <= rounds,
				"Differences: " + differences + "/" + rounds);
	}

	@Disabled
	@Test
  void testCountTimeInMeasures() {
		ListenerTimeMeasure measure = new ListenerTimeMeasure();

		SimplePushMeasure<Object> original10ms = new SimplePushMeasure<>();
		PushMeasure<Object> wrapper10ms = measure.wrapMeasure(original10ms);
		wrapper10ms.register(new FakeListener(5));
		wrapper10ms.register(new FakeListener(5));

		SimplePushMeasure<Object> original20ms = new SimplePushMeasure<>();
		PushMeasure<Object> wrapper20ms = measure.wrapMeasure(original20ms);
		wrapper20ms.register(new FakeListener(5));
		wrapper20ms.register(new FakeListener(10));
		wrapper20ms.register(new FakeListener(5));

		original10ms.push(null);
		original20ms.push(null);
		original20ms.push(null);
		original10ms.push(null);
		long expected = 60;

		// check we are within a range of 10% around the expected time
		Assertions.assertTrue(measure.get() > expected * 0.9
				&& measure.get() < expected * 1.1,
				"Time spent: " + measure.get() + " instead of " + expected);
	}

	@Test
  void testExceptionOnNullMeasure() {
		ListenerTimeMeasure measure = new ListenerTimeMeasure();
		try {
			measure.wrapMeasure(null);
			Assertions.fail("No exception thrown");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
  void testReturnSameWrapperForSameMeasure() {
		ListenerTimeMeasure measure = new ListenerTimeMeasure();

		SimplePushMeasure<Object> wrapped = new SimplePushMeasure<Object>();
		PushMeasure<Object> wrapper = measure.wrapMeasure(wrapped);

		Assertions.assertEquals(wrapper, measure.wrapMeasure(wrapped));
		Assertions.assertEquals(wrapper, measure.wrapMeasure(wrapped));
		Assertions.assertEquals(wrapper, measure.wrapMeasure(wrapped));
	}

	@Test
  void testForgetMeasureWrapperIfNotUsedAnymore() {
		ListenerTimeMeasure measure = new ListenerTimeMeasure();

		SimplePushMeasure<Object> wrapped = new SimplePushMeasure<Object>();
		int lastHashCode = 0;
		int differences = 0;
		int rounds = 10;
		for (int round = 0; round < rounds; round++) {
			System.gc();
			PushMeasure<Object> wrapper = measure.wrapMeasure(wrapped);
			int hashCode = wrapper.hashCode();
			if (lastHashCode != hashCode) {
				differences++;
			} else {
				// maybe the same object (hashcode does not guarantee it)
			}
		}

		// check the instance always changes with an error of 10%
		Assertions.assertTrue(differences > rounds * 0.9 && differences <= rounds,
				"Differences: " + differences + "/" + rounds);
	}

	@Test
  void testSameNameAndDescriptionThanOriginalMeasure() {
		ListenerTimeMeasure measure = new ListenerTimeMeasure();

		{
			String name = "measure 1";
			String description = null;
			PushMeasure<Object> original = new SimplePushMeasure<>(name,
					description);
			PushMeasure<Object> wrapper = measure.wrapMeasure(original);
			Assertions.assertEquals(name, wrapper.name());
			Assertions.assertEquals(description, wrapper.description());
		}

		{
			String name = "measure 2";
			String description = "Some description";
			PushMeasure<Object> original = new SimplePushMeasure<>(name,
					description);
			PushMeasure<Object> wrapper = measure.wrapMeasure(original);
			Assertions.assertEquals(name, wrapper.name());
			Assertions.assertEquals(description, wrapper.description());
		}

		{
			String name = null;
			String description = "Unidentified Java Object";
			PushMeasure<Object> original = new SimplePushMeasure<>(name,
					description);
			PushMeasure<Object> wrapper = measure.wrapMeasure(original);
			Assertions.assertEquals(name, wrapper.name());
			Assertions.assertEquals(description, wrapper.description());
		}
	}

	@Disabled
	@Test
  void testCountTimeInManager() {
		ListenerTimeMeasure measure = new ListenerTimeMeasure();

		SimplePushMeasure<Object> measure1 = new SimplePushMeasure<Object>();
		SimplePushMeasure<Object> measure2 = new SimplePushMeasure<Object>();
		SimpleMeasureManager wrapped = new SimpleMeasureManager();
		wrapped.setPushMeasure(1, measure1);
		wrapped.setPushMeasure(2, measure2);
		MeasureManager wrapper = measure.wrapManager(wrapped, null);

		PushMeasure<Object> measure1With10ms = wrapper.getPushMeasure(1);
		measure1With10ms.register(new FakeListener(5));
		measure1With10ms.register(new FakeListener(5));

		PushMeasure<Object> measure2With20ms = wrapper.getPushMeasure(2);
		measure2With20ms.register(new FakeListener(5));
		measure2With20ms.register(new FakeListener(10));
		measure2With20ms.register(new FakeListener(5));

		measure1.push(null);
		measure2.push(null);
		measure2.push(null);
		measure1.push(null);
		long expected = 60;

		// check we are within a range of 10% around the expected time
		Assertions.assertTrue(measure.get() > expected * 0.9
				&& measure.get() < expected * 1.1,
				"Time spent: " + measure.get() + " instead of " + expected);
	}

	@Test
  void testExceptionOnNullManager() {
		ListenerTimeMeasure measure = new ListenerTimeMeasure();
		try {
			measure.wrapManager(null, null);
			Assertions.fail("No exception thrown");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
  void testAdditionalKeyProvidedByManager() {
		ListenerTimeMeasure measure = new ListenerTimeMeasure();
		String key = "measure";

		SimpleMeasureManager wrapped = new SimpleMeasureManager();
		MeasureManager wrapper = measure.wrapManager(wrapped, key);

		Assertions.assertTrue(wrapper.getMeasureKeys().contains(key));
	}

	@Test
  void testAdditionalKeyForWrappedManagerReturnCurrentMeasure() {
		ListenerTimeMeasure measure = new ListenerTimeMeasure();
		String key = "measure";

		SimpleMeasureManager wrapped = new SimpleMeasureManager();
		MeasureManager wrapper = measure.wrapManager(wrapped, key);

		Assertions.assertEquals(measure, wrapper.getPullMeasure(key));
	}

	@Test
	@SuppressWarnings("serial")
  void testAdditionalKeyForWrappedManagerRejectAlreadyUsedKeys() {
		ListenerTimeMeasure measure = new ListenerTimeMeasure();

		SimplePullMeasure<Object> pull = new SimplePullMeasure<Object>() {

			@Override
			public Object get() {
				return null;
			}
		};
		SimplePushMeasure<Object> push = new SimplePushMeasure<Object>();
		SimpleMeasureManager wrapped = new SimpleMeasureManager();
		wrapped.setPullMeasure(1, pull);
		wrapped.setPullMeasure(2, pull);
		wrapped.setPushMeasure(3, push);

		int counter = 0;
		for (Object key : wrapped.getMeasureKeys()) {
			try {
				measure.wrapManager(wrapped, key);
				Assertions.fail("No exception thrown for key " + key);
			} catch (IllegalArgumentException e) {
				counter++;
			}
		}
		Assertions.assertEquals(3, counter);
	}

	@Test
  void testResetToZeroWhenNoListenerIsRunning() {
		ListenerTimeMeasure measure = new ListenerTimeMeasure();

		MeasureListener<Object> original10ms = new FakeListener(10);
		MeasureListener<Object> wrapper10ms = measure
				.wrapListener(original10ms);

		MeasureListener<Object> original20ms = new FakeListener(20);
		MeasureListener<Object> wrapper20ms = measure
				.wrapListener(original20ms);

		wrapper10ms.measureGenerated(null);
		wrapper20ms.measureGenerated(null);
		wrapper20ms.measureGenerated(null);
		wrapper10ms.measureGenerated(null);

		measure.reset();
		Assertions.assertEquals(0, (long) measure.get());
	}

	@Disabled
	@Test
  void testResetToCurrentTimeWhenListenerIsRunning() {
		final ListenerTimeMeasure measure = new ListenerTimeMeasure();

		MeasureListener<Object> original50ms = new FakeListener(50);
		MeasureListener<Object> wrapper50ms = measure
				.wrapListener(original50ms);

		MeasureListener<Object> original50msWithReset = new MeasureListener<Object>() {

			@Override
			public void measureGenerated(Object value) {
				try {
					Thread.sleep(25);
					measure.reset();
					Thread.sleep(25);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		};
		MeasureListener<Object> wrapper50msWithReset = measure
				.wrapListener(original50msWithReset);

		wrapper50ms.measureGenerated(null);
		wrapper50msWithReset.measureGenerated(null);
		wrapper50ms.measureGenerated(null);
		long expected = 75;

		// check we are within a range of 10% around the expected time
		Assertions.assertTrue(measure.get() > expected * 0.9
				&& measure.get() < expected * 1.1,
				"Time spent: " + measure.get() + " instead of " + expected);
	}
}
