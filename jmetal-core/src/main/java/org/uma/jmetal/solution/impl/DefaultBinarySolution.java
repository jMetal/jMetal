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

package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.binarySet.BinarySet;

import java.util.HashMap;

/**
 * Defines an implementation of a binary solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultBinarySolution
    extends AbstractGenericSolution<BinarySet, BinaryProblem>
    implements BinarySolution {

  /** Constructor */
  public DefaultBinarySolution(BinaryProblem problem) {
    super(problem) ;

    initializeBinaryVariables();
    initializeObjectiveValues();
  }

  /** Copy constructor */
  public DefaultBinarySolution(DefaultBinarySolution solution) {
    super(solution.problem);

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      setVariableValue(i, (BinarySet) solution.getVariableValue(i).clone());
    }

    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      setObjective(i, solution.getObjective(i)) ;
    }

    //overallConstraintViolationDegree = solution.overallConstraintViolationDegree ;
    //numberOfViolatedConstraints = solution.numberOfViolatedConstraints ;

    attributes = new HashMap<Object, Object>(solution.attributes) ;
  }

  private BinarySet createNewBitSet(int numberOfBits) {
    BinarySet bitSet = new BinarySet(numberOfBits) ;

    for (int i = 0; i < numberOfBits; i++) {
      double rnd = randomGenerator.nextDouble() ;
      if (rnd < 0.5) {
        bitSet.set(i);
      } else {
        bitSet.clear(i);
      }
    }
    return bitSet ;
  }

  @Override
  public int getNumberOfBits(int index) {
    return getVariableValue(index).getBinarySetLength() ;
  }

  @Override
  public DefaultBinarySolution copy() {
    return new DefaultBinarySolution(this);
  }

  @Override
  public int getTotalNumberOfBits() {
    int sum = 0 ;
    for (int i = 0; i < getNumberOfVariables(); i++) {
      sum += getVariableValue(i).getBinarySetLength() ;
    }

    return sum ;
  }

  @Override
  public String getVariableValueString(int index) {
    String result = "" ;
    for (int i = 0; i < getVariableValue(index).getBinarySetLength() ; i++) {
      if (getVariableValue(index).get(i)) {
        result += "1" ;
      }
      else {
        result+= "0" ;
      }
    }
    return result ;
  }
  
  private void initializeBinaryVariables() {
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      setVariableValue(i, createNewBitSet(problem.getNumberOfBits(i)));
    }
  }
}
