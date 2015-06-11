package org.uma.jmetal.algorithm.multiobjective.abyss;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.EqualSolutionsComparator;
import org.uma.jmetal.util.comparator.StrengthFitnessComparator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.DistanceToSolutionListAttribute;
import org.uma.jmetal.util.solutionattribute.impl.MarkAttribute;
import org.uma.jmetal.util.solutionattribute.impl.StrengthRawFitness;

import javax.management.JMException;
import java.util.*;

/**
 * Created by ajnebro on 11/6/15.
 */
public class ABYSS extends AbstractABYSS<DoubleSolution> {
  protected JMetalRandom randomGenerator ;

  /**
   * These variables are used in the diversification method.
   */
  protected int numberOfSubRanges;
  protected int[] sumOfFrequencyValues;
  protected int[] sumOfReverseFrequencyValues;
  protected int[][] frequency;
  protected int[][] reverseFrequency;

  protected StrengthRawFitness strengthRawFitness; //TODO: invert this dependency
  protected Comparator<Solution> fitnessComparator; //TODO: invert this dependency
  protected MarkAttribute marked;
  protected DistanceToSolutionListAttribute distanceToSolutionListAttribute;
  //protected CrossoverOperator crossoverOperator;
  protected Comparator<Solution> dominanceComparator;
  protected Comparator<Solution> equalComparator;
  protected Comparator<Solution> crowdingDistanceComparator;

  public ABYSS(DoubleProblem problem, int maxEvaluations, int populationSize, int referenceSet1Size,
      int referenceSet2Size, int archiveSize, Archive<DoubleSolution> archive,
      LocalSearchOperator<DoubleSolution> localSearch,
      CrossoverOperator<List<DoubleSolution>, List<DoubleSolution>> crossoverOperator,
      int numberOfSubRanges) {
    super(problem, maxEvaluations, populationSize, referenceSet1Size, referenceSet2Size, archiveSize,
        archive, localSearch, crossoverOperator) ;

    this.numberOfSubRanges = numberOfSubRanges ;

    this.localSearch = localSearch ;
    this.archive = archive ;

    randomGenerator = JMetalRandom.getInstance() ;

    sumOfFrequencyValues       = new int[problem.getNumberOfVariables()] ;
    sumOfReverseFrequencyValues = new int[problem.getNumberOfVariables()] ;
    frequency       = new int[numberOfSubRanges][problem.getNumberOfVariables()] ;
    reverseFrequency = new int[numberOfSubRanges][problem.getNumberOfVariables()] ;

    strengthRawFitness = new StrengthRawFitness() ;
    fitnessComparator = new StrengthFitnessComparator();
    marked = new MarkAttribute();
    distanceToSolutionListAttribute = new DistanceToSolutionListAttribute();
    crowdingDistanceComparator = new CrowdingDistanceComparator();

    dominanceComparator = new DominanceComparator();
    equalComparator = new EqualSolutionsComparator();

    evaluations = 0 ;
  }

