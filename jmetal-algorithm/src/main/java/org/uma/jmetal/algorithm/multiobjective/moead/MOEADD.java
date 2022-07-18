package org.uma.jmetal.algorithm.multiobjective.moead;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

@SuppressWarnings("serial")
public class MOEADD<S extends DoubleSolution> extends AbstractMOEAD<S> {

  protected Ranking<S> ranking;
  protected int[][] rankIdx;      // index matrix for the non-domination levels
  protected int[][] subregionIdx;    // index matrix for subregion record
  protected double[][] subregionDist;  // distance matrix for perpendicular distance
  protected int numRanks;

  public MOEADD(Problem<S> problem,
                int populationSize,
                int resultPopulationSize,
                int maxEvaluations,
                CrossoverOperator<S> crossoverOperator,
                MutationOperator<S> mutation,
                AbstractMOEAD.FunctionType functionType,
                String dataDirectory,
                double neighborhoodSelectionProbability,
                int maximumNumberOfReplacedSolutions,
                int neighborSize) {
    super(problem, populationSize, resultPopulationSize, maxEvaluations, crossoverOperator, mutation, functionType, dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions, neighborSize);
  }

  @Override
  public void run() {

    evaluations = 0;
    population = new ArrayList<>(populationSize);

    neighborhood = new int[populationSize][neighborSize];
    lambda = new double[populationSize][problem.getNumberOfObjectives()];

    idealPoint = new IdealPoint(problem.getNumberOfObjectives()); // ideal point for Pareto-based population
    nadirPoint = new NadirPoint(problem.getNumberOfObjectives()); // nadir point for Pareto-based population

    rankIdx = new int[populationSize][populationSize];
    subregionIdx = new int[populationSize][populationSize];
    subregionDist = new double[populationSize][populationSize];

    // STEP 1. Initialization
    initializeUniformWeight();
    initializeNeighborhood();
    initPopulation();
    idealPoint.update(population);
    nadirPoint.update(population);

    // initialize the distance
    for (var i = 0; i < populationSize; i++) {
      var distance = calculateDistance2(
          population.get(i), lambda[i], idealPoint.getValues(), nadirPoint.getValues());
      subregionDist[i][i] = distance;
    }

    ranking = computeRanking(population);
    for (var curRank = 0; curRank < ranking.getNumberOfSubFronts(); curRank++) {
      var front = ranking.getSubFront(curRank);
      for (var s : front) {
        var position = this.population.indexOf(s);
        rankIdx[curRank][position] = 1;
      }
    }

    // main procedure
    do {
      var permutation = new int[populationSize];
      MOEADUtils.randomPermutation(permutation, populationSize);

      for (var i = 0; i < populationSize; i++) {
        var cid = permutation[i];
        int type;
        var rnd = randomGenerator.nextDouble();

        // mating selection style
        if (rnd < neighborhoodSelectionProbability) {
          type = 1; // neighborhood
        } else {
          type = 2; // whole population
        }
        var parents = matingSelection(cid, type);

        var children = crossoverOperator.execute(parents);

        var child = children.get(0);
        mutationOperator.execute(child);
        problem.evaluate(child);

        evaluations++;

        idealPoint.update(child.objectives());
        nadirPoint.update(child.objectives());
        updateArchive(child);
      }
    } while (evaluations < maxEvaluations);
  }

  /**
   * Initialize the population
   */
  public void initPopulation() {
    for (var i = 0; i < populationSize; i++) {
      var newSolution = problem.createSolution();
      problem.evaluate(newSolution);
      evaluations++;
      population.add(newSolution);
      subregionIdx[i][i] = 1;
    }
  }

  /**
   * Select two parents for reproduction
   */
  public List<S> matingSelection(int cid, int type) {
    int rnd1, rnd2;

    List<S> parents = new ArrayList<>(2);

    var nLength = neighborhood[cid].length;

    ArrayList<Integer> activeList;
    if (type == 1) {
        activeList = Arrays.stream(neighborhood[cid]).filter(idx -> {
          var bound = populationSize;
            for (var j = 0; j < bound; j++) {
                if (subregionIdx[idx][j] == 1) {
                    return true;
                }
            }
            return false;
        }).boxed().collect(Collectors.toCollection(ArrayList::new));
      if (activeList.size() < 2) {
        activeList.clear();
        for (var i = 0; i < populationSize; i++) {
          for (var j = 0; j < populationSize; j++) {
            if (subregionIdx[i][j] == 1) {
              activeList.add(i);
              break;
            }
          }
        }
      }
      var activeSize = activeList.size();
      rnd1 = randomGenerator.nextInt(0, activeSize - 1);
      do {
        rnd2 = randomGenerator.nextInt(0, activeSize - 1);
      } while (rnd1 == rnd2);  // in a very extreme case, this will be a dead loop
      var list1 = new ArrayList<Integer>();
      @NotNull ArrayList<Integer> list2 = new ArrayList<>();
      int id1 = activeList.get(rnd1);
      int id2 = activeList.get(rnd2);
      for (var i = 0; i < populationSize; i++) {
        if (subregionIdx[id1][i] == 1) {
          list1.add(i);
        }
        if (subregionIdx[id2][i] == 1) {
          list2.add(i);
        }
      }
      var p1 = randomGenerator.nextInt(0, list1.size() - 1);
      var p2 = randomGenerator.nextInt(0, list2.size() - 1);
      parents.add(population.get(list1.get(p1)));
      parents.add(population.get(list2.get(p2)));
    } else {
        activeList = IntStream.range(0, populationSize).filter(i -> {
          var bound = populationSize;
            for (var j = 0; j < bound; j++) {
                if (subregionIdx[i][j] == 1) {
                    return true;
                }
            }
            return false;
        }).boxed().collect(Collectors.toCollection(ArrayList::new));
      var activeSize = activeList.size();
      rnd1 = randomGenerator.nextInt(0, activeSize - 1);
      do {
        rnd2 = randomGenerator.nextInt(0, activeSize - 1);
      } while (rnd1 == rnd2);  // in a very extreme case, this will be a dead loop
      var list1 = new ArrayList<Integer>();
      var list2 = new ArrayList<Integer>();
      int id1 = activeList.get(rnd1);
      int id2 = activeList.get(rnd2);
      for (var i = 0; i < populationSize; i++) {
        if (subregionIdx[id1][i] == 1) {
          list1.add(i);
        }
        if (subregionIdx[id2][i] == 1) {
          list2.add(i);
        }
      }
      var p1 = randomGenerator.nextInt(0, list1.size() - 1);
      var p2 = randomGenerator.nextInt(0, list2.size() - 1);
      parents.add(population.get(list1.get(p1)));
      parents.add(population.get(list2.get(p2)));
    }

    return parents;
  } // matingSelection

