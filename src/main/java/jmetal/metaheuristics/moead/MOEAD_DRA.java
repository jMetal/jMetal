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
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Reference: Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of 
 * MOEA/D on CEC09 Unconstrained MOP Test Instances, Working Report CES-491, 
 * School of CS & EE, University of Essex, 02/2009 
  */
public class MOEAD_DRA extends Algorithm {

	private int populationSize_;
  /**
   * Stores the population
   */
  private SolutionSet population_;
  /**
   * Stores the values of the individuals
   */
  private Solution [] savedValues_;


  private double [] utility_;
  private int [] frequency_;

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
   * Constructor
   * @param problem Problem to solve
   */
  public MOEAD_DRA(Problem problem) {
    super (problem) ;

    functionType_ = "_TCHE1";

  } // DMOEA

  public SolutionSet execute() throws JMException, ClassNotFoundException {
    int maxEvaluations;

    evaluations_ = 0;
    maxEvaluations = ((Integer) this.getInputParameter("maxEvaluations")).intValue();
    populationSize_ = ((Integer) this.getInputParameter("populationSize")).intValue();
    dataDirectory_ = this.getInputParameter("dataDirectory").toString();

    population_  = new SolutionSet(populationSize_);
    savedValues_ = new Solution[populationSize_];
    utility_     = new double[populationSize_];
    frequency_   = new int[populationSize_];
    for (int i = 0; i < utility_.length; i++) {
        utility_[i] = 1.0;
        frequency_[i] = 0;
    }
    indArray_    = new Solution[problem_.getNumberOfObjectives()];

    //T_ = 20;
    //delta_ = 0.9;
    //nr_ = 2;

    //T_ = (int) (0.1 * populationSize_);
    //delta_ = 0.9;
    //nr_ = (int) (0.01 * populationSize_);

    T_ = ((Integer) this.getInputParameter("T")).intValue();
    nr_ = ((Integer) this.getInputParameter("nr")).intValue();
    delta_ = ((Double) this.getInputParameter("delta")).doubleValue();

    neighborhood_ = new int[populationSize_][T_];

    z_ = new double[problem_.getNumberOfObjectives()];
    //lambda_ = new Vector(problem_.getNumberOfObjectives()) ;
    lambda_ = new double[populationSize_][problem_.getNumberOfObjectives()];

    crossover_ = operators_.get("crossover"); // default: DE crossover
    mutation_ = operators_.get("mutation");  // default: polynomial mutation

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
        //int n = permutation[i]; // or int n = i;
        int n = order.get(i) ; // or int n = i;
        frequency_[n]++;

        int type;
        double rnd = PseudoRandom.randDouble();

        // STEP 2.1. Mating selection based on probability
        if (rnd < delta_) // if (rnd < realb)
        {
          type = 1;   // neighborhood
        } else {
          type = 2;   // whole population
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
        child = (Solution) crossover_.execute(new Object[]{population_.get(n), parents});

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
      } // for

      gen++;
        if(gen%30==0)
        {
		    comp_utility();
        }

    } while (evaluations_ < maxEvaluations);

