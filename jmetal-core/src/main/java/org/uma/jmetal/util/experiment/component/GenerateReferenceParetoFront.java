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
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.util.PointSolution;
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
 * experiment base directory. Each front is named following the scheme "problemName.rf".
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenerateReferenceParetoFront implements ExperimentComponent{
  private final Experiment<?, ?> experiment;
  
  public GenerateReferenceParetoFront(Experiment<?, ?> experimentConfiguration) {
    this.experiment = experimentConfiguration ;
    this.experiment.removeDuplicatedAlgorithms();
  }

  /**
   * The run() method creates de output directory and compute the fronts
   */
  @Override
  public void run() throws IOException {
    String outputDirectoryName = experiment.getReferenceFrontDirectory() ;

    File outputDirectory = createOutputDirectory(outputDirectoryName) ;

    List<String> referenceFrontFileNames = new LinkedList<>() ;
    for (Problem<?> problem : experiment.getProblemList()) {
      NonDominatedSolutionListArchive<PointSolution> nonDominatedSolutionArchive =
          new NonDominatedSolutionListArchive<PointSolution>() ;

      for (TaggedAlgorithm<?> algorithm : experiment.getAlgorithmList()) {
        String problemDirectory = experiment.getExperimentBaseDirectory() + "/data/" +
            algorithm.getTag() + "/" + problem.getName() ;

        for (int i = 0; i < experiment.getIndependentRuns(); i++) {
          String frontFileName = problemDirectory + "/" + experiment.getOutputParetoFrontFileName() +
              i + ".tsv";
          Front front = new ArrayFront(frontFileName) ;
          List<PointSolution> solutionList = FrontUtils.convertFrontToSolutionList(front) ;
          GenericSolutionAttribute<PointSolution, String> solutionAttribute = new GenericSolutionAttribute<PointSolution, String>()  ;

          for (PointSolution solution : solutionList) {
            solutionAttribute.setAttribute(solution, algorithm.getTag());
            nonDominatedSolutionArchive.add(solution) ;
          }
        }
      }
      String referenceSetFileName = outputDirectoryName + "/" + problem.getName() + ".rf" ;
      referenceFrontFileNames.add(problem.getName() + ".rf");
      new SolutionListOutput(nonDominatedSolutionArchive.getSolutionList())
          .printObjectivesToFile(referenceSetFileName);

      writeFilesWithTheSolutionsContributedByEachAlgorithm(outputDirectoryName, problem,
          nonDominatedSolutionArchive.getSolutionList()) ;
    }

    experiment.setReferenceFrontFileNames(referenceFrontFileNames);
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

  private void writeFilesWithTheSolutionsContributedByEachAlgorithm(
      String outputDirectoryName, Problem<?> problem,
      List<PointSolution> nonDominatedSolutions) throws IOException {
    GenericSolutionAttribute<PointSolution, String> solutionAttribute = new GenericSolutionAttribute<PointSolution, String>()  ;

    for (TaggedAlgorithm<?> algorithm : experiment.getAlgorithmList()) {
      List<PointSolution> solutionsPerAlgorithm = new ArrayList<>() ;
      for (PointSolution solution : nonDominatedSolutions) {
        if (algorithm.getTag().equals(solutionAttribute.getAttribute(solution))) {
          solutionsPerAlgorithm.add(solution) ;
        }
      }

      new SolutionListOutput(solutionsPerAlgorithm)
          .printObjectivesToFile(
              outputDirectoryName + "/" + problem.getName() + "." +
                  algorithm.getTag() + ".rf"
          );
    }
  }
}
