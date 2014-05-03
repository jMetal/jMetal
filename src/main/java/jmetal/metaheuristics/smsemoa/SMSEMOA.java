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

package jmetal.metaheuristics.smsemoa;

import jmetal.core.*;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.comparators.CrowdingDistanceComparator;

import java.util.Collections;
import java.util.LinkedList;

/**
 * This class implements the SMS-EMOA algorithm, as described in
 *
 * Michael Emmerich, Nicola Beume, and Boris Naujoks.
 * An EMO algorithm using the hypervolume measure as selection criterion.
 * In C. A. Coello Coello et al., Eds., Proc. Evolutionary Multi-Criterion Optimization,
 * 3rd Int'l Conf. (EMO 2005), LNCS 3410, pp. 62-76. Springer, Berlin, 2005.
 *
 * and
 * 
 * Boris Naujoks, Nicola Beume, and Michael Emmerich.
 * Multi-objective optimisation using S-metric selection: Application to
 * three-dimensional solution spaces. In B. McKay et al., Eds., Proc. of the 2005
 * Congress on Evolutionary Computation (CEC 2005), Edinburgh, Band 2, pp. 1282-1289.
 * IEEE Press, Piscataway NJ, 2005.
 */
public class SMSEMOA extends Algorithm {

  /**
   * stores the problem  to solve
   */
  private MetricsUtil utils_;
  private Hypervolume hv_;

  /**
   * Constructor
   * @param problem Problem to solve
   */
  public SMSEMOA(Problem problem) {
    super(problem) ;
    this.utils_ = new jmetal.qualityIndicator.util.MetricsUtil();
    this.hv_ = new Hypervolume();
  } // SMSEMOA

  /**
   * Runs the SMS-EMOA algorithm.
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   * @throws JMException
   */
  public SolutionSet execute() throws JMException, ClassNotFoundException {
    int populationSize;
    int maxEvaluations;
    int evaluations;
    double offset = 100.0;

    QualityIndicator indicators; // QualityIndicator object
    int requiredEvaluations; // Use in the example of use of the indicators object (see below)

    SolutionSet population;
    SolutionSet offspringPopulation;
    SolutionSet union;

    Operator mutationOperator;
    Operator crossoverOperator;
    Operator selectionOperator;

    //Read the parameters
    populationSize = ((Integer) getInputParameter("populationSize")).intValue();
    maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();
    indicators = (QualityIndicator) getInputParameter("indicators");
    offset = (Double) getInputParameter("offset");


    //Initialize the variables
    population = new SolutionSet(populationSize);
    evaluations = 0;

    requiredEvaluations = 0;

    //Read the operators
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
    } //for

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
      if (lastFront.size() > 1) {
        double[][] frontValues = lastFront.writeObjectivesToMatrix();
        int numberOfObjectives = problem_.getNumberOfObjectives();
        // STEP 1. Obtain the maximum and minimum values of the Pareto front
        double[] maximumValues = utils_.getMaximumValues(union.writeObjectivesToMatrix(), numberOfObjectives);
        double[] minimumValues = utils_.getMinimumValues(union.writeObjectivesToMatrix(), numberOfObjectives);
        // STEP 2. Get the normalized front
        double[][] normalizedFront = utils_.getNormalizedFront(frontValues, maximumValues, minimumValues);
        // compute offsets for reference point in normalized space
        double[] offsets = new double[maximumValues.length];
        for (int i = 0; i < maximumValues.length; i++) {
          offsets[i] = offset / (maximumValues[i] - minimumValues[i]);
        }
        // STEP 3. Inverse the pareto front. This is needed because the original
        //metric by Zitzler is for maximization problems
        double[][] invertedFront = utils_.invertedFront(normalizedFront);
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

      // This piece of code shows how to use the indicator object into the code
      // of SMS-EMOA. In particular, it finds the number of evaluations required
      // by the algorithm to obtain a Pareto front with a hypervolume higher
      // than the hypervolume of the true Pareto front.
      if (indicators != null && requiredEvaluations == 0) {
        double HV = indicators.getHypervolume(population);
        if (HV >= (0.98 * indicators.getTrueParetoFrontHypervolume())) {
          requiredEvaluations = evaluations;
        } // if
      } // if
    } // while

    // Return as output parameter the required evaluations
    setOutputParameter("evaluations", requiredEvaluations);
    
    // Return the first non-dominated front
    Ranking ranking = new Ranking(population);
    ranking.getSubfront(0).printFeasibleFUN("FUN") ;
    return ranking.getSubfront(0);
  } // execute

  /**
   * Calculates how much hypervolume each point dominates exclusively. The points
   * have to be transformed beforehand, to accommodate the assumptions of Zitzler's
   * hypervolume code.
   * @param front transformed objective values
   * @return HV contributions
   */
  private double[] hvContributions(double[][] front) {
    int numberOfObjectives = problem_.getNumberOfObjectives();
    double[] contributions = new double[front.length];
    double[][] frontSubset = new double[front.length - 1][front[0].length];
    LinkedList<double[]> frontCopy = new LinkedList<double[]>();
      Collections.addAll(frontCopy, front);
    double[][] totalFront = frontCopy.toArray(frontSubset);
    double totalVolume = hv_.calculateHypervolume(totalFront, totalFront.length, numberOfObjectives);
    for (int i = 0; i < front.length; i++) {
      double[] evaluatedPoint = frontCopy.remove(i);
      frontSubset = frontCopy.toArray(frontSubset);
      // STEP4. The hypervolume (control is passed to java version of Zitzler code)
      double hv = hv_.calculateHypervolume(frontSubset, frontSubset.length, numberOfObjectives);
      double contribution = totalVolume - hv;
      contributions[i] = contribution;
      // put point back
      frontCopy.add(i, evaluatedPoint);
    }
    return contributions;
  }
} // SMSEMOA