  /**
   * update the parent population by using the ENLU method, instead of fast non-dominated sorting
   */
  public void updateArchive(S indiv) {

    // find the location of 'indiv'
    setLocation(indiv, idealPoint.getValues(), nadirPoint.getValues());
    var location = (int) indiv.attributes().get("region");

    numRanks = nondominated_sorting_add(indiv);

    if (numRanks == 1) {
      deleteRankOne(indiv, location);
    } else {
      var lastFront = new ArrayList<S>(populationSize);
      var frontSize = countRankOnes(numRanks - 1);
      if (frontSize == 0) {  // the last non-domination level only contains 'indiv'
        frontSize++;
        lastFront.add(indiv);
      } else {
        var result = new ArrayList<S>(populationSize);
        var bound = populationSize;
          for (var i = 0; i < bound; i++) {
              if (rankIdx[numRanks - 1][i] == 1) {
                var s = population.get(i);
                  result.add(s);
              }
          }
          lastFront = result;
        if ((ranking.getRank(indiv)) == (numRanks - 1)) {
//        if (rankSolution.getOrDefault(indiv, 0) == (numRanks - 1)) {
          frontSize++;
          lastFront.add(indiv);
        }
      }

      if (frontSize == 1 && lastFront.get(0).equals(indiv)) {  // the last non-domination level only has 'indiv'
        var curNC = countOnes(location);
        if (curNC > 0) {  // if the subregion of 'indiv' has other solution, drop 'indiv'
          nondominated_sorting_delete(indiv);
        } else {  // if the subregion of 'indiv' has no solution, keep 'indiv'
          deleteCrowdRegion1(indiv, location);
        }
      } else if (frontSize == 1 && !lastFront.get(0).equals(indiv)) { // the last non-domination level only has one solution, but not 'indiv'
        var targetIdx = findPosition(lastFront.get(0));
        var parentLocation = findRegion(targetIdx);
        var curNC = countOnes(parentLocation);
        if (parentLocation == location) {
          curNC++;
        }

        if (curNC == 1) {  // the subregion only has the solution 'targetIdx', keep solution 'targetIdx'
          deleteCrowdRegion2(indiv, location);
        } else {  // the subregion contains some other solutions, drop solution 'targetIdx'
          int indivRank = ranking.getRank(indiv);
          int targetRank = ranking.getRank(population.get(targetIdx));
          rankIdx[targetRank][targetIdx] = 0;
          rankIdx[indivRank][targetIdx] = 1;

          var targetSol = population.get(targetIdx);

          replace(targetIdx, indiv);
          subregionIdx[parentLocation][targetIdx] = 0;
          subregionIdx[location][targetIdx] = 1;

          // update the non-domination level structure
          nondominated_sorting_delete(targetSol);
        }
      } else {

        var indivFitness = fitnessFunction(indiv, lambda[location]);

        // find the index of the solution in the last non-domination level, and its corresponding subregion
        var idxArray = new int[frontSize];
        var regionArray = new int[frontSize];

        for (var i = 0; i < frontSize; i++) {
          idxArray[i] = findPosition(lastFront.get(i));
          if (idxArray[i] == -1) {
            regionArray[i] = location;
          } else {
            regionArray[i] = findRegion(idxArray[i]);
          }
        }

        // find the most crowded subregion, if more than one exist, keep them in 'crowdList'
        var crowdList = new ArrayList<Integer>();
        int crowdIdx;
        var nicheCount = countOnes(regionArray[0]);
        if (regionArray[0] == location) {
          nicheCount++;
        }
        crowdList.add(regionArray[0]);
        for (var i = 1; i < frontSize; i++) {
          var curSize = countOnes(regionArray[i]);
          if (regionArray[i] == location) {
            curSize++;
          }
          if (curSize > nicheCount) {
            crowdList.clear();
            nicheCount = curSize;
            crowdList.add(regionArray[i]);
          } else if (curSize == nicheCount) {
            crowdList.add(regionArray[i]);
          }
        }
        // find the index of the most crowded subregion
        if (crowdList.size() == 1) {
          crowdIdx = crowdList.get(0);
        } else {
          var listLength = crowdList.size();
          crowdIdx = crowdList.get(0);
          var sumFitness = sumFitness(crowdIdx);
          if (crowdIdx == location) {
            sumFitness = sumFitness + indivFitness;
          }
          for (var i = 1; i < listLength; i++) {
            int curIdx = crowdList.get(i);
            var curFitness = sumFitness(curIdx);
            if (curIdx == location) {
              curFitness = curFitness + indivFitness;
            }
            if (curFitness > sumFitness) {
              crowdIdx = curIdx;
              sumFitness = curFitness;
            }
          }
        }

        switch (nicheCount) {
          case 0:
            System.out.println("Impossible empty subregion!!!");
            break;
          case 1:
            // if the subregion of each solution in the last non-domination level only has one solution, keep them all
            deleteCrowdRegion2(indiv, location);
            break;
          default:
            // delete the worst solution from the most crowded subregion in the last non-domination level
            var list = new ArrayList<Integer>();
            for (var i = 0; i < frontSize; i++) {
              if (regionArray[i] == crowdIdx) {
                list.add(i);
              }
            }
            if (list.isEmpty()) {
              System.out.println("Cannot happen!!!");
            } else {
              double maxFitness, curFitness;
              int targetIdx = list.get(0);
              if (idxArray[targetIdx] == -1) {
                maxFitness = indivFitness;
              } else {
                maxFitness = fitnessFunction(population.get(idxArray[targetIdx]), lambda[crowdIdx]);
              }
              for (var i = 1; i < list.size(); i++) {
                int curIdx = list.get(i);
                if (idxArray[curIdx] == -1) {
                  curFitness = indivFitness;
                } else {
                  curFitness = fitnessFunction(population.get(idxArray[curIdx]), lambda[crowdIdx]);
                }
                if (curFitness > maxFitness) {
                  targetIdx = curIdx;
                  maxFitness = curFitness;
                }
              }
              if (idxArray[targetIdx] == -1) {
                nondominated_sorting_delete(indiv);
              } else {
                //indiv.getRank();
                int indivRank = ranking.getRank(indiv);

                //int targetRank = ((DoubleSolution) population.get(idxArray[targetIdx])).getRank();
                int targetRank = ranking.getRank(population.get(idxArray[targetIdx])) ;

                rankIdx[targetRank][idxArray[targetIdx]] = 0;
                rankIdx[indivRank][idxArray[targetIdx]] = 1;

                var targetSol = population.get(idxArray[targetIdx]);

                replace(idxArray[targetIdx], indiv);
                subregionIdx[crowdIdx][idxArray[targetIdx]] = 0;
                subregionIdx[location][idxArray[targetIdx]] = 1;

                // update the non-domination level structure
                nondominated_sorting_delete(targetSol);
              }
            }
            break;
        }
      }
    }
  }

