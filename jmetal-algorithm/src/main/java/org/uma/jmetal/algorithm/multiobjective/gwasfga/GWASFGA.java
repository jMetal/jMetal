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

package org.uma.jmetal.algorithm.multiobjective.gwasfga;

import org.uma.jmetal.algorithm.multiobjective.gwasfga.util.GWASFGARanking;
import org.uma.jmetal.algorithm.multiobjective.mombi.util.ASFWASFGA;
import org.uma.jmetal.algorithm.multiobjective.mombi.util.AbstractUtilityFunctionsSet;
import org.uma.jmetal.algorithm.multiobjective.wasfga.WASFGA;
import org.uma.jmetal.algorithm.multiobjective.wasfga.util.WeightVector;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;

import java.util.List;

/**
 * This class executes the GWASFGA algorithm described in:
 * Saborido, R., Ruiz, A. B. and Luque, M. (2015). Global WASF-GA: An Evolutionary Algorithm in
 * Multiobjective Optimization to Approximate the whole Pareto Optimal Front.
 * Evolutionary Computation Accepted for publication.
 *
 * @author Juanjo Durillo
 */
public class GWASFGA<S extends Solution<?>> extends WASFGA<S> {
  final AbstractUtilityFunctionsSet<S> achievementScalarizingUtopia;
  final AbstractUtilityFunctionsSet<S> achievementScalarizingNadir;
  private static final long serialVersionUID = 1L;

  public GWASFGA(Problem<S> problem, int populationSize, int maxIterations, CrossoverOperator<S> crossoverOperator,
                 MutationOperator<S> mutationOperator, SelectionOperator<List<S>, S> selectionOperator,
                 SolutionListEvaluator<S> evaluator) {
    super(problem, populationSize, maxIterations, crossoverOperator, mutationOperator, selectionOperator, evaluator,
        null);
    setMaxPopulationSize(populationSize);

    double [][] weights =  WeightVector.initUniformWeights2D(0.005, getMaxPopulationSize());

    int halfVectorSize = weights.length  / 2;
    int evenVectorsSize    = (weights.length%2==0) ? halfVectorSize : (halfVectorSize+1);
    int oddVectorsSize     = halfVectorSize;

    double [][] evenVectors = new double[evenVectorsSize][getProblem().getNumberOfObjectives()];
    double [][] oddVectors = new double[oddVectorsSize][getProblem().getNumberOfObjectives()];

    int index = 0;
    for (int i = 0; i < weights.length; i = i + 2)
      evenVectors[index++] = weights[i];

    index = 0;
    for (int i = 1; i < weights.length; i = i + 2)
      oddVectors[index++] = weights[i];

    this.achievementScalarizingNadir  =  createUtilityFunction(this.getNadirPoint(), evenVectors);
    this.achievementScalarizingUtopia =  createUtilityFunction(this.getReferencePoint(), oddVectors);

  }

  public AbstractUtilityFunctionsSet<S> createUtilityFunction(List<Double> referencePoint, double [][] weights) {
    weights = WeightVector.invertWeights(weights,true);
    ASFWASFGA<S> aux = new ASFWASFGA<>(weights,referencePoint);

    return aux;
  }

  protected Ranking<S> computeRanking(List<S> solutionList) {
    Ranking<S> ranking = new GWASFGARanking<>(this.achievementScalarizingUtopia, this.achievementScalarizingNadir);
    ranking.computeRanking(solutionList);
    return ranking;
  }

  @Override public String getName() {
    return "GWASFGA" ;
  }

  @Override public String getDescription() {
    return "Global Weighting Achievement Scalarizing Function Genetic Algorithm" ;
  }
}
