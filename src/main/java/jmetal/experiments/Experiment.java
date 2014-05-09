//  Experiment.java
//
//  Authors:
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
//
package jmetal.experiments;

import jmetal.experiments.util.*;
import jmetal.util.parallel.MultithreadedAlgorithmRunner;
import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.NonDominatedSolutionList;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by Antonio J. Nebro on 08/02/14.
 * Class for configuring and running experiments
 */
public class Experiment {
  public String experimentName_;
  public String[] algorithmNameList_; // List of the names of the algorithms to be executed
  public String[] problemList_; // List of problems to be solved
  public String[] paretoFrontFileList_; // List of the files containing the pareto fronts
                                        // corresponding to the problems in problemList_
  public String[] indicatorList_; // List of the quality indicators to be applied
  public String experimentBaseDirectory_; // Directory to store the results
  public String latexDirectory_; // Directory to store the latex files
  public String paretoFrontDirectory_; // Directory containing the Pareto front files
  public String outputParetoFrontFile_; // Name of the file containing the output
  // Pareto front
  public String outputParetoSetFile_; // Name of the file containing the output
  // Pareto set
  public int independentRuns_; // Number of independent runs per algorithm
  public Settings[] algorithmSettings_; // Parameter experiments.settings of each algorithm
  HashMap<String, Object> map_; // Map used to send experiment parameters to threads
  public HashMap<String, Boolean> indicatorMinimize_; // To indicate whether an indicator is to be minimized. Hard-coded
                                                      // in the constructor

  public boolean  runTheAlgorithms_ ;
  public boolean  generateReferenceParetoFronts_ ;
  public boolean  generateQualityIndicators_ ;
  public boolean  generateLatexTables_ ;
  public boolean  generateBoxplots_ ;
  public boolean  generateWilcoxonTables_ ;
  public boolean  generateFriedmanTables_ ;
  public boolean  useConfigurationFilesForAlgorithms_ ;

  public int boxplotRows_    ;
  public int boxplotColumns_ ;
  public boolean boxplotNotch_ ;

  public int numberOfExecutionThreads_ ;

  public Properties[] problemsSettings_;

  Properties configuration_ = new Properties() ;
  InputStreamReader propertiesFile_ = null;

  public Experiment() {
    problemsSettings_ = null;

    algorithmNameList_ = null;
    problemList_ = null;
    paretoFrontFileList_ = null;
    indicatorList_ = null;

    experimentBaseDirectory_ = "";
    paretoFrontDirectory_ = "";
    latexDirectory_ = "latex";

    outputParetoFrontFile_ = "FUN";
    outputParetoSetFile_ = "VAR";

    algorithmSettings_ = null;

    independentRuns_ = 0;
    numberOfExecutionThreads_ = 1 ;

    runTheAlgorithms_  = false ;
    generateReferenceParetoFronts_ = false ;
    generateQualityIndicators_ = false ;
    generateLatexTables_ = false ;
    generateBoxplots_ = false ;
    generateWilcoxonTables_ = false ;
    generateFriedmanTables_ = false ;
    useConfigurationFilesForAlgorithms_ = false ;

    indicatorMinimize_ = new HashMap<String, Boolean>();
    indicatorMinimize_.put("HV", false);
    indicatorMinimize_.put("EPSILON", true);
    indicatorMinimize_.put("SPREAD", true);
    indicatorMinimize_.put("GD", true);
    indicatorMinimize_.put("IGD", true);
  } // Constructor

