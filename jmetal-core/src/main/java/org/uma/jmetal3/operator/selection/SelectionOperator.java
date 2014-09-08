package org.uma.jmetal3.operator.selection;

import org.uma.jmetal3.core.Operator;
import org.uma.jmetal3.core.Solution;

import java.util.List;

/**
 * Created by Antonio J. Nebro on 04/09/14.
 */
public interface SelectionOperator<Source extends List<Solution>,Result extends Solution<?>> extends Operator<Source,Result> {
}
