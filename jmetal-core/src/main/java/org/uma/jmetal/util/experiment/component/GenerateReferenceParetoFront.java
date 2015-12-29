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
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;

import java.io.File;
import java.io.IOException;
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
public class GenerateReferenceParetoFront implements ExperimentComponent{
  private static final String DEFAULT_OUTPUT_DIRECTORY = "referenceFronts" ;

  private final Experiment<?, ?> experimentConfiguration;
  
  public GenerateReferenceParetoFront(Experiment experimentConfiguration) {
    this.experimentConfiguration = experimentConfiguration ;
    this.experimentConfiguration.removeDuplicatedAlgorithms();
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
          Front front = new ArrayFront(frontFileName) ;
          List<DoubleSolution> solutionList = FrontUtils.convertFrontToSolutionList(front) ;
          for (DoubleSolution solution : solutionList) {
            nonDominatedSolutionArchive.add(solution) ;
          }
        }
      }
      String referenceSetFileName = outputDirectoryName + "/" + problem.getName() + ".rf" ;
      referenceFrontFileNames.add(problem.getName() + ".rf");
      new SolutionListOutput(nonDominatedSolutionArchive.getSolutionList())
          .printObjectivesToFile(referenceSetFileName); ;
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
}
