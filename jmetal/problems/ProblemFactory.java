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

package jmetal.problems;

import java.lang.reflect.Constructor;
import java.util.Properties;

import jmetal.core.Problem;
import jmetal.util.Configuration;
import jmetal.util.JMException;

/**
 * This class represents a factory for problems
 */
public class ProblemFactory {
  /**
   * Creates an object representing a problem
   * @param name Name of the problem
   * @param params Parameters characterizing the problem
   * @return The object representing the problem
   * @throws JMException 
   */
  public Problem getProblem(String name, Object [] params) throws JMException {
    // Params are the arguments
    // The number of argument must correspond with the problem constructor params
    
    String base = "jmetal.problems.";
    if (name.substring(0,name.length()-1).equalsIgnoreCase("DTLZ"))
      base += "DTLZ.";
    else if (name.substring(0,name.length()-1).equalsIgnoreCase("WFG"))
      base += "WFG.";
    else if (name.substring(0,name.length()-1).equalsIgnoreCase("ZDT"))
      base += "ZDT.";    
    else if (name.substring(0,name.length()-3).equalsIgnoreCase("ZZJ07"))
      base += "ZZJ07.";        
    else if (name.substring(0,name.length()-3).equalsIgnoreCase("LZ09"))
      base += "LZ09.";        
    else if (name.substring(0,name.length()-4).equalsIgnoreCase("ZZJ07"))
        base += "ZZJ07.";    
    else if (name.substring(0,name.length()-3).equalsIgnoreCase("LZ06"))
      base += "LZ06.";    
    else if (name.substring(0,name.length()-4).equalsIgnoreCase("CEC2009"))
      base += "cec2009Competition.";    
    else if (name.substring(0,name.length()-5).equalsIgnoreCase("CEC2009"))
      base += "cec2009Competition.";    
    
    try {
      Class problemClass = Class.forName(base+name);
      Constructor [] constructors = problemClass.getConstructors();
      int i = 0;
      //find the constructor
      while ((i < constructors.length) && 
             (constructors[i].getParameterTypes().length!=params.length)) {
        i++;
      }
      // constructors[i] is the selected one constructor
      Problem problem = (Problem)constructors[i].newInstance(params);
      return problem;      
    }// try
    catch(Exception e) {
      Configuration.logger_.severe("ProblemFactory.getProblem: " +
          "Problem '"+ name + "' does not exist. "  +
          "Please, check the problem names in jmetal/problems") ;
      throw new JMException("Exception in " + name + ".getProblem()") ;
    } // catch            
  }    
  
  public Problem getProblem(String name, Properties params) throws JMException {
    // Params are the arguments
    // The number of argument must correspond with the problem constructor params

    String base = "jmetal.gui.problems.";
    if (name.substring(0,name.length()-1).startsWith("DTLZ"))
      base += "DTLZ.";
    else if (name.substring(0,name.length()-1).startsWith("WFG"))
      base += "WFG.";
    else if (name.substring(0,name.length()-1).startsWith("ZDT"))
      base += "ZDT.";
    else if (name.substring(0,name.length()-3).startsWith("ZZJ07"))
      base += "ZZJ07.";
    else if (name.substring(0,name.length()-3).startsWith("LZ09"))
      base += "LZ09.";
    else if (name.substring(0,name.length()-4).startsWith("ZZJ07"))
        base += "ZZJ07.";
    else if (name.substring(0,name.length()-3).startsWith("LZ06"))
      base += "LZ06.";
    else if (name.substring(0,name.length()-4).startsWith("CEC2009"))
      base += "cec2009Competition.";
    else if (name.substring(0,name.length()-5).startsWith("CEC2009"))
      base += "cec2009Competition.";

    try {
      Class problemClass = Class.forName(base+name);
      //Constructor constructors = problemClass.getConstructor(Properties.class);
      Constructor constructors = problemClass.getConstructor(Properties.class);

      Problem problem = (Problem)constructors.newInstance(params);
     
      return problem;
    }// try
    catch(Exception e) {
        e.printStackTrace();
      Configuration.logger_.severe("ProblemFactory.getProblem: " +
          "Problem '"+ name + "' does not exist. "  +
          "Please, check the problem names in jmetal/problems") ;
      throw new JMException("Exception in " + name + ".getProblem()") ;
    } // catch
  }

}
