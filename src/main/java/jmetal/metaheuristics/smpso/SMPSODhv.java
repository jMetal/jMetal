/**
 * SMPSOD11.java
 * 
 * @author Juan J. Durillo
 * @author Antonio J. Nebro
 * @version 1.0
 */
package jmetal.metaheuristics.smpso;

import jmetal.core.*;
import jmetal.metaheuristics.moead.Utils;
import jmetal.operators.crossover.Crossover;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.qualityIndicator.fastHypervolume.FastHypervolumeArchive;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.random.PseudoRandom;
import jmetal.util.comparators.CrowdingDistanceComparator;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.wrapper.XReal;

import java.io.*;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.Vector;

public class SMPSODhv extends Algorithm {

	/**
	 * Stores the number of particles_ used
	 */
	private int particlesSize_;
	/**
	 * Stores the maximum size for the archive
	 */
	private int archiveSize_;
	/**
	 * Stores the maximum number of iteration_
	 */
	private int maxIterations_;
	/**
	 * Stores the current number of iteration_
	 */
	private int iteration_;
	/**
	 * Stores the particles
	 */
	private SolutionSet particles_;
	/**
	 * Stores the best_ solutions founds so far for each particles
	 */
	private Solution[] lBest_;

	/**
	 * Stores the global best solutions founds so far for each particles
	 */
	private Solution[] gBest_;

	/**
	 * Stores the leaders_
	 */
	private FastHypervolumeArchive leaders_;
	/**
	 * Stores the speed_ of each particle
	 */
	private double[][] speed_;
	/**
	 * Stores a comparator for checking dominance
	 */
	private Comparator dominance_;
	/**
	 * Stores a comparator for crowding checking
	 */
	private Comparator crowdingDistanceComparator_;
	/**
	 * Stores a <code>Distance</code> object
	 */
	private Distance distance_;
	/**
	 * Stores a operator for non uniform mutations
	 */
	private Operator polynomialMutation_;

	/////// BEGIN MOEAD //////
	/*
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

	Crossover crossover_ ;
	String functionType_ = "_TCHE"; //"_PBI";//"_AGG";

	String dataDirectory_ ;

	/////// END MOEAD //////


	QualityIndicator indicators_; // QualityIndicator object

	double r1Max_;
	double r1Min_;
	double r2Max_;
	double r2Min_;
	double C1Max_;
	double C1Min_;
	double C2Max_;
	double C2Min_;
	double WMax_;
	double WMin_;
	double ChVel1_;
	double ChVel2_;
	private double trueHypervolume_;
	private Hypervolume hy_;
	private SolutionSet trueFront_;
	private double deltaMax_[];
	private double deltaMin_[];
	//boolean success_;

	public SMPSODhv(Problem problem) {
		super(problem);
		r1Max_ = 1.0;
		r1Min_ = 0.0;
		r2Max_ = 1.0;
		r2Min_ = 0.0;
		C1Max_ = 2.5;
		C1Min_ = 1.5;
		C2Max_ = 2.5;
		C2Min_ = 1.5;
		WMax_ = 0.4;
		WMin_ = 0.1;
		ChVel1_ = -1.0;
		ChVel2_ = -1.0;
	}

	public SMPSODhv(Problem problem,
			Vector<Double> variables,
			String trueParetoFront) throws FileNotFoundException {
		super(problem);

		r1Max_ = variables.get(0);
		r1Min_ = variables.get(1);
		r2Max_ = variables.get(2);
		r2Min_ = variables.get(3);
		C1Max_ = variables.get(4);
		C1Min_ = variables.get(5);
		C2Max_ = variables.get(6);
		C2Min_ = variables.get(7);
		WMax_ = variables.get(8);
		WMin_ = variables.get(9);
		ChVel1_ = variables.get(10);
		ChVel2_ = variables.get(11);

		hy_ = new Hypervolume();
		jmetal.qualityIndicator.util.MetricsUtil mu = new jmetal.qualityIndicator.util.MetricsUtil();
		trueFront_ = mu.readNonDominatedSolutionSet(trueParetoFront);
		trueHypervolume_ = hy_.hypervolume(trueFront_.writeObjectivesToMatrix(),
				trueFront_.writeObjectivesToMatrix(),
				problem_.getNumberOfObjectives());

	} // FPSO

	
	/**
	 * Initialize all parameter of the algorithm
	 */
	public void initParams() {
		particlesSize_ = ((Integer) getInputParameter("swarmSize")).intValue();
		//archiveSize_ = ((Integer) getInputParameter("archiveSize")).intValue();
		maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();

		indicators_ = (QualityIndicator) getInputParameter("indicators");

		polynomialMutation_ = operators_.get("mutation") ;

		String funcType = ((String) getInputParameter("functionType"));
		if(funcType != null && funcType != ""){
			functionType_ = funcType;
		}		
		
		iteration_ = 0 ;		

		particles_ = new SolutionSet(particlesSize_);
		lBest_ = new Solution[particlesSize_];
		gBest_ = new Solution[particlesSize_];

		leaders_ = new FastHypervolumeArchive(archiveSize_, problem_.getNumberOfObjectives());

		// Create comparators for dominance and crowding distance
		dominance_ = new DominanceComparator();
		crowdingDistanceComparator_ = new CrowdingDistanceComparator();
		distance_ = new Distance();

		// Create the speed_ vector
		speed_ = new double[particlesSize_][problem_.getNumberOfVariables()];

		deltaMax_ = new double[problem_.getNumberOfVariables()];
		deltaMin_ = new double[problem_.getNumberOfVariables()];
		for (int i = 0; i < problem_.getNumberOfVariables(); i++) {
			deltaMax_[i] = (problem_.getUpperLimit(i) -
					problem_.getLowerLimit(i)) / 2.0;
			deltaMin_[i] = -deltaMax_[i];
		} // for
	} // initParams 