  @Override protected DoubleSolution diversificationGeneration() {
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

      double low = ((DoubleProblem)problem).getLowerBound(i) + range *
          (((DoubleProblem)problem).getUpperBound(i) -
              ((DoubleProblem)problem).getLowerBound(i)) / numberOfSubRanges;
      double high = low + (((DoubleProblem)problem).getUpperBound(i) -
          ((DoubleProblem)problem).getLowerBound(i)) / numberOfSubRanges;

      value = randomGenerator.nextDouble(low, high);
      solution.setVariableValue(i, value);
    }
    return solution;
  }

  @Override public void referenceSetUpdate() {
    buildNewReferenceSet1() ;
    buildNewReferenceSet2();
  }

  @Override public void referenceSetUpdate(DoubleSolution solution) {
    if (refSet1Test(solution)) {
      for (int indSet2 = 0; indSet2 < referenceSet2.size(); indSet2++) {
        double aux = SolutionUtils.distanceBetweenSolutions(solution,
            referenceSet2.get(indSet2));
        DoubleSolution auxSolution = referenceSet2.get(indSet2);
        if (aux < distanceToSolutionListAttribute.getAttribute(auxSolution)) {
          distanceToSolutionListAttribute.setAttribute(auxSolution, aux);
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
    strengthRawFitness.computeDensityEstimator(getPopulation());
    Collections.sort(getPopulation(), fitnessComparator);

    for (int i = 0; i < referenceSet1Size; i++) {
      individual = getPopulation().get(0);
      getPopulation().remove(0);
      marked.setAttribute(individual, false);
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
        double aux = SolutionUtils.distanceBetweenSolutions(getPopulation().get(j), individual);

        if (aux < distanceToSolutionListAttribute.getAttribute(individual)) {
          Solution auxSolution = getPopulation().get(j);
          distanceToSolutionListAttribute.setAttribute(auxSolution, aux);
        }
      }

      // Insert the individual into REFSET2
      marked.setAttribute(individual, false);
      referenceSet2.add(individual);

      // Update distances in REFSET2
      for (int j = 0; j < referenceSet2.size(); j++) {
        for (int k = 0; k < referenceSet2.size(); k++) {
          if (i != j) {
            double aux = SolutionUtils.distanceBetweenSolutions(referenceSet2.get(j), referenceSet2.get(k));
            Solution auxSolution = referenceSet2.get(j);
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
      if (flag == -1) {
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
      marked.setAttribute(solution, false);
      if (referenceSet1.size() < referenceSet1Size) {
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
   * Tries to update the reference set 2 with a <code>Solution</code>
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
        aux = SolutionUtils.distanceBetweenSolutions(referenceSet2.get(j), solution);
        if (aux < distanceToSolutionListAttribute.getAttribute(referenceSet2.get(j))) {
          distanceToSolutionListAttribute.setAttribute(referenceSet2.get(j), aux);
        }
      }
      marked.setAttribute(solution, false);
      referenceSet2.add(solution);
      return true;
    }
    return false;
  }

  @Override
  public boolean restartConditionIsFulfilled() {
    boolean allTheSolutionsAreMarked = true ;

    int i = 0 ;
    while ((i < referenceSet1.size()) && allTheSolutionsAreMarked) {
      if (marked.getAttribute(referenceSet1.get(i))) {
        i++ ;
      } else {
        allTheSolutionsAreMarked = false ;
      }
    }

    i = 0 ;
    while ((i < referenceSet2.size()) && allTheSolutionsAreMarked) {
      if (marked.getAttribute(referenceSet2.get(i))) {
        i++ ;
      } else {
        allTheSolutionsAreMarked = false ;
      }
    }

    return allTheSolutionsAreMarked ;
  }

  /**
   *
   * @return
   */
  @Override
  public List<List<DoubleSolution>> subsetGeneration() {
    List<List<DoubleSolution>> solutionGroupsList ;

    solutionGroupsList = generatePairsFromSolutionList(referenceSet1) ;
    solutionGroupsList.addAll(generatePairsFromSolutionList(referenceSet2));

    for (List<DoubleSolution> pair : solutionGroupsList) {
      marked.setAttribute(pair.get(0), true);
      marked.setAttribute(pair.get(1), true);
    }

    return solutionGroupsList ;
  }

  /**
   * Generate all pair combinations of the referenceSet1
   */
  public List<List<DoubleSolution>> generatePairsFromSolutionList(List<DoubleSolution> solutionList) {
    List<List<DoubleSolution>> subset = new ArrayList<>() ;
    for (int i = 0; i < solutionList.size(); i++) {
      DoubleSolution solution1 = solutionList.get(i);
      for (int j = i + 1; j < solutionList.size(); j++) {
        DoubleSolution solution2 = solutionList.get(j);

        List<DoubleSolution> pair = new ArrayList<>(2) ;
        pair.add(solution1);
        pair.add(solution2);
        subset.add(pair);
      }
    }

    return subset ;
  }

  @Override
  public List<DoubleSolution> solutionCombination(List<List<DoubleSolution>> solutionList) {
    List<DoubleSolution> resultList = new ArrayList<>() ;
    for (List<DoubleSolution> pair : solutionList) {
      List<DoubleSolution> offspring = (List<DoubleSolution>) crossover.execute(pair);
      if (problem instanceof ConstrainedProblem) {
        ((ConstrainedProblem) problem).evaluateConstraints(offspring.get(0));
        ((ConstrainedProblem) problem).evaluateConstraints(offspring.get(1));
      }

      problem.evaluate(offspring.get(0));
      problem.evaluate(offspring.get(1));
      evaluations += 2;
      //if (evaluations < maxEvaluations) {
        resultList.add(offspring.get(0));
        resultList.add(offspring.get(1));
      //}
    }

    return resultList;
  }

  @Override
  protected void restart() {
    getPopulation().clear();
    addReferenceSet1ToPopulation() ;
    updatePopulationWithArchive() ;
    fillPopulationWithRandomSolutions() ;
  }

  private void addReferenceSet1ToPopulation() {
    for (int i = 0; i < referenceSet1.size(); i++) {
      DoubleSolution solution = referenceSet1.get(i);
      marked.setAttribute(solution, false);

      getPopulation().add(improvement(solution));
    }
    referenceSet1.clear();
    referenceSet2.clear();
  }

  private void updatePopulationWithArchive() {
    CrowdingDistanceArchive<DoubleSolution> crowdingArchive ;
    crowdingArchive = (CrowdingDistanceArchive<DoubleSolution>)archive ;
    crowdingArchive.computeDistance(); // TODO: FIX this dependence

    Collections.sort(crowdingArchive.getSolutionList(),crowdingDistanceComparator);

    int insert = getPopulationSize() / 2;

    if (insert > crowdingArchive.getSolutionList().size())
      insert = crowdingArchive.getSolutionList().size();

    if (insert > (getPopulationSize() - getPopulation().size()))
      insert = getPopulationSize() - getPopulation().size();

    for (int i = 0; i < insert; i++) {
      DoubleSolution solution = (DoubleSolution) crowdingArchive.getSolutionList().get(i).copy();
      marked.setAttribute(solution,false);
      getPopulation().add(solution);
    }
  }

  private void fillPopulationWithRandomSolutions() {
    while (getPopulation().size() < getPopulationSize()) {
      DoubleSolution solution = diversificationGeneration();
      if(problem instanceof ConstrainedProblem){
        ((ConstrainedProblem)problem).evaluateConstraints(solution);
      }

      problem.evaluate(solution);
      evaluations++;
      solution = improvement(solution);

      marked.setAttribute(solution,false);
      getPopulation().add(solution);
    }
  }
}

