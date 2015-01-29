//  RankingAndCrowdingSelection.java
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

package org.uma.jmetal.operator.impl.selection;

import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class implements a selection for selecting a number of solutions from
 * a solution list. The solutions are taken by mean of its ranking and
 * crowding distance values.
 */
public class RankingAndCrowdingSelection
    implements SelectionOperator<List<? extends Solution>,List<? extends Solution>> {
  private int solutionsToSelect = 0 ;

  /** Constructor */
  public RankingAndCrowdingSelection(int solutionsToSelect) {
    this.solutionsToSelect = solutionsToSelect ;
  }

  /* Getter */
  public int getSolutionsToSelect() {
    return solutionsToSelect;
  }

  /** Execute() method */
  public List<? extends Solution> execute(List<? extends Solution> solutionList) throws JMetalException {
    if (null == solutionList) {
      throw new JMetalException("The solution list is null");
    } else if (solutionList.isEmpty()) {
        throw new JMetalException("The solution list is empty") ;
    }  else if (solutionList.size() < solutionsToSelect) {
      throw new JMetalException("The population size ("+solutionList.size()+") is smaller than" +
              "the solutions to selected ("+solutionsToSelect+")")  ;
    }

    Ranking ranking = new DominanceRanking();
    ranking.computeRanking(solutionList) ;

    List<Solution> resultPopulation = crowdingDistanceSelection(ranking) ;


    return resultPopulation;
  }

  protected List<Solution> crowdingDistanceSelection(Ranking ranking) {
    CrowdingDistance crowdingDistance = new CrowdingDistance() ;
    List<Solution> population = new ArrayList<>(solutionsToSelect) ;
    int rankingIndex = 0;
    while (population.size() < solutionsToSelect) {
      if (subfrontFillsIntoThePopulation(ranking, rankingIndex, population)) {
        addRankedSolutionsToPopulation(ranking, rankingIndex, population);
        rankingIndex++;
      } else {
        crowdingDistance.computeDensityEstimator(ranking.getSubfront(rankingIndex));
        addLastRankedSolutionsToPopulation(ranking, rankingIndex, population);
      }
    }

    return population ;
  }

  protected boolean subfrontFillsIntoThePopulation(Ranking ranking, int rank, List<Solution> population) {
    return ranking.getSubfront(rank).size() < (solutionsToSelect - population.size()) ;
  }

  protected void addRankedSolutionsToPopulation(Ranking ranking, int rank, List<Solution> population) {
    List<Solution> front ;

    front = ranking.getSubfront(rank);

    for (int i = 0 ; i < front.size(); i++) {
      population.add(front.get(i));
    }
  }

  protected void addLastRankedSolutionsToPopulation(Ranking ranking, int rank, List<Solution>population) {
    List<Solution> currentRankedFront = ranking.getSubfront(rank) ;

    Collections.sort(currentRankedFront, new CrowdingDistanceComparator()) ;

    int i = 0 ;
    while (population.size() < solutionsToSelect) {
      population.add(currentRankedFront.get(i)) ;
      i++ ;
    }
  }
}
