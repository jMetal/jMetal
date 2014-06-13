//  MOEAD_DRA.java
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
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.random.PseudoRandom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Level;

/**
 * Reference: Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of
 * MOEA/D on CEC09 Unconstrained MOP Test Instances, Working Report CES-491,
 * School of CS & EE, University of Essex, 02/2009
 */
public class MOEAD_DRA extends Algorithm {

  /**
   *
   */
  private static final long serialVersionUID = 4289052728188335534L;
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
  /**
   * Operators
   */
  Operator crossover_;
  Operator mutation_;
  String dataDirectory_;
  private int populationSize_;
  /**
   * Stores the population
   */
  private SolutionSet population_;
  /**
   * Stores the values of the individuals
   */
  private Solution[] savedValues_;
  private double[] utility_;
  private int[] frequency_;

  /**
   * Constructor
   *
   * @param problem Problem to solve
   */
  public MOEAD_DRA() {
    super();

    functionType_ = "_TCHE1";
  }

  public SolutionSet execute() throws JMException, ClassNotFoundException {
    int maxEvaluations;

    evaluations_ = 0;
    maxEvaluations = (Integer) this.getInputParameter("maxEvaluations");
    populationSize_ = (Integer) this.getInputParameter("populationSize");
    dataDirectory_ = this.getInputParameter("dataDirectory").toString();

    population_ = new SolutionSet(populationSize_);
    savedValues_ = new Solution[populationSize_];
    utility_ = new double[populationSize_];
    frequency_ = new int[populationSize_];
    for (int i = 0; i < utility_.length; i++) {
      utility_[i] = 1.0;
      frequency_[i] = 0;
    }
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

    int gen = 0;
    // STEP 2. Update
    do {
      int[] permutation = new int[populationSize_];
      Utils.randomPermutation(permutation, populationSize_);
      List<Integer> order = tour_selection(10);

      for (int i = 0; i < order.size(); i++) {
        int n = order.get(i);
        frequency_[n]++;

        int type;
        double rnd = PseudoRandom.randDouble();

        // STEP 2.1. Mating selection based on probability
        if (rnd < delta_) {
          type = 1;
        } else {
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

        evaluations_++;

        // STEP 2.3. Repair. Not necessary

        // STEP 2.4. Update z_
        updateReference(child);

        // STEP 2.5. Update of solutions
        updateProblem(child, n, type);
      }

      gen++;
      if (gen % 30 == 0) {
        comp_utility();
      }

    } while (evaluations_ < maxEvaluations);

    int final_size = populationSize_;
    try {
      final_size = (Integer) (getInputParameter("finalSize"));
      Configuration.logger_.info("FINAL SIZE: " + final_size);
    } catch (Exception e) { // if there is an exception indicate it!
      Configuration.logger_.log(Level.SEVERE,
        "The final size parameter has been ignored. The number of solutions is " + population_
          .size(),
        e);
      return population_;

    }
    return finalSelection(final_size);
  }


  /**
   * initUniformWeight
   */
  public void initUniformWeight() {
    if ((problem_.getNumberOfObjectives() == 2) && (populationSize_ <= 100)) {
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
          "initUniformWeight: failed when reading for file: " + dataDirectory_ + "/" + dataFileName,
          e);
      }
    }
  }

  public void comp_utility() throws JMException {
    double f1, f2, uti, delta;
    for (int n = 0; n < populationSize_; n++) {
      f1 = fitnessFunction(population_.get(n), lambda_[n]);
      f2 = fitnessFunction(savedValues_[n], lambda_[n]);
      delta = f2 - f1;
      if (delta > 0.001) {
        utility_[n] = 1.0;
      } else {
        uti = (0.95 + (0.05 * delta / 0.001)) * utility_[n];
        utility_[n] = uti < 1.0 ? uti : 1.0;
      }
      savedValues_[n] = new Solution(population_.get(n));
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
      evaluations_++;
      population_.add(newSolution);
      savedValues_[i] = new Solution(newSolution);
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

    ss = neighborhood_[cid].length;
    while (list.size() < size) {
      if (type == 1) {
        r = PseudoRandom.randInt(0, ss - 1);
        p = neighborhood_[cid][r];
      } else {
        p = PseudoRandom.randInt(0, populationSize_ - 1);
      }
      boolean flag = true;
      for (Integer aList : list) {
        if (aList == p) {
          flag = false;
          break;
        }
      }
      if (flag) {
        list.addElement(p);
      }
    }
  }


  public List<Integer> tour_selection(int depth) {
    // selection based on utility
    List<Integer> selected = new ArrayList<Integer>();
    List<Integer> candidate = new ArrayList<Integer>();

    for (int k = 0; k < problem_.getNumberOfObjectives(); k++) {
      // WARNING! HERE YOU HAVE TO USE THE WEIGHT PROVIDED BY QINGFU (NOT SORTED!!!!)
      selected.add(k);
    }


    for (int n = problem_.getNumberOfObjectives(); n < populationSize_; n++) {
      // set of unselected weights
      candidate.add(n);
    }

    while (selected.size() < (int) (populationSize_ / 5.0)) {
      int best_idd = (int) (PseudoRandom.randDouble() * candidate.size());
      int i2;
      int best_sub = candidate.get(best_idd);
      int s2;
      for (int i = 1; i < depth; i++) {
        i2 = (int) (PseudoRandom.randDouble() * candidate.size());
        s2 = candidate.get(i2);
        if (utility_[s2] > utility_[best_sub]) {
          best_idd = i2;
          best_sub = s2;
        }
      }
      selected.add(best_sub);
      candidate.remove(best_idd);
    }
    return selected;
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
    // individual: child solution
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
        k = perm[i];
      }
      double f1, f2;

      f1 = fitnessFunction(population_.get(k), lambda_[k]);
      f2 = fitnessFunction(individual, lambda_[k]);

      if (f2 < f1) {
        population_.replace(k, new Solution(individual));
        time++;
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
    } else {
      throw new JMException("MOEAD.fitnessFunction: unknown type " + functionType_);
    }
    return fitness;
  }


  /**
   * @param n: The number of solutions to return
   * @return A solution set containing those elements
   * @author Juanjo Durililo
   * This method selects N solutions from a set M, where N <= M
   * using the same method proposed by Qingfu Zhang, W. Liu, and Hui Li in
   * the paper describing MOEA/D-DRA (CEC 09 COMPTETITION)
   * An example is giving in that paper for two objectives.
   * If N = 100, then the best solutions  attenting to the weights (0,1),
   * (1/99,98/99), ...,(98/99,1/99), (1,0) are selected.
   * <p/>
   * Using this method result in 101 solutions instead of 100. We will just
   * compute 100 even distributed weights and used them. The result is the same
   * <p/>
   * In case of more than two objectives the procedure is:
   * 1- Select a solution at random
   * 2- Select the solution from the population which have maximum distance to
   * it (whithout considering the already included)
   */
  SolutionSet finalSelection(int n) throws JMException {
    SolutionSet res = new SolutionSet(n);
    if (problem_.getNumberOfObjectives() == 2) {
      double[][] internLambda = new double[n][2];
      for (int i = 0; i < n; i++) {
        double a = 1.0 * i / (n - 1);
        internLambda[i][0] = a;
        internLambda[i][1] = 1 - a;
      }

      // we have now the weights, now select the best solution for each of them
      for (int i = 0; i < n; i++) {
        Solution currentBest = population_.get(0);
        int index = 0;
        double value = fitnessFunction(currentBest, internLambda[i]);
        for (int j = 1; j < n; j++) {
          // we are looking the best for the weight i
          double aux = fitnessFunction(population_.get(j), internLambda[i]);
          if (aux < value) {
            // solution in position j is better!
            value = aux;
            currentBest = population_.get(j);
          }
        }
        res.add(new Solution(currentBest));
      }

    } else {
      // general case (more than two objectives)
      Distance distance_utility = new Distance();
      int randomIndex = PseudoRandom.randInt(0, population_.size() - 1);

      // create a list containing all the solutions but the selected one (only references to them)
      List<Solution> candidate = new LinkedList<Solution>();
      candidate.add(population_.get(randomIndex));


      for (int i = 0; i < population_.size(); i++) {
        if (i != randomIndex) {
          candidate.add(population_.get(i));
        }
      }

      while (res.size() < n) {
        int index = 0;
        // it should be a next! (n <= population size!)
        Solution selected = candidate.get(0);
        double distanceValue =
          distance_utility.distanceToSolutionSetInObjectiveSpace(selected, res);
        int i = 1;
        while (i < candidate.size()) {
          Solution nextCandidate = candidate.get(i);
          double aux = distanceValue =
            distance_utility.distanceToSolutionSetInObjectiveSpace(nextCandidate, res);
          if (aux > distanceValue) {
            distanceValue = aux;
            index = i;
          }
          i++;
        }

        // add the selected to res and remove from candidate list
        res.add(new Solution(candidate.remove(index)));
      }
    }
    return res;
  }
} 
