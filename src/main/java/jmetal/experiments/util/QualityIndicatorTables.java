//  QualityIndicatorTables.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
//
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
//

package jmetal.experiments.util;

import jmetal.experiments.Experiment;
import jmetal.qualityIndicator.Epsilon;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.InvertedGenerationalDistance;
import jmetal.qualityIndicator.Spread;
import jmetal.util.Configuration;
import jmetal.util.JMetalException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Antonio J. Nebro on 17/02/14.
 * <p/>
 * This class generates Latex tables with the values of quality indicators when applied to the result of an experiment
 */
public class QualityIndicatorTables implements IExperimentOutput {
  Experiment experiment_;

  public QualityIndicatorTables(Experiment experiment) {
    experiment_ = experiment;
  }


  @Override
  public void generate() throws JMetalException {
    String [] paretoFront = new String[experiment_.getProblemList().length];

    for (int i = 0; i < experiment_.getProblemList().length; i++) {
      if (experiment_.generateReferenceParetoFronts()) {
        paretoFront[i] = experiment_.getExperimentBaseDirectory()
            + "/referenceFronts" + "/" + experiment_.getProblemList()[i] + ".pf";
      } else {
        paretoFront[i] =
            experiment_.getParetoFrontDirectory() + "/" + experiment_.getParetoFrontFileList()[i];
      }
      Configuration.logger_.info("Pareto front " + i + ": " + paretoFront[i]);
    }

    if (experiment_.getIndicatorList().length > 0) {

      for (int algorithmIndex = 0;
          algorithmIndex < experiment_.getAlgorithmNameList().length; algorithmIndex++) {

        String algorithmDirectory;
        algorithmDirectory = experiment_.getExperimentBaseDirectory()
            + "/data/" + experiment_.getAlgorithmNameList()[algorithmIndex] + "/";

        for (int problemIndex = 0;
            problemIndex < experiment_.getProblemList().length; problemIndex++) {

          String problemDirectory = algorithmDirectory + experiment_.getProblemList()[problemIndex];

          for (String indicator : experiment_.getIndicatorList()) {
            Configuration.logger_.info("Experiment - Quality indicator: " + indicator);

            resetFile(problemDirectory + "/" + indicator);

            for (int numRun = 0; numRun < experiment_.getIndependentRuns(); numRun++) {

              String outputParetoFrontFilePath;
              outputParetoFrontFilePath = problemDirectory + "/FUN." + numRun;
              String solutionFrontFile = outputParetoFrontFilePath;
              String qualityIndicatorFile = problemDirectory;
              double value = 0;

              double[][] trueFront = new Hypervolume().utils_.readFront(paretoFront[problemIndex]);

              if ("HV".equals(indicator)) {

                Hypervolume indicators = new Hypervolume();
                double[][] solutionFront =
                    indicators.utils_.readFront(solutionFrontFile);
                value = indicators.hypervolume(solutionFront, trueFront, trueFront[0].length);

                qualityIndicatorFile = qualityIndicatorFile + "/HV";
              }
              if ("SPREAD".equals(indicator)) {
                Spread indicators = new Spread();
                double[][] solutionFront =
                    indicators.utils_.readFront(solutionFrontFile);
                value = indicators.spread(solutionFront, trueFront, trueFront[0].length);

                qualityIndicatorFile = qualityIndicatorFile + "/SPREAD";
              }
              if ("IGD".equals(indicator)) {
                InvertedGenerationalDistance indicators = new InvertedGenerationalDistance();
                double[][] solutionFront =
                    indicators.utils_.readFront(solutionFrontFile);
                value = indicators
                    .invertedGenerationalDistance(solutionFront, trueFront, trueFront[0].length);

                qualityIndicatorFile = qualityIndicatorFile + "/IGD";
              }
              if ("EPSILON".equals(indicator)) {
                Epsilon indicators = new Epsilon();
                double[][] solutionFront =
                    indicators.utils_.readFront(solutionFrontFile);
                value = indicators.epsilon(solutionFront, trueFront, trueFront[0].length);

                qualityIndicatorFile = qualityIndicatorFile + "/EPSILON";
              }
              
              if (!qualityIndicatorFile.equals(problemDirectory)) {
                FileWriter os;
                try {
                  os = new FileWriter(qualityIndicatorFile, true);
                  os.write("" + value + "\n");
                  os.close();
                } catch (IOException ex) {
                  Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
                }
              }
            }
          }
        }
      }
    }
  }

  /**
   * @param file
   */
  private void resetFile(String file) {
    File f = new File(file);
    if (f.exists()) {
      Configuration.logger_.info("File " + file + " exist.");

      if (f.isDirectory()) {
        Configuration.logger_.info("File " + file + " is a directory. Deleting directory.");
        if (f.delete()) {
          Configuration.logger_.info("Directory successfully deleted.");
        } else {
          Configuration.logger_.info("Error deleting directory.");
        }
      } else {
        Configuration.logger_.info("File " + file + " is a file. Deleting file.");
        if (f.delete()) {
          Configuration.logger_.info("File succesfully deleted.");
        } else {
          Configuration.logger_.info("Error deleting file.");
        }
      }
    } else {
    }
  }
}
