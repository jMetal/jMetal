package org.uma.jmetal.util.experiment;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ajnebro on 27/11/15.
 */
public class ExperimentExecution {
  private List<ExperimentComponent> experimentComponents = new LinkedList<>() ;

  public ExperimentExecution add(ExperimentComponent component) {
    experimentComponents.add(component);

    return this ;
  }

  public void run() throws IOException {
    for (ExperimentComponent component : experimentComponents) {
      component.run() ;
    }
  }
}
