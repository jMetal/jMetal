//  OMOPSO.java
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

package org.uma.jmetal.algorithm.multiobjective.omopso;


import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.NonUniformMutation;
import org.uma.jmetal.operator.impl.mutation.UniformMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/** Class implementing the OMOPSO algorithm */
public class OMOPSOBuilder implements AlgorithmBuilder<OMOPSO> {
  protected DoubleProblem problem;
  protected SolutionListEvaluator<DoubleSolution> evaluator;

  private int swarmSize = 100 ;
  private int archiveSize = 100 ;
  private int maxIterations = 25000 ;

  private UniformMutation uniformMutation ;
  private NonUniformMutation nonUniformMutation ;

  public OMOPSOBuilder(DoubleProblem problem, SolutionListEvaluator<DoubleSolution> evaluator) {
    this.evaluator = evaluator ;
    this.problem = problem ;
  }

  public OMOPSOBuilder setSwarmSize(int swarmSize) {
    this.swarmSize = swarmSize ;

    return this ;
  }

  public OMOPSOBuilder setArchiveSize(int archiveSize) {
    this.archiveSize = archiveSize ;

    return this ;
  }

  public OMOPSOBuilder setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations ;

    return this ;
  }

  public OMOPSOBuilder setUniformMutation(MutationOperator<DoubleSolution> uniformMutation) {
    this.uniformMutation = (UniformMutation)uniformMutation ;

    return this ;
  }

  public OMOPSOBuilder setNonUniformMutation(MutationOperator<DoubleSolution> nonUniformMutation) {
    this.nonUniformMutation = (NonUniformMutation)nonUniformMutation ;

    return this ;
  }

  /* Getters */
  public int getArchiveSize() {
    return archiveSize;
  }

  public int getSwarmSize() {
    return swarmSize;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public UniformMutation getUniformMutation() {
    return uniformMutation;
  }

  public NonUniformMutation getNonUniformMutation() {
    return nonUniformMutation;
  }

  public OMOPSO build() {
    return new OMOPSO(problem, evaluator, swarmSize, maxIterations, archiveSize, uniformMutation,
        nonUniformMutation) ;
  }
}
