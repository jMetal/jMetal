package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIMeasures;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.measure.MeasureListener;
import org.uma.jmetal.util.measure.MeasureManager;
import org.uma.jmetal.util.measure.impl.BasicMeasure;
import org.uma.jmetal.util.measure.impl.DurationMeasure;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class to configure and run the NSGA-II algorithm (variant with measures) getting the value of quality indicators of
 * each iteration.
 */
public class NSGAIIMeasuresWithQualityIndicatorRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws SecurityException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.NSGAIIMeasuresRunner problemName [referenceFront]
   */
  public static void main(String[] args)
      throws JMetalException, InterruptedException, FileNotFoundException {
    Problem<DoubleSolution> problem;
    Algorithm<List<DoubleSolution>> algorithm;
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;
    String referenceParetoFront = "" ;

    String problemName ;
    if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
      referenceParetoFront = "jmetal-problem/src/test/resources/pareto_fronts/ZDT1.pf" ;
    }

    problem = ProblemUtils.<DoubleSolution> loadProblem(problemName);

    double crossoverProbability = 0.9 ;
    double crossoverDistributionIndex = 20.0 ;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    selection = new BinaryTournamentSelection<DoubleSolution>(new RankingAndCrowdingDistanceComparator<DoubleSolution>());

    int maxEvaluations = 25000 ;
    int populationSize = 100 ;

    algorithm = new NSGAIIBuilder<DoubleSolution>(problem, crossover, mutation, populationSize)
        .setSelectionOperator(selection)
        .setMaxEvaluations(maxEvaluations)
        .setVariant(NSGAIIBuilder.NSGAIIVariant.Measures)
        .build() ;

    ((NSGAIIMeasures<DoubleSolution>)algorithm).setReferenceFront(new ArrayFront(referenceParetoFront));

    /* Measure management */
    MeasureManager measureManager = ((NSGAIIMeasures<DoubleSolution>)algorithm).getMeasureManager() ;

    DurationMeasure currentComputingTime =
        (DurationMeasure) measureManager.<Long>getPullMeasure("currentExecutionTime");

    BasicMeasure<List<DoubleSolution>> solutionListMeasure = (BasicMeasure<List<DoubleSolution>>) measureManager
            .<List<DoubleSolution>>getPushMeasure("currentPopulation");

    solutionListMeasure.register(new Listener(referenceParetoFront));
    /* End of measure management */

    Thread algorithmThread = new Thread(algorithm) ;
    algorithmThread.start();

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
    private static int counter = 1 ;

    ArrayFront referenceParetoFront ;

    public Listener(String referenceParetoFrontFile) throws FileNotFoundException {
      referenceParetoFront = new ArrayFront(referenceParetoFrontFile) ;
    }

    @Override synchronized public void measureGenerated(List<DoubleSolution> solutionList) {
      QualityIndicator<List<DoubleSolution>, Double> epsilon =
              new Epsilon<DoubleSolution>(referenceParetoFront) ;
      QualityIndicator<List<DoubleSolution>, Double> hv =
              new PISAHypervolume<>(referenceParetoFront) ;

      System.out.println("Iteration: " + counter +
              ". Epsilon: " + epsilon.evaluate(solutionList) + ". Hypervolume: " + hv.evaluate(solutionList)) ;
      counter ++ ;
    }

  }
}
