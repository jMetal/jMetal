package org.uma.jmetal.operator;

import java.io.Serializable;

/** Interface representing an operator
 *
 * @author Antonio J. Nebro
 * @version 1.0

 * @param <Source> Source Class of the object to be operated with
 * @param <Result> Result Class of the result obtained after applying the operator
 */
public interface Operator<Source, Result> extends Serializable {
  /**
   * @param source The data to process
   */
  Result execute(Source source) ;
}
