//  PermutationSolutionType.java
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

package org.uma.jmetal.encoding.solutiontype;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionType;
import org.uma.jmetal.core.Variable;
import org.uma.jmetal.encoding.variable.Permutation;

/**
 * Class representing the solutiontype type of solutions composed of Permutation
 * variables
 */
public class PermutationSolutionType extends SolutionType {

  /** Constructor */
  public PermutationSolutionType(Problem problem) {
    super(problem);
  }

  /** Creates the variables of the solution type */
  public Variable[] createVariables() {
    Variable[] variables = new Variable[getProblem().getNumberOfVariables()];

    for (int var = 0; var < getProblem().getNumberOfVariables(); var++) {
      variables[var] = new Permutation(getProblem().getLength(var));
    }

    return variables;
  } 
} 
