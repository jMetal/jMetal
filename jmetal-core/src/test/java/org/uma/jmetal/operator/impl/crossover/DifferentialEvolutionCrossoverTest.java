package org.uma.jmetal.operator.impl.crossover;

import org.junit.Test;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class DifferentialEvolutionCrossoverTest {

	@Test
	public void shouldJMetalRandomGeneratorNotBeUsedWhenCustomRandomGeneratorProvided() {
		// Configuration
		double cr = 0.5;
		double f = 0.5;
		String variant = "rand/1/bin";
		@SuppressWarnings("serial")
		DoubleProblem problem = new AbstractDoubleProblem() {

			@Override
			public void evaluate(DoubleSolution solution) {
				// Do nothing
			}
			
			@Override
			public int getNumberOfVariables() {
				return 5;
			}
			
			@Override
			public Double getLowerBound(int index) {
				return 0.0;
			}
			
			@Override
			public Double getUpperBound(int index) {
				return 10.0;
			}

		};
		DoubleSolution currentSolution = problem.createSolution();
		List<DoubleSolution> parentSolutions = new LinkedList<>();
		parentSolutions.add(problem.createSolution());
		parentSolutions.add(problem.createSolution());
		parentSolutions.add(problem.createSolution());
		parentSolutions.add(problem.createSolution());

		// Check configuration leads to use default generator by default
		final int[] defaultUses = { 0 };
		JMetalRandom defaultGenerator = JMetalRandom.getInstance();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

		DifferentialEvolutionCrossover crossover1 = new DifferentialEvolutionCrossover(cr, f, variant);
		crossover1.setCurrentSolution(currentSolution);
		crossover1.execute(parentSolutions);
		assertTrue("No use of the default generator", defaultUses[0] > 0);

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final int[] custom1Uses = { 0 };
		final int[] custom2Uses = { 0 };
		DifferentialEvolutionCrossover crossover2 = new DifferentialEvolutionCrossover(cr, f, variant, (a, b) -> {
			custom1Uses[0]++;
			return new Random().nextInt(b - a + 1) + a;
		}, (a, b) -> {
			custom2Uses[0]++;
			return new Random().nextDouble() * (b - a) + a;
		});
		crossover2.setCurrentSolution(currentSolution);
		crossover2.execute(parentSolutions);
		assertTrue("Default random generator used", defaultUses[0] == 0);
		assertTrue("No use of the custom generator 1", custom1Uses[0] > 0);
		assertTrue("No use of the custom generator 2", custom2Uses[0] > 0);
	}

}
