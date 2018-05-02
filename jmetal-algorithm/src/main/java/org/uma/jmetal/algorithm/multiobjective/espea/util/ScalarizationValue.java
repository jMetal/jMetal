package org.uma.jmetal.algorithm.multiobjective.espea.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

/**
 * Scalarization attribute. A scalarization value is an aggregation of the
 * objective values.
 *
 * @param <S> The solution type
 * @author Marlon Braun <marlon.braun@partner.kit.edu>
 */
@SuppressWarnings("serial")
public class ScalarizationValue<S extends Solution<?>> extends GenericSolutionAttribute<S, Double> {

}
