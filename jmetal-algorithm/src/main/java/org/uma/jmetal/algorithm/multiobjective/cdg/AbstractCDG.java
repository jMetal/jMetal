//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.algorithm.multiobjective.cdg;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

import java.util.*;

/**
 * Abstract class for implementing versions of the CDG algorithm.
 *
 * @author Feng Zhang
 * @version 1.0
 */
@SuppressWarnings("serial")
public abstract class AbstractCDG<S extends Solution<?>> implements Algorithm<List<S>> {

  protected enum NeighborType {
    NEIGHBOR,
    POPULATION
  }

  protected Problem<S> problem;

  /** Z vector in Zhang & Li paper */
  protected double[] idealPoint;

  // nadir point
  protected double[] nadirPoint;

  protected int[][] neighborhood;
  protected int[] neighborhoodNum;

  protected int childGrid_;
  protected int childGridNum_;
  protected int[][] subP;
  protected int[] subPNum;
  protected List<List<Integer>> team = new ArrayList<>();

  /** Delta in Zhang & Li paper */
  protected double neighborhoodSelectionProbability;

  protected double[] d_;
  protected double sigma_;
  protected int k_;
  protected int t_;
  protected int subproblemNum_;

  protected int borderLength;

  protected int slimDetal_;
  protected int badSolutionNum;
  protected int[] badSolution;
  protected int[] gridDetal_;
  protected double[][] gridDetalSum_;

  protected List<S> population;
  protected List<S> badPopulation;

  protected List<S> specialPopulation;
  protected List<Integer> spPopulationOrder;

  protected List<List<S>> subproblem = new ArrayList<>();

  protected List<List<S>> tempBorder = new ArrayList<>();

  protected List<List<S>> border = new ArrayList<>();

  protected int populationSize;
  protected int resultPopulationSize;

  protected int evaluations;
  protected int maxEvaluations;

  protected JMetalRandom randomGenerator;

  protected CrossoverOperator<S> crossoverOperator;

  public AbstractCDG(
          @NotNull Problem<S> problem,
          int populationSize,
          int resultPopulationSize,
          int maxEvaluations,
          CrossoverOperator<S> crossoverOperator,
          double neighborhoodSelectionProbability,
          double sigma_,
          int k_,
          int t_,
          int subproblemNum_,
          int childGrid_,
          int childGridNum_) {
    this.problem = problem;
    this.populationSize = populationSize;
    this.resultPopulationSize = resultPopulationSize;
    this.maxEvaluations = maxEvaluations;
    this.crossoverOperator = crossoverOperator;
    this.neighborhoodSelectionProbability = neighborhoodSelectionProbability;
    this.sigma_ = sigma_;
    this.k_ = k_;
    this.t_ = t_;
    this.subproblemNum_ = subproblemNum_;
    this.childGrid_ = childGrid_;
    this.childGridNum_ = childGridNum_;

    randomGenerator = JMetalRandom.getInstance();

    population = new ArrayList<>(populationSize);

    badPopulation = new ArrayList<>(2 * populationSize);

    specialPopulation = new ArrayList<>(2 * populationSize);
    spPopulationOrder = new ArrayList<>(2 * populationSize);

    neighborhood = new int[populationSize][populationSize];
    neighborhoodNum = new int[populationSize];

    idealPoint = new double[problem.getNumberOfObjectives()];
    nadirPoint = new double[problem.getNumberOfObjectives()];

    d_ = new double[problem.getNumberOfObjectives()];

    subproblem = new ArrayList<>(subproblemNum_);

    for (var i = 0; i < subproblemNum_; i++) {
      List<S> list = new ArrayList<>(populationSize);
      subproblem.add(list);
    }

    var subPLength = (int) Math.pow(3, problem.getNumberOfObjectives());
    subP = new int[childGridNum_][subPLength];
    subPNum = new int[childGridNum_];
    team = new ArrayList<>(childGridNum_);
    for (var i = 0; i < childGridNum_; i++) {
      @NotNull List<Integer> list = new ArrayList<>(populationSize);
      team.add(list);
    }

    slimDetal_ = k_ - 3;
    badSolution = new int[2 * populationSize];
    gridDetal_ = new int[k_];
    gridDetalSum_ = new double[problem.getNumberOfObjectives()][k_];

    tempBorder = new ArrayList<>(problem.getNumberOfObjectives());
    border = new ArrayList<>(problem.getNumberOfObjectives());
    borderLength = 2 * populationSize * (problem.getNumberOfObjectives() - 1);
    for (var i = 0; i < problem.getNumberOfObjectives(); i++) {
      @NotNull List<S> list = new ArrayList<>(borderLength);
      tempBorder.add(list);
    }
    for (var i = 0; i < problem.getNumberOfObjectives(); i++) {
      List<S> list = new ArrayList<>(borderLength);
      border.add(list);
    }
  }

