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

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for describing the configuration of a jMetal experiment.
 *
 * Created by Antonio J. Nebro on 17/07/14.
 */
public class Experiment<S extends Solution<?>, Result> {
	private String experimentName;
	private List<TaggedAlgorithm<Result>> algorithmList;
	private List<Problem<S>> problemList;
	private String experimentBaseDirectory;

	private String outputParetoFrontFileName;
	private String outputParetoSetFileName;
	private int independentRuns;

  private List<String> referenceFrontFileNames ;
  private String referenceFrontDirectory;

  private List<GenericIndicator<S>> indicatorList ;

  private int numberOfCores ;

	/** Constructor */
	public Experiment(ExperimentBuilder<S, Result> builder) {
		this.experimentName = builder.getExperimentName() ;
    this.experimentBaseDirectory = builder.getExperimentBaseDirectory() ;
    this.algorithmList = builder.getAlgorithmList() ;
    this.problemList = builder.getProblemList() ;
    this.independentRuns = builder.getIndependentRuns() ;
    this.outputParetoFrontFileName = builder.getOutputParetoFrontFileName() ;
    this.outputParetoSetFileName = builder.getOutputParetoSetFileName() ;
    this.numberOfCores = builder.getNumberOfCores() ;
    this.referenceFrontDirectory = builder.getReferenceFrontDirectory() ;
    this.referenceFrontFileNames = builder.getReferenceFrontFileNames() ;
    this.indicatorList = builder.getIndicatorList() ;
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

  public List<GenericIndicator<S>> getIndicatorList() {
    return indicatorList;
  }

  /* Setters */
  public void setReferenceFrontDirectory(String referenceFrontDirectory) {
    this.referenceFrontDirectory = referenceFrontDirectory ;
  }

  public void setReferenceFrontFileNames(List<String> referenceFrontFileNames) {
    this.referenceFrontFileNames = referenceFrontFileNames ;
  }

  public void setAlgorithmList(List<TaggedAlgorithm<Result>> algorithmList) {
    this.algorithmList = algorithmList ;
  }

  /**
   * The list of algorithms contain an algorithm instance per problem. This is not convenient for
   * calculating statistical data, because a same algorithm will appear many times.
   * This method remove duplicated algorithms and leave only an instance of each one.
   */
  public void removeDuplicatedAlgorithms() {
    List<TaggedAlgorithm<Result>> algorithmList = new ArrayList<>() ;
    List<String> algorithmTagList = new ArrayList<>() ;

    for (TaggedAlgorithm<Result> algorithm : getAlgorithmList()) {
      if (!algorithmTagList.contains(algorithm.getTag())) {
        algorithmList.add(algorithm) ;
        algorithmTagList.add(algorithm.getTag()) ;
      }
    }

    setAlgorithmList(algorithmList);
  }
}
