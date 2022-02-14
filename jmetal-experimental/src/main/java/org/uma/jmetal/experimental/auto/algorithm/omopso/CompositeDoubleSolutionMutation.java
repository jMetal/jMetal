package org.uma.jmetal.experimental.auto.algorithm.omopso;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class allows to apply a list of crossover operator on the solutions belonging to a list of
 * {@link CompositeSolution} objects. It is required that the operators be compatible with the
 * solutions inside the composite solutions.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class CompositeDoubleSolutionMutation implements MutationOperator<DoubleSolution> {
  private List<MutationOperator<DoubleSolution>> operators;
  private double mutationProbability = 1.0;

  /** Constructor */
  public CompositeDoubleSolutionMutation(List<MutationOperator<DoubleSolution>> operators) {
    Check.notNull(operators);
    Check.collectionIsNotEmpty(operators);

    this.operators = new ArrayList<>();
    for (int i = 0; i < operators.size(); i++) {
      Check.that(
          operators.get(i) instanceof MutationOperator,
          "The operator list does not contain an object implementing class CrossoverOperator");
      this.operators.add(operators.get(i));
    }
  }

  /* Getters */
  @Override
  public double getMutationProbability() {
    return mutationProbability;
  }

  /** Execute() method */
  @Override
  /*
  TODO: generalizar para que cada operador de la listea se seleccione con la misma
  prioridad
   */

  public DoubleSolution execute(DoubleSolution solution) {
    Check.notNull(solution);

    List<MutationOperator<DoubleSolution>> mutatedSolutionComponents = new ArrayList<>();
    for (int i = 0; i < operators.size(); i++) {
      solution = operators.get(i).execute(solution) ;
    }

    return solution;
  }

  public List<MutationOperator<DoubleSolution>> getOperators() {
    return operators ;
  }
}
