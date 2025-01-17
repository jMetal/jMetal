package org.uma.jmetal.component.examples.multiobjective.smsemoa;

import static org.uma.jmetal.util.VectorUtils.readVectors;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.SMSEMOADEBuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover.DE_VARIANT;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.qualityindicator.QualityIndicatorUtils;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.FrontPlotObserver;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class to configure and run the NSGA-II-DE algorithm showing the population while the algorithm is
 * running
 *
 * @author Antonio J. Nebro
 */
public class SMSEMOADESolving3DProblemWithRealTimeChartExample {

  public static void main(String[] args) throws JMetalException, IOException {
    String problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2";
    String referenceParetoFront = "resources/referenceFrontsCSV/DTLZ2.3D.csv";

    Problem<DoubleSolution> problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int populationSize = 100;

    Termination termination = new TerminationByEvaluations(40000);

    double cr = 1.0;
    double f = 0.5;
    EvolutionaryAlgorithm<DoubleSolution> smsemoa = new SMSEMOADEBuilder(
        problem,
        populationSize,
        cr,
        f,
        mutation,
        DE_VARIANT.RAND_1_BIN)
        .setTermination(termination)
        .build();

    EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    var chartObserver =
        new FrontPlotObserver<DoubleSolution>("SMS-EMOA-DE", "F1", "F2", problem.name(), 1000);

    chartObserver.setFront(readVectors(referenceParetoFront, ","), "Reference front");

    smsemoa.observable().register(evaluationObserver);
    smsemoa.observable().register(chartObserver);

    smsemoa.run();

    List<DoubleSolution> population = smsemoa.result();
    JMetalLogger.logger.info("Total execution time : " + smsemoa.totalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + smsemoa.numberOfEvaluations());

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    QualityIndicatorUtils.printQualityIndicators(
        SolutionListUtils.getMatrixWithObjectiveValues(population),
        readVectors(referenceParetoFront, ","));
  }
}
