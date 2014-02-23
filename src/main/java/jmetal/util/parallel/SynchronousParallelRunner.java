package jmetal.util.parallel;

import jmetal.util.Configuration;

import java.util.concurrent.ExecutorService;

/**
 * Created by Antonio J. Nebro on 09/02/14.
 */
abstract public class SynchronousParallelRunner {
  protected int numberOfThreads_ ;
  protected ExecutorService executor_ ;


  public SynchronousParallelRunner(int threads) {
    numberOfThreads_ = threads ;
    if (threads == 0)
      numberOfThreads_ = Runtime.getRuntime().availableProcessors() ;
    else if (threads < 0) {
      Configuration.logger_.severe("SynchronousParallelRunner: the number of threads" +
              " cannot be negative number " + threads);
    }
    else {
      numberOfThreads_ = threads ;
    }
    System.out.println("THREADS: " + numberOfThreads_) ;
  }

  abstract public void startParallelRunner(Object configuration) ;
  abstract public void addTaskForExecution(Object[] taskParameters) ;
  abstract public Object parallelExecution() ;

  /**
   * Shutdown the executor
   */
  public void stopEvaluator() {
    executor_.shutdown() ;
  }}
