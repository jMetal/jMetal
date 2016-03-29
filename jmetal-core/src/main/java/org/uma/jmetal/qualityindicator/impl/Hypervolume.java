//  Hypervolume.java
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

package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * This interface represents implementations of the Hypervolume quality indicator

 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public abstract class Hypervolume<S extends Solution<?>> extends GenericIndicator<S> {

  public Hypervolume() {
  }

  public Hypervolume(String referenceParetoFrontFile) throws FileNotFoundException {
    super(referenceParetoFrontFile);
  }

  public Hypervolume(Front referenceParetoFront) {
    super(referenceParetoFront);
  }

  public abstract List<S> computeHypervolumeContribution(List<S> solutionList, List<S> referenceFrontList) ;
  public abstract double getOffset() ;
  public abstract void setOffset(double offset) ;

  @Override public String getName() {
    return "HV" ;
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return false ;
  }
}
