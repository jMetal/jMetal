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
package org.uma.jmetal.experiment;

import org.uma.jmetal.experiment.util.*;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.parallel.MultithreadedAlgorithmExecutor;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Created by Antonio J. Nebro on 08/02/14.
 * Class for configuring and running experiment
 */
public class Experiment {
  protected String experimentName;
  //List of the names of the algorithms to be executed
  protected String[] algorithmNameList;
  //List of problem to be solved
  protected String[] problemList;
  protected String[] paretoFrontFileList;
  // List of the quality indicators to be applied
  protected String[] indicatorList;
  //Directory to store the results
  protected String experimentBaseDirectory;

  //List of the files containing the pareto fronts corresponding 
  //to the problem in problemList
  //Directory to store the latex files
  protected String latexDirectory;
  //Directory containing the Pareto front files
  protected String paretoFrontDirectory;
  //Name of the file containing the output Pareto front
  protected String outputParetoFrontFile;
  //Name of the file containing the output Pareto set
  protected String outputParetoSetFile;
  //Number of independent runs per algorithm
  protected int independentRuns;
  //Parameter experiment.settings of each algorithm
  protected Settings[] algorithmSettings;
  //To indicate whether an indicator is to be minimized. Hard-coded
  // in the constructor
  protected HashMap<String, Boolean> indicatorMinimize_;
  protected boolean runTheAlgorithms_;
  protected boolean generateReferenceParetoFronts_;
  protected boolean generateQualityIndicators_;
  protected boolean generateLatexTables_;
  protected boolean generateBoxplots_;
  protected boolean generateWilcoxonTables_;
  protected boolean generateFriedmanTables_;
  protected boolean generateSetCoverageTables_;
  protected boolean useConfigurationFilesForAlgorithms_;
  protected int boxplotRows_;
  protected int boxplotColumns_;
  protected boolean boxplotNotch_;
  protected int numberOfExecutionThreads_;
  protected Properties[] problemsSettings_;
  //Map used to send experiment parameters to threads
  HashMap<String, Object> map_;
  Properties configuration_ = new Properties();
  InputStreamReader propertiesFile_ = null;

  public Experiment() {
    problemsSettings_ = null;

    algorithmNameList = null;
    problemList = null;
    paretoFrontFileList = null;
    indicatorList = null;

    experimentBaseDirectory = "";
    paretoFrontDirectory = "";
    latexDirectory = "latex";

    outputParetoFrontFile = "FUN";
    outputParetoSetFile = "VAR";

    algorithmSettings = null;

    independentRuns = 0;
    numberOfExecutionThreads_ = 1;

    runTheAlgorithms_ = false;
    generateReferenceParetoFronts_ = false;
    generateQualityIndicators_ = false;
    generateLatexTables_ = false;
    generateBoxplots_ = false;
    generateWilcoxonTables_ = false;
    generateFriedmanTables_ = false;
    useConfigurationFilesForAlgorithms_ = false;

    indicatorMinimize_ = new HashMap<String, Boolean>();
    indicatorMinimize_.put("HV", false);
    indicatorMinimize_.put("EPSILON", true);
    indicatorMinimize_.put("SPREAD", true);
    indicatorMinimize_.put("GD", true);
    indicatorMinimize_.put("IGD", true);
  }

  public String getExperimentName() {
    return experimentName;
  }

  public void setExperimentName(String value) {
    experimentName = configuration_.getProperty("experimentName", value);
  }

  public String[] getAlgorithmNameList() {
    return algorithmNameList;
  }

  public void setAlgorithmNameList(String[] values) {
    if (configuration_.getProperty("algorithmNameList") == null) {
      algorithmNameList = Arrays.copyOf(values, values.length);

    } else {
      algorithmNameList = configuration_.getProperty("algorithmNameList").split(",");
    }
  }

  public String[] getProblemList() {
    return problemList;
  }

  public void setProblemList(String[] values) {
    if (configuration_.getProperty("problemList") == null) {
      problemList = Arrays.copyOf(values, values.length);
    } else {
      problemList = configuration_.getProperty("problemList").split(",");
    }
  }

