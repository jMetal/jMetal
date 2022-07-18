package org.uma.jmetal.experimental.auto.parameter.catalogue;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.BestSolutionsArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.archive.impl.HypervolumeArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.archive.impl.SpatialSpreadDeviationArchive;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.WFGHypervolume;

public class ExternalArchiveParameter extends CategoricalParameter {
  private int size ;
  public ExternalArchiveParameter(String[] args, List<String> archiveTypes) {
    super("externalArchive", args, archiveTypes);
  }

  public @NotNull Archive<DoubleSolution> getParameter() {
    Archive<DoubleSolution> archive;

    switch (getValue()) {
      case "crowdingDistanceArchive":
        archive = new CrowdingDistanceArchive<>(size) ;
        break;
      case "hypervolumeArchive":
        archive = new HypervolumeArchive<>(size, new WFGHypervolume<>()) ;
        break;
      case "spatialSpreadDeviationArchive":
        archive = new SpatialSpreadDeviationArchive<>(size) ;
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
