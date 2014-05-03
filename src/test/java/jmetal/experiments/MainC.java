//  MainC.java
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

package jmetal.experiments;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Class for running algorithms reading the configuration from properties files
 */
public class MainC {
  public static Logger      logger_ ;      // Logger object
  public static FileHandler fileHandler_ ; // FileHandler object

  /**
   * @param args Command line arguments.
   * @throws jmetal.util.JMException
   * @throws java.io.IOException
   * @throws SecurityException
   * Usage: three options
   *      - jmetal.experiments.Main algorithmName
   *      - jmetal.experiments.Main algorithmName problemName
   *      - jmetal.experiments.Main algorithmName problemName paretoFrontFile
   * @throws ClassNotFoundException 
   */
  public static void main(String [] args) throws
                                  JMException, SecurityException, IOException, 
                                  IllegalArgumentException, IllegalAccessException, 
                                  ClassNotFoundException {
    Problem   problem   ;         // The problem to solve
    Algorithm algorithm ;         // The algorithm to use

    QualityIndicator indicators ; // Object to get quality indicators

    Settings settings = null;

    String algorithmName   = "" ;
    String problemName     = "Kursawe" ; // Default problem
    String paretoFrontFile = "" ;

    indicators = null ;
    problem = null ;

    if (args.length == 0) { //
      System.err.println("Sintax error. Usage:") ;
      System.err.println("a) jmetal.experiments.Main algorithmName ") ;
      System.err.println("b) jmetal.experiments.Main algorithmName problemName") ;
      System.err.println("c) jmetal.experiments.Main algorithmName problemName paretoFrontFile") ;
      System.exit(-1) ;
    } // if
    else if (args.length == 1) { // algorithmName
      algorithmName = args[0] ;
      Object [] settingsParams = {problemName} ;
      settings = (new SettingsFactory()).getSettingsObject(algorithmName, settingsParams) ;
      } // if
    else if (args.length == 2) { // algorithmName problemName
      algorithmName = args[0] ;
      problemName = args[1] ;
      Object [] settingsParams = {problemName} ;
      settings = (new SettingsFactory()).getSettingsObject(algorithmName, settingsParams) ;
      } // if
    else if (args.length == 3) { // algorithmName problemName paretoFrontFile
      algorithmName = args[0] ;
      problemName = args[1] ;
      paretoFrontFile = args[2] ;
      Object [] settingsParams = {problemName} ;
      settings = (new SettingsFactory()).getSettingsObject(algorithmName, settingsParams) ;
    } // if

    // Logger object and file to store log messages
    logger_      = Configuration.logger_ ;
    fileHandler_ = new FileHandler(algorithmName + ".log");
    logger_.addHandler(fileHandler_) ;

    Properties configuration = new Properties();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(algorithmName+".conf"));
    configuration.load(isr);

    algorithm = settings.configure(configuration);
    isr.close();

    if (args.length == 3) {
    	Problem p = algorithm.getProblem() ;
      indicators = new QualityIndicator(p, paretoFrontFile);
    }
    
    // Execute the Algorithm
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;

    // Result messages
    logger_.info("Total execution time: "+estimatedTime + "ms");
    logger_.info("Objectives values have been writen to file FUN");
    population.printObjectivesToFile("FUN");
    logger_.info("Variables values have been writen to file VAR");
    population.printVariablesToFile("VAR");

    if (indicators != null) {
      logger_.info("Quality indicators") ;
      logger_.info("Hypervolume: " + indicators.getHypervolume(population)) ;
      logger_.info("GD         : " + indicators.getGD(population)) ;
      logger_.info("IGD        : " + indicators.getIGD(population)) ;
      logger_.info("Spread     : " + indicators.getSpread(population)) ;
      logger_.info("Epsilon    : " + indicators.getEpsilon(population)) ;

      if (algorithm.getOutputParameter("evaluations") != null) {
        Integer evals = (Integer)algorithm.getOutputParameter("evaluations") ;
        int evaluations = (Integer)evals.intValue();
        logger_.info("Speed      : " + evaluations + " evaluations") ;
      } // if
    } // if
  } //main
} // Main
