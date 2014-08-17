//  CellDE.java
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

package org.uma.jmetal.metaheuristic.multiobjective.cellde;

import org.uma.jmetal.core.*;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.selection.BinaryTournament;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.util.Distance;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Neighborhood;
import org.uma.jmetal.util.Ranking;
import org.uma.jmetal.util.archive.SPEA2DensityArchive;
import org.uma.jmetal.util.comparator.CrowdingComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.Comparator;

/**
 * This class represents the original asynchronous MOCell algorithm
 * hybridized with Differential Evolution, called CellDE. It uses an
 * archive based on spea2 fitness to store non-dominated solutions, and it is
 * described in:
 * J.J. Durillo, A.J. Nebro, F. Luna, E. Alba "Solving Three-Objective
 * Optimization Problems Using a new Hybrid Cellular Genetic Algorithm".
 * PPSN'08. Dortmund. September 2008.
 */
public class CellDE extends Algorithm {
  private static final long serialVersionUID = 8699667515096532262L;

  private int populationSize ;
  private int archiveSize    ;
  private int maxEvaluations  ;
  private int numberOfFeedbackSolutionsFromArchive;

  private Crossover crossover ;
  private Selection selection ;

  private int evaluations ;
  private SolutionSet population;
  private SolutionSet archive;
  private SolutionSet[] neighbors;
  private Neighborhood neighborhood;

  /** Constructor */
  private CellDE(Builder builder) {
    this.problem = builder.problem ;

    this.populationSize = builder.populationSize ;
    this.archiveSize = builder.archiveSize ;
    this.maxEvaluations = builder.maxEvaluations ;
    this.numberOfFeedbackSolutionsFromArchive = builder.numberOfFeedbackSolutionsFromArchive;

    this.crossover = builder.crossover ;
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

  public int getNumberOfFeedbackSolutionsFromArchive() {
    return numberOfFeedbackSolutionsFromArchive;
  }

  public Crossover getCrossover() {
    return crossover;
  }

  public Selection getSelection() {
    return selection;
  }

  /** Builder class */
  public static class Builder {
    private Problem problem ;
    private int populationSize = 100 ;
    private int archiveSize = 100 ;
    private int maxEvaluations = 25000 ;
    private int numberOfFeedbackSolutionsFromArchive = 20 ;

    private Crossover crossover ;
    private Selection selection ;

    public Builder(Problem problem) {
      this.problem = problem ;

      crossover = new DifferentialEvolutionCrossover.Builder()
              .cr(0.5)
              .f(0.5).
                      build() ;
      selection = new BinaryTournament.Builder()
              .build() ;
    }

    public Builder populationSize(int populationSize) {
      this.populationSize = populationSize ;

      return this ;
    }

    public Builder archiveSize(int archiveSize) {
      this.archiveSize = archiveSize ;

      return this ;
    }

    public Builder maxEvaluations(int maxEvaluations) {
      this.maxEvaluations = maxEvaluations ;

      return this ;
    }

    public Builder numberOfFeedbackSolutionsFromArchive(int archiveFeedback) {
      this.numberOfFeedbackSolutionsFromArchive = archiveFeedback ;

      return this ;
    }

    public Builder crossover (Crossover crossover) {
      this.crossover = crossover ;

      return this ;
    }

    public Builder selection(Selection selection) {
      this.selection = selection ;

      return this ;
    }

    public CellDE build() {
      return new CellDE(this) ;
    }
  }

  /** Execute() method */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    Comparator<Solution> dominance = new DominanceComparator();
    Comparator<Solution> crowding = new CrowdingComparator();

    Distance distance = new Distance();

    //Initialize the variables    
    population = new SolutionSet(populationSize);
    archive = new SPEA2DensityArchive(archiveSize);
    neighborhood = new Neighborhood(populationSize);
    neighbors = new SolutionSet[populationSize];

    evaluations = 0;

/*    //Create the initial population
    for (int i = 0; i < populationSize; i++) {
      Solution solution = new Solution(problem);
      problem.evaluate(solution);
      problem.evaluateConstraints(solution);
      population.add(solution);
      solution.setLocation(i);
      evaluations++;
    }
*/
    createInitialPopulation();
    population = evaluatePopulation(population) ;
    evaluations = population.size() ;

    while (evaluations < maxEvaluations) {
      for (int ind = 0; ind < population.size(); ind++) {
        Solution individual = new Solution(population.get(ind));

        Solution[] parents = new Solution[3];
        Solution offSpring;

        neighbors[ind] = neighborhood.getEightNeighbors(population, ind);

        //parents
        parents[0] = (Solution) selection.execute(neighbors[ind]);
        parents[1] = (Solution) selection.execute(neighbors[ind]);
        parents[2] = individual;

        //Create a new solution, using genetic operator mutation and crossover
        offSpring = (Solution) crossover.execute(new Object[] {individual, parents});

        //->Evaluate offspring and constraints
        problem.evaluate(offSpring);
        problem.evaluateConstraints(offSpring);
        evaluations++;

        int flag = dominance.compare(individual, offSpring);

        if (flag == 1) { //The offSpring dominates
          offSpring.setLocation(individual.getLocation());
          population.replace(ind, new Solution(offSpring));
          archive.add(new Solution(offSpring));
        } else if (flag == 0) {
          //Both two are non-dominates
          neighbors[ind].add(offSpring);
          Ranking rank = new Ranking(neighbors[ind]);
          for (int j = 0; j < rank.getNumberOfSubfronts(); j++) {
            distance
                    .crowdingDistanceAssignment(rank.getSubfront(j));
          }

          boolean deleteMutant = true;
          int compareResult = crowding.compare(individual, offSpring);
          if (compareResult == 1) {
            deleteMutant = false;
          }

          if (!deleteMutant) {
            offSpring.setLocation(individual.getLocation());
            population.replace(offSpring.getLocation(), offSpring);
          }
          archive.add(new Solution(offSpring));
        }
      }

      //Store a portion of the archive into the population
      for (int j = 0; j < numberOfFeedbackSolutionsFromArchive; j++) {
        if (archive.size() > j) {
          int r = PseudoRandom.randInt(0, population.size() - 1);
          if (r < population.size()) {
            Solution individual = archive.get(j);
            individual.setLocation(r);
            population.replace(r, new Solution(individual));
          }
        }
      }
    }
    return archive;
  }

  private void createInitialPopulation() throws ClassNotFoundException, JMetalException {
    population = new SolutionSet(populationSize);

    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem);
      newSolution.setLocation(i);
      population.add(newSolution);
    }
  }

  private SolutionSet evaluatePopulation(SolutionSet population) throws JMetalException {
    for (int i = 0; i < populationSize; i++) {
      problem.evaluate(population.get(i));
      problem.evaluateConstraints(population.get(i));
    }
    return population ;
  }

} 
