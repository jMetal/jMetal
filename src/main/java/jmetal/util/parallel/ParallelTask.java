package jmetal.util.parallel;

import java.util.concurrent.Callable;

/**
 * Created by Antonio J. Nebro on 20/02/14.
 */
abstract public class ParallelTask implements Callable<Object> {
  abstract public Object call() throws Exception ;
}
