package org.uma.jmetal.algorithm.multiobjective.abyss;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.impl.localsearch.MutationLocalSearch;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * This class implements the AbYSS algorithm. This algorithm is an adaptation
 * of the single-objective scatter search template defined by F. Glover in:
 * F. Glover. "A template for scatter search and path relinking", Lecture Notes
 * in Computer Science, Springer Verlag, 1997. AbYSS is described in:
 *   A.J. Nebro, F. Luna, E. Alba, B. Dorronsoro, J.J. Durillo, A. Beham
 *   "AbYSS: Adapting Scatter Search to Multiobjective Optimization."
 *   IEEE Transactions on Evolutionary Computation. Vol. 12,
 *   No. 4 (August 2008), pp. 439-457
 */
public abstract class AbstractABYSS <S extends Solution<?>> implements Algorithm<List<? extends Solution<?>>> {
  /**
   * Stores the number of subranges in which each encodings.variable is divided. Used in
   * the diversification method. By default it takes the value 4 (see the method
   * <code>initParams</code>).
   */
  protected int numberOfSubranges;

  /**
   * These variables are used in the diversification method.
   */
  protected int[] sumOfFrequencyValues;
  protected int[] sumOfReverseFrequencyValues;
  protected int[][] frequency;
  protected int[][] reverseFrequency;

  /**
   * Stores the initial solution set
   */
  protected List<DoubleSolution> solutionSet;
  /**
   * Maximum number of solution allowed for the initial solution set
   */
  protected int solutionSetSize;


  /**
   * Stores the reference set one
   */
  protected List<DoubleSolution> refSet1;

  /**
   * Stores the reference set two
   */
  protected List<DoubleSolution> refSet2;
  /**
   * Stores the solutions provided by the subset generation method of the
   * scatter search template
   */
  protected List<DoubleSolution> subSet;
  /**
   * Maximum size of the reference set one
   */
  protected int refSet1Size;

  /**
   * Maximum size of the reference set two
   */
  protected int refSet2Size;
  /**
   * Problem
   */
  protected DoubleProblem problem;
  // protected ConstrainedProblem<DoubleSolution> problem;
  protected JMetalRandom randomGenerator;
  /**
   * Stores the improvement operator
   */
  protected MutationLocalSearch improvementOperator;
  /**
   * Fitness
   */
  protected StrengthRawFitness strenghtRawFitness;
  /**
   * Stores the comparators for dominance and equality, respectively
   */
  protected Comparator<Solution<?>> dominanceComparator;
  protected Comparator<Solution<?>> equalComparator;
  protected Comparator<Solution<?>> fitnessComparator;
  protected Comparator<Solution<?>> crowdingDistanceComparator;

  /**
   * Solution Marked Attributed
   */
  protected MarkAttribute marked;

  /**
   * Solution Distance To Solution List Attribute
   */
  protected DistanceToSolutionListAttribute distanceToSolutionListAttribute;
  /**
   * Stores the current number of performed getEvaluations
   */
  protected int evaluations;
  /**
   * Maximum size of the external archive
   */
  protected int archiveSize;
  /**
   * Stores the external solution archive
   */
  protected CrowdingDistanceArchive<DoubleSolution> archive;
  /**
   * Stores the crossover operator
   */
  protected CrossoverOperator crossoverOperator;
  /**
   * Maximum number of getEvaluations to carry out
   */
  protected int maxEvaluations;


