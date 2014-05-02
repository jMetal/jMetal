//  PseudoRandom.java
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

package jmetal.util;

/**
 * Class representing a pseudo-random number generator
 */
public class PseudoRandom  {
    
  /**
   * generator used to obtain the random values
   */
  private static IRandomGenerator random_ = null;
  private static RandomGenerator defaultGenerator_ = new RandomGenerator() ;
               
  /** 
   * Constructor.
   * Creates a new instance of PseudoRandom.
   */
  private PseudoRandom() {
    if (random_ == null){
      //this.random = new java.util.Random((long)seed);
      random_ = new RandomGenerator();
    }
  } // PseudoRandom
    
  public static void setRandomGenerator(IRandomGenerator generator) {
  	random_ = generator ;
  }
  
  /** 
   * Returns a random int value using the Java random generator.
   * @return A random int value.
   */
  public static int randInt() {
    if (random_ == null) {
      random_ = defaultGenerator_ ;
    }
    return random_.nextInt(Integer.MAX_VALUE);
  } // randInt
    
  /** 
   * Returns a random double value using the PseudoRandom generator.
   * Returns A random double value.
   */
  public static double randDouble() {
    if (random_ == null) {
      random_ = defaultGenerator_ ;
    }
    //return random_.rndReal(0.0,1.0);
    return random_.nextDouble();
    //return randomJava.nextDouble();
  } // randDouble
    
  /** 
   * Returns a random int value between a minimum bound and maximum bound using
   * the PseudoRandom generator.
   * @param minBound The minimum bound.
   * @param maxBound The maximum bound.
   * Return A pseudo random int value between minBound and maxBound.
   */
  public static int randInt(int minBound, int maxBound) {
    if (random_ == null) {
      random_ = defaultGenerator_ ;
    }
    return minBound + random_.nextInt(maxBound-minBound);
    //return minBound + randomJava.nextInt(maxBound-minBound+1);
  } // randInt
    
  /** Returns a random double value between a minimum bound and a maximum bound
   * using the PseudoRandom generator.
   * @param minBound The minimum bound.
   * @param maxBound The maximum bound.
   * @return A pseudo random double value between minBound and maxBound
   */
  public static double randDouble(double minBound, double maxBound) {
    if (random_ == null) {
      random_ = defaultGenerator_ ;
    }
    return minBound + random_.nextDouble() * (maxBound-minBound);
    //return minBound + (maxBound - minBound)*randomJava.nextDouble();
  } // randDouble    
} // PseudoRandom
