package org.uma.jmetal.operator.crossover.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class allows to apply a whole arithmetic crossover operator to two parent solutions.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class WholeArithmeticCrossover implements CrossoverOperator<DoubleSolution> {
  private double crossoverProbability;
  private RepairDoubleSolution solutionRepair ;
  private RandomGenerator<Double> randomGenerator ;

  /** Constructor */
  public WholeArithmeticCrossover(double crossoverProbability) {
    this (crossoverProbability, new RepairDoubleSolutionWithBoundValue()) ;
  }

  /** Constructor */
  public WholeArithmeticCrossover(double crossoverProbability, RepairDoubleSolution solutionRepair) {
	  this(crossoverProbability, solutionRepair, () -> JMetalRandom.getInstance().nextDouble());
  }

  /** Constructor */
  public WholeArithmeticCrossover(double crossoverProbability, RepairDoubleSolution solutionRepair, RandomGenerator<Double> randomGenerator) {
    Check.probabilityIsValid(crossoverProbability);

    this.crossoverProbability = crossoverProbability ;
    this.randomGenerator = randomGenerator ;
    this.solutionRepair = solutionRepair ;
  }

  /* Getters */
  @Override
  public double crossoverProbability() {
    return crossoverProbability;
  }


  /* Setters */
  public void crossoverProbability(double crossoverProbability) {
    this.crossoverProbability = crossoverProbability;
  }

  /** Execute() method */
  @Override
  public List<DoubleSolution> execute(List<DoubleSolution> solutions) {
    Check.notNull(solutions);
    Check.that(solutions.size() == 2, "There must be two parents instead of " + solutions.size());

    return doCrossover(crossoverProbability, solutions.get(0), solutions.get(1)) ;
  }

  /** doCrossover method */
  public List<DoubleSolution> doCrossover(
      double probability, DoubleSolution parent1, DoubleSolution parent2) {
    List<DoubleSolution> offspring = new ArrayList<DoubleSolution>(2);

    offspring.add((DoubleSolution) parent1.copy()) ;
    offspring.add((DoubleSolution) parent2.copy()) ;

    int i;
    double upperBound;
    double lowerBound;

    if (randomGenerator.getRandomValue() <= probability) {
      double alpha = randomGenerator.getRandomValue() ;

      for (i = 0; i < parent1.variables().size(); i++) {
        Bounds<Double> bounds = parent1.getBounds(i);
        upperBound = bounds.getUpperBound();
        lowerBound = bounds.getLowerBound();

        double valueX1 = alpha * parent1.variables().get(i) + (1.0 - alpha) * parent2.variables().get(i) ;
        double valueX2 = alpha * parent2.variables().get(i) + (1.0 - alpha) * parent1.variables().get(i) ;


        valueX1 = solutionRepair.repairSolutionVariableValue(valueX1, lowerBound, upperBound) ;
        valueX2 = solutionRepair.repairSolutionVariableValue(valueX2, lowerBound, upperBound) ;

        offspring.get(0).variables().set(i, valueX1);
        offspring.get(1).variables().set(i, valueX2);
      }
    }

    return offspring;
  }

  public int numberOfRequiredParents() {
    return 2 ;
  }

  public int numberOfGeneratedChildren() {
    return 2 ;
  }
}

