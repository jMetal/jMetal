package org.uma.jmetal.util.pseudorandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.uma.jmetal.util.pseudorandom.impl.JavaRandomGenerator;

public class RandomGeneratorTest {

	@Test
	public void testFilteredGeneratorGeneratesCorrectValues() {
		Random random = new Random();
		RandomGenerator<Integer> generator = RandomGenerator.filter(() -> random.nextInt(5), (i) -> i != 2);

		Set<Integer> generated = IntStream.range(0, 10000).mapToObj(i -> generator.getRandomValue()).collect(Collectors.toSet());

        assertTrue(generated.contains(0));
		assertTrue(generated.contains(1));
		assertFalse(generated.contains(2));
		assertTrue(generated.contains(3));
		assertTrue(generated.contains(4));
	}

	@Test
	public void testArrayGeneratorGeneratesAllValues() {
		JavaRandomGenerator random = new JavaRandomGenerator();
		BoundedRandomGenerator<Integer> indexSelector = (a, b) -> random.nextInt(a, b);
		String[] values = { "a", "b", "c" };
		RandomGenerator<String> generator = RandomGenerator.forArray(indexSelector, values);

		Set<String> generated = IntStream.range(0, 10000).mapToObj(i -> generator.getRandomValue()).collect(Collectors.toSet());

        assertTrue(generated.containsAll(Arrays.asList(values)));
		assertEquals(values.length, generated.size());
	}

	@Test
	public void testCollectionGeneratorGeneratesAllValues() {
		JavaRandomGenerator random = new JavaRandomGenerator();
		BoundedRandomGenerator<Integer> indexSelector = (a, b) -> random.nextInt(a, b);
		Collection<String> values = Arrays.asList("a", "b", "c");
		RandomGenerator<String> generator = RandomGenerator.forCollection(indexSelector, values);

		Set<String> generated = IntStream.range(0, 10000).mapToObj(i -> generator.getRandomValue()).collect(Collectors.toSet());

        assertTrue(generated.containsAll(values));
		assertEquals(values.size(), generated.size());
	}

	enum EnumValues {
		VAL1, VAL2, VAL3
	}

	@Test
	public void testEnumGeneratorGeneratesAllValues() {
		JavaRandomGenerator random = new JavaRandomGenerator();
		BoundedRandomGenerator<Integer> indexSelector = (a, b) -> random.nextInt(a, b);
		RandomGenerator<EnumValues> generator = RandomGenerator.forEnum(indexSelector, EnumValues.class);

		Set<EnumValues> generated = IntStream.range(0, 10000).mapToObj(i -> generator.getRandomValue()).collect(Collectors.toSet());

        assertTrue(generated.containsAll(Arrays.asList(EnumValues.values())));
		assertEquals(EnumValues.values().length, generated.size());
	}

}
