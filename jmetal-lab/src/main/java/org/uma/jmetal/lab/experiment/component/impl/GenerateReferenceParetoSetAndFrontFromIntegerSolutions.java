package org.uma.jmetal.lab.experiment.component.impl;

import static org.uma.jmetal.util.VectorUtils.readVectors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.component.ExperimentComponent;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.problem.integerproblem.IntegerProblem;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

/**
 * This class computes the reference Pareto set and front from a set of data files containing the
 * variable (VARx.tsv file) and objective (FUNx.tsv) values. A requirement is that the variable
 * values MUST correspond to {@link IntegerSolution} solutions, i.e., the solved problems must be
 * instances of {@link IntegerProblem}.
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
public class GenerateReferenceParetoSetAndFrontFromIntegerSolutions implements ExperimentComponent {
  private final Experiment<?, ?> experiment;

  public GenerateReferenceParetoSetAndFrontFromIntegerSolutions(
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

    for (ExperimentProblem<?> problem : experiment.getProblemList()) {
      List<DummyIntegerSolution> nonDominatedSolutions = getNonDominatedSolutions(problem);

      writeReferenceFrontFile(outputDirectoryName, problem, nonDominatedSolutions);
      writeReferenceSetFile(outputDirectoryName, problem, nonDominatedSolutions);

      writeFilesWithTheSolutionsContributedByEachAlgorithm(
              outputDirectoryName, problem, nonDominatedSolutions);
    }
  }

  private void writeFilesWithTheSolutionsContributedByEachAlgorithm(
          String outputDirectoryName,
          ExperimentProblem<?> problem,
          List<DummyIntegerSolution> nonDominatedSolutions) {
    GenericSolutionAttribute<IntegerSolution, String> solutionAttribute =
            new GenericSolutionAttribute<>();

    for (ExperimentAlgorithm<?, ?> algorithm : experiment.getAlgorithmList()) {
      List<IntegerSolution> solutionsPerAlgorithm = nonDominatedSolutions.stream().filter(solution -> algorithm.getAlgorithmTag().equals(solutionAttribute.getAttribute(solution))).collect(Collectors.toList());

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
          List<DummyIntegerSolution> nonDominatedSolutions) {
    String referenceFrontFileName = outputDirectoryName + "/" + problem.getReferenceFront();

    new SolutionListOutput(nonDominatedSolutions)
            .printObjectivesToFile(referenceFrontFileName, ",");
  }

  private void writeReferenceSetFile(
          String outputDirectoryName,
          ExperimentProblem<?> problem,
          List<DummyIntegerSolution> nonDominatedSolutions) {
    String referenceSetFileName = outputDirectoryName + "/" + problem.getTag() + ".ps";
    new SolutionListOutput(nonDominatedSolutions).printVariablesToFile(referenceSetFileName, ",");
  }

  /**
   * Create a list of non dominated {@link IntegerSolution} solutions from the FUNx.tsv and VARx.tsv
   * files that must have been previously obtained (probably by invoking the {@link
   * ExecuteAlgorithms#run} method).
   *
   * @param problem
   * @return
   * @throws FileNotFoundException
   */
  private List<DummyIntegerSolution> getNonDominatedSolutions(ExperimentProblem<?> problem)
      throws IOException {
    NonDominatedSolutionListArchive<DummyIntegerSolution> nonDominatedSolutionArchive =
            new NonDominatedSolutionListArchive<>();

    for (ExperimentAlgorithm<?, ?> algorithm :
            experiment.getAlgorithmList().stream()
                    .filter(s -> s.getProblemTag().equals(problem.getTag()))
                    .collect(Collectors.toCollection(ArrayList::new))) {
      String problemDirectory =
              experiment.getExperimentBaseDirectory()
                      + "/data/"
                      + algorithm.getAlgorithmTag()
                      + "/"
                      + problem.getTag();

      String frontFileName =
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
      double[][] frontWithVariableValues = readVectors(paretoSetFileName, ",");
      List<DummyIntegerSolution> solutionList =
              createSolutionListFrontFiles(
                      algorithm.getAlgorithmTag(), frontWithVariableValues, frontWithObjectiveValues);
      for (DummyIntegerSolution solution : solutionList) {
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
  private File createOutputDirectory(String outputDirectoryName) {
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
  private List<DummyIntegerSolution> createSolutionListFrontFiles(
          String algorithmName, double[][] frontWithVariableValues, double[][] frontWithObjectiveValues) {
    if (frontWithVariableValues.length
            != frontWithObjectiveValues.length) {
      throw new JMetalException(
              "The number of solutions in the variable and objective fronts are not equal");
    } else if (frontWithObjectiveValues.length == 0) {
      throw new JMetalException("The front of solutions is empty");
    }

    GenericSolutionAttribute<DummyIntegerSolution, String> solutionAttribute =
            new GenericSolutionAttribute<>();

    int numberOfVariables = frontWithVariableValues[0].length;
    int numberOfObjectives = frontWithObjectiveValues[0].length;

    List<DummyIntegerSolution> solutionList = new ArrayList<>();
    for (int i = 0; i < frontWithVariableValues.length; i++) {
      DummyIntegerSolution solution = new DummyIntegerSolution(numberOfVariables, numberOfObjectives);
      for (int vars = 0; vars < numberOfVariables; vars++) {
        solution.variables().set(vars, (int)frontWithVariableValues[i][vars]);
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
  private static class DummyIntegerSolution extends AbstractSolution<Integer>
          implements IntegerSolution {

    public DummyIntegerSolution(int numberOfVariables, int numberOfObjectives) {
      super(numberOfVariables, numberOfObjectives);
    }

    @Override
    public Solution<Integer> copy() {
      return null;
    }

    /**
     * @deprecated Use {@link #getBounds(int)}{@link Bounds#getLowerBound()
     * .getLowerBound()} instead.
     */

    @Override
    public Bounds<Integer> getBounds(int index) {
      return null;
    }
  }
}
