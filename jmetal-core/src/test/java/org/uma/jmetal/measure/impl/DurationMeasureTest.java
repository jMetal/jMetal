package org.uma.jmetal.measure.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DurationMeasureTest {

	private final static long DEFAULT_LIMIT = 100;

	@Test
	public void testNoRunningRemainsAtZero() throws InterruptedException {
		DurationMeasure measure = new DurationMeasure();
		assertEquals(0, (long) measure.get());
		Thread.sleep(DEFAULT_LIMIT);
		assertEquals(0, (long) measure.get());
	}

	@Test
	public void testIncreasesBetweenStartAndStop() throws InterruptedException {
		DurationMeasure measure = new DurationMeasure();
		long oldDuration;
		long newDuration;

		oldDuration = measure.get();
		measure.start();
		Thread.sleep(DEFAULT_LIMIT);
		measure.stop();
		newDuration = measure.get();
		assertTrue(oldDuration < newDuration);

		oldDuration = measure.get();
		measure.start();
		Thread.sleep(DEFAULT_LIMIT);
		measure.stop();
		newDuration = measure.get();
		assertTrue(oldDuration < newDuration);
	}

	@Test
	public void testDoNotEvolveBetweenStopAndRestart()
			throws InterruptedException {
		DurationMeasure measure = new DurationMeasure();

		measure.start();
		Thread.sleep(DEFAULT_LIMIT);
		measure.stop();
		long oldDuration = measure.get();
		Thread.sleep(DEFAULT_LIMIT);
		long newDuration = measure.get();

		assertEquals(oldDuration, newDuration);
	}

	@Test
	public void testResetGoBackToZeroWhenStopped() throws InterruptedException {
		DurationMeasure measure = new DurationMeasure();

		measure.start();
		Thread.sleep(DEFAULT_LIMIT);
		measure.stop();
		measure.reset();
		long dureation = measure.get();

		assertEquals(0, dureation);
	}

	@Test
	public void testResetRestartFromZeroWhenRunning()
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
		assertTrue(duration > lowerExpected);
		assertTrue(duration < higherExpected);
	}

}
