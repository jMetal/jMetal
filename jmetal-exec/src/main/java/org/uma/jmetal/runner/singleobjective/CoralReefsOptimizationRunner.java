//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.runner.singleobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.coralreefsoptimization.CoralReefsOptimizationBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SinglePointCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.AlgorithmRunner;
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
		Algorithm<List<BinarySolution>> algorithm;
		BinaryProblem problem = new OneMax(512);

		CrossoverOperator<BinarySolution> crossoverOperator = new SinglePointCrossover(
				0.9);
		MutationOperator<BinarySolution> mutationOperator = new BitFlipMutation(
				1.0 / problem.getNumberOfBits(0));
		SelectionOperator<List<BinarySolution>, BinarySolution> selectionOperator = new BinaryTournamentSelection<BinarySolution>();

		algorithm = new CoralReefsOptimizationBuilder<BinarySolution>(problem,
				selectionOperator, crossoverOperator, mutationOperator)
				.setM(10).setN(10).setRho(0.6).setFbs(0.9).setFbr(0.1)
				.setFa(0.1).setPd(0.1).setAttemptsToSettle(3)
				.setComparator(new ObjectiveComparator<BinarySolution>(0))
				.build();

		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(
				algorithm).execute();

		List<BinarySolution> population = algorithm.getResult();
		
		long computingTime = algorithmRunner.getComputingTime();

		new SolutionListOutput(population)
				.setSeparator("\t")
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
