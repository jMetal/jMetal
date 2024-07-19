package org.uma.jmetal.util.measure.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.measure.MeasureListener;

class CountingMeasureTest {

	@Test
  void testIncrementAddOne() {
		CountingMeasure measure = new CountingMeasure(15);

		measure.increment();
		Assertions.assertEquals(16, (long) measure.get());

		measure.increment();
		Assertions.assertEquals(17, (long) measure.get());

		measure.increment();
		Assertions.assertEquals(18, (long) measure.get());
	}

	@Test
  void testIncrementNotificationsOccur() {
		CountingMeasure measure = new CountingMeasure(15);
		final boolean[] isCalled = { false };
		measure.register(new MeasureListener<Long>() {

			@Override
			public void measureGenerated(Long value) {
				isCalled[0] = true;
			}
		});

		isCalled[0] = false;
		measure.increment();
		Assertions.assertTrue(isCalled[0]);

		isCalled[0] = false;
		measure.increment();
		Assertions.assertTrue(isCalled[0]);

		isCalled[0] = false;
		measure.increment();
		Assertions.assertTrue(isCalled[0]);
	}

	@Test
  void testGetAlignedWithNotifications() {
		final CountingMeasure measure = new CountingMeasure(15);
		final int[] notifications = { 0 };
		measure.register(new MeasureListener<Long>() {

			@Override
			public void measureGenerated(Long value) {
				notifications[0]++;
				Assertions.assertEquals(value, measure.get());
			}
		});
		measure.increment();
		Assertions.assertEquals(1, notifications[0]);
		measure.increment();
		Assertions.assertEquals(2, notifications[0]);
		measure.increment();
		Assertions.assertEquals(3, notifications[0]);
	}

	@Test
  void testLinkedMeasureCorrectlyCounted() {
		SimplePushMeasure<Object> pusher = new SimplePushMeasure<>();

		CountingMeasure measure = new CountingMeasure();
		measure.link(pusher);

		Assertions.assertEquals(0, (long) measure.get());
		pusher.push(null);
		Assertions.assertEquals(1, (long) measure.get());
		pusher.push(null);
		Assertions.assertEquals(2, (long) measure.get());
		pusher.push(null);
		Assertions.assertEquals(3, (long) measure.get());
	}

	@Test
  void testMultipleLinkedMeasuresCorrectlyCounted() {
		SimplePushMeasure<Object> pusher1 = new SimplePushMeasure<>();
		SimplePushMeasure<Object> pusher2 = new SimplePushMeasure<>();
		SimplePushMeasure<Object> pusher3 = new SimplePushMeasure<>();

		CountingMeasure measure = new CountingMeasure();
		measure.link(pusher1);
		measure.link(pusher2);
		measure.link(pusher3);

		Assertions.assertEquals(0, (long) measure.get());
		pusher1.push(null);
		Assertions.assertEquals(1, (long) measure.get());
		pusher2.push(null);
		Assertions.assertEquals(2, (long) measure.get());
		pusher1.push(null);
		Assertions.assertEquals(3, (long) measure.get());
		pusher3.push(null);
		Assertions.assertEquals(4, (long) measure.get());
		pusher2.push(null);
		Assertions.assertEquals(5, (long) measure.get());
	}

	@Test
  void testMultipleLinksOnTheSameMeasureCountedOnce() {
		SimplePushMeasure<Object> pusher = new SimplePushMeasure<>();

		CountingMeasure measure = new CountingMeasure();
		measure.link(pusher);
		measure.link(pusher);
		measure.link(pusher);

		Assertions.assertEquals(0, (long) measure.get());
		pusher.push(null);
		Assertions.assertEquals(1, (long) measure.get());
		pusher.push(null);
		Assertions.assertEquals(2, (long) measure.get());
		pusher.push(null);
		Assertions.assertEquals(3, (long) measure.get());
	}

	@Test
  void testUnlinkCorrectlyIgnored() {
		SimplePushMeasure<Object> pusher = new SimplePushMeasure<>();

		CountingMeasure measure = new CountingMeasure();
		measure.link(pusher);
		measure.unlink(pusher);

		Assertions.assertEquals(0, (long) measure.get());
		pusher.push(null);
		Assertions.assertEquals(0, (long) measure.get());
		pusher.push(null);
		Assertions.assertEquals(0, (long) measure.get());
		pusher.push(null);
		Assertions.assertEquals(0, (long) measure.get());
	}

	@Test
  void testReset() {
		CountingMeasure measure = new CountingMeasure();

		measure.increment();
		measure.increment();
		measure.increment();
		measure.reset();
		Assertions.assertEquals(0, (long) measure.get());

		measure.increment(5);
		measure.increment();
		measure.increment(3);
		measure.reset();
		Assertions.assertEquals(0, (long) measure.get());
	}

	@Test
  void testResetToAGivenValue() {
		CountingMeasure measure = new CountingMeasure();

		measure.increment();
		measure.increment();
		measure.increment();
		measure.reset(-13);
		Assertions.assertEquals(-13, (long) measure.get());

		measure.increment(5);
		measure.increment();
		measure.increment(3);
		measure.reset(45);
		Assertions.assertEquals(45, (long) measure.get());
	}

	@Test
  void testResetNotificationsOccur() {
		CountingMeasure measure = new CountingMeasure(15);
		final boolean[] isCalled = { false };
		measure.register(new MeasureListener<Long>() {

			@Override
			public void measureGenerated(Long value) {
				isCalled[0] = true;
			}
		});

		isCalled[0] = false;
		measure.reset();
		Assertions.assertTrue(isCalled[0]);

		isCalled[0] = false;
		measure.reset();
		Assertions.assertFalse(isCalled[0]);

		isCalled[0] = false;
		measure.reset(35);
		Assertions.assertTrue(isCalled[0]);

		isCalled[0] = false;
		measure.reset(35);
		Assertions.assertFalse(isCalled[0]);
	}
	
	@Test
  void testIncrementNotificationsOccurIfNonZero() {
		CountingMeasure measure = new CountingMeasure(15);
		final boolean[] isCalled = { false };
		measure.register(new MeasureListener<Long>() {

			@Override
			public void measureGenerated(Long value) {
				isCalled[0] = true;
			}
		});

		isCalled[0] = false;
		measure.increment(3);
		Assertions.assertTrue(isCalled[0]);

		isCalled[0] = false;
		measure.increment(0);
		Assertions.assertFalse(isCalled[0]);

		isCalled[0] = false;
		measure.increment(-35);
		Assertions.assertTrue(isCalled[0]);

		isCalled[0] = false;
		measure.increment(0);
		Assertions.assertFalse(isCalled[0]);
	}
}
