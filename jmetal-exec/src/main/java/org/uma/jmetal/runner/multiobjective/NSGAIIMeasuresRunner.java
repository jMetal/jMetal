//  NSGAIIRunner.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
//
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
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIMeasures;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIMeasuresBuilder;
import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.CountingMeasure;
import org.uma.jmetal.measure.impl.DurationMeasure;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class to configure and run the NSGA-II algorithm
 */
public class NSGAIIMeasuresRunner {
  /**
   * @param args Command line arguments.
   * @throws java.io.IOException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * Usage: three options
   *        - org.uma.jmetal.runner.multiobjective.NSGAIIRunner
   *        - org.uma.jmetal.runner.multiobjective.NSGAIIRunner problemName
   *        - org.uma.jmetal.runner.multiobjective.NSGAIIRunner problemName paretoFrontFile
   */
  public static void main(String[] args) throws JMetalException, InterruptedException {
    Problem problem;
    Algorithm<List<Solution>> algorithm;
    CrossoverOperator crossover;
    MutationOperator mutation;
    SelectionOperator selection;

    String problemName ;
    if (args.length == 1) {
      problemName = args[0] ;
      problem = ProblemUtils.loadProblem(problemName);
    } else {
      problem = new ZDT1(3000);
    }

    double crossoverProbability = 0.9 ;
    double crossoverDistributionIndex = 20.0 ;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    selection = new BinaryTournamentSelection(new RankingAndCrowdingDistanceComparator());

    int maxIterations = 250 ;
    int populationSize = 100 ;

    algorithm = new NSGAIIMeasuresBuilder(problem)
        .setCrossoverOperator(crossover)
        .setMutationOperator(mutation)
        .setSelectionOperator(selection)
        .setMaxIterations(maxIterations)
        .setPopulationSize(populationSize)
        .build() ;

    /* Measure management */
    MeasureManager measureManager = ((NSGAIIMeasures)algorithm).getMeasureManager() ;

    CountingMeasure iteration =
        (CountingMeasure) measureManager.<Long>getPullMeasure("currentIteration");
    DurationMeasure currentComputingTime =
        (DurationMeasure) measureManager.<Long>getPullMeasure("currentExecutionTime");
    BasicMeasure<Integer> nonDominatedSolutions =
        (BasicMeasure<Integer>) measureManager.<Integer>getPullMeasure("numberOfNonDominatedSolutionsInPopulation");

    BasicMeasure<List<Solution>> solutionListMeasure =
        (BasicMeasure) measureManager.getPushMeasure("currentPopulation");
    CountingMeasure iteration2 =
        (CountingMeasure) measureManager.<Long>getPushMeasure("currentIteration");

    solutionListMeasure.register(new Listener());
    iteration2.register(new Listener2());
    /* End of measure management */

    Thread algorithmThread = new Thread(algorithm) ;
    algorithmThread.start();

    /* Using the measures */
    int i = 0 ;
    while(iteration.get() < maxIterations) {
      TimeUnit.SECONDS.sleep(5);
      System.out.println("Iteration (" + i + ")                       : " + iteration.get()) ;
      System.out.println("Computing time (" + i + ")                  : " + currentComputingTime.get()) ;
      System.out.println("Number of Nondominated solutions (" + i + "): " + nonDominatedSolutions.get()) ;
      i++ ;
    }

    algorithmThread.join();

    List<Solution> population = algorithm.getResult() ;
    long computingTime = currentComputingTime.get() ;

    //long computingTime = algorithmRunner.getComputingTime() ;

    new SolutionSetOutput.Printer(population)
        .setSeparator("\t")
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
  }


  private static class Listener implements MeasureListener<List<Solution>> {
    private int counter = 0 ;

    @Override synchronized public void measureGenerated(List<Solution> solutions) {
      if ((counter % 10 == 0)) {
        System.out.println("PUSH MEASURE. Counter = " + counter+ " First solution: " + solutions.get(0)) ;
      }
      counter ++ ;
    }
  }

  private static class Listener2 implements MeasureListener<Long> {
    @Override synchronized public void measureGenerated(Long value) {
      if ((value % 50 == 0)) {
        System.out.println("PUSH MEASURE. Iteration: " + value) ;
      }
    }
  }
}
