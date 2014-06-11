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

package jmetal.runner;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.SolutionSet;
import jmetal.experiments.settings.NSGAIIBinaryReal_Settings;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.fileOutput.DefaultFileOutputContext;
import jmetal.util.fileOutput.FileOutputContext;
import jmetal.util.fileOutput.SolutionSetOutput;

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
   * @throws jmetal.util.JMException
   * @throws java.io.IOException
   * @throws SecurityException Usage: three options
   *                           - jmetal.metaheuristics.nsgaII.NSGAII_main
   *                           - jmetal.metaheuristics.nsgaII.NSGAII_main problemName
   *                           - jmetal.metaheuristics.nsgaII.NSGAII_main problemName paretoFrontFile
   */
  public static void main(String[] args) throws
    JMException,
    SecurityException,
    IOException,
    ClassNotFoundException {

    String problemName;
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    Operator selection;

    QualityIndicator indicators;

    HashMap<String, Object> parameters ;

    // Logger object and file to store log messages
    logger_ = Configuration.logger_;
    fileHandler_ = new FileHandler("NSGAII_main.log");
    logger_.addHandler(fileHandler_);

    if (args.length == 1) {
      problemName = args[0] ;
    } else {
      problemName = "Kursawe" ;
    }

    algorithm = new NSGAIIBinaryReal_Settings(problemName).configure() ;

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
