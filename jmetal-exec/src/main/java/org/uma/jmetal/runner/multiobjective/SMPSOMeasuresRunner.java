package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOMeasures;
import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.CountingMeasure;
import org.uma.jmetal.measure.impl.DurationMeasure;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class to configure and run the NSGA-II algorithm (variant with measures)
 */
public class SMPSOMeasuresRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws SecurityException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.NSGAIIMeasuresRunner problemName [referenceFront]
   */
  public static void main(String[] args)
      throws JMetalException, InterruptedException, FileNotFoundException {
    DoubleProblem problem;
    Algorithm<List<DoubleSolution>> algorithm;
    MutationOperator<DoubleSolution> mutation;

    String referenceParetoFront = "" ;

    String problemName ;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
      referenceParetoFront = "jmetal-problem/src/test/resources/pareto_fronts/ZDT4.pf" ;
    }

    problem = (DoubleProblem) ProblemUtils.<DoubleSolution> loadProblem(problemName);

    BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(100) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    int maxIterations = 250 ;
    int swarmSize = 100 ;

    algorithm = new SMPSOBuilder(problem, archive)
        .setMutation(mutation)
        .setMaxIterations(maxIterations)
        .setSwarmSize(swarmSize)
        .setRandomGenerator(new MersenneTwisterGenerator())
        .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
        .setVariant(SMPSOBuilder.SMPSOVariant.Measures)
        .build();

    /* Measure management */
    MeasureManager measureManager = ((SMPSOMeasures)algorithm).getMeasureManager() ;

    CountingMeasure currentIteration =
        (CountingMeasure) measureManager.<Long>getPullMeasure("currentIteration");
    DurationMeasure currentComputingTime =
        (DurationMeasure) measureManager.<Long>getPullMeasure("currentExecutionTime");

    BasicMeasure<List<DoubleSolution>> solutionListMeasure =
        (BasicMeasure<List<DoubleSolution>>) measureManager.<List<DoubleSolution>> getPushMeasure("currentPopulation");
    CountingMeasure iteration2 =
        (CountingMeasure) measureManager.<Long>getPushMeasure("currentIteration");

    solutionListMeasure.register(new Listener());
    iteration2.register(new Listener2());
    /* End of measure management */

    Thread algorithmThread = new Thread(algorithm) ;
    algorithmThread.start();

    /* Using the measures */
    int i = 0 ;
    while(currentIteration.get() < maxIterations) {
      TimeUnit.SECONDS.sleep(1);
      System.out.println("Iteration (" + i + ")            : " + currentIteration.get()) ;
      System.out.println("Computing time (" + i + ")       : " + currentComputingTime.get()) ;
      i++ ;
    }

    algorithmThread.join();

    List<DoubleSolution> population = algorithm.getResult() ;
    long computingTime = currentComputingTime.get() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }
  }

  private static class Listener implements MeasureListener<List<DoubleSolution>> {
    private int counter = 0 ;

    @Override synchronized public void measureGenerated(List<DoubleSolution> solutions) {
      if ((counter % 100 == 0)) {
        System.out.println("PUSH MEASURE. Counter = " + counter+ " First solution: " + solutions.get(0).getVariableValue(0)) ;
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
