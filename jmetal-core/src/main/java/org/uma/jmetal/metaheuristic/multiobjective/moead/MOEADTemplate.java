//  MOEADTemplate.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
//
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

package org.uma.jmetal.metaheuristic.multiobjective.moead;

import org.uma.jmetal.core.*;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.random.PseudoRandom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;

/**
 * Created by Antonio J. Nebro on 02/08/14.
 */
public abstract class MOEADTemplate extends Algorithm {
  protected enum NeighborType {NEIGHBOR, POPULATION}

  /** Z vector in Zhang & Li paper */
  protected double[] idealPoint;
  /** Lambda vectors */
  protected double[][] lambda;
  /** T in Zhang & Li paper */
  protected int neighborSize;
  protected int[][] neighborhood;
  /** Delta in Zhang & Li paper */
  protected double neighborhoodSelectionProbability;
  /** nr in Zhang & Li paper */
  protected int maximumNumberOfReplacedSolutions;

  protected Solution[] indArray;
  protected String functionType;

  protected Operator crossover;
  protected Operator mutation;
  protected String dataDirectory;

  protected SolutionSet population;
  protected int populationSize;

  protected int evaluations;
  protected int maxEvaluations;

  /** Constructor */
  protected MOEADTemplate (Builder builder) {
    problem_ = builder.problem ;
    populationSize = builder.populationSize ;
    maxEvaluations = builder.maxEvaluations ;
    crossover = builder.crossover ;
    mutation = builder.mutation ;
    functionType = builder.functionType ;
    neighborhoodSelectionProbability = builder.neighborhoodSelectionProbability ;
    maximumNumberOfReplacedSolutions = builder.maximumNumberOfReplacedSolutions ;
    dataDirectory = builder.dataDirectory ;
    neighborSize = builder.neighborSize ;

    population = new SolutionSet(populationSize);
    indArray = new Solution[problem_.getNumberOfObjectives()];
    neighborhood = new int[populationSize][neighborSize];
    idealPoint = new double[problem_.getNumberOfObjectives()];
    lambda = new double[populationSize][problem_.getNumberOfObjectives()];
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

  public String getDataDirectory() {
    return dataDirectory;
  }

  public Operator getMutation() {
    return mutation;
  }

  public Operator getCrossover() {
    return crossover;
  }

  public String getFunctionType() {
    return functionType;
  }

  public int getMaximumNumberOfReplacedSolutions() {
    return maximumNumberOfReplacedSolutions;
  }

  public double getNeighborhoodSelectionProbability() {
    return neighborhoodSelectionProbability;
  }

  /** Builder class */
  public static class Builder {
    private Problem problem ;
    private int populationSize ;
    private int maxEvaluations ;
    private int neighborSize;
    private double neighborhoodSelectionProbability;
    private int maximumNumberOfReplacedSolutions;
    private String functionType ;
    private Operator crossover ;
    private Operator mutation ;
    private String dataDirectory ;

    public Builder(Problem problem) {
      this.problem = problem ;
      functionType = "_TCHE1";
    }

    public Builder populationSize(int populationSize) {
      this.populationSize = populationSize;

      return this;
    }

    public Builder maxEvaluations(int maxEvaluations) {
      this.maxEvaluations = maxEvaluations;

      return this;
    }

    public Builder neighborSize(int neighborSize) {
      this.neighborSize = neighborSize ;

      return this ;
    }

    public Builder neighborhoodSelectionProbability(double neighborhoodSelectionProbability) {
      this.neighborhoodSelectionProbability = neighborhoodSelectionProbability ;

      return this ;
    }

    public Builder functionType(String functionType) {
      this.functionType = functionType ;

      return this ;
    }

    public Builder maximumNumberOfReplacedSolutions(int maximumNumberOfReplacedSolutions) {
      this.maximumNumberOfReplacedSolutions = maximumNumberOfReplacedSolutions ;

      return this ;
    }

    public Builder crossover(Operator crossover) {
      this.crossover = crossover ;

      return this ;
    }

    public Builder mutation(Mutation mutation) {
      this.mutation = mutation ;

      return this ;
    }

    public Builder dataDirectory(String dataDirectory) {
      this.dataDirectory = dataDirectory ;

      return this ;
    }

    public MOEADTemplate build(String moeadVariant) {
      MOEADTemplate algorithm  ;
      if ("MOEAD".equals(moeadVariant)) {
        algorithm = new MOEAD(this);
      } else {
        throw new JMetalException(moeadVariant + " variant unknown") ;
      }

      return algorithm ;
    }
  }

  /* Class methods */
  public NeighborType chooseNeighborType() {
    double rnd = PseudoRandom.randDouble();
    NeighborType neighborType ;

    if (rnd < neighborhoodSelectionProbability) {
      neighborType = NeighborType.NEIGHBOR;
    } else {
      neighborType = NeighborType.POPULATION;
    }
    return neighborType ;
  }

  public Solution[] parentSelection(int subProblemId, NeighborType neighborType) {
    Vector<Integer> matingPool = new Vector<Integer>();
    matingSelection(matingPool, subProblemId, neighborType);

    Solution[] parents = new Solution[3];

    parents[0] = population.get(matingPool.get(0));
    parents[1] = population.get(matingPool.get(1));
    parents[2] = population.get(subProblemId);

    return parents ;
  }

  public void initializeUniformWeight() {
    if ((problem_.getNumberOfObjectives() == 2) && (populationSize <= 300)) {
      for (int n = 0; n < populationSize; n++) {
        double a = 1.0 * n / (populationSize - 1);
        lambda[n][0] = a;
        lambda[n][1] = 1 - a;
      }
    } else {
      String dataFileName;
      dataFileName = "W" + problem_.getNumberOfObjectives() + "D_" +
        populationSize + ".dat";

      try {
        // Open the file from the resources directory
        FileInputStream fis =
          new FileInputStream(
            this.getClass().getClassLoader().getResource(dataDirectory + "/" + dataFileName)
              .getPath());
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        int numberOfObjectives = 0;
        int i = 0;
        int j = 0;
        String aux = br.readLine();
        while (aux != null) {
          StringTokenizer st = new StringTokenizer(aux);
          j = 0;
          numberOfObjectives = st.countTokens();
          while (st.hasMoreTokens()) {
            double value = new Double(st.nextToken());
            lambda[i][j] = value;
            j++;
          }
          aux = br.readLine();
          i++;
        }
        br.close();
      } catch (Exception e) {
        JMetalLogger.logger.log(
          Level.SEVERE,
          "initializeUniformWeight: failed when reading for file: " + dataDirectory + "/" + dataFileName,
          e);
      }
    }
  }

  public void initializeNeighborhood() {
    double[] x = new double[populationSize];
    int[] idx = new int[populationSize];

    for (int i = 0; i < populationSize; i++) {
      // calculate the distances based on weight vectors
      for (int j = 0; j < populationSize; j++) {
        x[j] = Utils.distVector(lambda[i], lambda[j]);
        idx[j] = j;
      }

      // find 'niche' nearest neighboring subproblems
      Utils.minFastSort(x, idx, populationSize, neighborSize);

      System.arraycopy(idx, 0, neighborhood[i], 0, neighborSize);
    }
  }

  public void initializePopulation() throws JMetalException, ClassNotFoundException {
    for (int i = 0; i < populationSize; i++) {
      Solution newSolution = new Solution(problem_);

      problem_.evaluate(newSolution);
      evaluations++;
      population.add(newSolution);
    }
  }

  void initializeIdealPoint() throws JMetalException, ClassNotFoundException {
    for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
      idealPoint[i] = 1.0e+30;
      //indArray[i] = new Solution(problem);
      //problem.evaluate(indArray[i]);
      evaluations++;
    }

    for (int i = 0; i < populationSize; i++) {
      updateIdealPoint(population.get(i));
    }
  }