  protected void initialCDGAttributes(@NotNull S individual) {
    var g_ = new int[problem.getNumberOfObjectives()];
    var rank_ = new int[problem.getNumberOfObjectives()];
    for (var i = 0; i < problem.getNumberOfObjectives(); i++) {
      g_[i] = 0;
      rank_[i] = 0;
    }
    var order_ = 0;

    individual.attributes().put("g_", g_);
    individual.attributes().put("rank_", rank_);
    individual.attributes().put("order_", order_);
  }

  protected int getG(S individual, int index) {
    var g_ = (int[]) individual.attributes().get("g_");
    return g_[index];
  }

  protected int getRank(S individual, int index) {
    var rank_ = (int[]) individual.attributes().get("rank_");
    return rank_[index];
  }

  protected int getOrder(S individual) {
    var order_ = (int) individual.attributes().get("order_");
    return order_;
  }

  protected void setG(S individual, int index, int value) {
    var g_ = (int[]) individual.attributes().get("g_");
    g_[index] = value;
    individual.attributes().put("g_", g_);
  }

  protected void setRank(S individual, int index, int value) {
    var rank_ = (int[]) individual.attributes().get("rank_");
    rank_[index] = value;
    individual.attributes().put("rank_", rank_);
  }

  protected void setOrder(S individual, int value) {
    var //order_ = (int) individual.attributes().get("order_");
    order_ = value;
    individual.attributes().put("order_", order_);
  }

  protected void updateNeighborhood() {
    if (problem.getNumberOfObjectives() == 2) {
      initializeSubP2();
      group2();
      initializeNeighborhoodGrid();
    } else if (problem.getNumberOfObjectives() == 3) {
      initializeSubP3();
      group3();
      initializeNeighborhoodGrid();
    } else {
      initializeNeighborhood();
    }
  }

  /** Initialize cdg neighborhoods */
  protected void initializeNeighborhood() {

    for (var i = 0; i < populationSize; i++) {
      neighborhoodNum[i] = 0;
    }

    for (var i = 0; i < populationSize; i++)
      for (var j = 0; j < populationSize; j++) {
        var gridDistance = 0;
        for (var k = 0; k < problem.getNumberOfObjectives(); k++) {
          var g1 = getG(population.get(i), k);
          var g2 = getG(population.get(j), k);

          var tempGridDistance = Math.abs(g1 - g2);
          if (tempGridDistance > gridDistance) gridDistance = tempGridDistance;
        }
        if (gridDistance < t_) {
          neighborhood[i][neighborhoodNum[i]] = j;
          neighborhoodNum[i]++;
        }
      }
  }

