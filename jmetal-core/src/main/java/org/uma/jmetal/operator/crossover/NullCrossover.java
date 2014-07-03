//  NullCrossover.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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

package org.uma.jmetal.operator.crossover;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.util.JMetalException;

/** This class is intended to perform no crossover */
public class NullCrossover extends Crossover {
  private NullCrossover(Builder builder) {
  }


  /** Executes the operation */
  public Object execute(Object object) throws JMetalException {
    Solution[] parents = (Solution[]) object;

    Solution[] offspring = new Solution[parents.length]  ;
    for (int i = 0 ; i < parents.length; i++) {
      offspring[i] = new Solution(parents[i]) ;
    }

    return offspring;
  }

  /** Builder class */
  public static class Builder {

    public Builder() {
    }

    public NullCrossover build() {
      return new NullCrossover(this) ;
    }
  }
}
