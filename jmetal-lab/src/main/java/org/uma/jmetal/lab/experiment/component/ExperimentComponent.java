package org.uma.jmetal.lab.experiment.component;

import java.io.IOException;

/**
 * An org.uma.jmetal.experiment is composed of instances of this interface.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface ExperimentComponent {
  void run() throws IOException;
}
