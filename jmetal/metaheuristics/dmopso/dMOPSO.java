/**
 * dMOPSO.java
 * 
 * @version 1.0
 */
package jmetal.metaheuristics.dmopso;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.wrapper.XReal;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

public class dMOPSO extends Algorithm {

	//static double y2;
	//static int use_last = 0;
	/**
	 * Stores the number of particles_ used
	 */
	private int swarmSize_;
	/**
	 * Stores the maximum Age of each particle before reset
	 */
	private int maxAge_;
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
	private int[] shfGBest_;
	/**
	 * Stores the speed_ of each particle
	 */
	private double[][] speed_;
	/**
	 * Stores the age_ of each particle
	 */
	private int[] age_;	

	/////// BEGIN MOEAD //////
	/**
	 * Z vector (ideal point)
	 */
	double[] z_;
	/**
	 * Lambda vectors
	 */ 
	double[][] lambda_;
	
	Solution[] indArray_;
	
	String functionType_ = "_PBI";//"_PBI";//"_TCHE";//"_AGG";

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

	public dMOPSO(Problem problem) {
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

	public dMOPSO(Problem problem,
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

	} // dMOPSO


	/**
	 * Initialize all parameter of the algorithm
	 */
	public void initParams() {
		swarmSize_ = ((Integer) getInputParameter("swarmSize")).intValue();

		maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();

		maxAge_ = ((Integer) getInputParameter("maxAge")).intValue();

		indicators_ = (QualityIndicator) getInputParameter("indicators");
		
		String funcType = ((String) getInputParameter("functionType"));
		if(funcType != null && funcType != ""){
			functionType_ = funcType;
		}		
		
		iteration_ = 0 ;		

		particles_ = new SolutionSet(swarmSize_);
		lBest_ = new Solution[swarmSize_];
		gBest_ = new Solution[swarmSize_];
		shfGBest_ = new int[swarmSize_];

		// Create the speed_ vector
		speed_ = new double[swarmSize_][problem_.getNumberOfVariables()];
		age_ = new int[swarmSize_];
		
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
		return wma;
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
	private void computeSpeed(int i) throws JMException{
		double r1, r2, W, C1, C2;
		double wmax, wmin;

		XReal particle = new XReal(particles_.get(i)) ;
		XReal bestParticle = new XReal(lBest_[i]) ;					
		//XReal bestGlobal = new XReal(gBest_[i]) ;
		XReal bestGlobal = new XReal(gBest_[shfGBest_[i]]) ;

		r1 = PseudoRandom.randDouble(r1Min_, r1Max_);
		r2 = PseudoRandom.randDouble(r2Min_, r2Max_);
		C1 = PseudoRandom.randDouble(C1Min_, C1Max_);
		C2 = PseudoRandom.randDouble(C2Min_, C2Max_);
		W = PseudoRandom.randDouble(WMin_, WMax_);
		
		wmax = WMax_;
		wmin = WMin_;

		for (int var = 0; var < particle.size(); var++) {
			//Computing the velocity of this particle 
			try {
				speed_[i][var] = velocityConstriction(constrictionCoefficient(C1, C2) *
						(inertiaWeight(iteration_, maxIterations_, wmax, wmin) * speed_[i][var] +
								C1 * r1 * (bestParticle.getValue(var) -
										particle.getValue(var)) +
								C2 * r2 * (bestGlobal.getValue(var) -
										particle.getValue(var))), deltaMax_, deltaMin_, var, i) ;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	} // computeSpeed

	/**
	 * Update the position of each particle
	 * @throws JMException 
	 */
	private void computeNewPositions(int i) throws JMException {
		XReal particle = new XReal(particles_.get(i)) ;
		for (int var = 0; var < particle.size(); var++) {
			particle.setValue(var, particle.getValue(var) +  speed_[i][var]) ;
		}
	} // computeNewPositions

	/**   
	 * Runs of the dMOPSO algorithm.
	 * @return a <code>SolutionSet</code> that is a set of non dominated solutions
	 * as a result of the algorithm execution  
	 * @throws JMException 
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		//->Step 1.1 Initialize parameters (iteration_ = 0)
		initParams();
		
		//->Step 1.3 Generate a swarm of N random particles
		for (int i = 0; i < swarmSize_; i++) {
			Solution particle = new Solution(problem_);
			problem_.evaluate(particle);
			particles_.add(particle);
		}

		//-> Step 1.4 Initialize the speed_ and age_ of each particle to 0
		for (int i = 0; i < swarmSize_; i++) {
			for (int j = 0; j < problem_.getNumberOfVariables(); j++) {
				speed_[i][j] = 0.0;
			}
			age_[i] = 0;
		}

		/////// BEGIN MOEAD //////
		indArray_ = new Solution[problem_.getNumberOfObjectives()];
		z_ = new double[problem_.getNumberOfObjectives()];		
		lambda_ = new double[swarmSize_][problem_.getNumberOfObjectives()];

		//-> Step 1.2 Generate a well-distributed set of N weighted vectors
		initUniformWeight();
		initIdealPoint();
		/////// END MOEAD //////

		//-> Step 1.5 and 1.6 Define the personal best and the global best
		for (int i = 0; i < particles_.size(); i++) {
			Solution particle = new Solution(particles_.get(i));
			lBest_[i] = particle;
			gBest_[i] = particle;
		}
		updateGlobalBest();

		// Iterations ...        
		while (iteration_ < maxIterations_) {
			
			//-> Step 2 Suffle the global best
			shuffleGlobalBest();
			
			//-> Step 3 The cycle
			for (int i = 0; i < particles_.size(); i++) {	
				
				if(age_[i] < maxAge_){
					//-> Step 3.1 Update particle
					updateParticle(i);					
				}else{
					//-> Step 3.2 Reset particle
					resetParticle(i);
				}

				//-> Step 3.3 Repair bounds
				repairBounds(i);
				
				//-> Step 3.4 Evaluate the particle and update Z*
				problem_.evaluate(particles_.get(i));
				updateReference(particles_.get(i));
				
				//-> Step 3.5 Update the personal best
				updateLocalBest(i);
			}
			
			//-> Step 4 Update the global best
			updateGlobalBest();			
			iteration_++;
		}

		SolutionSet ss = new SolutionSet(gBest_.length);
		for (int i =0; i < gBest_.length; i++) {
			ss.add(gBest_[i]);
		}

		return ss;
	} // execute
	
//	public SolutionSet execute() throws JMException, ClassNotFoundException {
//		//->Step 1.1 Initialize parameters (iteration_ = 0)
//		initParams();
//		
//		//->Step 1.3 Generate a swarm of N random particles
//		for (int i = 0; i < swarmSize_; i++) {
//			Solution particle = new Solution(problem_);
//			problem_.evaluate(particle);
//			particles_.add(particle);
//		}
//
//		//-> Step 1.4 Initialize the speed_ and age_ of each particle to 0
//		for (int i = 0; i < swarmSize_; i++) {
//			for (int j = 0; j < problem_.getNumberOfVariables(); j++) {
//				speed_[i][j] = 0.0;
//			}
//			age_[i] = 0;
//		}
//
//		/////// BEGIN MOEAD //////
//		indArray_ = new Solution[problem_.getNumberOfObjectives()];
//		z_ = new double[problem_.getNumberOfObjectives()];		
//		lambda_ = new double[swarmSize_][problem_.getNumberOfObjectives()];
//
//		//-> Step 1.2 Generate a well-distributed set of N weighted vectors
//		initUniformWeight();
//		initIdealPoint();
//		/////// END MOEAD //////
//
//		//-> Step 1.5 and 1.6 Define the personal best and the global best
//		for (int i = 0; i < particles_.size(); i++) {
//			Solution particle = new Solution(particles_.get(i));
//			lBest_[i] = particle;
//			gBest_[i] = particle;
//			//updateGlobalBest(i);
//		}
//		updateGlobalBest();
//
//		//BORRAR
//		particles_.printObjectivesToFile("EXEC/FUN"+iteration_);
//		SolutionSet sgb = new SolutionSet(particles_.size());
//		for (int i =0; i < gBest_.length; i++) {
//			sgb.add(gBest_[i]);
//		}
//		sgb.printObjectivesToFile("EXEC/GB"+iteration_);
//		
//		// Iterations ...        
//		while (iteration_ < maxIterations_) {
//	
//			
//			//-> Step 2 Suffle the global best
//			shuffleGlobalBest();
//			
//			//-> Step 3 The cycle
//			for (int i = 0; i < particles_.size(); i++) {	
//				
//				if(age_[i] < maxAge_){
//					//-> Step 3.1 Update particle
//					updateParticle(i);					
//				}else{
//					//-> Step 3.2 Reset particle
//					resetParticle(i);
//				}
//
//				//-> Step 3.3 Repair bounds
//				repairBounds(i);
//				
//				//-> Step 3.4 Evaluate the particle and update Z*
//				problem_.evaluate(particles_.get(i));
//				updateReference(particles_.get(i));
//				
//				//-> Step 3.5 Update the personal best
//				updateLocalBest(i);
//				//updateGlobalBest(i);	
//			}
//			
//			//-> Step 4 Update the global best
//			updateGlobalBest();			
//			iteration_++;
//			//BORRAR
//			particles_.printObjectivesToFile("EXEC/FUN"+iteration_);
//			sgb = new SolutionSet(particles_.size());
//			for (int i =0; i < gBest_.length; i++) {
//				sgb.add(gBest_[i]);
//			}
//			sgb.printObjectivesToFile("EXEC/GB"+iteration_);
//		}
//
//		SolutionSet ss = new SolutionSet(gBest_.length);
//		for (int i =0; i < gBest_.length; i++) {
//			ss.add(gBest_[i]);
//		}
//
//		return ss;
//	} // execute
	

//	private void shuffleGlobalBest(){
//		Solution[] aux = new Solution[swarmSize_];
//		int rnd;
//		Solution tmp;
//
//		for (int i = 0; i < swarmSize_; i++)
//		{
//			aux[i] = new Solution(gBest_[i]);
//		}
//
//		for (int i = 0; i < swarmSize_; i++)
//		{
//			rnd = PseudoRandom.randInt(i, swarmSize_ - 1);
//			tmp = new Solution(aux[rnd]);
//			aux[rnd] = new Solution(aux[i]);
//			gBest_[i] = new Solution(tmp);
//		}
//	} // shuffleGlobalBest
	
	private void shuffleGlobalBest(){
		int[] aux = new int[swarmSize_];
		int rnd;
		int tmp;

		for (int i = 0; i < swarmSize_; i++)
		{
			aux[i] = i;
		}

		for (int i = 0; i < swarmSize_; i++)
		{
			rnd = PseudoRandom.randInt(i, swarmSize_ - 1);
			tmp = aux[rnd];
			aux[rnd] = aux[i];
			shfGBest_[i] = tmp;
		}
	} // shuffleGlobalBest

	private void repairBounds(int part) throws JMException{

		XReal particle = new XReal(particles_.get(part)) ;

		for(int var = 0; var < particle.getNumberOfDecisionVariables(); var++){
			if (particle.getValue(var) < problem_.getLowerLimit(var)) {
				particle.setValue(var, problem_.getLowerLimit(var));
				speed_[part][var] = speed_[part][var] * ChVel1_;    				
			}
			if (particle.getValue(var) > problem_.getUpperLimit(var)) {
				particle.setValue(var, problem_.getUpperLimit(var));
				speed_[part][var] = speed_[part][var] * ChVel2_;
			}
		}	

	} // repairBounds
	
	private void resetParticle(int i) throws JMException {
		XReal particle = new XReal(particles_.get(i)) ;
		double mean, sigma, N;
		
		for (int var = 0; var < particle.size(); var++) {
			XReal gB, pB;
			//gB = new XReal(gBest_[i]);
			gB = new XReal(gBest_[shfGBest_[i]]);
			pB = new XReal(lBest_[i]);	

			mean = (gB.getValue(var) - pB.getValue(var))/2;

			sigma = Math.abs(gB.getValue(var) - pB.getValue(var));

			java.util.Random rnd = new java.util.Random();					

			N = rnd.nextGaussian()*sigma + mean; // N(mean, sigma)
			//N = box_muller(mean, sigma);

			particle.setValue(var,N);	
			speed_[i][var] = 0.0;
		}
	} // resetParticle

	/******************************************/
//	double box_muller(double m, double s)
//	{
//		/* mean m, standard deviation s */
//		double x1, x2, w, y1;
//		
//		RandomGenerator rnd = new RandomGenerator(); 
//
//		if (use_last!=0) /* use value from previous call */
//		{
//			y1 = y2;
//			use_last = 0;
//		}
//		else
//		{
//			do
//			{
//				x1 = 2.0 * rnd.randomperc() - 1.0;
//				x2 = 2.0 * rnd.randomperc() - 1.0;
//				w = x1 * x1 + x2 * x2;
//			} while (w >= 1.0);
//
//			w = Math.sqrt((-2.0 * Math.log(w)) / w);
//			y1 = x1 * w;
//			y2 = x2 * w;
//			use_last = 1;
//		}
//		return (m + y1 * s);
//	}
	/******************************************/
	
	private void updateParticle(int i) throws JMException {
		computeSpeed(i);
		computeNewPositions(i);	
	}
	
	/**
	 * initUniformWeight
	 */
	private void initUniformWeight() {
		if ((problem_.getNumberOfObjectives() == 2) && (swarmSize_ < 300)) {
			for (int n = 0; n < swarmSize_; n++) {
				double a = 1.0 * n / (swarmSize_ - 1);
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
					swarmSize_ + ".dat";

//			System.out.println(dataDirectory_);
//			System.out.println(dataDirectory_ + "/" + dataFileName);

			try {
				// Open the file
				FileInputStream fis = new FileInputStream(dataDirectory_ + "/" + dataFileName);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader br = new BufferedReader(isr);


				int i = 0;
				int j = 0;
				String aux = br.readLine();
				while (aux != null) {
					StringTokenizer st = new StringTokenizer(aux);
					j = 0;
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

	} // initUniformWeight


	private void initIdealPoint() throws JMException, ClassNotFoundException {
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
			z_[i] = 1.0e+30;
			indArray_[i] = new Solution(problem_);
			problem_.evaluate(indArray_[i]);
		} // for

		for (int i = 0; i < swarmSize_; i++) {
			updateReference(particles_.get(i));
		} // for
	} // initIdealPoint

	private void updateReference(Solution individual) {
		for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
			if (individual.getObjective(n) < z_[n]) {
				z_[n] = individual.getObjective(n);

				indArray_[n] = new Solution(individual);
			}
		}
	} // updateReference

	private void updateGlobalBest() {

		double gBestFitness ;
		
		for(int j = 0; j<lambda_.length; j++){
			gBestFitness = fitnessFunction(gBest_[j], lambda_[j]) ;
			
			for (int i = 0 ; i < particles_.size(); i++) {
				double v1 = fitnessFunction(particles_.get(i), lambda_[j]) ;
				double v2 = gBestFitness ;
				if (v1 < v2) {
					gBest_[j] = new Solution(particles_.get(i)) ; 
					gBestFitness = v1;
				}
			}
		}	
	}
	
	private void updateLocalBest(int part) {

		double f1, f2;
		Solution indiv = new Solution(particles_.get(part));

		f1 = fitnessFunction(lBest_[part], lambda_[part]);
		f2 = fitnessFunction(indiv, lambda_[part]);

//		System.out.println("F1 = "+f1);
//		System.out.println("F2 = "+f2);

		if(age_[part] >= maxAge_ || f2 <= f1){
			lBest_[part] = indiv;
			age_[part] = 0;
		}else{			
			age_[part]++;
		}
	} // updateLocalBest

	
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
			System.out.println("dMOPSO.fitnessFunction: unknown type " + functionType_);
			System.exit(-1);
		}
		return fitness;	
	} // fitnessFunction
	
} // dMOPSO