//  ExperimentData.java 
//
//  Authors:
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

package org.uma.jmetal.util.experiment;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;

import java.util.List;

/**
 * Created by Antonio J. Nebro on 17/07/14.
 * 
 * Class for describing the configuration of a jMetal experiment
 */
public class ExperimentConfiguration<S extends Solution<?>, Result> {
	private String experimentName;
	private List<TaggedAlgorithm<Result>> algorithmList;
	private List<Problem<S>> problemList;
	private String experimentBaseDirectory;

	private String outputParetoFrontFileName;
	private String outputParetoSetFileName;
	private int independentRuns;

  private List<String> referenceFrontFileNames ;
  private String referenceFrontDirectory;

  private int numberOfCores ;

	/** Constructor */
	public ExperimentConfiguration(ExperimentConfigurationBuilder<S, Result> builder) {
		experimentName = builder.getExperimentName() ;
    this.experimentBaseDirectory = builder.getExperimentBaseDirectory() ;
    this.algorithmList = builder.getAlgorithmList() ;
    this.problemList = builder.getProblemList() ;
    this.independentRuns = builder.getIndependentRuns() ;
    this.outputParetoFrontFileName = builder.getOutputParetoFrontFileName() ;
    this.outputParetoSetFileName = builder.getOutputParetoSetFileName() ;
    this.numberOfCores = builder.getNumberOfCores() ;
    this.referenceFrontDirectory = builder.getReferenceFrontDirectory() ;
    this.referenceFrontFileNames = builder.getReferenceFrontFileNames() ;
  }

  /* Getters */
  public String getExperimentName() {
    return experimentName;
  }

  public List<TaggedAlgorithm<Result>> getAlgorithmList() {
    return algorithmList;
  }

  public List<Problem<S>> getProblemList() {
    return problemList;
  }

  public String getExperimentBaseDirectory() {
    return experimentBaseDirectory;
  }

  public String getOutputParetoFrontFileName() {
    return outputParetoFrontFileName;
  }

  public String getOutputParetoSetFileName() {
    return outputParetoSetFileName;
  }

  public int getIndependentRuns() {
    return independentRuns;
  }

  public int getNumberOfCores() {
    return numberOfCores ;
  }

  public List<String> getReferenceFrontFileNames() {
    return referenceFrontFileNames;
  }

  public String getReferenceFrontDirectory() {
    return referenceFrontDirectory;
  }
  
}
