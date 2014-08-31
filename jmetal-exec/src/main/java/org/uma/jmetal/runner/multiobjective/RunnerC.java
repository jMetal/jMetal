//  RunnerC.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Francisco Luna <fluna@unex.es>
//
//  Copyright (c) 2013 Antonio J. Nebro, Francisco Luna
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

package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.experiment.SettingsFactory;
import org.uma.jmetal.qualityindicator.QualityIndicatorGetter;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileOutput.DefaultFileOutputContext;
import org.uma.jmetal.util.fileOutput.FileOutputContext;
import org.uma.jmetal.util.fileOutput.SolutionSetOutput;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Class for running algorithms reading the configuration from properties files
 */
public class RunnerC {
  /**
   * @param args Command line arguments.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws java.io.IOException
   * @throws SecurityException       
   * Usage: three options
   *       - org.uma.jmetal.runner.multiobjective.RunnerC propertiesFile
   *       - org.uma.jmetal.runner.multiobjective.RunnerC propertiesFile problemName
   *       - org.uma.jmetal.runner.multiobjective.RunnerC propertiesFile paretoFrontFile
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

    Properties configuration = new Properties();
    InputStreamReader inputStreamReader = null;

    if (args.length == 0) {
      JMetalLogger.logger.log(Level.SEVERE, "Sintax error. Usage:");
      JMetalLogger.logger.log(Level.SEVERE, "a) org.uma.jmetal.runner.multiobjective.RunnerC configurationFile ");
      JMetalLogger.logger.log(Level.SEVERE, "b) org.uma.jmetal.runner.multiobjective.RunnerC configurationFile problemName");
      JMetalLogger.logger.log(Level.SEVERE, "c) org.uma.jmetal.runner.multiobjective.RunnerC configurationFile problemName paretoFrontFile");
      throw new RuntimeException("Sintax error when invoking the program");
    } else if (args.length == 1) {
      inputStreamReader = new InputStreamReader(new FileInputStream(args[0]));
      configuration.load(inputStreamReader);

      algorithmName = configuration.getProperty("algorithm");
      Object[] settingsParams = {problemName};
      settings = (new SettingsFactory()).getSettingsObject(algorithmName, settingsParams);
    } else if (args.length == 2) {
      inputStreamReader = new InputStreamReader(new FileInputStream(args[0]));
      configuration.load(inputStreamReader);
      algorithmName = configuration.getProperty("algorithm");

      problemName = args[1];
      Object[] settingsParams = {problemName};
      settings = (new SettingsFactory()).getSettingsObject(algorithmName, settingsParams);
    } else if (args.length == 3) {
      inputStreamReader = new InputStreamReader(new FileInputStream(args[0]));
      configuration.load(inputStreamReader);
      algorithmName = configuration.getProperty("algorithm");

      problemName = args[1];
      paretoFrontFile = args[2];
      Object[] settingsParams = {problemName};
      settings = (new SettingsFactory()).getSettingsObject(algorithmName, settingsParams);
    }

    algorithm = settings.configure(configuration);
    inputStreamReader.close();

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
    JMetalLogger.logger.info("Variables values have been writen to file VAR.tsv");
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
