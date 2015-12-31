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

package org.uma.jmetal.util.experiment.component;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * This class computes the {@link QualityIndicator}s of an experiment. Once the algorithms of an
 * experiment have been executed through running an instance of class {@link ExecuteAlgorithms},
 * the list of indicators in obtained from the {@link ExperimentComponent #getIndicatorsList()} method.
 * Then, for every combination algorithm + problem, the indicators are applied to all the FUN files and
 * the resulting values are store in a file called as {@link QualityIndicator #getName()}, which is located
 * in the same directory of the FUN files.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("unchecked")
public class ComputeQualityIndicators<Result> implements ExperimentComponent {

  private final Experiment<?, Result> configuration;
  private List<GenericIndicator<? extends Solution<?>>> indicatorList ;

  public ComputeQualityIndicators(Experiment experimentConfiguration) {
    this.configuration = experimentConfiguration ;
    this.indicatorList = experimentConfiguration.getIndicatorList() ;

    configuration.removeDuplicatedAlgorithms();
  }

  @Override
  public void run() throws IOException {
    for (GenericIndicator indicator : indicatorList) {
      JMetalLogger.logger.info("Computing indicator: " + indicator.getName()); ;

      for (TaggedAlgorithm algorithm : configuration.getAlgorithmList()) {
        String algorithmDirectory ;
        algorithmDirectory = configuration.getExperimentBaseDirectory() + "/data/" +
            algorithm.getTag() ;

        for (int problemId = 0 ; problemId < configuration.getProblemList().size(); problemId++) {
          String problemDirectory = algorithmDirectory + "/" + configuration.getProblemList().get(problemId).getName() ;

          String referenceFrontDirectory = configuration.getReferenceFrontDirectory() ;
          String referenceFrontName = referenceFrontDirectory +
              "/" + configuration.getReferenceFrontFileNames().get(problemId) ;

          JMetalLogger.logger.info("RF: " + referenceFrontName); ;
          Front referenceFront = new ArrayFront(referenceFrontName) ;

          FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront) ;
          Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront) ;

          String qualityIndicatorFile = problemDirectory + "/" + indicator.getName();
          resetFile(qualityIndicatorFile);

          indicator.setReferenceParetoFront(normalizedReferenceFront);
          for (int i = 0; i < configuration.getIndependentRuns(); i++) {
            String frontFileName;
            frontFileName = problemDirectory + "/" +
                configuration.getOutputParetoFrontFileName() + i + ".tsv";

            Front front = new ArrayFront(frontFileName) ;
            Front normalizedFront = frontNormalizer.normalize(front) ;
            List<DoubleSolution> normalizedPopulation = FrontUtils .convertFrontToSolutionList(normalizedFront) ;
            Double indicatorValue = (Double)indicator.evaluate(normalizedPopulation) ;
            JMetalLogger.logger.info(indicator.getName() + ": " + indicatorValue) ;

            FileWriter os;
            try {
              os = new FileWriter(qualityIndicatorFile, true);
              os.write("" + indicatorValue + "\n");
              os.close();
            } catch (IOException ex) {
              throw new JMetalException("Error writing indicator file" + ex) ;
            }
          }

        }
      }
    }
  }

  /**
   * Deletes a file or directory if it does exist
   * @param file
   */
  private void resetFile(String file) {
    File f = new File(file);
    if (f.exists()) {
      JMetalLogger.logger.info("File " + file + " exist.");

      if (f.isDirectory()) {
        JMetalLogger.logger.info("File " + file + " is a directory. Deleting directory.");
        if (f.delete()) {
          JMetalLogger.logger.info("Directory successfully deleted.");
        } else {
          JMetalLogger.logger.info("Error deleting directory.");
        }
      } else {
        JMetalLogger.logger.info("File " + file + " is a file. Deleting file.");
        if (f.delete()) {
          JMetalLogger.logger.info("File succesfully deleted.");
        } else {
          JMetalLogger.logger.info("Error deleting file.");
        }
      }
    } else {
      JMetalLogger.logger.info("File " + file + " does NOT exist.");
    }
  }
}
