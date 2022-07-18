package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.BestSolutionsArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.archive.impl.HypervolumeArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.archive.impl.SpatialSpreadDeviationArchive;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.WFGHypervolume;

public class ExternalArchiveParameter<S extends Solution<?>> extends CategoricalParameter {
  private int size ;
  public ExternalArchiveParameter(String parameterName, String[] args, List<String> archiveTypes) {
    super(parameterName, args, archiveTypes);
  }

  public ExternalArchiveParameter(String[] args, List<String> archiveTypes) {
    this("externalArchive", args, archiveTypes);
  }

  public @NotNull Archive<S> getParameter() {
    Archive<S> archive;

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
      case "unboundedArchive":
        archive = new BestSolutionsArchive<>(new NonDominatedSolutionListArchive<>(), size) ;
        break;
      default:
        throw new JMetalException("Archive type does not exist: " + getName());
    }
    return archive;
  }

  public void setSize(int size) {
    this.size = size ;
  }
}
