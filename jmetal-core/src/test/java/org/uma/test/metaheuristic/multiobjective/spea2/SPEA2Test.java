//  SPEA2Test.java
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

package org.uma.test.metaheuristic.multiobjective.spea2;

import org.junit.Test;
import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.experiment.settings.NSGAIISettings;
import org.uma.jmetal.experiment.settings.SPEA2Settings;
import org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII;
import org.uma.jmetal.metaheuristic.multiobjective.spea2.SPEA2;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Antonio J. Nebro on 13/08/14.
 */
public class SPEA2Test {
  Algorithm algorithm;

  @Test
  public void numberOfReturnedSolutionsInEasyProblemTest() throws IOException, ClassNotFoundException {
    algorithm = new SPEA2Settings("Kursawe").configure() ;

    SolutionSet solutionSet = algorithm.execute() ;
    /*
    Rationale: the default problem is Kursawe, and usually SPEA2, configured with standard
    settings, should return 100 solutions
     */
    assertTrue(solutionSet.size() >= 98) ;
  }
}
