package org.uma.jmetal.operator.crossover;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import org.uma.jmetal.operator.crossover.impl.PMXCrossover;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.problem.permutationproblem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

public class PMXCrossoverTest {

	@Test
	public void shouldJMetalRandomGeneratorNotBeUsedWhenCustomRandomGeneratorProvided() {
		// Configuration
		var crossoverProbability = 1.0;
		@SuppressWarnings("serial")
		PermutationProblem<PermutationSolution<Integer>> problem = new AbstractIntegerPermutationProblem() {

			@Override
			public PermutationSolution<Integer> evaluate(PermutationSolution<Integer> solution) {
				// Do nothing
				return solution ;
			}
			
			@Override
			public int getNumberOfVariables() {
				return 10;
			}

			@Override
			public int getLength() {
				return 10;
			}

		};
		List<PermutationSolution<Integer>> parentSolutions = new LinkedList<>();
		parentSolutions.add(problem.createSolution());
		parentSolutions.add(problem.createSolution());

		// Check configuration leads to use default generator by default
		final var defaultUses = new int[]{0};
		var defaultGenerator = JMetalRandom.getInstance();
		var auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

		var crossover1 = new PMXCrossover(crossoverProbability);
		crossover1.execute(parentSolutions);
		assertTrue("No use of the default generator", defaultUses[0] > 0);

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final var custom1Uses = new int[]{0};
		final var custom2Uses = new int[]{0};
		var crossover2 = new PMXCrossover(crossoverProbability, () -> {
			custom1Uses[0]++;
			return new Random().nextDouble();
		}, (a, b) -> {
			custom2Uses[0]++;
			return new Random().nextInt(b - a + 1) + a;
		});
		crossover2.execute(parentSolutions);
		assertTrue("Default random generator used", defaultUses[0] == 0);
		assertTrue("No use of the custom generator 1", custom1Uses[0] > 0);
		assertTrue("No use of the custom generator 2", custom2Uses[0] > 0);
	}
}
