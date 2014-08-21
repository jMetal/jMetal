//  WorstSolutionSelection.java
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

package org.uma.jmetal.operator.selection;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;

import java.util.HashMap;

/**
 * Class implementing a factory of selection operator
 */
public class SelectionFactory {

  /**
   * Gets a selection operator through its name.
   *
   * @param name of the operator
   * @return the operator
   * @throws org.uma.jmetal.util.JMetalException
   */
  public static Selection getSelectionOperator(String name, HashMap<String, Object> parameters)
    throws JMetalException {
    if ("BinaryTournament".equalsIgnoreCase(name)) {
      return new BinaryTournament(parameters);
    } else if ("BinaryTournament2".equalsIgnoreCase(name)) {
      return new BinaryTournament2(parameters);
    } else if ("PESA2Selection".equalsIgnoreCase(name)) {
      return new PESA2Selection(parameters);
    } else if ("RandomSelection".equalsIgnoreCase(name)) {
      return new RandomSelection(parameters);
    } else if ("RankingAndCrowdingSelection".equalsIgnoreCase(name)) {
      return new RankingAndCrowdingSelection(parameters);
    } else if ("DifferentialEvolutionSelection".equalsIgnoreCase(name)) {
      return new DifferentialEvolutionSelection(parameters);
    } else {
      JMetalLogger.logger.severe("Operator '" + name + "' not found ");
      throw new JMetalException("Exception in " + name + ".getSelectionOperator()");
    }
  }
}
