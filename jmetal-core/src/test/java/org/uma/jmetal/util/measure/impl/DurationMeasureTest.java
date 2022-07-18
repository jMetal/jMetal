package org.uma.jmetal.util.measure.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DurationMeasureTest {

	private final static long DEFAULT_LIMIT = 100;

	@Test
	public void testNoRunningRemainsAtZero() throws InterruptedException {
		var measure = new DurationMeasure();
		assertEquals(0, (long) measure.get());
		Thread.sleep(DEFAULT_LIMIT);
		assertEquals(0, (long) measure.get());
	}

	@Test
	public void testIncreasesBetweenStartAndStop() throws InterruptedException {
		var measure = new DurationMeasure();

        long oldDuration = measure.get();
		measure.start();
		Thread.sleep(DEFAULT_LIMIT);
		measure.stop();
        long newDuration = measure.get();
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
		var measure = new DurationMeasure();

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
		var measure = new DurationMeasure();

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
		var measure = new DurationMeasure();

		var expected = DEFAULT_LIMIT;
		measure.start();
		Thread.sleep(5 * expected);
		measure.reset();
		Thread.sleep(expected);
		measure.stop();
		long duration = measure.get();

		var errorRatioAccepted = 0.2;
		var lowerExpected = expected * (1 - errorRatioAccepted);
		var higherExpected = expected * (1 + errorRatioAccepted);
		assertTrue(duration > lowerExpected);
		assertTrue(duration < higherExpected);
	}

}
