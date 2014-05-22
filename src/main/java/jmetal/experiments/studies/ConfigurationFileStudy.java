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

package jmetal.experiments.studies;

import jmetal.experiments.Experiment;
import jmetal.util.JMException;

import java.io.IOException;

/**
 * Class implementing an example experimental study. Three algorithms are
 * compared when solving the benchmarks, and the hypervolume,
 * spread and additive epsilon indicators are used for performance assessment.
 */
public class ConfigurationFileStudy extends Experiment {

  public ConfigurationFileStudy() {
    experimentName_ = "ConfigurationFileStudy";
    independentRuns_ = 0;
    algorithmNameList_ = null;
    problemList_ = null;
    paretoFrontFileList_ = null;
    indicatorList_ = null;
    experimentBaseDirectory_ = null;
    paretoFrontDirectory_ = null;
    numberOfExecutionThreads_ = 1;

    generateReferenceParetoFronts_ = false;
    runTheAlgorithms_ = false;
    generateBoxplots_ = false;
    boxplotRows_ = 0;
    boxplotColumns_ = 0;
    boxplotNotch_ = false;
    generateFriedmanTables_ = false;
    generateLatexTables_ = false;
    generateWilcoxonTables_ = false;
    generateQualityIndicators_ = false;
  }


  /**
   * Main method
   *
   * @param args
   * @throws jmetal.util.JMException
   * @throws java.io.IOException
   */
  public static void main(String[] args) throws JMException, IOException {
    ConfigurationFileStudy exp = new ConfigurationFileStudy();

    System.out.println("START");

    exp.initExperiment(args);

    System.out.println(exp);

    exp.runExperiment();
  } // main
} // ConfigurationFileStudy


