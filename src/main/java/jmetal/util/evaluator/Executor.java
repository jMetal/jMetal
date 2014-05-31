package jmetal.util.evaluator;

import jmetal.core.Problem;
import jmetal.core.SolutionSet;

/**
 * Created by Antonio J. Nebro on 30/05/14.
 */
public interface Executor {
  //public void startup(Object parameters) ;
  public SolutionSet evaluate(SolutionSet solutionSet, Problem problem) ;
  public void shutdown() ;
}
