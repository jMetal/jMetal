//  MultithreadedEvaluator.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
<<<<<<< HEAD
//  Copyright (c) 2014 Antonio J. Nebro
=======
//  Copyright (c) 2013 Antonio J. Nebro
>>>>>>> master
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

package jmetal.util.parallel;

import jmetal.core.Problem;
import jmetal.core.Solution;
<<<<<<< HEAD
=======
import jmetal.util.Configuration;
>>>>>>> master

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
<<<<<<< HEAD
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
=======
import java.util.concurrent.*;
>>>>>>> master

/**
 * @author Antonio J. Nebro
 * Class for evaluating solutions in parallel using threads
 */
<<<<<<< HEAD
public class MultithreadedEvaluator extends SynchronousParallelRunner {
  private Problem problem_ ;
  private Collection<EvaluationTask> taskList_ ;

=======
public class MultithreadedEvaluator implements IParallelEvaluator {
  private int numberOfThreads_ ;
  private Problem problem_ ;
  private ExecutorService executor_ ;
  private Collection<Callable<Solution>> taskList_ ;

  /**
   * @author Antonio J. Nebro
   * Private class representing tasks to evaluate solutions. 
   */

  private class EvaluationTask implements Callable<Solution> {
    private Problem problem_ ;
    private Solution solution_ ;

    /**
     * Constructor
     * @param problem Problem to solve
     * @param solution Solution to evaluate
     */
    public EvaluationTask(Problem problem,  Solution solution) {
      problem_ = problem ;
      solution_ = solution ;
    }

    public Solution call() throws Exception {
      //long initTime = System.currentTimeMillis();
      problem_.evaluate(solution_) ;
      problem_.evaluateConstraints(solution_) ;
      //long estimatedTime = System.currentTimeMillis() - initTime;
      //System.out.println("Time: "+ estimatedTime) ;
      return solution_ ;
    } 
  }
>>>>>>> master

  /**
   * Constructor
   * @param threads 
   */
  public MultithreadedEvaluator(int threads) {
<<<<<<< HEAD
    super(threads) ;
=======
    numberOfThreads_ = threads ;
    if (threads == 0)
      numberOfThreads_ = Runtime.getRuntime().availableProcessors() ;
    else if (threads < 0) {
      Configuration.logger_.severe("MultithreadedEvaluator: the number of threads" +
          " cannot be negative number " + threads);
    }
    else {
      numberOfThreads_ = threads ;
    }
>>>>>>> master
  }

  /**
   * Constructor
   * @param problem problem to solve
   */
<<<<<<< HEAD
  public void startParallelRunner(Object problem) {
    problem_ = (Problem)problem ;

    executor_ = Executors.newFixedThreadPool(numberOfThreads_) ;
    System.out.println("Cores: "+ numberOfThreads_) ;
    taskList_ = null ; 
=======
  public void startEvaluator(Problem problem) {
    executor_ = Executors.newFixedThreadPool(numberOfThreads_) ;
    System.out.println("Cores: "+ numberOfThreads_) ;
    taskList_ = null ; 
    problem_ = problem ;
>>>>>>> master
  }

  /**
   * Adds a solution to be evaluated to a list of tasks
<<<<<<< HEAD
   */
  public void addTaskForExecution(Object[] taskParameters) {
    Solution solution = (Solution)taskParameters[0] ;
    if (taskList_ == null)
      taskList_ = new ArrayList<EvaluationTask>();
=======
   * @param solution Solution to be evaluated
   */
  public void addSolutionForEvaluation(Solution solution) {
    if (taskList_ == null)
      taskList_ = new ArrayList<Callable<Solution>>();
>>>>>>> master

    taskList_.add(new EvaluationTask(problem_, solution)) ;			
  }

  /**
   * Evaluates a list of solutions
   * @return A list with the evaluated solutions
   */
<<<<<<< HEAD
  public Object parallelExecution() {
    List<Future<Object>> future = null ;
=======
  public List<Solution> parallelEvaluation() {
    List<Future<Solution>> future = null ;
>>>>>>> master
    try {
      future = executor_.invokeAll(taskList_);
    } catch (InterruptedException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
<<<<<<< HEAD
    List<Object> solutionList = new Vector<Object>() ;

    for(Future<Object> result : future){
      Object solution = null ;
=======
    List<Solution> solutionList = new Vector<Solution>() ;

    for(Future<Solution> result : future){
      Solution solution = null ;
>>>>>>> master
      try {
        solution = result.get();
        solutionList.add(solution) ;
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ExecutionException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    taskList_ = null ;
    return solutionList ;
  }

  /**
   * Shutdown the executor
   */
  public void stopEvaluator() {
    executor_.shutdown() ;
  }
<<<<<<< HEAD

  /**
   * @author Antonio J. Nebro
   * Private class representing tasks to evaluate solutions.
   */

  private class EvaluationTask extends ParallelTask {
    private Problem problem_ ;
    private Solution solution_ ;

    /**
     * Constructor
     * @param problem Problem to solve
     * @param solution Solution to evaluate
     */
    public EvaluationTask(Problem problem,  Solution solution) {
      problem_ = problem ;
      solution_ = solution ;
    }

    public Solution call() throws Exception {
      problem_.evaluate(solution_) ;
      problem_.evaluateConstraints(solution_) ;

      return solution_ ;
    }
  }
=======
>>>>>>> master
}
