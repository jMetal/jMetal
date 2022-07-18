package org.uma.jmetal.algorithm.examples.multiobjective.smpso;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOMeasures;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.measure.MeasureListener;
import org.uma.jmetal.util.measure.MeasureManager;
import org.uma.jmetal.util.measure.impl.BasicMeasure;
import org.uma.jmetal.util.measure.impl.CountingMeasure;
import org.uma.jmetal.util.measure.impl.DurationMeasure;
import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;

/**
 * Class to configure and run the NSGA-II algorithm (variant with measures)
 */
public class SMPSOMeasuresRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws SecurityException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.nsgaii.NSGAIIMeasuresRunner problemName [referenceFront]
   */
  public static void main(String[] args)
      throws JMetalException, InterruptedException, FileNotFoundException {

    var referenceParetoFront = "" ;

    String problemName ;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
      referenceParetoFront = "resources/referenceFronts/ZDT4.csv" ;
    }

    var problem = (DoubleProblem) ProblemFactory.<DoubleSolution>loadProblem(problemName);

    @NotNull BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(100) ;

    var mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    var mutationDistributionIndex = 20.0 ;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    var maxIterations = 250 ;
    var swarmSize = 100 ;

    Algorithm<List<DoubleSolution>> algorithm = new SMPSOBuilder(problem, archive)
            .setMutation(mutation)
            .setMaxIterations(maxIterations)
            .setSwarmSize(swarmSize)
            .setRandomGenerator(new MersenneTwisterGenerator())
            .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
            .setVariant(SMPSOBuilder.SMPSOVariant.Measures)
            .build();

    /* Measure management */
    var measureManager = ((SMPSOMeasures)algorithm).getMeasureManager() ;

    var currentIteration =
        (CountingMeasure) measureManager.<Long>getPullMeasure("currentIteration");
    var currentComputingTime =
        (DurationMeasure) measureManager.<Long>getPullMeasure("currentExecutionTime");

    var solutionListMeasure =
        (BasicMeasure<List<DoubleSolution>>) measureManager.<List<DoubleSolution>> getPushMeasure("currentPopulation");
    var iteration2 =
        (CountingMeasure) measureManager.<Long>getPushMeasure("currentIteration");

    solutionListMeasure.register(new Listener());
    iteration2.register(new Listener2());
    /* End of measure management */

    var algorithmThread = new Thread(algorithm) ;
    algorithmThread.start();

    /* Using the measures */
    var i = 0 ;
    while(currentIteration.get() < maxIterations) {
      TimeUnit.SECONDS.sleep(1);
      System.out.println("Iteration (" + i + ")            : " + currentIteration.get()) ;
      System.out.println("Computing time (" + i + ")       : " + currentComputingTime.get()) ;
      i++ ;
    }

    algorithmThread.join();

    var population = algorithm.getResult() ;
    long computingTime = currentComputingTime.get() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }
  }

  private static class Listener implements MeasureListener<List<DoubleSolution>> {
    private int counter = 0 ;

    @Override synchronized public void measureGenerated(@NotNull List<DoubleSolution> solutions) {
      if ((counter % 100 == 0)) {
        System.out.println("PUSH MEASURE. Counter = " + counter+ " First solution: " + solutions.get(0).variables().get(0)) ;
      }
      counter ++ ;
    }
  }

  private static class Listener2 implements MeasureListener<Long> {
    @Override synchronized public void measureGenerated(Long value) {
      if ((value % 10 == 0)) {
        System.out.println("PUSH MEASURE. Iteration: " + value) ;
      }
    }
  }
}
