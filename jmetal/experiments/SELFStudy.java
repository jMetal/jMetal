//  StandardStudy.java
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

package jmetal.experiments;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.settings.GDE3_Settings;
import jmetal.experiments.settings.MOCell_Settings;
import jmetal.experiments.settings.MOEAD_DRA_Settings;
import jmetal.experiments.settings.MOEAD_Settings;
import jmetal.experiments.settings.NSGAII_Settings;
import jmetal.experiments.settings.SELF_Settings;
import jmetal.experiments.settings.SPEA2_Settings;
import jmetal.experiments.settings.SMPSO_Settings;
import jmetal.experiments.util.RBoxplot;
import jmetal.experiments.util.RWilcoxon;
import jmetal.util.JMException;

/**
 * Class implementing a typical experimental study. Five algorithms are 
 * compared when solving the ZDT, DTLZ, and WFG benchmarks, and the hypervolume,
 * spread and additive epsilon indicators are used for performance assessment.
 */
public class SELFStudy extends Experiment {

  /**
   * Configures the algorithms in each independent run
   * @param problemName The problem to solve
   * @param problemIndex
   * @throws ClassNotFoundException 
   */
  public void algorithmSettings(String problemName, 
  		                          int problemIndex, 
  		                          Algorithm[] algorithm) throws ClassNotFoundException {
    try {
      int numberOfAlgorithms = algorithmNameList_.length;

      HashMap[] parameters = new HashMap[numberOfAlgorithms];

      for (int i = 0; i < numberOfAlgorithms; i++) {
        parameters[i] = new HashMap();
      } // for

      if (!paretoFrontFile_[problemIndex].equals("")) {
        for (int i = 0; i < numberOfAlgorithms; i++)
          parameters[i].put("paretoFrontFile_", paretoFrontFile_[problemIndex]);
        } // if

        algorithm[0] = new SELF_Settings(problemName).configure(parameters[0]);
        algorithm[1] = new MOEAD_Settings(problemName).configure(parameters[1]);
        algorithm[2] = new MOEAD_DRA_Settings(problemName).configure(parameters[2]);
      } catch (IllegalArgumentException ex) {
      Logger.getLogger(SELFStudy.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(SELFStudy.class.getName()).log(Level.SEVERE, null, ex);
    } catch  (JMException ex) {
      Logger.getLogger(SELFStudy.class.getName()).log(Level.SEVERE, null, ex);
    }
  } // algorithmSettings

  /**
   * Main method
   * @param args
   * @throws JMException
   * @throws IOException
   */
  public static void main(String[] args) throws JMException, IOException {
    SELFStudy exp = new SELFStudy();

    exp.experimentName_ = "SELFStudy";
    exp.algorithmNameList_ = new String[]{
                                "SELF", "MOEAD", "DRA"};
    exp.problemList_ = new String[]{"LZ09_F1","LZ09_F2","LZ09_F3", "LZ09_F4",
        "LZ09_F5","LZ09_F7","LZ09_F8","LZ09_F9"};
    exp.paretoFrontFile_ = new String[]{"LZ09_F1.pf", "LZ09_F2.pf","LZ09_F3.pf","LZ09_F4.pf",
        "LZ09_F5.pf", "LZ09_F7.pf","LZ09_F8.pf","LZ09_F9.pf"};


    exp.indicatorList_ = new String[]{"HV", "SPREAD", "EPSILON"};

    int numberOfAlgorithms = exp.algorithmNameList_.length;

    exp.experimentBaseDirectory_ = "/Users/antelverde/Softw/pruebas/jmetal/" +
                                   exp.experimentName_;
    exp.paretoFrontDirectory_ = "/Users/antelverde/Dropbox/jMetal/data/paretoFronts";

    exp.algorithmSettings_ = new Settings[numberOfAlgorithms];

    exp.independentRuns_ = 30;

    // Run the experiments
    int numberOfThreads ;
    exp.runExperiment(numberOfThreads = 8) ;

    // Generate latex tables
    exp.generateLatexTables() ;

    // Configuring scripts for ZDT
    int rows  ;
    int columns  ;
    String prefix ;
    String [] problems ;
    boolean notch ;
 
    // Configuring scripts for ZDT
    rows = 3 ;
    columns = 3 ;
    prefix = new String("LZ09");
    problems = new String[]{"LZ09_F1", "LZ09_F2","LZ09_F3", "LZ09_F4","LZ09_F5", "LZ09_F7", "LZ09_F8", "LZ09_F9"} ;
    
    RBoxplot.generateScripts(rows, columns, problems, prefix, notch = false, exp) ;
    RWilcoxon.generateScripts(problems, prefix, exp) ;
 
  } // main
} // StandardStudy


