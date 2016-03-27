//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.operator.impl.localsearch;

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
 * This class implements a local search operator based in the use of a
 * mutation operator. An archive is used to store the non-dominated solutions
 * found during the search.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class ArchiveMutationLocalSearch<S extends Solution<?>> implements LocalSearchOperator<S>{
  private Problem<S> problem;
  private Archive<S> archive;
  private int improvementRounds ;
  private Comparator<S> constraintComparator ;
  private Comparator<S> dominanceComparator ;

  private MutationOperator<S> mutationOperator;
  private int evaluations ;

  private int numberOfImprovements ;
  private int numberOfNonComparableSolutions ;
  /**
   * Constructor.
   * Creates a new local search object.
   * @param improvementRounds number of iterations
   * @param mutationOperator mutation operator
   * @param archive archive to store non-dominated solution
   * @param problem problem to resolve

   */
  public ArchiveMutationLocalSearch(int improvementRounds, MutationOperator<S> mutationOperator,
      Archive<S> archive, Problem<S> problem){
    this.problem=problem;
    this.mutationOperator=mutationOperator;
    this.improvementRounds=improvementRounds;
    this.archive=archive;
    dominanceComparator  = new DominanceComparator<S>();
    constraintComparator = new OverallConstraintViolationComparator<S>();

    numberOfImprovements = 0 ;
    numberOfNonComparableSolutions = 0 ;
  }

  /**
   * Executes the local search.
   *
   * @param  solution The solution to improve
   * @return The improved solution
   */
  @SuppressWarnings("unchecked")
  public S execute(S solution) {
    int i = 0;
    int best ;
    evaluations = 0;
    numberOfNonComparableSolutions = 0 ;

    int rounds = improvementRounds;

    while (i < rounds) {
      S mutatedSolution = mutationOperator.execute((S) solution.copy());
      if (problem.getNumberOfConstraints() > 0) {

        ((ConstrainedProblem<S>) problem).evaluateConstraints(mutatedSolution);
        best = constraintComparator.compare(mutatedSolution, solution);
        if (best == 0)
        {
          problem.evaluate(mutatedSolution);
          evaluations++;
          best = dominanceComparator.compare(mutatedSolution, solution);
        } else if (best == -1) {
          problem.evaluate(mutatedSolution);
          evaluations++;
        }
      } else {
        problem.evaluate(mutatedSolution);
        evaluations++;
        best = dominanceComparator.compare(mutatedSolution, solution);
      }
      if (best == -1) {
        solution = mutatedSolution;
        numberOfImprovements ++ ;
      }
      else if (best == 1) {
        ;
      }
      else {
        numberOfNonComparableSolutions ++ ;
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

  @Override public int getNumberOfImprovements() {
    return numberOfImprovements ;
  }

  @Override public int getNumberOfNonComparableSolutions() {
    return numberOfNonComparableSolutions ;
  }
}
