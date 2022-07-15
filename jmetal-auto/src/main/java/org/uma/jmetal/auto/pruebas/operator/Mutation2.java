package org.uma.jmetal.auto.pruebas.operator;

import org.uma.jmetal.auto.pruebas.solution.Solution2;

/**
 * Interface representing mutation operators
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 *
 */
public interface Mutation2<S extends Solution2>  {
    S execute(S solution);

    double getMutationProbability() ;
}
