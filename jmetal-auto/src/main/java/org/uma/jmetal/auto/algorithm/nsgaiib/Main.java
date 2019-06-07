package org.uma.jmetal.auto.algorithm.nsgaiib;

import static org.uma.jmetal.auto.algorithm.nsgaiib.NSGAII.Step.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.uma.jmetal.auto.algorithm.nsgaiib.NSGAII.Step;
import org.uma.jmetal.solution.Solution;

public class Main {

	public static void main(String[] args) {
		System.out.println("Create algorithm");
		NSGAII<Solution<Object>> algorithm = AlgorithmCreator.NSGA2.create(args);

		System.out.println("Setup algorithm observation");
		algorithm.registerObserver(new NSGAII.Observer<Solution<Object>>() {
			int evaluations = 0;

			@Override
			public void updateNumberOfEvaluations(int evaluations) {
				this.evaluations = evaluations;
			}

			int creations = 0;

			@Override
			public void createSolution(Solution<Object> solution) {
				creations++;
			}

			List<Solution<Object>> lastPopulation = Collections.emptyList();
			int replacements = 0;

			@Override
			public void updatePopulation(List<Solution<Object>> population) {
				replacements += population.stream().filter(solution -> !lastPopulation.contains(solution)).count();
				lastPopulation = population;
			}

			int iterations = 0;

			@Override
			public void updateStep(Step step) {
				if (step == DONE) {
					System.out.print(step + " ");
				}
				if (step == MATE || step == DONE) {
					// Iteration summary
					System.out.println("= "
							+ creations + " creations, "       // total nb of creations
							+ evaluations + " evaluations, "   // total nb of evaluations
							+ replacements + " replacements"); // replacements of iteration
					replacements = 0;                          // reset replacements
				}
				if (step == MATE) {
					System.out.print(++iterations + ": ");
				}
				if (step != DONE) {
					System.out.print(step + " ");
				}
			}
		});

		System.out.println("Run algorithm");
		algorithm.run();

		System.out.println("Result:");
		algorithm.getResult().forEach(solution -> {
			System.out.println("  Solution:");
			System.out.println("    Variables : " + solution.getVariables());
			System.out.println("    Objectives: " + toList(solution.getObjectives()));
		});
	}

	private static List<Double> toList(double[] objectives) {
		return DoubleStream.of(objectives).mapToObj(x -> x).collect(Collectors.toList());
	}
}
