package org.uma.jmetal.operator.impl.crossover;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Ignore;
import org.junit.Test;
import org.uma.jmetal.operator.crossover.impl.IntegerSBXCrossover;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.solution.integersolution.impl.DefaultIntegerSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class IntegerSBXCrossoverTest {
	@Ignore
	@Test
	public void testJMetalRandomGeneratorNotUsedWhenCustomRandomGeneratorProvided() {
		// Configuration
		@SuppressWarnings("serial")
		List<IntegerSolution> parents = new LinkedList<>();

		List<Pair<Integer, Integer>> bounds = Arrays.asList(new ImmutablePair<>(0, 1)) ;

		parents.add(new DefaultIntegerSolution(bounds, 2));
		parents.add(new DefaultIntegerSolution(bounds, 2));

		// Check configuration leads to use default generator by default
		final int[] defaultUses = { 0 };
		JMetalRandom defaultGenerator = JMetalRandom.getInstance();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

		new IntegerSBXCrossover(0.5, 0.5).execute(parents);
		assertTrue("No use of the default generator", defaultUses[0] > 0);

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final int[] customUses = { 0 };
		new IntegerSBXCrossover(0.5, 0.5, () -> {
			customUses[0]++;
			return new Random().nextDouble();
		}).execute(parents);
		assertTrue("Default random generator used", defaultUses[0] == 0);
		assertTrue("No use of the custom generator", customUses[0] > 0);
	}

}
