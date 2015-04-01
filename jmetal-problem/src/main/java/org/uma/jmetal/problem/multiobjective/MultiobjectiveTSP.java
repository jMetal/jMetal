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

package org.uma.jmetal.problem.multiobjective;


import org.uma.jmetal.problem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.JMetalException;

import java.io.*;
import java.util.List;

/**
 * Class representing a multi-objective TSP (Traveling Salesman Problem) problem.
 * This class is tested with two objectives and the KROA150 and KROB150 
 * instances of TSPLIB
 */
public class MultiobjectiveTSP extends AbstractIntegerPermutationProblem {
  private int         numberOfCities ;
  private double [][] distanceMatrix ;
  private double [][] costMatrix;

  /**
   * Creates a new MultiobjectiveTSP problem instance. It accepts data files from TSPLIB
   */
  public MultiobjectiveTSP(String distanceFile, String costFile) throws IOException {
    setNumberOfVariables(1);
    setNumberOfObjectives(2);
    setName("MultiobjectiveTSP");

    distanceMatrix = readProblem(distanceFile) ;
    costMatrix     = readProblem(costFile);
  }

  /** Evaluate() method */
  public void evaluate(PermutationSolution<List<Integer>> solution){
    double fitness1   ;
    double fitness2   ;

    fitness1   = 0.0 ;
    fitness2   = 0.0 ;

    for (int i = 0; i < (numberOfCities - 1); i++) {
      int x ;
      int y ;

      x = solution.getVariableValue(0).get(i) ;
      y = solution.getVariableValue(0).get(i+1) ;

      fitness1 += distanceMatrix[x][y] ;
      fitness2 += costMatrix[x][y];
    }
    int firstCity ;
    int lastCity  ;

    firstCity = solution.getVariableValue(0).get(0) ;
    lastCity = solution.getVariableValue(0).get(numberOfCities - 1) ;

    fitness1 += distanceMatrix[firstCity][lastCity] ;
    fitness2 += costMatrix[firstCity][lastCity];

    solution.setObjective(0, fitness1);
    solution.setObjective(1, fitness2);
  }

  public double [][] readProblem(String file) throws
      IOException {
    double [][] matrix = null;
    Reader inputFile = new BufferedReader(
        new InputStreamReader(
            new FileInputStream(file)));

    StreamTokenizer token = new StreamTokenizer(inputFile);
    try {
      boolean found ;
      found = false ;

      token.nextToken();
      while(!found) {
        if ((token.sval != null) && ((token.sval.compareTo("DIMENSION") == 0)))
          found = true ;
        else
          token.nextToken() ;
      }

      token.nextToken() ;
      token.nextToken() ;

      numberOfCities =  (int)token.nval ;

      matrix = new double[numberOfCities][numberOfCities] ;

      // Find the string SECTION  
      found = false ;
      token.nextToken();
      while(!found) {
        if ((token.sval != null) &&
            ((token.sval.compareTo("SECTION") == 0)))
          found = true ;
        else
          token.nextToken() ;
      }

      double [] c = new double[2*numberOfCities] ;

      for (int i = 0; i < numberOfCities; i++) {
        token.nextToken() ;
        int j = (int)token.nval ;

        token.nextToken() ;
        c[2*(j-1)] = token.nval ;
        token.nextToken() ;
        c[2*(j-1)+1] = token.nval ;
      } // for

      double dist ;
      for (int k = 0; k < numberOfCities; k++) {
        matrix[k][k] = 0;
        for (int j = k + 1; j < numberOfCities; j++) {
          dist = Math.sqrt(Math.pow((c[k*2]-c[j*2]),2.0) +
              Math.pow((c[k*2+1]-c[j*2+1]), 2));
          dist = (int)(dist + .5);
          matrix[k][j] = dist;
          matrix[j][k] = dist;
        }
      }
    }     catch (Exception e) {
      new JMetalException("TSP.readProblem(): error when reading data file " + e);
    }
    return matrix;
  }

  @Override public int getPermutationLength(int index) {
    if (index != 0) {
      new JMetalException("Index value " + index + " is not 0") ;
    }
    return numberOfCities;
  }
}
