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
 * NOTE: if you use the default constructor, the problem has to be passed as
 * a parameter before invoking the execute() method -- see lines 67 - 74
 */
public class RankingAndCrowdingSelection extends Selection {
  private static final long serialVersionUID = 3650068556668255844L;

  private static final Comparator<Solution> crowdingComparator = new CrowdingComparator();
  private static final Distance distance = new Distance();
  private Problem problem = null;
  private int populationSize = 0 ;

  @Deprecated
  public RankingAndCrowdingSelection(HashMap<String, Object> parameters) {
    super(parameters);

    if (parameters.get("problem") != null) {
      problem = (Problem) parameters.get("problem");
    }

    if (parameters.get("populationSize") != null) {
      populationSize = (Integer) parameters.get("populationSize");
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

    problem = builder.problem ;
    populationSize = builder.populationSize ;
  }

  /** execute() method */
  public Object execute(Object object) throws JMetalException {
    SolutionSet population = (SolutionSet) object;
    //int populationSize = (Integer) parameters_.get("populationSize");
    SolutionSet result = new SolutionSet(populationSize);

    // Ranking the union
    Ranking ranking = new Ranking(population);

    int remain = populationSize;
    int index = 0;
    SolutionSet front = null;
    population.clear();

    // Obtain the next front
    front = ranking.getSubfront(index);

    while ((remain > 0) && (remain >= front.size())) {
      //Assign crowding distance to individuals
      distance.crowdingDistanceAssignment(front);
      //Add the individuals of this front
      for (int k = 0; k < front.size(); k++) {
        result.add(front.get(k));
      }

      //Decrement remain
      remain = remain - front.size();

      //Obtain the next front
      index++;
      if (remain > 0) {
        front = ranking.getSubfront(index);
      }
    }

    //remain is less than front(index).size, insert only the best one
    if (remain > 0) {
      distance.crowdingDistanceAssignment(front);
      front.sort(crowdingComparator);
      for (int k = 0; k < remain; k++) {
        result.add(front.get(k));
      }

      remain = 0;
    }

    return result;
  }

  /** Builder class */
  public static class Builder {
    Problem problem ;
    int populationSize ;

    public Builder(Problem problem) {
      this.problem = problem ;
      this.populationSize = 0 ;
    }

    public Builder populationSize(int populationSize) {
      this.populationSize = populationSize ;

       return this ;
    }

    public RankingAndCrowdingSelection build() {
      return new RankingAndCrowdingSelection(this) ;
    }
  }
}
