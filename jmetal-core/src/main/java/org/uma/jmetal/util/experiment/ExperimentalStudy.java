package org.uma.jmetal.util.experiment;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 18/02/14.
 * Class for configuring and running an experimental study
 */
public class ExperimentalStudy {
  private List<ExperimentComponent> resultObjectList ;

  /** Constructor */
  private ExperimentalStudy(Builder builder) {
    resultObjectList = builder.resultObjectList ;
  }
  
  /** Builder */
  public static class Builder {
    private LinkedList<ExperimentComponent> resultObjectList ;

    public Builder(ExperimentConfiguration<?, ?> experimentData) {
      this.resultObjectList = new LinkedList<>() ;
  	}

    public Builder addExperiment(ExperimentComponent experimentComponent) {
      resultObjectList.add(experimentComponent) ;

      return this ;
    }

  	public ExperimentalStudy build() {
  		return new ExperimentalStudy(this) ;
  	}
  }

  public void run() {
    for (ExperimentComponent result : resultObjectList) {
      result.run();
    }
  }
}
  
