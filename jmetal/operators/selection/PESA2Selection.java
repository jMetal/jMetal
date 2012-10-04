//  PESA2Selection.java
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

import java.util.HashMap;

import jmetal.core.*;
import jmetal.util.archive.*;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

/** 
 * This class implements a selection operator as the used in PESA-II 
 * algorithm
 */
public class PESA2Selection extends Selection {      
        
	public PESA2Selection(HashMap<String, Object> parameters) {
		super(parameters) ;
	}
	
  /**
  * Performs the operation
  * @param object Object representing a SolutionSet. This solution set
  * must be an instancen <code>AdaptiveGridArchive</code>
  * @return the selected solution
   * @throws JMException 
  */
  public Object execute(Object object) throws JMException    {
    try {
      AdaptiveGridArchive archive = (AdaptiveGridArchive)object;
      int selected;        
      int hypercube1 = archive.getGrid().randomOccupiedHypercube();
      int hypercube2 = archive.getGrid().randomOccupiedHypercube();                                        
        
      if (hypercube1 != hypercube2){
        if (archive.getGrid().getLocationDensity(hypercube1) < 
            archive.getGrid().getLocationDensity(hypercube2)) {
        
          selected = hypercube1;
        
        } else if (archive.getGrid().getLocationDensity(hypercube2) <
                   archive.getGrid().getLocationDensity(hypercube1)) {
        
          selected = hypercube2;
        } else {
          if (PseudoRandom.randDouble() < 0.5) {
            selected = hypercube2;
          } else {
            selected = hypercube1;
          }
        }
      } else { 
        selected = hypercube1;
      }
      int base = PseudoRandom.randInt(0,archive.size()-1);
      int cnt = 0;
      while (cnt < archive.size()){   
        Solution individual = archive.get((base + cnt)% archive.size());        
        if (archive.getGrid().location(individual) != selected){
          cnt++;                
        } else {
          return individual;
        }
      }        
      return archive.get((base + cnt) % archive.size());
    } catch (ClassCastException e) {
      Configuration.logger_.severe("PESA2Selection.execute: ClassCastException. " +
          "Found" + object.getClass() + "Expected: AdaptativeGridArchive") ;
      Class cls = java.lang.String.class;
      String name = cls.getName(); 
      throw new JMException("Exception in " + name + ".execute()") ;  
    }
  } //execute
} // PESA2Selection
