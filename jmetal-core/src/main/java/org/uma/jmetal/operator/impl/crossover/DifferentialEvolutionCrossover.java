package org.uma.jmetal.operator.impl.crossover;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Differential evolution crossover operator
 *
 * @author Antonio J. Nebro
 *
 * Comments:
 * - The operator receives two parameters: the current individual and an array
 * of three parent individuals
 * - The best and rand variants depends on the third parent, according whether
 * it represents the current of the "best" individual or a random one.
 * The implementation of both variants are the same, due to that the parent
 * selection is external to the crossover operator.
 * - Implemented variants:
 * - rand/1/bin (best/1/bin)
 * - rand/1/exp (best/1/exp)
 * - current-to-rand/1 (current-to-best/1)
 * - current-to-rand/1/bin (current-to-best/1/bin)
 * - current-to-rand/1/exp (current-to-best/1/exp)
 */
@SuppressWarnings("serial")
public class DifferentialEvolutionCrossover implements CrossoverOperator<DoubleSolution> {
  private static final double DEFAULT_CR = 0.5;
  private static final double DEFAULT_F = 0.5;
  private static final double DEFAULT_K = 0.5;
  private static final String DEFAULT_DE_VARIANT = "rand/1/bin";

  private double cr;
  private double f;
  private double k;
  // DE variant (rand/1/bin, rand/1/exp, etc.)
  private String variant;

  private DoubleSolution currentSolution ;

  private BoundedRandomGenerator<Integer> jRandomGenerator ;
  private BoundedRandomGenerator<Double> crRandomGenerator ;

  /** Constructor */
  public DifferentialEvolutionCrossover() {
    this(DEFAULT_CR, DEFAULT_F, DEFAULT_K, DEFAULT_DE_VARIANT) ;
  }

  /**
   * Constructor
   * @param cr
   * @param f
   * @param variant
   */
  public DifferentialEvolutionCrossover(double cr, double f, String variant) {
	  this(cr, f, variant, (a, b) -> JMetalRandom.getInstance().nextInt(a, b), (a, b) -> JMetalRandom.getInstance().nextDouble(a, b));
  }

  /**
   * Constructor
   * @param cr
   * @param f
   * @param variant
   * @param jRandomGenerator
   * @param crRandomGenerator
   */
  public DifferentialEvolutionCrossover(double cr, double f, String variant, RandomGenerator<Double> randomGenerator) {
	  this(cr, f, variant, BoundedRandomGenerator.fromDoubleToInteger(randomGenerator), BoundedRandomGenerator.bound(randomGenerator));
  }

  /**
   * Constructor
   * @param cr
   * @param f
   * @param variant
   * @param jRandomGenerator
   * @param crRandomGenerator
   */
  public DifferentialEvolutionCrossover(double cr, double f, String variant, BoundedRandomGenerator<Integer> jRandomGenerator, BoundedRandomGenerator<Double> crRandomGenerator) {
    this.cr = cr;
    this.f = f;
    this.k = DEFAULT_K ;
    this.variant = variant ;

    this.jRandomGenerator = jRandomGenerator;
    this.crRandomGenerator = crRandomGenerator ;
  }

  /** Constructor */
  public DifferentialEvolutionCrossover(double cr, double f, double k, String variant) {
    this(cr, f, variant) ;
    this.k = k ;
  }

  /* Getters */
  public double getCr() {
    return cr;
  }

  public double getF() {
    return f;
  }

  public double getK() {
    return k;
  }

  public String getVariant() {
    return variant;
  }

  /* Setters */
  public void setCurrentSolution(DoubleSolution current) {
    this.currentSolution = current ;
  }

  public void setCr(double cr) {
    this.cr = cr;
  }

  public void setF(double f) {
    this.f = f;
  }

  public void setK(double k) {
    this.k = k;
  }

