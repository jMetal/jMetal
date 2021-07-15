package org.uma.jmetal.util.pseudorandom;

import static org.junit.Assert.*;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

// TODO Shouldn't we be able to extend AbstractRandomTest?
public class JMetalRandomTest {

	private static final long TEST_SEED = 10L;

	@Test
	public void testJMetalRandomInstanceIsNotNull() {
		assertNotNull(JMetalRandom.getInstance());
	}

	@Test
	public void testNextIntDoesNotReturnConstantValue() {
		JMetalRandom instance = JMetalRandom.getInstance();
		instance.setSeed(TEST_SEED);

		Set<Integer> generated = new LinkedHashSet<>();
		int trials = 1000;
		int min = 0;
		int max = trials * 100;// enough to have different trials
		for (int i = 0; i < trials; i++) {
			generated.add(instance.nextInt(min, max));
		}
		// At least 80% of different values
		assertTrue(generated.size() > trials * 0.8);
	}

	@Test
	public void testNextDoubleDoesNotReturnConstantValue() {
		JMetalRandom instance = JMetalRandom.getInstance();
		instance.setSeed(TEST_SEED);

		Set<Double> generated = new LinkedHashSet<>();
		int trials = 1000;
		for (int i = 0; i < trials; i++) {
			generated.add(instance.nextDouble());
		}
		// At least 80% of different values
		assertTrue(generated.size() > trials * 0.8);
	}

	@Test
	public void testNextBoundedDoubleDoesNotReturnConstantValue() {
		JMetalRandom instance = JMetalRandom.getInstance();
		instance.setSeed(TEST_SEED);

		Set<Double> generated = new LinkedHashSet<>();
		int trials = 1000;
		double min = 10;
		double max = 100;
		for (int i = 0; i < trials; i++) {
			generated.add(instance.nextDouble(min, max));
		}
		// At least 80% of different values
		assertTrue(generated.size() > trials * 0.8);
	}

	@Test
	public void testNextDoubleReturnsValuesInZeroOne() {
		JMetalRandom instance = JMetalRandom.getInstance();
		instance.setSeed(TEST_SEED);

		int trials = 1000;
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (int i = 0; i < trials; i++) {
			double generated = instance.nextDouble();
			min = Math.min(min, generated);
			max = Math.max(max, generated);
		}
		assertTrue("Min: " + min, min >= 0 && min <= 1);
		assertTrue("Max: " + max, max >= 0 && max <= 1);
	}

	@Test
	public void testNextBoundedDoubleReturnsValuesInMinMax() {
		JMetalRandom instance = JMetalRandom.getInstance();
		instance.setSeed(TEST_SEED);

		int trials = 1000;
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		double limitMin = 10;
		double limitMax = 100;
		for (int i = 0; i < trials; i++) {
			double generated = instance.nextDouble(limitMin, limitMax);
			min = Math.min(min, generated);
			max = Math.max(max, generated);
		}
		assertTrue("Min: " + min, min >= limitMin && min <= limitMax);
		assertTrue("Max: " + max, max >= limitMin && max <= limitMax);
	}

	@Test
	public void testNextIntReturnsValuesInMinMax() {
		JMetalRandom instance = JMetalRandom.getInstance();
		instance.setSeed(TEST_SEED);

		int trials = 1000;
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		int limitMin = 10;
		int limitMax = 100;
		for (int i = 0; i < trials; i++) {
			int generated = instance.nextInt(limitMin, limitMax);
			min = Math.min(min, generated);
			max = Math.max(max, generated);
		}
		assertTrue("Min: " + min, min >= limitMin && min <= limitMax);
		assertTrue("Max: " + max, max >= limitMin && max <= limitMax);
	}

	@Test
	public void testNextDoubleIsReproducible() {
		JMetalRandom instance = JMetalRandom.getInstance();

		instance.setSeed(TEST_SEED);
		int sequenceLength = 1000;
		List<Double> sequence = new LinkedList<>();
		for (int i = 0; i < sequenceLength; i++) {
			sequence.add(instance.nextDouble());
		}

		instance.setSeed(TEST_SEED);
		for (int i = 0; i < sequenceLength; i++) {
			assertEquals(sequence.get(i), instance.nextDouble(), 0.0);
		}
	}

	@Test
	public void testNextBoundedDoubleIsReproducible() {
		JMetalRandom instance = JMetalRandom.getInstance();

		instance.setSeed(TEST_SEED);
		int sequenceLength = 1000;
		double min = 10;
		double max = 100;
		List<Double> sequence = new LinkedList<>();
		for (int i = 0; i < sequenceLength; i++) {
			sequence.add(instance.nextDouble(min, max));
		}

		instance.setSeed(TEST_SEED);
		for (int i = 0; i < sequenceLength; i++) {
			assertEquals(sequence.get(i), instance.nextDouble(min, max), 0.0);
		}
	}

	@Test
	public void testNextIntIsReproducible() {
		JMetalRandom instance = JMetalRandom.getInstance();

		instance.setSeed(TEST_SEED);
		int sequenceLength = 1000;
		int min = 10;
		int max = 100;
		List<Integer> sequence = new LinkedList<>();
		for (int i = 0; i < sequenceLength; i++) {
			sequence.add(instance.nextInt(min, max));
		}

		instance.setSeed(TEST_SEED);
		for (int i = 0; i < sequenceLength; i++) {
			assertEquals(sequence.get(i), instance.nextInt(min, max), 0.0);
		}
	}

}
