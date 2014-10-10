//  StandardPSO2011.java
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

package org.uma.jmetal.metaheuristic.singleobjective.particleswarmoptimization;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.metaheuristic.singleobjective.particleswarmoptimization.StandardPSO2007.Builder;
import org.uma.jmetal.operator.selection.BestSolutionSelection;
import org.uma.jmetal.util.AdaptiveRandomNeighborhood;
import org.uma.jmetal.util.Distance;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Class implementing a Standard PSO 2011 algorithm
 */
public class StandardPSO2011 extends Algorithm {
	private Operator findBestSolution;
	private Comparator fitnessComparator;
	private SolutionSet swarm;
	private int swarmSize;
	private int maxIterations;
	private int iterations;
	private int numberOfParticlesToInform;
	private Solution[] localBest;
	private Solution[] neighborhoodBest;
	private double[][] speed;
	private AdaptiveRandomNeighborhood neighborhood;
	private double weight;
	private double c;
	private double changeVelocity;

	SolutionSetEvaluator evaluator;

	/** Constructor */
	public StandardPSO2011(Builder builder) {
		super();
		this.problem = builder.problem ;
		this.swarmSize = builder.swarmSize ;
		this.maxIterations = builder.maxIterations ;
		this.numberOfParticlesToInform = builder.numberOfParticlesToInform ;
		this.evaluator = builder.evaluator ;

		weight = 1.0 / (2.0 * Math.log(2)); //0.721;
		c = 1.0 / 2.0 + Math.log(2); //1.193;
		changeVelocity = -0.5 ; 

		fitnessComparator = new ObjectiveComparator(0) ; 
		findBestSolution = new BestSolutionSelection.Builder(fitnessComparator).build();

		swarm = new SolutionSet(swarmSize);
		localBest = new Solution[swarmSize];
		neighborhoodBest = new Solution[swarmSize];

		speed = new double[swarmSize][problem.getNumberOfVariables()];
	}

	/* Getters */
	public int getSwarmSize() {
		return swarmSize ;
	}

	public int getNumberOfParticlesToInform() {
		return numberOfParticlesToInform ;
	}

	public int getMaxIterations() {
		return maxIterations ;
	}

	public double getWeight() {
		return weight;
	}

	public double getC() {
		return c;
	}

	public SolutionSetEvaluator getEvaluator() {
		return evaluator ;
	}

	/** Builder class */
	public static class Builder {
		private Problem problem ;
		private int swarmSize;
		private int maxIterations;
		private int numberOfParticlesToInform; 

		private SolutionSetEvaluator evaluator ;

		public Builder(Problem problem) {
			this.problem = problem ;
			swarmSize = 100 ;
			maxIterations = 80000 ;
			numberOfParticlesToInform = 3 ;
			evaluator = new SequentialSolutionSetEvaluator() ;
		}

		public Builder setSwarmSize(int swarmSize) {
			this.swarmSize = swarmSize ;

			return this ;
		}

		public Builder setMaxIterations(int maxIterations) {
			this.maxIterations = maxIterations ;

			return this ;
		}

		public Builder setNumberOfParticlesToInform(int numberOfParticlesToInform) {
			this.numberOfParticlesToInform = numberOfParticlesToInform ;

			return this ;
		}

		public Builder setEvaluator(SolutionSetEvaluator evaluator) {
			this.evaluator = evaluator ;

			return this ;
		}

		public StandardPSO2011 build() {
			return new StandardPSO2011(this) ;
		}
	}

	/** Execute() method */
	public SolutionSet execute() throws JMetalException, ClassNotFoundException {
		createInitialSwarm() ;
		evaluateSwarm() ;
		initializeParticlesSpeed() ;
    initializeParticlesLocalBest() ;

    neighborhood = new AdaptiveRandomNeighborhood(swarm, numberOfParticlesToInform);

    updateNeighborBest() ;

    iterations = 1 ;


    //	swarm.printObjectives();
	//	double b = swarm.best(fitnessComparator).getObjective(0);

		double bestFoundFitness = Double.MAX_VALUE;
		while (iterations < maxIterations) {
			computeSpeed();
			computeNewPositions();
			evaluateSwarm() ;
			updateParticlesLocalBest() ;			
			updateNeighborBest() ;
			
			iterations++;

      double bestCurrentFitness = swarm.best(fitnessComparator).getObjective(0);
			//JMetalLogger.logger.info("Best: " + bestCurrentFitness);

			if (bestCurrentFitness == bestFoundFitness) {
				neighborhood.recompute();
			}

			if (bestCurrentFitness < bestFoundFitness) {
				bestFoundFitness = bestCurrentFitness;
			}
		}

		tearDown() ;
		
		return getPopulationWithTheBestSolution() ;
	}

