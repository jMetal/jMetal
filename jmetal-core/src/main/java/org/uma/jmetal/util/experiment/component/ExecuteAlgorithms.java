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

package org.uma.jmetal.util.experiment.component;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.util.MultithreadedExperimentExecutor;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;

import java.io.File;

/**
 * This class executes the algorithms the have been configured with a instance of class
 * {@link Experiment}. For each combination algorithm + problem + runId an instance
 * of {@link TaggedAlgorithm} is created and inserted as a task of a {@link MultithreadedExperimentExecutor},
 * which runs all the algorithms.
 *
 * The result of the execution is a pair of files FUNrunId.tsv and VARrunID.tsv per experiment, which are
 * stored in the directory {@link Experiment #getExperimentBaseDirectory()}/algorithmName/problemName.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ExecuteAlgorithms<S extends Solution<?>, Result> implements ExperimentComponent {
  private Experiment<S, Result> experiment;

  /** Constructor */
  public ExecuteAlgorithms(Experiment<S, Result> configuration) {
    this.experiment = configuration ;
  }

  @Override
  public void run() {
    JMetalLogger.logger.info("ExecuteAlgorithms: Preparing output directory");
    prepareOutputDirectory() ;

    MultithreadedExperimentExecutor<S, Result> parallelExecutor ;

    parallelExecutor = new MultithreadedExperimentExecutor<S, Result>(experiment.getNumberOfCores()) ;
    parallelExecutor.start(this);

    for (TaggedAlgorithm<Result> algorithm : experiment.getAlgorithmList()) {
      //for (int i = 0; i < experiment.getIndependentRuns(); i++) {
      //TaggedAlgorithm<Result> clonedAlgorithm = SerializationUtils.clone(algorithm) ;
      parallelExecutor.addTask(new Object[]{algorithm, algorithm.getRunId(), experiment});
      //}
    }
    parallelExecutor.parallelExecution();
    parallelExecutor.stop();  }

  private void prepareOutputDirectory() {
    if (experimentDirectoryDoesNotExist()) {
      createExperimentDirectory() ;
    }
  }

  private boolean experimentDirectoryDoesNotExist() {
    boolean result;
    File experimentDirectory;

    experimentDirectory = new File(experiment.getExperimentBaseDirectory());
    if (experimentDirectory.exists() && experimentDirectory.isDirectory()) {
      result = false;
    } else {
      result = true;
    }

    return result;
  }

  private void createExperimentDirectory() {
    File experimentDirectory;
    experimentDirectory = new File(experiment.getExperimentBaseDirectory());

    if (experimentDirectory.exists()) {
      experimentDirectory.delete() ;
    }

    boolean result ;
    result = new File(experiment.getExperimentBaseDirectory()).mkdirs() ;
    if (!result) {
      throw new JMetalException("Error creating experiment directory: " +
          experiment.getExperimentBaseDirectory()) ;
    }
  }
}
