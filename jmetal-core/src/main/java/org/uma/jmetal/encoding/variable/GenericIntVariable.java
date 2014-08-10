//  IntVariable.java
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

package org.uma.jmetal.encoding.variable;

import org.uma.jmetal.core.Variable;

/**
 * Created by Antonio J. Nebro on 07/08/14.
 *
 * Interface representing integer encoded variables
 */
public interface GenericIntVariable extends Variable {
  public abstract int getValue() ;
  public abstract void setValue(int value) ;
  public abstract int getLowerBound() ;
  public abstract void setLowerBound(int lowerBound) ;
  public abstract int getUpperBound() ;
  public abstract void setUpperBound(int upperBound);
}
