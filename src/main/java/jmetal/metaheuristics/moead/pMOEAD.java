//  pMOEAD.java
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
import jmetal.util.random.PseudoRandom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class implementing the pMOEA/D algorithm
 */
public class pMOEAD extends Algorithm implements Runnable {

  /**
   *
   */
  private static final long serialVersionUID = 4567976724590665092L;
  public HashMap<String, Object> map_;
  /**
   * Z vector (ideal point)
   */
  double[] z_;
  /**
   * Lambda vectors
   */
  double[][] lambda_;
  /**
   * T: neighbor size
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
  int maxEvaluations_;
  /**
   * Operators
   */
  Operator crossover_;
  Operator mutation_;
  int id_;
  pMOEAD parentThread_;
  Thread[] thread_;
  String dataDirectory_;
  CyclicBarrier barrier_;
  long initTime_;
  /**
   * Population size
   */
  private int populationSize_;
  /**
   * Stores the population
   */
  private SolutionSet population_;
  /**
   * Number of threads
   */
  private int numberOfThreads_;

  /**
   * Constructor
   *
   * @param problem Problem to solve
   */
  public pMOEAD() {
    super();
    parentThread_ = null;

    functionType_ = "_TCHE1";

    id_ = 0;
  }

  /**
   * Constructor
   *
   * @param problem Problem to solve
   */

  public pMOEAD(pMOEAD parentThread, int id, int numberOfThreads) {
    super();
    parentThread_ = parentThread;

    numberOfThreads_ = numberOfThreads;
    thread_ = new Thread[numberOfThreads_];

    functionType_ = "_TCHE1";

    id_ = id;
  }

  public void run() {
    neighborhood_ = parentThread_.neighborhood_;
    problem_ = parentThread_.problem_;
    lambda_ = parentThread_.lambda_;
    population_ = parentThread_.population_;
    z_ = parentThread_.z_;
    indArray_ = parentThread_.indArray_;
    barrier_ = parentThread_.barrier_;

    int partitions = parentThread_.populationSize_ / parentThread_.numberOfThreads_;

    evaluations_ = 0;
    maxEvaluations_ = parentThread_.maxEvaluations_ / parentThread_.numberOfThreads_;

    try {
      //Configuration.logger_.info("en espera: " + barrier_.getNumberWaiting()) ;
      barrier_.await();
      //Configuration.logger_.info("Running: " + id_ ) ;
    } catch (InterruptedException e) {
      Configuration.logger_.log(Level.SEVERE, "Error", e);
    } catch (BrokenBarrierException e) {
      Configuration.logger_.log(Level.SEVERE, "Error", e);
    }

    int first;
    int last;

    first = partitions * id_;
    if (id_ == (parentThread_.numberOfThreads_ - 1)) {
      last = parentThread_.populationSize_ - 1;
    } else {
      last = first + partitions - 1;
    }

    Configuration.logger_.info("Id: " + id_ + "  Partitions: " + partitions +
      " First: " + first + " Last: " + last);

    do {
      for (int i = first; i <= last; i++) {
        int n = i;
        int type;
        double rnd = PseudoRandom.randDouble();

        // STEP 2.1. Mating selection based on probability
        if (rnd < parentThread_.delta_) {
          // neighborhood
          type = 1;
        } else {
          // whole population
          type = 2;
        }

        Vector<Integer> p = new Vector<Integer>();
        this.matingSelection(p, n, 2, type);

        // STEP 2.2. Reproduction
        Solution child = null;
        Solution[] parents = new Solution[3];

        try {
          synchronized (parentThread_) {
            parents[0] = parentThread_.population_.get(p.get(0));
            parents[1] = parentThread_.population_.get(p.get(1));
            parents[2] = parentThread_.population_.get(n);
            // Apply DE crossover
            child = (Solution) parentThread_.crossover_
              .execute(new Object[] {parentThread_.population_.get(n), parents});
          }
          // Apply mutation
          parentThread_.mutation_.execute(child);

          // Evaluation
          parentThread_.problem_.evaluate(child);

        } catch (JMException ex) {
          Logger.getLogger(pMOEAD.class.getName()).log(Level.SEVERE, null, ex);
        }

        evaluations_++;

        // STEP 2.3. Repair. Not necessary

        // STEP 2.4. Update z_
        updateReference(child);

        // STEP 2.5. Update of solutions
        try {
          updateOfSolutions(child, n, type);
        } catch (JMException e) {
          Configuration.logger_.log(Level.SEVERE, "Error", e);
        }
      }
    } while (evaluations_ < maxEvaluations_);

    long estimatedTime = System.currentTimeMillis() - parentThread_.initTime_;
    Configuration.logger_.info("Time thread " + id_ + ": " + estimatedTime);
  }

