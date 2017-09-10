package org.uma.jmetal.algorithm.multiobjective.rnsgaii;

import org.uma.jmetal.algorithm.multiobjective.mombi.util.AbstractUtilityFunctionsSet;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.rnsgaii.util.ASFRNSGAII;
import org.uma.jmetal.algorithm.multiobjective.rnsgaii.util.RNSGAIIRanking;
import org.uma.jmetal.algorithm.multiobjective.wasfga.util.WeightVector;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.ArrayList;
import java.util.List;

public class RNSGAII<S extends Solution<?>> extends NSGAII<S> {

  protected List<Double> interestPoint = null;
  protected List<Double> lowerBounds, upperBounds;
  protected boolean estimateObjectivesBounds, normalization;
  protected double epsilon;
  final AbstractUtilityFunctionsSet<S> achievementScalarizingFunction;

  /**
   * Constructor
   */
  public RNSGAII(Problem<S> problem, int maxEvaluations, int populationSize, CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator, SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator, List<Double> referencePoint, boolean estimateObjectivesBounds, boolean normalization, double epsilon, List<Double> lowerBounds, List<Double> upperBounds) {
    super(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator);
    this.interestPoint = referencePoint;
    this.estimateObjectivesBounds = estimateObjectivesBounds;
    this.normalization = normalization;
    this.epsilon = epsilon;
    this.lowerBounds = lowerBounds;
    this.upperBounds = upperBounds;
    this.achievementScalarizingFunction = createUtilityFunction();
    if (this.estimateObjectivesBounds) {
      initializeBounds();
    }

  }

  public AbstractUtilityFunctionsSet<S> createUtilityFunction() {
    double[][] weights = WeightVector.initUniformWeights2D(0.005D, this.getMaxPopulationSize());
    weights = WeightVector.invertWeights(weights, true);
    ASFRNSGAII<S> aux = new ASFRNSGAII(weights, this.interestPoint);
    return aux;
  }

  private void initializeBounds() {
    this.lowerBounds = new ArrayList<>(getProblem().getNumberOfObjectives());
    this.upperBounds = new ArrayList<>(getProblem().getNumberOfObjectives());
    for (int i = 0; i < getProblem().getNumberOfObjectives(); i++) {
      this.lowerBounds.add(Double.MAX_VALUE);
      this.upperBounds.add(Double.MIN_VALUE);
    }
  }

  private void updateLowerBounds(Solution individual) {
    for (int i = 0; i < getProblem().getNumberOfObjectives(); i++) {
      if (individual.getObjective(i) < this.lowerBounds.get(i)) {
        this.lowerBounds.set(i, individual.getObjective(i));
      }
    }
  }

  private void updateUpperBounds(Solution individual) {
    for (int i = 0; i < getProblem().getNumberOfObjectives(); i++) {
      if (individual.getObjective(i) > this.upperBounds.get(i)) {
        this.upperBounds.set(i, individual.getObjective(i));
      }
    }
  }

  private void updateBounds(Solution individual) {
    this.updateLowerBounds(individual);
    this.updateUpperBounds(individual);
  }

  @Override
  protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population);
    jointPopulation.addAll(offspringPopulation);

    if (estimateObjectivesBounds) {
      updateBounds(offspringPopulation.get(0));
      updateBounds(offspringPopulation.get(1));
    }
    Ranking<S> ranking = computeRanking(jointPopulation);

    return preferenceDistanceSelection(ranking);//hay que cambiar el ranking

  }

  protected Ranking<S> computeRanking(List<S> solutionList) {
    Ranking<S> ranking = new RNSGAIIRanking<>(achievementScalarizingFunction, epsilon);
    ranking.computeRanking(solutionList);

    return ranking;
  }

  protected List<S> preferenceDistanceSelection(Ranking<S> ranking) {
    CrowdingDistance<S> crowdingDistance = new CrowdingDistance<S>();
    List<S> population = new ArrayList<>(getMaxPopulationSize());
    int rankingIndex = 0;
        /*
        while (populationIsNotFull(population)) {
            if (subfrontFillsIntoThePopulation(ranking, rankingIndex, population)) {
                addRankedSolutionsToPopulation(ranking, rankingIndex, population);
                rankingIndex++;
            } else {
                crowdingDistance.computeDensityEstimator(ranking.getSubfront(rankingIndex));
                addLastRankedSolutionsToPopulation(ranking, rankingIndex, population);
            }
        }
        */

    return population;
  }


}
