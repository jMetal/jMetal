//  AlgorithmRunningTime.java
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

import jmetal.core.Algorithm;
import jmetal.core.SolutionSet;

import java.io.IOException;

/**
 * Created by Antonio J. Nebro on 08/06/14.
 */
public class AlgorithmRunner {
  private SolutionSet solutionSet_ ;
  private long computingTime_ ;

  private AlgorithmRunner(Executor execute) {
    solutionSet_ = execute.solutionSet_ ;
    computingTime_ = execute.computingTime_ ;
  }

  public SolutionSet getSolutionSet() {
    return solutionSet_;
  }

  public long getComputingTime() {
    return computingTime_ ;
  }
  /*
  public RunAlgorithm(Algorithm algorithm) throws IOException, ClassNotFoundException {
    long initTime = System.currentTimeMillis();
    solutionSet_ = algorithm.execute() ;
    computingTime_ = System.currentTimeMillis() - initTime ;
  }

  public long getComputingTime() {
    return computingTime_ ;
  }

  public SolutionSet getSolutionSet() {
    return solutionSet_ ;
  }

  */
  public static class Executor {
    Algorithm algorithm_ ;
    long computingTime_ ;
    SolutionSet solutionSet_ ;

    public Executor(Algorithm algorithm) {
      algorithm_ = algorithm ;
    }

    public AlgorithmRunner execute() throws IOException, ClassNotFoundException {
      long initTime = System.currentTimeMillis();
      solutionSet_ = algorithm_.execute() ;
      computingTime_ = System.currentTimeMillis() - initTime ;

      return new AlgorithmRunner(this) ;
    }
  }
}

