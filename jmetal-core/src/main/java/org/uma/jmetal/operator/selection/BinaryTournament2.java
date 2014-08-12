//  BinaryTournament2.java
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

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.encoding.variable.Permutation;
import org.uma.jmetal.operator.selection.BinaryTournament.Builder;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.Comparator;
import java.util.HashMap;

/**
 * This class implements an operator for binary selections using the same code
 * in Deb's NSGA-II implementation
 */
public class BinaryTournament2 extends Selection {
  private static final long serialVersionUID = -6853195126789856216L;

  private Comparator<Solution> comparator;

  /** Stores a permutation of the solutions in the SolutionSet */
  private int permutation[];

  /**
   * index_ stores the actual index for selection
   */
  private int index = 0;

  /**
   * Constructor
   * Creates a new instance of the Binary tournament operator (Deb's
   * NSGA-II implementation version)
   */
  @Deprecated
  public BinaryTournament2(HashMap<String, Object> parameters) {
    super(parameters);
    comparator = new DominanceComparator();
  }

  private BinaryTournament2(Builder builder) {
    super(new HashMap<String, Object>()) ;

    comparator = builder.comparator ;
  }

  /** execute() method */
  public Object execute(Object object) {
    if (null == object) {
      throw new JMetalException("Parameter is null") ;
    } else if (!(object instanceof SolutionSet)) {
      throw new JMetalException("Invalid parameter class") ;
    } else if (((SolutionSet)object).size() == 0) {
      throw new JMetalException("Solution set size is 0") ;
    } else if (((SolutionSet)object).size() == 1) {
      throw new JMetalException("Solution set size is 1") ;
    }
  	
    SolutionSet population = (SolutionSet) object;
    if (index == 0) {
    	permutation = new Permutation(population.size()).getVector() ;
    }

    Solution solution1, solution2;
    solution1 = population.get(permutation[index]);
    solution2 = population.get(permutation[index + 1]);

    index = (index + 2) % population.size();

    int flag = comparator.compare(solution1, solution2);
    if (flag == -1) {
      return solution1;
    } else if (flag == 1) {
      return solution2;
    } else if (solution1.getCrowdingDistance() > solution2.getCrowdingDistance()) {
      return solution1;
    } else if (solution2.getCrowdingDistance() > solution1.getCrowdingDistance()) {
      return solution2;
    } else {
      if (PseudoRandom.randDouble() < 0.5) {
        return solution1;
      } else {
        return solution2;
      }
    }
  }

  /** Builder class */
  public static class Builder {
    Comparator<Solution> comparator ;

    public Builder() {
      comparator = new DominanceComparator() ;
    }

    public Builder comparator(Comparator<Solution> comparator) {
      this.comparator = comparator ;

      return this ;
    }

    public BinaryTournament2 build() {
      return new BinaryTournament2(this) ;
    }
  }
}
