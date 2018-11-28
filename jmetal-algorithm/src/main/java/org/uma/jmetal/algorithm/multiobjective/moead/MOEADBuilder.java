package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmBuilder;

/**
 * Builder class for algorithm MOEA/D and variants
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class MOEADBuilder implements AlgorithmBuilder<AbstractMOEAD<DoubleSolution>> {
  public enum Variant {MOEAD, ConstraintMOEAD, MOEADDRA, MOEADSTM, MOEADD} ;

  protected Problem<DoubleSolution> problem ;

  /** T in Zhang & Li paper */
  protected int neighborSize;
  /** Delta in Zhang & Li paper */
  protected double neighborhoodSelectionProbability;
  /** nr in Zhang & Li paper */
  protected int maximumNumberOfReplacedSolutions;

  protected MOEAD.FunctionType functionType;

  protected CrossoverOperator<DoubleSolution> crossover;
  protected MutationOperator<DoubleSolution> mutation;
  protected String dataDirectory;

  protected int populationSize;
  protected int resultPopulationSize ;

  protected int maxEvaluations;

  protected int numberOfThreads ;

  protected Variant moeadVariant ;

  /** Constructor */
  public MOEADBuilder(Problem<DoubleSolution> problem, Variant variant) {
    this.problem = problem ;
    populationSize = 300 ;
    resultPopulationSize = 300 ;
    maxEvaluations = 150000 ;
    crossover = new DifferentialEvolutionCrossover() ;
    mutation = new PolynomialMutation(1.0/problem.getNumberOfVariables(), 20.0);
    functionType = MOEAD.FunctionType.TCHE ;
    neighborhoodSelectionProbability = 0.1 ;
    maximumNumberOfReplacedSolutions = 2 ;
    dataDirectory = "" ;
    neighborSize = 20 ;
    numberOfThreads = 1 ;
    moeadVariant = variant ;
  }

  /* Getters/Setters */
  public int getNeighborSize() {
    return neighborSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public int getResultPopulationSize() {
    return resultPopulationSize;
  }

  public String getDataDirectory() {
    return dataDirectory;
  }

  public MutationOperator<DoubleSolution> getMutation() {
    return mutation;
  }

  public CrossoverOperator<DoubleSolution> getCrossover() {
    return crossover;
  }

  public MOEAD.FunctionType getFunctionType() {
    return functionType;
  }

  public int getMaximumNumberOfReplacedSolutions() {
    return maximumNumberOfReplacedSolutions;
  }

  public double getNeighborhoodSelectionProbability() {
    return neighborhoodSelectionProbability;
  }

  public int getNumberOfThreads() {
    return numberOfThreads ;
  }

  public MOEADBuilder setPopulationSize(int populationSize) {
    this.populationSize = populationSize;

    return this;
  }

  public MOEADBuilder setResultPopulationSize(int resultPopulationSize) {
    this.resultPopulationSize = resultPopulationSize;

    return this;
  }

  public MOEADBuilder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public MOEADBuilder setNeighborSize(int neighborSize) {
    this.neighborSize = neighborSize ;

    return this ;
  }

  public MOEADBuilder setNeighborhoodSelectionProbability(double neighborhoodSelectionProbability) {
    this.neighborhoodSelectionProbability = neighborhoodSelectionProbability ;

    return this ;
  }

  public MOEADBuilder setFunctionType(MOEAD.FunctionType functionType) {
    this.functionType = functionType ;

    return this ;
  }

  public MOEADBuilder setMaximumNumberOfReplacedSolutions(int maximumNumberOfReplacedSolutions) {
    this.maximumNumberOfReplacedSolutions = maximumNumberOfReplacedSolutions ;

    return this ;
  }

  public MOEADBuilder setCrossover(CrossoverOperator<DoubleSolution> crossover) {
    this.crossover = crossover ;

    return this ;
  }

  public MOEADBuilder setMutation(MutationOperator<DoubleSolution> mutation) {
    this.mutation = mutation ;

    return this ;
  }

  public MOEADBuilder setDataDirectory(String dataDirectory) {
    this.dataDirectory = dataDirectory ;

    return this ;
  }

  public MOEADBuilder setNumberOfThreads(int numberOfThreads) {
    this.numberOfThreads = numberOfThreads ;

    return this ;
  }

  public AbstractMOEAD<DoubleSolution> build() {
    AbstractMOEAD<DoubleSolution> algorithm = null ;
    if (moeadVariant.equals(Variant.MOEAD)) {
      algorithm = new MOEAD(problem, populationSize, resultPopulationSize, maxEvaluations, mutation,
          crossover, functionType, dataDirectory, neighborhoodSelectionProbability,
          maximumNumberOfReplacedSolutions, neighborSize);
    } else if (moeadVariant.equals(Variant.ConstraintMOEAD)) {
      algorithm =  new ConstraintMOEAD(problem, populationSize, resultPopulationSize, maxEvaluations, mutation,
          crossover, functionType, dataDirectory, neighborhoodSelectionProbability,
          maximumNumberOfReplacedSolutions, neighborSize);
    } else if (moeadVariant.equals(Variant.MOEADDRA)) {
      algorithm =  new MOEADDRA(problem, populationSize, resultPopulationSize, maxEvaluations, mutation,
          crossover, functionType, dataDirectory, neighborhoodSelectionProbability,
          maximumNumberOfReplacedSolutions, neighborSize);
    } else if (moeadVariant.equals(Variant.MOEADSTM)) {
        algorithm =  new MOEADSTM(problem, populationSize, resultPopulationSize, maxEvaluations, mutation,
                crossover, functionType, dataDirectory, neighborhoodSelectionProbability,
                maximumNumberOfReplacedSolutions, neighborSize);
    } else if (moeadVariant.equals(Variant.MOEADD)) {
      algorithm = new MOEADD(problem, populationSize, resultPopulationSize, maxEvaluations, crossover, mutation,
              functionType, dataDirectory, neighborhoodSelectionProbability,
              maximumNumberOfReplacedSolutions, neighborSize);
    }
    return algorithm ;
  }
}
