package org.uma.jmetal.util.extremevalues;

/**
 * Interface representing classes aimed at finding the extreme values of Source objects (e.g., lists)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 *
 * @param <Source>
 * @param <Result>
 */
public interface ExtremeValuesFinder<Source, Result> {
  Result findLowestValues(Source source) ;
  Result findHighestValues(Source source) ;
}
