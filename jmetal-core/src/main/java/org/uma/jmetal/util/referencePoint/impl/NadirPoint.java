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

package org.uma.jmetal.util.referencePoint.impl;

import org.uma.jmetal.util.referencePoint.ReferencePoint;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

/**
 * Class representing a nadir point (minimization is assumed)
 *
 * @author Antonio J.Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class NadirPoint extends ReferencePoint {
  private static final double DEFAULT_INITIAL_VALUE = Double.NEGATIVE_INFINITY ;

  public NadirPoint(int numberOfPoints) {
    super(numberOfPoints) ;

    for (int i = 0; i < numberOfPoints; i++) {
      this.setObjective(i, DEFAULT_INITIAL_VALUE);
    }
  }

  @Override
  public void update(Solution<?> solution) {
    if (solution == null) {
      throw new JMetalException("The solution is null") ;
    } else if (solution.getNumberOfObjectives() != this.getNumberOfObjectives()) {
      throw new JMetalException("The number of objectives of the solution ("
          + solution.getNumberOfObjectives()
          + ") "
          + "is different to the size of the reference point("
          + this.getNumberOfObjectives()
          + ")"
      );
    }

    for (int i = 0; i < this.getNumberOfObjectives(); i++) {
      if (this.getObjective(i) < solution.getObjective(i)) {
        this.setObjective(i, solution.getObjective(i));
      }
    }
  }
}
