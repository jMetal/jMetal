//  cMOEAD.java
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
package jmetal.metaheuristics.moead;


import jmetal.core.*;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.comparator.IConstraintViolationComparator;
import jmetal.util.comparator.ViolationThresholdComparator;
import jmetal.util.random.PseudoRandom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;

// This class implements a constrained version of the MOEAD algorithm based on
// the paper:
// "An adaptive constraint handling approach embedded MOEA/D". DOI: 10.1109/CEC.2012.6252868
//
public class cMOEAD extends Algorithm {

  /**
   *
   */
  private static final long serialVersionUID = 6039874453533436343L;
  /**
   * Z vector (ideal point)
   */
  double[] z_;
  /**
   * Lambda vectors
   */
  //Vector<Vector<Double>> lambda_ ;
  double[][] lambda_;
  /**
   * T: neighbour size
   */
  int T_;
  /**
   * Neighborhood
   */
  int[][] neighborhood_;
  /**
   * delta: probability that parent solutions are selected from neighbourhood
   */
  double delta_;
  /**
   * nr: maximal number of solutions replaced by each child solution
   */
  int nr_;
  Solution[] indArray_;
  String functionType_;
  int evaluations_;
  /**
   * Operators
   */
  Operator crossover_;
  Operator mutation_;
  String dataDirectory_;
  /**
   * Use this encodings.variable as comparator for the constraints
   */
  IConstraintViolationComparator comparator = new ViolationThresholdComparator();
  private int populationSize_;
  /**
   * Stores the population
   */
  private SolutionSet population_;

  /**
   * Constructor
   *
   * @param problem Problem to solve
   */
  public cMOEAD() {
    super();

    functionType_ = "_TCHE1";

  } // DMOEA

  public SolutionSet execute() throws JMException, ClassNotFoundException {
    int maxEvaluations;

    evaluations_ = 0;
    maxEvaluations = (Integer) this.getInputParameter("maxEvaluations");
    populationSize_ = (Integer) this.getInputParameter("populationSize");
    dataDirectory_ = this.getInputParameter("dataDirectory").toString();
    Configuration.logger_.info("POPSIZE: " + populationSize_);

    population_ = new SolutionSet(populationSize_);
    indArray_ = new Solution[problem_.getNumberOfObjectives()];

    T_ = (Integer) this.getInputParameter("T");
    nr_ = (Integer) this.getInputParameter("nr");
    delta_ = (Double) this.getInputParameter("delta");

    /*
    T_ = 20;
    delta_ = 0.9;
    nr_ = 2;
     */
    /*
    T_ = (int) (0.1 * populationSize_);
    delta_ = 0.9;
    nr_ = (int) (0.01 * populationSize_);
     */
    neighborhood_ = new int[populationSize_][T_];

    z_ = new double[problem_.getNumberOfObjectives()];
    //lambda_ = new Vector(problem_.getNumberOfObjectives()) ;
    lambda_ = new double[populationSize_][problem_.getNumberOfObjectives()];

    crossover_ = operators_.get("crossover");
    mutation_ = operators_.get("mutation");

    // STEP 1. Initialization
    // STEP 1.1. Compute euclidean distances between weight vectors and find T
    initUniformWeight();

    initNeighborhood();

    // STEP 1.2. Initialize population
    initPopulation();
    ((ViolationThresholdComparator) this.comparator).updateThreshold(this.population_);

    // STEP 1.3. Initialize z_
    initIdealPoint();

    // STEP 2. Update
    do {
      int[] permutation = new int[populationSize_];
      Utils.randomPermutation(permutation, populationSize_);

      for (int i = 0; i < populationSize_; i++) {
        int n = permutation[i]; // or int n = i;
        //int n = i ; // or int n = i;
        int type;
        double rnd = PseudoRandom.randDouble();

        // STEP 2.1. Mating selection based on probability
        if (rnd < delta_) {
          // neighborhood
          type = 1;
        } else {
          // whole population
          type = 2;
        }
        Vector<Integer> p = new Vector<Integer>();
        matingSelection(p, n, 2, type);

        // STEP 2.2. Reproduction
        Solution child;
        Solution[] parents = new Solution[3];

        parents[0] = population_.get(p.get(0));
        parents[1] = population_.get(p.get(1));
        parents[2] = population_.get(n);

        // Apply DE crossover 
        child = (Solution) crossover_.execute(new Object[] {population_.get(n), parents});

        // Apply mutation
        mutation_.execute(child);

        // Evaluation
        problem_.evaluate(child);
        problem_.evaluateConstraints(child);

        evaluations_++;

        // STEP 2.3. Repair. Not necessary

        // STEP 2.4. Update z_
        updateReference(child);

        // STEP 2.5. Update of solutions
        updateProblem(child, n, type);
      } // for 
      ((ViolationThresholdComparator) this.comparator).updateThreshold(this.population_);
    } while (evaluations_ < maxEvaluations);

    return population_;
  }


