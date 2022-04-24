package org.uma.jmetal.operator.localsearch;

import org.junit.Test;

public class BasicLocalSearchTest {

	@Test
	public void testJMetalRandomGeneratorNotUsedWhenCustomRandomGeneratorProvided() {
		// TODO. The tested class are to be replaced by another different
		/*
		// Configuration
		@SuppressWarnings("serial")
		IntegerProblem problem = new AbstractIntegerProblem() {

			@Override
			public void evaluate(IntegerSolution solution) {
				// Do nothing
			}
		};
		IntegerSolution solution = problem.createSolution();

		// Check configuration leads to use default generator by default
		final int[] defaultUses = { 0 };
		JMetalRandom defaultGenerator = JMetalRandom.getInstance();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

		new BasicLocalSearch<>(10, (x) -> x, (a, b) -> 0, problem).execute(solution);
		assertTrue("No use of the default generator", defaultUses[0] > 0);

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final int[] customUses = { 0 };
		new BasicLocalSearch<>(10, (x) -> x, (a, b) -> 0, problem, () -> {
			customUses[0]++;
			return new Random().nextDouble();
		}).execute(solution);
		assertTrue("Default random generator used", defaultUses[0] == 0);
		assertTrue("No use of the custom generator", customUses[0] > 0);
		*/
	}
}
