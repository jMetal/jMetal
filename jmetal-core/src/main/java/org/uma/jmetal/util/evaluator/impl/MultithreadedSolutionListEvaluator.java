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

import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.List;

/**
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class MultithreadedSolutionListEvaluator<S> implements SolutionListEvaluator<S> {
  private int numberOfThreads ;

  public MultithreadedSolutionListEvaluator(int numberOfThreads, Problem<S> problem) {
    if (numberOfThreads == 0) {
      this.numberOfThreads = Runtime.getRuntime().availableProcessors();
    } else {
      this.numberOfThreads = numberOfThreads;
      System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism",
              "" + this.numberOfThreads);
    }
    JMetalLogger.logger.info("Number of cores: " + numberOfThreads);
  }

  @Override
  public List<S> evaluate(List<S> solutionList, Problem<S> problem) {
      if (problem instanceof ConstrainedProblem) {
        solutionList.parallelStream().forEach(s -> {
          problem.evaluate(s);
          ((ConstrainedProblem<S>) problem).evaluateConstraints(s);
        });
      } else {
        solutionList.parallelStream().forEach(s -> problem.evaluate(s));
      }

    return solutionList;
  }

  public int getNumberOfThreads() {
  	return numberOfThreads ;
  }
  
  @Override public void shutdown() {
    ;
  }

}