  public SolutionSet execute() throws JMException, ClassNotFoundException {
    parentThread_ = this;

    evaluations_ = 0;
    maxEvaluations_ = (Integer) this.getInputParameter("maxEvaluations");
    populationSize_ = (Integer) this.getInputParameter("populationSize");
    dataDirectory_ = this.getInputParameter("dataDirectory").toString();
    numberOfThreads_ = (Integer) this.getInputParameter("numberOfThreads");

    thread_ = new Thread[numberOfThreads_];

    barrier_ = new CyclicBarrier(numberOfThreads_);

    population_ = new SolutionSet(populationSize_);
    indArray_ = new Solution[problem_.getNumberOfObjectives()];

    T_ = (Integer) this.getInputParameter("T");
    nr_ = (Integer) this.getInputParameter("nr");
    delta_ = (Double) this.getInputParameter("delta");

    neighborhood_ = new int[populationSize_][T_];

    z_ = new double[problem_.getNumberOfObjectives()];
    lambda_ = new double[populationSize_][problem_.getNumberOfObjectives()];

    crossover_ = operators_.get("crossover");
    mutation_ = operators_.get("mutation");

    // STEP 1. Initialization
    // STEP 1.1. Compute euclidean distances between weight vectors and find T
    initUniformWeight();

    initNeighborhood();

    // STEP 1.2. Initialize population
    initPopulation();

    // STEP 1.3. Initialize z_
    initIdealPoint();

    initTime_ = System.currentTimeMillis();

    for (int i = 0; i < numberOfThreads_; i++) {
      thread_[i] = new Thread(new pMOEAD(this, i, numberOfThreads_), "pepe");
      thread_[i].start();
    }

    for (int i = 0; i < numberOfThreads_; i++) {
      try {
        thread_[i].join();
      } catch (InterruptedException ex) {
        Logger.getLogger(pMOEAD.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    return population_;
  }

  /**
   * Initialize neighborhoods
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
   * Initialize weights
   */
  public void initUniformWeight() {
    if ((problem_.getNumberOfObjectives() == 2) && (populationSize_ < 300)) {
      for (int n = 0; n < populationSize_; n++) {
        double a = 1.0 * n / (populationSize_ - 1);
        lambda_[n][0] = a;
        lambda_[n][1] = 1 - a;
      }
    } else {
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
          "initUniformWeight: fail when reading for file: " + dataDirectory_ + "/" + dataFileName,
          e);
      }
    }
  }

  /**
   *
   */
  public void initPopulation() throws JMException, ClassNotFoundException {
    for (int i = 0; i < populationSize_; i++) {
      Solution newSolution = new Solution(problem_);

      problem_.evaluate(newSolution);
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

    ss = parentThread_.neighborhood_[cid].length;
    while (list.size() < size) {
      if (type == 1) {
        r = PseudoRandom.randInt(0, ss - 1);
        p = parentThread_.neighborhood_[cid][r];
      } else {
        p = PseudoRandom.randInt(0, parentThread_.populationSize_ - 1);
      }
      boolean flag = true;
      for (int i = 0; i < list.size(); i++) {
        if (list.get(i) == p) {
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
   * Update reference point
   *
   * @param individual
   */
  synchronized void updateReference(Solution individual) {
    for (int n = 0; n < parentThread_.problem_.getNumberOfObjectives(); n++) {
      if (individual.getObjective(n) < z_[n]) {
        parentThread_.z_[n] = individual.getObjective(n);
        parentThread_.indArray_[n] = individual;
      }
    }
  }

  /**
   * @param individual
   * @param id
   * @param type
   */
  void updateOfSolutions(Solution individual, int id, int type) throws JMException {
    // individual: child solution
    // id:   the id of current subproblem
    // type: update solutions in - neighborhood (1) or whole population (otherwise)
    int size;
    int time;

    time = 0;

    if (type == 1) {
      size = parentThread_.neighborhood_[id].length;
    } else {
      size = parentThread_.population_.size();
    }
    int[] perm = new int[size];

    Utils.randomPermutation(perm, size);

    for (int i = 0; i < size; i++) {
      int k;
      if (type == 1) {
        k = parentThread_.neighborhood_[id][perm[i]];
      } else {
        k = perm[i];
      }
      double f1, f2;

      f2 = fitnessFunction(individual, parentThread_.lambda_[k]);
      synchronized (parentThread_) {
        f1 = fitnessFunction(parentThread_.population_.get(k), parentThread_.lambda_[k]);

        if (f2 < f1) {
          parentThread_.population_.replace(k, new Solution(individual));
          time++;
        }
      }
      // the maximal number of solutions updated is not allowed to exceed 'limit'
      if (time >= parentThread_.nr_) {
        return;
      }
    }
  }

  double fitnessFunction(Solution individual, double[] lambda) throws JMException {
    double fitness;
    fitness = 0.0;

    if ("_TCHE1".equals(parentThread_.functionType_)) {
      double maxFun = -1.0e+30;

      for (int n = 0; n < parentThread_.problem_.getNumberOfObjectives(); n++) {
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
    } else {
      throw new JMException("pMOEAD.fitnessFunction: unknown type " + functionType_);
    }
    return fitness;
  }
}

