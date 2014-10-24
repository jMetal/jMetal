//  NSGAIIBinaryRealRunner.java
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

package org.uma.jmetal.exec.runner.multiobjective;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.experiment.settings.NSGAIIBinaryRealSettings;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.DefaultFileOutputContext;
import org.uma.jmetal.util.fileoutput.FileOutputContext;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;

/** Class to configure and run the NSGA-II algorithm (Binary real encoding) */
public class NSGAIIBinaryRealRunner {
  /**
   * @param args Command line arguments.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws java.io.IOException
   * @throws SecurityException
   * Usage: three options
   *         - NSGAIIBinaryRealRunner
   *         - NSGAIIBinaryRealRunner problemName
   * @throws ClassNotFoundException 
   */
  public static void main(String[] args) throws
          Exception {

    String problemName;
    Algorithm algorithm;

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
    JMetalLogger.logger.info("Total execution time: " + estimatedTime + "ms");

    // Result messages
    FileOutputContext fileContext = new DefaultFileOutputContext("VAR.tsv") ;
    fileContext.setSeparator("\t");

    JMetalLogger.logger.info("Variables values have been writen to file VAR.tsv");
    SolutionSetOutput.printVariablesToFile(fileContext, population) ;

    fileContext = new DefaultFileOutputContext("FUN.tsv");
    fileContext.setSeparator("\t");

    SolutionSetOutput.printObjectivesToFile(fileContext, population);
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
  }
}
