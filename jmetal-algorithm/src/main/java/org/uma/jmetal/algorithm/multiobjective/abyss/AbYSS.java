package org.uma.jmetal.algorithm.multiobjective.abyss;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.comparator.StrengthFitnessComparator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.DistanceToSolutionListAttribute;
import org.uma.jmetal.util.solutionattribute.impl.MarkAttribute;
import org.uma.jmetal.util.solutionattribute.impl.StrengthRawFitness;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
  protected CrossoverOperator crossoverOperator;


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

  @Override public void referenceSetUpdate(boolean firstTime) {
    if (firstTime) {
      buildNewReferenceSet1() ;
      buildNewReferenceSet2();
    } else { // Update the reference set from the subset generation result
      /*
      DoubleSolution individual;
      for (int i = 0; i < subSet.size(); i++) {

        individual = (DoubleSolution) improvementOperator.execute(subSet.get(i));
        evaluations += improvementOperator.getEvaluations();

        if (refSet1Test(individual)) { //Update distance of RefSet2
          for (int indSet2 = 0; indSet2 < refSet2.size(); indSet2++) {
            double aux = SolutionUtils.distanceBetweenSolutions(individual,
                refSet2.get(indSet2));
            DoubleSolution auxSolution = refSet2.get(indSet2);
            if (aux < distanceToSolutionListAttribute.getAttribute(auxSolution)) {
              distanceToSolutionListAttribute.setAttribute(auxSolution, aux);
            }// if
          } // for
        } else {
          refSet2Test(individual);
        } // if
      }

      subSet.clear();
*/
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

    return solutionGroupsList ;
  }

  /**
   * Generate all pair combinations of the referenceSet1
   */
  public List<List<DoubleSolution>> generatePairsFromSolutionList(List<DoubleSolution> solutionList) {
    List<DoubleSolution> pair = new ArrayList<>(2) ;
    List<List<DoubleSolution>> subset = new ArrayList<>() ;
    for (int i = 0; i < solutionList.size(); i++) {
      pair.add(solutionList.get(i));
      for (int j = i + 1; j < solutionList.size(); j++) {
        pair.add(1, solutionList.get(j));
        if (!marked.getAttribute(pair.get(0)) || !marked.getAttribute(pair.get(1))) {
          marked.setAttribute(pair.get(0), true);
          marked.setAttribute(pair.get(1), true);
          subset.add(pair);
        }
      }
    }

    return subset ;
  }
/*
  public List<DoubleSolution> generatePairsFromReferenceSet1() {
    List<DoubleSolution> parents = new ArrayList<>(2) ;
    List<DoubleSolution> subset = new ArrayList<>() ;
    for (int i = 0; i < referenceSet1.size(); i++) {
      parents.set(0, referenceSet1.get(i));
      for (int j = i + 1; j < referenceSet1.size(); j++) {
        parents.set(1, referenceSet1.get(j));
        if (!marked.getAttribute(parents.get(0)) || !marked.getAttribute(parents.get(1))) {
          List<DoubleSolution> offspring = (List<DoubleSolution>) crossoverOperator.execute(parents);
          problem.evaluate(offspring.get(0));
          problem.evaluate(offspring.get(1));
          if (problem instanceof ConstrainedProblem) {
            ((ConstrainedProblem) problem).evaluateConstraints(offspring.get(0));
            ((ConstrainedProblem) problem).evaluateConstraints(offspring.get(1));
          }
          evaluations += 2;
          if (evaluations < maxEvaluations) {
            subset.add(offspring.get(0));
            subset.add(offspring.get(1));
          }
          marked.setAttribute(parents.get(0), true);
          marked.setAttribute(parents.get(1), true);
        }
      }
    }

    return subset ;
  }

  /**
   * Generate all pair combination of the referenceSet2
   */
  /*
  public List<DoubleSolution> generatePairsFromReferenceSet2() {
    List<DoubleSolution> parents = new ArrayList<>(2);
    List<DoubleSolution> subset = new ArrayList<>();

    for (int i = 0; i < referenceSet2.size(); i++) {
      parents.set(0, referenceSet2.get(i));
      for (int j = i + 1; j < referenceSet2.size(); j++) {
        parents.set(1, referenceSet2.get(j));
        if (!marked.getAttribute(parents.get(0)) || !marked.getAttribute(parents.get(1))) {
          List<DoubleSolution> offspring = (List<DoubleSolution>) crossoverOperator.execute(parents);
          if (problem instanceof ConstrainedProblem) {
            ((ConstrainedProblem) problem).evaluateConstraints(offspring.get(0));
            ((ConstrainedProblem) problem).evaluateConstraints(offspring.get(1));
          }
          problem.evaluate(offspring.get(0));
          problem.evaluate(offspring.get(1));
          evaluations += 2;
          if (evaluations < maxEvaluations) {
            subset.add(offspring.get(0));
            subset.add(offspring.get(1));
          }
          marked.setAttribute(parents.get(0), true);
          marked.setAttribute(parents.get(1), true);
        }
      }
    }

    return subset ;
  }
*/
  @Override
  public List<DoubleSolution> solutionCombination(List<List<DoubleSolution>> solutionList) {
    List<DoubleSolution> resultList = new ArrayList<>() ;
    for (List<DoubleSolution> pair : solutionList) {
      List<DoubleSolution> offspring = (List<DoubleSolution>) crossoverOperator.execute(pair);
      if (problem instanceof ConstrainedProblem) {
        ((ConstrainedProblem) problem).evaluateConstraints(offspring.get(0));
        ((ConstrainedProblem) problem).evaluateConstraints(offspring.get(1));
      }

      problem.evaluate(offspring.get(0));
      problem.evaluate(offspring.get(1));
      evaluations += 2;
      if (evaluations < maxEvaluations) {
        resultList.add(offspring.get(0));
        resultList.add(offspring.get(1));
      }
    }

    return resultList;
  }

}

