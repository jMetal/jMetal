//  CEC2013PlanBStudy.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro, Juan J. Durillo
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

import jmetal.core.Algorithm;
import jmetal.experiments.Experiment;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.NSGAII_Settings;
import jmetal.experiments.util.Friedman;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class implementing a typical experimental study. Five algorithms are 
 * compared when solving the ZDT, DTLZ, and WFG benchmarks, and the hypervolume,
 * spread and additive epsilon indicators are used for performance assessment.
 */
public class jMetalExperiment extends Experiment {

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

        //algorithm[0] = new SMSEMOA_Settings(problemName).configure(parameters[0]);
        //algorithm[0] = new SMPSOCell2_Settings(problemName).configure(parameters[0]);
        //algorithm[1] = new SMPSO_Settings(problemName).configure(parameters[1]);
        //algorithm[2] = new SMPSOhv_Settings(problemName).configure(parameters[2]);
        //algorithm[2] = new SMPSOhv_Settings(problemName).configure(parameters[2]);
        //algorithm[3] = new SMSEMOA_Settings(problemName).configure(parameters[3]);
        //algorithm[0] = new SMPSORandomArchive_Settings(problemName).configure(parameters[0]);
        //algorithm[1] = new SMPSORandomSwarm_Settings(problemName).configure(parameters[1]);
        //algorithm[2] = new SMPSOhvRandomArchive_Settings(problemName).configure(parameters[2]);
      algorithm[0] = new NSGAII_Settings(problemName).configure(parameters[0]);
      //algorithm[0] = new PAES_Settings(problemName).configure(parameters[0]);
      //algorithm[1] = new PAEShv_Settings(problemName).configure(parameters[1]);
      } catch (IllegalArgumentException ex) {
      Logger.getLogger(jMetalExperiment.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(jMetalExperiment.class.getName()).log(Level.SEVERE, null, ex);
    } catch  (JMException ex) {
      Logger.getLogger(jMetalExperiment.class.getName()).log(Level.SEVERE, null, ex);
    }
  } // algorithmSettings

  /**
   * Main method
   * @param args
   * @throws JMException
   * @throws IOException
   */
  public static void main(String[] args) throws JMException, IOException {
    jMetalExperiment exp = new jMetalExperiment();

    exp.experimentName_ = "leaders_experiments";
    //exp.algorithmNameList_ = new String[]{"SMPSOhv3","SMPSOhvRandomArchive"};
    //exp.algorithmNameList_ = new String[]{"SMPSO","SMPSOArchiveRandom","SMPSOCell2","SMPSOhv3","SMPSOhvRandomArchive"}; //, "SMSEMOA","NSGA-II"};
    exp.algorithmNameList_ = new String[]{"SMPSOhv3","SMSEMOA","NSGA-II"};
    //exp.algorithmNameList_ = new String[]{"SMPSOhvRandomArchive"};
    //    "PAES", "PAEShv"};
        //"SMSEMOA", "SMPSO", "SMPSOhv2"};
    //"SMPSOCell2"};
    exp.problemList_ = new String[]{"ZDT1", "ZDT2","ZDT3", "ZDT4","ZDT6",                                    
                                    "DTLZ1","DTLZ2","DTLZ3","DTLZ4","DTLZ5",
                                    "DTLZ6","DTLZ7", "WFG1","WFG2","WFG3","WFG4","WFG5","WFG6","WFG7","WFG8","WFG9"};
    
    exp.paretoFrontFile_ = new String[]{"ZDT1.pf", "ZDT2.pf","ZDT3.pf",
                                    "ZDT4.pf","ZDT6.pf",
                                    "DTLZ1.3D.pf","DTLZ2.3D.pf","DTLZ3.3D.pf",
                                    "DTLZ4.3D.pf","DTLZ5.3D.pf","DTLZ6.3D.pf",
                                    "DTLZ7.3D.pf", "WFG1.pf","WFG2.pf","WFG3.pf",
            "WFG4.pf","WFG5.pf","WFG6.pf",
            "WFG7.pf","WFG8.pf","WFG9.pf"};

    exp.indicatorList_ = new String[]{"HV","EPSILON"};

    int numberOfAlgorithms = exp.algorithmNameList_.length;

    exp.experimentBaseDirectory_ = "/home/juan/Dropbox/Juanjo (1)/CEC2013/" +
                                   exp.experimentName_;
    exp.paretoFrontDirectory_ = "/home/juan/Dropbox/fronts";

    exp.algorithmSettings_ = new Settings[numberOfAlgorithms];

    exp.independentRuns_ = 30;

    exp.initExperiment();

    // Run the experiments
    int numberOfThreads ;
    //exp.runExperiment(numberOfThreads = 8) ;

    //exp.generateQualityIndicators() ;
    
    
    
    
    // Friedman Tests
    Friedman test = new Friedman(exp);
    test.executeTest("EPSILON");
    test.executeTest("HV");
    test.executeTest("SPREAD");
    test.executeTest("IGD");
    
    // Generate latex tables
    exp.generateLatexTables() ;

    // Configure the R scripts to be generated
    int rows  ;
    int columns  ;
    String prefix ;
    String [] problems ;
    boolean notch ;


    
    //rows = 3 ;
    //columns = 2 ;
    prefix = new String("PSOses");
    problems = new String[]{"ZDT1", "ZDT2","ZDT3", "ZDT4","ZDT6","DTLZ1","DTLZ2","DTLZ4","DTLZ5","DTLZ6","DTLZ7","WFG1","WFG2","WFG3","WFG4","WFG5","WFG6","WFG7","WFG8","WFG9"} ;
    //problems = new String[]{"ZDT1"} ;
    
    //exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch = false, exp) ;
    exp.generateRWilcoxonScripts(problems, prefix, exp) ;

    
    // Configuring scripts for ZDT
    /*
    rows = 3 ;
    columns = 2 ;
    prefix = new String("ZDT");
    problems = new String[]{"ZDT1", "ZDT2","ZDT3", "ZDT4","ZDT6"} ;
    
    exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch = false, exp) ;
    exp.generateRWilcoxonScripts(problems, prefix, exp) ;

    // Configure scripts for DTLZ
    rows = 3 ;
    columns = 3 ;
    prefix = new String("DTLZ");
    problems = new String[]{"DTLZ1","DTLZ2","DTLZ3","DTLZ4","DTLZ5",
                                    "DTLZ6","DTLZ7"} ;

    exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch=false, exp) ;
    exp.generateRWilcoxonScripts(problems, prefix, exp) ;

    // Configure scripts for WFG
    rows = 3 ;
    columns = 3 ;
    prefix = new String("WFG");
    problems = new String[]{"WFG1","WFG2","WFG3","WFG4","WFG5","WFG6",
                            "WFG7","WFG8","WFG9"} ;

    exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch=false, exp) ;
    exp.generateRWilcoxonScripts(problems, prefix, exp) ;
    */
  } // main
} // CEC2013PlanBStudy


