//  LZ09.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.problems.LZ09;

import java.util.Vector;

/**
 * Base class to implement the problems of the LZ09 benchmark, which is
 * defined in:
 * H. Li and Q. Zhang. Multiobjective optimization problems with complicated 
 * pareto sets, MOEA/D and NSGA-II. IEEE Transactions on Evolutionary 
 * Computation, 12(2):284Ð302, April 2009.
 */
public class LZ09 {
	
	public LZ09 (int nvar, int nobj, int ptype, int dtype, int ltype) {
		this.nvar = nvar ;
		this.nobj = nobj ;
		this.ltype = ltype ;
		this.dtype = dtype ;
	  this.ptype = ptype ;
	}

	int nvar  ;
	int nobj  ;
	int ltype ;
  int dtype ;
  int ptype ;
  
	// control the PF shape
	void alphaFunction(double alpha[], Vector<Double> x, int dim, int type) {
		if (dim == 2) {
			if (type == 21) {
				alpha[0] = x.elementAt(0);
				alpha[1] = 1 - Math.sqrt(x.elementAt(0));
			}

			if (type == 22) {
				alpha[0] = x.elementAt(0);
				alpha[1] = 1 - x.elementAt(0) * x.elementAt(0);
			}

			if (type == 23) {
				alpha[0] = x.elementAt(0);
				alpha[1] = 1 - Math.sqrt(alpha[0]) - alpha[0]
				           * Math.sin(10 * alpha[0] * alpha[0] * Math.PI);
			}

			if (type == 24) {
				alpha[0] = x.elementAt(0);
				alpha[1] = 1 - x.elementAt(0) - 0.05 * Math.sin(4 * Math.PI * x.elementAt(0));
			}
		} else {
			if (type == 31) {
				alpha[0] = Math.cos(x.elementAt(0) * Math.PI / 2) * Math.cos(x.elementAt(1) * Math.PI / 2);
				alpha[1] = Math.cos(x.elementAt(0) * Math.PI / 2) * Math.sin(x.elementAt(1) * Math.PI / 2);
				alpha[2] = Math.sin(x.elementAt(0) * Math.PI / 2);
			}

			if (type == 32) {
				alpha[0] = 1 - Math.cos(x.elementAt(0) * Math.PI / 2)
				* Math.cos(x.elementAt(1) * Math.PI / 2);
				alpha[1] = 1 - Math.cos(x.elementAt(0) * Math.PI / 2)
				* Math.sin(x.elementAt(1) * Math.PI / 2);
				alpha[2] = 1 - Math.sin(x.elementAt(0) * Math.PI / 2);
			}

			if (type == 33) {
				alpha[0] = x.elementAt(0);
				alpha[1] = x.elementAt(1);
				alpha[2] = 3
				- (Math.sin(3 * Math.PI * x.elementAt(0)) + Math.sin(3 * Math.PI * x.elementAt(1))) - 2
				* (x.elementAt(0) + x.elementAt(1));
			}

			if (type == 34) {
				alpha[0] = x.elementAt(0) * x.elementAt(1);
				alpha[1] = x.elementAt(0) * (1 - x.elementAt(1));
				alpha[2] = (1 - x.elementAt(0));
			}
		}
	} // alphaFunction

	// control the distance
	double betaFunction(Vector<Double> x, int type) {
		double beta;
		beta = 0;
		int dim = x.size();

		if (dim == 0)
			beta = 0;

		if (type == 1) {
			beta = 0;
			for (int i = 0; i < dim; i++) {
				beta += x.elementAt(i) * x.elementAt(i);
			}
			beta = 2.0 * beta / dim;
		}

		if (type == 2) {
			beta = 0;
			for (int i = 0; i < dim; i++) {
				beta += Math.sqrt(i + 1) * x.elementAt(i) * x.elementAt(i);
			}
			beta = 2.0 * beta / dim;
		}

		if (type == 3) {
			double sum = 0, xx;
			for (int i = 0; i < dim; i++) {
				xx = 2 * x.elementAt(i);
				sum += (xx * xx - Math.cos(4 * Math.PI * xx) + 1);
			}
			beta = 2.0 * sum / dim;
		}

		if (type == 4) {
			double sum = 0, prod = 1, xx;
			for (int i = 0; i < dim; i++) {
				xx = 2 * x.elementAt(i);
				sum += xx * xx;
				prod *= Math.cos(10 * Math.PI * xx / Math.sqrt(i + 1));
			}
			beta = 2.0 * (sum - 2 * prod + 2) / dim;
		}

		return beta;
	} // betaFunction


//	control the PS shape of 2-d instances
	double psfunc2(double x, double t1, int dim, int type, int css) {
		// type:  the type of curve 
		// css:   the class of index
		double beta;
		beta = 0.0;

		dim++;

		if(type==21){
			double xy   = 2*(x - 0.5);
			beta = xy - Math.pow(t1, 0.5*(nvar + 3*dim - 8)/(nvar - 2));
		}	

		if(type==22){
			double theta = 6*Math.PI*t1 + dim*Math.PI/nvar;  
			double xy    = 2*(x - 0.5);
			beta = xy - Math.sin(theta);
		}

		if(type==23){
			double theta = 6*Math.PI*t1 + dim*Math.PI/nvar;
			double ra    = 0.8*t1;
			double xy    = 2*(x - 0.5);
			if(css==1)
				beta = xy - ra*Math.cos(theta);
			else{
				beta = xy - ra*Math.sin(theta);
			}
		}

		if(type==24){
			double theta = 6*Math.PI*t1 + dim*Math.PI/nvar;
			double xy    = 2*(x - 0.5);
			double ra    = 0.8*t1;
			if(css==1)
				beta = xy - ra*Math.cos(theta/3);
			else{
				beta = xy - ra*Math.sin(theta);
			}
		}

		if(type==25){
			double rho   = 0.8;
			double phi   = Math.PI*t1;
			double theta = 6*Math.PI*t1 + dim*Math.PI/nvar;
			double xy    = 2*(x - 0.5);
			if(css==1)
				beta = xy - rho*Math.sin(phi)*Math.sin(theta);
			else if(css==2)
				beta = xy - rho*Math.sin(phi)*Math.cos(theta);
			else
				beta = xy - rho*Math.cos(phi);			
		}

		if(type==26){
			double theta = 6*Math.PI*t1 + dim*Math.PI/nvar;
			double ra    = 0.3*t1*(t1*Math.cos(4*theta) + 2);
			double xy    = 2*(x - 0.5);
			if(css==1)
				beta = xy - ra*Math.cos(theta);
			else{
				beta = xy - ra*Math.sin(theta);
			}
		}

		return beta;
	}


//	control the PS shapes of 3-D instances
	double psfunc3(double x, double t1, double t2, int dim, int type){
		// type:  the type of curve 
		// css:   the class of index
		double beta;
		beta = 0.0 ;

		dim++;

		if(type==31){
			double xy  = 4*(x - 0.5);
			double rate = 1.0*dim/nvar;
			beta = xy - 4*(t1*t1*rate + t2*(1.0-rate)) + 2;
		}

		if(type==32){
			double theta = 2*Math.PI*t1 + dim*Math.PI/nvar;
			double xy    = 4*(x - 0.5);
			beta = xy - 2*t2*Math.sin(theta);	
		}

		return beta;
	}
	

