package org.uma.jmetal.qualityindicator.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;
import org.uma.jmetal.util.solutionattribute.impl.HypervolumeContribution;

import java.util.Collections;
import java.util.List;

/**
 * Created by ajnebro on 2/2/15.
 */
public class FastHypervolume {
  private static final double DEFAULT_OFFSET = 20.0 ;
  private Point referencePoint;
  private int numberOfObjectives;
  private double offset ;
  //private HypervolumeContribution hvContribution ;

  public FastHypervolume() {
    this(DEFAULT_OFFSET) ;
  }

  public FastHypervolume(double offset) {
    referencePoint = null;
    numberOfObjectives = 0;
    this.offset = offset;
    //hvContribution = new HypervolumeContribution() ;
  }

  public double computeHypervolume(List<? extends Solution<?>> solutionList) {
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
        WfgHypervolume wfgHv = new WfgHypervolume(numberOfObjectives, solutionList.size());
        hv = wfgHv.getHV(new WfgHypervolumeFront(solutionList));
      }
    }
/*
      updateReferencePoint(solutionSet);
      if (numberOfObjectives == 2) {
        solutionSet.sort(new ObjectiveComparator(numberOfObjectives - 1, true));
        hv = get2DHV(solutionSet);
      } else {
        updateReferencePoint(solutionSet);
        Front front = new Front(solutionSet.size(), numberOfObjectives, solutionSet);
        hv = new WfgHv(numberOfObjectives, solutionSet.size(), referencePoint).getHV(front);
      }
    }
*/
    return hv;
  }

  public double computeHypervolume(List<? extends Solution<?>> solutionList, Point referencePoint) {
    double hv = 0.0;
    if (solutionList.size() == 0) {
      hv = 0.0;
    } else {
      numberOfObjectives = solutionList.get(0).getNumberOfObjectives();
      this.referencePoint = referencePoint;
/*
      if (numberOfObjectives == 2) {
        solutionSet.sort(new ObjectiveComparator(numberOfObjectives - 1, true));

        hv = get2DHV(solutionSet);
      } else {
        WFGHV wfg = new WFGHV(numberOfObjectives, solutionSet.size());
        Front front = new Front(solutionSet.size(), numberOfObjectives, solutionSet);
        hv = wfg.getHV(front, referencePoint);
      }
      */
      if (numberOfObjectives == 2) {
        Collections.sort(solutionList, new ObjectiveComparator<Solution<?>>(solutionList.size()-1,
            ObjectiveComparator.Ordering.DESCENDING));
        hv = get2DHV(solutionList) ;
      } else {
        WfgHypervolume wfgHv = new WfgHypervolume(numberOfObjectives, solutionList.size());
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

    for (int i = 0; i < referencePoint.getNumberOfDimensions(); i++) {
      referencePoint.setDimensionValue(i, maxObjectives[i] + offset);
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
        if (maxObjectives[j] < front.getPoint(i).getDimensionValue(j)) {
          maxObjectives[j] = front.getPoint(i).getDimensionValue(j) ;
        }
      }
    }

    for (int i = 0; i < referencePoint.getNumberOfDimensions(); i++) {
      referencePoint.setDimensionValue(i, maxObjectives[i] + offset);
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
      hv = Math.abs((solutionSet.get(0).getObjective(0) - referencePoint.getDimensionValue(0)) *
          (solutionSet.get(0).getObjective(1) - referencePoint.getDimensionValue(1)));

      for (int i = 1; i < solutionSet.size(); i++) {
        double tmp =
            Math.abs((solutionSet.get(i).getObjective(0) - referencePoint.getDimensionValue(0)) *
                (solutionSet.get(i).getObjective(1) - solutionSet.get(i - 1).getObjective(1)));
        hv += tmp;
      }
    }
    return hv;
  }

  /**
   * Computes the HV contribution of the solutions
   *
   * @return
   */
  public <S extends Solution<?>> void computeHVContributions(List<S> solutionList) {
    double[] contributions = new double[solutionList.size()];
    double solutionSetHV = 0;

    solutionSetHV = computeHypervolume(solutionList);

    for (int i = 0; i < solutionList.size(); i++) {
      S currentPoint = solutionList.get(i);
      solutionList.remove(i);

      if (numberOfObjectives == 2) {
        contributions[i] = solutionSetHV - get2DHV(solutionList);
      } else {
        //Front front = new Front(solutionSet.size(), numberOfObjectives, solutionSet);
        WfgHypervolumeFront front = new WfgHypervolumeFront(solutionList);
        double hv = new WfgHypervolume(numberOfObjectives, solutionList.size()).getHV(front);
        contributions[i] = solutionSetHV - hv;
      }

      solutionList.add(i, currentPoint);
    }

    HypervolumeContribution<Solution<?>> hvContribution = new HypervolumeContribution<Solution<?>>() ;
    for (int i = 0; i < solutionList.size(); i++) {
      hvContribution.setAttribute(solutionList.get(i), contributions[i]);
      //solutionList.get(i).setCrowdingDistance(contributions[i]);
    }
  }

  /**
   * Computes the HV contribution of a solutiontype in a solutiontype set.
   * REQUIRES: the solutiontype belongs to the solutiontype set
   * REQUIRES: the HV of the solutiontype set is computed beforehand and its value is passed as third parameter
   *
   * @return The hv contribution of the solutiontype
   */
  public <S extends Solution<?>> double computeSolutionHVContribution(List<S> solutionList, int solutionIndex,
      double solutionSetHV) {
    double contribution;

    S currentPoint = solutionList.get(solutionIndex);
    solutionList.remove(solutionIndex);

    WfgHypervolumeFront front = new WfgHypervolumeFront(solutionList);
    double hv =
        new WfgHypervolume(numberOfObjectives, solutionList.size(), referencePoint).getHV(front);
    contribution = solutionSetHV - hv;

    solutionList.add(solutionIndex, currentPoint);
    HypervolumeContribution<Solution<?>> hvContribution = new HypervolumeContribution<Solution<?>>() ;
    hvContribution.setAttribute(solutionList.get(solutionIndex), contribution);
   // solutionSet.get(solutionIndex).setCrowdingDistance(contribution);

    return contribution;
  }

}
