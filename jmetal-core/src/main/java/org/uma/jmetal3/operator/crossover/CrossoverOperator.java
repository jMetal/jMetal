package org.uma.jmetal3.operator.crossover;

import org.uma.jmetal3.core.Operator;
import org.uma.jmetal3.core.Solution;

import java.util.Collection;

/**
 * Created by Antonio J. Nebro on 04/09/14.
 */
public interface CrossoverOperator<Source extends Collection<Solution>,Result extends Collection<Solution>> extends Operator<Source,Result> {
}
