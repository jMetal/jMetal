//  ProblemFactory.java
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

package org.uma.jmetal.problem;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

/**
 * This class represents a factory for problem
 */
public class ProblemFactory {
  /**
   * Creates an object representing a problem
   *
   * @param name   Name of the problem
   * @param params Parameters characterizing the problem
   * @return The object representing the problem
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Problem getProblem(String name, Object[] params) throws JMetalException {
    // Params are the arguments
    // The number of argument must correspond with the problem constructor params
    Problem result = null ;

    String base = "org.uma.jmetal.problem.";
    if ("TSP".equals(name) || "OneMax".equals(name)) {
      base += "singleobjective.";
    } else if ("MultiObjectiveQAP".equals(name)) {
      base += "multiobjective.mqap.";
    } else if ("dtlz".equalsIgnoreCase(name.substring(0, name.length() - 1))) {
      base += "multiobjective.dtlz.";
    } else if ("wfg".equalsIgnoreCase(name.substring(0, name.length() - 1))) {
      base += "multiobjective.wfg.";
    } else if ("UF".equalsIgnoreCase(name.substring(0, name.length() - 1))) {
      base += "multiobjective.cec2009competition.";
    } else if ("UF".equalsIgnoreCase(name.substring(0, name.length() - 2))) {
      base += "multiobjective.cec2009competition.";
    } else if ("zdt".equalsIgnoreCase(name.substring(0, name.length() - 1))) {
      base += "multiobjective.zdt.";
    } else if ("lz09".equalsIgnoreCase(name.substring(0, name.length() - 2))) {
      base += "multiobjective.lz09.";
    } else {
      base += "multiobjective." ;
    }

    Class<?> problemClass ;

    try {
      problemClass = Class.forName(base + name);
    } catch (ClassNotFoundException e) {
      throw new JMetalException("ProblemFactory.getProblem: " + "Problem '" + name + "' does not exist. " +
              "Please, check the problem names in org.uma.jmetal.problem. ", e);
    }

    try {
      Constructor<?>[] constructors;
      constructors = problemClass.getConstructors();

      int i = 0;
      //find the constructor
      while ((i < constructors.length) &&
              (constructors[i].getParameterTypes().length != params.length)) {
        i++;
      }
      // constructors[i] is the selected one constructor
      result = (Problem)constructors[i].newInstance(params);
    } catch (InvocationTargetException e) {
      throw new JMetalException("ProblemFactory.getProblem: wrong parameters", e) ;
    } catch (InstantiationException e) {
      throw new JMetalException(e) ;
    } catch (IllegalAccessException e) {
      throw new JMetalException(e) ;
    }

    return result ;
  }
}
/*
catch(Exception e) {
        throw new JMetalException("ProblemFactory.getProblem: " + "Problem '" + name + "' does not exist. " +
        "Please, check the problem names in org.uma.jmetal.problem. ", e);
        }
        */