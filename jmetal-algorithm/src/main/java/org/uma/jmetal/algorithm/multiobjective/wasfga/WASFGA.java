package org.uma.jmetal.algorithm.multiobjective.wasfga;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.wasfga.util.AchievementScalarizingFunction;
import org.uma.jmetal.algorithm.multiobjective.wasfga.util.RankingASFs;
import org.uma.jmetal.algorithm.multiobjective.wasfga.util.ReferencePoint;
import org.uma.jmetal.algorithm.multiobjective.wasfga.util.WeightVector;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the preference based algorithm named WASF-GA.
 * 
 * @author Rubén Saborido Infantes
 * 
 *         This algorithm is described in the paper: A.B. Ruiz, R. Saborido, M.
 *         Luque "A Preference-based Evolutionary Algorithm for Multiobjective
 *         Optimization: The Weighting Achievement Scalarizing Function Genetic
 *         Algorithm" Published in Journal of Global Optimization in 2014 
 *         DOI = {10.1007/s10898-014-0214-y}
 */
public class WASFGA<S extends Solution<?>> implements Algorithm<List<S>> {
	protected final Problem<S> problem;

	int populationSize;
	int maxEvaluations;
	int evaluations;
	String weightsDirectory ;
	String weightFileName;
  AchievementScalarizingFunction<S> achievementScalarizingFunction ;

  CrossoverOperator<S> crossoverOperator;
  MutationOperator<S> mutationOperator;
  SelectionOperator<List<S>, S> selectionOperator;

	double[][] weights;
	ReferencePoint referencePoint;
	boolean estimatePoints ;
	boolean normalization;
	String folderForOutputFiles;

	RankingASFs<S> ranking ;

	/**
	 * Constructor
	 *
	 * @param problem
	 *            Problem to solve
	 */
	public WASFGA(Problem<S> problem, int populationSize, int maxEvaluations,
                CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
                SelectionOperator<List<S>, S> selectionOperator,
								String folderForOutputFiles, String weightsDirectory,
								boolean normalization, boolean estimatePoints, ReferencePoint referencePoint,
                AchievementScalarizingFunction<S> achievementScalarizingFunction) {
		super();
		this.problem = problem ;
		this.populationSize = populationSize ;
		this.maxEvaluations = maxEvaluations ;
		this.crossoverOperator = crossoverOperator ;
		this.mutationOperator = mutationOperator ;
		this.selectionOperator = selectionOperator ;

		this.folderForOutputFiles = folderForOutputFiles ;
		this.weightsDirectory = weightsDirectory ;
		this.normalization = normalization ;
		this.estimatePoints = estimatePoints ;
		this.referencePoint = referencePoint ;

    this.achievementScalarizingFunction = achievementScalarizingFunction ;
	}

	/**
	 * Runs the WASF-GA algorithm.
	 *
	 * @return A <code>SolutionSet</code> that is a set of non dominated
	 *         solutions as a result of the algorithm execution
	 * @throws JMetalException
	 * @throws ClassNotFoundException
	 */
	@Override
	public void run() throws JMetalException {
    S newSolution;

    List<S> population;
    List<S> offspringPopulation;

    population = new ArrayList<>(populationSize);
    evaluations = 0;

    if (estimatePoints) {
      achievementScalarizingFunction = new AchievementScalarizingFunction<S>(problem.getNumberOfObjectives());

      // If the nadir and ideal point of the problem are not known, they
      // will be estimated using the population
      initializeBounds();
      //} else {
      //	asf = (AchievementScalarizingFunction) getInputParameter("asf");
      //}

      try {
        achievementScalarizingFunction.setReferencePoint(referencePoint);
      } catch (CloneNotSupportedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      // the name of the weight file must be "WND_P.dat", where N is the
      // dimension of the problem and P the population size
      weightFileName = weightsDirectory + "/W" + problem.getNumberOfObjectives() + "D_" + populationSize + ".dat";

      // If the dimension of the problem is equal to 2, the weight vectors are
      // calculated
      if (problem.getNumberOfObjectives() == 2) {
        this.weights = WeightVector.initUniformWeights2D(0.005, populationSize);
      }
      // If the dimension of the problem is greater than 2, the weight vectors
      // are read from a file
      else {
        this.weights = WeightVector.getWeightsFromFile(weightFileName);
      }

      // The weight vectors are inverted
      this.weights = WeightVector.invertWeights(weights, true);

      // --- ALGORITHM --- \\

      // Create the initial solutionSet
      for (int i = 0; i < populationSize; i++) {
        newSolution = problem.createSolution();
        problem.evaluate(newSolution);
        evaluations++;
        population.add(newSolution);

        // If the nadir and ideal points of the problem are not known, they
        // are estimated for the Achievement Scalarizing Function
        if (estimatePoints)
          updateBounds(newSolution);
      }

      // Evolutionary process
      while (evaluations < maxEvaluations) {
        // Create the offSpring solutionSet
        offspringPopulation = new ArrayList<S>(populationSize);
        for (int i = 0; i < (populationSize / 2); i++) {
          if (evaluations < maxEvaluations) {
            // obtain parents
            List<S> parents = new ArrayList<>(2);
            parents.add(selectionOperator.execute(population));
            parents.add(selectionOperator.execute(population));

            List<S> offspring = crossoverOperator.execute(parents);

            mutationOperator.execute(offspring.get(0));
            mutationOperator.execute(offspring.get(1));

            offspringPopulation.add(offspring.get(0));
            offspringPopulation.add(offspring.get(1));

            evaluations += 2;

            // If the nadir and ideal points of the problem are not
            // known, they are estimated for the Achievement Scalarizing Function
            if (estimatePoints) {
              updateBounds(offspring.get(0));
              updateBounds(offspring.get(1));
            }
          } // end if (evaluations < maxEvaluations)
        } // end for (int i = 0; i < (populationSize / 2); i++)

        List<S> jointPopulation = new ArrayList<>();
        jointPopulation.addAll(population);
        jointPopulation.addAll(offspringPopulation);
        // Create the solutionSet union of solutionSet and offSpring
        //union = ((SolutionSet) population).union(offspringPopulation);

        // Ranking the union considering the values of the Achievement Scalarizing Function
        ranking = new RankingASFs<S>(jointPopulation, achievementScalarizingFunction, this.weights, normalization);

        // Obtain the next front
        int remain = populationSize;
        int index = 0;
        List<S> front;
        population.clear();
        front = ranking.getSubfront(index);

        while ((remain > 0) && (remain >= front.size())) {
          // Add the individuals of this front
          for (int k = 0; k < front.size(); k++) {
            population.add(front.get(k));
          }

          // Decrement remain
          remain = remain - front.size();

          // Obtain the next front
          index++;
          if (remain > 0) {
            front = ranking.getSubfront(index);
          }
        } // while

        // Remain is less than front(index).size, insert only the best one
        if (remain > 0) {
          // front contains individuals to insert
          for (int k = 0; k < remain; k++) {
            population.add(front.get(k));
          }

          remain = 0;
        } // if

        // If the nadir and ideal points of the problem are not known, they
        // are estimated for the Achievement Scalarizing Function
        if (estimatePoints) {
          updateLowerBounds(ranking.getSubfront(0));
          updateUpperBounds(ranking.getSubfront(0));
        }
      } // end while (evaluations < maxEvaluations)

      // Return the first non-dominated front
      ranking = new RankingASFs<S>(population, achievementScalarizingFunction, this.weights, normalization);

      // If the nadir and ideal points of the problem are not known, they are
      // estimated for the Achievement Scalarizing Function
      if (estimatePoints) {
        updateLowerBounds(ranking.getSubfront(0));
        updateUpperBounds(ranking.getSubfront(0));
      }
    }
    System.out.println("End") ;
  }

	/**
	 * Update lower bounds (ideal point in a minimization problem) of the Achievement Scalarizing Function
	 * considering the values of the given <code>Solution</code>
	 * 
	 * @param individual
	 */
	private void updateLowerBounds(S individual) {
		for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
			if (individual.getObjective(n) < this.achievementScalarizingFunction.getIdeal()[n]) {
				this.achievementScalarizingFunction.setIdeal(n, individual.getObjective(n));
			}
		}
	}

