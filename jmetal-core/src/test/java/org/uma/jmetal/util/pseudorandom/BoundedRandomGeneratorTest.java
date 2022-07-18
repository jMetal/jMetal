package org.uma.jmetal.util.pseudorandom;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

public class BoundedRandomGeneratorTest {

	@Test
	public void testUnboundedDoubleToIntegerFactoryMethodReturnsGeneratorWithCorrectValues() {
		double[] doubleValue = { 0 };
		RandomGenerator<Double> doubleGenerator = () -> doubleValue[0];
		var generator = BoundedRandomGenerator.fromDoubleToInteger(doubleGenerator);

		for (double v = 0; v <= 1; v += 0.01) {
			doubleValue[0] = v;
			var value = generator.getRandomValue(1, 5);
			assertTrue("Out of bounds value (" + value + ") for double " + v, value >= 1 && value <= 5);
		}
	}

	@Test
	public void testUnboundedDoubleToIntegerFactoryMethodReturnsGeneratorWithCorrectDistribution() {
		var random = new Random();
		var generator = BoundedRandomGenerator
				.fromDoubleToInteger((RandomGenerator<Double>) random::nextDouble);

		var min = 1;
		var max = 5;

		// Generate random values
		Map<Integer, Integer> counters = new HashMap<>();
		for (var i1 = min; i1 <= max; i1++) {
			Integer integer = i1;
			counters.put(integer, 0);
		}
		for (var i = 0; i <= 10000; i++) {
			var value = generator.getRandomValue(min, max);
			counters.put(value, counters.get(value) + 1);
		}

		// Test the distribution is reasonably uniform
		var epsilon = 0.1;
		for (var i = min; i <= max; i++) {
			var occurrences = counters.get(i);
			assertTrue("Not a reasonable amount of occurrences (" + occurrences + ") for value " + i,
					occurrences >= 2000 * (1 - epsilon) && occurrences <= 2000 * (1 + epsilon));
		}
	}

	@Test
	public void testBoundedDoubleToIntegerFactoryMethodReturnsGeneratorWithCorrectValues() {
		double[] doubleValue = { 0 };
		BoundedRandomGenerator<Double> doubleGenerator = (min, max) -> doubleValue[0] * (max - min) + min;
		var generator = BoundedRandomGenerator.fromDoubleToInteger(doubleGenerator);

		for (double v = 0; v <= 1; v += 0.01) {
			doubleValue[0] = v;
			var value = generator.getRandomValue(1, 5);
			assertTrue("Out of bounds value (" + value + ") for double " + v, value >= 1 && value <= 5);
		}
	}

	@Test
	public void testBoundedDoubleToIntegerFactoryMethodReturnsGeneratorWithCorrectDistribution() {
		var random = new Random();
		var generator = BoundedRandomGenerator.fromDoubleToInteger(
				(BoundedRandomGenerator<Double>) (min, max) -> random.nextDouble() * (max - min) + min);

		var min = 1;
		var max = 5;

		// Generate random values
		Map<Integer, Integer> counters = new HashMap<>();
		for (var i1 = min; i1 <= max; i1++) {
			Integer integer = i1;
			counters.put(integer, 0);
		}
		for (var i = 0; i <= 10000; i++) {
			var value = generator.getRandomValue(min, max);
			counters.put(value, counters.get(value) + 1);
		}

		// Test the distribution is reasonably uniform
		var epsilon = 0.1;
		for (var i = min; i <= max; i++) {
			var occurrences = counters.get(i);
			assertTrue("Not a reasonable amount of occurrences (" + occurrences + ") for value " + i,
					occurrences >= 2000 * (1 - epsilon) && occurrences <= 2000 * (1 + epsilon));
		}
	}

	@Test
	public void testBoundingFactoryMethodReturnsGeneratorWithCorrectValues() {
		var random = new Random();
		var generator = BoundedRandomGenerator.bound(random::nextDouble);

		double min = 1;
		double max = 5;
		for (var i = 0; i <= 10000; i++) {
			double value = generator.getRandomValue(min, max);
			assertTrue("Out of bounds value: " + value, value >= min && value <= max);
		}
	}

}
