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

package org.uma.jmetal.operator.selection;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.Distance;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Ranking;
import org.uma.jmetal.util.comparator.CrowdingComparator;

import java.util.Comparator;
import java.util.HashMap;

/**
 * This class implements a selection for selecting a number of solutions from
 * a solutionSet. The solutions are taken by mean of its ranking and
 * crowding distance values.
 */
public class RankingAndCrowdingSelection extends Selection {
  private static final long serialVersionUID = 3650068556668255844L;

  private Comparator<Solution> crowdingComparator = new CrowdingComparator();
  private Distance distance = new Distance();
  private Problem problem = null;

  private int solutionsToSelect = 0 ;

  @Deprecated
  public RankingAndCrowdingSelection(HashMap<String, Object> parameters) {
    super(parameters);

    if (parameters.get("problem") != null) {
      problem = (Problem) parameters.get("problem");
    }

    if (parameters.get("solutionsToSelect") != null) {
      solutionsToSelect = (Integer) parameters.get("solutionsToSelect");
    }

    if (problem == null) {
      Configuration.logger_.severe("RankingAndCrowdingSelection.execute: " +
              "problem not specified");
      Class cls = java.lang.String.class;
      String name = cls.getName();
    }
  }

  /** Constructor */
  private RankingAndCrowdingSelection(Builder builder) {
    super(new HashMap<String, Object>()) ;

    solutionsToSelect = builder.solutionsToSelect ;
  }

  public int getSolutionsToSelect() {
    return solutionsToSelect;
  }

  /** execute() method */
  public Object execute(Object object) throws JMetalException {
    if (null == object) {
      throw new JMetalException("Parameter is null") ;
    } else if (!(object instanceof SolutionSet)) {
      throw new JMetalException("Invalid parameter class") ;
    }  else if (((SolutionSet) object).size() < solutionsToSelect) {
      throw new JMetalException("The population size ("+((SolutionSet) object).size()+") is smaller than" +
              "the solutions to selected ("+solutionsToSelect+")")  ;
    }

    SolutionSet population = (SolutionSet) object;
    SolutionSet resultPopulation = new SolutionSet(solutionsToSelect);

    Ranking ranking = new Ranking(population);

    int remain = solutionsToSelect;
    SolutionSet front ;
    population.clear();

    int rankingIndex = 0;
    front = ranking.getSubfront(rankingIndex);

    while ((remain > 0) && (remain >= front.size())) {
      distance.crowdingDistanceAssignment(front);
      //Add the individuals of this front
      for (int i = 0; i < front.size(); i++) {
        resultPopulation.add(front.get(i));
      }
      remain = remain - front.size();

      rankingIndex++;
      if (remain > 0) {
        front = ranking.getSubfront(rankingIndex);
      }
    }

    //remain is less than front(index).size, insert only the best one
    if (remain > 0) {
      distance.crowdingDistanceAssignment(front);
      front.sort(crowdingComparator);
      for (int k = 0; k < remain; k++) {
        resultPopulation.add(front.get(k));
      }
    }

    return resultPopulation;
  }

  /** Builder class */
  public static class Builder {
    int solutionsToSelect ;

    public Builder(int solutionsToSelect) {
      this.solutionsToSelect = solutionsToSelect ;
    }

    public RankingAndCrowdingSelection build() {
      return new RankingAndCrowdingSelection(this) ;
    }
  }
}
