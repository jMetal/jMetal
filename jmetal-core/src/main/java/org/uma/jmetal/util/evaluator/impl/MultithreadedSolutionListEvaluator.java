//  MultithreadedSolutionSetEvaluator.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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
//

package org.uma.jmetal.util.evaluator.impl;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.parallel.impl.MultithreadedEvaluator;

import java.util.List;

/**
 * Created by Antonio J. Nebro on 30/05/14.
 */
public class MultithreadedSolutionListEvaluator<S extends Solution> implements SolutionListEvaluator<S> {
  private MultithreadedEvaluator evaluator;
  private Problem problem;
  private int numberOfThreads ;

  public MultithreadedSolutionListEvaluator(int numberOfThreads, Problem problem) {
  	this.numberOfThreads = numberOfThreads ;
    evaluator = new MultithreadedEvaluator(numberOfThreads)  ;
    this.problem = problem ;
    evaluator.start(problem) ;
  }

  @Override
  public List<S> evaluate(List<S> SolutionList, Problem problem) {
    for (int i = 0 ; i < SolutionList.size(); i++) {
      evaluator.addTask(new Object[] {SolutionList.get(i)});
    }
    evaluator.parallelExecution() ;

    return SolutionList;
  }

  public int getNumberOfThreads() {
  	return numberOfThreads ;
  }
  
  @Override public void shutdown() {
    evaluator.stop();
  }

}