  /** Execute() method */
  @Override
  public List<DoubleSolution> execute(List<DoubleSolution> parentSolutions) {
    DoubleSolution child;

    int jrand;

    child = (DoubleSolution)currentSolution.copy() ;

    int numberOfVariables = parentSolutions.get(0).getNumberOfVariables();
    jrand = jRandomGenerator.getRandomValue(0, numberOfVariables - 1);

    // STEP 4. Checking the DE variant
    if ((DEFAULT_DE_VARIANT.equals(variant)) ||
            "best/1/bin".equals(variant)) {
      for (int j = 0; j < numberOfVariables; j++) {
        if (crRandomGenerator.getRandomValue(0.0, 1.0) < cr || j == jrand) {
          double value;
          value = parentSolutions.get(2).getVariableValue(j) + f * (parentSolutions.get(0).getVariableValue(
            j) -
            parentSolutions.get(1).getVariableValue(j));

          if (value < child.getLowerBound(j)) {
            value = child.getLowerBound(j);
          }
          if (value > child.getUpperBound(j)) {
            value = child.getUpperBound(j);
          }
          child.setVariableValue(j, value);
        } else {
          double value;
          value = currentSolution.getVariableValue(j);
          child.setVariableValue(j, value);
        }
      }
    } else if ("rand/1/exp".equals(variant) ||
            "best/1/exp".equals(variant)) {
      for (int j = 0; j < numberOfVariables; j++) {
        if (crRandomGenerator.getRandomValue(0.0, 1.0) < cr || j == jrand) {
          double value;
          value = parentSolutions.get(2).getVariableValue(j) + f * (parentSolutions.get(0).getVariableValue(j) -
                  parentSolutions.get(1).getVariableValue(j));

          if (value < child.getLowerBound(j)) {
            value = child.getLowerBound(j);
          }
          if (value > child.getUpperBound(j)) {
            value = child.getUpperBound(j);
          }

          child.setVariableValue(j, value);
        } else {
          cr = 0.0;
          double value;
          value = currentSolution.getVariableValue(j);
          child.setVariableValue(j, value);
        }
      }
    } else if ("current-to-rand/1".equals(variant) ||
            "current-to-best/1".equals(variant)) {
      for (int j = 0; j < numberOfVariables; j++) {
        double value;
        value = currentSolution.getVariableValue(j) + k * (parentSolutions.get(2).getVariableValue(j) -
                currentSolution.getVariableValue(j)) +
                f * (parentSolutions.get(0).getVariableValue(j) - parentSolutions.get(1).getVariableValue(j));

        if (value < child.getLowerBound(j)) {
          value = child.getLowerBound(j);
        }
        if (value > child.getUpperBound(j)) {
          value = child.getUpperBound(j);
        }

        child.setVariableValue(j, value);
      }
    } else if ("current-to-rand/1/bin".equals(variant) ||
            "current-to-best/1/bin".equals(variant)) {
      for (int j = 0; j < numberOfVariables; j++) {
        if (crRandomGenerator.getRandomValue(0.0, 1.0) < cr || j == jrand) {
          double value;
          value = currentSolution.getVariableValue(j) + k * (parentSolutions.get(2).getVariableValue(j) -
                  currentSolution.getVariableValue(j)) +
                  f * (parentSolutions.get(0).getVariableValue(j) - parentSolutions.get(1).getVariableValue(j));

          if (value < child.getLowerBound(j)) {
            value = child.getLowerBound(j);
          }
          if (value > child.getUpperBound(j)) {
            value = child.getUpperBound(j);
          }

          child.setVariableValue(j, value);
        } else {
          double value;
          value = currentSolution.getVariableValue(j);
          child.setVariableValue(j, value);
        }
      }
    } else if ("current-to-rand/1/exp".equals(variant) ||
            "current-to-best/1/exp".equals(variant)) {
      for (int j = 0; j < numberOfVariables; j++) {
        if (crRandomGenerator.getRandomValue(0.0, 1.0) < cr || j == jrand) {
          double value;
          value = currentSolution.getVariableValue(j) + k * (parentSolutions.get(2).getVariableValue(j) -
                  currentSolution.getVariableValue(j)) +
                  f * (parentSolutions.get(0).getVariableValue(j) - parentSolutions.get(1).getVariableValue(j));

          if (value < child.getLowerBound(j)) {
            value = child.getLowerBound(j);
          }
          if (value > child.getUpperBound(j)) {
            value = child.getUpperBound(j);
          }

          child.setVariableValue(j, value);
        } else {
          cr = 0.0;
          double value;
          value = currentSolution.getVariableValue(j);
          child.setVariableValue(j, value);
        }
      }
    } else {
      JMetalLogger.logger.severe("DifferentialEvolutionCrossover.execute: " +
              " unknown DE variant (" + variant + ")");
      Class<String> cls = String.class;
      String name = cls.getName();
      throw new JMetalException("Exception in " + name + ".execute()");
    }

    List<DoubleSolution> result = new ArrayList<>(1) ;
    result.add(child) ;
    return result;
  }

  public int getNumberOfRequiredParents() {
    return 3 ;
  }

  public int getNumberOfGeneratedChildren() {
    return 1 ;
  }
}
