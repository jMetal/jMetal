package org.uma.jmetal.qualityindicator;

import org.uma.jmetal.util.naming.DescribedEntity;

import java.io.Serializable;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>

 * @param <Evaluate> Entity to runAlgorithm
 * @param <Result> Result of the evaluation
 */
public interface QualityIndicator<Evaluate, Result> extends DescribedEntity, Serializable {
  public Result evaluate(Evaluate evaluate) ;
}
