package org.uma.jmetal.experimental.parallel.asynchronous.example;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.experimental.parallel.asynchronous.algorithm.AsynchronousMultithreadedMasterWorkerNSGAII;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;

import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.List;

import static java.lang.Math.sin;

public class AsynchronousMasterWorkerBasedNSGAIIExample {
  public static void main(String[] args) {
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;
    int populationSize = 100;
    int maxEvaluations = 25000;
    int numberOfCores = 32 ;

    DoubleProblem problem = new ZDT1() {
      @Override
      public DoubleSolution evaluate (DoubleSolution solution) {
        super.evaluate(solution) ;

        for (long i = 0 ; i < 10000; i++)
          for (long j = 0; j < 10000; j++) {
            double a = sin(i)*Math.cos(j) ;
          }

        return solution ;
      }
    } ;

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    long initTime = System.currentTimeMillis();
    AsynchronousMultithreadedMasterWorkerNSGAII<DoubleSolution> nsgaiiMaster =
        new AsynchronousMultithreadedMasterWorkerNSGAII<DoubleSolution>(
            numberOfCores, problem, populationSize, crossover, mutation, new TerminationByEvaluations(maxEvaluations));

//    RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
//        new RunTimeChartObserver<>(
//            "NSGA-II",
//            80,  1000, "resources/referenceFrontsCSV/ZDT1.pf");

//    nsgaiiMaster.getObservable().register(runTimeChartObserver);

    nsgaiiMaster.run();

    long endTime = System.currentTimeMillis();

    List<DoubleSolution> resultList = nsgaiiMaster.getResult();

    JMetalLogger.logger.info("Computing time: " + (endTime - initTime));
    new SolutionListOutput(resultList)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();
    System.exit(0);
  }
}
