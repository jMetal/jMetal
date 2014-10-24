//  Settings.java
//
//  Authors:
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

package org.uma.jmetal.experiment;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.util.JMetalException;

import java.util.Properties;

/**
 * Class representing Settings objects.
 */
public abstract class Settings {
  protected Problem problem;
  protected String problemName;
  protected String paretoFrontFile;

  /** Constructor */
  public Settings() {
  }

  /** Constructor */
  public Settings(String problemName) throws JMetalException {
    this.problemName = problemName;
  }

  /* Getters */
  public Problem getProblem() {
    return problem ;
  }

  public String getProblemName() {
    return problemName ;
  }

  /** Configure() method */
  abstract public Algorithm configure() throws JMetalException;

  /** Configure() method based on reading a properties file */
  abstract public Algorithm configure(Properties configuration) throws JMetalException;
}
