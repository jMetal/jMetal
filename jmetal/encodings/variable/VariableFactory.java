//  VariableFactory.java
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

package jmetal.encodings.variable;

import jmetal.core.Variable;
import jmetal.encodings.variable.*;
import jmetal.util.Configuration;
import jmetal.util.JMException;

/**
 * This class is intended to be used as a static Factory to obtains variables. 
 */
public class VariableFactory {
    
  /** 
   * Obtains an instance of a <code>Variable</code> given its name.
   * @param name The name of the class from which we want to obtain an instance
   * object
   * @throws JMException 
   */
  public static Variable getVariable(String name) throws JMException{
    Variable variable   = null;
    String baseLocation = "jmetal.base.variable.";
    try {
      Class c = Class.forName(baseLocation + name);
      variable = (Variable) c.newInstance();
      return variable;
    } catch (ClassNotFoundException e1) {
      Configuration.logger_.severe("VariableFactory.getVariable: " +
      "ClassNotFoundException ");
      Class cls = java.lang.String.class;
      String name2 = cls.getName() ;    
      throw new JMException("Exception in " + name2 + ".getVariable()") ;
    } catch (InstantiationException e2) {
      Configuration.logger_.severe("VariableFactory.getVariable: " +
      "InstantiationException ");
      Class cls = java.lang.String.class;
      String name2 = cls.getName() ;    
      throw new JMException("Exception in " + name2 + ".getVariable()") ;
    } catch (IllegalAccessException e3) {
      Configuration.logger_.severe("VariableFactory.getVariable: " +
      "IllegalAccessException ");
      Class cls = java.lang.String.class;
      String name2 = cls.getName() ;    
      throw new JMException("Exception in " + name2 + ".getVariable()") ;
    }
  } // getVariable      
} //VariabeFactory
