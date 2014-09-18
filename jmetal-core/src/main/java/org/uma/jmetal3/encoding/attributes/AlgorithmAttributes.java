package org.uma.jmetal3.encoding.attributes;

import java.util.HashMap;

/**
 * A {link Solution} has a set of components that are {@link Problem} related (variables, objective values, etc)
 * and, optionally, a set of attributes that are {@link Algorithm} dependant (e.g., the ranking and crowding distance
 * of NSGA-II.  The {@link AlgorithmAttibutes} interface models these kinds of attributes.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public abstract class AlgorithmAttributes {
@Override
  public abstract Object clone() ;
}
