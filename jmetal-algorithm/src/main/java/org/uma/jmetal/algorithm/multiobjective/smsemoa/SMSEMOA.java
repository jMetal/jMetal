package org.uma.jmetal.algorithm.multiobjective.smsemoa;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator2.impl.Hypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.HypervolumeContributorComparator;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;
import org.uma.jmetal.util.solutionattribute.impl.HypervolumeContribution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ajnebro on 17/4/15.
 */
public class SMSEMOA<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> {
  protected final int maxEvaluations;
  protected final int populationSize;
  protected final double offset ;

  protected final Problem<S> problem;

  protected int evaluations;

  private Hypervolume hypervolume;

  /**
   * Constructor
   */
  public SMSEMOA(Problem<S> problem, int maxEvaluations, int populationSize, double offset,
      CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator) {
    super() ;
    this.problem = problem;
    this.maxEvaluations = maxEvaluations;
    this.populationSize = populationSize;

    this.offset = offset ;

    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = selectionOperator;

    hypervolume = new Hypervolume() ;
  }

  @Override protected void initProgress() {
    evaluations = populationSize ;
  }

  @Override protected void updateProgress() {
    evaluations++ ;
  }

  @Override protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations ;
  }

  @Override protected List<S> createInitialPopulation() {
    List<S> population = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i++) {
      S newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override protected List<S> evaluatePopulation(List<S> population) {
    for (int i = 0 ; i < population.size(); i++) {
      problem.evaluate(population.get(i)) ;
    }
    return population ;
  }

  @Override protected List<S> selection(List<S> population) {
    List<S> matingPopulation = new ArrayList<>(2);
    for (int i = 0; i < 2; i++) {
      S solution = selectionOperator.execute(population);
      matingPopulation.add(solution);
    }

    return matingPopulation;
  }

  @Override protected List<S> reproduction(List<S> population) {
    List<S> offspringPopulation = new ArrayList<>(1);

    List<S> parents = new ArrayList<>(2);
    parents.add(population.get(0));
    parents.add(population.get(1));

    List<S> offspring = crossoverOperator.execute(parents);

    mutationOperator.execute(offspring.get(0));

    offspringPopulation.add(offspring.get(0));
    return offspringPopulation;
  }

  @Override protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population);
    jointPopulation.addAll(offspringPopulation);

    Ranking<S> ranking = computeRanking(jointPopulation);
    List<S> lastSubfront = ranking.getSubfront(ranking.getNumberOfSubfronts()-1) ;

    lastSubfront = computeHypervolumeContribution(lastSubfront, jointPopulation) ;

    List<S> resultPopulation = new ArrayList<>() ;
    for (int i = 0; i < ranking.getNumberOfSubfronts()-1; i++) {
      for (S solution : ranking.getSubfront(i)) {
        resultPopulation.add(solution);
      }
    }

    for (int i = 0; i < lastSubfront.size()-1; i++) {
      resultPopulation.add(lastSubfront.get(i)) ;
    }

    return resultPopulation ;
  }

  @Override public List<S> getResult() {
    return getPopulation();
  }

  protected Ranking<S> computeRanking(List<S> solutionList) {
    Ranking<S> ranking = new DominanceRanking<S>();
    ranking.computeRanking(solutionList);

    return ranking;
  }

  private List<S> computeHypervolumeContribution(List<S> lastFront, List<S> solutionList) {
    if (lastFront.size() > 1) {
      Front subFront = new ArrayFront((lastFront)) ;
      Front front = new ArrayFront(solutionList) ;

      // STEP 1. Obtain the maximum and minimum values of the Pareto front
      double[] maximumValues = FrontUtils.getMaximumValues(front) ;
      double[] minimumValues = FrontUtils.getMinimumValues(front) ;

      // STEP 2. Get the normalized front
      Front normalizedFront = FrontUtils.getNormalizedFront(subFront, maximumValues, minimumValues) ;

      // compute offsets for reference point in normalized space
      double[] offsets = new double[maximumValues.length];
      for (int i = 0; i < maximumValues.length; i++) {
        offsets[i] = offset / (maximumValues[i] - minimumValues[i]);
      }
      // STEP 3. Inverse the pareto front. This is needed because the original
      // metric by Zitzler is for maximization problem
      Front invertedFront = FrontUtils.getInvertedFront(normalizedFront);

      // shift away from origin, so that boundary points also get a contribution > 0
      for (int i = 0; i < invertedFront.getNumberOfPoints(); i++) {
        Point point = invertedFront.getPoint(i) ;

        for (int j = 0; j < point.getNumberOfDimensions(); j++) {
          point.setDimensionValue(j, point.getDimensionValue(j)+ offsets[j]);
        }
      }

      HypervolumeContribution<S> hvContribution = new HypervolumeContribution<S>() ;

      // calculate contributions and sort
      double[] contributions = hvContributions(FrontUtils.convertFrontToArray(invertedFront));
      for (int i = 0; i < contributions.length; i++) {
        hvContribution.setAttribute(lastFront.get(i), contributions[i]);
      }

      Collections.sort(lastFront, new HypervolumeContributorComparator());
      //      lastFront.sort(new HypervolumeContributorComparator());

    }
    return lastFront ;
  }

  /**
   * Calculates how much hypervolume each point dominates exclusively. The points
   * have to be transformed beforehand, to accommodate the assumptions of Zitzler's
   * hypervolume code.
   *
   * @param front transformed objective values
   * @return HV contributions
   */
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
}
