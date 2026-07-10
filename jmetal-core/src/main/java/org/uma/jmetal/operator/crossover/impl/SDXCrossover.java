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
 * Synthetic Differences Crossover (SDX) operator for real-coded evolutionary algorithms. This
 * operator recombines two parent solutions ({@link DoubleSolution} encoding) generating two
 * offspring by combining synthetic differences of the parents scaled by a factor {@code f}, in the
 * spirit of differential-evolution schemes.
 *
 * <p>Unlike most jMetal crossover operators, where the crossover probability gates the recombination
 * of the whole pair of parents, in SDX the probability is applied <b>per variable</b>: for each
 * decision variable the recombination is performed with probability {@code crossoverProbability},
 * otherwise the parent values are copied to the offspring.
 *
 * <p>Reference: Santiago, A. (2026). A synthetic differences crossover operator for real-coded
 * evolutionary algorithms. Evolutionary Intelligence, 19(4), 110.
 * <a href="https://doi.org/10.1007/s12065-026-01220-4">https://doi.org/10.1007/s12065-026-01220-4</a>
 *
 * @author Alejandro Santiago (aurelio.santiago@uat.edu.mx)
 */
@SuppressWarnings("serial")
public class SDXCrossover implements CrossoverOperator<DoubleSolution> {
  private static final double DEFAULT_F = 0.5;

  private double crossoverProbability;
  private double f;
  private final RepairDoubleSolution solutionRepair;
  private final RandomGenerator<Double> randomGenerator;

  /** Constructor */
  public SDXCrossover(double crossoverProbability, double f) {
    this(crossoverProbability, f, new RepairDoubleSolutionWithBoundValue());
  }

  /** Constructor */
  public SDXCrossover(
      double crossoverProbability, double f, RandomGenerator<Double> randomGenerator) {
    this(crossoverProbability, f, new RepairDoubleSolutionWithBoundValue(), randomGenerator);
  }

  /** Constructor */
  public SDXCrossover(double crossoverProbability, double f, RepairDoubleSolution solutionRepair) {
    this(crossoverProbability, f, solutionRepair, () -> JMetalRandom.getInstance().nextDouble());
  }

  /** Constructor */
  public SDXCrossover(
      double crossoverProbability,
      double f,
      RepairDoubleSolution solutionRepair,
      RandomGenerator<Double> randomGenerator) {
    Check.probabilityIsValid(crossoverProbability);
    Check.notNull(solutionRepair);
    Check.notNull(randomGenerator);

    this.crossoverProbability = crossoverProbability;
    this.f = f;
    this.solutionRepair = solutionRepair;
    this.randomGenerator = randomGenerator;
  }

  /* Getters */
  @Override
  public double crossoverProbability() {
    return crossoverProbability;
  }

  public double f() {
    return f;
  }

  /* Setters */
  public void crossoverProbability(double crossoverProbability) {
    Check.probabilityIsValid(crossoverProbability);
    this.crossoverProbability = crossoverProbability;
  }

  public void f(double f) {
    this.f = f;
  }

  /** Execute() method */
  @Override
  public List<DoubleSolution> execute(List<DoubleSolution> solutions) {
    Check.notNull(solutions);
    Check.that(solutions.size() == 2, "There must be two parents instead of " + solutions.size());

    return doCrossover(crossoverProbability, solutions.get(0), solutions.get(1));
  }

  /** doCrossover method */
  public List<DoubleSolution> doCrossover(
      double probability, DoubleSolution parent1, DoubleSolution parent2) {
    List<DoubleSolution> offspring = new ArrayList<>(2);
    offspring.add((DoubleSolution) parent1.copy());
    offspring.add((DoubleSolution) parent2.copy());

    for (int i = 0; i < parent1.variables().size(); i++) {
      double valueX1 = parent1.variables().get(i);
      double valueX2 = parent2.variables().get(i);

      Bounds<Double> bounds = parent1.getBounds(i);
      double lowerBound = bounds.getLowerBound();
      double upperBound = bounds.getUpperBound();

      double c1;
      double c2;
      if (randomGenerator.getRandomValue() <= probability) {
        if (valueX1 == valueX2) {
          valueX1 = randomGenerator.getRandomValue() * (upperBound - lowerBound) + lowerBound;
          valueX2 = randomGenerator.getRandomValue() * (upperBound - lowerBound) + lowerBound;
        }

        double z1 = 0.5 * (valueX1 + valueX2);
        double z2 = valueX1 + f * (valueX1 - valueX2);
        double z3 = valueX2 + f * (valueX2 - valueX1);
        double z4 = valueX1 + f * (valueX2 - valueX1);
        double z5 = valueX2 + f * (valueX1 - valueX2);

        c1 = z1 + f * (z2 - z3);
        c2 = z1 + f * (z4 - z5);

        c1 = solutionRepair.repairSolutionVariableValue(c1, lowerBound, upperBound);
        c2 = solutionRepair.repairSolutionVariableValue(c2, lowerBound, upperBound);
      } else {
        c1 = valueX1;
        c2 = valueX2;
      }

      if (randomGenerator.getRandomValue() <= 0.5) {
        double temp = c1;
        c1 = c2;
        c2 = temp;
      }

      offspring.get(0).variables().set(i, c1);
      offspring.get(1).variables().set(i, c2);
    }

    return offspring;
  }

  @Override
  public int numberOfRequiredParents() {
    return 2;
  }

  @Override
  public int numberOfGeneratedChildren() {
    return 2;
  }
}