  public String[] getParetoFrontFileList() {
    return paretoFrontFileList;
  }

  public void setParetoFrontFileList(String[] values) {
    if (configuration_.getProperty("paretoFrontFileList") == null) {
      paretoFrontFileList = Arrays.copyOf(values, values.length);
    } else {
      paretoFrontFileList = configuration_.getProperty("paretoFrontFileList").split(",");
    }
  }

  public String[] getIndicatorList() {
    return indicatorList;
  }

  public void setIndicatorList(String[] values) {
    if (configuration_.getProperty("indicatorList") == null) {
      indicatorList = Arrays.copyOf(values, values.length);
    } else {
      indicatorList = configuration_.getProperty("indicatorList").split(",");
    }
  }

  public String getExperimentBaseDirectory() {
    return experimentBaseDirectory;
  }

  public void setExperimentBaseDirectory(String directory) {
    experimentBaseDirectory = configuration_.getProperty("experimentBaseDirectory", directory);
  }

  public void setExperimentBaseDirector(String directory) {
    experimentBaseDirectory = directory;
  }

  public String getLatexDirectory() {
    return latexDirectory;
  }

  public void setLatexDirectory(String directory) {
    latexDirectory = directory;
  }

  public String getParetoFrontDirectory() {
    return paretoFrontDirectory;
  }

  public void setParetoFrontDirectory(String directory) {
    paretoFrontDirectory = configuration_.getProperty("paretoFrontDirectory", directory);
  }

  public String getOutputParetoFrontFile() {
    return outputParetoFrontFile;
  }

  public String getOutputParetoSetFile() {
    return outputParetoSetFile;
  }

  public int getIndependentRuns() {
    return independentRuns;
  }

  public void setIndependentRuns(int value) {
    independentRuns =
        Integer.parseInt(configuration_.getProperty("independentRuns", Integer.toString(value)));
  }

  public Settings[] getAlgorithmSettings() {
    return algorithmSettings;
  }

  public HashMap<String, Boolean> indicatorMinimize() {
    return indicatorMinimize_;
  }

  public boolean runTheAlgorithms() {
    return runTheAlgorithms_;
  }

  public boolean generateReferenceParetoFronts() {
    return generateReferenceParetoFronts_;
  }

  public boolean generateQualityIndicators() {
    return generateQualityIndicators_;
  }

  public boolean generateLatexTables() {
    return generateLatexTables_;
  }

  public boolean generateBoxplots() {
    return generateBoxplots_;
  }

  public boolean generateWilcoxonTables() {
    return generateWilcoxonTables_;
  }

  public boolean generateFriedmanTables() {
    return generateFriedmanTables_;
  }

  public boolean generateSetCoverageTables() {
    return generateFriedmanTables_;
  }


  public boolean useConfigurationFilesForAlgorithms() {
    return useConfigurationFilesForAlgorithms_;
  }

  public int getBoxplotRows() {
    return boxplotRows_;
  }

  public void setBoxplotRows(int value) {
    boxplotRows_ =
        Integer.parseInt(configuration_.getProperty("boxplotsRows", Integer.toString(value)));
  }

  public int getBoxplotColumns() {
    return boxplotColumns_;
  }

  public void setBoxplotColumns(int value) {
    boxplotColumns_ =
        Integer.parseInt(configuration_.getProperty("boxplotsColumns", Integer.toString(value)));
  }

  public boolean getBoxplotNotch() {
    return boxplotNotch_;
  }

  public void setBoxplotNotch(boolean value) {
    boxplotNotch_ =
        Boolean.parseBoolean(configuration_.getProperty("boxplotsNotch", Boolean.toString(value)));
  }

