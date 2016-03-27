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

package org.uma.jmetal.util.pseudorandom;

import org.uma.jmetal.util.pseudorandom.impl.JavaRandomGenerator;

import java.io.Serializable;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class JMetalRandom implements Serializable {
  private static JMetalRandom instance ;
  private PseudoRandomGenerator randomGenerator ;

  private JMetalRandom() {
    randomGenerator = new JavaRandomGenerator() ;
  }

  public static JMetalRandom getInstance() {
    if (instance == null) {
      instance = new JMetalRandom() ;
    }
    return instance ;
  }

  public void setRandomGenerator(PseudoRandomGenerator randomGenerator) {
    this.randomGenerator = randomGenerator;
  }

  public PseudoRandomGenerator getRanndomGenerator() {
    return randomGenerator ;
  }

  public int nextInt(int lowerBound, int upperBound) {
    return randomGenerator.nextInt(lowerBound, upperBound) ;
  }

  public double nextDouble() {
    return randomGenerator.nextDouble() ;
  }

  public double nextDouble(double lowerBound, double upperBound) {
    return randomGenerator.nextDouble(lowerBound, upperBound) ;
  }

  public void setSeed(long seed) {
    randomGenerator.setSeed(seed);
  }

  public long getSeed() {
    return randomGenerator.getSeed() ;
  }

  public String getGeneratorName() {
    return randomGenerator.getName() ;
  }
}
