package org.uma.jmetal.operator.crossover;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.crossover.impl.IntegerSBXCrossover;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.solution.integersolution.impl.DefaultIntegerSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

public class IntegerSBXCrossoverTest {
	@Disabled
	@Test
	public void testJMetalRandomGeneratorNotUsedWhenCustomRandomGeneratorProvided() {
		// Configuration
		List<IntegerSolution> parents = new LinkedList<>();

		List<Bounds<Integer>> bounds = Arrays.asList(Bounds.create(0, 1)) ;

		parents.add(new DefaultIntegerSolution(bounds, 2, 0));
		parents.add(new DefaultIntegerSolution(bounds, 2, 0));

		// Check configuration leads to use default generator by default
		final int[] defaultUses = { 0 };
		JMetalRandom defaultGenerator = JMetalRandom.getInstance();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

		new IntegerSBXCrossover(0.5, 0.5).execute(parents);
		assertTrue(defaultUses[0] > 0, "No use of the default generator");

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final int[] customUses = { 0 };
		new IntegerSBXCrossover(0.5, 0.5, () -> {
			customUses[0]++;
			return new Random().nextDouble();
		}).execute(parents);
		assertTrue(defaultUses[0] == 0, "Default random generator used");
		assertTrue(customUses[0] > 0, "No use of the custom generator");
	}

}
