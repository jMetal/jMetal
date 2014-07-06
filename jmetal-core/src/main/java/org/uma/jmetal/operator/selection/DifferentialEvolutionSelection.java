//  DifferentialEvolutionSelection.java
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

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.HashMap;

/**
 * Class implementing the selection operator used in DE: three different solutions
 * are returned from a population.
 */
public class DifferentialEvolutionSelection extends Selection {

  /**
   *
   */
  private static final long serialVersionUID = 2866073033079299561L;

  /**
   * Constructor
   */
  @Deprecated
  DifferentialEvolutionSelection(HashMap<String, Object> parameters) {
    super(parameters);
  }

  /** Constructor */
  DifferentialEvolutionSelection(Builder builder) {
    super(new HashMap<String, Object>()) ;
  }

  /** execute() method  */
  public Object execute(Object object) throws JMetalException {
    if (null == object) {
      throw new JMetalException("Parameter is null") ;
    } else if (!(object instanceof Object[])) {
      throw new JMetalException("Invalid parameter class") ;
    } else if (((Object[])object).length != 2) {
      throw new JMetalException("An array of size 2 is required") ;
    } else if (!(((Object[])object)[0] instanceof SolutionSet)) {
      throw new JMetalException("Parameter 0 must be of class SolutionSet") ;
    } else if (!(((Object[])object)[1] instanceof Integer)) {
      throw new JMetalException("Parameter 0 must be of class Integer") ;
    }
    
    Object[] parameters = (Object[]) object;
    SolutionSet population = (SolutionSet) parameters[0];
    int index = (Integer) parameters[1];

    Solution[] parents = new Solution[3];
    int r1, r2, r3;

    if (population.size() < 4) {
      throw new JMetalException(
        "DifferentialEvolutionSelection: the population has less than four solutions");
    }

    do {
      r1 = PseudoRandom.randInt(0, population.size() - 1);
    } while (r1 == index);
    do {
      r2 = PseudoRandom.randInt(0, population.size() - 1);
    } while (r2 == index || r2 == r1);
    do {
      r3 = PseudoRandom.randInt(0, population.size() - 1);
    } while (r3 == index || r3 == r1 || r3 == r2);

    parents[0] = population.get(r1);
    parents[1] = population.get(r2);
    parents[2] = population.get(r3);

    return parents;
  }

  /** Builder class */
  public static class Builder {

    public Builder() {
    }

    public DifferentialEvolutionSelection build() {
      return new DifferentialEvolutionSelection(this) ;
    }
  }
}
