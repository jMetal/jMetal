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

package jmetal.util;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Class representing an adaptive random neighborhood
 */
public class AdaptiveRandomNeighborhood {
  private SolutionSet solutionSet_;

  //private ArrayList<ArrayList<Solution>> solutionList_ ;
  private ArrayList<ArrayList<Integer>> list_;

  private int numberOfRandomNeighbours_;

  /**
   * Constructor.
   * Defines a neighborhood of a given size.
   */
  public AdaptiveRandomNeighborhood(SolutionSet solutionSet, int numberOfRandomNeighbours) {
    solutionSet_ = solutionSet;
    numberOfRandomNeighbours_ = numberOfRandomNeighbours;
    //problem_ = solutionSet_.get(0).getProblem() ;

    list_ = new ArrayList<ArrayList<Integer>>(solutionSet_.size());

    for (int i = 0; i < solutionSet_.size(); i++) {
      list_.add(new ArrayList<Integer>());
      list_.get(i).add(i) ;
    }

    //System.out.println("list: " + list_) ;
    for (int i = 0; i < solutionSet_.size(); i++) {
      //list_.get(i).add(i);
      for (int j = 0; j < numberOfRandomNeighbours_; j++) {
        int random = PseudoRandom.randInt(0, solutionSet_.size() - 1);
        //System.out.println("i: " + i + " random: " + random + " listb: " + list_.get(random)) ;
        if (!list_.get(random).contains((Integer) i)) {
          list_.get(random).add(i);
        }
      }
    }
    System.out.println("L: " + list_) ;
  }

  public ArrayList<Integer> getNeighbors(int i) throws JMException {
    if (i > list_.size()) {
      String message = "Error in AdaptiveRandomNeighborhood.getNeighbors"  ;
      message += "the parameter " + i + " is less than " + list_.size() ;
      throw new JMException(message);
    }

    return list_.get(i);
  }

  public int getNumberOfRandomNeighbours() {
    return numberOfRandomNeighbours_;
  }

  public SolutionSet getBestFitnessSolutionInNeighborhood(Comparator comparator) {
    SolutionSet result = new SolutionSet();
    for (int i = 0; i < list_.size(); i++) {
      Solution bestSolution = solutionSet_.get(list_.get(i).get(0));
      for (int j = 1; j < list_.get(i).size(); j++) {
        if (comparator.compare(bestSolution, solutionSet_.get(list_.get(i).get(j))) > 0) {
          bestSolution = solutionSet_.get(list_.get(i).get(j));
        }
      }
      result.add(bestSolution);
    }

    return result;
  }

  public ArrayList<ArrayList<Integer>> getNeighborhood() {
    return list_;
  }
//  public AdaptiveRandomNeighborhood(int size, int numberOfInformants) {
//    list_ = new ArrayList<ArrayList<Integer>>(size) ;
//
//    for (int i = 0 ; i < size; i++) {
//      list_.get(i).add(i) ;
//      for (int j = 0 ; j < numberOfInformants; j++) {
//        int random = PseudoRandom.randInt(0, size-1) ;
//        list_.get(random).add(i) ;
//      }
//    }
//  }

  public void recompute() {
    list_ = new ArrayList<ArrayList<Integer>>(solutionSet_.size());

    for (int i = 0; i < solutionSet_.size(); i++) {
      list_.add(new ArrayList<Integer>());
      list_.get(i).add(i);
    }

    for (int i = 0; i < solutionSet_.size(); i++) {
      for (int j = 0; j < numberOfRandomNeighbours_; j++) {
        int random = PseudoRandom.randInt(0, solutionSet_.size() - 1);
        if (!list_.get(random).contains((Integer)i)) {
          list_.get(random).add(i);
        }
      }
    }
  }

  public String toString() {
    String result = "";
    for (int i = 0; i < list_.size(); i++) {
      result += i + ": ";
      for (int j = 0; j < list_.get(i).size(); j++) {
        result += list_.get(i).get(j) + " ";
      }
      result += "\n";
    }

    return result;
  }

} // AdaptiveRandomNeighborhood
