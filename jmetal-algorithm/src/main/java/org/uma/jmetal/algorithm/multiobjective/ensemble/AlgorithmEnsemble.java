package org.uma.jmetal.algorithm.multiobjective.ensemble;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.checking.Check;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmEnsemble<S extends Solution<?>> implements Algorithm<List<S>> {
  private List<Algorithm<List<S>>> algorithmList;
  private Archive<S> archive;
  private long totalComputingTime;

  public AlgorithmEnsemble(List<Algorithm<List<S>>> algorithmList, Archive<S> archive) {
    Check.isNotNull(algorithmList);
    Check.isNotNull(archive);
    Check.that(algorithmList.size() > 0, "The algorithm list is empty");

    this.algorithmList = algorithmList;
    this.archive = archive;
  }

  @Override
  public void run() {
    long startComputingTime = System.currentTimeMillis();
    List<S> bagOfSolutions = new ArrayList<>();
    for (Algorithm<List<S>> algorithm : algorithmList) {
      algorithm.run();
      JMetalLogger.logger.info(
          "Algorithm "
              + algorithm.getName()
              + " finished. "
              + (System.currentTimeMillis() - startComputingTime));
      for (S solution : algorithm.getResult()) {
        solution.setAttribute("ALGORITHM_NAME", algorithm.getName());
      }
      bagOfSolutions.addAll(algorithm.getResult());
    }

    for (S solution : bagOfSolutions) {
      archive.add(solution);
    }

    totalComputingTime = System.currentTimeMillis() - startComputingTime;
  }

  @Override
  public List<S> getResult() {
    return archive.getSolutionList();
  }

  public Archive<S> getArchive() {
    return archive;
  }

  public List<Algorithm<List<S>>> getAlgorithmList() {
    return algorithmList;
  }

  @Override
  public String getName() {
    return "Ensemble";
  }

  public long getTotalComputingTime() {
    return totalComputingTime;
  }

  @Override
  public String getDescription() {
    return "Ensemble of multiobjective algorithms using an external archive";
  }
}
