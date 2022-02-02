package org.uma.jmetal.experimental.componentbasedalgorithm.example.multiobjective.smpso;

import org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.multiobjective.smpso.SMPSO;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.lab.visualization.plot.PlotFront;
import org.uma.jmetal.lab.visualization.plot.impl.PlotSmile;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.legacy.front.impl.ArrayFront;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.termination.Termination;
import org.uma.jmetal.util.termination.impl.TerminationByEvaluations;

import java.util.List;

/**
 * Class for configuring and running the SMPSO algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SMPSOStandardSettingsExample extends AbstractAlgorithmRunner {
  public static void main(String[] args) throws Exception {
    DoubleProblem problem;
    SMPSO algorithm;
    MutationOperator<DoubleSolution> mutation;

    String problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
    String referenceParetoFront = "resources/referenceFrontsCSV/ZDT1.csv" ;

    problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

    int swarmSize = 100 ;
    BoundedArchive<DoubleSolution> leadersArchive = new CrowdingDistanceArchive<>(swarmSize) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem) ;
    Termination termination = new TerminationByEvaluations(25000) ;

    algorithm = new SMPSO(problem, swarmSize, leadersArchive, mutation, evaluation, termination) ;

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();
    JMetalLogger.logger.info("Total execution time : " + algorithm.getTotalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + algorithm.getEvaluations());

    new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }

    PlotFront plot = new PlotSmile(new ArrayFront(population).getMatrix(), problem.getName() + " (NSGA-II)") ;
    plot.plot();
  }
}
