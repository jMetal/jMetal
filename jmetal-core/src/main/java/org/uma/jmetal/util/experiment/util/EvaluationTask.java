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

package org.uma.jmetal.util.experiment.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Class defining tasks of an algorithm execution to be computed in parallel.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
class EvaluationTask<S extends Solution<?>, Result> implements Callable<Object> {
  private TaggedAlgorithm<Result> algorithm ;
  private int id;
  private String outputDirectoryName ;

  /** Constructor */
  public EvaluationTask(TaggedAlgorithm<Result> algorithm, int id, Experiment<?,?> experimentData) {
    JMetalLogger.logger.info(
        " Task: " + algorithm.getTag() + ", problem: " + algorithm.getProblem().getName() + ", run: " + id);
    this.algorithm = algorithm ;
    this.id = id;

    outputDirectoryName = experimentData.getExperimentBaseDirectory()
        + "/data/"
        + algorithm.getTag()
        + "/"
        + algorithm.getProblem().getName() ;

    File outputDirectory = new File(outputDirectoryName);
    if (!outputDirectory.exists()) {
      boolean result = new File(outputDirectoryName).mkdirs();
      if (result) {
        JMetalLogger.logger.info("Creating " + outputDirectoryName);
      } else {
        JMetalLogger.logger.severe("Creating " + outputDirectoryName + " failed");
      }
    }
  }

  @SuppressWarnings("unchecked")
  public Integer call() throws Exception {
    String funFile = outputDirectoryName + "/FUN" + id + ".tsv" ;
    String varFile = outputDirectoryName + "/VAR" + id + ".tsv" ;
    JMetalLogger.logger.info(
        " Running algorithm: " + algorithm.getTag() +
            ", problem: " + algorithm.getProblem().getName() +
            ", run: " + id+
            ", funFile: " + funFile);


    algorithm.run();
    Result population = algorithm.getResult() ;

    new SolutionListOutput((List<? extends S>) population)
        .setSeparator("\t")
        .setVarFileOutputContext(new DefaultFileOutputContext(varFile))
        .setFunFileOutputContext(new DefaultFileOutputContext(funFile))
        .print();

    return id;
  }

}
