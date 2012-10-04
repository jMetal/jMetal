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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.util.*;

import java.util.Vector;

import jmetal.core.*;
import jmetal.util.PseudoRandom;

/**
 * Class implemeting the pMOEA/D algorithm
 */
public class pMOEAD extends Algorithm implements Runnable {

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
	int maxEvaluations_;
	/**
	 * Operators
	 */
	Operator crossover_;
	Operator mutation_;
	int id_;
	public HashMap<String, Object> map_;
	pMOEAD parentThread_;
	Thread[] thread_;

	String dataDirectory_;
	
	CyclicBarrier barrier_ ;

	long initTime_ ;

	/**
	 * Constructor
	 * @param problem Problem to solve
	 */
	public pMOEAD(Problem problem) {
    super (problem) ;
		parentThread_ = null;

		functionType_ = "_TCHE1";

		id_ = 0;
	} // DMOEA

	/**
	 * Constructor
	 * @param problem Problem to solve
	 */

	public pMOEAD(pMOEAD parentThread, Problem problem, int id, int numberOfThreads) {
    super (problem) ;
		parentThread_ = parentThread;
		
		numberOfThreads_ = numberOfThreads;
		thread_ = new Thread[numberOfThreads_];

		functionType_ = "_TCHE1";

		id_ = id;
	} // DMOEA

