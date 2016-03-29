//  RandomSearch.java
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

package org.uma.jmetal.algorithm.multiobjective.randomsearch;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;

import java.util.List;

/**
 * This class implements a simple random search algorithm.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class RandomSearch<S extends Solution<?>> implements Algorithm<List<S>> {
  private Problem<S> problem ;
  private int maxEvaluations ;
  NonDominatedSolutionListArchive<S> nonDominatedArchive ;

  /** Constructor */
  public RandomSearch(Problem<S> problem, int maxEvaluations) {
    this.problem = problem ;
    this.maxEvaluations = maxEvaluations ;
    nonDominatedArchive = new NonDominatedSolutionListArchive<S>();
  }

  /* Getter */
  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  @Override public void run() {
    int evaluations;

    evaluations = 0;

    S newSolution;
    for (int i = 0; i < maxEvaluations; i++) {
      newSolution = problem.createSolution() ;
      problem.evaluate(newSolution);
      evaluations++;
      nonDominatedArchive.add(newSolution);
    }
  }

  @Override public List<S> getResult() {
    return nonDominatedArchive.getSolutionList();
  }

  @Override public String getName() {
    return "RS" ;
  }

  @Override public String getDescription() {
    return "Multi-objective random search algorithm" ;
  }
} 
