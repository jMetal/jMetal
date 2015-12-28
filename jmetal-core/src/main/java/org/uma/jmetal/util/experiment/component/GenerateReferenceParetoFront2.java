//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
// package org.uma.jmetal.util.experiment.component;

package org.uma.jmetal.util.experiment.component;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.ExperimentConfiguration;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class computes a reference Pareto front from a set of files. Once the algorithms of an
 * experiment have been executed through running an instance of class {@link ExecuteAlgorithms},
 * all the obtained fronts of all the algorithms are gathered per problem; then, the dominated solutions
 * are removed and the final result is a file per problem containing the reference Pareto front.
 *
 * By default, the files are stored in a directory called "referenceFront", which is located in the
 * experiment base directory. Each front is named following the scheme "problemName.pf".
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenerateReferenceParetoFront2 implements ExperimentComponent{
  private static final String DEFAULT_OUTPUT_DIRECTORY = "referenceFronts" ;

  private final ExperimentConfiguration<?, ?> experimentConfiguration;

  GenericSolutionAttribute<DoubleSolution, String> solutionAttribute;
  public GenerateReferenceParetoFront2(ExperimentConfiguration experimentConfiguration) {
    this.experimentConfiguration = experimentConfiguration ;
    this.experimentConfiguration.removeDuplicatedAlgorithms();

    solutionAttribute = new GenericSolutionAttribute<DoubleSolution, String>()  ;
  }

  /**
   * The run() method creates de output directory and compute the fronts
   */
  @Override
  public void run() throws IOException {
    String outputDirectoryName ;
    outputDirectoryName = experimentConfiguration.getExperimentBaseDirectory() + "/" + DEFAULT_OUTPUT_DIRECTORY ;

    File outputDirectory = createOutputDirectory(outputDirectoryName) ;

    List<String> referenceFrontFileNames = new LinkedList<>() ;
    for (Problem<?> problem : experimentConfiguration.getProblemList()) {
      NonDominatedSolutionListArchive<DoubleSolution> nonDominatedSolutionArchive =
          new NonDominatedSolutionListArchive<DoubleSolution>() ;

      for (TaggedAlgorithm<?> algorithm : experimentConfiguration.getAlgorithmList()) {
        String problemDirectory = experimentConfiguration.getExperimentBaseDirectory() + "/data/" +
            algorithm.getTag() + "/" + problem.getName() ;

        for (int i = 0 ; i < experimentConfiguration.getIndependentRuns(); i++) {
          String frontFileName = problemDirectory + "/" + experimentConfiguration.getOutputParetoFrontFileName() +
              i + ".tsv";
          String paretoSetFileName = problemDirectory + "/" + experimentConfiguration.getOutputParetoSetFileName() +
              i + ".tsv";
          Front frontWithObjectiveValues = new ArrayFront(frontFileName) ;
          Front frontWithVariableValues = new ArrayFront(paretoSetFileName) ;
          List<DoubleSolution> solutionList =
              createSolutionList(algorithm.getTag(), frontWithVariableValues, frontWithObjectiveValues) ;
          for (DoubleSolution solution : solutionList) {
            nonDominatedSolutionArchive.add(solution) ;
          }
        }
      }
      String referenceFrontFileName = outputDirectoryName + "/" + problem.getName() + ".rf" ;
      referenceFrontFileNames.add(problem.getName() + ".rf");
      new SolutionListOutput(nonDominatedSolutionArchive.getSolutionList())
          .printObjectivesToFile(referenceFrontFileName);

      String referenceSetFileName = outputDirectoryName + "/" + problem.getName() + ".rs" ;
      new SolutionListOutput(nonDominatedSolutionArchive.getSolutionList())
          .printVariablesToFile(referenceSetFileName);

      for (TaggedAlgorithm<?> algorithm : experimentConfiguration.getAlgorithmList()) {
        List<DoubleSolution> solutionsPerAlgorithm = new ArrayList<>() ;
        for (DoubleSolution solution : nonDominatedSolutionArchive.getSolutionList()) {
          if (algorithm.getTag().equals(solutionAttribute.getAttribute(solution))) {
            solutionsPerAlgorithm.add(solution) ;
          }
        }

        new SolutionListOutput(solutionsPerAlgorithm)
            .printObjectivesToFile(
                outputDirectoryName + "/" + problem.getName() + "." +
                    algorithm.getTag() + ".rf"
            );
        new SolutionListOutput(solutionsPerAlgorithm)
            .printVariablesToFile(
                outputDirectoryName + "/" + problem.getName() + "." +
                    algorithm.getTag() + ".rs"
            );
      }
    }

    experimentConfiguration.setReferenceFrontDirectory(outputDirectoryName);
    experimentConfiguration.setReferenceFrontFileNames(referenceFrontFileNames);
  }

  private File createOutputDirectory(String outputDirectoryName) {
    File outputDirectory ;
    outputDirectory = new File(outputDirectoryName) ;
    if (!outputDirectory.exists()) {
      boolean result = new File(outputDirectoryName).mkdir() ;
      JMetalLogger.logger.info("Creating " + outputDirectoryName + ". Status = " + result);
    }

    return outputDirectory ;
  }

  private List<DoubleSolution> createSolutionList(String algorithmName, Front frontWithVariableValues, Front frontWithObjectiveValues) {
    if (frontWithVariableValues.getNumberOfPoints() != frontWithObjectiveValues.getNumberOfPoints()) {
      throw new JMetalException("The number of solutions in the variable and objective fronts are not equal") ;
    } else if (frontWithObjectiveValues.getNumberOfPoints() == 0) {
      throw new JMetalException("The front of solutions is empty") ;
    }

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
