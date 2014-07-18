//  ConfigurationFileStudy.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2012 Antonio J. Nebro
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

package org.uma.jmetal.runner.experiments;

import org.uma.jmetal.experiment.Experiment;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;

import java.io.IOException;

/**
 * Class implementing an example experimental study. Three algorithms are
 * compared when solving the benchmarks, and the hypervolume,
 * spread and additive epsilon indicators are used for performance assessment.
 */
public class ConfigurationFileStudy extends Experiment {

  public ConfigurationFileStudy() {
    experimentName = "ConfigurationFileStudy";
    independentRuns = 0;
    algorithmNameList = null;
    problemList = null;
    paretoFrontFileList = null;
    indicatorList = null;
    experimentBaseDirectory = null;
    paretoFrontDirectory = null;
    numberOfExecutionThreads = 1;

    generateReferenceParetoFronts = false;
    runTheAlgorithms = false;
    generateBoxplots = false;
    boxplotRows = 0;
    boxplotColumns = 0;
    boxplotNotch = false;
    generateFriedmanTables = false;
    generateLatexTables = false;
    generateWilcoxonTables = false;
    generateQualityIndicators = false;
  }


  /**
   * Main method
   *
   * @param args
   * @throws org.uma.jmetal.util.JMetalException
   * @throws java.io.IOException
   */
  public static void main(String[] args) throws JMetalException, IOException {
    ConfigurationFileStudy exp = new ConfigurationFileStudy();

    Configuration.logger.info("START");

    exp.initExperiment(args);

    Configuration.logger.info(""+exp);

    exp.runExperiment();
  } 
} 

