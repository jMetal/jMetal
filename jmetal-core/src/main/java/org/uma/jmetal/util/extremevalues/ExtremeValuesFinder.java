package org.uma.jmetal.util.extremevalues;

/**
 * Created by ajnebro on 23/4/15.
 */
public interface ExtremeValuesFinder<S, R> {
  R findLowestValues(S source) ;
  R findHighestValues(S source) ;
}