  /**
   * update the non-domination level structure after deleting a solution
   */
  public void nondominated_sorting_delete(S indiv) {

    // find the non-domination level of 'indiv'
    //int indivRank = indiv.getRank();
    int indivRank = ranking.getRank(indiv);

    var dominateList = new ArrayList<Integer>();  // used to keep the solutions need to be moved

    var integers = new ArrayList<Integer>();
    var bound = populationSize;
      for (var i1 = 0; i1 < bound; i1++) {
          if (rankIdx[indivRank][i1] == 1) {
              Integer integer = i1;
              integers.add(integer);
          }
      }
    // used to keep the solutions in the current non-domination level
    var curLevel = integers;

    int flag;
    // find the solutions belonging to the 'indivRank+1'th level and are dominated by 'indiv'
    var investigateRank = indivRank + 1;
    if (investigateRank < numRanks) {
      for (var i = 0; i < populationSize; i++) {
        if (rankIdx[investigateRank][i] == 1) {
          flag = 0;
          if (checkDominance(indiv, population.get(i)) == 1) {
            for (var j = 0; j < curLevel.size(); j++) {
              if (checkDominance(population.get(i), population.get(curLevel.get(j))) == -1) {
                flag = 1;
                break;
              }
            }
            if (flag == 0) {  // the ith solution can move to the prior level
              dominateList.add(i);
              rankIdx[investigateRank][i] = 0;
              rankIdx[investigateRank - 1][i] = 1;
              //((DoubleSolution) population.get(i)).setRank(investigateRank - 1);
              population.get(i).attributes().put(ranking.getAttributedId(), investigateRank - 1);
            }
          }
        }
      }
    }

    int curIdx;
    var curListSize = dominateList.size();
    while (curListSize != 0) {
      curLevel.clear();
      for (var i = 0; i < populationSize; i++) {
        if (rankIdx[investigateRank][i] == 1) {
          curLevel.add(i);
        }
      }
      investigateRank = investigateRank + 1;

      if (investigateRank < numRanks) {
        for (var i = 0; i < curListSize; i++) {
          curIdx = dominateList.get(i);
          for (var j = 0; j < populationSize; j++) {
            if (j == populationSize) {
              System.err.println("There are problems");
            }
            if (rankIdx[investigateRank][j] == 1) {
              flag = 0;
              if (checkDominance(population.get(curIdx), population.get(j)) == 1) {
                for (var k = 0; k < curLevel.size(); k++) {
                  if (checkDominance(population.get(j), population.get(curLevel.get(k))) == -1) {
                    flag = 1;
                    break;
                  }
                }
                if (flag == 0) {
                  dominateList.add(j);
                  rankIdx[investigateRank][j] = 0;
                  rankIdx[investigateRank - 1][j] = 1;
                  //((DoubleSolution) population.get(j)).setRank(investigateRank - 1);
                  population.get(j).attributes().put(ranking.getAttributedId(), investigateRank - 1);
                }
              }
            }
          }
        }
      }
      for (var i = 0; i < curListSize; i++) {
        dominateList.remove(0);
      }

      curListSize = dominateList.size();
    }

  }

