//  Real.java
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

package org.uma.jmetal.encoding.variable;

import org.uma.jmetal.core.Variable;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.logging.Level;


/**
 * This class implements a Real value decision variable
 */
public class Real implements GenericRealVariable {
	private static final long serialVersionUID = -227984897558565037L;

	private double value;
	private double lowerBound;
	private double upperBound;

	public Real() {
	}

	public Real(double lowerBound, double upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		value = PseudoRandom.randDouble() * (upperBound - lowerBound) + lowerBound;
	}

	public Real(double value, double lowerBound, double upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.value = value;
	}

	public Real(Real variable) throws JMetalException {
		lowerBound = variable.getLowerBound();
		upperBound = variable.getUpperBound();
		value = variable.getValue();
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

  public Variable copy() {
    return new Real(this);
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}

	public String toString() {
		return value + "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(lowerBound);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(upperBound);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Real)) {
			return false;
		}
		Real other = (Real) obj;
		if (Double.doubleToLongBits(lowerBound) != Double
				.doubleToLongBits(other.lowerBound)) {
			return false;
		}
		if (Double.doubleToLongBits(upperBound) != Double
				.doubleToLongBits(other.upperBound)) {
			return false;
		}
		if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value)) {
			return false;
		}
		return true;
	}

}
