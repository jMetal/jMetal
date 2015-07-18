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
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class to configure and run the NSGA-II algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIIRunner {
  /**
   * @param args Command line arguments.
   * @throws JMetalException
   * @throws FileNotFoundException
   * Usage: two options
   *        - org.uma.jmetal.runner.multiobjective.NSGAIIRunner
   *        - org.uma.jmetal.runner.multiobjective.NSGAIIRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws JMetalException, FileNotFoundException {
    Problem<DoubleSolution> problem;
    Algorithm<List<DoubleSolution>> algorithm;
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;
    String referenceParetoFront = null ;

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

    problem = (DoubleProblem) ProblemUtils.<DoubleSolution> loadProblem(problemName);

    double crossoverProbability = 0.9 ;
    double crossoverDistributionIndex = 20.0 ;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    selection = new BinaryTournamentSelection<DoubleSolution>(new RankingAndCrowdingDistanceComparator<DoubleSolution>());

    algorithm = new NSGAIIBuilder<DoubleSolution>(problem, crossover, mutation)
            .setSelectionOperator(selection)
            .setMaxIterations(250)
            .setPopulationSize(100)
            .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    List<DoubleSolution> population = algorithm.getResult() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    new SolutionSetOutput.Printer(population)
            .setSeparator("\t")
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
            .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

    /* Quality indicators section */

    if (referenceParetoFront != null) {
      Front referenceFront = new ArrayFront(referenceParetoFront);

      Hypervolume<List<DoubleSolution>> hypervolume =
          new Hypervolume(referenceFront);
      Epsilon<List<DoubleSolution>> epsilon =
          new Epsilon<List<DoubleSolution>>(referenceFront);
      GenerationalDistance<List<DoubleSolution>> gd =
          new GenerationalDistance<List<DoubleSolution>>(referenceFront);
      InvertedGenerationalDistance<List<DoubleSolution>> igd =
          new InvertedGenerationalDistance<List<DoubleSolution>>(referenceFront);
      InvertedGenerationalDistancePlus<List<DoubleSolution>> igdplus =
          new InvertedGenerationalDistancePlus<List<DoubleSolution>>(referenceFront) ;
      Spread<List<DoubleSolution>> spread =
          new Spread<List<DoubleSolution>>(referenceFront);
      R2<List<DoubleSolution>> r2 =
          new R2<List<DoubleSolution>>(referenceFront);
      ErrorRatio<List<DoubleSolution>> errorRatio =
          new ErrorRatio<List<DoubleSolution>>(referenceFront);
      SetCoverage setCoverage =
          new SetCoverage();

      Double hvValueNormalized = hypervolume.evaluate(population);
      Double hvValue = hypervolume.setNormalize(false).evaluate(population);
      Double epsilonValue = epsilon.evaluate(population);
      Double epsilonValueNormalized = epsilon.setNormalize(true).evaluate(population);
      Double gdValue = gd.evaluate(population);
      Double igdValueNormalized = igd.evaluate(population);
      Double igdValue = igd.setNormalize(false).evaluate(population);
      Double igdPlusValueNormalized = igdplus.evaluate(population);
      Double igdPlusValue = igdplus.setNormalize(false).evaluate(population);
      Double spreadValue = spread.evaluate(population);
      Double r2Value = r2.evaluate(population);
      Double errorRatioValue = errorRatio.evaluate(population);
      //Pair<Double, Double> setCoverageValues;
      //setCoverageValues = (Pair<Double, Double>) setCoverage.evaluate(
      //    new ImmutablePair(population, FrontUtils.convertFrontToSolutionList(referenceFront)));
      double setCoveragePopRef = setCoverage.evaluate(
          population, FrontUtils.convertFrontToSolutionList(referenceFront)) ;
      double setCoverageRefPop = setCoverage.evaluate(
          FrontUtils.convertFrontToSolutionList(referenceFront), population) ;

      JMetalLogger.logger.info("Hypervolume (N) : " + hvValueNormalized);
      JMetalLogger.logger.info("Hypervolume     : " + hvValue);
      JMetalLogger.logger.info("Epsilon         : " + epsilonValue);
      JMetalLogger.logger.info("Epsilon (N)     : " + epsilonValueNormalized);
      JMetalLogger.logger.info("GD              : " + gdValue);
      JMetalLogger.logger.info("IGD (N)         : " + igdValueNormalized);
      JMetalLogger.logger.info("IGD             : " + igdValue);
      JMetalLogger.logger.info("IGD+ (N)        : " + igdPlusValueNormalized);
      JMetalLogger.logger.info("IGD+            : " + igdPlusValue);
      JMetalLogger.logger.info("Spread          : " + spreadValue);
      JMetalLogger.logger.info("R2              : " + r2Value);
      JMetalLogger.logger.info("Error ratio     : " + errorRatioValue);
      JMetalLogger.logger.info("SC(pop, ref)    : " + setCoveragePopRef);
      JMetalLogger.logger.info("SC(ref, pop)    : " + setCoverageRefPop);
    }
  }
}