  protected void initializeSubP2() {
    var left = new int[problem.getNumberOfObjectives()];
    var right = new int[problem.getNumberOfObjectives()];
    var s = 0;
    var ns = 0;

    for (var i = 1; i < childGridNum_; i++) subPNum[i] = 0;

    for (var i = 1; i <= childGrid_; i++)
      for (var j = 1; j <= childGrid_; j++) {

        s = getPos(i, j, 1);

        left[0] = i - t_;
        right[0] = i + t_;
        left[1] = j - t_;
        right[1] = j + t_;

        for (var d = 0; d < problem.getNumberOfObjectives(); d++) {
          if (left[d] < 1) {
            left[d] = 1;
            right[d] = 2 * t_ + 1;
          }
          if (right[d] > childGrid_) {
            right[d] = childGrid_;
            left[d] = childGrid_ - 2 * t_;
          }
        }
        for (var ni = left[0]; ni <= right[0]; ni++)
          for (var nj = left[1]; nj <= right[1]; nj++) {

            ns = getPos(ni, nj, 1);

            subP[s][subPNum[s]] = ns;
            subPNum[s]++;
          }
      }
  }

  protected void initializeSubP3() {
    var left = new int[problem.getNumberOfObjectives()];
    var right = new int[problem.getNumberOfObjectives()];
    var s = 0;
    var ns = 0;

    for (var i = 1; i < childGridNum_; i++) subPNum[i] = 0;

    for (var i = 1; i <= childGrid_; i++)
      for (var j = 1; j <= childGrid_; j++) {
        for (var k = 1; k <= childGrid_; k++) {

          s = getPos(i, j, k);

          left[0] = i - t_;
          right[0] = i + t_;
          left[1] = j - t_;
          right[1] = j + t_;
          left[2] = k - t_;
          right[2] = k + t_;

          for (var d = 0; d < problem.getNumberOfObjectives(); d++) {
            if (left[d] < 1) {
              left[d] = 1;
              right[d] = 2 * t_ + 1;
            }
            if (right[d] > childGrid_) {
              right[d] = childGrid_;
              left[d] = childGrid_ - 2 * t_;
            }
          }
          for (var ni = left[0]; ni <= right[0]; ni++)
            for (var nj = left[1]; nj <= right[1]; nj++)
              for (var nk = left[2]; nk <= right[2]; nk++) {
                ns = getPos(ni, nj, nk);
                subP[s][subPNum[s]] = ns;
                subPNum[s]++;
              }
        }
      }
  }

  protected int getPos(int i, int j, int k) {
    var s = 0;
    var l = (int) Math.pow(childGrid_, 2);
    l = l * (k - 1);
    if (i >= j) {
      s = (i - 1) * (i - 1) + j;
    } else {
      s = j * j - i + 1;
    }
    s = s + l;
    return s;
  }

  protected void group2() {
    var maxFunValue = new double[10];
    var count1 = 0;
    var bound1 = problem.getNumberOfObjectives();
    for (var i2 = 0; i2 < bound1; i2++) {
      double v1 = 0;
      if (maxFunValue.length == count1) maxFunValue = Arrays.copyOf(maxFunValue, count1 * 2);
      maxFunValue[count1++] = v1;
    }
    maxFunValue = Arrays.copyOfRange(maxFunValue, 0, count1);

    for (var i = 0; i < population.size(); i++) {
      for (var j = 0; j < problem.getNumberOfObjectives(); j++) {
        if (population.get(i).objectives()[j] > maxFunValue[j])
          maxFunValue[j] = population.get(i).objectives()[j];
      }
    }

    var arr = new double[10];
    var count = 0;
    var bound = problem.getNumberOfObjectives();
    for (var i1 = 0; i1 < bound; i1++) {
      var v = (maxFunValue[i1] - idealPoint[i1]) / childGrid_;
      if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
      arr[count++] = v;
    }
    arr = Arrays.copyOfRange(arr, 0, count);
    var childDelta = arr;

    for (var i = 1; i < childGridNum_; i++) team.get(i).clear();

    int grid;
    var childSigma = 1e-10;
    var pos = new int[problem.getNumberOfObjectives()];

    for (var i = 0; i < populationSize; i++) {
      for (var j = 0; j < problem.getNumberOfObjectives(); j++) {
        var nornalObj = population.get(i).objectives()[j] - idealPoint[j];
        pos[j] = (int) Math.ceil(nornalObj / childDelta[j] - childSigma);
      }
      grid = 0;
      for (var j = 0; j < problem.getNumberOfObjectives(); j++) if (pos[j] < 1) pos[j] = 1;

      grid = getPos(pos[0], pos[1], 1);

      if (grid < 1) grid = 1;
      if (grid > childGridNum_ - 1) grid = childGridNum_ - 1;
      team.get(grid).add(i);
    }
  }

