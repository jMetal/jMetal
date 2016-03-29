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

package org.uma.jmetal.util.referencePoint;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.point.util.PointSolution;

/**
 * Interface representing a reference point
 *
 * @author <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public abstract class ReferencePoint extends PointSolution {
  public ReferencePoint(int numberOfObjectives) {
    super(numberOfObjectives);
  }

  public abstract void update(Solution<?> solution) ;
}
