package org.uma.jmetal.operator;

import org.uma.jmetal.solution.Solution;

/**
 * Created by cbarba on 5/3/15.
 */
public interface LocalSearchOperator <Source extends Solution<?>> extends Operator<Source, Source> {
  int getEvaluations();
  int getNumberOfImprovements() ;
  int getNumberOfNonComparableSolutions() ;
}
