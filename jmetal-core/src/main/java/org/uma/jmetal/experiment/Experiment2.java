package org.uma.jmetal.experiment;

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
  private LinkedList<ExperimentOutput> resultObjectList ;
  
  private ExperimentData experimentData ;
  
  /** Constructor */
  private Experiment2(Builder builder) {
  	experimentData = builder.experimentData ;
  	//getExperimentData() ;
    resultObjectList = builder.resultObjectList ;

  }
  
  /** Builder */
  public static class Builder {
  	private final ExperimentData experimentData ;
    private LinkedList<ExperimentOutput> resultObjectList ;

    public Builder(ExperimentData experimentData) {
  		this.experimentData = experimentData ;
      this.resultObjectList = new LinkedList<>() ;
  	}

    public Builder addExperimentOutput(ExperimentOutput experimentResult) {
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

  public void run() {
    for (ExperimentOutput result : resultObjectList) {
      result.generate();
    }
  }

}
  