  protected void group3() {
    var maxFunValue = new double[10];
    var count1 = 0;
    var bound1 = problem.getNumberOfObjectives();
    for (var i2 = 0; i2 < bound1; i2++) {
      double v1 = 0;
      if (maxFunValue.length == count1) maxFunValue = Arrays.copyOf(maxFunValue, count1 * 2);
      maxFunValue[count1++] = v1;
    }
    maxFunValue = Arrays.copyOfRange(maxFunValue, 0, count1);

    for (var i = 0; i < population.size(); i++) {
      for (var j = 0; j < problem.getNumberOfObjectives(); j++) {
        if (population.get(i).objectives()[j] > maxFunValue[j])
          maxFunValue[j] = population.get(i).objectives()[j];
      }
    }

    var arr = new double[10];
    var count = 0;
    var bound = problem.getNumberOfObjectives();
    for (var i1 = 0; i1 < bound; i1++) {
      var v = (maxFunValue[i1] - idealPoint[i1]) / childGrid_;
      if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
      arr[count++] = v;
    }
    arr = Arrays.copyOfRange(arr, 0, count);
    var childDelta = arr;

    for (var i = 1; i < childGridNum_; i++) team.get(i).clear();

    int grid;
    var childSigma = 1e-10;
    var pos = new int[problem.getNumberOfObjectives()];

    for (var i = 0; i < populationSize; i++) {
      for (var j = 0; j < problem.getNumberOfObjectives(); j++) {
        var nornalObj = population.get(i).objectives()[j] - idealPoint[j];
        pos[j] = (int) Math.ceil(nornalObj / childDelta[j] - childSigma);
      }
      grid = 0;
      for (var j = 0; j < problem.getNumberOfObjectives(); j++) if (pos[j] < 1) pos[j] = 1;

      grid = getPos(pos[0], pos[1], pos[2]);

      if (grid < 1) grid = 1;
      if (grid > childGridNum_ - 1) grid = childGridNum_ - 1;
      team.get(grid).add(i);
    }
  }

  protected void initializeNeighborhoodGrid() {

    for (var i = 0; i < populationSize; i++) {
      neighborhoodNum[i] = 0;
    }

    for (var i = 1; i < childGridNum_; i++) {
      for (var j = 0; j < team.get(i).size(); j++) {
        int parentIndex = team.get(i).get(j);
        if (subP.length < i || subPNum[i] == 0) {
          neighborhoodNum[parentIndex] = 0;
        } else {
          for (var ni = 0; ni < subPNum[i]; ni++) {
            for (var nj = 0; nj < team.get(subP[i][ni]).size(); nj++) {
              neighborhood[parentIndex][neighborhoodNum[parentIndex]] =
                  team.get(subP[i][ni]).get(nj);
              neighborhoodNum[parentIndex]++;
            }
          }
        }
      }
    }
  }

  protected void updateBorder() {
    getBorder();

    paretoFilter();

    var coefficient = 1 + (1 - evaluations / maxEvaluations) * 0.15;
    var borderCoef = 1 + (coefficient - 1) / 4;
    for (var i = 0; i < problem.getNumberOfObjectives(); i++)
      for (var j = 0; j < border.get(i).size(); j++)
        for (var k = 0; k < problem.getNumberOfObjectives(); k++)
          if (i != k) {
            var funValue = border.get(i).get(j).objectives()[k];
            border.get(i).get(j).objectives()[k] = funValue * borderCoef;
          }
  }

