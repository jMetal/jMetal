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

package org.uma.jmetal3.metaheuristic.multiobjective.smsemoa;

import org.uma.jmetal.qualityindicator.Hypervolume;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal3.core.Solution;

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

  private Hypervolume hypervolume;

  /**
   * Constructor
   */
  protected SMSEMOA(Builder builder) {
    super(builder);

    this.hypervolume = new Hypervolume();
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
    /*
      // Create the solutionSet union of solutionSet and offSpring
      offspringPopulation.addAll(population) ;
      computeRanking(offspringPopulation);

      // ensure crowding distance values are up to date
      // (may be important for parent selection)
      for (int j = 0; j < population.size(); j++) {
        population.get(j).setCrowdingDistance(0.0);
      }

      SolutionSet lastFront = ranking.getSubfront(ranking.getNumberOfSubfronts() - 1);
      if (lastFront.size() > 1) {
        double[][] frontValues = lastFront.writeObjectivesToMatrix();
        int numberOfObjectives = problem.getNumberOfObjectives();
        // STEP 1. Obtain the maximum and minimum values of the Pareto front
        double[] maximumValues =
          utils.getMaximumValues(union.writeObjectivesToMatrix(), numberOfObjectives);
        double[] minimumValues =
          utils.getMinimumValues(union.writeObjectivesToMatrix(), numberOfObjectives);
        // STEP 2. Get the normalized front
        double[][] normalizedFront =
          utils.getNormalizedFront(frontValues, maximumValues, minimumValues);
        // compute offsets for reference point in normalized space
        double[] offsets = new double[maximumValues.length];
        for (int i = 0; i < maximumValues.length; i++) {
          offsets[i] = offset / (maximumValues[i] - minimumValues[i]);
        }
        // STEP 3. Inverse the pareto front. This is needed because the original
        //metric by Zitzler is for maximization problem
        double[][] invertedFront = utils.invertedFront(normalizedFront);
        // shift away from origin, so that boundary points also get a contribution > 0
        for (double[] point : invertedFront) {
          for (int i = 0; i < point.length; i++) {
            point[i] += offsets[i];
          }
        }

        // calculate contributions and sort
        double[] contributions = hvContributions(invertedFront);
        for (int i = 0; i < contributions.length; i++) {
          // contribution values are used analogously to crowding distance
          lastFront.get(i).setCrowdingDistance(contributions[i]);
        }

        lastFront.sort(new CrowdingDistanceComparator());
      }

      // all but the worst are carried over to the survivor population
      SolutionSet front = null;
      population.clear();
      for (int i = 0; i < ranking.getNumberOfSubfronts() - 1; i++) {
        front = ranking.getSubfront(i);
        for (int j = 0; j < front.size(); j++) {
          population.add(front.get(j));
        }
      }
      for (int i = 0; i < lastFront.size() - 1; i++) {
        population.add(lastFront.get(i));
      }

    }

    return getNonDominatedSolutions(population) ;
    */
    }
    return null;

    /**
     * Calculates how much hypervolume each point dominates exclusively. The points
     * have to be transformed beforehand, to accommodate the assumptions of Zitzler's
     * hypervolume code.
     *
     * @param front transformed objective values
     * @return HV contributions
     */
    /*
  private double[] hvContributions(double[][] front) {
    int numberOfObjectives = problem.getNumberOfObjectives();
    double[] contributions = new double[front.length];
    double[][] frontSubset = new double[front.length - 1][front[0].length];
    LinkedList<double[]> frontCopy = new LinkedList<double[]>();
    Collections.addAll(frontCopy, front);
    double[][] totalFront = frontCopy.toArray(frontSubset);
    double totalVolume =
      hypervolume.calculateHypervolume(totalFront, totalFront.length, numberOfObjectives);
    for (int i = 0; i < front.length; i++) {
      double[] evaluatedPoint = frontCopy.remove(i);
      frontSubset = frontCopy.toArray(frontSubset);
      // STEP4. The hypervolume (control is passed to java version of Zitzler code)
      double hv = hypervolume.calculateHypervolume(frontSubset, frontSubset.length, numberOfObjectives);
      double contribution = totalVolume - hv;
      contributions[i] = contribution;
      // put point back
      frontCopy.add(i, evaluatedPoint);
    }
    return contributions;
  }
  */
  }
}