	public void run() {
		
		neighborhood_ = parentThread_.neighborhood_ ;
		problem_      = parentThread_.problem_ ;
		lambda_       = parentThread_.lambda_ ;
		population_   = parentThread_.population_ ;
		z_            = parentThread_.z_;
		indArray_     = parentThread_.indArray_ ;
		barrier_      = parentThread_.barrier_ ;

	
		int partitions = parentThread_.populationSize_ / parentThread_.numberOfThreads_;

		evaluations_ = 0;
		maxEvaluations_ = parentThread_.maxEvaluations_ / parentThread_.numberOfThreads_;
	

		try {
			//System.out.println("en espera: " + barrier_.getNumberWaiting()) ;
			barrier_.await();
			//System.out.println("Running: " + id_ ) ;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		int first;
		int last;

		first = partitions * id_;
		if (id_ == (parentThread_.numberOfThreads_ - 1)) {
			last = parentThread_.populationSize_ - 1;
		} else {
			last = first + partitions - 1;
		}

		System.out.println("Id: " + id_ + "  Partitions: " + partitions +
				" First: " + first + " Last: " + last);

		do {
			// int[] permutation = new int[populationSize_];
			//Utils.randomPermutation(permutation, populationSize_);

			for (int i = first; i <= last; i++) {
				//int n = permutation[i]; // or int n = i;
				int n = i; // or int n = i;
				int type;
				double rnd = PseudoRandom.randDouble();

				// STEP 2.1. Mating selection based on probability
				if (rnd < parentThread_.delta_) // if (rnd < realb)
				{
					type = 1;   // neighborhood
				} else {
					type = 2;   // whole population
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
						child = (Solution) parentThread_.crossover_.execute(new Object[]{parentThread_.population_.get(n), parents});
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
				updateOfSolutions(child, n, type);
			} // for
		} while (evaluations_ < maxEvaluations_);

		long estimatedTime = System.currentTimeMillis() - parentThread_.initTime_;
		System.out.println("Time thread " + id_ +": " + estimatedTime) ;
	}

	public SolutionSet execute() throws JMException, ClassNotFoundException {
		parentThread_ = this ;

		evaluations_ = 0;
		maxEvaluations_ = ((Integer) this.getInputParameter("maxEvaluations")).intValue();
		populationSize_ = ((Integer) this.getInputParameter("populationSize")).intValue();
		dataDirectory_ = this.getInputParameter("dataDirectory").toString();
		numberOfThreads_ = ((Integer) this.getInputParameter("numberOfThreads")).intValue();

		thread_ = new Thread[numberOfThreads_];

		barrier_ = new CyclicBarrier(numberOfThreads_) ;
		
		population_ = new SolutionSet(populationSize_);
		indArray_ = new Solution[problem_.getNumberOfObjectives()];

		T_ = 20; 
		delta_ = 0.9;
		nr_ = 2;

		//T_ = (int) (0.1 * populationSize_);
		//delta_ = 0.9;
		//nr_ = (int) (0.01 * populationSize_);

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

		initTime_ = System.currentTimeMillis();

		for (int i = 0; i < numberOfThreads_; i++) {
			thread_[i] = new Thread(new pMOEAD(this, problem_, i, numberOfThreads_), "pepe");
			thread_[i].start();
		}

		for (int i = 0; i < numberOfThreads_; i++) {
			try {
				thread_[i].join();
				//long estimatedTime = System.currentTimeMillis() - initTime;
				//System.out.println("Time thread " + i +": " + estimatedTime) ;
			} catch (InterruptedException ex) {
				Logger.getLogger(pMOEAD.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		return population_;
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

			for (int k = 0; k < T_; k++) {
				neighborhood_[i][k] = idx[k];
			}
		} // for
	} // initNeighborhood

	public void initUniformWeight() {
		if ((problem_.getNumberOfObjectives() == 2) && (populationSize_ < 300)) {
			for (int n = 0; n < populationSize_; n++) {
				double a = 1.0 * n / (populationSize_ - 1);
				lambda_[n][0] = a;
				lambda_[n][1] = 1 - a;
			} // for
		} // if
		else {
			String dataFileName;
			dataFileName = "W" + problem_.getNumberOfObjectives() + "D_" +
			populationSize_ + ".dat";

			System.out.println(dataDirectory_);
			System.out.println(dataDirectory_ + "/" + dataFileName);

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
				System.out.println("initUniformWeight: fail when reading for file: " + dataDirectory_ + "/" + dataFileName);
				e.printStackTrace();
			}
		} // else
	} // initUniformWeight

	/**
	 * 
	 */
	public void initPopulation() throws JMException, ClassNotFoundException {
		for (int i = 0; i < populationSize_; i++) {
			Solution newSolution = new Solution(problem_);

			problem_.evaluate(newSolution);
			//problem_.evaluateConstraints(newSolution);
			evaluations_++;
			population_.add(newSolution);
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

		ss = parentThread_.neighborhood_[cid].length;
		while (list.size() < size) {
			if (type == 1) {
				r = PseudoRandom.randInt(0, ss - 1);
				p = parentThread_.neighborhood_[cid][r];
				//p = population[cid].table[r];
			} else {
				p = PseudoRandom.randInt(0, parentThread_.populationSize_ - 1);
				//p = int(population.size()*rnd_uni(&rnd_uni_init));
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

	/**
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
	} // updateReference

	/**
	 * @param individual
	 * @param id
	 * @param type
	 */
	void updateOfSolutions(Solution indiv, int id, int type) {
		// indiv: child solution
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
				k = perm[i];      // calculate the values of objective function regarding the current subproblem
			}
			double f1, f2;

			f2 = fitnessFunction(indiv, parentThread_.lambda_[k]);
			synchronized (parentThread_) {
				f1 = fitnessFunction(parentThread_.population_.get(k), parentThread_.lambda_[k]);

				if (f2 < f1) {
					parentThread_.population_.replace(k, new Solution(indiv));
					//population[k].indiv = indiv;
					time++;
				}
			}
			// the maximal number of solutions updated is not allowed to exceed 'limit'
			if (time >= parentThread_.nr_) {
				return;
			}
		}
	} // updateProblem

	double fitnessFunction(Solution individual, double[] lambda) {
		double fitness;
		fitness = 0.0;

		if (parentThread_.functionType_.equals("_TCHE1")) {
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
			} // for

			fitness = maxFun;
		} // if
		else {
			System.out.println("MOEAD.fitnessFunction: unknown type " + functionType_);
			System.exit(-1);
		}
		return fitness;
	} // fitnessEvaluation
} // pMOEAD

