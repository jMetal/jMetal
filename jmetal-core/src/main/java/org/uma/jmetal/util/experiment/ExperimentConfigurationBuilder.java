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
 * Builder for class {@link ExperimentConfiguration}
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ExperimentConfigurationBuilder<S extends Solution<?>, Result> {
  private final String experimentName ;
  private List<TaggedAlgorithm<Result>> algorithmList;
  private List<Problem<S>> problemList;
  private List<String> referenceFrontFileNames ;
  private String referenceFrontDirectory;
  private String experimentBaseDirectory;
  private String outputParetoFrontFileName;
  private String outputParetoSetFileName;
  private int independentRuns;

  private List<GenericIndicator<List<? extends Solution<?>>>> indicatorList ;

  private int numberOfCores ;

  public ExperimentConfigurationBuilder(String experimentName) {
    this.experimentName = experimentName ;
    this.independentRuns = 1 ;
    this.numberOfCores = 1 ;
    this.referenceFrontFileNames = null ;
    this.referenceFrontDirectory = null ;
  }

  public ExperimentConfigurationBuilder<S, Result> setAlgorithmList(List<TaggedAlgorithm<Result>> algorithmList) {
    this.algorithmList = new ArrayList<>(algorithmList) ;

    return this ;
  }

  public ExperimentConfigurationBuilder<S, Result> setProblemList(List<Problem<S>> problemList) {
    this.problemList = new ArrayList<>(problemList) ;

    return this ;
  }

  public ExperimentConfigurationBuilder<S, Result> setExperimentBaseDirectory(String experimentBaseDirectory) {
    this.experimentBaseDirectory = experimentBaseDirectory+"/"+experimentName ;

    return this ;
  }

  public ExperimentConfigurationBuilder<S, Result> setReferenceFrontDirectory(String referenceFrontDirectory) {
    this.referenceFrontDirectory = referenceFrontDirectory ;

    return this ;
  }

  public ExperimentConfigurationBuilder<S, Result> setReferenceFrontFileNames(List<String> referenceFrontFileNames) {
    this.referenceFrontFileNames = referenceFrontFileNames ;

    return this ;
  }

  public ExperimentConfigurationBuilder<S, Result> setIndicatorList(
      List<GenericIndicator<List<? extends Solution<?>>>> indicatorList ) {
    this.indicatorList = indicatorList ;

    return this ;
  }

  public ExperimentConfigurationBuilder<S, Result> setOutputParetoFrontFileName(String outputParetoFrontFileName) {
    this.outputParetoFrontFileName = outputParetoFrontFileName ;

    return this ;
  }

  public ExperimentConfigurationBuilder<S, Result> setOutputParetoSetFileName(String outputParetoSetFileName) {
    this.outputParetoSetFileName = outputParetoSetFileName ;

    return this ;
  }

  public ExperimentConfigurationBuilder<S, Result> setIndependentRuns(int independentRuns) {
    this.independentRuns = independentRuns ;

    return this ;
  }

  public ExperimentConfigurationBuilder<S, Result> setNumberOfCores(int numberOfCores) {
    this.numberOfCores = numberOfCores;

    return this ;
  }

  public ExperimentConfiguration<S, Result> build() {
    return new ExperimentConfiguration<S, Result>(this);
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
    return numberOfCores;
  }

  public List<String> getReferenceFrontFileNames() {
    return referenceFrontFileNames;
  }

  public String getReferenceFrontDirectory() {
    return referenceFrontDirectory;
  }

  public List<GenericIndicator<List<? extends Solution<?>>>> getIndicatorList() {
    return indicatorList;
  }
}
