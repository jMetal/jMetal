//  MultithreadedEvaluator.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro
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
import jmetal.util.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.*;

/**
 * @author Antonio J. Nebro
 * Class for evaluating solutions in parallel using threads
 */
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
      long initTime = System.currentTimeMillis();
      problem_.evaluate(solution_) ;
      problem_.evaluateConstraints(solution_) ;
      //long estimatedTime = System.currentTimeMillis() - initTime;
      //System.out.println("Time: "+ estimatedTime) ;
      return solution_ ;
    } 
  }

  /**
   * Constructor
   * @param threads 
   */
  public MultithreadedEvaluator(int threads) {
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
  }

  /**
   * Constructor
   * @param problem problem to solve
   */
  public void startEvaluator(Problem problem) {
    executor_ = Executors.newFixedThreadPool(numberOfThreads_) ;
    System.out.println("Cores: "+ numberOfThreads_) ;
    taskList_ = null ; 
    problem_ = problem ;
  }

  /**
   * Adds a solution to be evaluated to a list of tasks
   * @param solution Solution to be evaluated
   */
  public void addSolutionForEvaluation(Solution solution) {
    if (taskList_ == null)
      taskList_ = new ArrayList<Callable<Solution>>();

    taskList_.add(new EvaluationTask(problem_, solution)) ;			
  }

  /**
   * Evaluates a list of solutions
   * @return A list with the evaluated solutions
   */
  public List<Solution> parallelEvaluation() {
    List<Future<Solution>> future = null ;
    try {
      future = executor_.invokeAll(taskList_);
    } catch (InterruptedException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    List<Solution> solutionList = new Vector<Solution>() ;

    for(Future<Solution> result : future){
      Solution solution = null ;
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
}
