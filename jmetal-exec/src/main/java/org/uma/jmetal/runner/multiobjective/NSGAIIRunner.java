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
import org.uma.jmetal.runner.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class to configure and run the NSGA-II algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIIRunner extends AbstractAlgorithmRunner {
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

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (referenceParetoFront != null) {
      printQualityIndicators(population, referenceParetoFront) ;
    }

    /*
    new SolutionSetOutput.Printer(population)
        .setSeparator("\t")
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
*/
    /* Quality indicators section */
/*
    if (referenceParetoFront != null) {
      Front referenceFront = new ArrayFront(referenceParetoFront);
      FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront) ;

      Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront) ;
      Front normalizedFront = frontNormalizer.normalize(new ArrayFront(population)) ;
      List<DoubleSolution> normalizedPopulation = FrontUtils.convertFrontToSolutionList(
          normalizedFront) ;

      JMetalLogger.logger.info("Hypervolume (N) : " +
          new Hypervolume<List<DoubleSolution>>(normalizedReferenceFront).evaluate(normalizedPopulation));
      JMetalLogger.logger.info("Hypervolume     : " +
          new Hypervolume<List<DoubleSolution>>(referenceFront).evaluate(population));
      JMetalLogger.logger.info("Epsilon (N)     : " +
          new Epsilon<List<DoubleSolution>>(normalizedReferenceFront).evaluate(normalizedPopulation));
      JMetalLogger.logger.info("Epsilon         : " +
          new Epsilon<List<DoubleSolution>>(referenceFront).evaluate(population));
      JMetalLogger.logger.info("GD (N)          : " +
      new GenerationalDistance<List<DoubleSolution>>(normalizedReferenceFront).evaluate(normalizedPopulation));
      JMetalLogger.logger.info("GD              : " +
          new GenerationalDistance<List<DoubleSolution>>(referenceFront).evaluate(population));
      JMetalLogger.logger.info("IGD (N)         : " +
          new InvertedGenerationalDistance<List<DoubleSolution>>(normalizedReferenceFront).evaluate(normalizedPopulation));
      JMetalLogger.logger.info("IGD             : " +
          new InvertedGenerationalDistance<List<DoubleSolution>>(referenceParetoFront).evaluate(population));
      JMetalLogger.logger.info("IGD+ (N)        : " +
          new InvertedGenerationalDistancePlus<List<DoubleSolution>>(normalizedReferenceFront).evaluate(normalizedPopulation));
      JMetalLogger.logger.info("IGD+            : " +
          new InvertedGenerationalDistancePlus<List<DoubleSolution>>(referenceFront).evaluate(population));
      JMetalLogger.logger.info("Spread (N)      : " +
          new Spread<List<DoubleSolution>>(normalizedReferenceFront).evaluate(normalizedPopulation));
      JMetalLogger.logger.info("Spread          : " +
          new Spread<List<DoubleSolution>>(referenceFront).evaluate(population));
      JMetalLogger.logger.info("R2 (N)          : " +
          new R2<List<DoubleSolution>>(normalizedReferenceFront).evaluate(normalizedPopulation));
      JMetalLogger.logger.info("R2              : " +
          new R2<List<DoubleSolution>>(referenceFront).evaluate(population));
      JMetalLogger.logger.info("Error ratio     : " +
          new ErrorRatio<List<DoubleSolution>>(referenceFront).evaluate(population));
      //JMetalLogger.logger.info("SC(pop, ref)    : " +
      //new SetCoverage().evaluate());
      //JMetalLogger.logger.info("SC(ref, pop)    : " + setCoverageRefPop);
    }
    */
  }
}
