//  JMetalLogger.java
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

package org.uma.jmetal.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * jMetal logger class
 */
public class JMetalLogger implements Serializable {
  private static final long serialVersionUID = 891486004065726989L;

  public static Logger logger = Logger.getLogger("jMetal");
  private static FileHandler fileHandler;

  static {
    try {
      fileHandler = new FileHandler("jMetal.log");
    } catch (IOException e) {
      e.printStackTrace();
    }
    logger.addHandler(fileHandler);
  }

  public static String cec2005SupportDataDirectory =
    "/Users/antelverde/Softw/jMetal/jMetalMV/cec2005CompetitionResources/supportData";
  public static String cec2005Package =
    "org.uma.jmetal.problem.singleobjective.cec2005Competition.originalCode";
}
