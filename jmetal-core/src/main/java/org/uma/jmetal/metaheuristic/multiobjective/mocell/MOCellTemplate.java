package org.uma.jmetal.metaheuristic.multiobjective.mocell;

import org.uma.jmetal.core.*;
import org.uma.jmetal.util.Distance;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Neighborhood;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.CrowdingArchive;
import org.uma.jmetal.util.comparator.CrowdingComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.Comparator;

/**
 * Created by Antonio J. Nebro on 29/06/14.
 */
public abstract class MOCellTemplate extends Algorithm {
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

    problem_ = builder.problem ;

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
      newSolution = new Solution(problem_);
      newSolution.setLocation(i);
      population.add(newSolution);
    }
  }

  protected SolutionSet evaluatePopulation(SolutionSet population) throws JMetalException {
    for (int i = 0; i < populationSize; i++) {
      problem_.evaluate(population.get(i));
      problem_.evaluateConstraints(population.get(i));
    }
    return population ;
  }

  protected boolean stoppingCondition() {
    return evaluations < maxEvaluations ;
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

    public Builder(Problem problem) {
      this.problem = problem ;
      dominanceComparator = new DominanceComparator() ;
      densityEstimatorComparator = new CrowdingComparator();

      archiveSize = 100 ;
      archive = new CrowdingArchive(archiveSize, problem.getNumberOfObjectives());
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
// QUE PASA CON EL ARCHIVO??
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
      MOCellTemplate algorithm = null ;
      switch (mocellVariant) {
        case "AsyncMOCell1": algorithm = new AsyncMOCell1(this) ; break ;
        case "AsyncMOCell2": algorithm = new AsyncMOCell2(this) ; break ;
      }
      /*
      if ("NSGAII".equals(NSGAIIVariant)) {
        algorithm = new NSGAII(this);
      } else if ("SteadyStateNSGAII".equals(NSGAIIVariant)) {
        algorithm =  new SteadyStateNSGAII(this) ;
      } else {
        throw new JMetalException(NSGAIIVariant + " variant unknown") ;
      }

*/

      return algorithm ;
    }
  }
}
