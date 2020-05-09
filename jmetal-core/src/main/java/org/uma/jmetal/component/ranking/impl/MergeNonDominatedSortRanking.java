package org.uma.jmetal.component.ranking.impl;

import org.uma.jmetal.component.ranking.Ranking;
import org.uma.jmetal.component.ranking.impl.util.MNDSBitsetManager;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.AttributeComparator;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.impl.IntegerValueAttributeComparator;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class implements a solution list ranking based on dominance ranking. Given a collection of
 * solutions, they are ranked according to scheme similar to the one proposed in NSGA-II. As an
 * output, a set of subsets are obtained. The subsets are numbered starting from 0 (in NSGA-II, the
 * numbering starts from 1); thus, subset 0 contains the non-dominated solutions, subset 1 contains
 * the non-dominated population after removing those belonging to subset 0, and so on.
 *
 * @author Javier Moreno <javier.morenom@edu.uah.es>
 */
public class MergeNonDominatedSortRanking<S extends Solution<?>> implements Ranking<S> {
  private String attributeId = getClass().getName();
  private Comparator<S> solutionComparator;

  private static final int INSERTIONSORT = 7;
  private int SOL_ID; //field to store the identifier of the jMetal solution
  private int SORT_INDEX; //field to store the solution index after ordering by the first objective
  private int m; // Number of Objectives
  private int n; // Population Size
  private int initialPopulationSize;
  private int[] ranking;
  private double[][] population;
  private double[][] work; // Working array for merge sort
  private ArrayList<int[]> duplicatedSolutions;
  private MNDSBitsetManager bsManager;
  private List<ArrayList<S>> rankedSubPopulations;

  public MergeNonDominatedSortRanking() {
    this.solutionComparator =
            new IntegerValueAttributeComparator<>(
                    attributeId, AttributeComparator.Ordering.ASCENDING);
  }

  @Override
  public Ranking<S> computeRanking(List<S> solutionSet) {
    initialPopulationSize = solutionSet.size();
    n = solutionSet.size();
    m = solutionSet.get(0).getNumberOfObjectives();
    bsManager = new MNDSBitsetManager(n);
    SOL_ID = m;
    SORT_INDEX = SOL_ID + 1;
    work = new double[n][SORT_INDEX + 1];

    population =
            new double[n]
                    [SORT_INDEX + 1]; // 2 extra fields to store: The solution id and the solution index after ordering by the first objective
    for (int i = 0; i < n; i++) {
      population[i] = new double[SORT_INDEX + 1];
      System.arraycopy(solutionSet.get(i).getObjectives(), 0, population[i], 0, m);
      population[i][SOL_ID] = i;
    }
    int ranking[] = sort(population);
    rankedSubPopulations = new ArrayList<ArrayList<S>>();
    for (int i = 0; i < n; i++) {
      for (int r = rankedSubPopulations.size(); r <= ranking[i]; r++) {
        rankedSubPopulations.add(new ArrayList<S>());
      }
      solutionSet.get(i).setAttribute(attributeId, ranking[i]);
      rankedSubPopulations.get(ranking[i]).add(solutionSet.get(i));
    }
    return this;
  }

  private final int compare_lex(double[] s1, double[] s2, int fromObj, int toObj) {
    for (; fromObj < toObj; fromObj++) {
      if (s1[fromObj] < s2[fromObj]) return -1;
      if (s1[fromObj] > s2[fromObj]) return 1;
    }
    return 0;
  }

  private boolean merge_sort(
          double src[][], double dest[][], int low, int high, int obj, int toObj) {
    int i, j, s;
    double temp[] = null;
    int destLow = low;
    int length = high - low;

    if (length < INSERTIONSORT) {
      for (i = low; i < high; i++) {
        for (j = i; j > low && compare_lex(dest[j - 1], dest[j], obj, toObj) > 0; j--) {
          temp = dest[j];
          dest[j] = dest[j - 1];
          dest[j - 1] = temp;
        }
      }
      return temp == null; // if temp==null, src is already sorted
    }
    int mid = (low + high) >>> 1;
    boolean isSorted = merge_sort(dest, src, low, mid, obj, toObj);
    isSorted &= merge_sort(dest, src, mid, high, obj, toObj);

    // If list is already sorted, just copy from src to dest.
    if (src[mid - 1][obj] <= src[mid][obj]) {
      System.arraycopy(src, low, dest, destLow, length);
      return isSorted;
    }

    for (s = low, i = low, j = mid; s < high; s++) {
      if (j >= high) {
        dest[s] = src[i++];
      } else if (i < mid && compare_lex(src[i], src[j], obj, toObj) <= 0) {
        dest[s] = src[i++];
      } else {
        dest[s] = src[j++];
      }
    }
    return false;
  }