  public AbstractABYSS(int numberOfSubranges,int solutionSetSize, int refSet1Size, int refSet2Size,
      int archiveSize, int maxEvaluations,Archive archive,
      CrossoverOperator crossoverOperator,MutationLocalSearch improvementOperator,
      DoubleProblem problem) {
    this.numberOfSubranges=numberOfSubranges;
    this.solutionSetSize = solutionSetSize;
    this.refSet1Size = refSet1Size;
    this.refSet2Size = refSet2Size;
    this.archiveSize = archiveSize;
    this.crossoverOperator = crossoverOperator;
    this.problem = problem;
    this.maxEvaluations = maxEvaluations;
    this.evaluations=0;
    randomGenerator = JMetalRandom.getInstance();
    strenghtRawFitness = new StrengthRawFitness();
    solutionSet = new ArrayList<DoubleSolution>(solutionSetSize);
    refSet1 = new ArrayList<DoubleSolution>(refSet1Size);
    refSet2 = new ArrayList<DoubleSolution>(refSet2Size);
    dominanceComparator = new DominanceComparator();
    equalComparator = new EqualSolutionsComparator();
    fitnessComparator = new StrengthFitnessComparator();
    crowdingDistanceComparator = new CrowdingDistanceComparator();
    this.improvementOperator = improvementOperator;
    marked = new MarkAttribute();
    distanceToSolutionListAttribute = new DistanceToSolutionListAttribute();
    subSet = new ArrayList<DoubleSolution>(solutionSetSize * 1000);
    this.archive = (CrowdingDistanceArchive)archive;
    sumOfFrequencyValues       = new int[problem.getNumberOfVariables()] ;
    sumOfReverseFrequencyValues = new int[problem.getNumberOfVariables()] ;
    frequency       = new int[numberOfSubranges][problem.getNumberOfVariables()] ;
    reverseFrequency = new int[numberOfSubranges][problem.getNumberOfVariables()] ;
  }

