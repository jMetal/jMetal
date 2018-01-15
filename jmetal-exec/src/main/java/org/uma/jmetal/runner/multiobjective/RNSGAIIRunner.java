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
import org.uma.jmetal.algorithm.multiobjective.rnsgaii.RNSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to configure and run the R-NSGA-II algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Cristobal Barba <cbarba@lcc.uma.es>
 */
public class RNSGAIIRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws JMetalException
   * @throws FileNotFoundException
   * Invoking command:
    java org.uma.jmetal.runner.multiobjective.RNSGAIIRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws JMetalException, FileNotFoundException {
    Problem<DoubleSolution> problem;
    Algorithm<List<DoubleSolution>> algorithm;
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;
    String referenceParetoFront = "" ;

    String problemName ;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
      referenceParetoFront = "jmetal-problem/src/test/resources/pareto_fronts/ZDT1.pf" ;
    }

    problem =new ZDT1();//  ProblemUtils.<DoubleSolution> loadProblem(problemName);//Tanaka();//

    double crossoverProbability = 0.9 ;
    double crossoverDistributionIndex = 20.0 ;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    selection = new BinaryTournamentSelection<DoubleSolution>(
        new RankingAndCrowdingDistanceComparator<DoubleSolution>());

    List<Double> referencePoint = new ArrayList<>() ;

    /*referencePoint.add(0.0) ;
    referencePoint.add(1.0) ;
    referencePoint.add(1.0) ;
    referencePoint.add(0.0) ;
    referencePoint.add(0.5) ;
    referencePoint.add(0.5) ;
    referencePoint.add(0.2) ;
    referencePoint.add(0.8) ;
    referencePoint.add(0.8) ;
    referencePoint.add(0.2) ;*/
    //Example fig 2 paper Deb
  /*  referencePoint.add(0.2) ;
    referencePoint.add(0.4) ;
    referencePoint.add(0.8) ;
    referencePoint.add(0.4) ;*/
    //Example fig 3 paper Deb
    referencePoint.add(0.1) ;
    referencePoint.add(0.6) ;

    referencePoint.add(0.3) ;
    referencePoint.add(0.6) ;

    referencePoint.add(0.5) ;
    referencePoint.add(0.2) ;

    referencePoint.add(0.7) ;
    referencePoint.add(0.2) ;

    referencePoint.add(0.9) ;
    referencePoint.add(0.0) ;
    /*referencePoint.add(0.1) ;
    referencePoint.add(1.0) ;
    referencePoint.add(1.0) ;
    referencePoint.add(0.0) ;

    referencePoint.add(0.5) ;
    referencePoint.add(0.8);
    referencePoint.add(0.8) ;
    referencePoint.add(0.6) ;*/
    //referencePoint.add(0.0) ;
    //referencePoint.add(1.0);

    //referencePoint.add(1.0) ;
    //referencePoint.add(0.6);
    //referencePoint.add(0.4) ;
    //referencePoint.add(0.8);

    double epsilon= 0.0045;


    algorithm = new RNSGAIIBuilder<DoubleSolution>(problem, crossover, mutation, referencePoint,epsilon)
        .setSelectionOperator(selection)
        .setMaxEvaluations(25000)
        .setPopulationSize(100)
        .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute() ;

    List<DoubleSolution> population = algorithm.getResult() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }
  }
}
