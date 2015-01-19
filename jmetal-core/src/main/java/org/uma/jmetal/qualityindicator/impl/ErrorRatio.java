//  ErrorRatio.java
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

package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.imp.FrontUtils;

import java.util.List;

/**
 * The Error Ratio (ER) quality indicator reports the number of vectors in PFknown that are not members of PFtrue.
 */
public class ErrorRatio implements QualityIndicator {
  private static String NAME ;

  /** Constructor */
  public ErrorRatio() {
    NAME = "ER" ;
  }

  @Override
  public double execute(Front paretoFrontApproximation, Front trueParetoFront) {
    if (paretoFrontApproximation == null) {
      throw new JMetalException("The pareto front approximation object is null") ;
    } else if (trueParetoFront == null) {
      throw new JMetalException("The pareto front object is null");
    }

    return er(FrontUtils.convertFrontToArray(paretoFrontApproximation),
        FrontUtils.convertFrontToArray(trueParetoFront)) ;
  }

  @Override
  public double execute(List<? extends Solution> paretoFrontApproximation,
      List<? extends Solution> trueParetoFront) {

    if (paretoFrontApproximation == null) {
      throw new JMetalException("The pareto front approximation object is null") ;
    } else if (trueParetoFront == null) {
      throw new JMetalException("The pareto front object is null");
    }

    return this.execute(new ArrayFront(paretoFrontApproximation), new ArrayFront(trueParetoFront)) ;
  }

  /**
   * Returns the value of the error ratio indicator.
   *
   * @param front Solution front
   * @param referenceFront True Pareto front
   *
   * @return the value of the error ratio indicator
   * @throws org.uma.jmetal.util.JMetalException
   */
  public double er(double[][] front, double[][] referenceFront) throws JMetalException {
    int num_objectives = referenceFront[0].length ;
    double total_sum = 0;
    for (int i = 0; i < front.length; i++) {
      double [] current_know = front[i];
      boolean is_there = false;
      for (int j = 0; j < referenceFront.length; j++) {
        double [] current_true = referenceFront[j];
        boolean flag = true;
        for (int k = 0; k < num_objectives; k++) {
          if(current_know[k] != current_true[k]){
            flag = false;
            break;
          }
        }
        if(flag){
          is_there = flag;
          break;
        }
      }
      if(!is_there){
        total_sum++;
      }
    }

    double error = total_sum / front.length;
    return error;
  }


  @Override public String getName() {
    return NAME ;
  }
}
