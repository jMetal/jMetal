package org.uma.jmetal.operator;

import org.uma.jmetal.solution.Solution;

/**
 * Interface representing mutation operators
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 *
 * @param <Source> The solution class of the solution to be mutated
 */
public interface MutationOperator<Source extends Solution<?>> extends Operator<Source, Source> {
}
