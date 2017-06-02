package org.uma.jmetal.util.experiment.component;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class computes the reference Pareto set and front from a set of data files containing the variable
 * (VARx.tsv file) and objective (FUNx.tsv) values. A requirement is that the variable values MUST correspond to
 * {@link DoubleSolution} solutions, i.e., the solved problems must be instances of {@link DoubleProblem}.
 *
 * Once the algorithms of an experiment have been executed through running an instance of class {@link ExecuteAlgorithms},
 * all the obtained fronts of all the algorithms are gathered per problem; then, the dominated solutions are removed
 * thus yielding to the reference Pareto front.
 *
 * By default, the files are stored in a directory called "referenceFront", which is located in the
 * experiment base directory. The following files are generated per problem:
 * - "problemName.pf": the reference Pareto front.
 * - "problemName.ps": the reference Pareto set (i.e., the variable values of the solutions of the reference
 *                     Pareto front.
 * - "problemName.algorithmName.pf": the objectives values of the contributed solutions by
 *                                   the algorithm called "algorithmName" to "problemName.pf"
 * - "problemName.algorithmName.ps": the variable values of the contributed solutions by
 *                                   the algorithm called "algorithmName" to "problemName.ps"
 *
 * This method must define one field of the {@link Experiment} object by invoking the following method:
 * - {@link Experiment#setReferenceFrontFileNames}
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenerateReferenceParetoSetAndFrontFromDoubleSolutions implements ExperimentComponent{
  private final Experiment<?, ?> experiment;

  public GenerateReferenceParetoSetAndFrontFromDoubleSolutions(Experiment<?, ?> experimentConfiguration) {
    this.experiment = experimentConfiguration ;

    experiment.removeDuplicatedAlgorithms();
  }

  /**
   * The run() method creates de output directory and compute the fronts
   */
  @Override
  public void run() throws IOException {
    String outputDirectoryName = experiment.getReferenceFrontDirectory() ;
    createOutputDirectory(outputDirectoryName) ;

    List<String> referenceFrontFileNames = new LinkedList<>() ;

    for (ExperimentProblem<?> problem : experiment.getProblemList()) {
      List<DoubleSolution> nonDominatedSolutions = getNonDominatedSolutions(problem.getProblem()) ;

      referenceFrontFileNames.add(problem.getTag() + ".rf");

      writeReferenceFrontFile(outputDirectoryName, problem.getProblem(), nonDominatedSolutions) ;
      writeReferenceSetFile(outputDirectoryName, problem.getProblem(), nonDominatedSolutions) ;

      writeFilesWithTheSolutionsContributedByEachAlgorithm(outputDirectoryName, problem.getProblem(), nonDominatedSolutions) ;
    }

    experiment.setReferenceFrontFileNames(referenceFrontFileNames);
  }

  private void writeFilesWithTheSolutionsContributedByEachAlgorithm(
      String outputDirectoryName, Problem<?> problem,
      List<DoubleSolution> nonDominatedSolutions) throws IOException {
    GenericSolutionAttribute<DoubleSolution, String> solutionAttribute = new GenericSolutionAttribute<DoubleSolution, String>()  ;

    for (ExperimentAlgorithm<?,?> algorithm : experiment.getAlgorithmList()) {
      List<DoubleSolution> solutionsPerAlgorithm = new ArrayList<>() ;
      for (DoubleSolution solution : nonDominatedSolutions) {
        if (algorithm.getAlgorithmTag().equals(solutionAttribute.getAttribute(solution))) {
          solutionsPerAlgorithm.add(solution) ;
        }
      }

      new SolutionListOutput(solutionsPerAlgorithm)
          .printObjectivesToFile(
              outputDirectoryName + "/" + problem.getName() + "." +
                  algorithm.getAlgorithmTag() + ".rf"
          );
      new SolutionListOutput(solutionsPerAlgorithm)
          .printVariablesToFile(
              outputDirectoryName + "/" + problem.getName() + "." +
                  algorithm.getAlgorithmTag() + ".rs"
          );
    }
  }

  private void writeReferenceFrontFile(
      String outputDirectoryName, Problem<?> problem, List<DoubleSolution> nonDominatedSolutions) throws IOException {
    String referenceFrontFileName = outputDirectoryName + "/" + problem.getName() + ".rf" ;

    new SolutionListOutput(nonDominatedSolutions).printObjectivesToFile(referenceFrontFileName);
  }

  private void writeReferenceSetFile(
      String outputDirectoryName, Problem<?> problem, List<DoubleSolution> nonDominatedSolutions) throws IOException {
    String referenceSetFileName = outputDirectoryName + "/" + problem.getName() + ".rs" ;
    new SolutionListOutput(nonDominatedSolutions).printVariablesToFile(referenceSetFileName);
  }

  /**
   * Create a list of non dominated {@link DoubleSolution} solutions from the FUNx.tsv and VARx.tsv files that
   * must have been previously obtained (probably by invoking the {@link ExecuteAlgorithms#run} method).
   *
   * @param problem
   * @return
   * @throws FileNotFoundException
   */
  private List<DoubleSolution> getNonDominatedSolutions(Problem<?> problem) throws FileNotFoundException {
    NonDominatedSolutionListArchive<DoubleSolution> nonDominatedSolutionArchive =
        new NonDominatedSolutionListArchive<DoubleSolution>() ;

    for (ExperimentAlgorithm<?,?> algorithm : experiment.getAlgorithmList()) {
      String problemDirectory = experiment.getExperimentBaseDirectory() + "/data/" +
          algorithm.getAlgorithmTag() + "/" + problem.getName() ;

      for (int i = 0; i < experiment.getIndependentRuns(); i++) {
        String frontFileName = problemDirectory + "/" + experiment.getOutputParetoFrontFileName() +
            i + ".tsv";
        String paretoSetFileName = problemDirectory + "/" + experiment.getOutputParetoSetFileName() +
            i + ".tsv";
        Front frontWithObjectiveValues = new ArrayFront(frontFileName) ;
        Front frontWithVariableValues = new ArrayFront(paretoSetFileName) ;
        List<DoubleSolution> solutionList =
            createSolutionListFrontFiles(algorithm.getAlgorithmTag(), frontWithVariableValues, frontWithObjectiveValues) ;
        for (DoubleSolution solution : solutionList) {
          nonDominatedSolutionArchive.add(solution) ;
        }
      }
    }

    return nonDominatedSolutionArchive.getSolutionList() ;
  }

  /**
   * Create the output directory where the result files will be stored
   * @param outputDirectoryName
   * @return
   */
  private File createOutputDirectory(String outputDirectoryName) {
    File outputDirectory ;
    outputDirectory = new File(outputDirectoryName) ;
    if (!outputDirectory.exists()) {
      boolean result = new File(outputDirectoryName).mkdir() ;
      JMetalLogger.logger.info("Creating " + outputDirectoryName + ". Status = " + result);
    }

    return outputDirectory ;
  }

  /**
   *
   * @param algorithmName
   * @param frontWithVariableValues
   * @param frontWithObjectiveValues
   * @return
   */
  private List<DoubleSolution> createSolutionListFrontFiles(String algorithmName, Front frontWithVariableValues, Front frontWithObjectiveValues) {
    if (frontWithVariableValues.getNumberOfPoints() != frontWithObjectiveValues.getNumberOfPoints()) {
      throw new JMetalException("The number of solutions in the variable and objective fronts are not equal") ;
    } else if (frontWithObjectiveValues.getNumberOfPoints() == 0) {
      throw new JMetalException("The front of solutions is empty") ;
    }

    GenericSolutionAttribute<DoubleSolution, String> solutionAttribute = new GenericSolutionAttribute<DoubleSolution, String>()  ;

    int numberOfVariables = frontWithVariableValues.getPointDimensions() ;
    int numberOfObjectives = frontWithObjectiveValues.getPointDimensions() ;
    DummyProblem problem = new DummyProblem(numberOfVariables, numberOfObjectives) ;

    List<DoubleSolution> solutionList = new ArrayList<>() ;
    for (int i = 0 ; i < frontWithVariableValues.getNumberOfPoints(); i++) {
      DoubleSolution solution = new DefaultDoubleSolution(problem);
      for (int vars = 0; vars < numberOfVariables; vars++) {
        solution.setVariableValue(vars, frontWithVariableValues.getPoint(i).getValues()[vars]);
      }
      for (int objs = 0; objs < numberOfObjectives; objs++) {
        solution.setObjective(objs, frontWithObjectiveValues.getPoint(i).getValues()[objs]);
      }

      solutionAttribute.setAttribute(solution, algorithmName);
      solutionList.add(solution) ;
    }

    return solutionList ;
  }

  /**
   * This private class is intended to create{@link DoubleSolution} objects from the stored values of variables and
   * objectives obtained in files after running an experiment. The values of the lower and upper limits are useless.
   */
  @SuppressWarnings("serial")
  private static class DummyProblem extends AbstractDoubleProblem {
    public DummyProblem(int numberOfVariables, int numberOfObjectives) {
      setNumberOfVariables(numberOfVariables);
      setNumberOfObjectives(numberOfObjectives);

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(-1.0);
        upperLimit.add(1.0);
      }

      setLowerLimit(lowerLimit);
      setUpperLimit(upperLimit);
    }

    @Override public void evaluate(DoubleSolution solution) {
    }
  }
}
