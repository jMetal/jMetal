package org.uma.jmetal.operator.impl.crossover;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.util.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

import java.util.Arrays;
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

		List<Pair<Double, Double>> bounds = Arrays.asList(new ImmutablePair<>(0.0, 1.0)) ;

		DoubleSolution currentSolution = new DefaultDoubleSolution(bounds, 2) ;
		List<DoubleSolution> parentSolutions = new LinkedList<>();

		parentSolutions.add(new DefaultDoubleSolution(bounds, 2));
		parentSolutions.add(new DefaultDoubleSolution(bounds, 2));
		parentSolutions.add(new DefaultDoubleSolution(bounds, 2));
		parentSolutions.add(new DefaultDoubleSolution(bounds, 2));

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
		}, new RepairDoubleSolutionWithBoundValue());
		crossover2.setCurrentSolution(currentSolution);
		crossover2.execute(parentSolutions);
		assertTrue("Default random generator used", defaultUses[0] == 0);
		assertTrue("No use of the custom generator 1", custom1Uses[0] > 0);
		assertTrue("No use of the custom generator 2", custom2Uses[0] > 0);
	}

}
