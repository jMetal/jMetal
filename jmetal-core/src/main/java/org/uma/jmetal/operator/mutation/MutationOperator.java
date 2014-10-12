package org.uma.jmetal.operator.mutation;

import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Solution;

/**
 * Created by Antonio J. Nebro on 05/09/14.
 */
public interface MutationOperator<Source extends Solution<?>> extends Operator<Source, Source> {
}
