package org.uma.jmetal.algorithm.multiobjective.abyss;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.management.JMException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.algorithm.impl.AbstractScatterSearch;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.localsearch.LocalSearchOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.comparator.EqualSolutionsComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.StrenghtRawFitnessDensityEstimator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.DistanceToSolutionListAttribute;

/**
 * This class implements the AbYSS algorithm, a multiobjective scatter search metaheuristics,
 * which is described in:
 *   A.J. Nebro, F. Luna, E. Alba, B. Dorronsoro, J.J. Durillo, A. Beham
 *   "AbYSS: Adapting Scatter Search to Multiobjective Optimization." IEEE Transactions on
 *   Evolutionary Computation. Vol. 12, No. 4 (August 2008), pp. 439-457
 *
 *   @author Antonio J. Nebro <antonio@lcc.uma.es>
 *   @author Cristobal Barba
 */
@SuppressWarnings("serial")
public class ABYSS extends AbstractScatterSearch<DoubleSolution, List<DoubleSolution>> {
  public static final String SOLUTION_IS_MARKED = "MARKED_ATTRIBUTE" ;

  protected final int maxEvaluations ;
  protected final Problem<DoubleSolution> problem;

  protected final int referenceSet1Size ;
  protected final int referenceSet2Size ;
  protected List<DoubleSolution> referenceSet1 ;
  protected List<DoubleSolution> referenceSet2 ;

  protected final int archiveSize ;
  protected Archive<DoubleSolution> archive ;

  protected LocalSearchOperator<DoubleSolution> localSearch ;
  protected CrossoverOperator<DoubleSolution> crossover ;
  protected int evaluations;
  protected JMetalRandom randomGenerator ;

  /**
   * These variables are used in the diversification method.
   */
  protected int numberOfSubRanges;
  protected int[] sumOfFrequencyValues;
  protected int[] sumOfReverseFrequencyValues;
  protected int[][] frequency;
  protected int[][] reverseFrequency;

  protected StrenghtRawFitnessDensityEstimator<DoubleSolution> densityEstimator ;
  protected Comparator<DoubleSolution> fitnessComparator; //TODO: invert this dependency
  protected DistanceToSolutionListAttribute distanceToSolutionListAttribute;
  protected Comparator<DoubleSolution> dominanceComparator;
  protected Comparator<DoubleSolution> equalComparator;
  protected Comparator<DoubleSolution> crowdingDistanceComparator;

  public ABYSS(@NotNull DoubleProblem problem, int maxEvaluations, int populationSize, int referenceSet1Size,
               int referenceSet2Size, int archiveSize, Archive<DoubleSolution> archive,
               LocalSearchOperator<DoubleSolution> localSearch,
               CrossoverOperator<DoubleSolution> crossoverOperator,
               int numberOfSubRanges) {

    setPopulationSize(populationSize);

    this.problem = problem ;
    this.maxEvaluations = maxEvaluations ;
    this.referenceSet1Size = referenceSet1Size ;
    this.referenceSet2Size = referenceSet2Size ;
    this.archiveSize = archiveSize ;
    this.archive = archive ;
    this.localSearch = localSearch ;
    this.crossover = crossoverOperator ;

    referenceSet1 = new ArrayList<>(referenceSet1Size) ;
    referenceSet2 = new ArrayList<>(referenceSet2Size) ;

    this.numberOfSubRanges = numberOfSubRanges ;

    randomGenerator = JMetalRandom.getInstance() ;

    sumOfFrequencyValues       = new int[problem.getNumberOfVariables()] ;
    sumOfReverseFrequencyValues = new int[problem.getNumberOfVariables()] ;
    frequency       = new int[numberOfSubRanges][problem.getNumberOfVariables()] ;
    reverseFrequency = new int[numberOfSubRanges][problem.getNumberOfVariables()] ;

    densityEstimator = new StrenghtRawFitnessDensityEstimator<>(1) ;
    fitnessComparator = densityEstimator.getComparator();
    distanceToSolutionListAttribute = new DistanceToSolutionListAttribute();

    crowdingDistanceComparator = new CrowdingDistanceDensityEstimator<DoubleSolution>().getComparator() ;

    dominanceComparator = new DominanceWithConstraintsComparator<>();
    equalComparator = new EqualSolutionsComparator<>();

    evaluations = 0 ;
  }

