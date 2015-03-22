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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 17/07/14.
 * 
 * Class for describing the configuration of a jMetal experiment
 */
public class ExperimentConfiguration {
	private String experimentName;
	private List<Algorithm> algorithmList;
	private List<Problem> problemList;
	private String experimentBaseDirectory;

	private String outputParetoFrontFileName;
	private String outputParetoSetFileName;
	private int independentRuns;

	/** Constructor */
	private ExperimentConfiguration(Builder builder) {
		experimentName = builder.experimentName ;
    this.experimentBaseDirectory = builder.experimentBaseDirectory ;
    this.algorithmList = builder.algorithmList ;
    this.problemList = builder.problemList ;
    this.independentRuns = builder.independentRuns ;
    this.outputParetoFrontFileName = builder.outputParetoFrontFileName ;
    this.outputParetoSetFileName = builder.outputParetoSetFileName ;
  }

	/* Getters */
	public String getExperimentName() {
		return experimentName;
	}

	public List<Algorithm> getAlgorithmList() {
		return algorithmList;
	}

	public List<Problem> getProblemList() {
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
	
	/** Builder */
	public static class Builder {
		private final String experimentName ;
		private List<Algorithm> algorithmList;
		private List<Problem> problemList;
		private String experimentBaseDirectory;
		private String outputParetoFrontFileName;
		private String outputParetoSetFileName;
		private int independentRuns;

		public Builder(String experimentName) {
			this.experimentName = experimentName ;
		}

		public Builder setAlgorithmList(List<Algorithm> algorithmList) {
			this.algorithmList = new ArrayList<>(algorithmList) ;

			return this ;
		}

		public Builder setProblemList(List<Problem> problemList) {
			this.problemList = new ArrayList<>(problemList) ;

			return this ;
		}

		public Builder setExperimentBaseDirectory(String experimentBaseDirectory) {
			this.experimentBaseDirectory = experimentBaseDirectory+"/"+experimentName ;

			return this ;
		}

		public Builder setOutputParetoFrontFileName(String outputParetoFrontFileName) {
			this.outputParetoFrontFileName = outputParetoFrontFileName ;

			return this ;
		}

		public Builder setOutputParetoSetFileName(String outputParetoSetFileName) {
			this.outputParetoSetFileName = outputParetoSetFileName ;

			return this ;
		}
		
		public Builder setIndependentRuns(int independentRuns) {
			this.independentRuns = independentRuns ;

			return this ;
		}

		public ExperimentConfiguration build() {
			return new ExperimentConfiguration(this) ;
		}
	}
}
