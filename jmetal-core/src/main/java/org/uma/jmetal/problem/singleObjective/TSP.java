//  TSP.java
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

package org.uma.jmetal.problem.singleObjective;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.PermutationSolutionType;
import org.uma.jmetal.encoding.variable.Permutation;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;

import java.io.*;
import java.util.logging.Level;

/**
 * Class representing a TSP (Traveling Salesman Problem) problem.
 */
public class TSP extends Problem {

  /**
   *
   */
  private static final long serialVersionUID = 7271160768259648914L;
  public int numberOfCities_;
  public double[][] distanceMatrix_;

  public TSP(String solutionType) throws Exception {
    this(solutionType, "eil101.tsp");
  }

  /**
   * Creates a new TSP problem instance. It accepts data files from TSPLIB
   *
   * @param filename The file containing the definition of the problem
   */
  public TSP(String solutionType, String filename) throws Exception {
    numberOfVariables = 1;
    numberOfObjectives = 1;
    numberOfConstraints = 0;
    problemName = "TSP";

    this.solutionType = new PermutationSolutionType(this);

    length = new int[numberOfVariables];

    try {
      if (solutionType.compareTo("Permutation") == 0) {
        this.solutionType = new PermutationSolutionType(this);
      } else {
        throw new JMetalException("Solution type invalid");
      }
    } catch (JMetalException e) {
      Configuration.logger_.log(Level.SEVERE, "Error", e);
    }
    readProblem(filename);

    Configuration.logger_.info(""+numberOfCities_);
    length[0] = numberOfCities_;
  }

  /**
   * Evaluates a solutiontype
   *
   * @param solution The solutiontype to evaluate
   */
  public void evaluate(Solution solution) {
    double fitness;

    fitness = 0.0;

    for (int i = 0; i < (numberOfCities_ - 1); i++) {
      int x;
      int y;

      x = ((Permutation) solution.getDecisionVariables()[0]).getVector()[i];
      y = ((Permutation) solution.getDecisionVariables()[0]).getVector()[i + 1];

      fitness += distanceMatrix_[x][y];
    }
    int firstCity;
    int lastCity;

    firstCity = ((Permutation) solution.getDecisionVariables()[0]).getVector()[0];
    lastCity = ((Permutation) solution.getDecisionVariables()[0]).getVector()[numberOfCities_ - 1];
    fitness += distanceMatrix_[firstCity][lastCity];

    solution.setObjective(0, fitness);
  }


  public void readProblem(String fileName) throws
    Exception {
    Reader inputFile = new BufferedReader(
      new InputStreamReader(
        new FileInputStream(fileName))
    );

    StreamTokenizer token = new StreamTokenizer(inputFile);
    try {
      boolean found;
      found = false;

      token.nextToken();
      while (!found) {
        if ((token.sval != null) && ((token.sval.compareTo("DIMENSION") == 0))) {
          found = true;
        } else {
          token.nextToken();
        }
      }

      token.nextToken();
      token.nextToken();

      numberOfCities_ = (int) token.nval;

      distanceMatrix_ = new double[numberOfCities_][numberOfCities_];

      // Find the string SECTION  
      found = false;
      token.nextToken();
      while (!found) {
        if ((token.sval != null) &&
          ((token.sval.compareTo("SECTION") == 0))) {
          found = true;
        } else {
          token.nextToken();
        }
      }

      // Read the data
      double[] c = new double[2 * numberOfCities_];

      for (int i = 0; i < numberOfCities_; i++) {
        token.nextToken();
        int j = (int) token.nval;

        token.nextToken();
        c[2 * (j - 1)] = token.nval;
        token.nextToken();
        c[2 * (j - 1) + 1] = token.nval;
      } // for

      double dist;
      for (int k = 0; k < numberOfCities_; k++) {
        distanceMatrix_[k][k] = 0;
        for (int j = k + 1; j < numberOfCities_; j++) {
          dist = Math.sqrt(Math.pow((c[k * 2] - c[j * 2]), 2.0) +
            Math.pow((c[k * 2 + 1] - c[j * 2 + 1]), 2));
          dist = (int) (dist + .5);
          distanceMatrix_[k][j] = dist;
          distanceMatrix_[j][k] = dist;
        }
      }
    } catch (Exception e) {
      Configuration.logger_.log(Level.SEVERE, "TSP.readProblem(): error when reading data file", e);
      throw new Exception("TSP.readProblem(): error when reading data file " + e);
    }
  }
}
