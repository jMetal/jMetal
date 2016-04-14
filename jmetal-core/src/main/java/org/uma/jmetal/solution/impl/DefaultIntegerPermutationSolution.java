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

package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Defines an implementation of solution composed of a permuation of integers
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultIntegerPermutationSolution
    extends AbstractGenericSolution<Integer, PermutationProblem<?>>
    implements PermutationSolution<Integer> {

  /** Constructor */
  public DefaultIntegerPermutationSolution(PermutationProblem<?> problem) {
    super(problem) ;

    List<Integer> randomSequence = new ArrayList<>(problem.getPermutationLength());

    for (int j = 0; j < problem.getPermutationLength(); j++) {
      randomSequence.add(j);
    }

    java.util.Collections.shuffle(randomSequence);

    for (int i = 0; i < getNumberOfVariables(); i++) {
      setVariableValue(i, randomSequence.get(i)) ;
    }
  }

  /** Copy Constructor */
  public DefaultIntegerPermutationSolution(DefaultIntegerPermutationSolution solution) {
    super(solution.problem) ;
    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      setObjective(i, solution.getObjective(i)) ;
    }

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      setVariableValue(i, solution.getVariableValue(i));
    }
    
    attributes = new HashMap<Object, Object>(solution.attributes) ;
  }

  @Override public String getVariableValueString(int index) {
    return getVariableValue(index).toString();
  }

  @Override
  public DefaultIntegerPermutationSolution copy() {
    return new DefaultIntegerPermutationSolution(this);
  }
}