	// Adaptive inertia 
	private double inertiaWeight(int iter, int miter, double wma, double wmin) {
		//return - (((wma-wmin)*(double)iter)/(double)miter);
		return wma; // - (((wma-wmin)*(double)iter)/(double)miter);
	} // inertiaWeight

	// constriction coefficient (M. Clerc)
	private double constrictionCoefficient(double c1, double c2) {
		double rho = c1 + c2;
		//rho = 1.0 ;
		if (rho <= 4) {
			return 1.0;
		} else {
			return 2 / (2 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho));
		}
	} // constrictionCoefficient


	// velocity bounds
	private double velocityConstriction(double v, double[] deltaMax,
			double[] deltaMin, int variableIndex,
			int particleIndex) throws IOException {


		//return v ;

		//System.out.println("v: " + v + "\tdmax: " + dmax + "\tdmin: " + dmin) ;
		double result;

		double dmax = deltaMax[variableIndex];
		double dmin = deltaMin[variableIndex];

		result = v;

		if (v > dmax) {
			result = dmax;
		}

		if (v < dmin) {
			result = dmin;
		}

		return result;

	} // velocityConstriction

	/**
	 * Update the speed of each particle
	 * @throws JMException 
	 */
	private void computeSpeed(int iter, int miter) throws JMException{
		double r1, r2, W, C1, C2;
		double wmax, wmin, deltaMax, deltaMin;
		XReal bestGlobal;

		for (int i = 0; i < particlesSize_; i++) {
			XReal particle = new XReal(particles_.get(i)) ;
			XReal bestParticle = new XReal(lBest_[i]) ;

			bestGlobal = new XReal(gBest_[i]) ;

			r1 = PseudoRandom.randDouble(r1Min_, r1Max_);
			r2 = PseudoRandom.randDouble(r2Min_, r2Max_);
			C1 = PseudoRandom.randDouble(C1Min_, C1Max_);
			C2 = PseudoRandom.randDouble(C2Min_, C2Max_);
			W = PseudoRandom.randDouble(WMin_, WMax_);
			//
			wmax = WMax_;
			wmin = WMin_;

			for (int var = 0; var < particle.size(); var++) {
				//Computing the velocity of this particle 				
				try {
					speed_[i][var] = velocityConstriction(constrictionCoefficient(C1, C2) *
							(inertiaWeight(iter, miter, wmax, wmin) * speed_[i][var] +
									C1 * r1 * (bestParticle.getValue(var) -
											particle.getValue(var)) +
											C2 * r2 * (bestGlobal.getValue(var) -
													particle.getValue(var))), deltaMax_, deltaMin_, var, i) ;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	} // computeSpeed

	/**
	 * Update the speed of each particle
	 * @throws JMException 
	 */
	private void computeSpeed(int iter, int miter, int i) throws JMException {
		double r1, r2, W, C1, C2;
		double wmax, wmin, deltaMax, deltaMin;
		XReal bestGlobal;


		XReal particle = new XReal(particles_.get(i)) ;
		XReal bestParticle = new XReal(lBest_[i]) ;
		
		bestGlobal = new XReal(gBest_[i]) ;

		r1 = PseudoRandom.randDouble(r1Min_, r1Max_);
		r2 = PseudoRandom.randDouble(r2Min_, r2Max_);
		C1 = PseudoRandom.randDouble(C1Min_, C1Max_);
		C2 = PseudoRandom.randDouble(C2Min_, C2Max_);
		W = PseudoRandom.randDouble(WMin_, WMax_);
		//
		wmax = WMax_;
		wmin = WMin_;

		for (int var = 0; var < particle.size(); var++) {
			//Computing the velocity of this particle 
			speed_[i][var] = W  * speed_[i][var] +
					C1 * r1 * (bestParticle.getValue(var) - 
							particle.getValue(var)) +
							C2 * r2 * (bestGlobal.getValue(var) - 
									particle.getValue(var));
		}

	} // computeSpeed




	/**
	 * Update the position of each particle
	 * @throws JMException 
	 */
	private void computeNewPositions() throws JMException {
		for (int i = 0; i < particlesSize_; i++) {
			//Variable[] particle = particles_.get(i).getDecisionVariables();
			XReal particle = new XReal(particles_.get(i)) ;
			//particle.move(speed_[i]);
			for (int var = 0; var < particle.size(); var++) {
				particle.setValue(var, particle.getValue(var) +  speed_[i][var]) ;
				//particle[var].setValue((particle[var].getValue() + speed_[i][var]));

				double c1 = PseudoRandom.randDouble(-1.0, 0.1);
				double c2 = PseudoRandom.randDouble(-1.0, 0.1);

				if (particle.getValue(var) < problem_.getLowerLimit(var)) {
					particle.setValue(var, problem_.getLowerLimit(var));
					speed_[i][var] = speed_[i][var] * ChVel1_; //    
					//speed_[i][var] = speed_[i][var] * c1; //    
				}
				if (particle.getValue(var) > problem_.getUpperLimit(var)) {
					particle.setValue(var, problem_.getUpperLimit(var));
					speed_[i][var] = speed_[i][var] * ChVel2_; //   
					//speed_[i][var] = speed_[i][var] * c2; //   
				}
			}
			problem_.evaluate(particles_.get(i));
			updateReference(particles_.get(i));

		}
	} // computeNewPositions

	/**
	 * Update the position of each particle
	 * @throws JMException 
	 */
	private void computeNewPositions(int i) throws JMException {

		//Variable[] particle = particles_.get(i).getDecisionVariables();
		XReal particle = new XReal(particles_.get(i)) ;
		//particle.move(speed_[i]);
		for (int var = 0; var < particle.size(); var++) {
			particle.setValue(var, particle.getValue(var) +  speed_[i][var]) ;
			//particle[var].setValue((particle[var].getValue() + speed_[i][var]));

			if (particle.getValue(var) < problem_.getLowerLimit(var)) {
				particle.setValue(var, problem_.getLowerLimit(var));
				speed_[i][var] = speed_[i][var] * ChVel1_; //    
			}
			if (particle.getValue(var) > problem_.getUpperLimit(var)) {
				particle.setValue(var, problem_.getUpperLimit(var));
				speed_[i][var] = speed_[i][var] * ChVel2_; //   
			}
		}
		problem_.evaluate(particles_.get(i));
		updateReference(particles_.get(i));

	} // computeNewPositions



	/**
	 * Apply a mutation operator to some particles in the swarm
	 * @throws JMException 
	 */
	private void mopsoMutation(int actualIteration, int totalIterations) throws JMException {
		for (int i = 0; i < particles_.size(); i++) {
			//if ( (i % 3) == 0)
			//polynomialMutation_.execute(particles_.get(PseudoRandom.randInt(0, particles_.size()-1))) ;
			//if (i % 3 == 0) { //particles_ mutated with a non-uniform mutation %3
			//  nonUniformMutation_.execute(particles_.get(i));
			//} else if (i % 3 == 1) { //particles_ mutated with a uniform mutation operator
			//  uniformMutation_.execute(particles_.get(i));
			//} else //particles_ without mutation
			//;
		}
	} // mopsoMutation

	/**   
	 * Runs of the FPSO algorithm.
	 * @return a <code>SolutionSet</code> that is a set of non dominated solutions
	 * as a result of the algorithm execution  
	 * @throws JMException 
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		initParams();

		//->Step 1 (and 3) Create the initial population and evaluate
		for (int i = 0; i < particlesSize_; i++) {
			Solution particle = new Solution(problem_);
			problem_.evaluate(particle);
			particles_.add(particle);
      leaders_.add(new Solution(particle)) ;
		}

		//-> Step2. Initialize the speed_ of each particle to 0
		for (int i = 0; i < particlesSize_; i++) {
			for (int j = 0; j < problem_.getNumberOfVariables(); j++) {
				speed_[i][j] = 0.0;
			}
		}

		/////// BEGIN MOEAD //////
		indArray_ = new Solution[problem_.getNumberOfObjectives()];
		T_ = 20;
		delta_ = 0.9;
		nr_ = 2;
		neighborhood_ = new int[particlesSize_][T_];
		z_ = new double[problem_.getNumberOfObjectives()];		
		lambda_ = new double[particlesSize_][problem_.getNumberOfObjectives()];
		initUniformWeight();
		initNeighborhood();
		initIdealPoint();
		/////// END MOEAD //////

		//-> Step 6. Initialize the memory of each particle
		for (int i = 0; i < particles_.size(); i++) {
			Solution particle = new Solution(particles_.get(i));
			lBest_[i] = particle;
			gBest_[i] = particle;
			updateGlobalBest(i, 2) ;
		}

		//-> Step 7. Iterations ..        
		while (iteration_ < maxIterations_) {


			computeSpeed(iteration_, maxIterations_);
			computeNewPositions();
			for (int i = 0; i < particles_.size(); i++) {
				int type;
				double rnd = PseudoRandom.randDouble();

				// STEP 2.1. Mating selection based on probability
				if (rnd < delta_) // if (rnd < realb)    
				{
					type = 1;   // neighborhood
				} else {
					type = 2;   // whole population
				}

				updateLocalBest(particles_.get(i), i, 1);
				updateGlobalBest(i, 2) ;
			}

			iteration_++;

      for (int i = 0 ; i < particles_.size(); i++) {
        leaders_.add(new Solution(particles_.get(i))) ;
        if (leaders_.size() < archiveSize_)
          leaders_.computeHVContribution() ;
      }
		}

		SolutionSet ss = new SolutionSet(gBest_.length);
		for (int i =0; i < gBest_.length; i++) {
			ss.add(gBest_[i]);
		}

    leaders_.printObjectivesToFile("LEADERS");
		return ss;
	} // execute


	/**
	 * initUniformWeight
	 */
	public void initUniformWeight() {
		if ((problem_.getNumberOfObjectives() == 2) && (particlesSize_ < 300)) {
			for (int n = 0; n < particlesSize_; n++) {
				double a = 1.0 * n / (particlesSize_ - 1);
				lambda_[n][0] = a;
				lambda_[n][1] = 1 - a;
			} // for
		} // if
		else {
			String dataFileName;
//			dataDirectory_ = "/home/juanjo/Dropbox/jMetalDropboxJuanjo/MOEAD_parameters/Weight";
			dataDirectory_ = "/Users/antelverde/Softw/pruebas/data/MOEAD_parameters/Weight";
//			dataDirectory_ = "/home/jorgero/moead/weight";
			dataFileName = "W" + problem_.getNumberOfObjectives() + "D_" +
					particlesSize_ + ".dat";

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
				System.out.println("initUniformWeight: failed when reading for file: " + dataDirectory_ + "/" + dataFileName);
				e.printStackTrace();
			}
		} // else

		//System.exit(0) ;
	} // initUniformWeight
	/**
	 * 
	 */
	public void initNeighborhood() {
		double[] x = new double[particlesSize_];
		int[] idx = new int[particlesSize_];

		for (int i = 0; i < particlesSize_; i++) {
			// calculate the distances based on weight vectors
			for (int j = 0; j < particlesSize_; j++) {
				x[j] = Utils.distVector(lambda_[i], lambda_[j]);
				//x[j] = dist_vector(population[i].namda,population[j].namda);
				idx[j] = j;
				//System.out.println("x["+j+"]: "+x[j]+ ". idx["+j+"]: "+idx[j]) ;
			} // for

			// find 'niche' nearest neighboring subproblems
			Utils.minFastSort(x, idx, particlesSize_, T_);
			//minfastsort(x,idx,population.size(),niche);

			for (int k = 0; k < T_; k++) {
				neighborhood_[i][k] = idx[k];
				//System.out.println("neg["+i+","+k+"]: "+ neighborhood_[i][k]) ;
			}
		} // for
	} // initNeighborhood

	/**
	 * 
	 */
	void initIdealPoint() throws JMException, ClassNotFoundException {
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
			z_[i] = 1.0e+30;
			indArray_[i] = new Solution(problem_);
			problem_.evaluate(indArray_[i]);
			//     evaluations_++;
		} // for

		for (int i = 0; i < particlesSize_; i++) {
			updateReference(particles_.get(i));
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
				p = PseudoRandom.randInt(0, particlesSize_ - 1);
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
	void updateReference(Solution individual) {
		for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
			if (individual.getObjective(n) < z_[n]) {
				z_[n] = individual.getObjective(n);

				indArray_[n] = individual;
			}
		}
	} // updateReference

	void updateGlobalBest(int solution, int type) {
		//gBest_[solution] = new Solution(particles_.get(solution)) ; 
		double gBestFitness ;
		gBestFitness = fitnessFunction(gBest_[solution], lambda_[solution]) ;
		if (type == 1) {
			for (int i = 0 ; i < neighborhood_[i].length; i++) {
				double v1 = fitnessFunction(particles_.get(neighborhood_[solution][i]), lambda_[solution]) ;
				double v2 = gBestFitness ;
				if (v1 < v2) {
					gBest_[solution] = new Solution(particles_.get(i)) ; 
					gBestFitness = fitnessFunction(gBest_[solution], lambda_[solution]) ;
				}
			}
		}
		else {
			for (int i = 0 ; i < particles_.size(); i++) {
				double v1 = fitnessFunction(particles_.get(i), lambda_[solution]) ;
				double v2 = gBestFitness ;
				if (v1 < v2) {
					gBest_[solution] = new Solution(particles_.get(i)) ; 
					gBestFitness = fitnessFunction(gBest_[solution], lambda_[solution]) ;
				}
			}

		}
	}

	/**
	 * @param indiv
	 * @param id
	 * @param type
	 */
	void updateLocalBest(Solution indiv, int id, int type) {
		// indiv: child solution
		// id:   the id of current subproblem
		// type: update solutions in - neighborhood (1) or whole population (otherwise)
		int size;
		int time;

		time = 0;

		if (type == 1) {
			size = neighborhood_[id].length;
		} else {
			size = particles_.size();
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

			f1 = fitnessFunction(lBest_[k], lambda_[k]);
			f2 = fitnessFunction(indiv, lambda_[k]);

			if (f2 < f1) {
				//particles_.replace(k, new Solution(indiv));
				lBest_[k] = new Solution(indiv);
				//population[k].indiv = indiv;
				time++;
			}
			// the maximal number of solutions updated is not allowed to exceed 'limit'
			if (time >= nr_) {
				return;
			}
		}
	} // updateProblem

	private double fitnessFunction(Solution sol, double[] lambda){
		double fitness = 0.0;

		if (functionType_.equals("_TCHE")) {
			double maxFun = -1.0e+30;

			for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
				double diff = Math.abs(sol.getObjective(n) - z_[n]);

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

		}else if(functionType_.equals("_AGG")){
			double sum = 0.0;
			for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
				sum += (lambda[n]) * sol.getObjective(n);
			}

			fitness = sum;	

		}else if(functionType_.equals("_PBI")){							
			double d1, d2, nl;
			double theta = 5.0;

			d1 = d2 = nl = 0.0;

			for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			{
				d1 += (sol.getObjective(i) - z_[i]) * lambda[i];
				nl += Math.pow(lambda[i], 2.0);
			}			
			nl = Math.sqrt(nl);
			d1 = Math.abs(d1) / nl;

			for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			{
				d2 += Math.pow((sol.getObjective(i) - z_[i]) - d1 * (lambda[i] / nl), 2.0);
			}			
			d2 = Math.sqrt(d2);

			fitness = (d1 + theta * d2);	

		}else{
			System.out.println("FPSO.fitnessFunction: unknown type " + functionType_);
			System.exit(-1);
		}
		return fitness;	
	} // fitnessFunction
} // FPSO