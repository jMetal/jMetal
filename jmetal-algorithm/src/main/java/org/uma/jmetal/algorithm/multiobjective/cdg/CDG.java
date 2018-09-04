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

package org.uma.jmetal.algorithm.multiobjective.cdg;

import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.mutation.CDGMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;

/**
 *	Xinye Cai, Zhiwei Mei, Zhun Fan, Qingfu Zhang, 
 *	A Constrained Decomposition Approach with Grids for Evolutionary Multiobjective Optimization, 
 *	IEEE Transaction on Evolutionary Computation, press online, 2018, DOI: 10.1109/TEVC.2017.2744674
 *	The paper and Matlab code can be download at 
 *  http://xinyecai.github.io/
 *
 * @author  Feng Zhang
 * @version 1.0
 */
@SuppressWarnings("serial")
public class CDG extends AbstractCDG<DoubleSolution> {
  private DifferentialEvolutionCrossover differentialEvolutionCrossover ;

  public CDG(Problem<DoubleSolution> problem,
      int populationSize,
      int resultPopulationSize,
      int maxEvaluations,
      CrossoverOperator<DoubleSolution> crossover,
      double neighborhoodSelectionProbability,
      double sigma_,
      int k_,
      int t_,
      int subproblemNum_,
      int childGrid_,
      int childGridNum_) {
    super(problem, populationSize, resultPopulationSize, maxEvaluations, crossover,
        neighborhoodSelectionProbability, sigma_, k_, t_, subproblemNum_, childGrid_, childGridNum_);

    differentialEvolutionCrossover = (DifferentialEvolutionCrossover)crossoverOperator ;
  }

  @Override public void run() {
    initializePopulation() ;

    initializeIdealPoint() ;
    
    initializeNadirPoint() ;
    
    evaluations = populationSize;

    int maxGen = (int) (maxEvaluations / populationSize);
    int gen = 0;
    
    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    
    double delta;

    do {
    	
      updateNeighborhood();

      delta = Math.pow((1 - evaluations / maxEvaluations), 0.7);
      
      int[] permutation = new int[populationSize];
      MOEADUtils.randomPermutation(permutation, populationSize);

      MutationOperator<DoubleSolution> mutation = new CDGMutation(mutationProbability, delta);
      
      for (int i = 0; i < populationSize; i++) {
        int subProblemId = permutation[i];

        NeighborType neighborType = chooseNeighborType(subProblemId) ;
        List<DoubleSolution> parents = parentSelection(subProblemId, neighborType) ;
        
        differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
        List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);

        DoubleSolution child = children.get(0) ;
        mutation.execute(child);
        
        problem.evaluate(child);
        
        evaluations++;
        
        initialCDGAttributes(child);
        
        population.add(child);
      }
      
      gen++;
     
      for(int i = 0;i < population.size();i++)
    	  updateIdealPoint(population.get(i));
      
      if(gen % 20 == 0)
    	  initializeNadirPoint();

      if(problem.getNumberOfObjectives() == 3){
          updateBorder();
    	  excludeBadSolution3();
    	  chooseSpecialPopulation();
    	  gridSystemSetup3();
      }
      else{
    	  excludeBadSolution();
    	  chooseSpecialPopulation();
    	  gridSystemSetup();
      }
    	  
      if(population.size() < populationSize)
    	  supplyBadSolution();
      else
          rankBasedSelection();

    } while (gen < maxGen);
  }

  protected void initializePopulation() {
    for (int i = 0; i < populationSize; i++) {
      DoubleSolution newSolution = (DoubleSolution)problem.createSolution();

      problem.evaluate(newSolution);
      initialCDGAttributes(newSolution);
      population.add(newSolution);
      
    }
  }

  @Override public String getName() {
    return "CDG" ;
  }

  @Override public String getDescription() {
	return "A Constrained Decomposition Approach with Grids for Evolutionary Multiobjective Optimization";
  }
}
