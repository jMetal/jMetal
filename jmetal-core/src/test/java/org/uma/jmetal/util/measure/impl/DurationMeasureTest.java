package org.uma.jmetal.util.measure.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DurationMeasureTest {

	private final static long DEFAULT_LIMIT = 100;

	@Test
  void testNoRunningRemainsAtZero() throws InterruptedException {
		DurationMeasure measure = new DurationMeasure();
		Assertions.assertEquals(0, (long) measure.get());
		Thread.sleep(DEFAULT_LIMIT);
		Assertions.assertEquals(0, (long) measure.get());
	}

	@Test
  void testIncreasesBetweenStartAndStop() throws InterruptedException {
		DurationMeasure measure = new DurationMeasure();
		long oldDuration;
		long newDuration;

		oldDuration = measure.get();
		measure.start();
		Thread.sleep(DEFAULT_LIMIT);
		measure.stop();
		newDuration = measure.get();
		Assertions.assertTrue(oldDuration < newDuration);

		oldDuration = measure.get();
		measure.start();
		Thread.sleep(DEFAULT_LIMIT);
		measure.stop();
		newDuration = measure.get();
		Assertions.assertTrue(oldDuration < newDuration);
	}

	@Test
  void testDoNotEvolveBetweenStopAndRestart()
			throws InterruptedException {
		DurationMeasure measure = new DurationMeasure();

		measure.start();
		Thread.sleep(DEFAULT_LIMIT);
		measure.stop();
		long oldDuration = measure.get();
		Thread.sleep(DEFAULT_LIMIT);
		long newDuration = measure.get();

		Assertions.assertEquals(oldDuration, newDuration);
	}

	@Test
  void testResetGoBackToZeroWhenStopped() throws InterruptedException {
		DurationMeasure measure = new DurationMeasure();

		measure.start();
		Thread.sleep(DEFAULT_LIMIT);
		measure.stop();
		measure.reset();
		long dureation = measure.get();

		Assertions.assertEquals(0, dureation);
	}

	@Test
  void testResetRestartFromZeroWhenRunning()
			throws InterruptedException {
		DurationMeasure measure = new DurationMeasure();

		long expected = DEFAULT_LIMIT;
		measure.start();
		Thread.sleep(5 * expected);
		measure.reset();
		Thread.sleep(expected);
		measure.stop();
		long duration = measure.get();

		double errorRatioAccepted = 0.2;
		double lowerExpected = expected * (1 - errorRatioAccepted);
		double higherExpected = expected * (1 + errorRatioAccepted);
		Assertions.assertTrue(duration > lowerExpected);
		Assertions.assertTrue(duration < higherExpected);
	}

}
