package org.uma.jmetal.util.archivewithreferencepoint;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.AbstractBoundedArchive;

import java.util.List;

public abstract class ArchiveWithMultipleReferencePoints<S extends Solution<?>> extends AbstractBoundedArchive<S> {
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
