//  AlgorithmRunningTime.java
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

package org.uma.jmetal.util;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by Antonio J. Nebro on 08/06/14.
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
    Algorithm<List<Solution>> algorithm ;
    long computingTime;

    public Executor(Algorithm<List<Solution>> algorithm) {
      this.algorithm = algorithm ;
    }

    public AlgorithmRunner execute() {
      long initTime = System.currentTimeMillis();
      Thread thread = new Thread(algorithm) ;
      //algorithm.run() ;
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

