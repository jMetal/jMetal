package org.uma.jmetal.parallel.example;

import static java.lang.Math.sin;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.parallel.asynchronous.algorithm.impl.AsynchronousMultiThreadedNSGAII;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;

public class AsynchronousMasterWorkerBasedNSGAIIExample {
  public static void main(String[] args) {

    var populationSize = 100;
    var maxEvaluations = 25000;
    var numberOfCores = 32;

    @NotNull DoubleProblem problem = new ZDT1() {
      @Override
      public DoubleSolution evaluate(DoubleSolution solution) {
        super.evaluate(solution);
        computingDelay();

        return solution;
      }

      private void computingDelay() {
        for (long i = 0; i < 1000; i++)
          for (long j = 0; j < 10000; j++) {
            var a = sin(i) * Math.cos(j);
          }
      }
    };

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    var initTime = System.currentTimeMillis();

    @NotNull AsynchronousMultiThreadedNSGAII<DoubleSolution> nsgaii =
            new AsynchronousMultiThreadedNSGAII<DoubleSolution>(
                    numberOfCores, problem, populationSize, crossover, mutation, new TerminationByEvaluations(maxEvaluations));


    var runTimeChartObserver =
            new RunTimeChartObserver<DoubleSolution>(
                    "NSGA-II",
                    80, 10, "resources/referenceFrontsCSV/ZDT1.csv");

    nsgaii.getObservable().register(runTimeChartObserver);

    nsgaii.run();

    var endTime = System.currentTimeMillis();

    var resultList = nsgaii.getResult();

    JMetalLogger.logger.info("Computing time: " + (endTime - initTime));
    new SolutionListOutput(resultList)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();
    System.exit(0);
  }
}