  /**
   *
   * @param listOfSolutions
   * @param subProblemId
   * @param neighbourType
   */
  public void matingSelection(Vector<Integer> listOfSolutions, int subProblemId, NeighborType neighbourType) {
    // list : the set of the indexes of selected mating parents
    // subProblemId  : the id of current subproblem
    // numberOfSolutionsToSelect : the number of selected mating parents
    // type : 1 - neighborhood; otherwise - whole population
    int neighbourSize;
    int selectedSolution;
    int numberOfSolutionsToSelect = 2 ;

    neighbourSize = neighborhood[subProblemId].length;
    while (listOfSolutions.size() < numberOfSolutionsToSelect) {
      int random;
      if (neighbourType == NeighborType.NEIGHBOR) {
        random = PseudoRandom.randInt(0, neighbourSize - 1);
        selectedSolution = neighborhood[subProblemId][random];
      } else {
        selectedSolution = PseudoRandom.randInt(0, populationSize - 1);
      }
      boolean flag = true;
      for (Integer individualId : listOfSolutions) {
        if (individualId == selectedSolution) {
          flag = false;
          break;
        }
      }

      if (flag) {
        listOfSolutions.addElement(selectedSolution);
      }
    }
  }

  /**
   * @param individual
   */
  void updateIdealPoint(Solution individual) {
    for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
      if (individual.getObjective(n) < idealPoint[n]) {
        idealPoint[n] = individual.getObjective(n);

        //indArray[n] = individual;
      }
    }
  }

  /**
   * @param individual
   * @param subProblemId
   * @param neighborType
   */
  void updateNeighborhood(Solution individual, int subProblemId, NeighborType neighborType) throws JMetalException {
    int size;
    int time;

    time = 0;

    if (neighborType == NeighborType.NEIGHBOR) {
      size = neighborhood[subProblemId].length;
    } else {
      size = population.size();
    }
    int[] perm = new int[size];

    Utils.randomPermutation(perm, size);

    for (int i = 0; i < size; i++) {
      int k;
      if (neighborType == NeighborType.NEIGHBOR) {
        k = neighborhood[subProblemId][perm[i]];
      } else {
        k = perm[i];
      }
      double f1, f2;

      f1 = fitnessFunction(population.get(k), lambda[k]);
      f2 = fitnessFunction(individual, lambda[k]);

      if (f2 < f1) {
        population.replace(k, new Solution(individual));
        time++;
      }

      if (time >= maximumNumberOfReplacedSolutions) {
        return;
      }
    }
  }

  double fitnessFunction(Solution individual, double[] lambda) throws JMetalException {
    double fitness;

    if ("_TCHE1".equals(functionType)) {
      double maxFun = -1.0e+30;

      for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
        double diff = Math.abs(individual.getObjective(n) - idealPoint[n]);

        double feval;
        if (lambda[n] == 0) {
          feval = 0.0001 * diff;
        } else {
          feval = diff * lambda[n];
        }
        if (feval > maxFun) {
          maxFun = feval;
        }
      }

      fitness = maxFun;
    } else {
      throw new JMetalException(" MOEAD.fitnessFunction: unknown type " + functionType);
    }
    return fitness;
  }
}
