//  MOEAD.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
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
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;

public class MOEAD extends Algorithm {
  private static final long serialVersionUID = -8602634334286344579L;

  /** Z vector in Zhang & Li paper */
  private double[] idealPoint;
  /** Lambda vectors */
  private double[][] lambda;
  /** T in Zhang & Li paper */
  private int neighborSize;
  private int[][] neighborhood;
  /** Delta inin Zhang & Li paper */
  private double neighborhoodSelectionProbability;
  /** nr in Zhang & Li paper */
  private int maximumNumberOfReplacedSolutions;

  private Solution[] indArray_;
  private String functionType;

  private Operator crossover;
  private Operator mutation;
  private String dataDirectory;

  private SolutionSet population;
  private int populationSize;

  private int evaluations_;
  private int maxEvaluations;

  @Deprecated
  public MOEAD() {
    super();

    functionType = "_TCHE1";
  }

  private MOEAD(Builder build) {
    evaluations_ = 0 ;
  }

  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    evaluations_ = 0;
    maxEvaluations = (Integer) this.getInputParameter("maxEvaluations");
    populationSize = (Integer) this.getInputParameter("populationSize");
    dataDirectory = this.getInputParameter("dataDirectory").toString();
    Configuration.logger_.info("POPSIZE: " + populationSize);

    population = new SolutionSet(populationSize);
    indArray_ = new Solution[problem_.getNumberOfObjectives()];

    neighborSize = (Integer) this.getInputParameter("t");
    maximumNumberOfReplacedSolutions = (Integer) this.getInputParameter("nr");
    neighborhoodSelectionProbability = (Double) this.getInputParameter("delta");

    /*
    neighborSize = (int) (0.1 * populationSize);
    neighborhoodSelectionProbability = 0.9;
    maximumNumberOfReplacedSolutions = (int) (0.01 * populationSize);
     */
    neighborhood = new int[populationSize][neighborSize];

    idealPoint = new double[problem_.getNumberOfObjectives()];
    lambda = new double[populationSize][problem_.getNumberOfObjectives()];

    crossover = operators_.get("crossover");
    mutation = operators_.get("mutation");

    // STEP 1. Initialization
    // STEP 1.1. Compute euclidean distances between weight vectors and find T
    initUniformWeight();

    initNeighborhood();

    // STEP 1.2. Initialize population
    initPopulation();

    // STEP 1.3. Initialize idealPoint
    initIdealPoint();

    // STEP 2. Update
    do {
      int[] permutation = new int[populationSize];
      Utils.randomPermutation(permutation, populationSize);

      for (int i = 0; i < populationSize; i++) {
        int n = permutation[i];

        int type;
        double rnd = PseudoRandom.randDouble();

        // STEP 2.1. Mating selection based on probability
        if (rnd < neighborhoodSelectionProbability)
        {
          type = 1;
        } else {
          type = 2;
        }
        Vector<Integer> p = new Vector<Integer>();
        matingSelection(p, n, 2, type);

        // STEP 2.2. Reproduction
        Solution child;
        Solution[] parents = new Solution[3];

        parents[0] = population.get(p.get(0));
        parents[1] = population.get(p.get(1));
        parents[2] = population.get(n);

        // Apply DE crossover 
        child = (Solution) crossover.execute(new Object[] {population.get(n), parents});

        // Apply mutation
        mutation.execute(child);

        // Evaluation
        problem_.evaluate(child);

        evaluations_++;

        // STEP 2.3. Repair. Not necessary

        // STEP 2.4. Update idealPoint
        updateReference(child);

        // STEP 2.5. Update of solutions
        updateProblem(child, n, type);
      }
    } while (evaluations_ < maxEvaluations);

    return population;
  }


  /**
   * initUniformWeight
   */
  public void initUniformWeight() {
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
        Configuration.logger_.log(
            Level.SEVERE,
            "initUniformWeight: failed when reading for file: " + dataDirectory + "/" + dataFileName,
            e);
      }
    }
  }

  /**
   *
   */
  public void initNeighborhood() {
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

  /**
   *
   */
  public void initPopulation() throws JMetalException, ClassNotFoundException {
    for (int i = 0; i < populationSize; i++) {
      Solution newSolution = new Solution(problem_);

      problem_.evaluate(newSolution);
      evaluations_++;
      population.add(newSolution);
    }
  }

  /**
   *
   */
  void initIdealPoint() throws JMetalException, ClassNotFoundException {
    for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
      idealPoint[i] = 1.0e+30;
      indArray_[i] = new Solution(problem_);
      problem_.evaluate(indArray_[i]);
      evaluations_++;
    }

    for (int i = 0; i < populationSize; i++) {
      updateReference(population.get(i));
    }
  }

  /**
   *
   */
  public void matingSelection(Vector<Integer> list, int cid, int size, int type) {
    // list : the set of the indexes of selected mating parents
    // cid  : the id of current subproblem
    // size : the number of selected mating parents
    // type : 1 - neighborhood; otherwise - whole population
    int ss;
    int r;
    int p;

    ss = neighborhood[cid].length;
    while (list.size() < size) {
      if (type == 1) {
        r = PseudoRandom.randInt(0, ss - 1);
        p = neighborhood[cid][r];
      } else {
        p = PseudoRandom.randInt(0, populationSize - 1);
      }
      boolean flag = true;
      for (Integer aList : list) {
        if (aList == p)
        {
          flag = false;
          break;
        }
      }

      if (flag) {
        list.addElement(p);
      }
    }
  }

  /**
   * @param individual
   */
  void updateReference(Solution individual) {
    for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
      if (individual.getObjective(n) < idealPoint[n]) {
        idealPoint[n] = individual.getObjective(n);

        indArray_[n] = individual;
      }
    }
  }

  /**
   * @param individual
   * @param id
   * @param type
   */
  void updateProblem(Solution individual, int id, int type) throws JMetalException {
    // individual: child solutiontype
    // id:   the id of current subproblem
    // type: update solutions in - neighborhood (1) or whole population (otherwise)
    int size;
    int time;

    time = 0;

    if (type == 1) {
      size = neighborhood[id].length;
    } else {
      size = population.size();
    }
    int[] perm = new int[size];

    Utils.randomPermutation(perm, size);

    for (int i = 0; i < size; i++) {
      int k;
      if (type == 1) {
        k = neighborhood[id][perm[i]];
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
      // the maximal number of solutions updated is not allowed to exceed 'limit'
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
    }
    else {
      throw new JMetalException(" MOEAD.fitnessFunction: unknown type " + functionType);
    }
    return fitness;
  }

  /** Builder class */
  public static class Builder {
    private int neighborSize;
    private double neighborhoodSelectionProbability;
    private int maximumNumberOfReplacedSolutions;
    private String functionType ;
    private Operator crossover ;
    private Operator mutation ;

    public Builder() {
      functionType = "_TCHE1";
    }

    public Builder neighborSize(int neighborSize) {
      this.neighborSize = neighborSize ;

      return this ;
    }

    public Builder neighborhoodSelectionProbability(int neighborhoodSelectionProbability) {
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

    public MOEAD build() {
      return new MOEAD(this) ;
    }
  }
}

