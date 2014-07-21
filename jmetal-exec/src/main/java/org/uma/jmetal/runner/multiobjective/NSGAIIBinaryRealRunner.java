//  NSGAIIRunner.java
//
//  Author:
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

package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.experiment.settings.NSGAIIBinaryRealSettings;
import org.uma.jmetal.qualityIndicator.QualityIndicatorGetter;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.fileOutput.DefaultFileOutputContext;
import org.uma.jmetal.util.fileOutput.FileOutputContext;
import org.uma.jmetal.util.fileOutput.SolutionSetOutput;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Class to configure and execute the NSGA-II algorithm.
 * <p/>
 * Besides the classic NSGA-II, a steady-state version (ssNSGAII) is also
 * included (See: J.J. Durillo, A.J. Nebro, F. Luna and E. Alba
 * "On the Effect of the Steady-State Selection Scheme in
 * Multi-Objective Genetic Algorithms"
 * 5th International Conference, EMO 2009, pp: 183-197.
 * April 2009)
 */

public class NSGAIIBinaryRealRunner {
  private static Logger logger_;
  private static FileHandler fileHandler_;

  /**
   * @param args Command line arguments.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws java.io.IOException
   * @throws SecurityException Usage: three options
   *                           - org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII_main
   *                           - org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII_main problemName
   *                           - org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII_main problemName paretoFrontFile
   */
  public static void main(String[] args) throws
    JMetalException,
    SecurityException,
    IOException,
    ClassNotFoundException {

    String problemName;
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    Operator selection;

    QualityIndicatorGetter indicators;

    HashMap<String, Object> parameters ;

    // Logger object and file to store log messages
    logger_ = Configuration.logger;
    fileHandler_ = new FileHandler("NSGAII_main.log");
    logger_.addHandler(fileHandler_);

    if (args.length == 1) {
      problemName = args[0] ;
    } else {
      problemName = "Kursawe" ;
    }

    algorithm = new NSGAIIBinaryRealSettings(problemName).configure() ;

    // Execute the Algorithm
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;
    logger_.info("Total execution time: " + estimatedTime + "ms");

    // Result messages
    FileOutputContext fileContext = new DefaultFileOutputContext("VAR.tsv") ;
    fileContext.setSeparator("\t");

    logger_.info("Variables values have been writen to file VAR.tsv");
    SolutionSetOutput.printVariablesToFile(fileContext, population) ;

    fileContext = new DefaultFileOutputContext("FUN.tsv");
    fileContext.setSeparator("\t");

    SolutionSetOutput.printObjectivesToFile(fileContext, population);
    logger_.info("Objectives values have been written to file FUN.tsv");
  }
}
