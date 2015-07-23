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

package org.uma.jmetal.util;

import org.uma.jmetal.algorithm.Algorithm;

/**
 * Class for running algorithms in a concurrent thread
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class AlgorithmRunner {
  private long computingTime;

  /** Constructor */
  private AlgorithmRunner(Executor execute) {
    computingTime = execute.computingTime;
  }

  /* Getters */
  public long getComputingTime() {
    return computingTime;
  }

  /** Executor class */
  public static class Executor {
    Algorithm<?> algorithm ;
    long computingTime;

    public Executor(Algorithm<?> algorithm) {
      this.algorithm = algorithm ;
    }

    public AlgorithmRunner execute() {
      long initTime = System.currentTimeMillis();
      Thread thread = new Thread(algorithm) ;
      thread.start();
      try {
        thread.join();
      } catch (InterruptedException e) {
        throw new JMetalException("Error in thread.join()", e) ;
      }
      computingTime = System.currentTimeMillis() - initTime ;

      return new AlgorithmRunner(this) ;
    }
  }
}

