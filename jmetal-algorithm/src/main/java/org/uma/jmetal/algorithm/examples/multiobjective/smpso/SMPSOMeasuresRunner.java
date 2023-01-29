package org.uma.jmetal.algorithm.examples.multiobjective.smpso;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOMeasures;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.qualityindicator.QualityIndicatorUtils;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
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
   */
  public static void main(String[] args) throws InterruptedException, IOException {
    String problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT4";
    String referenceParetoFront = "resources/referenceFrontsCSV/ZDT4.csv";

    DoubleProblem problem = (DoubleProblem) ProblemFactory.<DoubleSolution>loadProblem(problemName);

    BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(100);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int maxIterations = 250;
    int swarmSize = 100;

    var algorithm = new SMPSOBuilder(problem, archive)
        .setMutation(mutation)
        .setMaxIterations(maxIterations)
        .setSwarmSize(swarmSize)
        .setRandomGenerator(new MersenneTwisterGenerator())
        .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
        .setVariant(SMPSOBuilder.SMPSOVariant.Measures)
        .build();

    /* Measure management */
    MeasureManager measureManager = ((SMPSOMeasures) algorithm).getMeasureManager();

    CountingMeasure currentIteration =
        (CountingMeasure) measureManager.<Long>getPullMeasure("currentIteration");
    DurationMeasure currentComputingTime =
        (DurationMeasure) measureManager.<Long>getPullMeasure("currentExecutionTime");

    BasicMeasure<List<DoubleSolution>> solutionListMeasure =
        (BasicMeasure<List<DoubleSolution>>) measureManager.<List<DoubleSolution>>getPushMeasure(
            "currentPopulation");
    CountingMeasure iteration2 =
        (CountingMeasure) measureManager.<Long>getPushMeasure("currentIteration");

    solutionListMeasure.register(new Listener());
    iteration2.register(new Listener2());
    /* End of measure management */

    Thread algorithmThread = new Thread(algorithm);
    algorithmThread.start();

    /* Using the measures */
    int i = 0;
    while (currentIteration.get() < maxIterations) {
      TimeUnit.SECONDS.sleep(1);
      System.out.println("Iteration (" + i + ")            : " + currentIteration.get());
      System.out.println("Computing time (" + i + ")       : " + currentComputingTime.get());
      i++;
    }

    algorithmThread.join();

    List<DoubleSolution> population = algorithm.result();
    long computingTime = currentComputingTime.get();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    QualityIndicatorUtils.printQualityIndicators(
        SolutionListUtils.getMatrixWithObjectiveValues(population),
        VectorUtils.readVectors(referenceParetoFront, ","));
  }

  private static class Listener implements MeasureListener<List<DoubleSolution>> {

    private int counter = 0;

    @Override
    synchronized public void measureGenerated(List<DoubleSolution> solutions) {
      if ((counter % 100 == 0)) {
        System.out.println(
            "PUSH MEASURE. Counter = " + counter + " First solution: " + solutions.get(0)
                .variables().get(0));
      }
      counter++;
    }
  }

  private static class Listener2 implements MeasureListener<Long> {

    @Override
    synchronized public void measureGenerated(Long value) {
      if ((value % 10 == 0)) {
        System.out.println("PUSH MEASURE. Iteration: " + value);
      }
    }
  }
}