  /**
   * update the non-domination level when adding a solution
   */
  public int nondominated_sorting_add(S indiv) {

    var flag = 0;
    int flag1, flag2, flag3;

    // count the number of non-domination levels
    var num_ranks = 0;
    var frontSize = new ArrayList<Integer>();
    for (var i = 0; i < populationSize; i++) {
      var rankCount = countRankOnes(i);
      if (rankCount != 0) {
        frontSize.add(rankCount);
        num_ranks++;
      } else {
        break;
      }
    }

    @NotNull ArrayList<Integer> dominateList = new ArrayList<>();  // used to keep the solutions dominated by 'indiv'
    var level = 0;
    for (var i = 0; i < num_ranks; i++) {
      level = i;
      if (flag == 1) {  // 'indiv' is non-dominated with all solutions in the ith non-domination level, then 'indiv' belongs to the ith level
        //indiv.setRank(i - 1);
        indiv.attributes().put(ranking.getAttributedId(), i - 1);
        return num_ranks;
      } else if (flag == 2) {  // 'indiv' dominates some solutions in the ith level, but is non-dominated with some others, then 'indiv' belongs to the ith level, and move the dominated solutions to the next level
        //indiv.setRank(i - 1);
        indiv.attributes().put(ranking.getAttributedId(), i - 1);
        var prevRank = i - 1;

        // process the solutions belong to 'prevRank'th level and are dominated by 'indiv' ==> move them to 'prevRank+1'th level and find the solutions dominated by them
        int curIdx;
        var newRank = prevRank + 1;
        var curListSize = dominateList.size();
        for (var j = 0; j < curListSize; j++) {
          curIdx = dominateList.get(j);
          rankIdx[prevRank][curIdx] = 0;
          rankIdx[newRank][curIdx] = 1;
          //((DoubleSolution) population.get(curIdx)).setRank(newRank);
          population.get(curIdx).attributes().put(ranking.getAttributedId(), newRank);
        }
        for (var j = 0; j < populationSize; j++) {
          if (rankIdx[newRank][j] == 1) {
            for (var k = 0; k < curListSize; k++) {
              curIdx = dominateList.get(k);
              if (checkDominance(population.get(curIdx), population.get(j)) == 1) {
                dominateList.add(j);
                break;
              }

            }
          }
        }
        for (var j = 0; j < curListSize; j++) {
          dominateList.remove(0);
        }

        // if there are still some other solutions moved to the next level, check their domination situation in their new level
        prevRank = newRank;
        newRank = newRank + 1;
        curListSize = dominateList.size();
        if (curListSize == 0) {
          return num_ranks;
        } else {
          var allFlag = 0;
          do {
            for (var j = 0; j < curListSize; j++) {
              curIdx = dominateList.get(j);
              rankIdx[prevRank][curIdx] = 0;
              rankIdx[newRank][curIdx] = 1;
              //((DoubleSolution) population.get(curIdx)).setRank(newRank);
              population.get(curIdx).attributes().put(ranking.getAttributedId(), newRank);
            }
            for (var j = 0; j < populationSize; j++) {
              if (rankIdx[newRank][j] == 1) {
                for (var k = 0; k < curListSize; k++) {
                  curIdx = dominateList.get(k);
                  if (checkDominance(population.get(curIdx), population.get(j)) == 1) {
                    dominateList.add(j);
                    break;
                  }
                }
              }
            }
            for (var j = 0; j < curListSize; j++) {
              dominateList.remove(0);
            }

            curListSize = dominateList.size();
            if (curListSize != 0) {
              prevRank = newRank;
              newRank = newRank + 1;
              if (frontSize.size() > prevRank && curListSize == frontSize.get(prevRank)) {  // if all solutions in the 'prevRank'th level are dominated by the newly added solution, move them all to the next level
                allFlag = 1;
                break;
              }
            }
          } while (curListSize != 0);

          if (allFlag == 1) {  // move the solutions after the 'prevRank'th level to their next levels
            var remainSize = num_ranks - prevRank;
            var tempRecord = new int[remainSize][populationSize];

            var tempIdx = 0;
            for (var j = 0; j < dominateList.size(); j++) {
              tempRecord[0][tempIdx] = dominateList.get(j);
              tempIdx++;
            }

            var k = 1;
            var curRank = prevRank + 1;
            while (curRank < num_ranks) {
              tempIdx = 0;
              for (var j = 0; j < populationSize; j++) {
                if (rankIdx[curRank][j] == 1) {
                  tempRecord[k][tempIdx] = j;
                  tempIdx++;
                }
              }
              curRank++;
              k++;
            }

            k = 0;
            curRank = prevRank;
            while (curRank < num_ranks) {
              int level_size = frontSize.get(curRank);

              int tempRank;
              for (var j = 0; j < level_size; j++) {
                curIdx = tempRecord[k][j];
                //tempRank = ((DoubleSolution) population.get(curIdx)).getRank();
                tempRank = (int) population.get(curIdx).attributes().get(ranking.getAttributedId());
                newRank = tempRank + 1;
                //((DoubleSolution) population.get(curIdx)).setRank(newRank);
                population.get(curIdx).attributes().put(ranking.getAttributedId(), newRank);
                rankIdx[tempRank][curIdx] = 0;
                rankIdx[newRank][curIdx] = 1;
              }
              curRank++;
              k++;
            }
            num_ranks++;
          }

          if (newRank == num_ranks) {
            num_ranks++;
          }

          return num_ranks;
        }
      } else if (flag == 3 || flag == 0) {  // if 'indiv' is dominated by some solutions in the ith level, skip it, and term to the next level
        flag1 = flag2 = flag3 = 0;
        for (var j = 0; j < populationSize; j++) {
          if (rankIdx[i][j] == 1) {
            switch (checkDominance(indiv, population.get(j))) {
              case 1: {
                flag1 = 1;
                dominateList.add(j);
                break;
              }
              case 0: {
                flag2 = 1;
                break;
              }
              case -1: {
                flag3 = 1;
                break;
              }
            }

            if (flag3 == 1) {
              flag = 3;
              break;
            } else if (flag1 == 0 && flag2 == 1) {
              flag = 1;
            } else if (flag1 == 1 && flag2 == 1) {
              flag = 2;
            } else if (flag1 == 1 && flag2 == 0) {
              flag = 4;
            } else {
            }
          }
        }

      } else {  // (flag == 4) if 'indiv' dominates all solutions in the ith level, solutions in the current level and beyond move their current next levels
        //indiv.setRank(i - 1);
        indiv.attributes().put(ranking.getAttributedId(), i - 1);
        i = i - 1;
        var remainSize = num_ranks - i;
        var tempRecord = new int[remainSize][populationSize];

        var k = 0;
        while (i < num_ranks) {
          var tempIdx = 0;
          for (var j = 0; j < populationSize; j++) {
            if (rankIdx[i][j] == 1) {
              tempRecord[k][tempIdx] = j;
              tempIdx++;
            }
          }
          i++;
          k++;
        }

        k = 0;
        //i = indiv.getRank();
        i = (int) indiv.attributes().get(ranking.getAttributedId());
        while (i < num_ranks) {
          int level_size = frontSize.get(i);

          int curIdx;
          int curRank, newRank;
          for (var j = 0; j < level_size; j++) {
            curIdx = tempRecord[k][j];
            //curRank = ((DoubleSolution) population.get(curIdx)).getRank();
            curRank = (int) population.get(curIdx).attributes().get(ranking.getAttributedId());
            newRank = curRank + 1;
            //((DoubleSolution) population.get(curIdx)).setRank(newRank);
            population.get(curIdx).attributes().put(ranking.getAttributedId(), newRank);

            rankIdx[curRank][curIdx] = 0;
            rankIdx[newRank][curIdx] = 1;
          }
          i++;
          k++;
        }
        num_ranks++;

        return num_ranks;
      }
    }
    // if flag is still 3 after the for-loop, it means that 'indiv' is in the current last level
    switch (flag) {
      case 1:
        //indiv.setRank(level);
        indiv.attributes().put(ranking.getAttributedId(), level);
        break;
      case 2:
        //indiv.setRank(level);
        indiv.attributes().put(ranking.getAttributedId(), level);
        int curIdx;
        var tempSize = dominateList.size();
        for (var i = 0; i < tempSize; i++) {
          curIdx = dominateList.get(i);
          //((DoubleSolution) population.get(curIdx)).setRank(level + 1);
          population.get(curIdx).attributes().put(ranking.getAttributedId(), level + 1);

          rankIdx[level][curIdx] = 0;
          rankIdx[level + 1][curIdx] = 1;
        }
        num_ranks++;
        break;
      case 3:
        //indiv.setRank(level + 1);
        indiv.attributes().put(ranking.getAttributedId(), level + 1);
        num_ranks++;
        break;
      default:
        //indiv.setRank(level);
        indiv.attributes().put(ranking.getAttributedId(), level);
        for (var i = 0; i < populationSize; i++) {
          if (rankIdx[level][i] == 1) {
            //((DoubleSolution) population.get(i)).setRank(level + 1);

            population.get(i).attributes().put(ranking.getAttributedId(), level + 1);
            rankIdx[level][i] = 0;
            rankIdx[level + 1][i] = 1;
          }
        }
        num_ranks++;
        break;
    }

    return num_ranks;
  }

