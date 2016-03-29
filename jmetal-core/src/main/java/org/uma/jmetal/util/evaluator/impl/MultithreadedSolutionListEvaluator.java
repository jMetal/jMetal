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

package org.uma.jmetal.util.evaluator.impl;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.parallel.impl.MultithreadedEvaluator;

import java.util.List;

/**
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class MultithreadedSolutionListEvaluator<S extends Solution<?>> implements SolutionListEvaluator<S> {
  private MultithreadedEvaluator<S> evaluator;
  private Problem<S> problem;
  private int numberOfThreads ;

  public MultithreadedSolutionListEvaluator(int numberOfThreads, Problem<S> problem) {
  	this.numberOfThreads = numberOfThreads ;
    evaluator = new MultithreadedEvaluator<S>(numberOfThreads)  ;
    this.problem = problem ;
    evaluator.start(problem) ;
  }

  @Override
  public List<S> evaluate(List<S> SolutionList, Problem<S> problem) {
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
