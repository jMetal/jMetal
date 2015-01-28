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

package org.uma.jmetal.algorithm.multiobjective.paes;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.solutionattribute.AlgorithmBuilder;

/**
 * Created by ajnebro on 17/11/14.
 */
public class PAESBuilder implements AlgorithmBuilder {
  public Problem problem;

  public int archiveSize;
  public int maxEvaluations;
  public int biSections;

  public MutationOperator mutationOperator;

  public PAESBuilder(Problem problem) {
    this.problem = problem;
  }

  public PAESBuilder setArchiveSize(int archiveSize) {
    this.archiveSize = archiveSize;

    return this;
  }

  public PAESBuilder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public PAESBuilder setBiSections(int biSections) {
    this.biSections = biSections;

    return this;
  }

  public PAESBuilder setMutationOperator(MutationOperator mutation) {
    mutationOperator = mutation;

    return this;
  }

  public PAES build() {
    return new PAES(this);
  }
}
