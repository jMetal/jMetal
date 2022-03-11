package org.uma.jmetal.util.legacy.qualityindicator;

import java.io.Serializable;
import org.uma.jmetal.util.naming.DescribedEntity;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>

 * @param <Evaluate> Entity to runAlgorithm
 * @param <Result> Result of the evaluation
 */
@Deprecated
public interface QualityIndicator<Evaluate, Result> extends DescribedEntity, Serializable {
  Result evaluate(Evaluate evaluate) ;
}
