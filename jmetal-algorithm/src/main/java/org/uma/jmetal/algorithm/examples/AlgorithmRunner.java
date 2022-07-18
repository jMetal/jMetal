package org.uma.jmetal.algorithm.examples;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class for running algorithms in a concurrent thread
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class AlgorithmRunner {
  private long computingTime;

  /** Constructor */
  private AlgorithmRunner(@NotNull Executor execute) {
    computingTime = execute.computingTime;
  }

  /* Getters */
  public long getComputingTime() {
    return computingTime;
  }

  /** Executor class */
  public static class Executor {
    Algorithm<?> algorithm ;
    long computingTime;

    public Executor(Algorithm<?> algorithm) {
      this.algorithm = algorithm ;
    }

    public AlgorithmRunner execute() {
      long initTime = System.currentTimeMillis();
      Thread thread = new Thread(algorithm) ;
      thread.start();
      try {
        thread.join();
      } catch (InterruptedException e) {
        throw new JMetalException("Error in thread.join()", e) ;
      }
      computingTime = System.currentTimeMillis() - initTime ;

      return new AlgorithmRunner(this) ;
    }
  }
}

