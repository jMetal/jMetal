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

package jmetal.problems.singleObjective;

import jmetal.core.*;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.PermutationSolutionType;
import jmetal.encodings.variable.Binary;
import jmetal.encodings.variable.Permutation;

import java.io.* ;

/**
 * Class representing a TSP (Traveling Salesman Problem) problem.
 */
public class TSP extends Problem {

  public int         numberOfCities_ ; 
  public double [][] distanceMatrix_ ;

  
 /**
  * Creates a new TSP problem instance. It accepts data files from TSPLIB
  * @param filename The file containing the definition of the problem
  */
  public TSP(String filename) throws FileNotFoundException, IOException, ClassNotFoundException  {
    numberOfVariables_  = 1;
    numberOfObjectives_ = 1;
    numberOfConstraints_= 0;
    problemName_        = "TSP";
             
    solutionType_ = new PermutationSolutionType(this) ; 
    
    //variableType_ = new Class[numberOfVariables_] ;
    length_       = new int[numberOfVariables_];
    
    //variableType_[0] = Class.forName("jmetal.base.variable.Permutation") ;
    
    readProblem(filename) ;
    System.out.println(numberOfCities_) ;
    length_      [0] = numberOfCities_ ;
  } // TSP
    
 /** 
  * Evaluates a solution 
  * @param solution The solution to evaluate
  */      
  public void evaluate(Solution solution) {
    double fitness   ;
    
    fitness   = 0.0 ;

    for (int i = 0; i < (numberOfCities_ - 1); i++) {
      int x ; 
      int y ;
      
      x = ((Permutation)solution.getDecisionVariables()[0]).vector_[i] ;
      y = ((Permutation)solution.getDecisionVariables()[0]).vector_[i+1] ;
//  cout << "I : " << i << ", x = " << x << ", y = " << y << endl ;    
      fitness += distanceMatrix_[x][y] ;
    } // for
    int firstCity ;
    int lastCity  ;
    
    firstCity = ((Permutation)solution.getDecisionVariables()[0]).vector_[0] ;
    lastCity  = ((Permutation)solution.getDecisionVariables()[0]).vector_[numberOfCities_ - 1] ;
    fitness += distanceMatrix_[firstCity][lastCity] ;
    
    solution.setObjective(0, fitness);            
  } // evaluate


  public void readProblem(String fileName) throws FileNotFoundException,
                                                  IOException {
    Reader inputFile = new BufferedReader(
                       new InputStreamReader(
                       new FileInputStream(fileName)));

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
      } // while

      token.nextToken() ;
      token.nextToken() ;
      
      numberOfCities_ =  (int)token.nval ;

      distanceMatrix_ = new double[numberOfCities_][numberOfCities_] ;

      // Find the string SECTION  
      found = false ;
      token.nextToken();
      while(!found) {
        if ((token.sval != null) &&
           ((token.sval.compareTo("SECTION") == 0)))
          found = true ;
        else
          token.nextToken() ;
      } // while

    // Read the data

      double [] c = new double[2*numberOfCities_] ;

      for (int i = 0; i < numberOfCities_; i++) {
        token.nextToken() ;
        int j = (int)token.nval ;

        token.nextToken() ;
        c[2*(j-1)] = token.nval ;
        token.nextToken() ;
        c[2*(j-1)+1] = token.nval ;
      } // for

      double dist ;
      for (int k = 0; k < numberOfCities_; k++) {
        distanceMatrix_[k][k] = 0;
        for (int j = k + 1; j < numberOfCities_; j++) {
          dist = Math.sqrt(Math.pow((c[k*2]-c[j*2]),2.0) +
              Math.pow((c[k*2+1]-c[j*2+1]), 2));
          dist = (int)(dist + .5);
          distanceMatrix_[k][j] = dist;
          distanceMatrix_[j][k] = dist;
        } // for
      } // for
    } // try
    catch (Exception e) { 
      System.err.println ("TSP.readProblem(): error when reading data file "+e);
      System.exit(1);
    } // catch
  } // readProblem


} // TSP