  public void initExperiment(String[] args) {
    testForConfigurationFile(args);

    setExperimentName(experimentName);
    setIndependentRuns(independentRuns);
    setAlgorithmNameList(algorithmNameList);
    setProblemList(problemList);
    setParetoFrontFileList(paretoFrontFileList);
    setIndicatorList(indicatorList);
    setExperimentBaseDirectory(experimentBaseDirectory);
    setParetoFrontDirectory(paretoFrontDirectory);
    setNumberOfExecutionThreads(numberOfExecutionThreads_);
    setGenerateQualityIndicators(generateQualityIndicators_);
    setGenerateReferenceParetoFronts(generateReferenceParetoFronts_);
    setRunTheAlgorithms(runTheAlgorithms_);
    setBoxplotRows(boxplotRows_);
    setBoxplotColumns(boxplotColumns_);
    setBoxplotNotch(boxplotNotch_);
    setUseConfigurationFilesForAlgorithms(useConfigurationFilesForAlgorithms_);

    int numberOfAlgorithms = algorithmNameList.length;
    algorithmSettings = new Settings[numberOfAlgorithms];

    checkIfExperimentDirectoryExists();
  }

  public void runExperiment() throws JMetalException, IOException {
    Configuration.logger_.info("Experiment: Name: " + experimentName);
    Configuration.logger_.info("Experiment: creating " + numberOfExecutionThreads_ + " threads");
    Configuration.logger_.info("Experiment: Number of algorithms: " + algorithmNameList.length);
    for (String s : algorithmNameList) {
      Configuration.logger_.info("  - " + s);
    }
    Configuration.logger_.info("Experiment: Number of problem: " + problemList.length);
    Configuration.logger_.info("Experiment: runs: " + independentRuns);
    Configuration.logger_.info("Experiment: Experiment directory: " + experimentBaseDirectory);
    Configuration.logger_.info(
        "Experiment: Use config files for algorithms: " + useConfigurationFilesForAlgorithms_);
    Configuration.logger_.info("Experiment: Generate reference Pareto fronts: " + generateReferenceParetoFronts_);
    Configuration.logger_.info("Experiment: Generate Latex tables: " + generateLatexTables_);
    Configuration.logger_.info("Experiment: Generate Friedman tables: " + generateFriedmanTables_);
    Configuration.logger_.info("Experiment: Generate boxplots: " + generateBoxplots_);
    if (generateBoxplots_) {
      Configuration.logger_.info("Experiment: Boxplots Rows: " + boxplotRows_);
      Configuration.logger_.info("Experiment: Boxplots Columns: " + boxplotColumns_);
      Configuration.logger_.info("Experiment: Boxplots Notch: " + boxplotNotch_);
    }

    if (runTheAlgorithms_) {
      MultithreadedAlgorithmExecutor parallelRunner =
          new MultithreadedAlgorithmExecutor(numberOfExecutionThreads_);
      parallelRunner.start(this);

      for (String algorithm : algorithmNameList) {
        for (String problem : problemList) {
          for (int i = 0; i < independentRuns; i++) {
            Configuration.logger_.info(
                "Adding task. Algorithm:  " + algorithm + " Problem: " + problem + " Run: " + i);
            parallelRunner.addTask(new Object[] {algorithm, problem, i});
          }
        }
      }

      parallelRunner.parallelExecution();
      parallelRunner.stop();
    }

    if (generateReferenceParetoFronts_) {
      new ReferenceParetoFronts(this).generate();
    }

    if (generateQualityIndicators_) {
      new QualityIndicatorTables(this).generate();
    }

    if (generateLatexTables_) {
      new LatexTables(this).generate();
    }

    if (generateWilcoxonTables_) {
      new WilcoxonTestTables(this).generate();
    }

    if (generateBoxplots_) {
      new BoxPlots(this).generate();
    }

    if (generateFriedmanTables_) {
      new FriedmanTables(this).generate();
    }

    if (generateSetCoverageTables_) {
      new SetCoverageTables(this).generate();
    }
  }

