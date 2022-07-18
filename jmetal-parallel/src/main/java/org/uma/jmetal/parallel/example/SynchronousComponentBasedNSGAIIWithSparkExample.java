package org.uma.jmetal.parallel.example;

import static java.lang.Math.sin;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIBuilder;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.MultiThreadedEvaluation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.parallel.synchronous.SparkEvaluation;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT2;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

;

/**
 * Class to configure and run the NSGA-II algorithm using a {@link SparkEvaluation} object.
 * Reference: C. Barba-González, J. García-Nieto, Antonio J. Nebro, J.F.Aldana-Montes: Multi-objective Big
 * Data Optimization with jMetal and Spark. EMO 2017". DOI: http://dx.doi.org/10.1007/978-3-319-54157-0_2
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SynchronousComponentBasedNSGAIIWithSparkExample extends AbstractAlgorithmRunner {
  public static void main(String[] args) throws JMetalException {
    var problem = new ZDT2() {
      @Override
      public DoubleSolution evaluate(DoubleSolution solution) {
        super.evaluate(solution);
        computingDelay();

        return solution;
      }

      private void computingDelay() {
        for (long i = 0; i < 1000; i++)
          for (long j = 0; j < 1000; j++) {
            double a = sin(i) * Math.cos(j);
          }
      }
    };

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int populationSize = 100;
    int offspringPopulationSize = populationSize;

    @NotNull Termination termination = new TerminationByEvaluations(25000);

    EvolutionaryAlgorithm<DoubleSolution> nsgaii = new NSGAIIBuilder<>(
        problem,
        populationSize,
        offspringPopulationSize,
        crossover,
        mutation)
        .setTermination(termination)
        .setEvaluation(new MultiThreadedEvaluation<>(8, problem))
        .build();

    RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
            new RunTimeChartObserver<>(
                    "NSGA-II",
                    80, 10, "resources/referenceFrontsCSV/ZDT2.csv");

    nsgaii.getObservable().register(runTimeChartObserver);

    nsgaii.run();

    List<DoubleSolution> population = nsgaii.getResult();
    JMetalLogger.logger.info("Total execution time : " + nsgaii.getTotalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + nsgaii.getNumberOfEvaluations());

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");
  }
}
