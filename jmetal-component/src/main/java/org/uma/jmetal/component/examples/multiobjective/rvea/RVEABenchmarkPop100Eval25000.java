package org.uma.jmetal.component.examples.multiobjective.rvea;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.RVEABuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.referencepoint.ReferencePointGenerator;

public class RVEABenchmarkPop100Eval25000 {
  private static final Path OUTPUT_DIRECTORY = Paths.get("results", "rvea_pop100_eval25000");
  private static final int POPULATION_SIZE = 100;
  private static final int MAX_EVALUATIONS = 25000;
  private static final double CROSSOVER_PROBABILITY = 0.9;
  private static final double CROSSOVER_DISTRIBUTION_INDEX = 30.0;
  private static final double MUTATION_DISTRIBUTION_INDEX = 20.0;
  private static final double ALPHA = 2.0;
  private static final double FR = 0.1;

  public static void main(String[] args) throws IOException {
    Files.createDirectories(OUTPUT_DIRECTORY);

    List<BenchmarkRun> runs = List.of(
        new BenchmarkRun("ZDT1",
            "ZDT1",
            "org.uma.jmetal.problem.multiobjective.zdt.ZDT1",
            "resources/referenceFrontsCSV/ZDT1.csv",
            99,
            ReferencePointGenerator.generateSingleLayer(2, 99),
            "single-layer(99)"),
        new BenchmarkRun("ZDT4",
            "ZDT4",
            "org.uma.jmetal.problem.multiobjective.zdt.ZDT4",
            "resources/referenceFrontsCSV/ZDT4.csv",
            99,
            ReferencePointGenerator.generateSingleLayer(2, 99),
            "single-layer(99)"),
        new BenchmarkRun("DTLZ1",
            "DTLZ1 (experimental custom reference vectors)",
            "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1",
            "resources/referenceFrontsCSV/DTLZ1.3D.csv",
            12,
            ReferencePointGenerator.generateTwoLayers(3, 9, 8),
            "two-layer(9,8)"));

    List<RunSummary> summaries = new ArrayList<>();
    for (BenchmarkRun run : runs) {
      summaries.add(execute(run));
    }

    writeRuntimeCsv(summaries);
    JMetalLogger.logger.info("Benchmark results written to " + OUTPUT_DIRECTORY.toAbsolutePath());
  }

  private static RunSummary execute(BenchmarkRun benchmarkRun) throws IOException {
    Problem<DoubleSolution> problem = ProblemFactory.loadProblem(benchmarkRun.problemName());

    var crossover = new SBXCrossover(CROSSOVER_PROBABILITY, CROSSOVER_DISTRIBUTION_INDEX);
    var mutation = new PolynomialMutation(
        1.0 / problem.numberOfVariables(), MUTATION_DISTRIBUTION_INDEX);

    Termination termination = new TerminationByEvaluations(MAX_EVALUATIONS);

    EvolutionaryAlgorithm<DoubleSolution> rvea =
        new RVEABuilder<>(problem, POPULATION_SIZE, MAX_EVALUATIONS, crossover, mutation, ALPHA,
        FR, benchmarkRun.referenceVectors())
            .setTermination(termination)
            .build();

    rvea.run();

    List<DoubleSolution> paretoFront = SolutionListUtils.getNonDominatedSolutions(rvea.result());

    Path funPath = OUTPUT_DIRECTORY.resolve("FUN_" + benchmarkRun.label() + ".csv");
    Path varPath = OUTPUT_DIRECTORY.resolve("VAR_" + benchmarkRun.label() + ".csv");

    new SolutionListOutput(paretoFront)
        .setVarFileOutputContext(new DefaultFileOutputContext(varPath.toString(), ","))
        .setFunFileOutputContext(new DefaultFileOutputContext(funPath.toString(), ","))
        .print();

    int referenceVectors = benchmarkRun.referenceVectors().size();

    JMetalLogger.logger.info(
        String.format(
            Locale.US,
            "%s finished. Runtime=%dms, evaluations=%d, nonDominated=%d, referenceVectors=%d",
            benchmarkRun.displayName(),
            rvea.totalComputingTime(),
            rvea.numberOfEvaluations(),
            paretoFront.size(),
            referenceVectors));

    return new RunSummary(
        benchmarkRun.displayName(),
        benchmarkRun.referenceFront(),
        POPULATION_SIZE,
        MAX_EVALUATIONS,
        benchmarkRun.divisions(),
        benchmarkRun.referenceVectorScheme(),
        referenceVectors,
        rvea.totalComputingTime(),
        rvea.numberOfEvaluations(),
        paretoFront.size(),
        funPath,
        varPath);
  }

