//  MutationFactory.java
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

package org.uma.jmetal.operator.mutation;

import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.JMetalException;

import java.util.HashMap;

/**
 * Class implementing a factory for Mutation objects.
 */
public class MutationFactory {

  /**
   * Gets a crossover operator through its name.
   *
   * @param name of the operator
   * @return the operator
   * @throws org.uma.jmetal.util.JMetalException
   */
  public static Mutation getMutationOperator(String name, HashMap<String, Object> parameters)
    throws JMetalException {

    if ("PolynomialMutation".equalsIgnoreCase(name)) {
      return new PolynomialMutation(parameters);
    } else if ("BitFlipMutation".equalsIgnoreCase(name)) {
      return new BitFlipMutation(parameters);
    } else if ("NonUniformMutation".equalsIgnoreCase(name)) {
      return new NonUniformMutation(parameters);
    } else if ("UniformMutation".equalsIgnoreCase(name)) {
      return new UniformMutation(parameters);
    } else if ("SwapMutation".equalsIgnoreCase(name)) {
      return new SwapMutation(parameters);
    } else {
      JMetalLogger.logger.severe("Operator '" + name + "' not found ");
      Class<String> cls = java.lang.String.class;
      String name2 = cls.getName();
      throw new JMetalException("Exception in " + name2 + ".getMutationOperator()");
    }
  }
}
