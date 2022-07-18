package org.uma.jmetal.problem.multiobjective.maf;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class representing problem MaF05
 */
@SuppressWarnings("serial")
public class MaF09 extends AbstractDoubleProblem {

  public int maxinter9;
  public int pindex9[];
  public int M9;
  public double points9[][], rangex9[][], rangey9[][], r_polyline9[][], oth_poly_points9[][];

  /**
   * Default constructor
   */
  public MaF09() {
    this(2, 10) ;
  }

  /**
   * Creates a MaF09 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public MaF09(Integer numberOfVariables,
      Integer numberOfObjectives) {
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF09");
    M9 = numberOfObjectives;

    //other constants during the whole process once M&D are defined
    //original polygon
    double r = 1;
    double[] @NotNull [] p = polygonpoints(numberOfObjectives, r);
    points9 = p;
   //range--line segment
    double[][] rx9 = new double[numberOfObjectives][2];
    double[][] ry9 = new double[numberOfObjectives][2];
    for (int i = 0; i < numberOfObjectives - 1; i++) {
      rx9[i][0] = p[i][0] <= p[i + 1][0] ? p[i][0] : p[i + 1][0];
      rx9[i][1] = p[i][0] <= p[i + 1][0] ? p[i + 1][0] : p[i][0];
      ry9[i][0] = p[i][1] <= p[i + 1][1] ? p[i][1] : p[i + 1][1];
      ry9[i][1] = p[i][1] <= p[i + 1][1] ? p[i + 1][1] : p[i][1];
    }
    rx9[numberOfObjectives - 1][0] =
        p[numberOfObjectives - 1][0] <= p[0][0] ? p[numberOfObjectives - 1][0] : p[0][0];
    rx9[numberOfObjectives - 1][1] =
        p[numberOfObjectives - 1][0] <= p[0][0] ? p[0][0] : p[numberOfObjectives - 1][0];
    ry9[numberOfObjectives - 1][0] =
        p[numberOfObjectives - 1][1] <= p[0][1] ? p[numberOfObjectives - 1][1] : p[0][1];
    ry9[numberOfObjectives - 1][1] =
        p[numberOfObjectives - 1][1] <= p[0][1] ? p[0][1] : p[numberOfObjectives - 1][1];
    rangex9 = rx9;
    rangey9 = ry9;
    double[][] c9 = lines_of_polygon(p);
    r_polyline9 = c9;
    //generated other polygons
    int maxinterval = (int) Math.ceil(numberOfObjectives / 2.0 - 2);
    maxinter9 = maxinterval;
      int lenp = 0;
      int bound = 1 + maxinterval;
      for (int i1 = 2; i1 <= bound; i1++) {
          lenp += i1;
      }
      lenp *= (numberOfObjectives * 2);
    double[][] opoly9 = new double[lenp][2];
    int[] head = new int[maxinterval * numberOfObjectives];//i
    int[] tail = new int[maxinterval * numberOfObjectives];//n
    for (int i = 0; i < maxinterval; i++) {
      for (int j = 0; j < numberOfObjectives; j++) {
        head[i * numberOfObjectives + j] = j;
        tail[i * numberOfObjectives + j] = j + i + 1;
      }
    }
    //evaluate the intersection and symmetric points for each pair of vertexes i,n
    int @NotNull [] v4 = new int[4];
    int tv;
    double @NotNull [] kb1 = new double[3];
    double[] kb2 = new double[3];
    double @NotNull [] interp = new double[2];
    int[] pind = new int[head.length + 1];
    int ic = 0;
    for (int i = 0; i < head.length; i++) { //for each generated polygon
      pind[i] = ic;
      // vertexes i-1,i,n,n+1
      v4[0] = head[i] - 1;
      v4[1] = head[i];
      v4[2] = tail[i];
      v4[3] = tail[i] + 1;
      for (int j = 0; j < 4; j++) {
        v4[j] = (v4[j] + numberOfObjectives) % numberOfObjectives;
      }
      // intersection

      kb1 = line_of_twoP(p[v4[0]], p[v4[1]]);
      kb2 = line_of_twoP(p[v4[2]], p[v4[3]]);
      interp = intersection(kb1, kb2);
      // symmetric points
      for (int j = head[i]; j <= tail[i]; j++) {
        tv = (j + numberOfObjectives) % numberOfObjectives;
        opoly9[ic] = p[tv];
        ic++;
      }
      for (int j = head[i]; j <= tail[i]; j++) {
        tv = (j + numberOfObjectives) % numberOfObjectives;
        opoly9[ic][0] = 2 * interp[0] - p[tv][0];
        opoly9[ic][1] = 2 * interp[1] - p[tv][1];
        ic++;
      }
    } //end:generate polygons
    pind[pind.length - 1] = ic;
    pindex9 = pind;
    oth_poly_points9 = opoly9;

    List<Double> lower = new ArrayList<>(numberOfVariables), upper = new ArrayList<>(
        numberOfVariables);

      for (int i = 0; i < numberOfVariables; i++) {
          lower.add(-10000.0);
          upper.add(10000.0);
      }
      setVariableBounds(lower, upper);
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   */
  @Override
  public @NotNull DoubleSolution evaluate(@NotNull DoubleSolution solution) {
    int numberOfVariables_ = solution.variables().size();
    int numberOfObjectives = solution.objectives().length;

    double[] x;
    double @NotNull [] f = new double[numberOfObjectives];

      double[] arr = new double[10];
      int count = 0;
      for (int i1 = 0; i1 < numberOfVariables_; i1++) {
          double v = solution.variables().get(i1);
          if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
          arr[count++] = v;
      }
      arr = Arrays.copyOfRange(arr, 0, count);
      x = arr;

    // check if the point is infeasible
    boolean infeasible = false;
    infeasible = if_infeasible(x);
    while (infeasible) {
      //re-generate a random variable
      for (int i = 0; i < numberOfVariables_; i++) {
        Bounds<Double> bounds = getVariableBounds().get(i) ;
        x[i] = generV(bounds.getLowerBound(), bounds.getUpperBound());
        solution.variables().set(i, x[i]);
      }
      infeasible = if_infeasible(x);
    }

    // evaluate f1,...m-1
    for (int i = 0; i < M9 - 1; i++) {
      if (r_polyline9[i][0] == 1) {
        f[i] = Math.abs(x[0] - r_polyline9[i][1]);
      } else {
        f[i] = Math.abs((r_polyline9[i][1] * x[0] - x[1] + r_polyline9[i][2])) / Math
            .sqrt(Math.pow(r_polyline9[i][1], 2) + 1);
      }
    }
    // evaluate fm
    if (r_polyline9[M9 - 1][0] == 1) {
      f[M9 - 1] = Math.abs(x[0] - r_polyline9[M9 - 1][1]);
    } else {
      f[M9 - 1] = Math.abs((r_polyline9[M9 - 1][1] * x[0] - x[1] + r_polyline9[M9 - 1][2])) / Math
          .sqrt(Math.pow(r_polyline9[M9 - 1][1], 2) + 1);
    }

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.objectives()[i] = f[i];
    }
    return solution ;
  }

  public static double[][] polygonpoints(int m, double r) {
    double[] @NotNull [] p = new double[m][2];
    double[] angle;
    double thera = Math.PI / 2, rho = r;

    // vertexes with the number of edges(m)
      double @NotNull [] arr = new double[10];
      int count = 0;
      for (int i1 = 0; i1 < m; i1++) {
          double v = thera - 2 * (i1 + 1) * Math.PI / m;
          if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
          arr[count++] = v;
      }
      arr = Arrays.copyOfRange(arr, 0, count);
      angle = arr;
    for (int i = 0; i < m; i++) {
      p[i][0] = rho * Math.cos(angle[i]);
      p[i][1] = rho * Math.sin(angle[i]);
    }
    return p;
  }

  //given two points,evaluate slope(k),intercept(b) (or x=a,a value) of the straight line
  public static double[] line_of_twoP(double[] p1, double[] p2) {
    // kb[0]:if the line is vertical;kb[1]:a value if kb[0]=1,k value or else;kb[2]:insignificance if kb[0]=1,b value or else;
    double[] kb = new double[3];
    if (p1[0] == p2[0]) {
      kb[0] = 1;
      kb[1] = p1[0];
      kb[2] = 0;
    } else {
      kb[0] = 0;
      kb[1] = (p1[1] - p2[1]) / (p1[0] - p2[0]);
      kb[2] = p1[1] - kb[1] * p1[0];
    }
    return kb;
  }
  //given vertexes,evaluate the straight lines of a polygon

  public double[][] lines_of_polygon(double[][] p) {
    double[] @NotNull [] c9 = new double[p.length][3];
    for (int i = 0; i < p.length - 1; i++) {//		evaluate formula of the straight line l1,...,m-1
      c9[i] = line_of_twoP(p[i], p[i + 1]);
    }
    // evaluate formula of the straight line lm
    c9[p.length - 1] = line_of_twoP(p[p.length - 1], p[0]);
    return c9;
  }
  //given two straight lines,evaluate their intersection(assuming there is a intersection between these two lines)

  public static double[] intersection(double @NotNull [] kb1, double[] kb2) {
    double[] interp = new double[2];
    if (kb1[0] == 1) {
      interp[0] = kb1[1];
      interp[1] = kb2[1] * interp[0] + kb2[2];
    } else if (kb2[0] == 1) {
      interp[0] = kb2[1];
      interp[1] = kb1[1] * interp[0] + kb1[2];
    } else {
      interp[0] = (kb2[2] - kb1[2]) / (kb1[1] - kb2[1]);
      interp[1] = kb1[1] * interp[0] + kb1[2];
    }
    return interp;

  }
  //check if a point is inside any generated polygons(not including the boundary)(only for MaF9)

  public boolean if_infeasible(double[] x) {
    boolean infeasible = false;
    for (int i = 0; i < pindex9.length - 1; i++) {
      double[][] p = new double[pindex9[i + 1] - pindex9[i]][2];
      for (int j = pindex9[i]; j < pindex9[i + 1]; j++) {
        p[j - pindex9[i]] = oth_poly_points9[j];
      }
      infeasible = if_inside_polygon(x, p);
      if (infeasible) {
        break;
      }
    }
    if (infeasible) {//check if the point is on the edges of regular polygon
      for (int i = 0; i < r_polyline9.length - 1; i++) {
        if (r_polyline9[i][0] == 1) {
          if (x[0] == r_polyline9[i][1] && x[1] >= rangey9[i][0] && x[1] <= rangey9[i][1]) {
            infeasible = false;
            break;
          }
        } else {
          if ((x[0] * r_polyline9[i][1] + r_polyline9[i][2] == x[1]) && x[1] >= rangey9[i][0]
              && x[1] <= rangey9[i][1] && x[0] >= rangex9[i][0] && x[0] <= rangex9[i][1]) {
            infeasible = false;
            break;
          }
        }
      }
      if (r_polyline9[M9 - 1][0] == 1) {
        if (x[0] == r_polyline9[M9 - 1][1] && x[1] >= rangey9[M9 - 1][0] && x[1] <= rangey9[M9
            - 1][1]) {
          infeasible = false;
        }
      } else {
        if (x[0] * r_polyline9[M9 - 1][1] + r_polyline9[M9 - 1][2] == x[1] && x[1] >= rangey9[M9
            - 1][0] && x[1] <= rangey9[M9 - 1][1]
            && x[0] >= rangex9[M9 - 1][0] && x[0] <= rangex9[M9 - 1][1]) {
          infeasible = false;
        }
      }
    }

    return infeasible;
  }

  public static boolean if_inside_polygon(double[] p1, double[][] points) {
    boolean ifin;
    List<Point2D.Double> polygon;
    Point2D.@NotNull Double p = new Point2D.Double(p1[0], p1[1]);
    // if the point is inside the polygon(boundary not included)
      List<Point2D.Double> list = new ArrayList<>();
      for (double[] point : points) {
          Point2D.@NotNull Double aDouble = new Point2D.Double(point[0], point[1]);
          list.add(aDouble);
      }
      polygon = list;
    ifin = checkWithJdkGeneralPath(p, polygon);

    return ifin;
  }

  public static boolean checkWithJdkGeneralPath(Point2D.@NotNull Double point,
                                                @NotNull List<Point2D.Double> polygon) {
    java.awt.geom.@NotNull GeneralPath p = new java.awt.geom.GeneralPath();
    Point2D.Double first = polygon.get(0);
    p.moveTo(first.x, first.y);
    polygon.remove(0);
    for (Point2D.@NotNull Double d : polygon) {
      p.lineTo(d.x, d.y);
    }
    p.lineTo(first.x, first.y);
    p.closePath();
    return p.contains(point);
  }
  //generate a random variable with boundary lb,ub

  public static double generV(double lb, double ub) {
    double p;
    p = JMetalRandom.getInstance().nextDouble() * (ub - lb) + lb;
    return p;
  }
}
