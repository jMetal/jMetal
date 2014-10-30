//  IBEA.java
//
//  Author:
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Juan J. Durillo
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

// This implementation is based on the PISA code:
// http://www.tik.ee.ethz.ch/sop/pisa/selectors/ibea/?page=ibea.php

package org.uma.jmetal.algorithm.impl.multiobjective.ibea;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.FitnessComparator;
import org.uma.jmetal.util.solutionattribute.impl.Fitness;

import java.util.ArrayList;
import java.util.List;

/** This class implements the IBEA algorithm */
public class IBEA implements Algorithm<List<Solution>> {
  private Problem problem ;

  public static final int TOURNAMENTS_ROUNDS = 1;

  private List<List<Double>> indicatorValues;
  private double maxIndicatorValue;

  private int populationSize ;
  private int archiveSize ;
  private int maxEvaluations;

  private List<Solution> archive ;

  private CrossoverOperator crossover ;
  private MutationOperator mutation ;
  private SelectionOperator selection ;

  private Fitness solutionFitness = new Fitness() ;

  /** Constructor */
  private IBEA(Builder builder) {
    this.problem = builder.problem ;
    this.populationSize = builder.populationSize ;
    this.archiveSize = builder.archiveSize ;
    this.maxEvaluations = builder.maxEvaluations ;
    this.crossover = builder.crossover ;
    this.mutation = builder.mutation ;
    this.selection = builder.selection ;
  }

  /* Getters */
  public int getPopulationSize() {
    return populationSize;
  }

  public int getArchiveSize() {
    return archiveSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public CrossoverOperator getCrossover() {
    return crossover;
  }

  public MutationOperator getMutation() {
    return mutation;
  }

  public SelectionOperator getSelection() {
    return selection;
  }

  /** Builder class */
  public static class Builder {
    private Problem problem ;
    private int populationSize ;
    private int archiveSize ;
    private int maxEvaluations;

    private CrossoverOperator crossover ;
    private MutationOperator mutation ;
    private SelectionOperator selection ;

    public Builder(Problem problem) {
      this.problem = problem ;
      populationSize = 100 ;
      archiveSize = 100 ;
      maxEvaluations = 25000 ;

      crossover = new SBXCrossover.Builder()
        .setProbability(0.9)
        .setDistributionIndex(20.0)
        .build();

      mutation = new PolynomialMutation.Builder()
        .setProbability(1.0 / problem.getNumberOfVariables())
        .setDistributionIndex(20.0)
        .build() ;

      selection = new BinaryTournamentSelection.Builder()
        .setComparator(new FitnessComparator())
        .build() ;

    }

    public Builder setPopulationSize(int populationSize) {
      this.populationSize = populationSize;

      return this ;
    }

    public Builder setArchiveSize(int archiveSize) {
      this.archiveSize = archiveSize;

      return this ;
    }

    public Builder setMaxEvaluations(int maxEvaluations) {
      this.maxEvaluations = maxEvaluations;

      return this ;
    }

    public Builder setCrossover(CrossoverOperator crossover) {
      this.crossover = crossover;

      return this ;
    }

    public Builder setMutation(MutationOperator mutation) {
      this.mutation = mutation;

      return this ;
    }

    public Builder setSelection(SelectionOperator selection) {
      this.selection = selection;

      return this ;
    }

    public IBEA build() {
      return new IBEA(this) ;
    }
  }

  /** Execute() method */
  @Override
  public void run() {
    int evaluations;
    List<Solution> solutionSet, offSpringSolutionSet;

    //Initialize the variables
    solutionSet = new ArrayList<>(populationSize);
    archive = new ArrayList<>(archiveSize);
    evaluations = 0;

    //-> Create the initial solutionSet
    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = problem.createSolution() ;
      problem.evaluate(newSolution);
      evaluations++;
      solutionSet.add(newSolution);
    }

    while (evaluations < maxEvaluations) {
      List<Solution> union = new ArrayList<>() ;
      union.addAll(solutionSet) ;
      union.addAll(archive);
      calculateFitness(union);
      archive = union;

      while (archive.size() > populationSize) {
        removeWorst(archive);
      }
      // Create a new offspringPopulation
      offSpringSolutionSet = new ArrayList<>(populationSize);
      Solution parent1 ;
      Solution parent2 ;
      while (offSpringSolutionSet.size() < populationSize) {
        int j = 0;
        do {
          j++;
          parent1 = (Solution) selection.execute(archive);
        } while (j < IBEA.TOURNAMENTS_ROUNDS);
        int k = 0;
        do {
          k++;
          parent2 = (Solution) selection.execute(archive);
        } while (k < IBEA.TOURNAMENTS_ROUNDS);

        List<Solution<?>> parents = new ArrayList<>(2);
        parents.add(parent1) ;
        parents.add(parent2) ;

        //make the crossover
        List<Solution> offspring  = (List<Solution>) crossover.execute(parents);
        mutation.execute(offspring.get(0));
        problem.evaluate(offspring.get(0));
        //problem.evaluateConstraints(offSpring[0]);
        offSpringSolutionSet.add(offspring.get(0));
        evaluations++;
      }
      solutionSet = offSpringSolutionSet;
    }
  }

  @Override
  public List<Solution> getResult() {
    return new SolutionListUtils().findNondominatedSolutions(archive) ;
  }