  protected void getBorder() {
    var flag = new int[problem.getNumberOfObjectives()];
    var minFunValue = new double[problem.getNumberOfObjectives()];

    for (var i = 0; i < problem.getNumberOfObjectives(); i++) {
      tempBorder.get(i).clear();
      minFunValue[i] = 1.0e+30;
    }

    for (var i = 0; i < population.size(); i++)
      for (var j = 0; j < problem.getNumberOfObjectives(); j++)
        if (population.get(i).objectives()[j] < minFunValue[j])
          minFunValue[j] = population.get(i).objectives()[j];

    int sum;
    int od;
    for (var i = 0; i < population.size(); i++) {
      sum = 0;
      for (var j = 0; j < problem.getNumberOfObjectives(); j++) {
        if (population.get(i).objectives()[j] < minFunValue[j] + nadirPoint[j] / 100) flag[j] = 1;
        else flag[j] = 0;
        sum = sum + flag[j];
      }
      if (sum == 1) {
        var found = 0;
        var bound = problem.getNumberOfObjectives();
        for (var j = 0; j < bound; j++) {
          if (flag[j] == 1) {
            found = j;
            break;
          }
        }
        od = found;
        tempBorder.get(od).add(population.get(i));
      }
    }
  }

  protected void paretoFilter() {
    for (var i = 0; i < problem.getNumberOfObjectives(); i++) border.get(i).clear();

    boolean tag;
    var nmbOfObjs = problem.getNumberOfObjectives() - 1;
    int sum1, sum2;

    for (var i = 0; i < problem.getNumberOfObjectives(); i++)
      for (var p = 0; p < tempBorder.get(i).size(); p++) {
        tag = false;
        for (var q = 0; q < tempBorder.get(i).size(); q++) {
          sum1 = 0;
          sum2 = 0;
          for (var j = 0; j < problem.getNumberOfObjectives(); j++) {
            if (i != j) {
              if (tempBorder.get(i).get(p).objectives()[j]
                  <= tempBorder.get(i).get(q).objectives()[j]) sum1++;
              if (tempBorder.get(i).get(p).objectives()[j]
                  < tempBorder.get(i).get(q).objectives()[j]) sum2++;
            }
          }
          if (sum1 == nmbOfObjs && sum2 > 0) {
            tag = true;
            break;
          }
        }
        if (tag) {
          border.get(i).add(tempBorder.get(i).get(p));
        }
      }
  }

  protected void initializeIdealPoint() {
    for (var i = 0; i < problem.getNumberOfObjectives(); i++) {
      idealPoint[i] = 1.0e+30;
    }

    for (var i = 0; i < populationSize; i++) {
      updateIdealPoint(population.get(i));
    }
  }

  // initialize the nadir point
  protected void initializeNadirPoint() {
    for (var i = 0; i < problem.getNumberOfObjectives(); i++) nadirPoint[i] = -1.0e+30;
    updateNadirPoint();
  }

  // update the current nadir point
  void updateNadirPoint() {
    Ranking<S> ranking = new FastNonDominatedSortRanking<S>();
    ranking.compute(population);
    var nondominatedPopulation = ranking.getSubFront(0);

    for (var i = 0; i < nondominatedPopulation.size(); i++) {
      var individual = nondominatedPopulation.get(i);
      for (var j = 0; j < problem.getNumberOfObjectives(); j++) {
        if (individual.objectives()[j] > nadirPoint[j]) {
          nadirPoint[j] = individual.objectives()[j];
        }
      }
    }
  }

  protected void updateIdealPoint(S individual) {
    for (var n = 0; n < problem.getNumberOfObjectives(); n++) {
      if (individual.objectives()[n] < idealPoint[n]) {
        idealPoint[n] = individual.objectives()[n];
      }
    }
  }

  protected void gridSystemSetup() {
    var coefficient = 1 + (1 - evaluations / maxEvaluations) * 0.15;
    for (var i = 0; i < problem.getNumberOfObjectives(); i++) {
      d_[i] = (nadirPoint[i] - idealPoint[i]) * coefficient / k_;
    }
    for (var i = 0; i < population.size(); i++) {
      for (var j = 0; j < problem.getNumberOfObjectives(); j++) {
        var g = (int) Math.ceil((population.get(i).objectives()[j] - idealPoint[j]) / d_[j]);
        if (g < 0) g = 0;
        if (g >= k_) g = k_ - 1;
        setG(population.get(i), j, g);
      }
    }
  }

