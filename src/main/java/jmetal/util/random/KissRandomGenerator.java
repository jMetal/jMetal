//  KissRandomGenerator.java
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

package jmetal.util.random;

import jmetal.experiments.Main;

import java.util.Random;

/**
 * Created by Antonio J. Nebro on 22/03/14.
 */
public class KissRandomGenerator implements IRandomGenerator {
  private long kiss_x; //= 1;
  private long kiss_y; //= 2;
  private long kiss_z; //= 4;
  private long kiss_w; //= 8;
  private long kiss_carry = 0;
  private long kiss_k;
  private long kiss_m;

  private long seed_ ;

  private static final long RAND_MAX_KISS = Long.MAX_VALUE ; //4294967295

  public KissRandomGenerator() {
    seed_ = (new Random(System.nanoTime())).nextLong();
    seed_rand_kiss(seed_);
  }

  public KissRandomGenerator(long seed) {
    seed_rand_kiss(seed);
  }

  @Override
  public int nextInt(int upperLimit) {
    double rand = (upperLimit + 1) * nextDouble() ;

    int result = (int)Math.floor(rand) ;
    if (result > upperLimit)
      result = upperLimit ;

    return result;
  }

  @Override
  public double nextDouble() {
    return (double)rand_kiss() / RAND_MAX_KISS;
  }

  void seed_rand_kiss(long seed)
  {
    kiss_x = seed | 1;
    kiss_y = seed | 2;
    kiss_z = seed | 4;
    kiss_w = seed | 8;
    kiss_carry = 0;
  }

  private long rand_kiss()
  {
    kiss_x = kiss_x * 69069 + 1;
    kiss_y ^= kiss_y << 13;
    kiss_y ^= kiss_y >> 17;
    kiss_y ^= kiss_y << 5;
    kiss_k = (kiss_z >> 2) + (kiss_w >> 3) + (kiss_carry >> 2);
    kiss_m = kiss_w + kiss_w + kiss_z + kiss_carry;
    kiss_z = kiss_w;
    kiss_w = kiss_m;
    kiss_carry = kiss_k >> 30;
    return kiss_x + kiss_y + kiss_w;
  }
}
