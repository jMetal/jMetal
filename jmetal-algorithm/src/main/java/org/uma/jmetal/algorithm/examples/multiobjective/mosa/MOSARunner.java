package org.uma.jmetal.algorithm.examples.multiobjective.mosa;

import java.io.FileNotFoundException;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.mosa.MOSA;
import org.uma.jmetal.algorithm.multiobjective.mosa.cooling.impl.Exponential;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.GenericBoundedArchive;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.GridDensityEstimator;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class for configuring and running the {@link MOSA} algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MOSARunner extends AbstractAlgorithmRunner {
  public static void main(String[] args) throws JMetalException, FileNotFoundException {
    @NotNull String problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2_2D";
    String referenceParetoFront = "resources/referenceFrontsCSV/DTLZ2.2D.csv";

    Problem<DoubleSolution> problem = ProblemFactory.loadProblem(problemName);

    MutationOperator<DoubleSolution> mutation =
            new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);

    BoundedArchive<DoubleSolution> archive = new GenericBoundedArchive<>(100, new GridDensityEstimator<>(5, problem.getNumberOfObjectives()));
    archive = new GenericBoundedArchive<>(100, new CrowdingDistanceDensityEstimator<>());

    MOSA<DoubleSolution> algorithm =
            new MOSA<>(problem, 50000, archive, mutation, 1.0, new Exponential(0.95));

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    List<DoubleSolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Number of non-accepted solutions: " + algorithm.getNumberOfWorstAcceptedSolutions());
    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }
  }
}