  /**
   * Delete a solution from the most crowded subregion (this function only happens when: it should
   * delete 'indiv' based on traditional method. However, the subregion of 'indiv' only has one
   * solution, so it should be kept)
   */
  public void deleteCrowdRegion1(@NotNull S indiv, int location) {

    // find the most crowded subregion, if more than one such subregion exists, keep them in the crowdList
    var crowdList = new ArrayList<Integer>();
    int crowdIdx;
    var nicheCount = countOnes(0);
    crowdList.add(0);
    for (var i = 1; i < populationSize; i++) {
      var curSize = countOnes(i);
      if (curSize > nicheCount) {
        crowdList.clear();
        nicheCount = curSize;
        crowdList.add(i);
      } else if (curSize == nicheCount) {
        crowdList.add(i);
      }
    }
    // find the index of the crowded subregion
    if (crowdList.size() == 1) {
      crowdIdx = crowdList.get(0);
    } else {
      var listLength = crowdList.size();
      crowdIdx = crowdList.get(0);
      var sumFitness = sumFitness(crowdIdx);
      for (var i = 1; i < listLength; i++) {
        int curIdx = crowdList.get(i);
        var curFitness = sumFitness(curIdx);
        if (curFitness > sumFitness) {
          crowdIdx = curIdx;
          sumFitness = curFitness;
        }
      }
    }

    // find the solution indices within the 'crowdIdx' subregion
    var indList = new ArrayList<Integer>();
    for (var i = 0; i < populationSize; i++) {
      if (subregionIdx[crowdIdx][i] == 1) {
        indList.add(i);
      }
    }

    // find the solution with the largest rank
    var maxRankList = new ArrayList<Integer>();
    //int maxRank = ((DoubleSolution) population.get(indList.get(0))).getRank();
    var maxRank = (int) population.get(indList.get(0)).attributes().get(ranking.getAttributedId());
    maxRankList.add(indList.get(0));
    for (var i = 1; i < indList.size(); i++) {
      //int curRank = ((DoubleSolution) population.get(indList.get(i))).getRank();
      var curRank = (int) population.get(indList.get(i)).attributes().get(ranking.getAttributedId());
      if (curRank > maxRank) {
        maxRankList.clear();
        maxRank = curRank;
        maxRankList.add(indList.get(i));
      } else if (curRank == maxRank) {
        maxRankList.add(indList.get(i));
      }
    }

    // find the solution with the largest rank and worst fitness
    var rankSize = maxRankList.size();
    int targetIdx = maxRankList.get(0);
    var maxFitness = fitnessFunction(population.get(targetIdx), lambda[crowdIdx]);
    for (var i = 1; i < rankSize; i++) {
      int curIdx = maxRankList.get(i);
      var curFitness = fitnessFunction(population.get(curIdx), lambda[crowdIdx]);
      if (curFitness > maxFitness) {
        targetIdx = curIdx;
        maxFitness = curFitness;
      }
    }

    //int indivRank = indiv.getRank();
    var indivRank = (int) indiv.attributes().get(ranking.getAttributedId());
    //int targetRank = ((DoubleSolution) population.get(targetIdx)).getRank();
    var targetRank = (int) population.get(targetIdx).attributes().get(ranking.getAttributedId());
    rankIdx[targetRank][targetIdx] = 0;
    rankIdx[indivRank][targetIdx] = 1;

    var targetSol = population.get(targetIdx);

    replace(targetIdx, indiv);
    subregionIdx[crowdIdx][targetIdx] = 0;
    subregionIdx[location][targetIdx] = 1;

    // update the non-domination level structure
    nondominated_sorting_delete(targetSol);

  }

