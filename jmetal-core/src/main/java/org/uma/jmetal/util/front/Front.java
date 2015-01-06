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

package org.uma.jmetal.util.front;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.point.Point;

import java.util.Comparator;
import java.util.List;

/**
 * A front is a list of points
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
public interface Front {
  public void readFrontFromFile(String fileName) ;
  public void createFrontFromAListOfSolutions(List<Solution> solutionList) ;
  public int getNumberOfPoints() ;
  public Point getPoint(int index) ;
  public void sort(Comparator<Point> comparator) ;
}
