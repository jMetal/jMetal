package jmetal.util.evaluator;

import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.util.parallel.MultithreadedEvaluator;

/**
 * Created by Antonio J. Nebro on 30/05/14.
 */
public class MultithreadedExecutor implements Executor {
  private MultithreadedEvaluator evaluator_ ;
  private Problem problem_ ;

  public  MultithreadedExecutor(int numberOfThreads, Problem problem) {
    evaluator_ = new MultithreadedEvaluator(numberOfThreads)  ;
    problem_ = problem ;
    evaluator_.start(problem) ;
  }

  @Override
  public SolutionSet evaluate(SolutionSet solutionSet, Problem problem) {
    for (int i = 0 ; i < solutionSet.size(); i++) {
      evaluator_.addTask(new Object[] {solutionSet.get(i)});
    }
    evaluator_.parallelExecution() ;

    return solutionSet;
  }

  @Override public void shutdown() {
    evaluator_.stop();
  }

}