  /**
   * delete a solution from the most crowded subregion (this function happens when: it should delete
   * the solution in the 'parentLocation' subregion, but since this subregion only has one solution,
   * it should be kept)
   */
  public void deleteCrowdRegion2(@NotNull S indiv, int location) {

    var indivFitness = fitnessFunction(indiv, lambda[location]);

    // find the most crowded subregion, if there are more than one, keep them in crowdList
    var crowdList = new ArrayList<Integer>();
    int crowdIdx;
    var nicheCount = countOnes(0);
    if (location == 0) {
      nicheCount++;
    }
    crowdList.add(0);
    for (var i = 1; i < populationSize; i++) {
      var curSize = countOnes(i);
      if (location == i) {
        curSize++;
      }
      if (curSize > nicheCount) {
        crowdList.clear();
        nicheCount = curSize;
        crowdList.add(i);
      } else if (curSize == nicheCount) {
        crowdList.add(i);
      }
    }
    // determine the index of the crowded subregion
    if (crowdList.size() == 1) {
      crowdIdx = crowdList.get(0);
    } else {
      var listLength = crowdList.size();
      crowdIdx = crowdList.get(0);
      var sumFitness = sumFitness(crowdIdx);
      if (crowdIdx == location) {
        sumFitness = sumFitness + indivFitness;
      }
      for (var i = 1; i < listLength; i++) {
        int curIdx = crowdList.get(i);
        var curFitness = sumFitness(curIdx);
        if (curIdx == location) {
          curFitness = curFitness + indivFitness;
        }
        if (curFitness > sumFitness) {
          crowdIdx = curIdx;
          sumFitness = curFitness;
        }
      }
    }

    // find the solution indices within the 'crowdIdx' subregion
    @NotNull ArrayList<Integer> indList = new ArrayList<>();
    for (var i = 0; i < populationSize; i++) {
      if (subregionIdx[crowdIdx][i] == 1) {
        indList.add(i);
      }
    }
    if (crowdIdx == location) {
      var temp = -1;
      indList.add(temp);
    }

    // find the solution with the largest rank
    @NotNull ArrayList<Integer> maxRankList = new ArrayList<>();
    //int maxRank = ((DoubleSolution) population.get(indList.get(0))).getRank();
    var maxRank = (int) population.get(indList.get(0)).attributes().get(ranking.getAttributedId());
    maxRankList.add(indList.get(0));
    for (var i = 1; i < indList.size(); i++) {
      int curRank;
      if (indList.get(i) == -1) {
        //curRank = indiv.getRank();
        curRank = (int) indiv.attributes().get(ranking.getAttributedId());
      } else {
        //curRank = ((DoubleSolution) population.get(indList.get(i))).getRank();
        curRank = (int) population.get(indList.get(i)).attributes().get(ranking.getAttributedId());
      }

      if (curRank > maxRank) {
        maxRankList.clear();
        maxRank = curRank;
        maxRankList.add(indList.get(i));
      } else if (curRank == maxRank) {
        maxRankList.add(indList.get(i));
      }
    }

    double maxFitness;
    var rankSize = maxRankList.size();
    int targetIdx = maxRankList.get(0);
    if (targetIdx == -1) {
      maxFitness = indivFitness;
    } else {
      maxFitness = fitnessFunction(population.get(targetIdx), lambda[crowdIdx]);
    }
    for (var i = 1; i < rankSize; i++) {
      double curFitness;
      int curIdx = maxRankList.get(i);
      if (curIdx == -1) {
        curFitness = indivFitness;
      } else {
        curFitness = fitnessFunction(population.get(curIdx), lambda[crowdIdx]);
      }

      if (curFitness > maxFitness) {
        targetIdx = curIdx;
        maxFitness = curFitness;
      }
    }

    if (targetIdx == -1) {

      nondominated_sorting_delete(indiv);

    } else {
      //int indivRank = indiv.getRank();
      var indivRank = (int) indiv.attributes().get(ranking.getAttributedId());
      //int targetRank = ((DoubleSolution) population.get(targetIdx)).getRank();
      var targetRank = (int) population.get(targetIdx).attributes().get(ranking.getAttributedId());
      rankIdx[targetRank][targetIdx] = 0;
      rankIdx[indivRank][targetIdx] = 1;

      var targetSol = population.get(targetIdx);

      replace(targetIdx, indiv);
      subregionIdx[crowdIdx][targetIdx] = 0;
      subregionIdx[location][targetIdx] = 1;

      // update the non-domination level structure of the population
      nondominated_sorting_delete(targetSol);
    }

  }

