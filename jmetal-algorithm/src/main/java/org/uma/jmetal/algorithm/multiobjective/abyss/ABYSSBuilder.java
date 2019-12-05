package org.uma.jmetal.algorithm.multiobjective.abyss;

import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.localsearch.LocalSearchOperator;
import org.uma.jmetal.operator.localsearch.impl.BasicLocalSearch;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;

import java.util.Arrays;
import java.util.Comparator;

/** @author Cristobal Barba */
public class ABYSSBuilder implements AlgorithmBuilder<ABYSS> {
  private DoubleProblem problem;
  private CrossoverOperator<DoubleSolution> crossoverOperator;
  protected LocalSearchOperator<DoubleSolution> improvementOperator;

  private MutationOperator<DoubleSolution> mutationOperator;
  private int numberOfSubranges;
  private int populationSize;
  private int refSet1Size;
  private int refSet2Size;
  private int archiveSize;
  private int maxEvaluations;
  private CrowdingDistanceArchive<DoubleSolution> archive;

  public ABYSSBuilder(DoubleProblem problem, Archive<DoubleSolution> archive) {
    this.populationSize = 20;
    this.maxEvaluations = 25000;
    this.archiveSize = 100;
    this.refSet1Size = 10;
    this.refSet2Size = 10;
    this.numberOfSubranges = 4;
    this.problem = problem;
    double crossoverProbability = 0.9;
    double distributionIndex = 20.0;
    this.crossoverOperator = new SBXCrossover(crossoverProbability, distributionIndex);
    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    this.mutationOperator = new PolynomialMutation(mutationProbability, distributionIndex);
    int improvementRounds = 1;
    this.archive = (CrowdingDistanceArchive<DoubleSolution>) archive;

    Comparator<DoubleSolution> comparator =
        new MultiComparator<>(
            Arrays.asList(
                new OverallConstraintViolationComparator<>(), new DominanceComparator<>()));

    this.improvementOperator =
        new BasicLocalSearch<>(
            improvementRounds, mutationOperator, comparator, problem);
  }

  public CrossoverOperator<DoubleSolution> getCrossoverOperator() {
    return crossoverOperator;
  }

  public ABYSSBuilder setCrossoverOperator(CrossoverOperator<DoubleSolution> crossoverOperator) {
    this.crossoverOperator = crossoverOperator;
    return this;
  }

  public LocalSearchOperator<DoubleSolution> getImprovementOperator() {
    return improvementOperator;
  }

  public ABYSSBuilder setImprovementOperator(
      LocalSearchOperator<DoubleSolution> improvementOperator) {
    this.improvementOperator = improvementOperator;
    return this;
  }

  public MutationOperator<DoubleSolution> getMutationOperator() {
    return mutationOperator;
  }

  public ABYSSBuilder setMutationOperator(MutationOperator<DoubleSolution> mutationOperator) {
    this.mutationOperator = mutationOperator;
    return this;
  }

  public int getNumberOfSubranges() {
    return numberOfSubranges;
  }

  public ABYSSBuilder setNumberOfSubranges(int numberOfSubranges) {
    this.numberOfSubranges = numberOfSubranges;
    return this;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public ABYSSBuilder setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
    return this;
  }

  public int getRefSet1Size() {
    return refSet1Size;
  }

  public ABYSSBuilder setRefSet1Size(int refSet1Size) {
    this.refSet1Size = refSet1Size;
    return this;
  }

  public int getRefSet2Size() {
    return refSet2Size;
  }

  public ABYSSBuilder setRefSet2Size(int refSet2Size) {
    this.refSet2Size = refSet2Size;
    return this;
  }

  public int getArchiveSize() {
    return archiveSize;
  }

  public ABYSSBuilder setArchiveSize(int archiveSize) {
    this.archiveSize = archiveSize;
    return this;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public ABYSSBuilder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;
    return this;
  }

  @Override
  public ABYSS build() {
    return new ABYSS(
        problem,
        maxEvaluations,
        populationSize,
        refSet1Size,
        refSet2Size,
        archiveSize,
        archive,
        improvementOperator,
        crossoverOperator,
        numberOfSubranges);
  }
}
