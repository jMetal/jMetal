//  AdaptiveRandomNeighborhood.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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

package org.uma.jmetal45.util;

import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.core.SolutionSet;
import org.uma.jmetal45.util.random.PseudoRandom;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Class representing an adaptive random neighborhood
 */
public class AdaptiveRandomNeighborhood {
  private SolutionSet solutionSet;
  private ArrayList<ArrayList<Integer>> list;

  private int numberOfRandomNeighbours;

  public AdaptiveRandomNeighborhood(SolutionSet solutionSet, int numberOfRandomNeighbours) {
    if (null == solutionSet) {
      throw new JMetalException("solution set is null") ;
    }
    this.solutionSet = solutionSet;
    this.numberOfRandomNeighbours = numberOfRandomNeighbours;

    list = new ArrayList<ArrayList<Integer>>(this.solutionSet.size());

    for (int i = 0; i < this.solutionSet.size(); i++) {
      list.add(new ArrayList<Integer>());
      list.get(i).add(i);
    }

    for (int i = 0; i < this.solutionSet.size(); i++) {
      for (int j = 0; j < this.numberOfRandomNeighbours; j++) {
        int random = PseudoRandom.randInt(0, this.solutionSet.size() - 1);
        if (!list.get(random).contains((Integer) i)) {
          list.get(random).add(i);
        }
      }
    }
  }

  public ArrayList<Integer> getNeighbors(int i) throws JMetalException {
    if (i > list.size()) {
      String message = "Error in AdaptiveRandomNeighborhood.getNeighbors";
      message += "the parameter " + i + " is less than " + list.size();
      throw new JMetalException(message);
    }

    return list.get(i);
  }

  public int getNumberOfRandomNeighbours() {
    return numberOfRandomNeighbours;
  }

  public SolutionSet getBestFitnessSolutionInNeighborhood(Comparator comparator) throws
    JMetalException {
    SolutionSet result = new SolutionSet();
    for (int i = 0; i < list.size(); i++) {
      Solution bestSolution = solutionSet.get(list.get(i).get(0));
      for (int j = 1; j < list.get(i).size(); j++) {
        if (comparator.compare(bestSolution, solutionSet.get(list.get(i).get(j))) > 0) {
          bestSolution = solutionSet.get(list.get(i).get(j));
        }
      }
      result.add(bestSolution);
    }

    return result;
  }

  public ArrayList<ArrayList<Integer>> getNeighborhood() {
    return list;
  }

  public void recompute() {
    list = new ArrayList<ArrayList<Integer>>(solutionSet.size());

    for (int i = 0; i < solutionSet.size(); i++) {
      list.add(new ArrayList<Integer>());
      list.get(i).add(i);
    }

    for (int i = 0; i < solutionSet.size(); i++) {
      for (int j = 0; j < numberOfRandomNeighbours; j++) {
        int random = PseudoRandom.randInt(0, solutionSet.size() - 1);
        if (!list.get(random).contains((Integer) i)) {
          list.get(random).add(i);
        }
      }
    }
  }

  public String toString() {
    String result = "";
    for (int i = 0; i < list.size(); i++) {
      result += i + ": ";
      for (int j = 0; j < list.get(i).size(); j++) {
        result += list.get(i).get(j) + " ";
      }
      result += "\n";
    }

    return result;
  }
}
