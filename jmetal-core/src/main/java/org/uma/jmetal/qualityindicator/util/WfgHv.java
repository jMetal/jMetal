package org.uma.jmetal.qualityindicator.util;

import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.point.Point;

import java.util.Comparator;

/**
 * Created by ajnebro on 2/2/15.
 */
public class WfgHv {
  static final int OPT = 2;
  private Front[] fs;
  private Point referencePoint;
  boolean maximizing;
  private int currentDeep;
  private int currentDimension;
  private int maxNumberOfPoints;
  private int maxNumberOfObjectives;
  private Comparator<Point> pointComparator;
}
