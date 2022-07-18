package org.uma.jmetal.lab.experiment.component.impl;

import static org.uma.jmetal.util.VectorUtils.readVectors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.component.ExperimentComponent;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

/**
 * This class computes the reference Pareto set and front from a set of data files containing the
 * variable (VARx.tsv file) and objective (FUNx.tsv) values. A requirement is that the variable
 * values MUST correspond to {@link DoubleSolution} solutions, i.e., the solved problems must be
 * instances of {@link DoubleProblem}.
 *
 * <p>Once the algorithms of an org.uma.jmetal.experiment have been executed through running an
 * instance of class {@link ExecuteAlgorithms}, all the obtained fronts of all the algorithms are
 * gathered per problem; then, the dominated solutions are removed thus yielding to the reference
 * Pareto front.
 *
 * <p>By default, the files are stored in a directory called "referenceFront", which is located in
 * the org.uma.jmetal.experiment base directory. The following files are generated per problem: -
 * "problemName.pf": the reference Pareto front. - "problemName.ps": the reference Pareto set (i.e.,
 * the variable values of the solutions of the reference Pareto front. -
 * "problemName.algorithmName.pf": the objectives values of the contributed solutions by the
 * algorithm called "algorithmName" to "problemName.pf" - "problemName.algorithmName.ps": the
 * variable values of the contributed solutions by the algorithm called "algorithmName" to
 * "problemName.ps"
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenerateReferenceParetoSetAndFrontFromDoubleSolutions implements ExperimentComponent {
  private final Experiment<?, ?> experiment;

  public GenerateReferenceParetoSetAndFrontFromDoubleSolutions(
          Experiment<?, ?> experimentConfiguration) {
    this.experiment = experimentConfiguration;
  }

  /**
   * The run() method creates de output directory and compute the fronts
   */
  @Override
  public void run() throws IOException {
    String outputDirectoryName = experiment.getReferenceFrontDirectory();
    createOutputDirectory(outputDirectoryName);

    for (@NotNull ExperimentProblem<?> problem : experiment.getProblemList()) {
      List<DummyDoubleSolution> nonDominatedSolutions = getNonDominatedSolutions(problem);

      writeReferenceFrontFile(outputDirectoryName, problem, nonDominatedSolutions);
      writeReferenceSetFile(outputDirectoryName, problem, nonDominatedSolutions);

      writeFilesWithTheSolutionsContributedByEachAlgorithm(
              outputDirectoryName, problem, nonDominatedSolutions);
    }
  }

  private void writeFilesWithTheSolutionsContributedByEachAlgorithm(
          String outputDirectoryName,
          ExperimentProblem<?> problem,
          List<DummyDoubleSolution> nonDominatedSolutions) {
    GenericSolutionAttribute<DoubleSolution, String> solutionAttribute =
            new GenericSolutionAttribute<DoubleSolution, String>();

    for (@NotNull ExperimentAlgorithm<?, ?> algorithm : experiment.getAlgorithmList()) {
        List<DoubleSolution> solutionsPerAlgorithm = new ArrayList<>();
        for (@NotNull DummyDoubleSolution solution : nonDominatedSolutions) {
            if (algorithm.getAlgorithmTag().equals(solutionAttribute.getAttribute(solution))) {
                solutionsPerAlgorithm.add(solution);
            }
        }

        new SolutionListOutput(solutionsPerAlgorithm)
              .printObjectivesToFile(
                      outputDirectoryName
                              + "/"
                              + problem.getTag()
                              + "."
                              + algorithm.getAlgorithmTag()
                              + ".rf",
                      ",");
      new SolutionListOutput(solutionsPerAlgorithm)
              .printVariablesToFile(
                      outputDirectoryName
                              + "/"
                              + problem.getTag()
                              + "."
                              + algorithm.getAlgorithmTag()
                              + ".rs",
                      ",");
    }
  }

  private void writeReferenceFrontFile(
          String outputDirectoryName,
          ExperimentProblem<?> problem,
          List<DummyDoubleSolution> nonDominatedSolutions) {
    @NotNull String referenceFrontFileName = outputDirectoryName + "/" + problem.getReferenceFront();

    new SolutionListOutput(nonDominatedSolutions)
            .printObjectivesToFile(referenceFrontFileName, ",");
  }

  private void writeReferenceSetFile(
          String outputDirectoryName,
          ExperimentProblem<?> problem,
          List<DummyDoubleSolution> nonDominatedSolutions) {
    String referenceSetFileName = outputDirectoryName + "/" + problem.getTag() + ".ps";
    new SolutionListOutput(nonDominatedSolutions).printVariablesToFile(referenceSetFileName, ",");
  }

  /**
   * Create a list of non dominated {@link DoubleSolution} solutions from the FUNx.tsv and VARx.tsv
   * files that must have been previously obtained (probably by invoking the {@link
   * ExecuteAlgorithms#run} method).
   *
   * @param problem
   * @return
   * @throws FileNotFoundException
   */
  private List<DummyDoubleSolution> getNonDominatedSolutions(@NotNull ExperimentProblem<?> problem)
      throws IOException {
    NonDominatedSolutionListArchive<DummyDoubleSolution> nonDominatedSolutionArchive =
            new NonDominatedSolutionListArchive<>();

      @NotNull ArrayList<ExperimentAlgorithm<?, ?>> experimentAlgorithms = new ArrayList<>();
      for (ExperimentAlgorithm<?, ?> s : experiment.getAlgorithmList()) {
          if (s.getProblemTag().equals(problem.getTag())) {
              experimentAlgorithms.add(s);
          }
      }
      for (ExperimentAlgorithm<?, ?> algorithm :
              experimentAlgorithms) {
      String problemDirectory =
              experiment.getExperimentBaseDirectory()
                      + "/data/"
                      + algorithm.getAlgorithmTag()
                      + "/"
                      + problem.getTag();

      @NotNull String frontFileName =
              problemDirectory
                      + "/"
                      + experiment.getOutputParetoFrontFileName()
                      + algorithm.getRunId()
                      + ".csv";
      String paretoSetFileName =
              problemDirectory
                      + "/"
                      + experiment.getOutputParetoSetFileName()
                      + algorithm.getRunId()
                      + ".csv";

      double[][] frontWithObjectiveValues = readVectors(frontFileName, ",");
      double[] @NotNull [] frontWithVariableValues = readVectors(paretoSetFileName, ",");
      List<DummyDoubleSolution> solutionList =
              createSolutionListFrontFiles(
                      algorithm.getAlgorithmTag(), frontWithVariableValues, frontWithObjectiveValues);
      for (DummyDoubleSolution solution : solutionList) {
        nonDominatedSolutionArchive.add(solution);
      }
    }

    return nonDominatedSolutionArchive.getSolutionList();
  }

  /**
   * Create the output directory where the result files will be stored
   *
   * @param outputDirectoryName
   * @return
   */
  private @NotNull File createOutputDirectory(@NotNull String outputDirectoryName) {
    File outputDirectory;
    outputDirectory = new File(outputDirectoryName);
    if (!outputDirectory.exists()) {
      boolean result = new File(outputDirectoryName).mkdir();
      JMetalLogger.logger.info("Creating " + outputDirectoryName + ". Status = " + result);
    }

    return outputDirectory;
  }

  /**
   * @param algorithmName
   * @param frontWithVariableValues
   * @param frontWithObjectiveValues
   * @return
   */
  private @NotNull List<DummyDoubleSolution> createSolutionListFrontFiles(
          String algorithmName, double[][] frontWithVariableValues, double[][] frontWithObjectiveValues) {
    if (frontWithVariableValues.length
            != frontWithObjectiveValues.length) {
      throw new JMetalException(
              "The number of solutions in the variable and objective fronts are not equal");
    } else if (frontWithObjectiveValues.length == 0) {
      throw new JMetalException("The front of solutions is empty");
    }

    GenericSolutionAttribute<DummyDoubleSolution, String> solutionAttribute =
            new GenericSolutionAttribute<DummyDoubleSolution, String>();

    int numberOfVariables = frontWithVariableValues[0].length;
    int numberOfObjectives = frontWithObjectiveValues[0].length;

    List<DummyDoubleSolution> solutionList = new ArrayList<>();
    for (int i = 0; i < frontWithVariableValues.length; i++) {
      @NotNull DummyDoubleSolution solution = new DummyDoubleSolution(numberOfVariables, numberOfObjectives);
      for (int vars = 0; vars < numberOfVariables; vars++) {
        solution.variables().set(vars, frontWithVariableValues[i][vars]);
      }
      for (int objs = 0; objs < numberOfObjectives; objs++) {
        solution.objectives()[objs] = frontWithObjectiveValues[i][objs];
      }

      solutionAttribute.setAttribute(solution, algorithmName);
      solutionList.add(solution);
    }

    return solutionList;
  }

  @SuppressWarnings("serial")
  private static class DummyDoubleSolution extends AbstractSolution<Double>
          implements DoubleSolution {

    public DummyDoubleSolution(int numberOfVariables, int numberOfObjectives) {
      super(numberOfVariables, numberOfObjectives);
    }

    @Override
    public @Nullable Solution<Double> copy() {
      return null;
    }

    /**
     * @deprecated Use {@link #getBounds(int)}{@link Bounds#getLowerBound()
     * .getLowerBound()} instead.
     */

    @Override
    public @Nullable Bounds<Double> getBounds(int index) {
      return null;
    }
  }
}
