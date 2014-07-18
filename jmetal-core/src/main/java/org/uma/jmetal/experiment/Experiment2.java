package org.uma.jmetal.experiment;

import java.util.Arrays;

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
  
  private ExperimentData experimentData ;
  
  /** Constructor */
  private Experiment2(Builder builder) {
  	experimentData = builder.experimentData ;
  	getExperimentData() ;
  }
  
  /** Builder */
  public static class Builder {
  	private final ExperimentData experimentData ;
  	
  	public Builder(ExperimentData experimentData) {
  		this.experimentData = experimentData ;
  	}
  	
  	public Experiment2 build() {
  		return new Experiment2(this) ;
  	}
  }
  
  private void getExperimentData() {
  	 experimentName = experimentData.getExperimentName() ;
  	 algorithmNameList = Arrays.copyOf(experimentData.getAlgorithmNameList(), (experimentData.getAlgorithmNameList()).length) ;
  	 problemList = Arrays.copyOf(experimentData.getProblemList(), (experimentData.getProblemList()).length) ;
  	 experimentBaseDirectory = experimentData.getExperimentBaseDirectory() ;
  	 independentRuns = experimentData.getIndependentRuns() ;
  }
}
  
