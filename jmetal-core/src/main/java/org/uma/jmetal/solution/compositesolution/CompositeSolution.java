package org.uma.jmetal.solution.compositesolution;

import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.checking.Check;

import java.util.HashMap;
import java.util.List;

/**
 * Class representing solutions composed of a list of solutions. The idea is that each decision
 * variable can be a solution of any type, so we can create mixed solutions (e.g., solutions
 * combining any of the existing encodings).
 *
 * The adopted approach has the advantage of easing the reuse of existing variation operators,
 * but all the solutions in the list will need to have the same function and constraint violation
 * values.
 *
 * It is assumed that problems using instances of this class will properly manage the solutions it contains.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class CompositeSolution extends AbstractSolution<Solution<?>> {
  /**
   * Constructor.
   * @param solutions Collection of solutions composing the composite solution. All of them have to have the same
   *                  number of objectives and constraints.
   */
  public CompositeSolution(List<Solution<?>> solutions) {
    super(solutions.size(), solutions.get(0).getNumberOfObjectives(), solutions.get(0).getNumberOfConstraints());
    Check.isNotNull(solutions);
    Check.collectionIsNotEmpty(solutions);
    int numberOfObjectives = solutions.get(0).getNumberOfObjectives();
    int numberOfConstraints = solutions.get(0).getNumberOfConstraints();
    for (Solution<?> solution : solutions) {
      Check.that(
          solution.getNumberOfObjectives() == numberOfObjectives,
          "The solutions in the list must have the same number of objectives: "
              + numberOfObjectives);
      Check.that(
              solution.getNumberOfConstraints() == numberOfConstraints,
              "The solutions in the list must have the same number of constraints: "
                      + numberOfConstraints);
    }

    for (int i = 0 ; i < solutions.size(); i++) {
      setVariable(i, solutions.get(i)) ;
    }
  }

  /**
   * Copy constructor
   * @param solution
   */
  public CompositeSolution(CompositeSolution solution) {
    super(solution.getNumberOfVariables(), solution.getNumberOfObjectives(), solution.getNumberOfConstraints()) ;

    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      setVariable(i, solution.getVariable(i).copy());
    }

    for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
      setObjective(i, solution.getObjective(i)) ;
    }

    for (int i = 0; i < solution.getNumberOfConstraints(); i++) {
      setConstraint(i, solution.getConstraint(i));
    }

    attributes = new HashMap<Object, Object>(solution.attributes) ;
  }

  @Override
  public Solution<Solution<?>> copy() {
    return new CompositeSolution(this);
  }
}