  private static void writeRuntimeCsv(List<RunSummary> summaries) throws IOException {
    Path runtimePath = OUTPUT_DIRECTORY.resolve("runtime.csv");
    try (BufferedWriter writer = Files.newBufferedWriter(runtimePath)) {
      writer.write(
          "problem,populationSize,maxEvaluations,divisions,referenceVectorScheme,referenceVectors,runtimeMs,evaluations,nonDominatedSolutions,funFile,varFile,referenceFront");
      writer.newLine();

      for (RunSummary summary : summaries) {
        writer.write(
            String.format(
                Locale.US,
                "%s,%d,%d,%d,%s,%d,%d,%d,%d,%s,%s,%s",
                summary.problem(),
                summary.populationSize(),
                summary.maxEvaluations(),
                summary.divisions(),
                summary.referenceVectorScheme(),
                summary.referenceVectors(),
                summary.runtimeMs(),
                summary.evaluations(),
                summary.nonDominatedSolutions(),
                summary.funFile().toString().replace('\\', '/'),
                summary.varFile().toString().replace('\\', '/'),
                summary.referenceFront().replace('\\', '/')));
        writer.newLine();
      }
    }
  }

  private static class BenchmarkRun {
    private final String label;
    private final String displayName;
    private final String problemName;
    private final String referenceFront;
    private final int divisions;
    private final List<double[]> referenceVectors;
    private final String referenceVectorScheme;

    private BenchmarkRun(
        String label,
        String displayName,
        String problemName,
        String referenceFront,
        int divisions,
        List<double[]> referenceVectors,
        String referenceVectorScheme) {
      this.label = label;
      this.displayName = displayName;
      this.problemName = problemName;
      this.referenceFront = referenceFront;
      this.divisions = divisions;
      this.referenceVectors = referenceVectors;
      this.referenceVectorScheme = referenceVectorScheme;
    }

    private String label() {
      return label;
    }

    private String problemName() {
      return problemName;
    }

    private String displayName() {
      return displayName;
    }

    private String referenceFront() {
      return referenceFront;
    }

    private int divisions() {
      return divisions;
    }

    private List<double[]> referenceVectors() {
      return referenceVectors;
    }

    private String referenceVectorScheme() {
      return referenceVectorScheme;
    }
  }

  private static class RunSummary {
    private final String problem;
    private final String referenceFront;
    private final int populationSize;
    private final int maxEvaluations;
    private final int divisions;
    private final String referenceVectorScheme;
    private final int referenceVectors;
    private final long runtimeMs;
    private final int evaluations;
    private final int nonDominatedSolutions;
    private final Path funFile;
    private final Path varFile;

    private RunSummary(
        String problem,
        String referenceFront,
        int populationSize,
        int maxEvaluations,
        int divisions,
        String referenceVectorScheme,
        int referenceVectors,
        long runtimeMs,
        int evaluations,
        int nonDominatedSolutions,
        Path funFile,
        Path varFile) {
      this.problem = problem;
      this.referenceFront = referenceFront;
      this.populationSize = populationSize;
      this.maxEvaluations = maxEvaluations;
      this.divisions = divisions;
      this.referenceVectorScheme = referenceVectorScheme;
      this.referenceVectors = referenceVectors;
      this.runtimeMs = runtimeMs;
      this.evaluations = evaluations;
      this.nonDominatedSolutions = nonDominatedSolutions;
      this.funFile = funFile;
      this.varFile = varFile;
    }

    private String problem() {
      return problem;
    }

    private String referenceFront() {
      return referenceFront;
    }

    private int populationSize() {
      return populationSize;
    }

    private int maxEvaluations() {
      return maxEvaluations;
    }

    private int divisions() {
      return divisions;
    }

    private String referenceVectorScheme() {
      return referenceVectorScheme;
    }

    private int referenceVectors() {
      return referenceVectors;
    }

    private long runtimeMs() {
      return runtimeMs;
    }

    private int evaluations() {
      return evaluations;
    }

    private int nonDominatedSolutions() {
      return nonDominatedSolutions;
    }

    private Path funFile() {
      return funFile;
    }

    private Path varFile() {
      return varFile;
    }
  }
}
