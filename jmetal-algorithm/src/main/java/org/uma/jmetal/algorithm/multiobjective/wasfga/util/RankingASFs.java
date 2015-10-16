package org.uma.jmetal.algorithm.multiobjective.wasfga.util;

import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class implements some facilities for ranking solutions. Given a
 * <code>SolutionSet</code> object, their solutions are ranked according to
 * scheme proposed in NSGA-II, but using several achievement scalarizing
 * function (ASF) and not Pareto dominance; as a result, a set of subsets are
 * obtained. The subsets are numbered starting from 0 (in NSGA-II, the numbering
 * starts from 1); thus, subset 0 contains the best solutions considering the
 * values of all ASFs, subset 1 contains the solutions after removing those 
 * belonging to subset 0, and so on.
 */
public class RankingASFs<S extends Solution<?>> {
  /**
   * An array containing all the fronts found during the search
   */
  private List<List<S>> ranking;

  /**
   * Internal structure to sort by the value of the ASF
   */
  private class Node {
    public int solutionIndex;
    public double[] asfs;
  }

  /**
   * Ranking solutions in frontiers considering several ASFs (one by each vector of weights)
   * @param solutionSet The <code>SolutionSet</code> to be ranked
   * @param achievementScalarizingFunction The <code>AchievementScalarizingFunction</code> to be used
   * @param weights The weight vectors to be used for each <code>AchievementScalarizingFunction</code>
   * @param normalization True if the ASF should be normalized
   */
  public RankingASFs(List<S> solutionSet, AchievementScalarizingFunction<S> achievementScalarizingFunction,
                     double[][] weights, WeightVector.NORMALIZE normalization) {
    // List that contains the index of a solution and the value of the ASF (one for each vector of weights)
    LinkedList<Node> list = new LinkedList<Node>();
    LinkedList<Node> solutionsToRemove = new LinkedList<Node>();
    Node bestNode;
    int solutionIndex, asfIndex, frontierIndex, numberOfFrontiersInNewPopulation;
    double[][] evaluationsAsfs;

    if (normalization == WeightVector.NORMALIZE.TRUE) {
      evaluationsAsfs = achievementScalarizingFunction.evaluateNormalizing(solutionSet, weights);
    } else {
      evaluationsAsfs = achievementScalarizingFunction.evaluate(solutionSet, weights);
    }

    //The list l is used to allow delete a solution when it is assigned to a frontier
    //Each node in the list contain the index of the solution in the SolutioSet because the size of the list changes
    for (solutionIndex = 0; solutionIndex < solutionSet.size(); solutionIndex++) {
      Node node = new Node();

      node.solutionIndex = solutionIndex;
      node.asfs = evaluationsAsfs[solutionIndex];

      list.add(node);
    }

    //Each frontier will have many solutions as weight vectors.
    //So we can calculate the number of frontiers
    if (solutionSet.size() % weights.length == 0) {
      numberOfFrontiersInNewPopulation = solutionSet.size() / weights.length;
    } else {
      numberOfFrontiersInNewPopulation = (solutionSet.size() / weights.length) + 1;
    }

    ranking = new ArrayList<List<S>>(numberOfFrontiersInNewPopulation);

    //Assign each solution in the correct frontier
    solutionIndex = 0;
    for (frontierIndex = 0; frontierIndex < numberOfFrontiersInNewPopulation; frontierIndex++) {
      ranking.add(frontierIndex, new ArrayList<S>(weights.length));
      solutionsToRemove.clear();

      //Search the best solution for the current ASF
      for (asfIndex = 0; (asfIndex < weights.length); asfIndex++) {
        //Initially, the best solution is the first
        bestNode = list.get(0);

        for (Node node : list) {
          if (node.asfs[asfIndex] < bestNode.asfs[asfIndex]) {
            bestNode = node;
          }
        }

        ranking.get(frontierIndex).add(solutionSet.get(bestNode.solutionIndex));

        list.remove(bestNode);

        solutionIndex++;
      }
    }
  }

  /**
   * Returns a <code>SolutionSet</code> containing the solutions of a given rank
   * @param rank The rank
   * @return Object representing the <code>SolutionSet</code>
   */
  public List<S> getSubfront(int rank) {
    return ranking.get(rank);
  }

}
