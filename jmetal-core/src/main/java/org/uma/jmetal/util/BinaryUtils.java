//  BinaryUtils.java
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

package org.uma.jmetal.util;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.Variable;
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.BinarySolutionType;
import org.uma.jmetal.encoding.variable.Binary;

/**
 * Created by Antonio J. Nebro on 08/08/14.
 *
 * Static class with utilities functions to work with binary solutions
 */
public class BinaryUtils {
  /**
   * Returns the number of bits of a Solution in case of using a binary representation
   */
  public static int getNumberOfBits(Solution solution) {
    int bits = 0;

    if ((solution.getType().getClass().equals(BinarySolutionType.class)) ||
        (solution.getType().getClass().equals(BinaryRealSolutionType.class))) {
      for (Variable variable : solution.getDecisionVariables()) {
        bits += ((Binary) (variable)).getNumberOfBits();
      }
    } else {
      throw new JMetalException("Invalid Solution Type: " + solution.getType()) ;
    }

    return bits ;
  }
}
