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

package org.uma.jmetal.util.solutionattribute.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("serial")
public class StrengthRawFitness <S extends Solution<?>>
    extends GenericSolutionAttribute<S, Double> implements DensityEstimator<S>{
  private static final Comparator<Solution<?>> DOMINANCE_COMPARATOR = new DominanceComparator<Solution<?>>();

  @Override
  public void computeDensityEstimator(List<S> solutionSet) {
    double [][] distance = SolutionListUtils.distanceMatrix(solutionSet);
    double []   strength    = new double[solutionSet.size()];
    double []   rawFitness  = new double[solutionSet.size()];
    double kDistance                                          ;

    // strength(i) = |{j | j <- SolutionSet and i dominate j}|
    for (int i = 0; i < solutionSet.size(); i++) {
      for (int j = 0; j < solutionSet.size();j++) {
        if (DOMINANCE_COMPARATOR.compare(solutionSet.get(i),solutionSet.get(j))==-1) {
          strength[i] += 1.0;
        }
      }
    }

    //Calculate the raw fitness
    // rawFitness(i) = |{sum strenght(j) | j <- SolutionSet and j dominate i}|
    for (int i = 0;i < solutionSet.size(); i++) {
      for (int j = 0; j < solutionSet.size();j++) {
        if (DOMINANCE_COMPARATOR.compare(solutionSet.get(i),solutionSet.get(j))==1) {
          rawFitness[i] += strength[j];
        }
      }
    }

    // Add the distance to the k-th individual. In the reference paper of SPEA2,
    // k = sqrt(population.size()), but a value of k = 1 is recommended. See
    // http://www.tik.ee.ethz.ch/pisa/selectors/spea2/spea2_documentation.txt
    int k = 1 ;
    for (int i = 0; i < distance.length; i++) {
      Arrays.sort(distance[i]);
      kDistance = 1.0 / (distance[i][k] + 2.0);
      solutionSet.get(i).setAttribute(getAttributeID(), rawFitness[i] + kDistance);
    }
  }
}
