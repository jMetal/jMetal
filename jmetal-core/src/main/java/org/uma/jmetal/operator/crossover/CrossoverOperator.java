package org.uma.jmetal.operator.crossover;

import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Solution;

import java.util.List;

/**
 * Created by Antonio J. Nebro on 04/09/14.
 */
public interface CrossoverOperator<
        Source extends List<? extends Solution<?>>,
        Result extends List<? extends Solution<?>>> extends Operator<Source,Result> {
}
