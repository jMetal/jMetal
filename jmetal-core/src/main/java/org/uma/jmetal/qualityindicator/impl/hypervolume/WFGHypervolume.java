package org.uma.jmetal.qualityindicator.impl.hypervolume;

import org.uma.jmetal.qualityindicator.impl.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.util.WfgHypervolumeFront;
import org.uma.jmetal.qualityindicator.impl.hypervolume.util.WfgHypervolumeVersion;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.HypervolumeContributionComparator;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;
import org.uma.jmetal.util.solutionattribute.impl.HypervolumeContributionAttribute;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajnebro on 2/2/15.
 */
@SuppressWarnings("serial")
public class WFGHypervolume<S extends Solution<?>> extends Hypervolume<S> {

  private Point referencePoint;
  private int numberOfObjectives;

  private static final double DEFAULT_OFFSET = 100.0 ;
  private double offset = DEFAULT_OFFSET ;
  /**
   * Default constructor
   */
  public WFGHypervolume() {
  }

  /**
   * Constructor
   *
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public WFGHypervolume(String referenceParetoFrontFile) throws FileNotFoundException {
    super(referenceParetoFrontFile) ;
    numberOfObjectives = referenceParetoFront.getPointDimensions() ;
    referencePoint = null ;
    updateReferencePoint(referenceParetoFront);
  }

  /**
   * Constructor
   *
   * @param referenceParetoFront
   * @throws FileNotFoundException
   */
  public WFGHypervolume(Front referenceParetoFront) {
    super(referenceParetoFront) ;
    numberOfObjectives = referenceParetoFront.getPointDimensions() ;
    referencePoint = null ;
    updateReferencePoint(referenceParetoFront);
  }

  @Override
  public Double evaluate(List<S> solutionList) {
    double hv;
    if (solutionList.size() == 0) {
      hv = 0.0;
    } else {
      numberOfObjectives = solutionList.get(0).getNumberOfObjectives();
      referencePoint = new ArrayPoint(numberOfObjectives);
      updateReferencePoint(solutionList);

      if (numberOfObjectives == 2) {
        Collections.sort(solutionList, new ObjectiveComparator<Solution<?>>(numberOfObjectives-1,
            ObjectiveComparator.Ordering.DESCENDING));
        hv = get2DHV(solutionList) ;
      } else {
        updateReferencePoint(solutionList);
        WfgHypervolumeVersion wfgHv = new WfgHypervolumeVersion(numberOfObjectives, solutionList.size());
        hv = wfgHv.getHV(new WfgHypervolumeFront(solutionList));
      }
    }

    return hv;
  }

  public double computeHypervolume(List<S> solutionList, Point referencePoint) {
    double hv = 0.0;
    if (solutionList.size() == 0) {
      hv = 0.0;
    } else {
      numberOfObjectives = solutionList.get(0).getNumberOfObjectives();
      this.referencePoint = referencePoint;

      if (numberOfObjectives == 2) {
        Collections.sort(solutionList, new ObjectiveComparator<S>(1,
            ObjectiveComparator.Ordering.DESCENDING));
        hv = get2DHV(solutionList) ;
      } else {
        WfgHypervolumeVersion wfgHv = new WfgHypervolumeVersion(numberOfObjectives, solutionList.size());
        hv = wfgHv.getHV(new WfgHypervolumeFront(solutionList));
      }
    }

    return hv;
  }

  /**
   * Updates the reference point
   */
  private void updateReferencePoint(List<? extends Solution<?>> solutionList) {
    double[] maxObjectives = new double[numberOfObjectives];
    for (int i = 0; i < numberOfObjectives; i++) {
      maxObjectives[i] = 0;
    }

    for (int i = 0; i < solutionList.size(); i++) {
      for (int j = 0; j < numberOfObjectives; j++) {
        if (maxObjectives[j] < solutionList.get(i).getObjective(j)) {
          maxObjectives[j] = solutionList.get(i).getObjective(j) ;
        }
      }
    }

    if (referencePoint == null) {
      referencePoint = new ArrayPoint(numberOfObjectives) ;
      for (int i = 0; i < numberOfObjectives ; i++) {
        referencePoint.setValue(i, Double.MAX_VALUE);
      }
    }

    for (int i = 0; i < referencePoint.getDimension(); i++) {
      referencePoint.setValue(i, maxObjectives[i] + offset);
    }
  }

