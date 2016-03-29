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
import org.uma.jmetal.util.naming.impl.SimpleDescribedEntity;
import org.uma.jmetal.util.point.Point;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * The Error Ratio (ER) quality indicator reports the ratio of solutions in a front of points
 * that are not members of the true Pareto front.
 *
 * NOTE: the indicator merely checks if the solutions in the front are not members of the
 * second front. No assumption is made about the second front is a true Pareto front, i.e,
 * the front could contain solutions that dominate some of those of the supposed Pareto front.
 * It is a responsibility of the caller to ensure that this does not happen.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * TODO: using an epsilon value
 */
@SuppressWarnings("serial")
public class ErrorRatio<Evaluate extends List<? extends Solution<?>>>
    extends SimpleDescribedEntity
    implements QualityIndicator<Evaluate, Double> {
  private Front referenceParetoFront ;

  /**
   * Constructor
   *
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public ErrorRatio(String referenceParetoFrontFile) throws FileNotFoundException {
    super("ER", "Error ratio quality indicator") ;
    if (referenceParetoFrontFile == null) {
      throw new JMetalException("The pareto front object is null");
    }

    Front front = new ArrayFront(referenceParetoFrontFile);
    referenceParetoFront = front ;
  }

  /**
   * Constructor
   *
   * @param referenceParetoFront
   */
  public ErrorRatio(Front referenceParetoFront) {
    super("ER", "Error ratio quality indicator") ;
    if (referenceParetoFront == null) {
      throw new JMetalException("\"The Pareto front approximation is null");
    }

    this.referenceParetoFront = referenceParetoFront ;
  }

  /**
   * Evaluate() method
   * @param solutionList
   * @return
   */
  @Override public Double evaluate(Evaluate solutionList) {
    if (solutionList == null) {
      throw new JMetalException("The solution list is null") ;
    }
    return er(new ArrayFront(solutionList), referenceParetoFront);
  }

  /**
   * Returns the value of the error ratio indicator.
   *
   * @param front Solution front
   * @param referenceFront True Pareto front
   *
   * @return the value of the error ratio indicator
   * @throws JMetalException
   */
  private double er(Front front, Front referenceFront) throws JMetalException {
    int numberOfObjectives = referenceFront.getPointDimensions() ;
    double sum = 0;

    for (int i = 0; i < front.getNumberOfPoints(); i++) {
      Point currentPoint = front.getPoint(i);
      boolean thePointIsInTheParetoFront = false;
      for (int j = 0; j < referenceFront.getNumberOfPoints(); j++) {
        Point currentParetoFrontPoint = referenceFront.getPoint(j);
        boolean found = true;
        for (int k = 0; k < numberOfObjectives; k++) {
          if(currentPoint.getDimensionValue(k) != currentParetoFrontPoint.getDimensionValue(k)){
            found = false;
            break;
          }
        }
        if(found){
          thePointIsInTheParetoFront = true;
          break;
        }
      }
      if(!thePointIsInTheParetoFront){
        sum++;
      }
    }

    return sum / front.getNumberOfPoints();
  }

  @Override public String getName() {
    return super.getName() ;
  }
}