  /**
   * initUniformWeight
   */
  public void initUniformWeight() {
    if ((problem_.getNumberOfObjectives() == 2) && (populationSize_ <= 300)) {
      for (int n = 0; n < populationSize_; n++) {
        double a = 1.0 * n / (populationSize_ - 1);
        lambda_[n][0] = a;
        lambda_[n][1] = 1 - a;
      }
    }
    else {
      String dataFileName;
      dataFileName = "W" + problem_.getNumberOfObjectives() + "D_" +
        populationSize_ + ".dat";

      try {
        // Open the file
        FileInputStream fis = new FileInputStream(dataDirectory_ + "/" + dataFileName);
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
            lambda_[i][j] = value;
            j++;
          }
          aux = br.readLine();
          i++;
        }
        br.close();
      } catch (Exception e) {
        Configuration.logger_.log(
          Level.SEVERE,
          "initUniformWeight: failed when reading for file: " + dataDirectory_ + "/" + dataFileName,
          e);
      }
    }
  }

  /**
   *
   */
  public void initNeighborhood() {
    double[] x = new double[populationSize_];
    int[] idx = new int[populationSize_];

    for (int i = 0; i < populationSize_; i++) {
      // calculate the distances based on weight vectors
      for (int j = 0; j < populationSize_; j++) {
        x[j] = Utils.distVector(lambda_[i], lambda_[j]);
        idx[j] = j;
      }

      // find 'niche' nearest neighboring subproblems
      Utils.minFastSort(x, idx, populationSize_, T_);

      System.arraycopy(idx, 0, neighborhood_[i], 0, T_);
    }
  }

  /**
   *
   */
  public void initPopulation() throws JMException, ClassNotFoundException {
    for (int i = 0; i < populationSize_; i++) {
      Solution newSolution = new Solution(problem_);

      problem_.evaluate(newSolution);
      problem_.evaluateConstraints(newSolution);
      evaluations_++;
      population_.add(newSolution);
    }
  }

  /**
   *
   */
  void initIdealPoint() throws JMException, ClassNotFoundException {
    for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
      z_[i] = 1.0e+30;
      indArray_[i] = new Solution(problem_);
      problem_.evaluate(indArray_[i]);
      problem_.evaluateConstraints(indArray_[i]);
      evaluations_++;
    }

    for (int i = 0; i < populationSize_; i++) {
      updateReference(population_.get(i));
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

    ss = neighborhood_[cid].length;
    while (list.size() < size) {
      if (type == 1) {
        r = PseudoRandom.randInt(0, ss - 1);
        p = neighborhood_[cid][r];
        //p = population[cid].table[r];
      } else {
        p = PseudoRandom.randInt(0, populationSize_ - 1);
      }
      boolean flag = true;
      for (Integer aList : list) {

        if (aList == p) {
          // p is in the list
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
      if (individual.getObjective(n) < z_[n]) {
        z_[n] = individual.getObjective(n);

        indArray_[n] = individual;
      }
    }
  }

  /**
   * @param individual
   * @param id
   * @param type
   */
  void updateProblem(Solution individual, int id, int type) throws JMException {
    // indiv: child solution
    // id:   the id of current subproblem
    // type: update solutions in - neighborhood (1) or whole population (otherwise)
    int size;
    int time;

    time = 0;

    if (type == 1) {
      size = neighborhood_[id].length;
    } else {
      size = population_.size();
    }
    int[] perm = new int[size];

    Utils.randomPermutation(perm, size);

    for (int i = 0; i < size; i++) {
      int k;
      if (type == 1) {
        k = neighborhood_[id][perm[i]];
      } else {
        k =
          perm[i];
      }
      double f1, f2;

      f1 = fitnessFunction(population_.get(k), lambda_[k]);
      f2 = fitnessFunction(individual, lambda_[k]);


      /***** This part is new according to the violation of constraints *****/
      if (comparator.needToCompare(population_.get(k), individual)) {
        int flag = comparator.compare(population_.get(k), individual);
        if (flag == 1) {
          population_.replace(k, new Solution(individual));
        } else if (flag == 0) {
          if (f2 < f1) {
            population_.replace(k, new Solution(individual));
            time++;
          }
        }
      } else {
        if (f2 < f1) {
          population_.replace(k, new Solution(individual));
          time++;
        }
      }
      // the maximal number of solutions updated is not allowed to exceed 'limit'
      if (time >= nr_) {
        return;
      }
    }
  }

  double fitnessFunction(Solution individual, double[] lambda) throws JMException {
    double fitness;
    fitness = 0.0;

    if ("_TCHE1".equals(functionType_)) {
      double maxFun = -1.0e+30;

      for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
        double diff = Math.abs(individual.getObjective(n) - z_[n]);

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
      throw new JMException("cMOEAD.fitnessFunction: unknown type " + functionType_);
    }
    return fitness;
  }
}
