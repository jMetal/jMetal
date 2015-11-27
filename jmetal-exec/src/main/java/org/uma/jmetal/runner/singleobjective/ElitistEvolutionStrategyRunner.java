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
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.EvolutionStrategyBuilder;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to configure and run an elitist (mu + lambda) evolution strategy. The target problem is
 * OneMax.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ElitistEvolutionStrategyRunner {
  /**
   * Usage: java org.uma.jmetal.runner.singleobjective.ElitistEvolutionStrategyRunner
   */
  public static void main(String[] args) throws Exception {
    Algorithm<BinarySolution> algorithm;
    BinaryProblem problem = new OneMax(512) ;

    MutationOperator<BinarySolution> mutationOperator =
        new BitFlipMutation(1.0 / problem.getNumberOfBits(0)) ;

    MutationOperator<DoubleSolution> m = new PolynomialMutation(0.0, 1.0) ;

    algorithm = new EvolutionStrategyBuilder<BinarySolution>(problem, mutationOperator,
        EvolutionStrategyBuilder.EvolutionStrategyVariant.ELITIST)
        .setMaxEvaluations(25000)
        .setMu(1)
        .setLambda(10)
        .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute() ;

    BinarySolution solution = algorithm.getResult() ;
    List<BinarySolution> population = new ArrayList<>(1) ;
    population.add(solution) ;

    long computingTime = algorithmRunner.getComputingTime() ;

    new SolutionListOutput(population)
        .setSeparator("\t")
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
  }
}
