package org.uma.jmetal.operator.crossover;

import java.util.List;
import org.uma.jmetal.operator.Operator;

/**
 * Interface representing crossover operators. They will receive a list of solutions and return
 * another list of solutions
 *
 * @author Antonio J. Nebro
 *
 * @param <Source> The class of the solutions
 */
public interface CrossoverOperator<Source> extends Operator<List<Source>,List<Source>> {
  double crossoverProbability() ;
  int numberOfRequiredParents() ;
  int numberOfGeneratedChildren() ;
}
