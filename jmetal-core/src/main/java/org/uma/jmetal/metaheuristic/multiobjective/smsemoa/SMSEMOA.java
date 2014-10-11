//  SMSEMOA.java
//
//  Author:
//       Simon Wessing
//
//  Copyright (c) 2011 Simon Wessing
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

package org.uma.jmetal.metaheuristic.multiobjective.smsemoa;

import org.uma.jmetal45.qualityindicator.fasthypervolume.FastHypervolume;
import org.uma.jmetal45.util.JMetalException;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.util.solutionattribute.impl.HypervolumeContribution;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the SMS-EMOA algorithm, as described in
 * <p/>
 * Michael Emmerich, Nicola Beume, and Boris Naujoks.
 * An EMO algorithm using the hypervolume measure as selection criterion.
 * In C. A. Coello Coello et al., Eds., Proc. Evolutionary Multi-Criterion Optimization,
 * 3rd Int'l Conf. (EMO 2005), LNCS 3410, pp. 62-76. Springer, Berlin, 2005.
 * <p/>
 * and
 * <p/>
 * Boris Naujoks, Nicola Beume, and Michael Emmerich.
 * Multi-objective optimisation using S-metric selection: Application to
 * three-dimensional solutiontype spaces. In B. McKay et al., Eds., Proc. of the 2005
 * Congress on Evolutionary Computation (CEC 2005), Edinburgh, Band 2, pp. 1282-1289.
 * IEEE Press, Piscataway NJ, 2005.
 */
public class SMSEMOA extends SMSEMOATemplate {

  private FastHypervolume fastHypervolume; ;
  private HypervolumeContribution hypervolumeContribution ;

  /**
   * Constructor
   */
  protected SMSEMOA(Builder builder) {
    super(builder);

    fastHypervolume = new FastHypervolume(offset);
    hypervolumeContribution = new HypervolumeContribution() ;
  }

  /**
   * Execute() method
   */
  public List<?> execute() throws JMetalException {
    createInitialPopulation();
    population = evaluatePopulation(population);

    while (!stoppingCondition()) {
      offspringPopulation = new ArrayList<>(populationSize + 1);

      List<Solution> parents = new ArrayList<>();
      parents.add((Solution) selection.execute(population));

      List<Solution> offspring = (List<Solution>) crossover.execute(parents);
      mutation.execute(offspring.get(0));

      problem.evaluate(offspring.get(0));
      //problem.evaluateConstraints(offSpring[0]);

      offspringPopulation.add(offspring.get(0));

      evaluations++;

      // Create the solutionSet union of solutionSet and offSpring
      offspringPopulation.addAll(population);
      computeRanking(offspringPopulation);

      for (int j = 0; j < population.size(); j++) {
        hypervolumeContribution.setAttribute(population.get(j), 0.0);
      }

      List<Solution<?>> lastFront = ranking.getSubfront(ranking.getNumberOfSubfronts() - 1);

      //fastHypervolume.computeHVContributions(lastFront);
      //lastFront.sort(new org.uma.jmetal45.util.comparator.CrowdingDistanceComparator());
    }
    return null ;
  }
}
