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

package org.uma.jmetal.util.pseudorandom.impl;

import org.apache.commons.math3.random.MersenneTwister;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

/**
 * @author Antonio J. Nebro
 * @version 0.1
 */
public class MersenneTwisterGenerator implements PseudoRandomGenerator {
  private MersenneTwister rnd ;
  private long seed ;
  private static final String name = "MersenneTwister" ;

  /** Constructor */
  public MersenneTwisterGenerator() {
    seed = System.currentTimeMillis() ;
    rnd = new MersenneTwister(seed) ;
  }

  /** Constructor */
  public MersenneTwisterGenerator(long seed) {
    this.seed = seed ;
    rnd = new MersenneTwister(seed) ;
  }

  @Override
  public long getSeed() {
    return seed ;
  }

  @Override
  public int nextInt(int lowerBound, int upperBound) {
    return lowerBound + rnd.nextInt((upperBound - lowerBound) + 1) ;
  }

  @Override
  public double nextDouble(double lowerBound, double upperBound) {
    return lowerBound + rnd.nextDouble()*(upperBound - lowerBound) ;
  }

  @Override public double nextDouble() {
    return nextDouble(0.0, 1.0);
  }

  @Override
  public void setSeed(long seed) {
    this.seed = seed ;
    rnd.setSeed(seed);
  }

  @Override
  public String getName() {
    return name ;
  }
}
