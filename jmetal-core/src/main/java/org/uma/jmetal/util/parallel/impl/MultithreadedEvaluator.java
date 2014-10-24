//  MultithreadedEvaluator.java
//
//  Author:
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

package org.uma.jmetal.util.parallel.impl;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.parallel.SynchronousParallelTaskExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.*;
import java.util.logging.Level;

/**
 * @author Antonio J. Nebro
 *         Class for evaluating solutions in parallel using threads
 */
public class MultithreadedEvaluator implements SynchronousParallelTaskExecutor {
  private Problem problem;
  private Collection<EvaluationTask> taskList;
  private int numberOfThreads;
  private ExecutorService executor;

  /**
   * Constructor
   *
   * @param threads Number of requested threads. A value of 0 implicates to request the maximum
   *                number of available threads in the system.
   */
  public MultithreadedEvaluator(int threads) {
    numberOfThreads = threads;
    if (threads == 0) {
      numberOfThreads = Runtime.getRuntime().availableProcessors();
    } else if (threads < 0) {
      JMetalLogger.logger.severe("MultithreadedEvaluator: the number of threads" +
        " cannot be negative number " + threads);
    } else {
      numberOfThreads = threads;
    }
    JMetalLogger.logger.info("THREADS: " + numberOfThreads);
  }

  /**
   * Constructor
   *
   * @param problem problem to solve
   */
  public void start(Object problem) {
    this.problem = (Problem) problem;

    executor = Executors.newFixedThreadPool(numberOfThreads);
    JMetalLogger.logger.info("Cores: " + numberOfThreads);
    taskList = null;
  }

  /**
   * Adds a solution to be evaluated to a list of tasks
   */
  public void addTask(Object[] taskParameters) {
    Solution<?> solution = (Solution<?>) taskParameters[0];
    if (taskList == null) {
      taskList = new ArrayList<EvaluationTask>();
    }

    taskList.add(new EvaluationTask(problem, solution));
  }

  /**
   * Evaluates a list of solutions
   *
   * @return A list with the evaluated solutions
   */
  public Object parallelExecution() {
    List<Future<Object>> future = null;
    try {
      future = executor.invokeAll(taskList);
    } catch (InterruptedException e1) {
      JMetalLogger.logger.log(Level.SEVERE, "Error", e1);
    }
    List<Object> solutionList = new Vector<Object>();

    for (Future<Object> result : future) {
      Object solution = null;
      try {
        solution = result.get();
        solutionList.add(solution);
      } catch (InterruptedException e) {
        JMetalLogger.logger.log(Level.SEVERE, "Error", e);
      } catch (ExecutionException e) {
        JMetalLogger.logger.log(Level.SEVERE, "Error", e);
      }
    }
    taskList = null;
    return solutionList;
  }

  /**
   * Shutdown the executor
   */
  public void stop() {
    executor.shutdown();
  }

  /**
   * @author Antonio J. Nebro
   *         Private class representing tasks to evaluate solutions.
   */

  private class EvaluationTask implements Callable<Object> {
    private Problem problem;
    private Solution solution;

    /**
     * Constructor
     *
     * @param problem  Problem to solve
     * @param solution Solution to evaluate
     */
    public EvaluationTask(Problem problem, Solution solution) {
      this.problem = problem;
      this.solution = solution;
    }

    public Solution call() throws Exception {
      problem.evaluate(solution);

      return solution;
    }
  }
}
