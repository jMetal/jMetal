package org.uma.jmetal.algorithm;

import java.io.Serializable;

/**
 * Interface representing an algorithm
 * @author Antonio J. Nebro
 * @version 1.0
 * @param <R> Result
 */
public interface Algorithm<R> extends Runnable, Serializable {
  void run() ;
  R result() ;

  String name();
  String description();
}
