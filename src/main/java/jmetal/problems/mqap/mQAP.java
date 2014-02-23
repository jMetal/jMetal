//  mQAP.java
//
//  Author:
//       Juan J. Durillo <juan@dps.uibk.ac.at>
//
//  Copyright (c) 2011 Juan J. Durillo
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

package jmetal.problems.mqap;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.PermutationSolutionType;
import jmetal.encodings.variable.Permutation;
import jmetal.util.JMException;

/**
 *  @author Juan J. Durillo
 *  @version 1.0
 *  This class implements the mQAP problem.
 *  Please notice that this class is also valid for the case m = 1 (mono-objective
 *  version of the problem)
 */
public class mQAP extends Problem {
  
  int [][] a_matrix;
  int [][][] b_matrixs;

  public mQAP(String solutionType) {
    this(solutionType, "KC10-2fl-2rl.dat") ;
  }

  /**
  * Creates a new instance of problem mQAP.
  * @param fileName: name of the file containing the instance  
  */
  public mQAP(String solutionType, String fileName)  {

    ReadInstance ri = new ReadInstance(fileName);    
    ri.loadInstance(); // necessary step (because I say it :-))
    numberOfVariables_  =   1; // the permutation
    numberOfObjectives_ =   ri.getNumberOfObjectives();
    numberOfConstraints_=   0;
    problemName_        =   "mQAP";
    a_matrix = ri.get_a_Matrix();
    b_matrixs = ri.get_b_Matrixs();

    upperLimit_ = new double[numberOfVariables_];
    lowerLimit_ = new double[numberOfVariables_];

    // Establishes upper and lower limits for the variables
    for (int var = 0; var < numberOfVariables_; var++)
    {
      lowerLimit_[var] = 0.0;
      upperLimit_[var] = ri.getNumberOfFacilities()-1;
    } // for
    
    // Establishes the length of every encodings.variable
    length_ = new int[numberOfVariables_];
    for (int var = 0; var < numberOfVariables_; var++)
    {
      length_[var] = ri.getNumberOfFacilities();
      
    } // for
    if (solutionType.compareTo("Permutation") == 0)
      solutionType_ = new PermutationSolutionType(this);
    else
      try {
        throw new JMException("SolutionType must be Permutation") ;
      } catch (JMException e) {
        e.printStackTrace();
      }
  } // mQAP
  
  
  // evaluation of the problem
  public void evaluate(Solution solution) throws JMException {
    int [] permutation = ((Permutation)solution.getDecisionVariables()[0]).vector_;
    for (int k = 0; k < numberOfObjectives_; k++) {      
      double aux = 0.0;
      for (int i = 0; i < a_matrix.length; i++) {        
        for (int j = 0; j < a_matrix[i].length; j++) {
          aux += a_matrix[i][j] * b_matrixs[k][permutation[i]][permutation[j]];
        }
      }
      solution.setObjective(k, aux);
    }
  } // evaluate
} // mQAP
