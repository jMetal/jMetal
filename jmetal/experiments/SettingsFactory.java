//  SettingsFactory.java
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

package jmetal.experiments;

import java.lang.reflect.Constructor;

import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.experiments.settings.*;
/**
 * This class represents a factory for Setting object
 */
public class SettingsFactory {
  /**
   * Creates a settings object
   * @param name Name of the algorithm
   * @param params Parameters
   * @return The settings object
   * @throws JMException 
   */
  public Settings getSettingsObject(String algorithmName, Object [] params) 
    throws JMException {    
    String base = "jmetal.experiments.settings." + algorithmName + "_Settings";
    try {
      Class problemClass = Class.forName(base);
      Constructor [] constructors = problemClass.getConstructors();
      int i = 0;
      //find the constructor
      while ((i < constructors.length) && 
             (constructors[i].getParameterTypes().length!=params.length)) {
        i++;
      }
      // constructors[i] is the selected one constructor
      Settings algorithmSettings = (Settings)constructors[i].newInstance(params);
      return algorithmSettings;      
    }// try
    catch(Exception e) {
    	e.printStackTrace() ;
      Configuration.logger_.severe("SettingsFactory.getSettingsObject: " +
          "Settings '"+ base + "' does not exist. "  +
          "Please, check the algorithm name in jmetal/metaheuristics") ;
      throw new JMException("Exception in " + base + ".getSettingsObject()") ;
    } // catch            
  } // getSttingsObject    
 
} // SettingsFactory
