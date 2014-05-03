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

package jmetal.operators.selection;

import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.util.HashMap;

/**
 * Class implementing a factory of selection operators
 */
public class SelectionFactory {
    
  /**
   * Gets a selection operator through its name.
   * @param name of the operator
   * @return the operator
   * @throws JMException 
   */
  public static Selection getSelectionOperator(String name, HashMap parameters) throws JMException {
    if (name.equalsIgnoreCase("BinaryTournament"))
      return new BinaryTournament(parameters);
    else if (name.equalsIgnoreCase("BinaryTournament2"))
      return new BinaryTournament2(parameters);
    else if (name.equalsIgnoreCase("PESA2Selection"))
      return new PESA2Selection(parameters);
    else if (name.equalsIgnoreCase("RandomSelection"))
      return new RandomSelection(parameters);    
    else if (name.equalsIgnoreCase("RankingAndCrowdingSelection"))
      return new RankingAndCrowdingSelection(parameters);
    else if (name.equalsIgnoreCase("DifferentialEvolutionSelection"))
      return new DifferentialEvolutionSelection(parameters);
//    else if (name.equalsIgnoreCase("MatingSelection"))
//        return new MatingSelection(parameters);
//    else if (name.equalsIgnoreCase("EnviromentalSelection"))
//        return new EnviromentalSelection(parameters);
    else {
      Configuration.logger_.severe("Operator '" + name + "' not found ");
      throw new JMException("Exception in " + name + ".getSelectionOperator()") ;
    } // else    
  } // getSelectionOperator
} // SelectionFactory
