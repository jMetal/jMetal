package org.uma.jmetal3.operator;

import org.uma.jmetal3.core.Operator;
import org.uma.jmetal3.core.Solution;

import java.util.Collection;

/**
 * Created by Antonio J. Nebro on 04/09/14.
 */
public interface PopulationOperator<Source,Result extends Solution> extends Operator<Collection<Source>,Result> {
}
