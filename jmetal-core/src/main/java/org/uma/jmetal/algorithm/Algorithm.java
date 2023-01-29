package org.uma.jmetal.algorithm;

import java.io.Serializable;
import org.uma.jmetal.util.naming.DescribedEntity;

/**
 * Interface representing an algorithm
 * @author Antonio J. Nebro
 * @version 1.0
 * @param <Result> Result
 */
public interface Algorithm<R> extends Runnable, Serializable, DescribedEntity {
  void run() ;
  R result() ;
}
