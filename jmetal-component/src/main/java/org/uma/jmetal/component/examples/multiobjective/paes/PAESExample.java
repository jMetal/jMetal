package org.uma.jmetal.component.examples.multiobjective.paes;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.multiobjective.PAESBuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.qualityindicator.QualityIndicatorUtils;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.GenericBoundedArchive;
import org.uma.jmetal.util.densityestimator.impl.GridDensityEstimator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class to configure and run the component-based PAES (Pareto Archived Evolution Strategy)
 * algorithm on a double-encoded problem. This example uses the canonical PAES setup: a bounded
 * archive whose density is estimated with an adaptive grid ({@link GridDensityEstimator}).
 *
 * @author Antonio J. Nebro
 */
public class PAESExample extends AbstractAlgorithmRunner {
  public static void main(String[] args) throws JMetalException, IOException {
    String problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
    String referenceParetoFront = "resources/referenceFrontsCSV/ZDT1.csv";

    Problem<DoubleSolution> problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int archiveSize = 100;
    int biSections = 5;
    BoundedArchive<DoubleSolution> archive =
        new GenericBoundedArchive<>(
            archiveSize, new GridDensityEstimator<>(biSections, problem.numberOfObjectives()));

    Termination termination = new TerminationByEvaluations(35000);

    var algorithm = new PAESBuilder<>(
            problem,
            archiveSize,
            mutation,
            archive)
        .setTermination(termination)
        .build();

    EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    algorithm.observable().register(evaluationObserver);

    algorithm.run();

    List<DoubleSolution> population = algorithm.result();
    JMetalLogger.logger.info("Total execution time : " + algorithm.totalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + algorithm.numberOfEvaluations());

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
