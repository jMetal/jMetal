package org.uma.jmetal.operator;

import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by Antonio J. Nebro on 04/09/14.
 */
public interface CrossoverOperator<S extends Solution<?>> extends Operator<List<S>,List<S>> {
}
