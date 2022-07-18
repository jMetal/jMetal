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

package org.uma.jmetal.algorithm.examples.multiobjective;

import java.io.FileNotFoundException;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.microfame.MicroFAME;
import org.uma.jmetal.algorithm.multiobjective.microfame.util.HVTournamentSelection;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.NullCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.NullMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class to configure and run the Micro-FAME algorithm
 *
 * @author Alejandro Santiago <aurelio.santiago@upalt.edu.mx>
 */
public class MicroFAMERunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws JMetalException
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws JMetalException, FileNotFoundException {
    var referenceParetoFront = "";

    var evaluations = 175000; //
    var archiveSize = 100;
    String problemName = null;
    if (args.length == 0) {
      // problemName = "org.uma.jmetal.problem.multiobjective.cec2009Competition.UF6";
      // problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
      // problemName = "org.uma.jmetal.problem.multiobjective.lz09.LZ09F6";
      // problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1";
      problemName = "org.uma.jmetal.problem.multiobjective.lz09.LZ09F2";
      evaluations = 175000;
      referenceParetoFront = "resources/referenceFrontsCSV/LZ09_F2.csv";
    } else if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 3) {
      problemName = args[0];
      archiveSize = Integer.parseInt(args[1]);
      evaluations = Integer.parseInt(args[2]);
    }

    var problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

      CrossoverOperator<DoubleSolution> crossover = new NullCrossover<>();
      MutationOperator<DoubleSolution> mutation = new NullMutation<>();
      SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new HVTournamentSelection(5);
      Algorithm<List<DoubleSolution>> algorithm = new MicroFAME<>(problem, evaluations, archiveSize, crossover, mutation, selection);

    var algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    var population = algorithm.getResult();
    var computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }
  }
}
