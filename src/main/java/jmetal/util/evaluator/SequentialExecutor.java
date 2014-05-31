package jmetal.util.evaluator;

import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;

/**
 * Created by Antonio J. Nebro on 30/05/14.
 */
public class SequentialExecutor implements Executor {
//  @Override
//    public void startup(Object parameters) {
//  }


  @Override
  public SolutionSet evaluate(SolutionSet solutionSet, Problem problem) {
    try {
      for (int i = 0 ; i < solutionSet.size(); i++) {
        problem.evaluate(solutionSet.get(i)) ;
      }
    } catch (JMException e) {
      e.printStackTrace();
    }

    return solutionSet;
  }

 @Override public void shutdown() {
 }
}
