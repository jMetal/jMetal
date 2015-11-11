//  MultithreadedExperimentExecutor.java
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

package org.uma.jmetal.util.experiment.util;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.experiment.ExperimentConfiguration;
import org.uma.jmetal.util.experiment.impl.AlgorithmExecution;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.parallel.SynchronousParallelTaskExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.*;
import java.util.logging.Level;

/**
 * Created by Antonio J. Nebro on 18/07/14.
 * Class for executing independent runs of algorithms
 */
public class MultithreadedExperimentExecutor<S extends Solution<?>, Result>
    implements SynchronousParallelTaskExecutor {
  private Collection<EvaluationTask<S,Result>> taskList;
  private AlgorithmExecution<S, Result> experimentExecution;
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

  public void start(Object object) {
    experimentExecution = (AlgorithmExecution<S, Result>)object ;
    executor = Executors.newFixedThreadPool(numberOfThreads);
    JMetalLogger.logger.info("Cores: " + numberOfThreads);
    taskList = null;
  }

  public void addTask(Object[] taskParameters) {
    if (taskList == null) {
      taskList = new ArrayList<EvaluationTask<S,Result>>();
    }

    TaggedAlgorithm<Result> algorithm = (TaggedAlgorithm<Result>) taskParameters[0];
    Integer id = (Integer) taskParameters[1];
    ExperimentConfiguration<?,?> experimentData = (ExperimentConfiguration<?,?>) taskParameters[2] ;
    taskList.add(new EvaluationTask(algorithm, id, experimentData));
  }

  public Object parallelExecution() {
    List<Future<Object>> future = null;
    try {
      future = executor.invokeAll(taskList);
    } catch (InterruptedException e1) {
      JMetalLogger.logger.log(Level.SEVERE, "Error", e1);
    }
    List<Object> resultList = new Vector<Object>();

    for (Future<Object> result : future) {
      Object returnValue = null;
      try {
        returnValue = result.get();
        resultList.add(returnValue);
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

  /** Class defining the tasks to be computed in parallel */
  private class EvaluationTask<S extends Solution<?>, Result> implements Callable<Object> {
    private TaggedAlgorithm<Result> algorithm ;
    private int id;
    private ExperimentConfiguration<?, ?> experimentData ;

    /** Constructor */
    public EvaluationTask(TaggedAlgorithm<Result> algorithm, int id, ExperimentConfiguration<?,?> experimentData) {
      JMetalLogger.logger.info(
        " Task: " + algorithm.getTag() + ", problem: " + algorithm.getProblem().getName() + ", run: " + id);
      this.algorithm = algorithm ;
      this.id = id;
      this.experimentData = experimentData ;
    }

    public Integer call() throws Exception {
      JMetalLogger.logger.info(
          " Running algorithm: " + algorithm.getTag() +
              ", problem: " + algorithm.getProblem().getName() +
              ", run: " + id);

      String outputDirectoryName = experimentData.getExperimentBaseDirectory()
          + "/data/"
          + algorithm.getTag()
          + "/"
          + algorithm.getProblem().getName() ;

      File outputDirectory = new File(outputDirectoryName);
      if (!outputDirectory.exists()) {
        boolean result = new File(outputDirectoryName).mkdirs();
        JMetalLogger.logger.info("Creating " + outputDirectoryName);
      }

      //AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
      //    .execute();
      algorithm.run();

      Result population = algorithm.getResult() ;

      new SolutionSetOutput.Printer((List<? extends Solution<?>>)population)
          .setSeparator("\t")
          .setVarFileOutputContext(new DefaultFileOutputContext(outputDirectoryName+"/VAR" + id+".tsv"))
          .setFunFileOutputContext(new DefaultFileOutputContext(outputDirectoryName+"/FUN" + id+".tsv"))
          .print();

      /*
      Algorithm algorithm;
      Object[] settingsParams = {problemName};
      Settings settings;

        settings = (new SettingsFactory()).getSettingsObject(algorithmName, settingsParams);
        algorithm = settings.configure();

      JMetalLogger.logger.info(
        " Running algorithm: " + algorithmName + ", problem: " + problemName + ", run: " + id);

      SolutionSet resultFront = algorithm.execute();

      File experimentDirectory;
      String directory;

      directory =
        experimentData.getExperimentBaseDirectory() + "/data/" + algorithmName + "/" + problemName;

      experimentDirectory = new File(directory);
      if (!experimentDirectory.exists()) {
        boolean result = new File(directory).mkdirs();
        JMetalLogger.logger.info("Creating " + directory);
      }

      SolutionSetOutput.printObjectivesToFile(resultFront,
          directory + "/" + experimentExecution.getParetoFrontFileName() + "." + id);
      SolutionSetOutput.printVariablesToFile(resultFront,
          directory + "/" + experimentExecution.getParetoSetFileName() + "." + id);
*/
      return id;
    }

  }

}
