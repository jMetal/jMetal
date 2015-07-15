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

package org.uma.jmetal.qualityindicator;

import org.uma.jmetal.util.naming.impl.SimpleDescribedEntity;

/**
 * Abstract class representing quality indicators having the option of normalizing the fronts
 * before computing the indicator
 * @author Antonio J. Nebro <antonio@lcc.uma.es>.
 */

public abstract class NormalizableQualityIndicator<Evaluate, Result>
    extends SimpleDescribedEntity
    implements QualityIndicator<Evaluate,Result> {

  protected boolean normalize ;

  public NormalizableQualityIndicator(String shortName, String description) {
    super(shortName, description) ;
  }

  /**
   * Set normalization of the fronts
   * @param normalize
   * @return
   */
  public QualityIndicator<Evaluate, Result> setNormalize(boolean normalize) {
    this.normalize = normalize ;

    return this ;
  }

  /**
   * Return true if the fronts are normalized before computing the indicator
   * @return
   */
  public boolean frontsNormalized() {
    return normalize ;
  }
}
