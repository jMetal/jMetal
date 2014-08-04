//  MOCellTemplate
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

package org.uma.jmetal.metaheuristic.multiobjective.mocell;

import org.uma.jmetal.core.*;
import org.uma.jmetal.util.Distance;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Neighborhood;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.comparator.CrowdingComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.Comparator;

/**
 * Created by Antonio J. Nebro on 29/06/14.
 */
public abstract class MOCellTemplate extends Algorithm {
  private static final int DEFAULT_ARCHIVE_SIZE = 100 ;
  protected int populationSize;
  protected int maxEvaluations;
  protected int evaluations;
  protected int archiveSize ;
  protected int numberOfFeedbackSolutionsFromArchive ;

  protected SolutionSet population;
  protected SolutionSet offspringPopulation;

  protected Operator mutationOperator;
  protected Operator crossoverOperator;
  protected Operator selectionOperator;

  protected Archive archive;
  protected SolutionSet[] neighbors;
  protected Neighborhood neighborhood;

  protected Comparator dominanceComparator ;
  protected Comparator densityEstimatorComparator ;

  protected MOCellTemplate(Builder builder) {
    super() ;

    problem = builder.problem ;

    populationSize = builder.populationSize ;
    maxEvaluations = builder.maxEvaluations ;
    archiveSize = builder.archiveSize ;
    numberOfFeedbackSolutionsFromArchive = builder.numberOfFeedbackSolutionsFromArchive ;

    mutationOperator = builder.mutationOperator ;
    crossoverOperator = builder.crossoverOperator ;
    selectionOperator = builder.selectionOperator ;

    dominanceComparator = builder.dominanceComparator ;
    densityEstimatorComparator = builder.densityEstimatorComparator ;

    archive = builder.archive ;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public int getArchiveSize() {
    return archiveSize;
  }

  public int getNumberOfFeedbackSolutionsFromArchive() {
    return numberOfFeedbackSolutionsFromArchive;
  }

  public Operator getMutationOperator() {
    return mutationOperator;
  }

  public Operator getCrossoverOperator() {
    return crossoverOperator;
  }

  public Operator getSelectionOperator() {
    return selectionOperator;
  }

  protected void createInitialPopulation() throws ClassNotFoundException, JMetalException {
    population = new SolutionSet(populationSize);

    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem);
      newSolution.setLocation(i);
      population.add(newSolution);
    }
  }

  protected SolutionSet evaluatePopulation(SolutionSet population) throws JMetalException {
    for (int i = 0; i < populationSize; i++) {
      problem.evaluate(population.get(i));
      problem.evaluateConstraints(population.get(i));
    }
    return population ;
  }

  protected boolean stoppingCondition() {
    return evaluations >= maxEvaluations ;
  }

  protected void archiveFeedback() {
    Distance.crowdingDistance(archive);
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

  public static class Builder {
    protected Problem problem ;

    protected int populationSize;
    protected int maxEvaluations;
    protected int archiveSize ;
    protected int numberOfFeedbackSolutionsFromArchive ;

    protected Operator mutationOperator;
    protected Operator crossoverOperator;
    protected Operator selectionOperator;

    protected Comparator dominanceComparator ;
    protected Comparator densityEstimatorComparator ;

    protected Archive archive ;

    /** Builder class */
    public Builder(Problem problem) {
      this.problem = problem ;
      dominanceComparator = new DominanceComparator() ;
      densityEstimatorComparator = new CrowdingComparator();

      archiveSize = DEFAULT_ARCHIVE_SIZE ;
      numberOfFeedbackSolutionsFromArchive = 0 ;
    }

    public Builder populationSize(int populationSize) {
      this.populationSize = populationSize ;

      return this ;
    }

    public Builder maxEvaluations(int maxEvaluations) {
      this.maxEvaluations = maxEvaluations ;

      return this ;
    }

    public Builder archiveSize(int archiveSize) {
      this.archiveSize = archiveSize ;

      return this ;
    }

    public Builder numberOfFeedbackSolutionsFromArchive(int numberOfFeedbackSolutionsFromArchive) {
      this.numberOfFeedbackSolutionsFromArchive = numberOfFeedbackSolutionsFromArchive ;

      return this ;
    }

    public Builder crossover(Operator crossover) {
      crossoverOperator = crossover ;

      return this ;
    }

    public Builder mutation(Operator mutation) {
      mutationOperator = mutation ;

      return this ;
    }

    public Builder selection(Operator selection) {
      selectionOperator = selection ;

      return this ;
    }

    public MOCellTemplate build(String mocellVariant) {
      MOCellTemplate algorithm ;
      switch (mocellVariant) {
        case "AsyncMOCell1": algorithm = new AsyncMOCell1(this) ; break ;
        case "AsyncMOCell2": algorithm = new AsyncMOCell2(this) ; break ;
        case "AsyncMOCell3": algorithm = new AsyncMOCell3(this) ; break ;
        case "AsyncMOCell4": algorithm = new AsyncMOCell4(this) ; break ;
        case "SyncMOCell1": algorithm = new SyncMOCell1(this) ; break ;
        case "SyncMOCell2": algorithm = new SyncMOCell2(this) ; break ;
        default:
          throw new JMetalException("MOCell variant unknown: " + mocellVariant) ;
      }
      return algorithm ;
    }
  }
}
