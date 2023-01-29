package org.uma.jmetal.algorithm.multiobjective.mosa;

import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mosa.cooling.CoolingScheme;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 * This class implements a multi-objective simulated annealing algorithm.
 */
public class MOSA<S extends Solution<?>> implements Algorithm<List<S>> {
  protected S currentSolution ;
  protected Problem<S> problem ;
  protected MutationOperator<S> mutationOperator ;

  protected int maxEvaluations;
  protected int evaluations;

  protected BoundedArchive<S> archive;
  protected Comparator<S> comparator;

  protected static final double minimumTemperature = 0.000001;
  protected double temperature;

  protected CoolingScheme coolingScheme;
  protected int numberOfWorstAcceptedSolutions = 0;

  /**
   * Constructor
   */
  public MOSA(
          S initialSolution,
          Problem<S> problem,
          int maxEvaluations,
          BoundedArchive<S> archive,
          MutationOperator<S> mutationOperator,
          double initialTemperature,
          CoolingScheme coolingScheme) {
    this.problem =problem ;
    this.currentSolution = initialSolution ;
    this.maxEvaluations = maxEvaluations;
    this.archive = archive;
    this.mutationOperator = mutationOperator;

    this.temperature = initialTemperature;
    this.coolingScheme = coolingScheme;

    comparator = new DefaultDominanceComparator<>() ;
  }
  /* Getters */
  public BoundedArchive<S> getArchive() {
    return archive;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public MutationOperator<S> getMutationOperator() {
    return mutationOperator;
  }

  protected void initProgress() {
    evaluations = 1;
  }

  protected void updateProgress() {
    evaluations++;
  }

  protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations;
  }

  @Override
  public void run() {
    initProgress();
    while (!isStoppingConditionReached()) {
      S mutatedSolution = mutationOperator.execute((S)currentSolution.copy()) ;
      problem.evaluate(mutatedSolution);

      int flag = comparator.compare(currentSolution, mutatedSolution);
      if (flag == 1) {
        currentSolution = mutatedSolution;
        archive.add(mutatedSolution);
      } else if (flag == 0) {
        if (archive.add(mutatedSolution)) {
          if (archive.comparator().compare(currentSolution, mutatedSolution) > 0) {
            currentSolution = mutatedSolution;
          }
        }
      } else {
        double acceptanceProbability = computeAcceptanceProbability(currentSolution,
            mutatedSolution, temperature);

        if (acceptanceProbability > JMetalRandom.getInstance().nextDouble()) {
          currentSolution = mutatedSolution;
          numberOfWorstAcceptedSolutions++;
        }
      }

      temperature = coolingScheme.updateTemperature(temperature, evaluations);

      updateProgress();
    }
  }

  protected double computeAcceptanceProbability(S currentSolution, S mutatedSolution, double temperature) {
    double value = 0.0;

    for (int i = 0; i < currentSolution.objectives().length; i++) {
      value += (mutatedSolution.objectives()[i] - currentSolution.objectives()[i])
              / Math.max(temperature, minimumTemperature);
    }

    double probability = Math.exp(-1.0 * value);
    Check.probabilityIsValid(probability);
    return probability;
  }

  public double getNumberOfWorstAcceptedSolutions() {
    return numberOfWorstAcceptedSolutions;
  }

  @Override
  public List<S> result() {
    return archive.solutions();
  }

  @Override
  public String name() {
    return "MOSA";
  }

  @Override
  public String description() {
    return "Multi-objective Simulated Annealing";
  }
}
