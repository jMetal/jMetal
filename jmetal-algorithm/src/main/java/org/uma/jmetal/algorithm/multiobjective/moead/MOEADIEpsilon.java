package org.uma.jmetal.algorithm.multiobjective.moead;

import static org.uma.jmetal.util.ConstraintHandling.feasibilityRatio;
import static org.uma.jmetal.util.ConstraintHandling.isFeasible;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/**
 * This class implements the MOEA/D-IEpsilon algorithm based on the one presented in the paper: "Z.
 * Fan, W. Li, X. Cai, H. Huang, Y. Fang, Y. You, J. Mo, C Wei, and E. D. Goodman, “An improved
 * epsilon constraint-handling method in MOEA/D for CMOPs with large infeasible regions,” Soft
 * Computing, https://doi.org/10.1007/s00500-019-03794-x
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
@SuppressWarnings("serial")
public class MOEADIEpsilon extends AbstractMOEAD<DoubleSolution> {

  private DifferentialEvolutionCrossover differentialEvolutionCrossover;
  private double epsilonK;
  private double phiMax = -1e30;
  private List<DoubleSolution> archive ;

  public MOEADIEpsilon(
          @NotNull Problem<DoubleSolution> problem,
          int populationSize,
          int resultPopulationSize,
          int maxEvaluations,
          MutationOperator<DoubleSolution> mutation,
          CrossoverOperator<DoubleSolution> crossover,
          FunctionType functionType,
          String dataDirectory,
          double neighborhoodSelectionProbability,
          int maximumNumberOfReplacedSolutions,
          int neighborSize) {
    super(
        problem,
        populationSize,
        resultPopulationSize,
        maxEvaluations,
        crossover,
        mutation,
        functionType,
        dataDirectory,
        neighborhoodSelectionProbability,
        maximumNumberOfReplacedSolutions,
        neighborSize);

    differentialEvolutionCrossover = (DifferentialEvolutionCrossover) crossoverOperator;
    archive = new ArrayList<>() ;
  }

  @Override
  public void run() {
    initializeUniformWeight();
    initializeNeighborhood();
    initializePopulation();
    idealPoint.update(population);

    var constraints = new double[10];
    var count = 0;
    var bound = populationSize;
      for (var i1 = 0; i1 < bound; i1++) {
        var v = ConstraintHandling.overallConstraintViolationDegree(population.get(i1));
          if (constraints.length == count) constraints = Arrays.copyOf(constraints, count * 2);
          constraints[count++] = v;
      }
      constraints = Arrays.copyOfRange(constraints, 0, count);
      Arrays.sort(constraints);
    var epsilonZero = Math.abs(constraints[(int) Math.ceil(0.05 * populationSize)]);

    if (phiMax < Math.abs(constraints[0])) {
      phiMax = Math.abs(constraints[0]);
    }

    var tc = 800;
    var tao = 0.1;
    var rk = feasibilityRatio(population);

    evaluations = populationSize;
    var generationCounter = 0 ;
    epsilonK = epsilonZero;
    do {
      // Update the epsilon level
      if (generationCounter >= tc) {
        epsilonK = 0;
      } else {
        if (rk < 0.95) {
          epsilonK = (1 - tao) * epsilonK;
        } else {
          epsilonK = phiMax * (1 + tao);
        }
      }

      var permutation = new int[populationSize];
      MOEADUtils.randomPermutation(permutation, populationSize);

      for (var i = 0; i < populationSize; i++) {
        var subProblemId = permutation[i];

        var neighborType = chooseNeighborType();
        var parents = parentSelection(subProblemId, neighborType);

        differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
        @NotNull List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);

        var child = children.get(0);
        mutationOperator.execute(child);
        problem.evaluate(child);

        evaluations++;

        // Update PhiMax
        if (phiMax < Math.abs((double) ConstraintHandling.overallConstraintViolationDegree(child))) {
          phiMax = ConstraintHandling.overallConstraintViolationDegree(child);
        }

        idealPoint.update(child.objectives());
        updateNeighborhood(child, subProblemId, neighborType);
      }
      rk = feasibilityRatio(population);

      updateExternalArchive();
      generationCounter++ ;
    } while (evaluations < maxEvaluations);
  }

  public void initializePopulation() {
    for (var i = 0; i < populationSize; i++) {
      var newSolution = problem.createSolution();

      problem.evaluate(newSolution);
      population.add(newSolution);
    }
  }

  @Override
  protected void updateNeighborhood(
      DoubleSolution individual, int subproblemId, NeighborType neighborType) {
    int size;

    var numberOfReplaceSolutions = 0;

    if (neighborType == NeighborType.NEIGHBOR) {
      size = neighborhood[subproblemId].length;
    } else {
      size = population.size();
    }
    var perm = new int[size];

    MOEADUtils.randomPermutation(perm, size);

    for (var i = 0; i < size; i++) {
      int k;
      if (neighborType == NeighborType.NEIGHBOR) {
        k = neighborhood[subproblemId][perm[i]];
      } else {
        k = perm[i];
      }

      var f1 = fitnessFunction(population.get(k), lambda[k]);
      var f2 = fitnessFunction(individual, lambda[k]);

      var cons1 =
          Math.abs(ConstraintHandling.overallConstraintViolationDegree(population.get(k))) ;
      var cons2 =
          Math.abs(ConstraintHandling.overallConstraintViolationDegree(individual));

      if (cons1 < epsilonK && cons2 <= epsilonK) {
        if (f2 < f1) {
          population.set(k, (DoubleSolution) individual.copy());
          numberOfReplaceSolutions++;
        }
      } else if (cons1 == cons2) {
        if (f2 < f1) {
          population.set(k, (DoubleSolution) individual.copy());
          numberOfReplaceSolutions++;
        }
      } else if (cons2 < cons1) {
        population.set(k, (DoubleSolution) individual.copy());
        numberOfReplaceSolutions++;
      }

      if (numberOfReplaceSolutions >= maximumNumberOfReplacedSolutions) {
        return;
      }
    }
  }

  @Override
  public List<DoubleSolution> getResult() {
    return archive ;
  }

  @Override
  public @NotNull String getName() {
    return "MOEA/D IEpsilon";
  }


  @Override
  public String getDescription() {
    return "MOEA/D with improved epsilon constraint handling method";
  }

  private void updateExternalArchive() {
      List<DoubleSolution> feasibleSolutions = new ArrayList<>();
      for (var doubleSolution : population) {
          if (isFeasible(doubleSolution)) {
              @Nullable DoubleSolution copy = (DoubleSolution) doubleSolution.copy();
              feasibleSolutions.add(copy);
          }
      }

      if (feasibleSolutions.size() > 0) {
      feasibleSolutions.addAll(archive) ;
      Ranking<DoubleSolution> ranking = new FastNonDominatedSortRanking<>() ;
      ranking.compute(feasibleSolutions) ;

        var firstRankSolutions = ranking.getSubFront(0) ;

      if (firstRankSolutions.size() <= populationSize) {
        archive.clear();
        for (var solution: firstRankSolutions) {
          archive.add((DoubleSolution)solution.copy()) ;
        }
      } else {
        var crowdingDistance = new CrowdingDistanceDensityEstimator<DoubleSolution>() ;
        while (firstRankSolutions.size() > populationSize) {
          crowdingDistance.compute(firstRankSolutions);
          firstRankSolutions.sort(crowdingDistance.getComparator());
          firstRankSolutions.remove(firstRankSolutions.size() - 1) ;
        }

        archive.clear();
        for (var i = 0; i < populationSize; i++) {
          archive.add((DoubleSolution)firstRankSolutions.get(i).copy()) ;
        }
      }
    }
  }
}
