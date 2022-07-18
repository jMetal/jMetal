package org.uma.jmetal.solution.compositesolution;

import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;

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
  public CompositeSolution(@NotNull List<Solution<?>> solutions) {
    super(solutions.size(), solutions.get(0).objectives().length, solutions.get(0).constraints().length);
    Check.notNull(solutions);
    Check.collectionIsNotEmpty(solutions);
    var numberOfObjectives = solutions.get(0).objectives().length;
    var numberOfConstraints = solutions.get(0).constraints().length;
    for (var solution : solutions) {
      Check.that(
          solution.objectives().length == numberOfObjectives,
          "The solutions in the list must have the same number of objectives: "
              + numberOfObjectives);
      Check.that(
              solution.constraints().length == numberOfConstraints,
              "The solutions in the list must have the same number of constraints: "
                      + numberOfConstraints);
    }

    for (var i = 0; i < solutions.size(); i++) {
      variables().set(i, solutions.get(i)) ;
    }
  }

  /**
   * Copy constructor
   * @param solution
   */
  public CompositeSolution(CompositeSolution solution) {
    super(solution.variables().size(), solution.objectives().length, solution.constraints().length) ;

    var bound2 = solution.variables().size();
    for (var i2 = 0; i2 < bound2; i2++) {
      variables().set(i2, solution.variables().get(i2).copy());
    }
    var bound1 = solution.objectives().length;
    for (var i1 = 0; i1 < bound1; i1++) {
      objectives()[i1] = solution.objectives()[i1];
    }
    var bound = solution.constraints().length;
    for (var i = 0; i < bound; i++) {
      constraints()[i] = solution.constraints()[i];
    }

    attributes = new HashMap<Object, Object>(solution.attributes) ;
  }

  @Override
  public Solution<Solution<?>> copy() {
    return new CompositeSolution(this);
  }
}
