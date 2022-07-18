package org.uma.jmetal.algorithm.examples.singleobjective;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.singleobjective.coralreefsoptimization.CoralReefsOptimizationBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.List;

/**
 * Class to configure and run a coral reefs optimization algorithm. The target
 * problem is OneMax.
 *
 * @author Inacio Medeiros <inaciogmedeiros@gmail.com>
 */
public class CoralReefsOptimizationRunner {
	/**
	 * Usage: java
	 * org.uma.jmetal.runner.singleobjective.CoralReefsOptimizationRunner
	 */
	public static void main(String[] args) throws Exception {
		BinaryProblem problem = new OneMax(512);

		@NotNull CrossoverOperator<BinarySolution> crossoverOperator = new SinglePointCrossover(
				0.9);
		@NotNull MutationOperator<BinarySolution> mutationOperator = new BitFlipMutation(
				1.0 / problem.getBitsFromVariable(0));
		@NotNull SelectionOperator<List<BinarySolution>, BinarySolution> selectionOperator = new BinaryTournamentSelection<BinarySolution>();

		Algorithm<List<BinarySolution>> algorithm = new CoralReefsOptimizationBuilder<BinarySolution>(problem,
				selectionOperator, crossoverOperator, mutationOperator)
				.setM(10).setN(10).setRho(0.6).setFbs(0.9).setFbr(0.1)
				.setFa(0.1).setPd(0.1).setAttemptsToSettle(3)
				.setComparator(new ObjectiveComparator<BinarySolution>(0))
				.build();

		var algorithmRunner = new AlgorithmRunner.Executor(
				algorithm).execute();

		var population = algorithm.getResult();

		var computingTime = algorithmRunner.getComputingTime();

		new SolutionListOutput(population)
				.setVarFileOutputContext(
						new DefaultFileOutputContext("VAR.tsv"))
				.setFunFileOutputContext(
						new DefaultFileOutputContext("FUN.tsv")).print();

		JMetalLogger.logger.info("Total execution time: " + computingTime
				+ "ms");
		JMetalLogger.logger
				.info("Objectives values have been written to file FUN.tsv");
		JMetalLogger.logger
				.info("Variables values have been written to file VAR.tsv");

	}
}
