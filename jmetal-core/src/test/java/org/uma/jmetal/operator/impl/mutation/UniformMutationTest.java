package org.uma.jmetal.operator.impl.mutation;

import org.junit.Test;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

import java.util.Random;

import static org.junit.Assert.assertTrue;

public class UniformMutationTest {

	@Test
	public void testJMetalRandomGeneratorNotUsedWhenCustomRandomGeneratorProvided() {
		// Configuration
		@SuppressWarnings("serial")
		DoubleProblem problem = new AbstractDoubleProblem() {

			@Override
			public void evaluate(DoubleSolution solution) {
				// Do nothing
			}

			@Override
			public int getNumberOfVariables() {
				return 100;
			}

			@Override
			public Double getLowerBound(int index) {
				return 0.0;
			}

			@Override
			public Double getUpperBound(int index) {
				return 1.0;
			}
		};
		DoubleSolution solution = problem.createSolution();

		// Check configuration leads to use default generator by default
		final int[] defaultUses = { 0 };
		JMetalRandom defaultGenerator = JMetalRandom.getInstance();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

		new UniformMutation(0.5, 0.5).execute(solution);
		assertTrue("No use of the default generator", defaultUses[0] > 0);

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final int[] customUses = { 0 };
		new UniformMutation(0.5, 0.5, () -> {
			customUses[0]++;
			return new Random().nextDouble();
		}).execute(solution);
		assertTrue("Default random generator used", defaultUses[0] == 0);
		assertTrue("No use of the custom generator", customUses[0] > 0);
	}

}
