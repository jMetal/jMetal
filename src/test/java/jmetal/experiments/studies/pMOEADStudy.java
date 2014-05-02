//  pMOEADStudy.java
//
//  Author:
//       Andre Siqueira
////
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

import jmetal.core.Algorithm;
import jmetal.experiments.Experiment;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.MOEAD_Settings;
import jmetal.experiments.settings.pMOEAD_Settings;
import jmetal.experiments.util.Friedman;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kurumin
 */
public class pMOEADStudy extends Experiment {

  /**
   * Configures the algorithms in each independent run
   *
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

      if (!(paretoFrontFile_[problemIndex] == null) && !paretoFrontFile_[problemIndex].equals("")) {
        for (int i = 0; i < numberOfAlgorithms; i++) {
          parameters[i].put("paretoFrontFile_", paretoFrontFile_[problemIndex]);
        }
      } // if

      algorithm[0] = new MOEAD_Settings(problemName).configure(parameters[0]);
      for (int i = 1; i < numberOfAlgorithms; i++) {
        algorithm[i] = new pMOEAD_Settings(problemName).configure(parameters[i]);
      }
      algorithm[1].setInputParameter("numberOfThreads", 1);
      algorithm[2].setInputParameter("numberOfThreads", 2);
      algorithm[3].setInputParameter("numberOfThreads", 4);

    } catch (IllegalArgumentException ex) {
      Logger.getLogger(pMOEADStudy.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(pMOEADStudy.class.getName()).log(Level.SEVERE, null, ex);
    } catch (JMException ex) {
      Logger.getLogger(pMOEADStudy.class.getName()).log(Level.SEVERE, null, ex);
    }
  } // algorithmSettings

  /**
   * Main method
   *
   * @param args
   * @throws JMException
   * @throws IOException
   */
  public static void main(String[] args) throws JMException, IOException {
    pMOEADStudy exp = new pMOEADStudy();

    exp.experimentName_ = "pMOEADStudy";
    exp.algorithmNameList_ = new String[]{"MOEADseq", "pMOEAD1T", "pMOEAD2T", "pMOEAD4T"};
    exp.problemList_ = new String[]{"LZ09_F1", "LZ09_F2", "LZ09_F3", "LZ09_F4", "LZ09_F5",
            "LZ09_F6", "LZ09_F7", "LZ09_F8", "LZ09_F9"};
    exp.paretoFrontFile_ = new String[9];

    exp.indicatorList_ = new String[]{"EPSILON"};

    int numberOfAlgorithms = exp.algorithmNameList_.length;

    exp.experimentBaseDirectory_ = "D:/Sheffield/experiments/" + exp.experimentName_;
    exp.paretoFrontDirectory_ = "";

    exp.algorithmSettings_ = new Settings[numberOfAlgorithms];

    exp.independentRuns_ = 10;

    exp.initExperiment();

    // Run the experiments
    int numberOfThreads;
    exp.runExperiment(numberOfThreads = 1);

    exp.generateQualityIndicators();

    // Generate latex tables
    exp.generateLatexTables();

    // Configure the R scripts to be generated
    int rows;
    int columns;
    String prefix;
    String[] problems;
    boolean notch;

    // Configuring scripts for LZ09
    rows = 3;
    columns = 3;
    prefix = new String("LZ09");
    problems = new String[]{"LZ09_F1", "LZ09_F2", "LZ09_F3", "LZ09_F4", "LZ09_F5",
            "LZ09_F6", "LZ09_F7", "LZ09_F8", "LZ09_F9"};

    exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch = false, exp);
    exp.generateRWilcoxonScripts(problems, prefix, exp);

    // Applying Friedman test
    Friedman test = new Friedman(exp);
    test.executeTest("EPSILON");
  } // main
} // pMOEADStudy
