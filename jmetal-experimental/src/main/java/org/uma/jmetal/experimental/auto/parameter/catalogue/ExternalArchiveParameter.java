package org.uma.jmetal.experimental.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.BestSolutionsArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;

public class ExternalArchiveParameter extends CategoricalParameter {
  private int size ;
  public ExternalArchiveParameter(String[] args, List<String> archiveTypes) {
    super("externalArchive", args, archiveTypes);
  }

  public Archive<DoubleSolution> getParameter() {
    Archive<DoubleSolution> archive;

    switch (getValue()) {
      case "crowdingDistanceArchive":
        archive = new CrowdingDistanceArchive<>(size) ;
        break;
      case "unboundedExternalArchive":
        archive = new BestSolutionsArchive<>(new NonDominatedSolutionListArchive<>(), size) ;
        break;
      default:
        throw new RuntimeException("Archive type does not exist: " + getName());
    }
    return archive;
  }

  public void setSize(int size) {
    this.size = size ;
  }
}
