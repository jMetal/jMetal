package org.uma.jmetal.component.ranking.impl;

import org.uma.jmetal.component.ranking.Ranking;
import org.uma.jmetal.component.ranking.impl.util.MNDSBitsetManager;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.AttributeComparator;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.impl.IntegerValueAttributeComparator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;

import java.util.*;

/**
 * This class implements a solution list ranking based on dominance ranking. Given a collection of
 * solutions, they are ranked according to scheme similar to the one proposed in NSGA-II. As an
 * output, a set of subsets are obtained. The subsets are numbered starting from 0 (in NSGA-II, the
 * numbering starts from 1); thus, subset 0 contains the non-dominated solutions, subset 1 contains
 * the non-dominated population after removing those belonging to subset 0, and so on.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class MergeSortNonDominatedSortRanking<S extends Solution<?>> implements Ranking<S> {
  private String attributeId = getClass().getName();
  private Comparator<S> solutionComparator;

  private static final int INSERTIONSORT = 7;
  private int _m; // Number of Objectives
  private int _obj; // Current objective
  private long _comparisonCounter = 0;
  private long _nonDomEarlyDetection = 0;
  private int _n; // Population Size
  private int _initialPopulationSize;
  private int SORT_INDEX, SOL_ID;
  private int[] _ranking;
  private double[][] _population; // Population to be sorted: Last objective must be the solution ID
  private double[][] _work; // Work array
  private ArrayList<int[]> _duplicatedSolutions;
  private MNDSBitsetManager bsManager;
  private List<ArrayList<S>> rankedSubPopulations;

  public MergeSortNonDominatedSortRanking(int populationSize, int nObjectives) {
    _initialPopulationSize = populationSize ;
    _n = populationSize;
    _m = nObjectives;
    bsManager = new MNDSBitsetManager(_n);
    SORT_INDEX = _m + 1;
    SOL_ID = _m;
    _work = new double[_n][SORT_INDEX + 1]; // m=solID, m+1=solNewIndex
    _nonDomEarlyDetection = 0;

    this.solutionComparator =
        new IntegerValueAttributeComparator<>(attributeId, AttributeComparator.Ordering.ASCENDING);
  }

  public final void freeMem() {
    _population = null;
    _work = null;
    _duplicatedSolutions = null;
    _ranking = null;
    bsManager.freeMem();
  }

  @Override
  public Ranking<S> computeRanking(List<S> solutionSet) {
    _population =
        new double[_n]
            [_m + 2]; // 2 campos extra: id de la solucion e indice de la primera ordenacion
    for (int i = 0; i < _n; i++) {
      _population[i] = new double[_m + 2];
      System.arraycopy(solutionSet.get(i).getObjectives(), 0, _population[i], 0, _m);
      _population[i][_m] = i; // asignamos id a la solucion
    }
    int ranking[] = sort(_population);
    rankedSubPopulations = new ArrayList<ArrayList<S>>();
    for (int r, rank, i = 0; i < _n; i++) {
      rank = ranking[i];
      for (r = rankedSubPopulations.size(); r <= rank; r++)
        rankedSubPopulations.add(new ArrayList<S>());
      rankedSubPopulations.get(rank).add(solutionSet.get(i));
    }
    return this;
  }

  public final long getNumberOfEarlyDetections() {
    return _nonDomEarlyDetection;
  }

  public final long getComparisonCounter() {
    return _comparisonCounter;
  }

  public final int getNumberOfDuplicatedSolutions() {
    return _duplicatedSolutions.size();
  }

  private final int compare_lex(double[] s1, double[] s2, int fromObj, int toObj) {
    for (; fromObj < toObj; fromObj++) {
      _comparisonCounter++;
      if (s1[fromObj] < s2[fromObj]) return -1;
      _comparisonCounter++;
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
    _comparisonCounter++;
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
    _obj = 0;
    System.arraycopy(_population, 0, _work, 0, _n);
    merge_sort(_population, _work, 0, _n, 0, _m);
    _population[0] = _work[0];
    _population[0][SORT_INDEX] = 0;
    for (int q = 1; q < _n; q++) {
      if (0 != compare_lex(_population[p], _work[q], 0, _m)) {
        p++;
        _population[p] = _work[q];
        _population[p][SORT_INDEX] = p;
      } else
        _duplicatedSolutions.add(new int[] {(int) _population[p][SOL_ID], (int) _work[q][SOL_ID]});
    }
    _n = p + 1;
    return _n > 1;
  }

  private boolean sortSecondObjective() {
    int p, solutionId;
    _obj++;
    boolean dominance = true;
    System.arraycopy(_population, 0, _work, 0, _n);
    if (!merge_sort(
        _population,
        _work,
        0,
        _n,
        _obj,
        _obj + 1)) { // Population doesn't have the same order as in previous objective
      System.arraycopy(_work, 0, _population, 0, _n);
      dominance = false;
      for (p = 0; p < _n; p++) {
        solutionId = ((int) _population[p][SORT_INDEX]);
        dominance |= bsManager.initializeSolutionBitset(solutionId);
        bsManager.updateIncrementalBitset(solutionId);
      }
      if (!dominance) _nonDomEarlyDetection++;
    }
    return dominance;
  }

  private void sortRestOfObjectives() {
    int p, solutionId, initSolId, lastObjective = _m - 1;
    boolean dominance;
    System.arraycopy(_population, 0, _work, 0, _n);
    for (_obj++; _obj < _m; _obj++) {
      if (merge_sort(
          _population,
          _work,
          0,
          _n,
          _obj,
          _obj + 1)) { // Population has the same order as in previous objective
        if (_obj == lastObjective) {
          for (p = 0; p < _n; p++)
            bsManager.computeSolutionRanking(
                (int) _population[p][SORT_INDEX], (int) _population[p][SOL_ID]);
        }
        continue;
      }
      System.arraycopy(_work, 0, _population, 0, _n);
      bsManager.clearIncrementalBitset();
      dominance = false;
      for (p = 0; p < _n; p++) {
        initSolId = ((int) _population[p][SOL_ID]);
        solutionId = ((int) _population[p][SORT_INDEX]);
        if (_obj < lastObjective) dominance |= bsManager.updateSolutionDominance(solutionId);
        else bsManager.computeSolutionRanking(solutionId, initSolId);
        bsManager.updateIncrementalBitset(solutionId);
      }
      if (!dominance) {
        _nonDomEarlyDetection++;
        return;
      }
    }
  }

  // main
  public final int[] sort(double[][] populationData) {
    // INITIALIZATION
    _comparisonCounter = 0;
    _population = populationData;
    _duplicatedSolutions = new ArrayList<int[]>(_n);
    // SORTING
    if (sortFirstObjective()) {
      if (sortSecondObjective()) {
        sortRestOfObjectives();
      }
    }
    _ranking = bsManager.getRanking();
    // UPDATING DUPLICATED SOLUTIONS
    for (int[] duplicated : _duplicatedSolutions)
      _ranking[duplicated[1]] =
          _ranking[duplicated[0]]; // ranking[dup solution]=ranking[original solution]

    _n = _initialPopulationSize; // equivalent to n += duplicatedSolutions.size();
    return _ranking;
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
