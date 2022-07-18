package org.uma.jmetal.util.measure.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.uma.jmetal.util.measure.MeasureListener;

public class CountingMeasureTest {

	@Test
	public void testIncrementAddOne() {
		var measure = new CountingMeasure(15);

		measure.increment();
		assertEquals(16, (long) measure.get());

		measure.increment();
		assertEquals(17, (long) measure.get());

		measure.increment();
		assertEquals(18, (long) measure.get());
	}

	@Test
	public void testIncrementNotificationsOccur() {
		var measure = new CountingMeasure(15);
		final var isCalled = new boolean[]{false};
		measure.register(value -> isCalled[0] = true);

		isCalled[0] = false;
		measure.increment();
		assertTrue(isCalled[0]);

		isCalled[0] = false;
		measure.increment();
		assertTrue(isCalled[0]);

		isCalled[0] = false;
		measure.increment();
		assertTrue(isCalled[0]);
	}

	@Test
	public void testGetAlignedWithNotifications() {
		final var measure = new CountingMeasure(15);
		final var notifications = new int[]{0};
		measure.register(value -> {
			notifications[0]++;
			assertEquals(value, measure.get());
		});
		measure.increment();
		assertEquals(1, notifications[0]);
		measure.increment();
		assertEquals(2, notifications[0]);
		measure.increment();
		assertEquals(3, notifications[0]);
	}

	@Test
	public void testLinkedMeasureCorrectlyCounted() {
		var pusher = new SimplePushMeasure<Object>();

		var measure = new CountingMeasure();
		measure.link(pusher);

		assertEquals(0, (long) measure.get());
		pusher.push(null);
		assertEquals(1, (long) measure.get());
		pusher.push(null);
		assertEquals(2, (long) measure.get());
		pusher.push(null);
		assertEquals(3, (long) measure.get());
	}

	@Test
	public void testMultipleLinkedMeasuresCorrectlyCounted() {
		var pusher1 = new SimplePushMeasure<Object>();
		var pusher2 = new SimplePushMeasure<Object>();
		var pusher3 = new SimplePushMeasure<Object>();

		var measure = new CountingMeasure();
		measure.link(pusher1);
		measure.link(pusher2);
		measure.link(pusher3);

		assertEquals(0, (long) measure.get());
		pusher1.push(null);
		assertEquals(1, (long) measure.get());
		pusher2.push(null);
		assertEquals(2, (long) measure.get());
		pusher1.push(null);
		assertEquals(3, (long) measure.get());
		pusher3.push(null);
		assertEquals(4, (long) measure.get());
		pusher2.push(null);
		assertEquals(5, (long) measure.get());
	}

	@Test
	public void testMultipleLinksOnTheSameMeasureCountedOnce() {
		var pusher = new SimplePushMeasure<Object>();

		var measure = new CountingMeasure();
		measure.link(pusher);
		measure.link(pusher);
		measure.link(pusher);

		assertEquals(0, (long) measure.get());
		pusher.push(null);
		assertEquals(1, (long) measure.get());
		pusher.push(null);
		assertEquals(2, (long) measure.get());
		pusher.push(null);
		assertEquals(3, (long) measure.get());
	}

	@Test
	public void testUnlinkCorrectlyIgnored() {
		var pusher = new SimplePushMeasure<Object>();

		var measure = new CountingMeasure();
		measure.link(pusher);
		measure.unlink(pusher);

		assertEquals(0, (long) measure.get());
		pusher.push(null);
		assertEquals(0, (long) measure.get());
		pusher.push(null);
		assertEquals(0, (long) measure.get());
		pusher.push(null);
		assertEquals(0, (long) measure.get());
	}

	@Test
	public void testReset() {
		var measure = new CountingMeasure();

		measure.increment();
		measure.increment();
		measure.increment();
		measure.reset();
		assertEquals(0, (long) measure.get());

		measure.increment(5);
		measure.increment();
		measure.increment(3);
		measure.reset();
		assertEquals(0, (long) measure.get());
	}

	@Test
	public void testResetToAGivenValue() {
		var measure = new CountingMeasure();

		measure.increment();
		measure.increment();
		measure.increment();
		measure.reset(-13);
		assertEquals(-13, (long) measure.get());

		measure.increment(5);
		measure.increment();
		measure.increment(3);
		measure.reset(45);
		assertEquals(45, (long) measure.get());
	}

	@Test
	public void testResetNotificationsOccur() {
		var measure = new CountingMeasure(15);
		final var isCalled = new boolean[]{false};
		measure.register(value -> isCalled[0] = true);

		isCalled[0] = false;
		measure.reset();
		assertTrue(isCalled[0]);

		isCalled[0] = false;
		measure.reset();
		assertFalse(isCalled[0]);

		isCalled[0] = false;
		measure.reset(35);
		assertTrue(isCalled[0]);

		isCalled[0] = false;
		measure.reset(35);
		assertFalse(isCalled[0]);
	}
	
	@Test
	public void testIncrementNotificationsOccurIfNonZero() {
		var measure = new CountingMeasure(15);
		final var isCalled = new boolean[]{false};
		measure.register(value -> isCalled[0] = true);

		isCalled[0] = false;
		measure.increment(3);
		assertTrue(isCalled[0]);

		isCalled[0] = false;
		measure.increment(0);
		assertFalse(isCalled[0]);

		isCalled[0] = false;
		measure.increment(-35);
		assertTrue(isCalled[0]);

		isCalled[0] = false;
		measure.increment(0);
		assertFalse(isCalled[0]);
	}
}
