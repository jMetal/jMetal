package org.uma.jmetal.util.pseudorandom;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class BoundedRandomGeneratorTest {

	@Test
	public void testUnboundedDoubleToIntegerFactoryMethodReturnsGeneratorWithCorrectValues() {
		double[] doubleValue = { 0 };
		RandomGenerator<Double> doubleGenerator = () -> doubleValue[0];
		BoundedRandomGenerator<Integer> generator = BoundedRandomGenerator.fromDoubleToInteger(doubleGenerator);

		for (double v = 0; v <= 1; v += 0.01) {
			doubleValue[0] = v;
			Integer value = generator.getRandomValue(1, 5);
			assertTrue("Out of bounds value (" + value + ") for double " + v, value >= 1 && value <= 5);
		}
	}

	@Test
	public void testUnboundedDoubleToIntegerFactoryMethodReturnsGeneratorWithCorrectDistribution() {
		Random random = new Random();
		BoundedRandomGenerator<Integer> generator = BoundedRandomGenerator
				.fromDoubleToInteger((RandomGenerator<Double>) () -> random.nextDouble());

		int min = 1;
		int max = 5;

		// Generate random values
		Map<Integer, Integer> counters = new HashMap<>();
		for (int i = min; i <= max; i++) {
			counters.put(i, 0);
		}
		for (int i = 0; i <= 10000; i++) {
			Integer value = generator.getRandomValue(min, max);
			counters.put(value, counters.get(value) + 1);
		}

		// Test the distribution is reasonably uniform
		double epsilon = 0.1;
		for (int i = min; i <= max; i++) {
			Integer occurrences = counters.get(i);
			assertTrue("Not a reasonable amount of occurrences (" + occurrences + ") for value " + i,
					occurrences >= 2000 * (1 - epsilon) && occurrences <= 2000 * (1 + epsilon));
		}
	}

	@Test
	public void testBoundedDoubleToIntegerFactoryMethodReturnsGeneratorWithCorrectValues() {
		double[] doubleValue = { 0 };
		BoundedRandomGenerator<Double> doubleGenerator = (min, max) -> doubleValue[0] * (max - min) + min;
		BoundedRandomGenerator<Integer> generator = BoundedRandomGenerator.fromDoubleToInteger(doubleGenerator);

		for (double v = 0; v <= 1; v += 0.01) {
			doubleValue[0] = v;
			Integer value = generator.getRandomValue(1, 5);
			assertTrue("Out of bounds value (" + value + ") for double " + v, value >= 1 && value <= 5);
		}
	}

	@Test
	public void testBoundedDoubleToIntegerFactoryMethodReturnsGeneratorWithCorrectDistribution() {
		Random random = new Random();
		BoundedRandomGenerator<Integer> generator = BoundedRandomGenerator.fromDoubleToInteger(
				(BoundedRandomGenerator<Double>) (min, max) -> random.nextDouble() * (max - min) + min);

		int min = 1;
		int max = 5;

		// Generate random values
		Map<Integer, Integer> counters = new HashMap<>();
		for (int i = min; i <= max; i++) {
			counters.put(i, 0);
		}
		for (int i = 0; i <= 10000; i++) {
			Integer value = generator.getRandomValue(min, max);
			counters.put(value, counters.get(value) + 1);
		}

		// Test the distribution is reasonably uniform
		double epsilon = 0.1;
		for (int i = min; i <= max; i++) {
			Integer occurrences = counters.get(i);
			assertTrue("Not a reasonable amount of occurrences (" + occurrences + ") for value " + i,
					occurrences >= 2000 * (1 - epsilon) && occurrences <= 2000 * (1 + epsilon));
		}
	}

	@Test
	public void testBoundingFactoryMethodReturnsGeneratorWithCorrectValues() {
		Random random = new Random();
		BoundedRandomGenerator<Double> generator = BoundedRandomGenerator.bound(() -> random.nextDouble());

		double min = 1;
		double max = 5;
		for (int i = 0; i <= 10000; i++) {
			double value = generator.getRandomValue(min, max);
			assertTrue("Out of bounds value: " + value, value >= min && value <= max);
		}
	}

}
