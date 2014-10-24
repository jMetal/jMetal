//  Runner.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
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

package org.uma.jmetal.exec.runner.multiobjective;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.experiment.SettingsFactory;
import org.uma.jmetal.qualityindicator.QualityIndicatorGetter;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.DefaultFileOutputContext;
import org.uma.jmetal.util.fileoutput.FileOutputContext;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Class for running algorithms
 */
public class Runner {
  /**
   * @param args Command line arguments.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws IOException
   * @throws SecurityException      
   * Usage: three options
   *       - Runner algorithmName
   *       - Runner algorithmName problemName
   *       - Runner problemName paretoFrontFile
   * @throws ClassNotFoundException
   */
  public static void main(String[] args) throws
          Exception {
    Algorithm algorithm;

    QualityIndicatorGetter indicators;

    Settings settings = null;

    String algorithmName = "";
    String problemName = "Kursawe";
    String paretoFrontFile = "";

    indicators = null;

    if (args.length == 0) {
      JMetalLogger.logger.log(Level.SEVERE, "Sintax error. Usage:\n" +
        "a) Runner algorithmName \n" +
        "b) RunnerC algorithmName problemName\n" +
        "c) org.uma.jmetal.experiment.Main algorithmName problemName paretoFrontFile");
      throw new JMetalException("Sintax error when invoking the program");
    } else if (args.length == 1) {
      algorithmName = args[0];
      Object[] settingsParams = {problemName};
      settings = (new SettingsFactory()).getSettingsObject(algorithmName, settingsParams);
    } else if (args.length == 2) {
      algorithmName = args[0];
      problemName = args[1];
      Object[] settingsParams = {problemName};
      settings = (new SettingsFactory()).getSettingsObject(algorithmName, settingsParams);
    } else if (args.length == 3) {
      algorithmName = args[0];
      problemName = args[1];
      paretoFrontFile = args[2];
      Object[] settingsParams = {problemName};
      settings = (new SettingsFactory()).getSettingsObject(algorithmName, settingsParams);
    }

    algorithm = settings.configure();

    if (args.length == 3) {
      Problem p = settings.getProblem();
      indicators = new QualityIndicatorGetter(p, paretoFrontFile);
    }

    // Execute the Algorithm
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;

    // Result messages
    JMetalLogger.logger.info("Total execution time: " + estimatedTime + "ms");

    FileOutputContext fileContext = new DefaultFileOutputContext("VAR.tsv") ;
    fileContext.setSeparator("\t");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
    SolutionSetOutput.printVariablesToFile(fileContext, population) ;

    fileContext = new DefaultFileOutputContext("FUN.tsv");
    fileContext.setSeparator("\t");
    SolutionSetOutput.printObjectivesToFile(fileContext, population);
    JMetalLogger.logger.info("Objectives values have been written to file FUN");

    if (indicators != null) {
      JMetalLogger.logger.info("Quality indicators");
      JMetalLogger.logger.info("Hypervolume: " + indicators.getHypervolume(population));
      JMetalLogger.logger.info("GD         : " + indicators.getGD(population));
      JMetalLogger.logger.info("IGD        : " + indicators.getIGD(population));
      JMetalLogger.logger.info("Spread     : " + indicators.getSpread(population));
      JMetalLogger.logger.info("Epsilon    : " + indicators.getEpsilon(population));
    }
  }
}