  /**
   * if there is only one non-domination level (i.e., all solutions are non-dominated with each
   * other), we should delete a solution from the most crowded subregion
   */
  public void deleteRankOne(S indiv, int location) {

    var indivFitness = fitnessFunction(indiv, lambda[location]);

    // find the most crowded subregion, if there are more than one, keep them in crowdList
    var crowdList = new ArrayList<Integer>();
    int crowdIdx;
    var nicheCount = countOnes(0);
    if (location == 0) {
      nicheCount++;
    }
    crowdList.add(0);
    for (var i = 1; i < populationSize; i++) {
      var curSize = countOnes(i);
      if (location == i) {
        curSize++;
      }
      if (curSize > nicheCount) {
        crowdList.clear();
        nicheCount = curSize;
        crowdList.add(i);
      } else if (curSize == nicheCount) {
        crowdList.add(i);
      }
    }
    // determine the index of the crowded subregion
    if (crowdList.size() == 1) {
      crowdIdx = crowdList.get(0);
    } else {
      var listLength = crowdList.size();
      crowdIdx = crowdList.get(0);
      var sumFitness = sumFitness(crowdIdx);
      if (crowdIdx == location) {
        sumFitness = sumFitness + indivFitness;
      }
      for (var i = 1; i < listLength; i++) {
        int curIdx = crowdList.get(i);
        var curFitness = sumFitness(curIdx);
        if (curIdx == location) {
          curFitness = curFitness + indivFitness;
        }
        if (curFitness > sumFitness) {
          crowdIdx = curIdx;
          sumFitness = curFitness;
        }
      }
    }

    switch (nicheCount) {
      case 0:
        System.out.println("Empty subregion!!!");
        break;
      case 1:
        // if every subregion only contains one solution, delete the worst from indiv's subregion
        int targetIdx;
        for (targetIdx = 0; targetIdx < populationSize; targetIdx++) {
          if (subregionIdx[location][targetIdx] == 1) {
            break;
          }
        }
        var prev_func = fitnessFunction(population.get(targetIdx), lambda[location]);
        if (indivFitness < prev_func) {
          replace(targetIdx, indiv);
        }
        break;
      default:
        if (location == crowdIdx) {  // if indiv's subregion is the most crowded one
          deleteCrowdIndiv_same(location, nicheCount, indivFitness, indiv);
        } else {
          var curNC = countOnes(location);
          var crowdNC = countOnes(crowdIdx);

          if (crowdNC > (curNC + 1)) {  // if the crowdIdx subregion is more crowded, delete one from this subregion
            deleteCrowdIndiv_diff(crowdIdx, location, crowdNC, indiv);
          } else if (crowdNC < (curNC + 1)) { // crowdNC == curNC, delete one from indiv's subregion
            deleteCrowdIndiv_same(location, curNC, indivFitness, indiv);
          } else { // crowdNC == (curNC + 1)
            if (curNC == 0) {
              deleteCrowdIndiv_diff(crowdIdx, location, crowdNC, indiv);
            } else {
              var rnd = randomGenerator.nextDouble();
              if (rnd < 0.5) {
                deleteCrowdIndiv_diff(crowdIdx, location, crowdNC, indiv);
              } else {
                deleteCrowdIndiv_same(location, curNC, indivFitness, indiv);
              }
            }
          }
        }
        break;
    }

  }

  /**
   * calculate the sum of fitnesses of solutions in the location subregion
   */
  public double sumFitness(int location) {

    var sum = 0.0;
    var bound = populationSize;
      for (var i = 0; i < bound; i++) {
          if (subregionIdx[location][i] == 1) {
            var v = fitnessFunction(population.get(i), lambda[location]);
              sum += v;
          }
      }

      return sum;
  }

  /**
   * delete one solution from the most crowded subregion, which is indiv's subregion. Compare
   * indiv's fitness value and the worst one in this subregion
   */
  public void deleteCrowdIndiv_same(int crowdIdx, int nicheCount, double indivFitness, S indiv) {

    // find the solution indices within this crowdIdx subregion
      @NotNull ArrayList<Integer> indList = new ArrayList<>();
    var bound = populationSize;
      for (var i1 = 0; i1 < bound; i1++) {
          if (subregionIdx[crowdIdx][i1] == 1) {
              Integer integer = i1;
              indList.add(integer);
          }
      }

      // find the solution with the worst fitness value
    var listSize = indList.size();
    int worstIdx = indList.get(0);
    var maxFitness = fitnessFunction(population.get(worstIdx), lambda[crowdIdx]);
    for (var i = 1; i < listSize; i++) {
      int curIdx = indList.get(i);
      var curFitness = fitnessFunction(population.get(curIdx), lambda[crowdIdx]);
      if (curFitness > maxFitness) {
        worstIdx = curIdx;
        maxFitness = curFitness;
      }
    }

    // if indiv has a better fitness, use indiv to replace the worst one
    if (indivFitness < maxFitness) {
      replace(worstIdx, indiv);
    }
  }

  /**
   * delete one solution from the most crowded subregion, which is different from indiv's subregion.
   * just use indiv to replace the worst solution in that subregion
   */
  public void deleteCrowdIndiv_diff(int crowdIdx, int curLocation, int nicheCount, S indiv) {

    // find the solution indices within this crowdIdx subregion
    var indList = new ArrayList<Integer>();
    var bound = populationSize;
      for (var i1 = 0; i1 < bound; i1++) {
          if (subregionIdx[crowdIdx][i1] == 1) {
              @NotNull Integer integer = i1;
              indList.add(integer);
          }
      }

      // find the solution with the worst fitness value
    int worstIdx = indList.get(0);
    var maxFitness = fitnessFunction(population.get(worstIdx), lambda[crowdIdx]);
    for (var i = 1; i < nicheCount; i++) {
      int curIdx = indList.get(i);
      var curFitness = fitnessFunction(population.get(curIdx), lambda[crowdIdx]);
      if (curFitness > maxFitness) {
        worstIdx = curIdx;
        maxFitness = curFitness;
      }
    }

    // use indiv to replace the worst one
    replace(worstIdx, indiv);
    subregionIdx[crowdIdx][worstIdx] = 0;
    subregionIdx[curLocation][worstIdx] = 1;

  }

  /**
   * Count the number of 1s in the 'location'th subregion
   */
  public int countOnes(int location) {

    var result = 0L;
    var bound = populationSize;
      for (var i = 0; i < bound; i++) {
          if (subregionIdx[location][i] == 1) {
              result++;
          }
      }
    var count = (int) result;

      return count;
  }

  /**
   * count the number of 1s in a row of rank matrix
   */
  public int countRankOnes(int location) {

    var result = 0L;
    var bound = populationSize;
      for (var i = 0; i < bound; i++) {
          if (rankIdx[location][i] == 1) {
              result++;
          }
      }
    var count = (int) result;

      return count;
  }

  /**
   * find the index of the solution 'indiv' in the population
   */
  public int findPosition(@NotNull S indiv) {

    var bound = populationSize;
      for (var i = 0; i < bound; i++) {
          if (indiv.equals(population.get(i))) {
              return i;
          }
      }
      return -1;

  }

