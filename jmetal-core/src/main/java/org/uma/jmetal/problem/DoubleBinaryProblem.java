package org.uma.jmetal.problem;

/**
 * Interface representing problems having integer and double variables
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @deprecated Not used. For examples of multiple encodings, consider
 *             {@link IntegerDoubleProblem} instead.
 */
@Deprecated
public interface DoubleBinaryProblem<S> extends BoundedProblem<Number, S> {
  public int getNumberOfDoubleVariables() ;
  public int getNumberOfBits() ;
}