  protected void gridSystemSetup3() {
    initialGridDetal();
    for (var i = 0; i < population.size(); i++) {
      for (var j = 0; j < problem.getNumberOfObjectives(); j++) {
        var g = getGridPos(j, population.get(i).objectives()[j]);
        setG(population.get(i), j, g);
      }
    }
  }

  protected void initialGridDetal() {
    var detalSum = 0;
    gridDetal_[0] = -1;
    for (var i = 0; i < problem.getNumberOfObjectives(); i++)
      gridDetalSum_[i][0] = (double) gridDetal_[0];

    for (var i = 1; i < slimDetal_; i++) {
      gridDetal_[i] = 2;
      detalSum = detalSum + gridDetal_[i];
      for (var j = 0; j < problem.getNumberOfObjectives(); j++)
        gridDetalSum_[j][i] = (double) detalSum;
    }

    for (var i = slimDetal_; i < k_; i++) {
      gridDetal_[i] = 1;
      detalSum = detalSum + gridDetal_[i];
      for (var j = 0; j < problem.getNumberOfObjectives(); j++)
        gridDetalSum_[j][i] = (double) detalSum;
    }

    var coefficient = 1 + (1 - evaluations / maxEvaluations) * 0.15;

    for (var i = 0; i < problem.getNumberOfObjectives(); i++)
      d_[i] = (nadirPoint[i] - idealPoint[i]) * coefficient / k_;

    for (var i = 0; i < k_; i++)
      for (var j = 0; j < problem.getNumberOfObjectives(); j++)
        gridDetalSum_[j][i] = gridDetalSum_[j][i] / detalSum * (d_[j] * k_);
  }

  protected int getGridPos(int j, double funValue) {
    var count = 0L;
    var bound = k_;
    for (var i = 0; i < bound; i++) {
      if (funValue > gridDetalSum_[j][i]) {
        count++;
      }
    }
    var g = (int) count;
      if (g == k_) g = k_ - 1;
    return g;
  }

  protected void chooseSpecialPopulation() {
    spPopulationOrder.clear();
    specialPopulation.clear();

    Map<Integer, S> specialSolution = new HashMap<Integer, S>();

    for (var i = 0; i < problem.getNumberOfObjectives(); i++) {
      for (var j = 0; j < population.size(); j++) {
        if (population.get(j).objectives()[i] == idealPoint[i]) {
          if (!specialSolution.containsKey(j)) {
            specialSolution.put(j, population.get(j));
          }
        }
      }
    }

    for (var i = 0; i < problem.getNumberOfObjectives(); i++) {
      for (var j = 0; j < population.size(); j++) {
        if (population.get(j).objectives()[i] == nadirPoint[i]) {
          if (!specialSolution.containsKey(j)) {
            specialSolution.put(j, population.get(j));
          }
        }
      }
    }

    for (var j : specialSolution.keySet()) {
      specialPopulation.add(specialSolution.get(j));
      spPopulationOrder.add(j);
    }

    if (specialPopulation.size() > 2 * problem.getNumberOfObjectives()) {
      for (var i = specialPopulation.size() - 1;
           i > (2 * problem.getNumberOfObjectives() - 1);
           i--) {
        specialPopulation.remove(i);
        spPopulationOrder.remove(i);
      }
    }
  }

  protected void excludeBadSolution() {
    badPopulation.clear();
    var length = population.size();
    for (var i = length - 1; i >= 0; i--) {
      var individual = population.get(i);
      for (var j = 0; j < problem.getNumberOfObjectives(); j++) {
        if (individual.objectives()[j] > nadirPoint[j]) {
          badPopulation.add(individual);
          population.remove(i);
          break;
        }
      }
    }
  }