  private boolean sortFirstObjective() {
    int p = 0;
    System.arraycopy(population, 0, work, 0, n);
    merge_sort(population, work, 0, n, 0, m);
    population[0] = work[0];
    population[0][SORT_INDEX] = 0;
    for (int q = 1; q < n; q++) {
      if (0 != compare_lex(population[p], work[q], 0, m)) {
        p++;
        population[p] = work[q];
        population[p][SORT_INDEX] = p;
      } else
        duplicatedSolutions.add(new int[] {(int) population[p][SOL_ID], (int) work[q][SOL_ID]});
    }
    n = p + 1;
    return n > 1;
  }

  private boolean sortSecondObjective() {
    int p, solutionId;
    boolean dominance = false;
    System.arraycopy(population, 0, work, 0, n);
    merge_sort(population, work, 0, n, 1, 2);
    System.arraycopy(work, 0, population, 0, n);
    for (p = 0; p < n; p++) {
      solutionId = ((int) population[p][SORT_INDEX]);
      dominance |= bsManager.initializeSolutionBitset(solutionId);
      bsManager.updateIncrementalBitset(solutionId);
      if (2 == m) {
        int initSolId = ((int) population[p][SOL_ID]);
        bsManager.computeSolutionRanking(solutionId, initSolId);
      }
    }
    return dominance;
  }

  private void sortRestOfObjectives() {
    int p, solutionId, initSolId, lastObjective = m - 1;
    boolean dominance;
    System.arraycopy(population, 0, work, 0, n);
    for (int obj = 2; obj < m; obj++) {
      if (merge_sort(
              population,
              work,
              0,
              n,
              obj,
              obj + 1)) { // Population has the same order as in previous objective
        if (obj == lastObjective) {
          for (p = 0; p < n; p++)
            bsManager.computeSolutionRanking(
                    (int) population[p][SORT_INDEX], (int) population[p][SOL_ID]);
        }
        continue;
      }
      System.arraycopy(work, 0, population, 0, n);
      bsManager.clearIncrementalBitset();
      dominance = false;
      for (p = 0; p < n; p++) {
        initSolId = ((int) population[p][SOL_ID]);
        solutionId = ((int) population[p][SORT_INDEX]);
        if (obj < lastObjective) dominance |= bsManager.updateSolutionDominance(solutionId);
        else bsManager.computeSolutionRanking(solutionId, initSolId);
        bsManager.updateIncrementalBitset(solutionId);
      }
      if (!dominance) {
        return;
      }
    }
  }

  // main
  public final int[] sort(double[][] populationData) {
    // INITIALIZATION
    population = populationData;
    duplicatedSolutions = new ArrayList<int[]>(n);
    // SORTING
    if (sortFirstObjective()) {
      if (sortSecondObjective()) {
        sortRestOfObjectives();
      }
    }
    ranking = bsManager.getRanking();
    // UPDATING DUPLICATED SOLUTIONS
    for (int[] duplicated : duplicatedSolutions)
      ranking[duplicated[1]] =
              ranking[duplicated[0]]; // ranking[dup solution]=ranking[original solution]

    n = initialPopulationSize; // equivalent to n += duplicatedSolutions.size();
    return ranking;
  }

  @Override
  public List<S> getSubFront(int rank) {
    if (rank >= rankedSubPopulations.size()) {
      throw new JMetalException(
              "Invalid rank: " + rank + ". Max rank = " + (rankedSubPopulations.size() - 1));
    }
    return rankedSubPopulations.get(rank);
  }

  @Override
  public int getNumberOfSubFronts() {
    return rankedSubPopulations.size();
  }

  @Override
  public Comparator<S> getSolutionComparator() {
    return solutionComparator;
  }

  @Override
  public String getAttributeId() {
    return attributeId;
  }
}
