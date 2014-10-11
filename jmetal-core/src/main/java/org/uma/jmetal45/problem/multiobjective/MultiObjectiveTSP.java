//  MOTSP.java
//
//  Author:
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

package org.uma.jmetal45.problem.multiobjective;

import org.uma.jmetal45.core.Problem;
import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.encoding.solutiontype.PermutationSolutionType;
import org.uma.jmetal45.encoding.variable.Permutation;
import org.uma.jmetal45.util.JMetalException;
import org.uma.jmetal45.util.JMetalLogger;

import java.io.*;
import java.util.logging.Level;

/**
 * Class representing a multi-objective TSP (Traveling Salesman Problem) problem.
 * This class is tested with two objectives and the KROA150 and KROB150
 * instances of TSPLIB
 */
public class MultiObjectiveTSP extends Problem {
  private static final long serialVersionUID = 3869748855198680149L;

  private int numberOfCities;
  private double[][] distanceMatrix;
  private double[][] costMatrix;

  /**
   * Creates a new multiobjective TSP problem instance. It accepts data files from TSPLIB
   */
  public MultiObjectiveTSP(String solutionType, String fileDistances, String fileCost) throws FileNotFoundException {
    numberOfVariables = 1;
    numberOfObjectives = 2;
    numberOfConstraints = 0;
    problemName = "mTSP";

    length = new int[numberOfVariables];

    distanceMatrix = readProblem(fileDistances);
    costMatrix = readProblem(fileCost);
    JMetalLogger.logger.info(""+ numberOfCities);
    length[0] = numberOfCities;

    if (solutionType.compareTo("Permutation") == 0) {
      this.solutionType = new PermutationSolutionType(this);
    } else {
      throw new JMetalException("Error: solution type " + solutionType + " invalid");
    }
  }

  /** Evaluate() method */
  public void evaluate(Solution solution) {
    double fitness1;
    double fitness2;

    fitness1 = 0.0;
    fitness2 = 0.0;

    for (int i = 0; i < (numberOfCities - 1); i++) {
      int x;
      int y;

      x = ((Permutation) solution.getDecisionVariables()[0]).getVector()[i];
      y = ((Permutation) solution.getDecisionVariables()[0]).getVector()[i + 1];
      fitness1 += distanceMatrix[x][y];
      fitness2 += costMatrix[x][y];
    }
    int firstCity;
    int lastCity;

    firstCity = ((Permutation) solution.getDecisionVariables()[0]).getVector()[0];
    lastCity = ((Permutation) solution.getDecisionVariables()[0]).getVector()[numberOfCities - 1];
    fitness1 += distanceMatrix[firstCity][lastCity];
    fitness2 += costMatrix[firstCity][lastCity];

    solution.setObjective(0, fitness1);
    solution.setObjective(1, fitness2);
  }


  private double[][] readProblem(String file) throws FileNotFoundException {
    double[][] matrix = null;
    Reader inputFile = new BufferedReader(
      new InputStreamReader(
        new FileInputStream(file))
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

      numberOfCities = (int) token.nval;

      matrix = new double[numberOfCities][numberOfCities];

      // Find the string SECTION  
      found = false;
      token.nextToken();
      while (!found) {
        if ((token.sval != null) && (token.sval.compareTo("SECTION") == 0)) {
          found = true;
        } else {
          token.nextToken();
        }
      }

      // Read the data
      double[] c = new double[2 * numberOfCities];

      for (int i = 0; i < numberOfCities; i++) {
        token.nextToken();
        int j = (int) token.nval;

        token.nextToken();
        c[2 * (j - 1)] = token.nval;
        token.nextToken();
        c[2 * (j - 1) + 1] = token.nval;
      }

      double dist;
      for (int k = 0; k < numberOfCities; k++) {
        matrix[k][k] = 0;
        for (int j = k + 1; j < numberOfCities; j++) {
          dist = Math.sqrt(Math.pow(c[k * 2] - c[j * 2], 2.0) + Math.pow(c[k * 2 + 1] - c[j * 2 + 1], 2));
          dist = (int) (dist + .5);
          matrix[k][j] = dist;
          matrix[j][k] = dist;
        }
      }
    } catch (Exception e) {
      JMetalLogger.logger
        .log(Level.SEVERE, "mTSP.readProblem(): error when reading data file", e);
      throw new JMetalException("mTSP.readProblem(): error when reading data file " + e);
    }
    return matrix;
  }
}
