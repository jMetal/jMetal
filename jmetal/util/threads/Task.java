package jmetal.util.threads;

import java.util.concurrent.Callable;

import jmetal.core.Problem;
import jmetal.core.Solution; 

public class Task implements Callable<Solution>{
	private Problem problem_ ;
	private Solution solution_ ;
  public Task(Problem problem,  Solution solution) {
  	problem_ = problem ;
  	solution_ = solution ;
  }
  
  public Solution call() throws Exception {
    long initTime = System.currentTimeMillis();
    problem_.evaluate(solution_) ;
    problem_.evaluateConstraints(solution_) ;
    long estimatedTime = System.currentTimeMillis() - initTime;
    System.out.println("Time: "+ estimatedTime) ;
    return solution_ ;
  }
  
}
