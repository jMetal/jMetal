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
import org.uma.jmetal.util.AlgorithmBuilder;

/**
 * This class implements a simple random search algorithm.
 */
public class RandomSearchBuilder implements AlgorithmBuilder {
  private Problem problem ;
  private int maxEvaluations ;

  /* Getter */
  public int getMaxEvaluations() {
    return maxEvaluations;
  }


  public RandomSearchBuilder(Problem problem) {
    this.problem = problem ;
    maxEvaluations = 25000 ;
  }

  public RandomSearchBuilder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations ;

    return this ;
  }

  public Algorithm build() {
    return new RandomSearch(problem, maxEvaluations) ;
  }
} 