  /**
   * Creates the experiment directory if it does not exist
   */
  private void checkIfExperimentDirectoryExists() {
    File experimentDirectory;

    experimentDirectory = new File(experimentBaseDirectory);
    if (experimentDirectory.exists()) {
      Configuration.logger_.info("Experiment directory exists");
      if (experimentDirectory.isDirectory()) {
        Configuration.logger_.info("Experiment directory is a directory");
      } else {
        Configuration.logger_.info("Experiment directory is not a directory. Deleting file and creating directory");
      }
      experimentDirectory.delete();
      new File(experimentBaseDirectory).mkdirs();
    } else {
      Configuration.logger_.info("Experiment directory does NOT exist. Creating");
      new File(experimentBaseDirectory).mkdirs();
    }
  }

  /**
   * Test if a property file with the experiment settings is provided
   *
   * @param args Argument of the main() method
   */
  public void testForConfigurationFile(String[] args) {
    if (args.length == 1) {
      configuration_ = new Properties();

      Configuration.logger_.info("ARGS[0]: " + args[0]);
      try {
        propertiesFile_ = new InputStreamReader(new FileInputStream(args[0]));
        configuration_.load(propertiesFile_);
      } catch (FileNotFoundException e) {
        Configuration.logger_.log(Level.SEVERE, "File not found", e);
        propertiesFile_ = null;
      }  catch (IOException e) {
        Configuration.logger_.log(Level.SEVERE, "Error reading properties file", e);
      }

      if (propertiesFile_ == null) {
        Configuration.logger_.log(Level.INFO, "Properties file " + args[0] + " doesn't exist");
      } else {
        Configuration.logger_.log(Level.INFO, "Properties file loaded");
      }
    }
  }

  public void setNumberOfExecutionThreads(int value) {
    numberOfExecutionThreads_ = Integer
        .parseInt(configuration_.getProperty("numberOfExecutionThreads", Integer.toString(value)));
  }

  public void setGenerateReferenceParetoFronts(boolean value) {
    generateReferenceParetoFronts_ =
        Boolean.parseBoolean(
            configuration_.getProperty("generateReferenceParetoFronts", Boolean.toString(value)));
  }

  public void setRunTheAlgorithms(boolean value) {
    runTheAlgorithms_ =
        Boolean.parseBoolean(configuration_.getProperty("runTheAlgorithms", Boolean.toString(value)));
  }

  public void setGenerateQualityIndicators(boolean value) {
    generateQualityIndicators_ =
        Boolean.parseBoolean(
            configuration_.getProperty("generateQualityIndicators", Boolean.toString(value)));
  }

  public void setUseConfigurationFilesForAlgorithms(boolean value) {
    useConfigurationFilesForAlgorithms_ =
        Boolean.parseBoolean(
            configuration_.getProperty("useConfigurationFilesForAlgorithms", Boolean.toString(value)));
  }

  public String toString() {
    String result = null;
    result = "ExperimentName   : " + experimentName + "\n";
    result += "Independent runs: " + independentRuns + "\n";
    result += "Number of threads: " + numberOfExecutionThreads_ + "\n";
    result += "Algorithm list  : ";
    for (String s : algorithmNameList) {
      result += s + ",";
    }
    result += "\n";
    result += "Problem list  : ";
    for (String s : problemList) {
      result += s + ",";
    }
    result += "\n";

    if (paretoFrontFileList != null) {
      result += "Pareto front file list  : ";
      for (String s : paretoFrontFileList) {
        result += s + ",";
      }
      result += "\n";
    }

    result += "Experiment base directory: " + experimentBaseDirectory + "\n";
    result += "Pareto front directory: " + paretoFrontDirectory + "\n";
    result += "Generate reference Pareto fronts: " + generateReferenceParetoFronts_ + "\n";
    result += "Generate quality Indicators: " + generateQualityIndicators_ + "\n";
    result += "Generate Latex tables: " + generateLatexTables_ + "\n";
    result += "Generate Friedman tables: " + generateFriedmanTables_ + "\n";
    result += "Generate boxplots: " + generateBoxplots_ + "\n";
    result += "Generate Wilcoxon tables: " + generateWilcoxonTables_ + "\n";
    result += "Run the algorithms: " + runTheAlgorithms_ + "\n";
    result += "Use config files for algorithms: " + useConfigurationFilesForAlgorithms_ + "\n";

    return result;
  }
}