	void objective(Vector<Double> x_var, Vector <Double> y_obj)
	{
		// 2-objective case
		if(nobj==2)
		{
			if(ltype==21||ltype==22||ltype==23||ltype==24||ltype==26)
			{
				double g = 0, h = 0, a, b;
				Vector <Double> aa = new Vector();
				Vector <Double> bb = new Vector();
				for(int n=1;n<nvar;n++)
				{

					if(n%2==0){
						a = psfunc2(x_var.elementAt(n),x_var.elementAt(0),n,ltype,1);  // linkage
						aa.addElement(a);
					}
					else
					{
						b = psfunc2(x_var.elementAt(n),x_var.elementAt(0),n,ltype,2);
						bb.addElement(b);
					}	

				}
				
				g = betaFunction(aa, dtype);
				h = betaFunction(bb, dtype);

				double alpha [] = new double[2] ;
				alphaFunction(alpha,x_var,2,ptype);  // shape function
				y_obj.set(0, alpha[0] + h);
				y_obj.set(1, alpha[1] + g); 
				aa.clear(); 
				bb.clear();
			}
			
			if(ltype==25)
			{
				double g = 0, h = 0, a, b;
				double e = 0, c;
				Vector <Double> aa = new Vector() ;
				Vector <Double> bb = new Vector() ;
				for(int n=1;n<nvar;n++){
					if(n%3==0){
						a = psfunc2(x_var.elementAt(n),x_var.elementAt(0),n,ltype,1); 
						aa.addElement(a);
					}
					else if(n%3==1)
					{
						b = psfunc2(x_var.elementAt(n),x_var.elementAt(0),n,ltype,2);
						bb.addElement(b);
					}	
					else{
						c = psfunc2(x_var.elementAt(n),x_var.elementAt(0),n,ltype,3);
						if(n%2==0)    aa.addElement(c);			
						else          bb.addElement(c);
					}
				}		
				g = betaFunction(aa,dtype);          // distance function
				h = betaFunction(bb,dtype);
				double alpha[] = new double[2];
				alphaFunction(alpha,x_var,2,ptype);  // shape function
				y_obj.set(0, alpha[0] + h);
				y_obj.set(1, alpha[1] + g); 
				aa.clear(); 
				bb.clear();
			}
		}
		

		// 3-objective case
		if(nobj==3)
		{
			if(ltype==31||ltype==32)
			{
				double g = 0, h = 0, e = 0, a;
				Vector <Double> aa  = new Vector() ;
				Vector <Double> bb = new Vector();
				Vector <Double> cc = new Vector();
				for(int n=2;n<nvar;n++)
				{
					a = psfunc3(x_var.elementAt(n),x_var.elementAt(0),x_var.elementAt(1),n,ltype);
					if(n%3==0)	    aa.addElement(a);
					else if(n%3==1)	bb.addElement(a);
					else            cc.addElement(a);
				}

				g = betaFunction(aa,dtype);
				h = betaFunction(bb,dtype);
				e = betaFunction(cc,dtype);

				double alpha[] = new double[3];
				alphaFunction(alpha,x_var,3,ptype);  // shape function
				y_obj.set(0, alpha[0] + h);
				y_obj.set(1 ,alpha[1] + g); 
				y_obj.set(2, alpha[2] + e); 
				aa.clear(); 
				bb.clear();
				cc.clear();
			}
		}
	}
}
