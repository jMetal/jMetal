package org.uma.jmetal.experiment;

import org.uma.jmetal.util.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by Antonio J. Nebro on 18/02/14.
 * Class for configuring and running an experiment
 */
public class Experiment2 {
  private String experimentName;
  private String[] algorithmNameList;
  private String[] problemList;
  private String experimentBaseDirectory;
  private int independentRuns;
  private LinkedList<ExperimentResult> resultObjectList ;
  
  private ExperimentData experimentData ;
  
  /** Constructor */
  private Experiment2(Builder builder) {
  	experimentData = builder.experimentData ;
  	getExperimentData() ;
    resultObjectList = builder.resultObjectList ;

  }
  
  /** Builder */
  public static class Builder {
  	private final ExperimentData experimentData ;
    private LinkedList<ExperimentResult> resultObjectList ;

    public Builder(ExperimentData experimentData) {
  		this.experimentData = experimentData ;
      this.resultObjectList = new LinkedList<>() ;
  	}

    public Builder addResultObject(ExperimentResult experimentResult) {
      resultObjectList.add(experimentResult) ;

      return this ;
    }

  	public Experiment2 build() {
  		return new Experiment2(this) ;
  	}
  }

  /** Gets the data from the experiment data object */
  private void getExperimentData() {
  	 experimentName = experimentData.getExperimentName() ;
  	 algorithmNameList = Arrays.copyOf(experimentData.getAlgorithmNameList(), (experimentData.getAlgorithmNameList()).length) ;
  	 problemList = Arrays.copyOf(experimentData.getProblemList(), (experimentData.getProblemList()).length) ;
  	 experimentBaseDirectory = experimentData.getExperimentBaseDirectory() ;
  	 independentRuns = experimentData.getIndependentRuns() ;
  }

  /** Creates the experiment directory if it does not exist */
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
}
  
