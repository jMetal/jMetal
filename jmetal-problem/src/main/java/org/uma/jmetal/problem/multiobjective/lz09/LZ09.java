package org.uma.jmetal.problem.multiobjective.lz09;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class to implement the problem of the lz09 benchmark, which is
 * defined in:
 * H. Li and Q. Zhang. Multiobjective optimization problem with complicated
 * pareto sets, MOEA/D and NSGA-II. IEEE Transactions on Evolutionary
 * Computation, 12(2):284-302, April 2009.
 */
public class LZ09 {
  int nvar;
  int nobj;
  int ltype;
  int dtype;
  int ptype;
  public LZ09(int nvar, int nobj, int ptype, int dtype, int ltype) {
    this.nvar = nvar;
    this.nobj = nobj;
    this.ltype = ltype;
    this.dtype = dtype;
    this.ptype = ptype;
  }

  // control the PF shape
  void alphaFunction(double alpha[], List<Double> x, int dim, int type) {
    if (dim == 2) {
      if (type == 21) {
        alpha[0] = x.get(0);
        alpha[1] = 1 - Math.sqrt(x.get(0));
      }

      if (type == 22) {
        alpha[0] = x.get(0);
        alpha[1] = 1 - x.get(0) * x.get(0);
      }

      if (type == 23) {
        alpha[0] = x.get(0);
        alpha[1] = 1 - Math.sqrt(alpha[0]) - alpha[0]
          * Math.sin(10 * alpha[0] * alpha[0] * Math.PI);
      }

      if (type == 24) {
        alpha[0] = x.get(0);
        alpha[1] = 1 - x.get(0) - 0.05 * Math.sin(4 * Math.PI * x.get(0));
      }
    } else {
      if (type == 31) {
        alpha[0] = Math.cos(x.get(0) * Math.PI / 2) * Math.cos(x.get(1) * Math.PI / 2);
        alpha[1] = Math.cos(x.get(0) * Math.PI / 2) * Math.sin(x.get(1) * Math.PI / 2);
        alpha[2] = Math.sin(x.get(0) * Math.PI / 2);
      }

      if (type == 32) {
        alpha[0] = 1 - Math.cos(x.get(0) * Math.PI / 2)
          * Math.cos(x.get(1) * Math.PI / 2);
        alpha[1] = 1 - Math.cos(x.get(0) * Math.PI / 2)
          * Math.sin(x.get(1) * Math.PI / 2);
        alpha[2] = 1 - Math.sin(x.get(0) * Math.PI / 2);
      }

      if (type == 33) {
        alpha[0] = x.get(0);
        alpha[1] = x.get(1);
        alpha[2] = 3
          - (Math.sin(3 * Math.PI * x.get(0)) + Math.sin(3 * Math.PI * x.get(1))) - 2
          * (x.get(0) + x.get(1));
      }

      if (type == 34) {
        alpha[0] = x.get(0) * x.get(1);
        alpha[1] = x.get(0) * (1 - x.get(1));
        alpha[2] = (1 - x.get(0));
      }
    }
  }

  // control the distance
  double betaFunction(List<Double> x, int type) {
    double beta;
    beta = 0;
    int dim = x.size();

    if (dim == 0) {
      beta = 0;
    }

    if (type == 1) {
      beta = 0;
      for (int i = 0; i < dim; i++) {
        beta += x.get(i) * x.get(i);
      }
      beta = 2.0 * beta / dim;
    }

    if (type == 2) {
      beta = 0;
      for (int i = 0; i < dim; i++) {
        beta += Math.sqrt(i + 1) * x.get(i) * x.get(i);
      }
      beta = 2.0 * beta / dim;
    }

    if (type == 3) {
      double sum = 0, xx;
      for (int i = 0; i < dim; i++) {
        xx = 2 * x.get(i);
        sum += (xx * xx - Math.cos(4 * Math.PI * xx) + 1);
      }
      beta = 2.0 * sum / dim;
    }

    if (type == 4) {
      double sum = 0, prod = 1, xx;
      for (int i = 0; i < dim; i++) {
        xx = 2 * x.get(i);
        sum += xx * xx;
        prod *= Math.cos(10 * Math.PI * xx / Math.sqrt(i + 1));
      }
      beta = 2.0 * (sum - 2 * prod + 2) / dim;
    }

    return beta;
  }

