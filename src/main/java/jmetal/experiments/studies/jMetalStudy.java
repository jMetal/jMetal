//  jMetalStudy.java
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
import jmetal.util.Configuration;
import jmetal.util.JMetalException;

import java.io.IOException;

/**
 * Class implementing an example experimental study. Three algorithms are
 * compared when solving the benchmarks, and the hypervolume,
 * spread and additive epsilon indicators are used for performance assessment.
 */
public class jMetalStudy extends Experiment {

  public jMetalStudy() {
    experimentName_ = "jMetalStudy";
    independentRuns_ = 30;
    algorithmNameList_ = new String[] {"NSGAII", "SMPSO", "GDE3"};
    problemList_ = new String[] {"ZDT1", "ZDT2", "ZDT3", "ZDT4", "ZDT6"};
    paretoFrontFileList_ = new String[] {"ZDT1.pf", "ZDT2.pf", "ZDT3.pf", "ZDT4.pf", "ZDT6.pf"};
    indicatorList_ = new String[] {"HV", "SPREAD", "EPSILON"};
    experimentBaseDirectory_ = "/Users/antelverde/Softw/pruebas/jmetal/" + experimentName_;
    paretoFrontDirectory_ = "/Users/antelverde/Softw/pruebas/data/paretoFronts";
    numberOfExecutionThreads_ = 6;

    generateReferenceParetoFronts_ = false;
    runTheAlgorithms_ = true;
    generateBoxplots_ = true;
    boxplotRows_ = 2;
    boxplotColumns_ = 2;
    boxplotNotch_ = true;
    generateFriedmanTables_ = true;
    generateLatexTables_ = true;
    generateWilcoxonTables_ = true;
    generateSetCoverageTables_ = true;
    generateQualityIndicators_ = true;
  }

  /**
   * Main method
   *
   * @param args
   * @throws jmetal.util.JMetalException
   * @throws java.io.IOException
   */
  public static void main(String[] args) throws JMetalException, IOException {
    jMetalStudy exp = new jMetalStudy();

    Configuration.logger_.info("START");

    exp.initExperiment(args);

    Configuration.logger_.info(""+exp);

    exp.runExperiment();
  } 
} 