	/**
	 * Update upper bounds (nadir point in a minimization problem) of the Achievement Scalarizing Function
	 * considering the values of the given <code>Solution</code>
	 * 
	 * @param individual
	 */
	private void updateUpperBounds(S individual) {
		for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
			if (individual.getObjective(n) > this.achievementScalarizingFunction.getNadir()[n]) {
				this.achievementScalarizingFunction.setNadir(n, individual.getObjective(n));
			}
		}
	}

	/**
	 * Update lower and upper bounds (ideal and nadir points in a minimization
	 * problem) of the Achievement Scalarizing Function considering the values of the given
	 * <code>Solution</code>
	 * 
	 * @param individual
	 */
	private void updateBounds(S individual) {
		updateLowerBounds(individual);
		updateUpperBounds(individual);
	}

	/**
	 * Update upper bounds (nadir point in a minimization problem) of the Achievement Scalarizing Function
	 * considering the values of the given <code>SolutionSet</code>
	 * 
	 * @param population
	 */
	private void updateUpperBounds(List<S> population) {
		initializeUpperBounds(population.get(0));

		for (int i = 1; i < population.size(); i++) {
			updateUpperBounds(population.get(i));
		}
	}

	/**
	 * Update lower bounds (ideal point in a minimization problem) of the Achievement Scalarizing Function
	 * considering the values of the given <code>SolutionSet</code>
	 * 
	 * @param population
	 */
	private void updateLowerBounds(List<S> population) {
		initializeLowerBounds(population.get(0));

		for (int i = 1; i < population.size(); i++) {
			updateLowerBounds(population.get(i));
		}
	}

	/**
	 * Initialize upper bounds (nadir point in a minimization problem) of the
	 * Achievement Scalarizing Function considering the values of the given <code>Solution</code>
	 * 
	 * @param individual
	 */
	private void initializeUpperBounds(S individual) {
		for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
			this.achievementScalarizingFunction.setNadir(n, individual.getObjective(n));
		}
	}

	/**
	 * Initialize lower bounds (ideal point in a minimization problem) of the
	 * Achievement Scalarizing Function considering the values of the given <code>Solution</code>
	 * 
	 * @param individual
	 */
	private void initializeLowerBounds(S individual) {
		for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
			this.achievementScalarizingFunction.setIdeal(n, individual.getObjective(n));
		}
	}

	/**
	 * Initialize nadir and ideal points of the Achievement Scalarizing Function to the worst values
	 */
	private void initializeBounds() {
		for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
			this.achievementScalarizingFunction.setIdeal(n, Double.MAX_VALUE);
			this.achievementScalarizingFunction.setNadir(n, Double.MIN_VALUE);
		}
	}

	@Override
	public List<S> getResult() {
		return ranking.getSubfront(0);
	}
}
