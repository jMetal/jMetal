package org.uma.jmetal.component.examples.multiobjective.agemoea;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.AGEMOEABuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea.AGEMOEA2EnvironmentalSelection;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea.AGEMOEAEnvironmentalSelection;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.qualityindicator.QualityIndicatorUtils;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Example comparing AGE-MOEA and AGE-MOEA-II on all 7 DTLZ problems (3 objectives, default config).
 *
 * <p>For each problem/algorithm combination, the quality indicators (Epsilon, HV, IGD, IGD+, etc.)
 * are printed to the console so the results can be directly compared.
 *
 * @author Antonio J. Nebro
 */
public class AGEMOEAComparisonExample {

  private record ProblemConfig(String name, String referenceFront, int evaluations) {}

  public static void main(String[] args) throws JMetalException, IOException {
    List<ProblemConfig> problems = List.of(
        new ProblemConfig(
            "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1",
            "resources/referenceFrontsCSV/DTLZ1.3D.csv",
            40000),
        new ProblemConfig(
            "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2",
            "resources/referenceFrontsCSV/DTLZ2.3D.csv",
            25000),
        new ProblemConfig(
            "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ3",
            "resources/referenceFrontsCSV/DTLZ3.3D.csv",
            40000),
        new ProblemConfig(
            "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ4",
            "resources/referenceFrontsCSV/DTLZ4.3D.csv",
            25000),
        new ProblemConfig(
            "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ5",
            "resources/referenceFrontsCSV/DTLZ5.3D.csv",
            25000),
        new ProblemConfig(
            "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ6",
            "resources/referenceFrontsCSV/DTLZ6.3D.csv",
            25000),
        new ProblemConfig(
            "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ7",
            "resources/referenceFrontsCSV/DTLZ7.3D.csv",
            25000));

    List<Map.Entry<String, Function<Integer, AGEMOEAEnvironmentalSelection<DoubleSolution>>>> variants =
        List.of(
            Map.entry("AGE-MOEA",  AGEMOEAEnvironmentalSelection::new),
            Map.entry("AGE-MOEA-II", AGEMOEA2EnvironmentalSelection::new));

    int populationSize = 100;
    int offspringPopulationSize = 100;

    for (ProblemConfig config : problems) {
      Problem<DoubleSolution> problem = ProblemFactory.loadProblem(config.name());

      double crossoverProbability = 0.9;
      double crossoverDistributionIndex = 30.0;
      double mutationDistributionIndex = 20.0;

      for (var variant : variants) {
        var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);
        var mutation = new PolynomialMutation(
            1.0 / problem.numberOfVariables(), mutationDistributionIndex);

        Termination termination = new TerminationByEvaluations(config.evaluations());

        var environmentalSelection = variant.getValue().apply(problem.numberOfObjectives());

        EvolutionaryAlgorithm<DoubleSolution> algorithm =
            new AGEMOEABuilder<>(
                    problem, populationSize, offspringPopulationSize, crossover, mutation,
                    environmentalSelection)
                .setTermination(termination)
                .build();

        algorithm.run();

        List<DoubleSolution> population = algorithm.result();

        JMetalLogger.logger.info("==================================================");
        JMetalLogger.logger.info("Algorithm : " + variant.getKey());
        JMetalLogger.logger.info("Problem   : " + problem.name());
        JMetalLogger.logger.info("Time (ms) : " + algorithm.totalComputingTime());
        JMetalLogger.logger.info("Evaluations: " + algorithm.numberOfEvaluations());

        QualityIndicatorUtils.printQualityIndicators(
            SolutionListUtils.getMatrixWithObjectiveValues(population),
            VectorUtils.readVectors(config.referenceFront(), ","));
      }
    }
  }
}
