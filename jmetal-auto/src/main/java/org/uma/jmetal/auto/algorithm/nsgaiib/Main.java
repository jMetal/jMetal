package org.uma.jmetal.auto.algorithm.nsgaiib;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.uma.jmetal.solution.Solution;

public class Main {

	public static void main(String[] args) {
		System.out.println("Create algorithm");
		NSGAII<Solution<Object>> algorithm = AlgorithmCreator.NSGA2.create(args);

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
