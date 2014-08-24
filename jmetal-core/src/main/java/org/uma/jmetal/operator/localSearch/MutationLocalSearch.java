//  MutationLocalSearch.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
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

package org.uma.jmetal.operator.localSearch;

import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.OverallConstraintViolationComparator;

import java.util.Comparator;
import java.util.HashMap;

/**
 * This class implements an local search operator based in the use of a
 * mutation operator. An setArchive is used to store the non-dominated solutions
 * found during the search.
 */
public class MutationLocalSearch extends LocalSearch {
  private static final long serialVersionUID = 6152832404856574555L;

  private int evaluations;
  private Problem problem;
  private SolutionSet archive;
  private int improvementRounds;

  private Comparator<Solution> constraintComparator;
  private Comparator<Solution> dominanceComparator;

  private Operator mutationOperator;

  /**
   * Constructor.
   * Creates a new local search object.
   *
   * @param parameters The parameters
   */
  @Deprecated
  public MutationLocalSearch(HashMap<String, Object> parameters) {
    super(parameters);
    if (parameters.get("problem") != null) {
      problem = (Problem) parameters.get("problem");
    }
    if (parameters.get("setImprovementRounds") != null) {
      improvementRounds = (Integer) parameters.get("setImprovementRounds");
    }
    if (parameters.get("mutation") != null) {
      mutationOperator = (Mutation) parameters.get("mutation");
    }

    evaluations = 0;
    archive = null;
    dominanceComparator = new DominanceComparator();
    constraintComparator = new OverallConstraintViolationComparator();
  }

  /** Constructor */
  private MutationLocalSearch(Builder builder) {
    problem = builder.problem ;
    improvementRounds = builder.improvementRounds ;
    mutationOperator = builder.mutationOperator ;
    archive = builder.archive;

    dominanceComparator = new DominanceComparator();
    constraintComparator = new OverallConstraintViolationComparator();
  }

  /* Getters */
  public Problem getProblem() {
    return problem;
  }

  public SolutionSet getArchive() {
    return archive;
  }

  public int getImprovementRounds() {
    return improvementRounds;
  }

  public Operator getMutationOperator() {
    return mutationOperator;
  }

  public int getEvaluations() {
    return evaluations;
  }

  /** Execute() method */
  public Object execute(Object object) throws JMetalException {
    if (null == object) {
      throw new JMetalException("Null parameter") ;
    } else if (!(object instanceof Solution)) {
      throw new JMetalException("Invalid parameter class") ;
    }

    int i = 0;
    int best = 0;
    evaluations = 0;
    Solution solution = (Solution) object;

    int rounds = improvementRounds;

    if (rounds <= 0) {
      return new Solution(solution);
    }

    do {
      i++;
      Solution mutatedSolution = new Solution(solution);
      mutationOperator.execute(mutatedSolution);

      // Evaluate the getNumberOfConstraints
      if (problem.getNumberOfConstraints() > 0) {
        problem.evaluateConstraints(mutatedSolution);
        best = constraintComparator.compare(mutatedSolution, solution);
        if (best == 0) {
          // none of then is better that the other one
          problem.evaluate(mutatedSolution);
          evaluations++;
          best = dominanceComparator.compare(mutatedSolution, solution);
        } else if (best == -1) {
          // mutatedSolution is best
          problem.evaluate(mutatedSolution);
          evaluations++;
        }
      } else {
        problem.evaluate(mutatedSolution);
        evaluations++;
        best = dominanceComparator.compare(mutatedSolution, solution);
      }
      if (best == -1) {
        // mutated is best
        solution = mutatedSolution;
      } else if (best == 1) {
        // original is best

      } else {
        // mutatedSolution and original are non-dominated
        if (archive != null) {
          archive.add(mutatedSolution);
        }
      }
    }
    while (i < rounds);
    return new Solution(solution);
  }

  /** Builder class */
  public static class Builder {
    private Problem problem;
    private int improvementRounds;
    private Operator mutationOperator;
    private Archive archive ;

    public Builder(Problem problem) {
      this.problem = problem ;
    }

    public Builder setImprovementRounds(int improvementRounds) {
      this.improvementRounds = improvementRounds ;

      return this ;
    }

    public Builder setMutationOperator(Operator mutationOperator) {
      this.mutationOperator = mutationOperator ;

      return this ;
    }

    public Builder setArchive(Archive archive) {
      this.archive = archive ;

      return this ;
    }

    public MutationLocalSearch build() {
      return new MutationLocalSearch(this) ;
    }
  }
}
