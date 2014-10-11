//  GenericRealVariable.java
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

package org.uma.jmetal45.encoding.variable;

import org.uma.jmetal45.core.Variable;

/**
 * Created by Antonio J. Nebro on 07/08/14.
 *
 * Interface representing real encoded variables
 */
public interface GenericRealVariable extends Variable {
  public abstract double getValue() ;
  public abstract void setValue(double value) ;
  public abstract double getLowerBound() ;
  public abstract void setLowerBound(double lowerBound) ;
  public abstract double getUpperBound() ;
  public abstract void setUpperBound(double upperBound);
}
