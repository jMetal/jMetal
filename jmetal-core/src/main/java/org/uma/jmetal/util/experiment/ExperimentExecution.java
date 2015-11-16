//  AlgorithmExecution.java
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

package org.uma.jmetal.util.experiment;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.experiment.util.MultithreadedExperimentExecutor;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;

import java.io.File;

/**
 * Created by Antonio J. Nebro on 18/07/14.
 */
public class ExperimentExecution<S extends Solution<?>, Result> {
  private ExperimentConfiguration<S, Result> configuration ;

  /** Constructor */
  public ExperimentExecution(ExperimentConfiguration<S, Result> configuration) {
    this.configuration = configuration ;
  }

  public void run() {
    JMetalLogger.logger.info("ExperimentExecution: Preparing output directory");
    prepareOutputDirectory() ;

    MultithreadedExperimentExecutor<S, Result> parallelExecutor ;
    parallelExecutor = new MultithreadedExperimentExecutor<S, Result>(configuration.getNumberOfCores()) ;
    parallelExecutor.start(this);


    for (TaggedAlgorithm<Result> algorithm : configuration.getAlgorithmList()) {
        //for (int i = 0; i < configuration.getIndependentRuns(); i++) {
          //System.out.println(algorithm.getTag() + " | " + algorithm.getProblem().getName() + " | " + i) ;
          //AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
          //    .execute();
          //Result population = algorithm.getResult() ;
          parallelExecutor.addTask(new Object[]{algorithm, algorithm.getRunId(), configuration});
      //}
    }

    parallelExecutor.parallelExecution();
    parallelExecutor.stop();
  }

  private void prepareOutputDirectory() {
    if (experimentDirectoryDoesNotExist()) {
      createExperimentDirectory() ;
    }
  }

  private boolean experimentDirectoryDoesNotExist() {
    boolean result;
    File experimentDirectory;

    experimentDirectory = new File(configuration.getExperimentBaseDirectory());
    if (experimentDirectory.exists() && experimentDirectory.isDirectory()) {
      result = false;
    } else {
      result = true;
    }

    return result;
  }

  private void createExperimentDirectory() {
    File experimentDirectory;
    experimentDirectory = new File(configuration.getExperimentBaseDirectory());

    if (experimentDirectory.exists()) {
      experimentDirectory.delete() ;
    }

    boolean result ;
    result = new File(configuration.getExperimentBaseDirectory()).mkdirs() ;
    if (!result) {
      throw new JMetalException("Error creating experiment directory: " +
        configuration.getExperimentBaseDirectory()) ;
    }
  }
}
