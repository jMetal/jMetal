package org.uma.jmetal.newideas.cosine.rsmpso.archive.archivewithmultiplereferencepoints;

import org.uma.jmetal.newideas.cosine.rsmpso.archive.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.impl.AbstractBoundedArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class ArchiveWithMultipleReferencePoints<S extends Solution<?>> extends AbstractBoundedArchive<S>  {
  protected List<List<Double>> referencePoints ;
  protected int maximumSize ;
  protected List<ArchiveWithReferencePoint<S>> archivesWithReferencePoint ;

  public ArchiveWithMultipleReferencePoints(
          int maxSize,
          List<List<Double>> referencePoints,
          List<ArchiveWithReferencePoint<S>> archivesWithReferencePoint) {
    super(maxSize);
    this.maximumSize = maxSize;
    this.referencePoints = referencePoints;
    this.archivesWithReferencePoint = archivesWithReferencePoint ;
  }
}
