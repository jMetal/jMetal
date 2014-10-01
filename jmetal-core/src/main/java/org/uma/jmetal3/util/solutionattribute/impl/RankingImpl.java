//  Ranking.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
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

package org.uma.jmetal3.util.solutionattribute.impl;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.util.solutionattribute.Ranking;
import org.uma.jmetal3.util.comparator.DominanceComparator;
import org.uma.jmetal3.util.comparator.OverallConstraintViolationComparator;

import java.util.*;

/**
 * This class implements some facilities for ranking set of solutions.
 * Given a collection of solutions, they are ranked
 * according to scheme proposed in NSGA-II; as an output, a set of subsets
 * are obtained. The subsets are numbered starting from 0 (in NSGA-II, the
 * numbering starts from 1); thus, subset 0 contains the non-dominated
 * solutions, subset 1 contains the non-dominated solutions after removing those
 * belonging to subset 0, and so on.
 */
public class RankingImpl implements Ranking<Solution<?>> {
  private final Object ATTRIBUTE = this ;

  private static final Comparator<Solution<?>> DOMINANCE_COMPARATOR = new DominanceComparator();
  private static final Comparator<Solution<?>> CONSTRAINT_VIOLATION_COMPARATOR =
    new OverallConstraintViolationComparator();

  private List<Solution<?>> population;
  private List<ArrayList<Solution<?>>> rankedSubpopulations;

  @Override
  public Ranking computeRanking(List<Solution<?>> solutionSet) {
    population = solutionSet;

    // dominateMe[i] contains the number of solutions dominating i
    int[] dominateMe = new int[this.population.size()];

    // iDominate[k] contains the list of solutions dominated by k
    List<Integer>[] iDominate = new List[this.population.size()];

    // front[i] contains the list of individuals belonging to the front i
    List<Integer>[] front = new List[this.population.size() + 1];

    // Initialize the fronts 
    for (int i = 0; i < front.length; i++) {
      front[i] = new LinkedList<Integer>();
    }

    // Fast non dominated sorting algorithm
    // Contribution of Guillaume Jacquenot
    for (int p = 0; p < this.population.size(); p++) {
      // Initialize the list of individuals that i dominate and the number
      // of individuals that dominate me
      iDominate[p] = new LinkedList<Integer>();
      dominateMe[p] = 0;
    }

    int flagDominate;
    for (int p = 0; p < (this.population.size() - 1); p++) {
      // For all q individuals , calculate if p dominates q or vice versa
      for (int q = p + 1; q < this.population.size(); q++) {
        flagDominate =
          CONSTRAINT_VIOLATION_COMPARATOR.compare(solutionSet.get(p), solutionSet.get(q));
        if (flagDominate == 0) {
          flagDominate = DOMINANCE_COMPARATOR.compare(solutionSet.get(p), solutionSet.get(q));
        }
        if (flagDominate == -1) {
          iDominate[p].add(q);
          dominateMe[q]++;
        } else if (flagDominate == 1) {
          iDominate[q].add(p);
          dominateMe[p]++;
        }
      }
    }

    for (int i = 0; i < this.population.size(); i++) {
      if (dominateMe[i] == 0) {

        front[0].add(i);
        //RankingAndCrowdingAttr.getAttributes(solutionSet.get(0)).setRank(0);
        solutionSet.get(i).setAttribute(ATTRIBUTE, 0);
      }
    }

    //Obtain the rest of fronts
    int i = 0;
    Iterator<Integer> it1, it2; // Iterators
    while (front[i].size() != 0) {
      i++;
      it1 = front[i - 1].iterator();
      while (it1.hasNext()) {
        it2 = iDominate[it1.next()].iterator();
        while (it2.hasNext()) {
          int index = it2.next();
          dominateMe[index]--;
          if (dominateMe[index] == 0) {
            front[i].add(index);
            //RankingAndCrowdingAttr.getAttributes(solutionSet.get(index)).setRank(i);
            solutionSet.get(index).setAttribute(ATTRIBUTE, i);
          }
        }
      }
    }

    rankedSubpopulations = new ArrayList<ArrayList<Solution<?>>>();
    //0,1,2,....,i-1 are fronts, then i fronts
    for (int j = 0; j < i; j++) {
      rankedSubpopulations.add(j, new ArrayList<Solution<?>>(front[j].size()));
      it1 = front[j].iterator();
      while (it1.hasNext()) {
        rankedSubpopulations.get(j).add(solutionSet.get(it1.next()));
      }
    }

    return this;
  }

  @Override
  public List<Solution<?>> getSubfront(int rank) {
    if (rank >= rankedSubpopulations.size()) {
      throw new JMetalException("Invalid rank: " + rank + ". Max rank = " + (rankedSubpopulations.size() -1)) ;
    }
    return rankedSubpopulations.get(rank);
  }

  @Override
  public int getNumberOfSubfronts() {
    return rankedSubpopulations.size();
  }

  @Override
  public void setAttribute(Solution<?> solution, Integer value) {
    solution.setAttribute(getAttributeID(), value);
  }

  @Override
  public Integer getAttribute(Solution<?> solution) {
    return (Integer) solution.getAttribute(getAttributeID());
  }

  public Object getAttributeID() {
    return this;
  }
}
