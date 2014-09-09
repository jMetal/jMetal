package org.uma.jmetal3.operator.mutation;

import org.uma.jmetal3.core.Operator;
import org.uma.jmetal3.core.Solution;

/**
 * Created by Antonio J. Nebro on 05/09/14.
 */
public interface MutationOperator<Source extends Solution<?>> extends Operator<Source, Source> {
}
