package org.uma.jmetal.problem.singleobjective;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.permutationproblem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class representing a single-objective TSP (Traveling Salesman Problem) problem.
 * It accepts data files from TSPLIB:
 *   http://www.iwr.uni-heidelberg.de/groups/comopt/software/TSPLIB95/tsp/
 */
@SuppressWarnings("serial")
public class TSP extends AbstractIntegerPermutationProblem {
  private int         numberOfCities ;
  private double [][] distanceMatrix ;

  /**
   * Creates a new TSP problem instance
   */
  public TSP(String distanceFile) throws IOException {
    distanceMatrix = readProblem(distanceFile) ;

    setNumberOfVariables(numberOfCities);
    setNumberOfObjectives(1);
    setName("TSP");
  }

  /** Evaluate() method */
  public @NotNull PermutationSolution<Integer> evaluate(PermutationSolution<Integer> solution){
    double fitness1   ;

    fitness1 = 0.0 ;

    for (int i = 0; i < (numberOfCities - 1); i++) {
      int x ;
      int y ;

      x = solution.variables().get(i) ;
      y = solution.variables().get(i+1) ;

      fitness1 += distanceMatrix[x][y] ;
    }
    int firstCity ;
    int lastCity  ;

    firstCity = solution.variables().get(0) ;
    lastCity = solution.variables().get(numberOfCities - 1) ;

    fitness1 += distanceMatrix[firstCity][lastCity] ;

    solution.objectives()[0] = fitness1;

    return solution ;
  }

  private double [][] readProblem(String file) throws IOException {
    double [][] matrix = null;

    InputStream in = getClass().getResourceAsStream(file);
    if (in == null) {
      in = new FileInputStream(file) ;
    }
    InputStreamReader isr = new InputStreamReader(in);
    @NotNull BufferedReader br = new BufferedReader(isr);

    StreamTokenizer token = new StreamTokenizer(br);
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

      double @NotNull [] c = new double[2*numberOfCities] ;

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
    } catch (Exception e) {
      throw new JMetalException("TSP.readProblem(): error when reading data file " + e);
    }
    return matrix;
  }

  @Override public int getLength() {
    return numberOfCities ;
  }
}