  public void initExperiment(String[] args) {
    testForConfigurationFile(args);

    setExperimentName(experimentName_) ;
    setIndependentRuns(independentRuns_);
    setAlgorithmNameList(algorithmNameList_);
    setProblemList(problemList_);
    setParetoFrontFileList(paretoFrontFileList_);
    setIndicatorList(indicatorList_);
    setExperimentBaseDirectory(experimentBaseDirectory_) ;
    setParetoFrontDirectory(paretoFrontDirectory_);
    setNumberOfExecutionThreads(numberOfExecutionThreads_);
    setGenerateQualityIndicators(generateQualityIndicators_);
    setGenerateReferenceParetoFronts(generateReferenceParetoFronts_);
    setRunTheAlgorithms(runTheAlgorithms_);
    setBoxplotRows(boxplotRows_);
    setBoxplotColumns(boxplotColumns_);
    setBoxplotNotch(boxplotNotch_);
    setUseConfigurationFilesForAlgorithms(useConfigurationFilesForAlgorithms_);

    int numberOfAlgorithms = algorithmNameList_.length;
    algorithmSettings_ = new Settings[numberOfAlgorithms];

    checkIfExperimentDirectoryExists();
  }

  public void runExperiment()  {
    System.out.println("Experiment: Name: " + experimentName_);
    System.out.println("Experiment: creating " + numberOfExecutionThreads_ + " threads");
    System.out.println("Experiment: Number of algorithms: " + algorithmNameList_.length);
    for (String s : algorithmNameList_)
      System.out.println("  - " + s);
    System.out.println("Experiment: Number of problems: " + problemList_.length);
    System.out.println("Experiment: runs: " + independentRuns_);
    System.out.println("Experiment: Experiment directory: " + experimentBaseDirectory_);
    System.out.println("Experiment: Use config files for algorithms: " + useConfigurationFilesForAlgorithms_);
    System.out.println("Experiment: Generate reference Pareto fronts: " + generateReferenceParetoFronts_);
    System.out.println("Experiment: Generate Latex tables: " + generateLatexTables_);
    System.out.println("Experiment: Generate Friedman tables: " + generateFriedmanTables_);
    System.out.println("Experiment: Generate boxplots: " + generateBoxplots_);
    if (generateBoxplots_) {
      System.out.println("Experiment: Boxplots Rows: " + boxplotRows_);
      System.out.println("Experiment: Boxplots Columns: " + boxplotColumns_);
      System.out.println("Experiment: Boxplots Notch: " + boxplotNotch_);
    }

    if (runTheAlgorithms_) {
      MultithreadedAlgorithmRunner parallelRunner = new MultithreadedAlgorithmRunner(numberOfExecutionThreads_) ;
      parallelRunner.startParallelRunner(this);

      for (String algorithm : algorithmNameList_) {
        for (String problem : problemList_) {
          for (int i = 0 ; i < independentRuns_; i++) {
            System.out.println("Adding task. Algorithm:  "+ algorithm + " Problem: " + problem+ " Run: "+ i) ;
            parallelRunner.addTaskForExecution(new Object[]{algorithm, problem, i});
          }
        }
      }

      parallelRunner.parallelExecution();
      parallelRunner.stopParallelRunner();
    }

    if (generateReferenceParetoFronts_){
      new ReferenceParetoFronts(this).generate() ;
    }

    if (generateQualityIndicators_) {
      new QualityIndicatorTables(this).generate();
    }

    if (generateLatexTables_) {
      new LatexTables(this).generate();
    }

    if (generateWilcoxonTables_) {
      new WilcoxonTextTables(this).generate();
    }

    if (generateBoxplots_) {
      new BoxPlots(this).generate();
    }

    if (generateFriedmanTables_) {
      new FriedmanTables(this).generate();
    }
  }

  /**
   * Creates the experiment directory if it does not exist
   */
  private void checkIfExperimentDirectoryExists() {
    File experimentDirectory;

    experimentDirectory = new File(experimentBaseDirectory_);
    if (experimentDirectory.exists()) {
      System.out.println("Experiment directory exists");
      if (experimentDirectory.isDirectory()) {
        System.out.println("Experiment directory is a directory");
      } else {
        System.out.println("Experiment directory is not a directory. Deleting file and creating directory");
      }
      experimentDirectory.delete();
      new File(experimentBaseDirectory_).mkdirs();
    } // if
    else {
      System.out.println("Experiment directory does NOT exist. Creating");
      new File(experimentBaseDirectory_).mkdirs();
    } // else
  } // checkIfExperimentDirectoryExists

