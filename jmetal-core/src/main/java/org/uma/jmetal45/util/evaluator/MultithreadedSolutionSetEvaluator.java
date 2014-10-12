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

package org.uma.jmetal45.util.evaluator;

import org.uma.jmetal45.core.Problem;
import org.uma.jmetal45.core.SolutionSet;
import org.uma.jmetal45.util.parallel.MultithreadedEvaluator;

/**
 * Created by Antonio J. Nebro on 30/05/14.
 */
public class MultithreadedSolutionSetEvaluator implements SolutionSetEvaluator {
  private MultithreadedEvaluator evaluator;
  private Problem problem;
  private int numberOfThreads ;

  public MultithreadedSolutionSetEvaluator(int numberOfThreads, Problem problem) {
  	this.numberOfThreads = numberOfThreads ;
    evaluator = new MultithreadedEvaluator(numberOfThreads)  ;
    this.problem = problem ;
    evaluator.start(problem) ;
  }

  @Override
  public SolutionSet evaluate(SolutionSet solutionSet, Problem problem) {
    for (int i = 0 ; i < solutionSet.size(); i++) {
      evaluator.addTask(new Object[] {solutionSet.get(i)});
    }
    evaluator.parallelExecution() ;

    return solutionSet;
  }

  public int getNumberOfThreads() {
  	return numberOfThreads ;
  }
  
  @Override public void shutdown() {
    evaluator.stop();
  }

}