  protected void excludeBadSolution3() {
    badSolutionNum = 0;
    for (var i = 0; i < population.size(); i++) {
      var individual = population.get(i);
      if (individual.objectives()[0] > nadirPoint[0]
          || individual.objectives()[1] > nadirPoint[1]
          || individual.objectives()[2] > nadirPoint[2]
          || !isInner(individual)) {
        badSolution[badSolutionNum] = i;
        badSolutionNum++;
      }
    }

    if (population.size() - badSolutionNum < populationSize) {
      excludeBadSolution();
    } else {
      for (var i = badSolutionNum - 1; i >= 0; i--) {
        population.remove(badSolution[i]);
      }
    }
  }

  protected boolean isInner(@NotNull S individual) {
    var flag = true;
    for (var i = 0; i < problem.getNumberOfObjectives(); i++) {
      if (border.get(i).size() == 0) {
        flag = false;
        break;
      }
      if (paretoDom(individual, i)) {
        flag = false;
        break;
      }
    }
    return flag;
  }

  protected boolean paretoDom(S individual, int i) {
    var flag = false;
    var m = problem.getNumberOfObjectives() - 1;
    int sum1, sum2;
    for (var j = 0; j < border.get(i).size(); j++) {
      sum1 = 0;
      sum2 = 0;
      for (var k = 0; k < problem.getNumberOfObjectives(); k++)
        if (k != i) {
          if (border.get(i).get(j).objectives()[k] <= individual.objectives()[k]) sum1++;
          if (border.get(i).get(j).objectives()[k] < individual.objectives()[k]) sum2++;
        }
      if (sum1 == m && sum2 > 0) {
        flag = true;
        break;
      }
    }
    return flag;
  }

  protected void supplyBadSolution() {
    @NotNull Random rand = new Random();
    do {
      var i = rand.nextInt(badPopulation.size());
      population.add(badPopulation.get(i));
    } while (population.size() < populationSize);
  }

  protected void rankBasedSelection() {
    for (var i = 0; i < subproblemNum_; i++) subproblem.get(i).clear();

    allocateSolution();

    subproblemSortl();

    setIndividualObjRank();

    setSpIndividualRank();

    individualObjRankSort();

    lexicographicSort();

    chooseSolution();
  }

  protected void allocateSolution() {
    for (var i = 0; i < population.size(); i++) {
      setOrder(population.get(i), i);

      for (var j = 0; j < problem.getNumberOfObjectives(); j++) {
        var objBasedAddress = (int) Math.pow(k_, problem.getNumberOfObjectives() - 1);
        objBasedAddress = j * objBasedAddress;
        var objOffset = 0;
        var bitIndex = 0;
        var bitWeight = 0;
        for (var k = problem.getNumberOfObjectives() - 1; k >= 0; k--) {
          if (j != k) {
            bitWeight = (int) Math.pow(k_, bitIndex);
            var g = getG(population.get(i), k);
            objOffset = objOffset + g * bitWeight;
            bitIndex++;
          }
        }

        subproblem.get(objBasedAddress + objOffset).add(population.get(i));
      }
    }
  }

  protected void subproblemSortl() {
    for (var i = 0; i < subproblemNum_; i++) {
      var perObjSubproblemNum = (int) Math.pow(k_, problem.getNumberOfObjectives() - 1);
      var objD = (int) (i / perObjSubproblemNum);

      Collections.sort(
          subproblem.get(i),
              (o1, o2) -> {
                var x = o1.objectives()[objD];
                var y = o2.objectives()[objD];
                return (x == y) ? 0 : (y < x) ? 1 : (-1);
              });
    }
  }