  /**
   * Calculates the hypervolume of that portion of the objective space that
   * is dominated by individual a but not by individual b
   */
  double calculateHypervolumeIndicator(Solution<?> solutionA,
                                       Solution<?> solutionB,
                                       int d,
                                       double maximumValues[],
                                       double minimumValues[]) {
    double a, b, r, max;
    double volume = 0;
    double rho = 2.0;

    r = rho * (maximumValues[d - 1] - minimumValues[d - 1]);
    max = minimumValues[d - 1] + r;

    a = solutionA.getObjective(d - 1);
    if (solutionB == null) {
      b = max;
    } else {
      b = solutionB.getObjective(d - 1);
    }

    if (d == 1) {
      if (a < b) {
        volume = (b - a) / r;
      } else {
        volume = 0;
      }
    } else {
      if (a < b) {
        volume = calculateHypervolumeIndicator(solutionA, null, d - 1, maximumValues, minimumValues) *
                (b - a) / r;
        volume += calculateHypervolumeIndicator(solutionA, solutionB, d - 1, maximumValues, minimumValues) *
                (max - b) / r;
      } else {
        volume = calculateHypervolumeIndicator(solutionA, solutionB, d - 1, maximumValues, minimumValues) *
                (max - a) / r;
      }
    }

    return (volume);
  }

  /**
   * This structure stores the indicator values of each pair of elements
   */
  public void computeIndicatorValuesHD(List<Solution> solutionSet,
                                       double[] maximumValues,
                                       double[] minimumValues) {
    List<Solution> A, B;
    // Initialize the structures
    indicatorValues = new ArrayList<List<Double>>();
    maxIndicatorValue = -Double.MAX_VALUE;

    for (int j = 0; j < solutionSet.size(); j++) {
      A = new ArrayList<>(1);
      A.add(solutionSet.get(j));

      List<Double> aux = new ArrayList<Double>();
      for (int i = 0; i < solutionSet.size(); i++) {
        B = new ArrayList<>(1);
        B.add(solutionSet.get(i));

        int flag = (new DominanceComparator()).compare(A.get(0), B.get(0));

        double value = 0.0;
        if (flag == -1) {
          value = -calculateHypervolumeIndicator(A.get(0), B.get(0), problem.getNumberOfObjectives(),
                  maximumValues, minimumValues);
        } else {
          value = calculateHypervolumeIndicator(B.get(0), A.get(0), problem.getNumberOfObjectives(),
                  maximumValues, minimumValues);
        }

        //Update the max value of the indicator
        if (Math.abs(value) > maxIndicatorValue) {
          maxIndicatorValue = Math.abs(value);
        }
        aux.add(value);
      }
      indicatorValues.add(aux);
    }
  }

  /**
   * Calculate the fitness for the individual at position pos
   */
  public void fitness(List<Solution> solutionSet, int pos) {
    double fitness = 0.0;
    double kappa = 0.05;

    for (int i = 0; i < solutionSet.size(); i++) {
      if (i != pos) {
        fitness += Math.exp((-1 * indicatorValues.get(i).get(pos) / maxIndicatorValue) / kappa);
      }
    }
    solutionFitness.setAttribute(solutionSet.get(pos), fitness);
  }

  /**
   * Calculate the fitness for the entire population.
   */
  public void calculateFitness(List<Solution> solutionSet) {
    // Obtains the lower and upper bounds of the population
    double[] maximumValues = new double[problem.getNumberOfObjectives()];
    double[] minimumValues = new double[problem.getNumberOfObjectives()];

    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      maximumValues[i] = -Double.MAX_VALUE;
      minimumValues[i] = Double.MAX_VALUE;
    }

    for (int pos = 0; pos < solutionSet.size(); pos++) {
      for (int obj = 0; obj < problem.getNumberOfObjectives(); obj++) {
        double value = solutionSet.get(pos).getObjective(obj);
        if (value > maximumValues[obj]) {
          maximumValues[obj] = value;
        }
        if (value < minimumValues[obj]) {
          minimumValues[obj] = value;
        }
      }
    }

    computeIndicatorValuesHD(solutionSet, maximumValues, minimumValues);
    for (int pos = 0; pos < solutionSet.size(); pos++) {
      fitness(solutionSet, pos);
    }
  }

  /**
   * Update the fitness before removing an individual
   */
  public void removeWorst(List<Solution> solutionSet) {

    // Find the worst;
    double worst = (double) solutionFitness.getAttribute(solutionSet.get(0));
    int worstIndex = 0;
    double kappa = 0.05;

    for (int i = 1; i < solutionSet.size(); i++) {
      if ((double)solutionFitness.getAttribute(solutionSet.get(i)) > worst) {
        worst = (double)solutionFitness.getAttribute(solutionSet.get(i)) ;
        worstIndex = i;
      }
    }

    // Update the population
    for (int i = 0; i < solutionSet.size(); i++) {
      if (i != worstIndex) {
        double fitness = (double)solutionFitness.getAttribute(solutionSet.get(i)) ;
        fitness -=
                Math.exp((-indicatorValues.get(worstIndex).get(i) / maxIndicatorValue) / kappa);
        solutionFitness.setAttribute(solutionSet.get(i), fitness);
      }
    }

    // remove worst from the indicatorValues list
    indicatorValues.remove(worstIndex);
    for (List<Double> anIndicatorValues_ : indicatorValues) {
      anIndicatorValues_.remove(worstIndex);
    }

    solutionSet.remove(worstIndex);
  }
}