  /**
   * Test if a property file with the experiment settings is provided
   * @param args Argument of the main() method
   */
  public void testForConfigurationFile(String[] args) {
    if (args.length == 1) {
      configuration_ = new Properties() ;

      System.out.println("ARGS[0]: " + args[0]);
      try {
        propertiesFile_ = new InputStreamReader(new FileInputStream(args[0]));
        try {
          configuration_.load(propertiesFile_);
        } catch (IOException e) {
          e.printStackTrace();
        }

      } catch (FileNotFoundException e) {
        propertiesFile_ = null ;
      }

      if (propertiesFile_ == null) {
        System.out.println("Properties file " + args[0] + " doesn't exist");
      } else {
        System.out.println("Properties file loaded");
      }
    }

  }

  public void setExperimentName(String value) {
    experimentName_ = configuration_.getProperty("experimentName", value) ;
  }

  public void setIndependentRuns(int value) {
    independentRuns_ = Integer.parseInt(configuration_.getProperty("independentRuns", Integer.toString(value)));
  }

  public void setAlgorithmNameList(String[] values) {
    if (configuration_.getProperty("algorithmNameList") == null) {
      algorithmNameList_ = values ;
    }
    else {
      algorithmNameList_ = configuration_.getProperty("algorithmNameList").split(",") ;
    }
  }

  public void setProblemList(String[] values) {
    if (configuration_.getProperty("problemList") == null) {
      problemList_ = values ;
    }
    else {
      problemList_ = configuration_.getProperty("problemList").split(",") ;
    }
  }

  public void setParetoFrontFileList(String[] values) {
    if (configuration_.getProperty("paretoFrontFileList") == null) {
      paretoFrontFileList_ = values ;
    }
    else {
      paretoFrontFileList_ = configuration_.getProperty("paretoFrontFileList").split(",") ;
    }
  }

  public void setIndicatorList(String[] values) {
    if (configuration_.getProperty("indicatorList") == null) {
      indicatorList_ = values ;
    }
    else {
      indicatorList_ = configuration_.getProperty("indicatorList").split(",") ;
    }
  }

  public void setExperimentBaseDirectory(String directory) {
    experimentBaseDirectory_ = configuration_.getProperty("experimentBaseDirectory", directory) ;
  }

  public void setParetoFrontDirectory(String directory) {
    paretoFrontDirectory_ = configuration_.getProperty("paretoFrontDirectory", directory) ;
  }

  public void setNumberOfExecutionThreads(int value) {
    numberOfExecutionThreads_ = Integer.parseInt(configuration_.getProperty("numberOfExecutionThreads", Integer.toString(value)));
  }

  public void setBoxplotRows(int value) {
    boxplotRows_ = Integer.parseInt(configuration_.getProperty("boxplotsRows", Integer.toString(value)));
  }

  public void setBoxplotColumns(int value) {
    boxplotColumns_ = Integer.parseInt(configuration_.getProperty("boxplotsColumns", Integer.toString(value)));
  }

  public void setBoxplotNotch(boolean value) {
    boxplotNotch_ =
            Boolean.parseBoolean(configuration_.getProperty("boxplotsNotch", Boolean.toString(value)));
  }

  public void setGenerateReferenceParetoFronts(boolean value) {
    generateReferenceParetoFronts_ =
            Boolean.parseBoolean(configuration_.getProperty("generateReferenceParetoFronts", Boolean.toString(value)));
  }

  public void setRunTheAlgorithms(boolean value) {
    runTheAlgorithms_ =
            Boolean.parseBoolean(configuration_.getProperty("runTheAlgorithms", Boolean.toString(value)));
  }

