//  SequentialEvaluator.java
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
//


package org.uma.jmetal.util.evaluator;

import java.util.logging.Level;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;

/**
 * Created by Antonio J. Nebro on 30/05/14.
 */
public class SequentialSolutionSetEvaluator implements SolutionSetEvaluator {
//  @Override
//    public void startup(Object parameters) {
//  }


  @Override
  public SolutionSet evaluate(SolutionSet solutionSet, Problem problem) throws JMetalException {
    try {
      for (int i = 0 ; i < solutionSet.size(); i++) {
        problem.evaluate(solutionSet.get(i)) ;
        problem.evaluateConstraints(solutionSet.get(i)) ;
      }
    } catch (JMetalException e) {
      Configuration.logger_.log(Level.SEVERE, "Error evaluating solutiontype", e);
      throw new JMetalException("Error in SequentialSolutionSetEvaluatior.evaluate()") ;
    }

    return solutionSet;
  }

 @Override public void shutdown() {
 }
}
