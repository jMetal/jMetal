//  IParallelEvaluator.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro
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

package jmetal.util.parallel;

import jmetal.core.Problem;
import jmetal.core.Solution;

import java.util.List;

/**
 * @author Antonio J. Nebro
 * Interface representing classes for evaluating solutions in parallel
 * The procedure is:
 * 1- create the parallel evaluator with startEvaluator()
 * 2- add solutions for being evaluated with addSolutionforEvaluation()
 * 3- evaluate the solutions with parallelEvaluation()
 * 4- shutdown the parallel evaluator with stopEvaluator()
 */

public interface IParallelEvaluator {
	public void startEvaluator(Problem problem) ;
	public void addSolutionForEvaluation(Solution solution) ;
	public List<Solution> parallelEvaluation() ;
	public void stopEvaluator() ;
}