	private void createInitialSwarm() throws ClassNotFoundException, JMetalException {
		swarm = new SolutionSet(swarmSize);

		Solution newSolution;
		for (int i = 0; i < swarmSize; i++) {
			newSolution = new Solution(problem);
			swarm.add(newSolution);
		}
	}

	private void evaluateSwarm() throws JMetalException {
		swarm = evaluator.evaluate(swarm, problem);
	}
	
	private void initializeParticlesSpeed() {
		for (int i = 0; i < swarmSize; i++) {
			XReal particle = new XReal(swarm.get(i));
			for (int j = 0; j < problem.getNumberOfVariables(); j++) {
				speed[i][j] = (PseudoRandom.randDouble(
						particle.getLowerBound(j) - particle.getValue(0),
						particle.getUpperBound(j) - particle.getValue(0)));
			}
		}
	}

	private void initializeParticlesLocalBest() {
		for (int i = 0; i < swarm.size(); i++) {
			Solution particle = new Solution(swarm.get(i));
			localBest[i] = particle;
		}
	}
	
	private void updateNeighborBest() {
		for (int i = 0; i < swarm.size(); i++) {
			neighborhoodBest[i] = getNeighborBest(i);
		}
	}
	
	private void updateParticlesLocalBest() {
		for (int i = 0; i < swarm.size(); i++) {
			if ((swarm.get(i).getObjective(0) < localBest[i].getObjective(0))) {
				Solution particle = new Solution(swarm.get(i));
				localBest[i] = particle;
			}
		}
	}
	
	private Solution getNeighborBest(int i) {
		Solution bestLocalBestSolution = null;

		try {
			for (int index : neighborhood.getNeighbors(i)) {
				if ((bestLocalBestSolution == null) || (bestLocalBestSolution.getObjective(0)
						> localBest[index].getObjective(0))) {
					bestLocalBestSolution = localBest[index];
				}
			}
		} catch (JMetalException e) {
			JMetalLogger.logger.log(Level.SEVERE, "Error", e);
		}

		return bestLocalBestSolution;
	}

	private void computeSpeed() throws ClassNotFoundException, JMetalException {
		for (int i = 0; i < swarmSize; i++) {
			XReal particle = new XReal(swarm.get(i));
			XReal lBest = new XReal(this.localBest[i]);
			XReal neighborhoodBest = new XReal(this.neighborhoodBest[i]);
			XReal gravityCenter = new XReal(new Solution(problem));
			XReal randomParticle = new XReal(new Solution(swarm.get(i)));

			if (this.localBest[i] != this.neighborhoodBest[i]) {
				for (int var = 0; var < particle.size(); var++) {
					double G;
					G = particle.getValue(var) +
							c * (lBest.getValue(var) + neighborhoodBest.getValue(var) - 2 * particle
									.getValue(var)) / 3.0;

					gravityCenter.setValue(var, G);
				}
			} else {
				for (int var = 0; var < particle.size(); var++) {
					double g  = particle.getValue(var) +
							c * (lBest.getValue(var) - particle.getValue(var)) / 2.0;

					gravityCenter.setValue(var, g);
				}
			}

			double radius = 0;
			radius = new Distance()
			.distanceBetweenSolutions(gravityCenter.getSolution(), particle.getSolution());

			double[] random = PseudoRandom.randSphere(problem.getNumberOfVariables());

			for (int var = 0; var < particle.size(); var++) {
				randomParticle.setValue(var, gravityCenter.getValue(var) + radius * random[var]);
			}

			for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
				speed[i][var] =
						weight * speed[i][var] + randomParticle.getValue(var) - particle.getValue(var);
			}
		}
	}

	private void computeNewPositions() throws JMetalException {
		for (int i = 0; i < swarmSize; i++) {
			XReal particle = new XReal(swarm.get(i));
			for (int var = 0; var < particle.size(); var++) {
				particle.setValue(var, particle.getValue(var) + speed[i][var]);

				if (particle.getValue(var) < problem.getLowerLimit(var)) {
					particle.setValue(var, problem.getLowerLimit(var));
					speed[i][var] = changeVelocity * speed[i][var];
				}
				if (particle.getValue(var) > problem.getUpperLimit(var)) {
					particle.setValue(var, problem.getUpperLimit(var));
					speed[i][var] = changeVelocity * speed[i][var];
				}
			}
		}
	}
	
	private SolutionSet getPopulationWithTheBestSolution() {
		SolutionSet resultPopulation = new SolutionSet(1);
		resultPopulation.add(swarm.get((Integer) findBestSolution.execute(swarm)));

		return resultPopulation;		
	}
	
  protected void tearDown() {
    evaluator.shutdown();
  }
}
