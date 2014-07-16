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

package org.uma.jmetal.experiment.studies;

import org.uma.jmetal.experiment.Experiment;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;

import java.io.IOException;

/**
 * Class implementing an example experimental study. Three algorithms are
 * compared when solving the benchmarks, and the hypervolume,
 * spread and additive epsilon indicators are used for performance assessment.
 */
public class RStudy extends Experiment {

  public RStudy() {
    experimentName = "bridgeMedium";
    independentRuns = 30;
    algorithmNameList = new String[] {"NSGAII", "MOCell", "MOEAD"};
    problemList = new String[] {"EBEs"};
    paretoFrontFileList = new String[] {"EBEs.pf"};
    indicatorList = new String[] {"HV", "SPREAD", "EPSILON"};
    experimentBaseDirectory = "/home/antonio/Investigacion/Puentes/puenteMediano/puenteMediano";
    paretoFrontDirectory = "/Users/antelverde/Softw/pruebas/data/paretoFronts";
    numberOfExecutionThreads = 6;

    generateReferenceParetoFronts = false;
    runTheAlgorithms = false;
    generateBoxplots = false;
    boxplotRows = 2;
    boxplotColumns = 2;
    boxplotNotch = true;
    generateFriedmanTables = false;
    generateLatexTables = false;
    generateWilcoxonTables = false;
    generateSetCoverageTables = true;
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
    RStudy exp = new RStudy();

    Configuration.logger.info("START");

    exp.initExperiment(args);

    Configuration.logger.info(""+exp);

    exp.runExperiment();
  } 
} 


