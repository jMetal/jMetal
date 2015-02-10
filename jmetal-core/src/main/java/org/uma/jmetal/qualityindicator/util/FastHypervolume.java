package org.uma.jmetal.qualityindicator.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.solutionattribute.impl.HypervolumeContribution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajnebro on 2/2/15.
 */
public class FastHypervolume {
  private static final double DEFAULT_OFFSET = 20.0 ;
  private Point referencePoint;
  private int numberOfObjectives;
  private double offset ;
  private HypervolumeContribution hvContribution ;

  public FastHypervolume() {
    this(DEFAULT_OFFSET) ;
  }

  public FastHypervolume(double offset) {
    referencePoint = null;
    numberOfObjectives = 0;
    this.offset = offset;
    hvContribution = new HypervolumeContribution() ;
  }

  public double computeHypervolume(List<? extends Solution> solutionList) {
    double hv;
    if (solutionList.size() == 0) {
      hv = 0.0;
    } else {
      numberOfObjectives = solutionList.get(0).getNumberOfObjectives();
      WfgHv wfgHv = new WfgHv(numberOfObjectives, solutionList.size());
      hv = wfgHv.getHV(new WfgHvFront(solutionList));
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

  public double computeHypervolume(List<? extends Solution> solutionList, Point referencePoint) {
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
      WfgHv wfgHv = new WfgHv(numberOfObjectives, solutionList.size());
      hv = wfgHv.getHV(new WfgHvFront(solutionList));
    }

    return hv;
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
  public double get2DHV(List<Solution> solutionSet) {
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

  private <T> void addHelper(List<T> list, T solution) {
     list.add(solution);
  }

  /**
   * Computes the HV contribution of the solutions
   *
   * @return
   */
  public void computeHVContributions(List<? extends Solution> solutionList) {
    List<Solution> list = (ArrayList<Solution>)solutionList ;
    double[] contributions = new double[list.size()];
    double solutionSetHV = 0;


    solutionSetHV = computeHypervolume(solutionList);

    for (int i = 0; i < solutionList.size(); i++) {
      Solution currentPoint = list.get(i);
      list.remove(i);

      //Front front = new Front(solutionSet.size(), numberOfObjectives, solutionSet);
      WfgHvFront front = new WfgHvFront(list);
      double hv =
          new WfgHv(numberOfObjectives, list.size()).getHV(front);
      contributions[i] = solutionSetHV - hv;

      list.add(i, currentPoint);
    }

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
  public double computeSolutionHVContribution(List<? extends Solution> solutionList, int solutionIndex,
      double solutionSetHV) {
    List<Solution> list = (ArrayList<Solution>)solutionList ;
    double contribution;

    Solution currentPoint = list.get(solutionIndex);
    list.remove(solutionIndex);

    WfgHvFront front = new WfgHvFront(list);
    double hv =
        new WfgHv(numberOfObjectives, list.size(), referencePoint).getHV(front);
    contribution = solutionSetHV - hv;

    list.add(solutionIndex, currentPoint);
    hvContribution.setAttribute(list.get(solutionIndex), contribution);
   // solutionSet.get(solutionIndex).setCrowdingDistance(contribution);

    return contribution;
  }

}