  @Override public boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations ;
  }

  @Override public DoubleSolution improvement(DoubleSolution solution) {
    DoubleSolution improvedSolution = localSearch.execute(solution) ;
    evaluations += localSearch.getNumberOfEvaluations() ;

    return improvedSolution ;
  }

  @Override public List<DoubleSolution> getResult() {
    return archive.getSolutionList();
  }

  @Override public DoubleSolution diversificationGeneration() {
    DoubleSolution solution = problem.createSolution();

    double value;
    int range;

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      sumOfReverseFrequencyValues[i] = 0;
      for (int j = 0; j < numberOfSubRanges; j++) {
        reverseFrequency[j][i] = sumOfFrequencyValues[i] - frequency[j][i];
        sumOfReverseFrequencyValues[i] += reverseFrequency[j][i];
      }

      if (sumOfReverseFrequencyValues[i] == 0) {
        range = randomGenerator.nextInt(0, numberOfSubRanges - 1);
      } else {
        value = randomGenerator.nextInt(0, sumOfReverseFrequencyValues[i] - 1);
        range = 0;
        while (value > reverseFrequency[range][i]) {
          value -= reverseFrequency[range][i];
          range++;
        }
      }

      frequency[range][i]++;
      sumOfFrequencyValues[i]++;

      Bounds<Double> bounds = ((DoubleProblem)problem).getVariableBounds().get(i) ;
      Double lowerBound = bounds.getLowerBound() ;
      Double upperBound = bounds.getUpperBound() ;
      double low = lowerBound + range * (upperBound - lowerBound) / numberOfSubRanges ;
      double high = low + (upperBound - lowerBound) / numberOfSubRanges ;

      value = randomGenerator.nextDouble(low, high);
      solution.variables().set(i, value);
    }

    problem.evaluate(solution);
    evaluations ++ ;
    return solution;
  }

  /**
   * Build the reference set after the initialization phase
   */
  @Override public void referenceSetUpdate() {
    buildNewReferenceSet1() ;
    buildNewReferenceSet2();
  }

  /**
   * Update the reference set with a new solution
   * @param solution
   */
  @Override public void referenceSetUpdate(DoubleSolution solution) {
    if (refSet1Test(solution)) {
      for (@NotNull DoubleSolution solutionInRefSet2 : referenceSet2) {
        double aux = SolutionUtils.distanceBetweenSolutionsInObjectiveSpace(solution, solutionInRefSet2);
        if (aux < distanceToSolutionListAttribute.getAttribute(solutionInRefSet2)) {
          distanceToSolutionListAttribute.setAttribute(solutionInRefSet2, aux);
        }
      }
    } else {
      refSet2Test(solution);
    }
  }

  /**
   * Build the referenceSet1 by moving the best referenceSet1Size individuals, according to
   * a fitness comparator, from the population to the referenceSet1
   */
  public void buildNewReferenceSet1() {
    DoubleSolution individual;
    densityEstimator.compute(getPopulation());
    getPopulation().sort(fitnessComparator);

    for (int i = 0; i < referenceSet1Size; i++) {
      individual = getPopulation().get(0);
      getPopulation().remove(0);
      individual.attributes().put(SOLUTION_IS_MARKED, false);
      referenceSet1.add(individual);
    }
  }

  /**
   * Build the referenceSet2 by moving to it the most diverse referenceSet2Size individuals from the
   * population in respect to the referenceSet1.
   *
   * The size of the referenceSet2 can be lower than referenceSet2Size depending on the current size
   * of the population
   */
  public void buildNewReferenceSet2() {
    for (int i = 0; i < getPopulation().size(); i++) {
      DoubleSolution individual = getPopulation().get(i);
      double distanceAux = SolutionUtils
          .distanceToSolutionListInSolutionSpace(individual, referenceSet1);
      distanceToSolutionListAttribute.setAttribute(individual, distanceAux);
    }

    int size = referenceSet2Size;
    if (getPopulation().size() < referenceSet2Size) {
      size = getPopulation().size();
    }

    for (int i = 0; i < size; i++) {
      // Find the maximumMinimumDistanceToPopulation
      double maxMinimum = 0.0;
      int index = 0;
      for (int j = 0; j < getPopulation().size(); j++) {

        DoubleSolution auxSolution = getPopulation().get(j);
        if (distanceToSolutionListAttribute.getAttribute(auxSolution) > maxMinimum) {
          maxMinimum = distanceToSolutionListAttribute.getAttribute(auxSolution);
          index = j;
        }
      }
      DoubleSolution individual = getPopulation().get(index);
      getPopulation().remove(index);

      // Update distances to REFSET in population
      for (int j = 0; j < getPopulation().size(); j++) {
        double aux = SolutionUtils.distanceBetweenSolutionsInObjectiveSpace(getPopulation().get(j), individual);

        if (aux < distanceToSolutionListAttribute.getAttribute(individual)) {
          DoubleSolution auxSolution = getPopulation().get(j);
          distanceToSolutionListAttribute.setAttribute(auxSolution, aux);
        }
      }

      // Insert the individual into REFSET2
      individual.attributes().put(SOLUTION_IS_MARKED, false);
      referenceSet2.add(individual);

      // Update distances in REFSET2
      for (int j = 0; j < referenceSet2.size(); j++) {
        for (DoubleSolution solution : referenceSet2) {
          if (i != j) {
            double aux = SolutionUtils.distanceBetweenSolutionsInObjectiveSpace(referenceSet2.get(j), solution);
            DoubleSolution auxSolution = referenceSet2.get(j);
            if (aux < distanceToSolutionListAttribute.getAttribute(auxSolution)) {
              distanceToSolutionListAttribute.setAttribute(auxSolution, aux);
            }
          }
        }
      }
    }
  }

  /**
   * Tries to update the reference set one with a solution
   *
   * @param solution The <code>Solution</code>
   * @return true if the <code>Solution</code> has been inserted, false
   * otherwise.
   */
  public boolean refSet1Test(DoubleSolution solution) {
    boolean dominated = false;
    int flag;
    int i = 0;
    while (i < referenceSet1.size()) {
      flag = dominanceComparator.compare(solution, referenceSet1.get(i));
      if (flag == -1) { //This is: solution dominates
        referenceSet1.remove(i);
      } else if (flag == 1) {
        dominated = true;
        i++;
      } else {
        flag = equalComparator.compare(solution, referenceSet1.get(i));
        if (flag == 0) {
          return true;
        }
        i++;
      }
    }

    if (!dominated) {
      solution.attributes().put(SOLUTION_IS_MARKED, false);
      if (referenceSet1.size() < referenceSet1Size) { //refSet1 isn't full
        referenceSet1.add(solution);
      } else {
        archive.add(solution);
      }
    } else {
      return false;
    }
    return true;
  }

  /**
   * Try to update the reference set 2 with a <code>Solution</code>
   *
   * @param solution The <code>Solution</code>
   * @return true if the <code>Solution</code> has been inserted, false
   * otherwise.
   * @throws JMException
   */
  public boolean refSet2Test(DoubleSolution solution) {
    if (referenceSet2.size() < referenceSet2Size) {
      double solutionAux = SolutionUtils.distanceToSolutionListInSolutionSpace(solution, referenceSet1);
      distanceToSolutionListAttribute.setAttribute(solution, solutionAux);
      double aux = SolutionUtils.distanceToSolutionListInSolutionSpace(solution, referenceSet2);
      if (aux < distanceToSolutionListAttribute.getAttribute(solution)) {
        distanceToSolutionListAttribute.setAttribute(solution, aux);
      }
      referenceSet2.add(solution);
      return true;
    }
    double auxDistance = SolutionUtils.distanceToSolutionListInSolutionSpace(solution, referenceSet1);
    distanceToSolutionListAttribute.setAttribute(solution, auxDistance);
    double aux = SolutionUtils.distanceToSolutionListInSolutionSpace(solution, referenceSet2);
    if (aux < distanceToSolutionListAttribute.getAttribute(solution)) {
      distanceToSolutionListAttribute.setAttribute(solution, aux);
    }
    double worst = 0.0;
    int index = 0;
    for (int i = 0; i < referenceSet2.size(); i++) {
      DoubleSolution auxSolution = referenceSet2.get(i);
      aux = distanceToSolutionListAttribute.getAttribute(auxSolution);
      if (aux > worst) {
        worst = aux;
        index = i;
      }
    }

    double auxDist = distanceToSolutionListAttribute.getAttribute(solution);
    if (auxDist < worst) {
      referenceSet2.remove(index);
      //Update distances in REFSET2
      for (int j = 0; j < referenceSet2.size(); j++) {
        aux = SolutionUtils.distanceBetweenSolutionsInObjectiveSpace(referenceSet2.get(j), solution);
        if (aux < distanceToSolutionListAttribute.getAttribute(referenceSet2.get(j))) {
          distanceToSolutionListAttribute.setAttribute(referenceSet2.get(j), aux);
        }
      }
      solution.attributes().put(SOLUTION_IS_MARKED, false);
      referenceSet2.add(solution);
      return true;
    }
    return false;
  }

  @Override
  public boolean restartConditionIsFulfilled(List<DoubleSolution> combinedSolutions) {
    return combinedSolutions.isEmpty() ;
  }

  /**
   * Subset generation method
   * @return
   */
  @Override
  public @NotNull List<List<DoubleSolution>> subsetGeneration() {
    List<List<DoubleSolution>> solutionGroupsList ;

    solutionGroupsList = generatePairsFromSolutionList(referenceSet1) ;

    solutionGroupsList.addAll(generatePairsFromSolutionList(referenceSet2));

    return solutionGroupsList ;
  }

  /**
   * Generate all pair combinations of the referenceSet1
   */
  public @NotNull List<List<DoubleSolution>> generatePairsFromSolutionList(@NotNull List<DoubleSolution> solutionList) {
    @NotNull List<List<DoubleSolution>> subset = new ArrayList<>() ;
    for (int i = 0; i < solutionList.size(); i++) {
      DoubleSolution solution1 = solutionList.get(i);
      for (int j = i + 1; j < solutionList.size(); j++) {
        DoubleSolution solution2 = solutionList.get(j);

        if (!(Boolean)solution1.attributes().get(SOLUTION_IS_MARKED)||
            !(Boolean)solution2.attributes().get(SOLUTION_IS_MARKED)) {
          List<DoubleSolution> pair = new ArrayList<>(2);
          pair.add(solution1);
          pair.add(solution2);
          subset.add(pair);

          solutionList.get(i).attributes().put(SOLUTION_IS_MARKED, true);
          solutionList.get(j).attributes().put(SOLUTION_IS_MARKED, true);
        }
      }
    }

    return subset ;
  }

  @Override
  public List<DoubleSolution> solutionCombination(List<List<DoubleSolution>> solutionList) {
    @NotNull List<DoubleSolution> resultList = new ArrayList<>() ;
    for (List<DoubleSolution> pair : solutionList) {
      List<DoubleSolution> offspring = crossover.execute(pair);

      problem.evaluate(offspring.get(0));
      problem.evaluate(offspring.get(1));
      evaluations += 2;
      resultList.add(offspring.get(0));
      resultList.add(offspring.get(1));
    }

    return resultList;
  }

  @Override
  public void restart() {
    getPopulation().clear();
    addReferenceSet1ToPopulation() ;
    updatePopulationWithArchive() ;
    fillPopulationWithRandomSolutions() ;
  }

  private void addReferenceSet1ToPopulation() {
    for (int i = 0; i < referenceSet1.size(); i++) {
      DoubleSolution solution = referenceSet1.get(i);
      solution = improvement(solution);

      solution.attributes().put(SOLUTION_IS_MARKED, true);

      getPopulation().add(solution);
    }
    referenceSet1.clear();
    referenceSet2.clear();
  }

  private void updatePopulationWithArchive() {
    CrowdingDistanceArchive<DoubleSolution> crowdingArchive ;
    crowdingArchive = (CrowdingDistanceArchive<DoubleSolution>)archive ;
    crowdingArchive.computeDensityEstimator();

    crowdingArchive.getSolutionList().sort(crowdingDistanceComparator);

    int insert = getPopulationSize() / 2;

    if (insert > crowdingArchive.getSolutionList().size())
      insert = crowdingArchive.getSolutionList().size();

    if (insert > (getPopulationSize() - getPopulation().size()))
      insert = getPopulationSize() - getPopulation().size();

    for (int i = 0; i < insert; i++) {
      @Nullable DoubleSolution solution = (DoubleSolution) crowdingArchive.getSolutionList().get(i).copy();
      solution.attributes().put(SOLUTION_IS_MARKED, false);
      getPopulation().add(solution);
    }
  }

  private void fillPopulationWithRandomSolutions() {
    while (getPopulation().size() < getPopulationSize()) {
      DoubleSolution solution = diversificationGeneration();

      problem.evaluate(solution);
      evaluations++;
      solution = improvement(solution);

      solution.attributes().put(SOLUTION_IS_MARKED, false);
      getPopulation().add(solution);
    }
  }

  @Override public String getName() {
    return "AbYSS" ;
  }

  @Override public @NotNull String getDescription() {
    return "Archived based hYbrid Scatter Search Algorithm" ;
  }
}