  /**
   * Returns a <code>Solution</code> using the diversification generation method
   * described in the scatter search template.
   *
   * @throws javax.management.JMException
   * @throws ClassNotFoundException
   */
  public DoubleSolution diversificationGeneration() throws JMException, ClassNotFoundException {

    DoubleSolution solution = problem.createSolution();

    double value;
    int range;

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      sumOfReverseFrequencyValues[i] = 0;
      for (int j = 0; j < numberOfSubranges; j++) {
        reverseFrequency[j][i] = sumOfFrequencyValues[i] - frequency[j][i];
        sumOfReverseFrequencyValues[i] += reverseFrequency[j][i];
      } // for

      if (sumOfReverseFrequencyValues[i] == 0) {
        range = randomGenerator.nextInt(0, numberOfSubranges - 1);
      } else {
        value = randomGenerator.nextInt(0, sumOfReverseFrequencyValues[i] - 1);
        range = 0;
        while (value > reverseFrequency[range][i]) {
          value -= reverseFrequency[range][i];
          range++;
        } // while
      } // else

      frequency[range][i]++;
      sumOfFrequencyValues[i]++;

      double low = problem.getLowerBound(i) + range * (problem.getUpperBound(i) -
          problem.getLowerBound(i)) / numberOfSubranges;
      double high = low + (problem.getUpperBound(i) -
          problem.getLowerBound(i)) / numberOfSubranges;

      value = randomGenerator.nextDouble(low, high);
      //solution.getDecisionVariables()[i].setValue(value);
      solution.setVariableValue(i, value);

    } // for
    return solution;
  } // diversificationGeneration


  /**
   * Implements the referenceSetUpdate method.
   *
   * @param build if true, indicates that the reference has to be build for the
   *              first time; if false, indicates that the reference set has to be
   *              updated with new solutions
   * @throws JMException
   */
  public void referenceSetUpdate(boolean build) throws JMException {
    if (build) { // Build a new reference set
      // STEP 1. Select the p best individuals of P, where p is refSet1Size_.
      //         Selection Criterium: Spea2Fitness
      DoubleSolution individual;
      strenghtRawFitness.computeDensityEstimator(solutionSet);
      Collections.sort(solutionSet, fitnessComparator);

      // STEP 2. Build the RefSet1 with these p individuals
      for (int i = 0; i < refSet1Size; i++) {
        individual = solutionSet.get(0);
        solutionSet.remove(0);
        marked.setAttribute(individual, false);
        refSet1.add(individual);
      }

      // STEP 3. Compute Euclidean distances in SolutionSet to obtain q
      //         individuals, where q is refSet2Size_
      for (int i = 0; i < solutionSet.size(); i++) {
        individual = solutionSet.get(i);
        double distanceAux = SolutionUtils.distanceToSolutionListInSolutionSpace(individual, refSet1);
        distanceToSolutionListAttribute.setAttribute(individual, distanceAux);
      }

      int size = refSet2Size;
      if (solutionSet.size() < refSet2Size) {
        size = solutionSet.size();
      }

      // STEP 4. Build the RefSet2 with these q individuals
      for (int i = 0; i < size; i++) {
        // Find the maximumMinimunDistanceToPopulation
        double maxMinimum = 0.0;
        int index = 0;
        for (int j = 0; j < solutionSet.size(); j++) {

          DoubleSolution auxSolution = solutionSet.get(j);
          if (distanceToSolutionListAttribute.getAttribute(auxSolution) > maxMinimum) {
            maxMinimum = distanceToSolutionListAttribute.getAttribute(auxSolution);
            index = j;
          }
        }
        individual = solutionSet.get(index);
        solutionSet.remove(index);

        // Update distances to REFSET in population
        for (int j = 0; j < solutionSet.size(); j++) {
          double aux = SolutionUtils.distanceBetweenSolutions(solutionSet.get(j), individual);

          if (aux < distanceToSolutionListAttribute.getAttribute(individual)) {
            Solution<?> auxSolution = solutionSet.get(j);
            distanceToSolutionListAttribute.setAttribute(auxSolution, aux);
          }
        }

        // Insert the individual into REFSET2
        refSet2.add(individual);

        // Update distances in REFSET2
        for (int j = 0; j < refSet2.size(); j++) {
          for (int k = 0; k < refSet2.size(); k++) {
            if (i != j) {
              double aux = SolutionUtils.distanceBetweenSolutions(refSet2.get(j), refSet2.get(k));
              Solution<?> auxSolution = refSet2.get(j);
              if (aux < distanceToSolutionListAttribute.getAttribute(auxSolution)) {
                distanceToSolutionListAttribute.setAttribute(auxSolution, aux);
              }//if
            } // if
          } // for
        } // for
      } // for

    } else { // Update the reference set from the subset generation result
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

    }
  } // referenceSetUpdate

  /**
   * Tries to update the reference set one with a <code>Solution</code>.
   *
   * @param solution The <code>Solution</code>
   * @return true if the <code>Solution</code> has been inserted, false
   * otherwise.
   */
  public boolean refSet1Test(DoubleSolution solution) {

    boolean dominated = false;
    int flag;
    int i = 0;
    while (i < refSet1.size()) {
      flag = dominanceComparator.compare(solution, refSet1.get(i));
      if (flag == -1) { //This is: solution dominates
        refSet1.remove(i);
      } else if (flag == 1) {
        dominated = true;
        i++;
      } else {
        flag = equalComparator.compare(solution, refSet1.get(i));
        if (flag == 0) {
          return true;
        } // if
        i++;
      } // if
    } // while

    if (!dominated) {
      marked.setAttribute(solution, false);
      if (refSet1.size() < refSet1Size) { //refSet1 isn't full
        refSet1.add(solution);
      } else {
        archive.add(solution);
      } // if
    } else {
      return false;
    } // if
    return true;
  } // refSet1Test

  /**
   * Tries to update the reference set 2 with a <code>Solution</code>
   *
   * @param solution The <code>Solution</code>
   * @return true if the <code>Solution</code> has been inserted, false
   * otherwise.
   * @throws JMException
   */
  public boolean refSet2Test(DoubleSolution solution) throws JMException {

    if (refSet2.size() < refSet2Size) {
      double solutionAux = SolutionUtils.distanceToSolutionListInSolutionSpace(solution, refSet1);
      distanceToSolutionListAttribute.setAttribute(solution, solutionAux);
      double aux = SolutionUtils.distanceToSolutionListInSolutionSpace(solution, refSet2);
      if (aux < distanceToSolutionListAttribute.getAttribute(solution)) {
        distanceToSolutionListAttribute.setAttribute(solution, aux);
      }
      refSet2.add(solution);
      return true;
    }
    double auxDistance = SolutionUtils.distanceToSolutionListInSolutionSpace(solution, refSet1);
    distanceToSolutionListAttribute.setAttribute(solution, auxDistance);
    double aux = SolutionUtils.distanceToSolutionListInSolutionSpace(solution, refSet2);
    if (aux < distanceToSolutionListAttribute.getAttribute(solution)) {
      distanceToSolutionListAttribute.setAttribute(solution, aux);
    }
    double peor = 0.0;
    int index = 0;
    for (int i = 0; i < refSet2.size(); i++) {
      DoubleSolution auxSolution = refSet2.get(i);
      aux = distanceToSolutionListAttribute.getAttribute(auxSolution);
      if (aux > peor) {
        peor = aux;
        index = i;
      }
    }

    double auxDist = distanceToSolutionListAttribute.getAttribute(solution);
    if (auxDist < peor) {
      refSet2.remove(index);
      //Update distances in REFSET2
      for (int j = 0; j < refSet2.size(); j++) {
        aux = SolutionUtils.distanceBetweenSolutions(refSet2.get(j), solution);
        if (aux < distanceToSolutionListAttribute.getAttribute(refSet2.get(j))) {
          distanceToSolutionListAttribute.setAttribute(refSet2.get(j), aux);
        }
      }
      marked.setAttribute(solution, false);
      refSet2.add(solution);
      return true;
    }
    return false;
  } // refSet2Test

  /**
   * Implements the subset generation method described in the scatter search
   * template
   *
   * @return Number of solutions created by the method
   * @throws JMException
   */
  public int subSetGeneration() throws JMException {
    List<DoubleSolution> parents = new ArrayList<DoubleSolution>(2);
    parents.add(problem.createSolution());
    parents.add(problem.createSolution());
    List<DoubleSolution> offSpring;

    subSet.clear();

    //All pairs from refSet1
    for (int i = 0; i < refSet1.size(); i++) {
      parents.set(0,refSet1.get(i));
      for (int j = i + 1; j < refSet1.size(); j++) {
        parents.set(1,refSet1.get(j));
        if (!marked.getAttribute(parents.get(0)) || !marked.getAttribute(parents.get(1))) {//parents[0].isMarked() || parents[1].isMarked()
          offSpring = (List<DoubleSolution>) crossoverOperator.execute(parents);
          problem.evaluate(offSpring.get(0));
          problem.evaluate(offSpring.get(1));
          if (problem instanceof  ConstrainedProblem) {
            ((ConstrainedProblem)problem).evaluateConstraints(offSpring.get(0));
            ((ConstrainedProblem)problem).evaluateConstraints(offSpring.get(1));
          }
          evaluations += 2;
          if (evaluations < maxEvaluations) {
            subSet.add(offSpring.get(0));
            subSet.add(offSpring.get(1));
          }
          marked.setAttribute(parents.get(0), true);
          marked.setAttribute(parents.get(1), true);

        }
      }
    }

    // All pairs from refSet2
    for (int i = 0; i < refSet2.size(); i++) {
      parents.set(0, refSet2.get(i));
      for (int j = i + 1; j < refSet2.size(); j++) {
        parents.set(1, refSet2.get(j));
        if ( !marked.getAttribute(parents.get(0)) || !marked.getAttribute(parents.get(1))) {//!parents[0].isMarked() || !parents[1].isMarked()
          offSpring = (List<DoubleSolution>) crossoverOperator.execute(parents);
          if (problem instanceof  ConstrainedProblem) {
            ((ConstrainedProblem)problem).evaluateConstraints(offSpring.get(0));
            ((ConstrainedProblem)problem).evaluateConstraints(offSpring.get(1));
          }
          problem.evaluate(offSpring.get(0));
          problem.evaluate(offSpring.get(1));
          evaluations += 2;
          if (evaluations < maxEvaluations) {
            subSet.add(offSpring.get(0));
            subSet.add(offSpring.get(1));
          }
          marked.setAttribute(parents.get(0), true);
          marked.setAttribute(parents.get(1), true);
        }
      }
    }

    return subSet.size();
  } // subSetGeneration

}