  protected void setIndividualObjRank() {
    for (var i = 0; i < subproblemNum_; i++) {

      var perObjSubproblemNum = (int) Math.pow(k_, problem.getNumberOfObjectives() - 1);
      var objD = (int) (i / perObjSubproblemNum);

      for (var j = 0; j < subproblem.get(i).size(); j++) {
        var order = getOrder(subproblem.get(i).get(j));

        var objValue = subproblem.get(i).get(j).objectives()[objD];
        var firstValue = subproblem.get(i).get(0).objectives()[objD];

        var gridRank = (int) Math.ceil((objValue - firstValue) / d_[objD]);
        var objRank = Math.max(gridRank + 1, j + 1);
        setRank(population.get(order), objD, objRank);
      }
    }
  }

  protected void setSpIndividualRank() {
    for (var i = 0; i < spPopulationOrder.size(); i++) {
      for (var j = 0; j < problem.getNumberOfObjectives(); j++) {
        setRank(population.get(spPopulationOrder.get(i)), j, 1000);
      }
    }
  }

  protected void individualObjRankSort() {
    for (var i = 0; i < population.size(); i++) {
      List<Integer> list = new ArrayList<Integer>();
      for (var j = 0; j < problem.getNumberOfObjectives(); j++) {
        var rank = getRank(population.get(i), j);
        list.add(rank);
      }

      Collections.sort(
          list,
              (x, y) -> (x == y) ? 0 : (y < x) ? 1 : (-1));

      for (var j = 0; j < problem.getNumberOfObjectives(); j++) {
        setRank(population.get(i), j, list.get(j));
      }
    }
  }

  protected void lexicographicSort() {
    Collections.sort(
        population,
            (o1, o2) -> {
              for (var i = 0; i < problem.getNumberOfObjectives(); i++) {
                var x = getRank(o1, i);
                var y = getRank(o2, i);
                if (y < x) return 1;
                if (x < y) return -1;
              }
              return 0;
            });
  }

  protected void chooseSolution() {
    if (population.size() < populationSize) {
      supplyBadSolution();
    } else {
      var length = population.size();
      for (var i = length - 1; i >= populationSize - specialPopulation.size(); i--) {
        population.remove(i);
      }
      for (var i = 0; i < specialPopulation.size(); i++) population.add(specialPopulation.get(i));
    }
  }

  protected NeighborType chooseNeighborType(int i) {
    var rnd = randomGenerator.nextDouble();
    NeighborType neighborType;

    if (rnd < neighborhoodSelectionProbability && neighborhoodNum[i] > 2) {
      neighborType = NeighborType.NEIGHBOR;
    } else {
      neighborType = NeighborType.POPULATION;
    }
    return neighborType;
  }

  protected List<S> parentSelection(int subProblemId, NeighborType neighborType) {
    var matingPool = matingSelection(subProblemId, 2, neighborType);

    @NotNull List<S> parents = new ArrayList<>(3);

    parents.add(population.get(matingPool.get(0)));
    parents.add(population.get(matingPool.get(1)));
    parents.add(population.get(subProblemId));

    return parents;
  }

  /**
   * @param subproblemId the id of current subproblem
   * @param neighbourType neighbour type
   */
  protected List<Integer> matingSelection(
      int subproblemId, int numberOfSolutionsToSelect, NeighborType neighbourType) {
    int selectedSolution;

    @NotNull List<Integer> listOfSolutions = new ArrayList<>(numberOfSolutionsToSelect);

    var neighbourSize = neighborhood[subproblemId].length;
    while (listOfSolutions.size() < numberOfSolutionsToSelect) {
      int random;
      if (neighbourType == NeighborType.NEIGHBOR) {
        neighbourSize = neighborhoodNum[subproblemId];
        random = randomGenerator.nextInt(0, neighbourSize - 1);
        selectedSolution = neighborhood[subproblemId][random];
      } else {
        selectedSolution = randomGenerator.nextInt(0, populationSize - 1);
      }
      var flag = true;
      for (var individualId : listOfSolutions) {
        if (individualId == selectedSolution) {
          flag = false;
          break;
        }
      }

      if (flag) {
        listOfSolutions.add(selectedSolution);
      }
    }

    return listOfSolutions;
  }

  @Override
  public List<S> getResult() {
    return population;
  }
}