  //	control the PS shape of 2-d instances
  double psfunc2(double x, double t1, int dim, int type, int css) {
    // type:  the type of curve 
    // css:   the class of index
    double beta;
    beta = 0.0;

    dim++;

    if (type == 21) {
      double xy = 2 * (x - 0.5);
      beta = xy - Math.pow(t1, 0.5 * (nvar + 3 * dim - 8) / (nvar - 2));
    }

    if (type == 22) {
      double theta = 6 * Math.PI * t1 + dim * Math.PI / nvar;
      double xy = 2 * (x - 0.5);
      beta = xy - Math.sin(theta);
    }

    if (type == 23) {
      double theta = 6 * Math.PI * t1 + dim * Math.PI / nvar;
      double ra = 0.8 * t1;
      double xy = 2 * (x - 0.5);
      if (css == 1) {
        beta = xy - ra * Math.cos(theta);
      } else {
        beta = xy - ra * Math.sin(theta);
      }
    }

    if (type == 24) {
      double theta = 6 * Math.PI * t1 + dim * Math.PI / nvar;
      double xy = 2 * (x - 0.5);
      double ra = 0.8 * t1;
      if (css == 1) {
        beta = xy - ra * Math.cos(theta / 3);
      } else {
        beta = xy - ra * Math.sin(theta);
      }
    }

    if (type == 25) {
      double rho = 0.8;
      double phi = Math.PI * t1;
      double theta = 6 * Math.PI * t1 + dim * Math.PI / nvar;
      double xy = 2 * (x - 0.5);
      if (css == 1) {
        beta = xy - rho * Math.sin(phi) * Math.sin(theta);
      } else if (css == 2) {
        beta = xy - rho * Math.sin(phi) * Math.cos(theta);
      } else {
        beta = xy - rho * Math.cos(phi);
      }
    }

    if (type == 26) {
      double theta = 6 * Math.PI * t1 + dim * Math.PI / nvar;
      double ra = 0.3 * t1 * (t1 * Math.cos(4 * theta) + 2);
      double xy = 2 * (x - 0.5);
      if (css == 1) {
        beta = xy - ra * Math.cos(theta);
      } else {
        beta = xy - ra * Math.sin(theta);
      }
    }

    return beta;
  }

  //	control the PS shapes of 3-D instances
  double psfunc3(double x, double t1, double t2, int dim, int type) {
    // type:  the type of curve 
    // css:   the class of index
    double beta;
    beta = 0.0;

    dim++;

    if (type == 31) {
      double xy = 4 * (x - 0.5);
      double rate = 1.0 * dim / nvar;
      beta = xy - 4 * (t1 * t1 * rate + t2 * (1.0 - rate)) + 2;
    }

    if (type == 32) {
      double theta = 2 * Math.PI * t1 + dim * Math.PI / nvar;
      double xy = 4 * (x - 0.5);
      beta = xy - 2 * t2 * Math.sin(theta);
    }

    return beta;
  }

  void objective(List<Double> xVar, List<Double> yObj) {
    // 2-objective case
    if (nobj == 2) {
      if (ltype == 21 || ltype == 22 || ltype == 23 || ltype == 24 || ltype == 26) {
        double g = 0, h = 0, a, b;
        ArrayList<Double> aa = new ArrayList<Double>();
        ArrayList<Double> bb = new ArrayList<Double>();
        for (int n = 1; n < nvar; n++) {
          if (n % 2 == 0) {
            a = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 1);  // linkage
            aa.add(a);
          } else {
            b = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 2);
            bb.add(b);
          }

        }
        g = betaFunction(aa, dtype);
        h = betaFunction(bb, dtype);

        double alpha[] = new double[2];
        alphaFunction(alpha, xVar, 2, ptype);  // shape function
        yObj.set(0, alpha[0] + h);
        yObj.set(1, alpha[1] + g);
        aa.clear();
        bb.clear();
      }

      if (ltype == 25) {
        double g = 0, h = 0, a, b;
        double /*e = 0,*/ c;
        ArrayList<Double> aa = new ArrayList<Double>();
        ArrayList<Double> bb = new ArrayList<Double>();
        for (int n = 1; n < nvar; n++) {
          if (n % 3 == 0) {
            a = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 1);
            aa.add(a);
          } else if (n % 3 == 1) {
            b = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 2);
            bb.add(b);
          } else {
            c = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 3);
            if (n % 2 == 0) {
              aa.add(c);
            } else {
              bb.add(c);
            }
          }
        }
        g = betaFunction(aa, dtype);
        h = betaFunction(bb, dtype);
        double alpha[] = new double[2];
        alphaFunction(alpha, xVar, 2, ptype);
        yObj.set(0, alpha[0] + h);
        yObj.set(1, alpha[1] + g);
        aa.clear();
        bb.clear();
      }
    }

    // 3-objective case
    if (nobj == 3) {
      if (ltype == 31 || ltype == 32) {
        double g = 0, h = 0, e = 0, a;
        ArrayList<Double> aa = new ArrayList<Double>();
        ArrayList<Double> bb = new ArrayList<Double>();
        ArrayList<Double> cc = new ArrayList<Double>();
        for (int n = 2; n < nvar; n++) {
          a = psfunc3(xVar.get(n), xVar.get(0), xVar.get(1), n, ltype);
          if (n % 3 == 0) {
            aa.add(a);
          } else if (n % 3 == 1) {
            bb.add(a);
          } else {
            cc.add(a);
          }
        }

        g = betaFunction(aa, dtype);
        h = betaFunction(bb, dtype);
        e = betaFunction(cc, dtype);

        double alpha[] = new double[3];
        alphaFunction(alpha, xVar, 3, ptype);
        yObj.set(0, alpha[0] + h);
        yObj.set(1, alpha[1] + g);
        yObj.set(2, alpha[2] + e);
        aa.clear();
        bb.clear();
        cc.clear();
      }
    }
  }
}