    int final_size = populationSize_;
      try {
       final_size= (Integer) (getInputParameter("finalSize"));
       System.out.println("FINAL SOZE: " + final_size) ;
    } catch (Exception e) { // if there is an exception indicate it!
      System.err.println("The final size paramater has been ignored");
      System.err.println("The number of solutions is " + population_.size());
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
  //      System.out.println(lambda_[n][0]);
  //      System.out.println(lambda_[n][1]);
      } // for
    } // if
    else {
      String dataFileName;
      dataFileName = "W" + problem_.getNumberOfObjectives() + "D_" +
        populationSize_ + ".dat";

//      System.out.println(dataDirectory_);
//      System.out.println(dataDirectory_ + "/" + dataFileName);

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
            double value = (new Double(st.nextToken())).doubleValue();
            lambda_[i][j] = value;
            //System.out.println("lambda["+i+","+j+"] = " + value) ;
            j++;
          }
          aux = br.readLine();
          i++;
        }
        br.close();
      } catch (Exception e) {
        System.err.println("initUniformWeight: failed when reading for file: " + dataDirectory_ + "/" + dataFileName);
        e.printStackTrace();
      }
    } // else

    //System.exit(0) ;
  } // initUniformWeight



    public void comp_utility()
    {
        double f1, f2, uti, delta;
        for(int n=0; n<populationSize_; n++)
        {
            f1 = fitnessFunction(population_.get(n), lambda_[n]);
            f2 = fitnessFunction(savedValues_[n], lambda_[n]);
            delta = f2 - f1;
            if(delta>0.001)  
              utility_[n] = 1.0;
            else{
                // uti = 0.95*(1.0+delta/0.001)*utility_[n];
                uti = (0.95 + (0.05 * delta/0.001)) * utility_[n];
                utility_[n] = uti<1.0?uti:1.0;
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
        //x[j] = dist_vector(population[i].namda,population[j].namda);
        idx[j] = j;
      //System.out.println("x["+j+"]: "+x[j]+ ". idx["+j+"]: "+idx[j]) ;
      } // for

      // find 'niche' nearest neighboring subproblems
      Utils.minFastSort(x, idx, populationSize_, T_);
      //minfastsort(x,idx,population.size(),niche);

        System.arraycopy(idx, 0, neighborhood_[i], 0, T_);
    } // for
  } // initNeighborhood

  /**
   *
   */
  public void initPopulation() throws JMException, ClassNotFoundException {
    for (int i = 0; i < populationSize_; i++) {
      Solution newSolution = new Solution(problem_);

      problem_.evaluate(newSolution);
      evaluations_++;
      population_.add(newSolution) ;
      savedValues_[i] = new Solution(newSolution);
    } // for
  } // initPopulation

  /**
   *
   */
  void initIdealPoint() throws JMException, ClassNotFoundException {
    for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
      z_[i] = 1.0e+30;
      indArray_[i] = new Solution(problem_);
      problem_.evaluate(indArray_[i]);
      evaluations_++;
    } // for

    for (int i = 0; i < populationSize_; i++) {
      updateReference(population_.get(i));
    } // for
  } // initIdealPoint

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
      for (int i = 0; i < list.size(); i++) {
        if (list.get(i) == p) // p is in the list
        {
          flag = false;
          break;
        }
      }

      //if (flag) list.push_back(p);
      if (flag) {
        list.addElement(p);
      }
    }
  } // matingSelection


   public List<Integer> tour_selection(int depth)
   {

	// selection based on utility
  List<Integer> selected     = new ArrayList<Integer>();
	List<Integer> candidate    = new ArrayList<Integer>();

	for(int k=0; k<problem_.getNumberOfObjectives(); k++)
      selected.add(k);   // WARNING! HERE YOU HAVE TO USE THE WEIGHT PROVIDED BY QINGFU (NOT SORTED!!!!)
  
  
	for(int n=problem_.getNumberOfObjectives(); n<populationSize_; n++)
            candidate.add(n);  // set of unselected weights

	while(selected.size()<(int)(populationSize_/5.0))
	{
	    //int best_idd = (int) (rnd_uni(&rnd_uni_init)*candidate.size()), i2;
            int best_idd = (int) (PseudoRandom.randDouble()*candidate.size());
            //System.out.println(best_idd);
            int i2;
	    int best_sub = candidate.get(best_idd);
            int s2;
            for(int i=1; i<depth; i++)
            {
                i2  = (int) (PseudoRandom.randDouble()*candidate.size());
                s2  = candidate.get(i2);
                //System.out.println("Candidate: "+i2);
                if(utility_[s2]>utility_[best_sub])
                {
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
   *
   * @param individual
   */
  void updateReference(Solution individual) {
    for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
      if (individual.getObjective(n) < z_[n]) {
        z_[n] = individual.getObjective(n);

        indArray_[n] = individual;
      }
    }
  } // updateReference

  /**
   * @param individual
   * @param id
   * @param type
   */
  void updateProblem(Solution indiv, int id, int type) {
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
        k = perm[i];      // calculate the values of objective function regarding the current subproblem
      }
      double f1, f2;

      f1 = fitnessFunction(population_.get(k), lambda_[k]);
      f2 = fitnessFunction(indiv, lambda_[k]);

      if (f2 < f1) {
        population_.replace(k, new Solution(indiv));
        //population[k].indiv = indiv;
        time++;
      }
      // the maximal number of solutions updated is not allowed to exceed 'limit'
      if (time >= nr_) {
        return;
      }
    }
  } // updateProblem

  double fitnessFunction(Solution individual, double[] lambda) {
    double fitness;
    fitness = 0.0;

    if (functionType_.equals("_TCHE1")) {
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
      } // for

      fitness = maxFun;
    } // if
    else {
      System.err.println("MOEAD.fitnessFunction: unknown type " + functionType_);
      System.exit(-1);
    }
    return fitness;
  } // fitnessEvaluation
  
  
  /** @author Juanjo
   * This method selects N solutions from a set M, where N <= M
   * using the same method proposed by Qingfu Zhang, W. Liu, and Hui Li in
   * the paper describing MOEA/D-DRA (CEC 09 COMPTETITION)
   * An example is giving in that paper for two objectives. 
   * If N = 100, then the best solutions  attenting to the weights (0,1), 
   * (1/99,98/99), ...,(98/99,1/99), (1,0) are selected. 
   * 
   * Using this method result in 101 solutions instead of 100. We will just 
   * compute 100 even distributed weights and used them. The result is the same
   * 
   * In case of more than two objectives the procedure is:
   * 1- Select a solution at random
   * 2- Select the solution from the population which have maximum distance to
   * it (whithout considering the already included)
   * 
   * 
   * 
   * @param n: The number of solutions to return
   * @return A solution set containing those elements
   * 
   */
   SolutionSet finalSelection(int n) throws JMException {
     SolutionSet res = new SolutionSet(n);
     if (problem_.getNumberOfObjectives() == 2) { // subcase 1                     
       double [][] intern_lambda = new double[n][2];
       for (int i = 0; i < n; i++) {
         double a = 1.0 * i / (n - 1);
         intern_lambda[i][0] = a;
         intern_lambda[i][1] = 1 - a;                
       } // for
       
       // we have now the weights, now select the best solution for each of them
       for (int i = 0; i < n; i++) {
         Solution current_best = population_.get(0);
         int index             = 0;
         double value          = fitnessFunction(current_best, intern_lambda[i]);
         for (int j = 1; j < n; j++) {           
             double aux = fitnessFunction(population_.get(j),intern_lambda[i]); // we are looking the best for the weight i
             if (aux < value) { // solution in position j is better!               
               value = aux;
               current_best = population_.get(j);           
           }
         }
         res.add(new Solution(current_best));
       }
       
     } else { // general case (more than two objectives)
       
       Distance distance_utility = new Distance();
       int random_index = PseudoRandom.randInt(0, population_.size()-1);
       
       // create a list containing all the solutions but the selected one (only references to them)
       List<Solution> candidate = new LinkedList<Solution>();
       candidate.add(population_.get(random_index));
       
       
       for (int i = 0; i< population_.size(); i++) {
         if (i != random_index)
          candidate.add(population_.get(i));
       } // for
       
       while (res.size() < n) {
         int index = 0;
         Solution selected = candidate.get(0); // it should be a next! (n <= population size!)
         double   distance_value = distance_utility.distanceToSolutionSetInObjectiveSpace(selected, res);
         int i = 1;
         while (i < candidate.size()) {
           Solution next_candidate = candidate.get(i);
           double aux = distance_value = distance_utility.distanceToSolutionSetInObjectiveSpace(next_candidate, res);
           if (aux > distance_value) {
             distance_value = aux;
             index = i;
           }
           i++;
         }
         
         // add the selected to res and remove from candidate list
         res.add(new Solution(candidate.remove(index)));                           
       } // 
     }
     return res;
   }
} // MOEAD_DRA
