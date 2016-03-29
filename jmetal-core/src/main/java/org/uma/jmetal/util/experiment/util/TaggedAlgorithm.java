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

package org.uma.jmetal.util.experiment.util;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;

/**
 * This class is a decorator for {@link Algorithm} objects that will be used in an experimental study.
 * An {@link Algorithm} is decorated with two fiels:
 * - problem: the {@link Problem} to be optimized by the {@link Algorithm}
 * - tag: used to indicate the name of the algorithm in the experiment. By the default it is assigned the
 *        value of {@link Algorithm #getName()}, but it can be set to another value if a same algorithm is configured
 *        with different settings in the experiment.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class TaggedAlgorithm<Result> implements Algorithm<Result> {
  private Algorithm<Result> algorithm ;
  private Problem<?> problem ;
  private String tag ;
  private int runId;

  public TaggedAlgorithm (Algorithm<Result> algorithm, Problem<?> problem, int runId) {
    this(algorithm, algorithm.getName(), problem, runId) ;
  }

  public TaggedAlgorithm (Algorithm<Result> algorithm, String tag, Problem<?> problem, int runId) {
    this.algorithm = algorithm ;
    this.tag = tag ;
    this.problem = problem ;
    this.runId = runId ;
  }

  @Override
  public void run() {
    algorithm.run();
  }

  @Override
  public Result getResult() {
    return algorithm.getResult() ;
  }

  @Override
  public String getName() {
    return algorithm.getName();
  }

  @Override
  public String getDescription() {
    return algorithm.getDescription();
  }

  public String getTag() {
    return tag ;
  }

  public int getRunId() {
    return runId;
  }

  public void setTag(String tag) {
    this.tag = tag ;
  }

  public Problem<?> getProblem() {
    return problem ;
  }

  public void setProblem(Problem<?> problem) {
    this.problem = problem ;
  }
}