  public void setGenerateQualityIndicators(boolean value) {
    generateQualityIndicators_ =
            Boolean.parseBoolean(configuration_.getProperty("generateQualityIndicators", Boolean.toString(value)));
  }

  public void setUseConfigurationFilesForAlgorithms(boolean value) {
    useConfigurationFilesForAlgorithms_ =
            Boolean.parseBoolean(configuration_.getProperty("useConfigurationFilesForAlgorithms", Boolean.toString(value)));
  }

  public String toString() {
    String result = null ;
    result = "ExperimentName   : " + experimentName_ + "\n";
    result += "Independent runs: " + independentRuns_ + "\n";
    result += "Number of threads: " + numberOfExecutionThreads_ + "\n";
    result += "Algorithm list  : " ;
    for (String s : algorithmNameList_) {
      result += s + "," ;
    }
    result += "\n" ;
    result += "Problem list  : " ;
    for (String s : problemList_) {
      result += s + "," ;
    }
    result += "\n" ;

    if (paretoFrontFileList_ != null) {
      result += "Pareto front file list  : ";
      for (String s : paretoFrontFileList_) {
        result += s + ",";
      }
      result += "\n";
    }

    result += "Experiment base directory: " + experimentBaseDirectory_ + "\n" ;
    result += "Pareto front directory: " + paretoFrontDirectory_ + "\n" ;
    result += "Generate reference Pareto fronts: " + generateReferenceParetoFronts_ + "\n" ;
    result += "Generate quality Indicators: " + generateQualityIndicators_ + "\n" ;
    result += "Generate Latex tables: " + generateLatexTables_ + "\n" ;
    result += "Generate Friedman tables: " + generateFriedmanTables_ + "\n" ;
    result += "Generate boxplots: " + generateBoxplots_ + "\n" ;
    result += "Generate Wilcoxon tables: " + generateWilcoxonTables_ + "\n" ;
    result += "Run the algorithms: " + runTheAlgorithms_ + "\n" ;
    result += "Use config files for algorithms: " + useConfigurationFilesForAlgorithms_ + "\n" ;

    return result ;
  }

  /**
   * @param problemIndex
   */
  public void generateReferenceFronts(int problemIndex) {

    File rfDirectory;
    String referenceFrontDirectory = experimentBaseDirectory_ + "/referenceFronts";

    rfDirectory = new File(referenceFrontDirectory);

    if (!rfDirectory.exists()) {                          // Si no existe el directorio
      boolean result = new File(referenceFrontDirectory).mkdirs();        // Lo creamos
      System.out.println("Creating " + referenceFrontDirectory);
    }

    //frontPath_[problemIndex] = referenceFrontDirectory + "/" + problemList_[problemIndex] + ".rf";
    String referenceParetoFront = referenceFrontDirectory + "/" + problemList_[problemIndex] + ".pf";
    //String referenceParetoSet = referenceFrontDirectory + "/" + problemList_[problemIndex] + ".ps";

    MetricsUtil metricsUtils = new MetricsUtil();
    NonDominatedSolutionList solutionSet = new NonDominatedSolutionList();
    for (String anAlgorithmNameList_ : algorithmNameList_) {

      String problemDirectory = experimentBaseDirectory_ + "/data/" + anAlgorithmNameList_ +
              "/" + problemList_[problemIndex];

      for (int numRun = 0; numRun < independentRuns_; numRun++) {

        String outputParetoFrontFilePath;
        outputParetoFrontFilePath = problemDirectory + "/FUN." + numRun;
        String solutionFrontFile = outputParetoFrontFilePath;

        metricsUtils.readNonDominatedSolutionSet(solutionFrontFile, solutionSet);
      } // for
    } // for
    //solutionSet.printObjectivesToFile(frontPath_[problemIndex]);
    try {
      solutionSet.printObjectivesToFile(referenceParetoFront);
    } catch (IOException e) {
      e.printStackTrace();
    }
    //solutionSet.printVariablesToFile(referenceParetoSet);
  } // generateReferenceFronts
}
