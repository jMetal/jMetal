package org.uma.jmetal.problem.multiobjective.multiobjectivetsp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.problem.permutationproblem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class representing a bi-objective TSP (Traveling Salesman Problem) problem.
 * It accepts data files from TSPLIB:
 *   http://www.iwr.uni-heidelberg.de/groups/comopt/software/TSPLIB95/tsp/
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class BiObjectiveTSP extends AbstractIntegerPermutationProblem {
  protected int         numberOfCities ;
  protected double [][] distanceMatrix ;
  protected double [][] costMatrix;

  /**
   * Creates a new BiObjectiveTSP problem instance
   */
  public BiObjectiveTSP(String distanceFile, String costFile) throws IOException {
    distanceMatrix = readProblem(distanceFile) ;
    costMatrix     = readProblem(costFile);
  }

  @Override
  public int numberOfVariables() {
    return numberOfCities ;
  }

  @Override
  public int numberOfObjectives() {
    return 2;
  }

  @Override
  public int numberOfConstraints() {
    return 0;
  }

  @Override
  public String name() {
    return "MultiobjectiveTSP";
  }

  /** Evaluate() method */
  public PermutationSolution<Integer> evaluate(PermutationSolution<Integer> solution){
    double fitness1   ;
    double fitness2   ;

    fitness1 = 0.0 ;
    fitness2 = 0.0 ;

    for (int i = 0; i < (numberOfCities - 1); i++) {
      int x ;
      int y ;

      x = solution.variables().get(i) ;
      y = solution.variables().get(i+1) ;

      fitness1 += distanceMatrix[x][y] ;
      fitness2 += costMatrix[x][y];
    }
    int firstCity ;
    int lastCity  ;

    firstCity = solution.variables().get(0) ;
    lastCity = solution.variables().get(numberOfCities - 1) ;

    fitness1 += distanceMatrix[firstCity][lastCity] ;
    fitness2 += costMatrix[firstCity][lastCity];

    solution.objectives()[0] = fitness1 ;
    solution.objectives()[1] = fitness2 ;

    return solution ;
  }

  private double [][] readProblem(String file) throws IOException {
    double [][] matrix = null;

    InputStream in = getClass().getResourceAsStream(file);
    if (in == null) {
      in = new FileInputStream(file) ;
    }
    InputStreamReader isr = new InputStreamReader(in);
    BufferedReader br = new BufferedReader(isr);

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
    } catch (Exception e) {
      throw new JMetalException("TSP.readProblem(): error when reading data file " + e);
    }
    return matrix;
  }

  @Override public int length() {
    return numberOfCities ;
  }
}