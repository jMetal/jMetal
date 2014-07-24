package org.uma.jmetal.experiment;

import java.util.LinkedList;

/**
 * Created by Antonio J. Nebro on 18/02/14.
 * Class for configuring and running an experiment
 */
public class Experiment {
  private LinkedList<ExperimentOutput> resultObjectList ;

  /** Constructor */
  private Experiment(Builder builder) {
    resultObjectList = builder.resultObjectList ;
  }
  
  /** Builder */
  public static class Builder {
    private LinkedList<ExperimentOutput> resultObjectList ;

    public Builder(ExperimentData experimentData) {
      this.resultObjectList = new LinkedList<>() ;
  	}

    public Builder addExperimentOutput(ExperimentOutput experimentResult) {
      resultObjectList.add(experimentResult) ;

      return this ;
    }

  	public Experiment build() {
  		return new Experiment(this) ;
  	}
  }

  public void run() {
    for (ExperimentOutput result : resultObjectList) {
      result.generate();
    }
  }

}
  