  /**
   * find the subregion of the 'idx'th solution in the population
   */
  public int findRegion(int idx) {

    var bound = populationSize;
      for (var i = 0; i < bound; i++) {
          if (subregionIdx[i][idx] == 1) {
              return i;
          }
      }
      return -1;

  }

  /**
   * Set the location of a solution based on the orthogonal distance
   */
  public void setLocation(S indiv, double[] z_, double[] nz_) {

    var minIdx = 0;
    var distance = calculateDistance2(indiv, lambda[0], z_, nz_);
    var minDist = distance;
    for (var i = 1; i < populationSize; i++) {
      distance = calculateDistance2(indiv, lambda[i], z_, nz_);
      if (distance < minDist) {
        minIdx = i;
        minDist = distance;
      }
    }
    //indiv.setRegion(minIdx);
    indiv.attributes().put("region", minIdx);
    //indiv.Set_associateDist(minDist);
//        indiv.setAttribute(ATTRIBUTES.DIST, minDist);

  }

  /**
   * check the dominance relationship between a and b: 1 -> a dominates b, -1 -> b dominates a 0 ->
   * non-dominated with each other
   */
  public int checkDominance(@NotNull S a, S b) {

    var flag1 = 0;
    var flag2 = 0;

    for (var i = 0; i < problem.getNumberOfObjectives(); i++) {
      if (a.objectives()[i] < b.objectives()[i]) {
        flag1 = 1;
      } else {
        if (a.objectives()[i] > b.objectives()[i]) {
          flag2 = 1;
        }
      }
    }
    if (flag1 == 1 && flag2 == 0) {
      return 1;
    } else {
      if (flag1 == 0 && flag2 == 1) {
        return -1;
      } else {
        return 0;
      }
    }
  }

  /**
   * Calculate the perpendicular distance between the solution and reference line
   */
  public double calculateDistance(S individual, double[] lambda,
                                  double[] z_, double[] nz_) {

    // normalize the weight vector (line segment)
    var nd = norm_vector(lambda);
    for (var i = 0; i < problem.getNumberOfObjectives(); i++) {
      lambda[i] = lambda[i] / nd;
    }

    // vecInd has been normalized to the range [0,1]
    var result = new double[10];
    var count1 = 0;
    var bound1 = problem.getNumberOfObjectives();
      for (var i1 = 0; i1 < bound1; i1++) {
        var v1 = (individual.objectives()[i1] - z_[i1]) / (nz_[i1] - z_[i1]);
          if (result.length == count1) result = Arrays.copyOf(result, count1 * 2);
          result[count1++] = v1;
      }
      result = Arrays.copyOfRange(result, 0, count1);
    var vecInd = result;

    var scale = innerproduct(vecInd, lambda);
    var arr = new double[10];
    var count = 0;
    var bound = problem.getNumberOfObjectives();
      for (var i = 0; i < bound; i++) {
        var v = vecInd[i] - scale * lambda[i];
          if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
          arr[count++] = v;
      }
      arr = Arrays.copyOfRange(arr, 0, count);
    var vecProj = arr;

    var distance = norm_vector(vecProj);

    return distance;
  }

  public double calculateDistance2(@NotNull S indiv, double[] lambda,
                                   double[] z_, double[] nz_) {

    // normalize the weight vector (line segment)
    var nd = norm_vector(lambda);
    for (var i = 0; i < problem.getNumberOfObjectives(); i++) {
      lambda[i] = lambda[i] / nd;
    }

    // difference between current point and reference point
    var result = new double[10];
    var count1 = 0;
    var bound1 = problem.getNumberOfObjectives();
      for (var i1 = 0; i1 < bound1; i1++) {
        var v1 = (indiv.objectives()[i1] - z_[i1]);
          if (result.length == count1) result = Arrays.copyOf(result, count1 * 2);
          result[count1++] = v1;
      }
      result = Arrays.copyOfRange(result, 0, count1);
    var realA = result;

    // distance along the line segment
    var d1 = Math.abs(innerproduct(realA, lambda));

    // distance to the line segment
    var arr = new double[10];
    var count = 0;
    var bound = problem.getNumberOfObjectives();
      for (var i = 0; i < bound; i++) {
        var v = (indiv.objectives()[i] - (z_[i] + d1 * lambda[i]));
          if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
          arr[count++] = v;
      }
      arr = Arrays.copyOfRange(arr, 0, count);
    var realB = arr;

    var distance = norm_vector(realB);

    return distance;
  }

  /**
   * Calculate the dot product of two vectors
   */
  public double innerproduct(double @NotNull [] vec1, double[] vec2) {
    var sum = 0.0;
      for (var i = 0; i < vec1.length; i++) {
        var v = vec1[i] * vec2[i];
          sum += v;
      }

      return sum;
  }

  /**
   * Calculate the norm of the vector
   */
  public double norm_vector(double[] z) {
    var sum = 0.0;
    var bound = problem.getNumberOfObjectives();
      for (var i = 0; i < bound; i++) {
        var v = z[i] * z[i];
          sum += v;
      }

      return Math.sqrt(sum);
  }

  public int countTest() {

    var sum = 0;
    for (var i = 0; i < populationSize; i++) {
      for (var j = 0; j < populationSize; j++) {
        if (subregionIdx[i][j] == 1) {
          sum++;
        }
      }
    }

    return sum;
  }

  @Override
  public @NotNull String getName() {
    return "MOEADD";
  }

  @Override
  public String getDescription() {
    return "An Evolutionary Many-Objective Optimization Algorithm Based on Dominance and Decomposition";
  }

  public void replace(int position, S solution) {
    if (position > this.population.size()) {
      population.add(solution);
    } else {
      var toRemove = population.get(position);
      population.remove(toRemove);
      population.add(position, solution);
    }
  }

  protected Ranking<S> computeRanking(List<S> solutionList) {
    Ranking<S> ranking = new FastNonDominatedSortRanking<S>();
    ranking.compute(solutionList);
    return ranking;
  }

} // MOEADD
