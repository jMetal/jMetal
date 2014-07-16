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
  protected String[] algorithmNameList;
  protected String[] problemList;
  protected String[] paretoFrontFileList;
  protected String[] indicatorList;
  protected String experimentBaseDirectory;

  protected String latexDirectory;
  protected String paretoFrontDirectory;
  protected String outputParetoFrontFileName;
  protected String outputParetoSetFileName;
  protected int independentRuns;

  protected Settings[] algorithmSettings;
  //To indicate whether an indicator is to be minimized. Hard-coded
  // in the constructor
  protected HashMap<String, Boolean> indicatorMinimize;

  protected boolean runTheAlgorithms;
  protected boolean generateReferenceParetoFronts;
  protected boolean generateQualityIndicators;
  protected boolean generateLatexTables;
  protected boolean generateBoxplots;
  protected boolean generateWilcoxonTables;
  protected boolean generateFriedmanTables;
  protected boolean generateSetCoverageTables;
  protected boolean useConfigurationFilesForAlgorithms;
  protected int boxplotRows;
  protected int boxplotColumns;
  protected boolean boxplotNotch;
  protected int numberOfExecutionThreads;

  protected Properties[] problemsSettings;

  Properties configuration = new Properties();
  InputStreamReader propertiesFile = null;

  ///////////////////////////////////
  private Experiment(Builder builder) {
    this.experimentName = builder.experimentName ;
    this.generateReferenceParetoFronts = builder.generateReferenceParetoFronts ;
    this.generateQualityIndicators = builder.generateQualityIndicators;
    this.generateLatexTables = builder.generateLatexTables;
    this.generateBoxplots = builder.generateBoxplots;

    this.boxplotRows = builder.boxplotRows ;
    this.boxplotColumns = builder.boxplotColumns ;
    this.boxplotNotch = builder.boxplotNotch ;

    this.generateWilcoxonTables = builder.generateWilcoxonTables;
    this.generateFriedmanTables = builder.generateFriedmanTables;
    this.generateSetCoverageTables = builder.generateSetCoverageTables;

    this.runTheAlgorithms = builder.runTheAlgorithms ;
    this.numberOfExecutionThreads = builder.numberOfExecutionThreads ;
  }

  public static class Builder {
    private final String experimentName ;
    private boolean generateReferenceParetoFronts ;
    private boolean generateQualityIndicators;
    private boolean generateLatexTables;
    private boolean generateBoxplots;
    private boolean generateWilcoxonTables;
    private boolean generateFriedmanTables;
    private boolean generateSetCoverageTables;
    private int boxplotRows;
    private int boxplotColumns;
    private boolean boxplotNotch;
    private boolean runTheAlgorithms;
    private int numberOfExecutionThreads ;

    public Builder(String experimentName) {
      this.experimentName = experimentName ;

      generateReferenceParetoFronts = false;
      generateQualityIndicators = false;
      generateLatexTables = false;
      generateBoxplots = false;
      generateWilcoxonTables = false;
      generateFriedmanTables = false;
      generateSetCoverageTables = false;
      boxplotRows = 0 ;
      boxplotColumns = 0 ;
      boxplotNotch= false ;
      runTheAlgorithms = false ;
      numberOfExecutionThreads = 1 ;
    }

    public Builder generateReferenceParetoFronts() {
      this.generateReferenceParetoFronts = true ;

      return this ;
    }

    public Builder generateQualityIndicators() {
      this.generateQualityIndicators = true ;

      return this ;
    }

    public Builder generateLatexTables() {
      this.generateLatexTables = true ;

      return this ;
    }

    public Builder generateBoxplots(int rows, int columns, boolean notch) {
      this.generateBoxplots = true ;
      this.boxplotRows = rows ;
      this.boxplotColumns = columns ;
      this.boxplotNotch = notch ;

      return this ;
    }

    public Builder generateWilcoxonTables(boolean generateWilcoxonTables) {
      this.generateWilcoxonTables = generateWilcoxonTables ;

      return this ;
    }

    public Builder generateFriedmanTables(boolean generateFriedmanTables) {
      this.generateFriedmanTables = generateFriedmanTables ;

      return this ;
    }

    public Builder generateSetCoverageTables(boolean generateSetCoverageTables) {
      this.generateSetCoverageTables = generateSetCoverageTables ;

      return this ;
    }

    public Builder runTheAlgorithms(int numberOfExecutionThreads) {
      this.runTheAlgorithms = true ;
      this.numberOfExecutionThreads = numberOfExecutionThreads ;

      return this ;
    }

    public Experiment build() {
      return new Experiment(this) ;
    }
  }


  public void execute() {

  }


  ///////////////////////////////////


  @Deprecated
  public Experiment() {
    problemsSettings = null;

    algorithmNameList = null;
    problemList = null;
    paretoFrontFileList = null;
    indicatorList = null;

    experimentBaseDirectory = "";
    paretoFrontDirectory = "";
    latexDirectory = "latex";

    outputParetoFrontFileName = "FUN";
    outputParetoSetFileName = "VAR";

    algorithmSettings = null;

    independentRuns = 0;
    numberOfExecutionThreads = 1;

    runTheAlgorithms = false;
    generateReferenceParetoFronts = false;
    generateQualityIndicators = false;
    generateLatexTables = false;
    generateBoxplots = false;
    generateWilcoxonTables = false;
    generateFriedmanTables = false;
    useConfigurationFilesForAlgorithms = false;

    indicatorMinimize = new HashMap<String, Boolean>();
    indicatorMinimize.put("HV", false);
    indicatorMinimize.put("EPSILON", true);
    indicatorMinimize.put("SPREAD", true);
    indicatorMinimize.put("GD", true);
    indicatorMinimize.put("IGD", true);
  }

  public String getExperimentName() {
    return experimentName;
  }

  public void setExperimentName(String value) {
    experimentName = configuration.getProperty("experimentName", value);
  }

  public String[] getAlgorithmNameList() {
    return algorithmNameList;
  }

  public void setAlgorithmNameList(String[] values) {
    if (configuration.getProperty("algorithmNameList") == null) {
      algorithmNameList = Arrays.copyOf(values, values.length);

    } else {
      algorithmNameList = configuration.getProperty("algorithmNameList").split(",");
    }
  }

  public String[] getProblemList() {
    return problemList;
  }

  public void setProblemList(String[] values) {
    if (configuration.getProperty("problemList") == null) {
      problemList = Arrays.copyOf(values, values.length);
    } else {
      problemList = configuration.getProperty("problemList").split(",");
    }
  }

  public String[] getParetoFrontFileList() {
    return paretoFrontFileList;
  }

  public void setParetoFrontFileList(String[] values) {
    if (configuration.getProperty("paretoFrontFileList") == null) {
      paretoFrontFileList = Arrays.copyOf(values, values.length);
    } else {
      paretoFrontFileList = configuration.getProperty("paretoFrontFileList").split(",");
    }
  }

  public String[] getIndicatorList() {
    return indicatorList;
  }

  public void setIndicatorList(String[] values) {
    if (configuration.getProperty("indicatorList") == null) {
      indicatorList = Arrays.copyOf(values, values.length);
    } else {
      indicatorList = configuration.getProperty("indicatorList").split(",");
    }
  }

  public String getExperimentBaseDirectory() {
    return experimentBaseDirectory;
  }

  public void setExperimentBaseDirectory(String directory) {
    experimentBaseDirectory = configuration.getProperty("experimentBaseDirectory", directory);
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
    paretoFrontDirectory = configuration.getProperty("paretoFrontDirectory", directory);
  }

  public String getOutputParetoFrontFileName() {
    return outputParetoFrontFileName;
  }

  public String getOutputParetoSetFileName() {
    return outputParetoSetFileName;
  }

  public int getIndependentRuns() {
    return independentRuns;
  }

  public void setIndependentRuns(int value) {
    independentRuns =
      Integer.parseInt(configuration.getProperty("independentRuns", Integer.toString(value)));
  }

  public Settings[] getAlgorithmSettings() {
    return algorithmSettings;
  }

  public HashMap<String, Boolean> indicatorMinimize() {
    return indicatorMinimize;
  }

  public boolean runTheAlgorithms() {
    return runTheAlgorithms;
  }

  public boolean generateReferenceParetoFronts() {
    return generateReferenceParetoFronts;
  }

  public boolean generateQualityIndicators() {
    return generateQualityIndicators;
  }

  public boolean generateLatexTables() {
    return generateLatexTables;
  }

  public boolean generateBoxplots() {
    return generateBoxplots;
  }

  public boolean generateWilcoxonTables() {
    return generateWilcoxonTables;
  }

  public boolean generateFriedmanTables() {
    return generateFriedmanTables;
  }

  public boolean generateSetCoverageTables() {
    return generateFriedmanTables;
  }


  public boolean useConfigurationFilesForAlgorithms() {
    return useConfigurationFilesForAlgorithms;
  }

  public int getBoxplotRows() {
    return boxplotRows;
  }

  public void setBoxplotRows(int value) {
    boxplotRows =
      Integer.parseInt(configuration.getProperty("boxplotsRows", Integer.toString(value)));
  }

  public int getBoxplotColumns() {
    return boxplotColumns;
  }

  public void setBoxplotColumns(int value) {
    boxplotColumns =
      Integer.parseInt(configuration.getProperty("boxplotsColumns", Integer.toString(value)));
  }

  public boolean getBoxplotNotch() {
    return boxplotNotch;
  }

  public void setBoxplotNotch(boolean value) {
    boxplotNotch =
      Boolean.parseBoolean(configuration.getProperty("boxplotsNotch", Boolean.toString(value)));
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
    setNumberOfExecutionThreads(numberOfExecutionThreads);
    setGenerateQualityIndicators(generateQualityIndicators);
    setGenerateReferenceParetoFronts(generateReferenceParetoFronts);
    setRunTheAlgorithms(runTheAlgorithms);
    setBoxplotRows(boxplotRows);
    setBoxplotColumns(boxplotColumns);
    setBoxplotNotch(boxplotNotch);
    setUseConfigurationFilesForAlgorithms(useConfigurationFilesForAlgorithms);

    int numberOfAlgorithms = algorithmNameList.length;
    algorithmSettings = new Settings[numberOfAlgorithms];

    checkIfExperimentDirectoryExists();
  }

  public void runExperiment() throws JMetalException, IOException {
    Configuration.logger.info("Experiment: Name: " + experimentName);
    Configuration.logger.info("Experiment: creating " + numberOfExecutionThreads + " threads");
    Configuration.logger.info("Experiment: Number of algorithms: " + algorithmNameList.length);
    for (String s : algorithmNameList) {
      Configuration.logger.info("  - " + s);
    }
    Configuration.logger.info("Experiment: Number of problem: " + problemList.length);
    Configuration.logger.info("Experiment: runs: " + independentRuns);
    Configuration.logger.info("Experiment: Experiment directory: " + experimentBaseDirectory);
    Configuration.logger.info(
      "Experiment: Use config files for algorithms: " + useConfigurationFilesForAlgorithms);
    Configuration.logger.info("Experiment: Generate reference Pareto fronts: " + generateReferenceParetoFronts);
    Configuration.logger.info("Experiment: Generate Latex tables: " + generateLatexTables);
    Configuration.logger.info("Experiment: Generate Friedman tables: " + generateFriedmanTables);
    Configuration.logger.info("Experiment: Generate boxplots: " + generateBoxplots);
    if (generateBoxplots) {
      Configuration.logger.info("Experiment: Boxplots Rows: " + boxplotRows);
      Configuration.logger.info("Experiment: Boxplots Columns: " + boxplotColumns);
      Configuration.logger.info("Experiment: Boxplots Notch: " + boxplotNotch);
    }

    if (runTheAlgorithms) {
      MultithreadedAlgorithmExecutor parallelRunner =
        new MultithreadedAlgorithmExecutor(numberOfExecutionThreads);
      parallelRunner.start(this);

      for (String algorithm : algorithmNameList) {
        for (String problem : problemList) {
          for (int i = 0; i < independentRuns; i++) {
            Configuration.logger.info(
              "Adding task. Algorithm:  " + algorithm + " Problem: " + problem + " Run: " + i);
            parallelRunner.addTask(new Object[] {algorithm, problem, i});
          }
        }
      }

      parallelRunner.parallelExecution();
      parallelRunner.stop();
    }

    if (generateReferenceParetoFronts) {
      new ReferenceParetoFronts(this).generate();
    }

    if (generateQualityIndicators) {
      new QualityIndicatorTables(this).generate();
    }

    if (generateLatexTables) {
      new LatexTables(this).generate();
    }

    if (generateWilcoxonTables) {
      new WilcoxonTestTables(this).generate();
    }

    if (generateBoxplots) {
      new BoxPlots(this).generate();
    }

    if (generateFriedmanTables) {
      new FriedmanTables(this).generate();
    }

    if (generateSetCoverageTables) {
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
      Configuration.logger.info("Experiment directory exists");
      if (experimentDirectory.isDirectory()) {
        Configuration.logger.info("Experiment directory is a directory");
      } else {
        Configuration.logger.info("Experiment directory is not a directory. Deleting file and creating directory");
      }
      experimentDirectory.delete();
      new File(experimentBaseDirectory).mkdirs();
    } else {
      Configuration.logger.info("Experiment directory does NOT exist. Creating");
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
      configuration = new Properties();

      Configuration.logger.info("ARGS[0]: " + args[0]);
      try {
        propertiesFile = new InputStreamReader(new FileInputStream(args[0]));
        configuration.load(propertiesFile);
      } catch (FileNotFoundException e) {
        Configuration.logger.log(Level.SEVERE, "File not found", e);
        propertiesFile = null;
      }  catch (IOException e) {
        Configuration.logger.log(Level.SEVERE, "Error reading properties file", e);
      }

      if (propertiesFile == null) {
        Configuration.logger.log(Level.INFO, "Properties file " + args[0] + " doesn't exist");
      } else {
        Configuration.logger.log(Level.INFO, "Properties file loaded");
      }
    }
  }

  public void setNumberOfExecutionThreads(int value) {
    numberOfExecutionThreads = Integer
      .parseInt(configuration.getProperty("numberOfExecutionThreads", Integer.toString(value)));
  }

  public void setGenerateReferenceParetoFronts(boolean value) {
    generateReferenceParetoFronts =
      Boolean.parseBoolean(
        configuration.getProperty("generateReferenceParetoFronts", Boolean.toString(value)));
  }

  public void setRunTheAlgorithms(boolean value) {
    runTheAlgorithms =
      Boolean.parseBoolean(
        configuration.getProperty("runTheAlgorithms", Boolean.toString(value)));
  }

  public void setGenerateQualityIndicators(boolean value) {
    generateQualityIndicators =
      Boolean.parseBoolean(
        configuration.getProperty("generateQualityIndicators", Boolean.toString(value)));
  }

  public void setUseConfigurationFilesForAlgorithms(boolean value) {
    useConfigurationFilesForAlgorithms =
      Boolean.parseBoolean(
        configuration.getProperty("useConfigurationFilesForAlgorithms", Boolean.toString(value)));
  }

  public String toString() {
    String result = null;
    result = "ExperimentName   : " + experimentName + "\n";
    result += "Independent runs: " + independentRuns + "\n";
    result += "Number of threads: " + numberOfExecutionThreads + "\n";
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
    result += "Generate reference Pareto fronts: " + generateReferenceParetoFronts + "\n";
    result += "Generate quality Indicators: " + generateQualityIndicators + "\n";
    result += "Generate Latex tables: " + generateLatexTables + "\n";
    result += "Generate Friedman tables: " + generateFriedmanTables + "\n";
    result += "Generate boxplots: " + generateBoxplots + "\n";
    result += "Generate Wilcoxon tables: " + generateWilcoxonTables + "\n";
    result += "Run the algorithms: " + runTheAlgorithms + "\n";
    result += "Use config files for algorithms: " + useConfigurationFilesForAlgorithms + "\n";

    return result;
  }
}
