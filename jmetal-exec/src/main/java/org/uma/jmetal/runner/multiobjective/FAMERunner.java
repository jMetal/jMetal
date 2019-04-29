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

package org.uma.jmetal.runner.multiobjective;


import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.fame.FAME;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.SpatialSpreadDeviationSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.*;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class to configure and run the FAME algorithm, described in:
 * A. Santiago, B. Dorronsoro, A.J. Nebro, J.J. Durillo, O. Castillo, H.J. Fraire
 * A Novel Multi-Objective Evolutionary Algorithm with Fuzzy Logic Based Adaptive Selection
 * of Operators: FAME.
 * Information Sciences. Volume 471, January 2019, Pages 233-251.
 * DOI: https://doi.org/10.1016/j.ins.2018.09.005
 *
 * @author Alejandro Santiago <aurelio.santiago@upalt.edu.mx>
 */
public class FAMERunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws JMetalException
   * @throws FileNotFoundException
   * Invoking command:
    java org.uma.jmetal.runner.multiobjective.FAMERunner problemName
   */
  public static void main(String[] args) throws JMetalException, FileNotFoundException {
    Problem<DoubleSolution> problem;
    Algorithm<List<DoubleSolution>> algorithm;
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

    String problemName = null ;
    if(args.length==0){
      problemName = "org.uma.jmetal.problem.multiobjective.glt.GLT1";
    }else if (args.length == 1) {
      problemName = args[0];
    } 

    problem = ProblemUtils.<DoubleSolution> loadProblem(problemName);

    selection = new SpatialSpreadDeviationSelection<DoubleSolution>(5);

    int populationSize=25 ;
    int archiveSize=200 ;
    int maxEvaluations=45000;

    algorithm = new FAME<>(problem,
            populationSize,
            archiveSize,
            maxEvaluations,
            selection,
            new SequentialSolutionListEvaluator<>()) ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute() ;

    List<DoubleSolution> population = algorithm.getResult() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
  }
}
