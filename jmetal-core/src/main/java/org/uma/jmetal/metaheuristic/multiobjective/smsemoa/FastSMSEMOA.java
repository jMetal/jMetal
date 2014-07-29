//  SMSEMOA2.java
//
//  Author:
//       Antonio J. Nebro
//
//  Copyright (c) 2013 Antonio J. Nebro
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

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.qualityIndicator.QualityIndicatorGetter;
import org.uma.jmetal.qualityIndicator.fasthypervolume.FastHypervolume;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Ranking;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;

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
 * <p/>
 * This algoritm is SMS-EMOA using the FastHypervolume class
 */
public class FastSMSEMOA extends Algorithm {
  private static final long serialVersionUID = 2217597718629923190L;

  public FastSMSEMOA() {
    super();
  }

  /** Execute() method */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    int populationSize;
    int maxEvaluations;
    int evaluations;
    double offset;

    QualityIndicatorGetter indicators;
    int requiredEvaluations;

    FastHypervolume fastHypervolume;

    SolutionSet population;
    SolutionSet offspringPopulation;
    SolutionSet union;

    Operator mutationOperator;
    Operator crossoverOperator;
    Operator selectionOperator;

    //Read the parameters
    populationSize = ((Integer) getInputParameter("populationSize")).intValue();
    maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();
    indicators = (QualityIndicatorGetter) getInputParameter("indicators");
    offset = (Double) getInputParameter("offset");


    //Initialize the variables
    population = new SolutionSet(populationSize);
    evaluations = 0;

    requiredEvaluations = 0;

    fastHypervolume = new FastHypervolume(offset);

    //Read the operator
    mutationOperator = operators_.get("mutation");
    crossoverOperator = operators_.get("crossover");
    selectionOperator = operators_.get("selection");

    // Create the initial solutionSet
    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem_);
      problem_.evaluate(newSolution);
      problem_.evaluateConstraints(newSolution);
      evaluations++;
      population.add(newSolution);
    }

    // Generations ...
    while (evaluations < maxEvaluations) {

      // select parents
      offspringPopulation = new SolutionSet(populationSize);
      LinkedList<Solution> selectedParents = new LinkedList<Solution>();
      Solution[] parents = new Solution[0];
      while (selectedParents.size() < 2) {
        Object selected = selectionOperator.execute(population);
        try {
          Solution parent = (Solution) selected;
          selectedParents.add(parent);
        } catch (ClassCastException e) {
          JMetalLogger.logger.log(Level.WARNING, "Class cast exception", e);
          parents = (Solution[]) selected;
          Collections.addAll(selectedParents, parents);
        }
      }
      parents = selectedParents.toArray(parents);

      // crossover
      Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);

      // mutation
      mutationOperator.execute(offSpring[0]);

      // evaluation
      problem_.evaluate(offSpring[0]);
      problem_.evaluateConstraints(offSpring[0]);

      // insert child into the offspring population
      offspringPopulation.add(offSpring[0]);

      evaluations++;

      // Create the solutionSet union of solutionSet and offSpring
      union = ((SolutionSet) population).union(offspringPopulation);

      // Ranking the union (non-dominated sorting)
      Ranking ranking = new Ranking(union);

      // ensure crowding distance values are up to date
      // (may be important for parent selection)
      for (int j = 0; j < population.size(); j++) {
        population.get(j).setCrowdingDistance(0.0);
      }

      SolutionSet lastFront = ranking.getSubfront(ranking.getNumberOfSubfronts() - 1);

      //FastHypervolume fasthypervolume = new FastHypervolume() ;
      fastHypervolume.computeHVContributions(lastFront);
      lastFront.sort(new CrowdingDistanceComparator());

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

    // Return as output parameter the required evaluations
    setOutputParameter("evaluations", requiredEvaluations);

    // Return the first non-dominated front
    Ranking ranking = new Ranking(population);

    return ranking.getSubfront(0);
  }
}
