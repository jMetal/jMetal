package org.uma.jmetal.algorithm.multiobjective.abyss;

import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;

import java.util.Comparator;


/**
 * This class implements an local search operator based in the use of a
 * mutation operator. An archive is used to store the non-dominated solutions
 * found during the search.
 */
public class AbYSSLocalSearch2<S extends Solution<?>> implements LocalSearchOperator<S>{
  private Problem<S> problem;
  private Archive<S> archive;
  private int improvementRounds ;
  private Comparator constraintComparator ;
  private Comparator dominanceComparator ;

  private MutationOperator mutationOperator;
  private int evaluations ;

  /**
   * Constructor.
   * Creates a new local search object.
   * @param improvementRounds number of iterations
   * @param mutationOperator mutation operator
   * @param archive archive to store non-dominanted solution
   * @param problem problem to resolve

   */
  public AbYSSLocalSearch2(int improvementRounds, MutationOperator<S> mutationOperator,
      Archive<S> archive, Problem<S> problem){
    this.problem=problem;
    this.mutationOperator=mutationOperator;
    this.improvementRounds=improvementRounds;
    this.archive=archive;
    dominanceComparator  = new DominanceComparator();
    constraintComparator = new OverallConstraintViolationComparator();

    evaluations=0;
  }


  /**
   * Executes the local search. The maximum number of iterations is given by
   * the param "improvementRounds", which is in the parameter list of the
   * operator. The archive to store the non-dominated solutions is also in the
   * parameter list.
   * @param  solution representing a solution
   * @return An object containing the new improved solution
   */
  public S execute(S solution) {
    int i = 0;
    int best ;
    evaluations = 0;

    int rounds = improvementRounds;

    while (i < rounds) {
      S mutatedSolution = (S) mutationOperator.execute(solution.copy());
      if (problem.getNumberOfConstraints() > 0) {

        ((ConstrainedProblem) problem).evaluateConstraints(mutatedSolution);
        best = constraintComparator.compare(mutatedSolution, solution);
        if (best == 0) //none of then is better that the other one
        {
          problem.evaluate(mutatedSolution);
          evaluations++;
          best = dominanceComparator.compare(mutatedSolution, solution);
        } else if (best == -1) //mutatedSolution is best
        {
          problem.evaluate(mutatedSolution);
          evaluations++;
        }
      } else {
        problem.evaluate(mutatedSolution);
        evaluations++;
        best = dominanceComparator.compare(mutatedSolution, solution);
      }
      if (best == -1)
        solution = mutatedSolution;
      else if (best == 1)
        //delete mutatedSolution
        ;
      else {
        archive.add(mutatedSolution);
      }
      i++ ;
    }
    return (S) solution.copy();
  }

  /**
   * Returns the number of evaluations
   */
  public int getEvaluations() {
    return evaluations;
  }
}
