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

package org.uma.jmetal.util.experiment.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.parallel.SynchronousParallelTaskExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

/**
 * Class for executing independent runs of algorithms
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MultithreadedExperimentExecutor<S extends Solution<?>, Result>
    implements SynchronousParallelTaskExecutor<Object> {
  private Collection<EvaluationTask<S,Result>> taskList;
  private int numberOfThreads;
  private ExecutorService executor;

  /** Constructor */
  public MultithreadedExperimentExecutor(int threads) {
    numberOfThreads = threads;
    if (threads == 0) {
      numberOfThreads = Runtime.getRuntime().availableProcessors();
    } else if (threads < 0) {
      JMetalLogger.logger.severe("MultithreadedExperimentExecutor: the number of threads" +
        " cannot be negative number " + threads);
    } else {
      numberOfThreads = threads;
    }
    JMetalLogger.logger.info("THREADS: " + numberOfThreads);
  }

  @Override
  public void start(Object configuration) {
    executor = Executors.newFixedThreadPool(numberOfThreads);
    JMetalLogger.logger.info("Cores: " + numberOfThreads);
    taskList = null;
  }

  public void addTask(Object[] taskParameters) {
    if (taskList == null) {
      taskList = new ArrayList<EvaluationTask<S,Result>>();
    }

    @SuppressWarnings("unchecked")
    TaggedAlgorithm<Result> algorithm = (TaggedAlgorithm<Result>) taskParameters[0];
    Integer id = (Integer) taskParameters[1];
    Experiment<?,?> experimentData = (Experiment<?,?>) taskParameters[2] ;
    taskList.add(new EvaluationTask<S, Result>(algorithm, id, experimentData));
  }

  public Object parallelExecution() {
    List<Future<Object>> future = null;
    try {
      future = executor.invokeAll(taskList);
    } catch (InterruptedException e1) {
      JMetalLogger.logger.log(Level.SEVERE, "Error", e1);
    }
    List<Integer> resultList = new Vector<Integer>();

    for (Future<Object> result : future) {
      Object returnValue = null;
      try {
        returnValue = result.get();
        resultList.add((Integer) returnValue);
      } catch (InterruptedException e) {
        JMetalLogger.logger.log(Level.SEVERE, "Error", e);
      } catch (ExecutionException e) {
        JMetalLogger.logger.log(Level.SEVERE, "Error", e);
      }
    }

    taskList = null;
    return null;
  }

  public void stop() {
    executor.shutdown();
  }
}
