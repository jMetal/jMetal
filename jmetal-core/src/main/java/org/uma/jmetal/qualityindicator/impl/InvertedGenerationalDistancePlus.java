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
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.naming.impl.SimpleDescribedEntity;
import org.uma.jmetal.util.point.util.DominanceDistance;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * This class implements the inverted generational distance metric plust (IGD+)
 * Reference: Ishibuchi et al 2015, "A Study on Performance Evaluation Ability of a Modified
 * Inverted Generational Distance Indicator", GECCO 2015
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class InvertedGenerationalDistancePlus<Evaluate extends List<? extends Solution<?>>>
    extends SimpleDescribedEntity
    implements QualityIndicator<Evaluate,Double> {

  private Front referenceParetoFront ;

  /**
   * Constructor
   *
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public InvertedGenerationalDistancePlus(String referenceParetoFrontFile) throws FileNotFoundException {
    super("IGD+", "Inverted generational distance plus") ;
    if (referenceParetoFrontFile == null) {
      throw new JMetalException("The pareto front approximation is null");
    }

    Front front = new ArrayFront(referenceParetoFrontFile);
    referenceParetoFront = front ;
  }

  /**
   * Constructor
   *
   * @param referenceParetoFront
   * @throws FileNotFoundException
   */
  public InvertedGenerationalDistancePlus(Front referenceParetoFront) {
    super("IGD+", "Inverted generational distance plus") ;
    if (referenceParetoFront == null) {
      throw new JMetalException("The pareto front approximation is null");
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
      throw new JMetalException("The pareto front approximation is null") ;
    }

    return invertedGenerationalDistancePlus(new ArrayFront(solutionList), referenceParetoFront);
  }

  /**
   * Returns the inverted generational distance value for a given front
   *
   * @param front The front
   * @param referenceFront The reference pareto front
   */
  public double invertedGenerationalDistancePlus(Front front, Front referenceFront) {

    double sum = 0.0;
    for (int i = 0 ; i < referenceFront.getNumberOfPoints(); i++) {
        sum += FrontUtils.distanceToClosestPoint(referenceFront.getPoint(i),
            front, new DominanceDistance());
    }

    // STEP 4. Divide the sum by the maximum number of points of the reference Pareto front
    return sum / referenceFront.getNumberOfPoints();
  }

  @Override
  public String getName() {
    return super.getName();
  }
}
