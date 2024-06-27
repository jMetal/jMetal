package org.uma.jmetal.util.measure.impl;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.measure.MeasureListener;

class SimplePushMeasureTest {

	@Test
  void testNotifiedWhenRegistered() {
		final Integer[] lastReceived = { null };
		MeasureListener<Integer> listener = new MeasureListener<Integer>() {

			@Override
			public void measureGenerated(Integer value) {
				lastReceived[0] = value;
			}
		};
		SimplePushMeasure<Integer> pusher = new SimplePushMeasure<Integer>();
		pusher.register(listener);

		pusher.push(3);
		Assertions.assertEquals(3, (Object) lastReceived[0]);
		pusher.push(null);
		Assertions.assertEquals(null, (Object) lastReceived[0]);
		pusher.push(5);
		Assertions.assertEquals(5, (Object) lastReceived[0]);
	}

	@Test
  void testNotNotifiedWhenUnregistered() {
		final Integer[] lastReceived = { null };
		MeasureListener<Integer> listener = new MeasureListener<Integer>() {

			@Override
			public void measureGenerated(Integer value) {
				lastReceived[0] = value;
			}
		};
		SimplePushMeasure<Integer> pusher = new SimplePushMeasure<Integer>();
		pusher.register(listener);
		pusher.unregister(listener);

		pusher.push(3);
		Assertions.assertEquals(null, (Object) lastReceived[0]);
		pusher.push(-45);
		Assertions.assertEquals(null, (Object) lastReceived[0]);
	}

}