  /**
   * Updates the reference point
   */
  private void updateReferencePoint(Front front) {
    double[] maxObjectives = new double[numberOfObjectives];
    for (int i = 0; i < numberOfObjectives; i++) {
      maxObjectives[i] = 0;
    }

    for (int i = 0; i < front.getNumberOfPoints(); i++) {
      for (int j = 0; j < numberOfObjectives; j++) {
        if (maxObjectives[j] < front.getPoint(i).getValue(j)) {
          maxObjectives[j] = front.getPoint(i).getValue(j) ;
        }
      }
    }

    if (referencePoint == null) {
      referencePoint = new ArrayPoint(numberOfObjectives) ;
      for (int i = 0; i < numberOfObjectives ; i++) {
        referencePoint.setValue(i, Double.MAX_VALUE);
      }
    }

    for (int i = 0; i < referencePoint.getDimension(); i++) {
      referencePoint.setValue(i, maxObjectives[i] + offset);
    }
  }

  /**
   * Computes the HV of a solution list.
   * REQUIRES: The problem is bi-objective
   * REQUIRES: The setArchive is ordered in descending order by the second objective
   *
   * @return
   */
  public double get2DHV(List<? extends Solution<?>> solutionSet) {
    double hv = 0.0;
    if (solutionSet.size() > 0) {
      hv = Math.abs((solutionSet.get(0).getObjective(0) - referencePoint.getValue(0)) *
          (solutionSet.get(0).getObjective(1) - referencePoint.getValue(1)));

      for (int i = 1; i < solutionSet.size(); i++) {
        double tmp ;
        tmp = Math.abs((solutionSet.get(i).getObjective(0) - referencePoint.getValue(0)) * (solutionSet.get(i).getObjective(1) - solutionSet.get(i - 1).getObjective(1)));
        hv += tmp;
      }
    }
    return hv;
  }

  @Override
  public List<S> computeHypervolumeContribution(List<S> solutionList, List<S> referenceFrontList) {
    numberOfObjectives = solutionList.get(0).getNumberOfObjectives() ;
    updateReferencePoint(referenceFrontList);
    if (solutionList.size() > 1) {
      double[] contributions = new double[solutionList.size()];
      double solutionSetHV = 0;

      solutionSetHV = evaluate(solutionList);

      for (int i = 0; i < solutionList.size(); i++) {
        S currentPoint = solutionList.get(i);
        solutionList.remove(i);

        if (numberOfObjectives == 2) {
          contributions[i] = solutionSetHV - get2DHV(solutionList);
        } else {
          //Front front = new Front(solutionSet.size(), numberOfObjectives, solutionSet);
          WfgHypervolumeFront front = new WfgHypervolumeFront(solutionList);
          double hv = new WfgHypervolumeVersion(numberOfObjectives, solutionList.size()).getHV(front);
          contributions[i] = solutionSetHV - hv;
        }

        solutionList.add(i, currentPoint);
      }

      HypervolumeContributionAttribute<Solution<?>> hvContribution = new HypervolumeContributionAttribute<Solution<?>>();
      for (int i = 0; i < solutionList.size(); i++) {
        hvContribution.setAttribute(solutionList.get(i), contributions[i]);
      }

      Collections.sort(solutionList, new HypervolumeContributionComparator<S>());
    }

    return solutionList ;
  }

  @Override
  public double getOffset() {
    return offset;
  }

  @Override
  public void setOffset(double offset) {
    this.offset = offset ;
  }

  @Override public String getDescription() {
    return "WFG implementation of the hypervolume quality indicator" ;
  }

}
