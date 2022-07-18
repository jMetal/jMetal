package org.uma.jmetal.util.measure.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.uma.jmetal.util.measure.MeasureListener;

public class SimplePushMeasureTest {

	@Test
	public void testNotifiedWhenRegistered() {
		final Integer[] lastReceived = { null };
		MeasureListener<Integer> listener = value -> lastReceived[0] = value;
		SimplePushMeasure<Integer> pusher = new SimplePushMeasure<Integer>();
		pusher.register(listener);

		pusher.push(3);
		assertEquals(3, (Object) lastReceived[0]);
		pusher.push(null);
		assertEquals(null, (Object) lastReceived[0]);
		pusher.push(5);
		assertEquals(5, (Object) lastReceived[0]);
	}

	@Test
	public void testNotNotifiedWhenUnregistered() {
		final Integer[] lastReceived = { null };
		MeasureListener<Integer> listener = value -> lastReceived[0] = value;
		SimplePushMeasure<Integer> pusher = new SimplePushMeasure<Integer>();
		pusher.register(listener);
		pusher.unregister(listener);

		pusher.push(3);
		assertEquals(null, (Object) lastReceived[0]);
		pusher.push(-45);
		assertEquals(null, (Object) lastReceived[0]);
	}

}
