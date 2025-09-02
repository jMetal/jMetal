package org.uma.jmetal.component.examples.multiobjective.smpso;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.SMPSOBuilder;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.MultiThreadedEvaluation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.qualityindicator.QualityIndicatorUtils;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import static java.lang.Math.sin;

public class ParallelSMPSOExample {
  public static void main(String[] args) throws JMetalException, IOException {
    String referenceParetoFront = "resources/referenceFrontsCSV/ZDT4.csv";

    DoubleProblem problem =
        new ZDT1() {
          @Override
          public DoubleSolution evaluate(DoubleSolution solution) {
            super.evaluate(solution);
            computingDelay();

            return solution;
          }

          private void computingDelay() {
            for (long i = 0; i < 1000; i++)
              for (long j = 0; j < 10000; j++) {
                double dummy = sin(i) * Math.cos(j);
              }
          }
        };

    int swarmSize = 100;
    Termination termination = new TerminationByEvaluations(25000);

    ParticleSwarmOptimizationAlgorithm smpso =
        new SMPSOBuilder(problem, swarmSize)
            .setTermination(termination)
            .setEvaluation(new MultiThreadedEvaluation<>(8, problem))
            .build();

    smpso.run();

    List<DoubleSolution> population = smpso.result();
    JMetalLogger.logger.info("Total execution time : " + smpso.totalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + smpso.evaluation());

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    QualityIndicatorUtils.printQualityIndicators(
        SolutionListUtils.getMatrixWithObjectiveValues(population),
        VectorUtils.readVectors(referenceParetoFront, ","));
  }
}
