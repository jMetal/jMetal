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

package org.uma.jmetal.algorithm.multiobjective.microfame.util;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.HypervolumeContributionComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.legacy.front.Front;
import org.uma.jmetal.util.legacy.front.impl.ArrayFront;
import org.uma.jmetal.util.legacy.front.util.FrontNormalizer;
import org.uma.jmetal.util.legacy.front.util.FrontUtils;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.solutionattribute.impl.HypervolumeContributionAttribute;

/**
 * This class implements the hypervolume indicator. The code is the a Java version
 * of the original metric implementation by Eckart Zitzler.
 * Reference: E. Zitzler and L. Thiele
 * Multiobjective Evolutionary Algorithms: A Comparative Case Study and the Strength Pareto Approach,
 * IEEE Transactions on Evolutionary Computation, vol. 3, no. 4,
 * pp. 257-271, 1999.

 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
public class WFGHypervolumeV2<S extends Solution<?>> extends Hypervolume<S> {
  private static final double DEFAULT_OFFSET = 100.0 ;
  private double offset = DEFAULT_OFFSET ;

    /**
   * Default constructor
   */
  public WFGHypervolumeV2() {
  }

  /**
   * Constructor
   *
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public WFGHypervolumeV2(String referenceParetoFrontFile) throws FileNotFoundException {
    super(referenceParetoFrontFile) ;
  }

  /**
   * Constructor
   *
   * @param referenceParetoFront
   * @throws FileNotFoundException
   */
  public WFGHypervolumeV2(Front referenceParetoFront) {
    super(referenceParetoFront) ;
  }
  
  public double getOffset() {
    return offset ;
  }

    @Override
    public void setOffset(double offset) {
    this.offset=offset;
    }

    @Override
    public Double evaluate(List<S> paretoFrontApproximation) {
            if (paretoFrontApproximation == null) {
      throw new JMetalException("The pareto front approximation is null") ;
    }

    return hypervolume(new ArrayFront(paretoFrontApproximation), referenceParetoFront);
    }
     static class ComparadorGreater implements Comparator{
       
            @Override
            public int compare(Object o1, Object o2) {
              POINT p = (POINT) o1;
              POINT q = (POINT) o2;
  for (int i = n-1; i >= 0; i--)
    if (BEATS(p.objectives[i],q.objectives[i])) return -1;
    else
    if (BEATS(q.objectives[i],p.objectives[i])) return  1;
  return 0;
            }
    
    }
    static class POINT{
    double []objectives;
    
    public POINT(int tamanio)
    {
        objectives=new double[tamanio];
    }
    }
    
    static class FRONT{
    int nPoints;
    POINT [] points;
    
    public FRONT(double frente[][])
    {
        points=new POINT[frente.length];
        this.nPoints=frente.length;
        for(int x=0;x<frente.length;x++)
        {
            points[x]=new POINT(frente[0].length);
            for(int j=0;j<frente[0].length;j++)
            {
                points[x].objectives[j]=frente[x][j];
            }
        }
    }
    }
    
    
    static int n;
    static FRONT[] fs;    // memory management stuff
    static int safe=0;     // the number of points that don't need sorting
    static int fr=0;

    
     static double CalculateHypervolume(double [][] fronton, int  noPoints,int  noObjectives){

        n=noObjectives;
        safe=0;
        fr=0;
        double volume;
        FRONT frente;
        frente = new FRONT(fronton);
        fs=new FRONT[noObjectives-2]; //maxdepth = objetivos-2
        for(int x=0;x<noObjectives-2;x++)
        fs[x]=new FRONT(fronton); //maxm numero de puntos
        volume = hv(frente);
        return volume;

    }

static boolean BEATS(double x, double y)
{
    if(x>y)
        return true;
    return false;
}

static double WORSE(double x, double y)
{
    return x>y? y:x;
}



static int dominates2way(POINT p, POINT q, int k)
// returns -1 if p dominates q, 1 if q dominates p, 2 if p == q, 0 otherwise 
// k is the highest index inspected 
{
  for (int i = k; i >= 0; i--)
    if (BEATS(p.objectives[i],q.objectives[i])) 
      {for (int j = i - 1; j >= 0; j--) 
         if (BEATS(q.objectives[j],p.objectives[j])) return 0; 
       return -1;}
    else
    if (BEATS(q.objectives[i],p.objectives[i])) 
      {for (int j = i - 1; j >= 0; j--) 
         if (BEATS(p.objectives[j],q.objectives[j])) return 0; 
       return  1;}
  return 2;
}


static boolean dominates1way(POINT p, POINT q, int k)
// returns true if p dominates q or p == q, false otherwise 
// the assumption is that q doesn't dominate p 
// k is the highest index inspected 
{
    return IntStream.iterate(k, i -> i >= 0, i -> i - 1).noneMatch(i -> BEATS(q.objectives[i], p.objectives[i]));
}


static void makeDominatedBit(FRONT ps, int p)
// creates the front ps[0 .. p-1] in fs[fr], with each point bounded by ps[p] and dominated points removed 
{
  int l = 0;
  int u = p - 1;
  for (int i = p - 1; i >= 0; i--)
    if (BEATS(ps.points[p].objectives[n - 1],ps.points[i].objectives[n - 1]))
      {fs[fr].points[u].objectives[n - 1] = ps.points[i].objectives[n - 1]; 
       for (int j = 0; j < n - 1; j++) 
	 fs[fr].points[u].objectives[j] = WORSE(ps.points[p].objectives[j],ps.points[i].objectives[j]); 
       u--;}
    else
      {fs[fr].points[l].objectives[n - 1] = ps.points[p].objectives[n - 1]; 
       for (int j = 0; j < n - 1; j++) 
 	 fs[fr].points[l].objectives[j] = WORSE(ps.points[p].objectives[j],ps.points[i].objectives[j]); 
       l++;}
  POINT t;
  // points below l are all equal in the last objective; points above l are all worse 
  // points below l can dominate each other, and we don't need to compare the last objective 
  // points above l cannot dominate points that start below l, and we don't need to compare the last objective 
  fs[fr].nPoints = 1;
  for (int i = 1; i < l; i++)
     {int j = 0;
      while (j < fs[fr].nPoints)
	switch (dominates2way(fs[fr].points[i], fs[fr].points[j], n-2))
	  {case  0: j++; break;
	   case -1: // AT THIS POINT WE KNOW THAT i CANNOT BE DOMINATED BY ANY OTHER PROMOTED POINT j 
	            // SWAP i INTO j, AND 1-WAY DOM FOR THE REST OF THE js 
	            t = fs[fr].points[j];
		    fs[fr].points[j] = fs[fr].points[i]; 
		    fs[fr].points[i] = t; 
		    while(j < fs[fr].nPoints - 1 && dominates1way(fs[fr].points[j], fs[fr].points[fs[fr].nPoints - 1], n-1))
		      fs[fr].nPoints--;
		    int k = j+1; 
		    while (k < fs[fr].nPoints)
		      if(dominates1way(fs[fr].points[j], fs[fr].points[k], n-2))
			{t = fs[fr].points[k];
			 fs[fr].nPoints--;
			 fs[fr].points[k] = fs[fr].points[fs[fr].nPoints]; 
			 fs[fr].points[fs[fr].nPoints] = t; 
			}
		      else
			k++;
	   default: j = fs[fr].nPoints + 1;
	  }
      if (j == fs[fr].nPoints) 
	{t = fs[fr].points[fs[fr].nPoints]; 
	 fs[fr].points[fs[fr].nPoints] = fs[fr].points[i]; 
	 fs[fr].points[i] = t; 
	 fs[fr].nPoints++;}
     }
  safe = (int)WORSE(l,fs[fr].nPoints);
  for (int i = l; i < p; i++)
     {int j = 0;
      while (j < safe)
	if(dominates1way(fs[fr].points[j], fs[fr].points[i], n-2))
	  j = fs[fr].nPoints + 1;
	else
	  j++;
      while (j < fs[fr].nPoints)
	switch (dominates2way(fs[fr].points[i], fs[fr].points[j], n-1))
	  {case  0: j++; break;
	   case -1: // AT THIS POINT WE KNOW THAT i CANNOT BE DOMINATED BY ANY OTHER PROMOTED POINT j 
	            // SWAP i INTO j, AND 1-WAY DOM FOR THE REST OF THE js 
	            t = fs[fr].points[j];
		    fs[fr].points[j] = fs[fr].points[i]; 
		    fs[fr].points[i] = t; 
		    while(j < fs[fr].nPoints - 1 && dominates1way(fs[fr].points[j], fs[fr].points[fs[fr].nPoints - 1], n-1))
		      fs[fr].nPoints--;
		    int k = j+1; 
		    while (k < fs[fr].nPoints)
		      if(dominates1way(fs[fr].points[j], fs[fr].points[k], n-1))
			{t = fs[fr].points[k];
			 fs[fr].nPoints--;
			 fs[fr].points[k] = fs[fr].points[fs[fr].nPoints]; 
			 fs[fr].points[fs[fr].nPoints] = t; 
			}
		      else
			k++;
	   default: j = fs[fr].nPoints + 1;
	  }
      if (j == fs[fr].nPoints) 
	{t = fs[fr].points[fs[fr].nPoints]; 
	  fs[fr].points[fs[fr].nPoints] = fs[fr].points[i]; 
	  fs[fr].points[i] = t; 
	  fs[fr].nPoints++;}
     }
  fr++;
}


static double hv2(FRONT ps, int k)
// returns the hypervolume of ps[0 .. k-1] in 2D 
// assumes that ps is sorted improving
{
  double volume = ps.points[0].objectives[0] * ps.points[0].objectives[1];
    volume += IntStream.range(1, k).mapToDouble(i -> ps.points[i].objectives[1] *
            (ps.points[i].objectives[0] - ps.points[i - 1].objectives[0])).sum();
  return volume;
}


static double inclhv(POINT p)
// returns the inclusive hypervolume of p
{
  double volume = Arrays.stream(p.objectives, 0, n).reduce(1, (a, b) -> a * b);
    return volume;
}


static double inclhv2(POINT p, POINT q)
// returns the hypervolume of {p, q}
{
  double vp  = 1; double vq  = 1;
  double vpq = 1;
  for (int i = 0; i < n; i++) 
    {
      vp  *= p.objectives[i];
      vq  *= q.objectives[i];
      vpq *= WORSE(p.objectives[i],q.objectives[i]);
    }
  double suma=vp+vq-vpq;
  return suma;
}


static double inclhv3(POINT p, POINT q, POINT r)
// returns the hypervolume of {p, q, r}
{
  double vp   = 1; double vq   = 1; double vr   = 1;
  double vpq  = 1; double vpr  = 1; double vqr  = 1;
  double vpqr = 1;
  for (int i = 0; i < n; i++) 
    {
      vp *= p.objectives[i];
      vq *= q.objectives[i];
      vr *= r.objectives[i];
      if (BEATS(p.objectives[i],q.objectives[i]))
	if (BEATS(q.objectives[i],r.objectives[i]))
	{
	  vpq  *= q.objectives[i];
	  vpr  *= r.objectives[i];
	  vqr  *= r.objectives[i];
	  vpqr *= r.objectives[i];
	}
	else
	{
	  vpq  *= q.objectives[i];
	  vpr  *= WORSE(p.objectives[i],r.objectives[i]);
	  vqr  *= q.objectives[i];
	  vpqr *= q.objectives[i];
	}
      else
	if (BEATS(p.objectives[i],r.objectives[i]))
	{
	  vpq  *= p.objectives[i];
	  vpr  *= r.objectives[i];
	  vqr  *= r.objectives[i];
	  vpqr *= r.objectives[i];
	}
	else
	{
	  vpq  *= p.objectives[i];
	  vpr  *= p.objectives[i];
	  vqr  *= WORSE(q.objectives[i],r.objectives[i]);
	  vpqr *= p.objectives[i];
	}
    }
  return vp + vq + vr - vpq - vpr - vqr + vpqr;
}


static double inclhv4(POINT p, POINT q, POINT r, POINT s)
// returns the hypervolume of {p, q, r, s}
{
  double vp    = 1; double vq   = 1; double vr   = 1; double vs   = 1;
  double vpq   = 1; double vpr  = 1; double vps  = 1; double vqr  = 1; double vqs = 1; double vrs = 1; 
  double vpqr  = 1; double vpqs = 1; double vprs = 1; double vqrs = 1; 
  double vpqrs = 1; 
  for (int i = 0; i < n; i++) 
    {
      vp *= p.objectives[i];
      vq *= q.objectives[i];
      vr *= r.objectives[i];
      vs *= s.objectives[i];
      if (BEATS(p.objectives[i],q.objectives[i]))
	if (BEATS(q.objectives[i],r.objectives[i]))
	  if (BEATS(r.objectives[i],s.objectives[i]))
	    {
	      vpq *= q.objectives[i];
	      vpr *= r.objectives[i];
	      vps *= s.objectives[i];
	      vqr *= r.objectives[i];
	      vqs *= s.objectives[i];
	      vrs *= s.objectives[i];
	      vpqr *= r.objectives[i];
	      vpqs *= s.objectives[i];
	      vprs *= s.objectives[i];
	      vqrs *= s.objectives[i];
	      vpqrs *= s.objectives[i];
	    }
	  else
	    {
	      double z1 = WORSE(q.objectives[i],s.objectives[i]);
	      vpq *= q.objectives[i];
	      vpr *= r.objectives[i];
	      vps *= WORSE(p.objectives[i],s.objectives[i]);
	      vqr *= r.objectives[i];
	      vqs *= z1;
	      vrs *= r.objectives[i];
	      vpqr *= r.objectives[i];
	      vpqs *= z1;
	      vprs *= r.objectives[i];
	      vqrs *= r.objectives[i];
	      vpqrs *= r.objectives[i];
	    }
	else
	  if (BEATS(q.objectives[i],s.objectives[i]))
	    {
	      vpq *= q.objectives[i];
	      vpr *= WORSE(p.objectives[i],r.objectives[i]);
	      vps *= s.objectives[i];
	      vqr *= q.objectives[i];
	      vqs *= s.objectives[i];
	      vrs *= s.objectives[i];
	      vpqr *= q.objectives[i];
	      vpqs *= s.objectives[i];
	      vprs *= s.objectives[i];
	      vqrs *= s.objectives[i];
	      vpqrs *= s.objectives[i];
	    }
	  else
	    {
	      double z1 = WORSE(p.objectives[i],r.objectives[i]);
	      vpq *= q.objectives[i];
	      vpr *= z1;
	      vps *= WORSE(p.objectives[i],s.objectives[i]);
	      vqr *= q.objectives[i];
	      vqs *= q.objectives[i];
	      vrs *= WORSE(r.objectives[i],s.objectives[i]);
	      vpqr *= q.objectives[i];
	      vpqs *= q.objectives[i];
	      vprs *= WORSE(z1,s.objectives[i]);
	      vqrs *= q.objectives[i];
	      vpqrs *= q.objectives[i];
	    }
      else
	if (BEATS(q.objectives[i],r.objectives[i]))
	  if (BEATS(p.objectives[i],s.objectives[i]))
	    {
	      double z1 = WORSE(p.objectives[i],r.objectives[i]);
	      double z2 = WORSE(r.objectives[i],s.objectives[i]);
	      vpq *= p.objectives[i];
	      vpr *= z1;
	      vps *= s.objectives[i];
	      vqr *= r.objectives[i];
	      vqs *= s.objectives[i];
	      vrs *= z2;
	      vpqr *= z1;
	      vpqs *= s.objectives[i];
	      vprs *= z2;
	      vqrs *= z2;
	      vpqrs *= z2;
	    }
	  else
	    {
	      double z1 = WORSE(p.objectives[i],r.objectives[i]);
	      double z2 = WORSE(r.objectives[i],s.objectives[i]);
	      vpq *= p.objectives[i];
	      vpr *= z1;
	      vps *= p.objectives[i];
	      vqr *= r.objectives[i];
	      vqs *= WORSE(q.objectives[i],s.objectives[i]);
	      vrs *= z2;
	      vpqr *= z1;
	      vpqs *= p.objectives[i];
	      vprs *= z1;
	      vqrs *= z2;
	      vpqrs *= z1;
	    }
	else
	  if (BEATS(p.objectives[i],s.objectives[i]))
	    {
	      vpq *= p.objectives[i];
	      vpr *= p.objectives[i];
	      vps *= s.objectives[i];
	      vqr *= q.objectives[i];
	      vqs *= s.objectives[i];
	      vrs *= s.objectives[i];
	      vpqr *= p.objectives[i];
	      vpqs *= s.objectives[i];
	      vprs *= s.objectives[i];
	      vqrs *= s.objectives[i];
	      vpqrs *= s.objectives[i];
	    }
	  else
	    {
	      double z1 = WORSE(q.objectives[i],s.objectives[i]);
	      vpq *= p.objectives[i];
	      vpr *= p.objectives[i];
	      vps *= p.objectives[i];
	      vqr *= q.objectives[i];
	      vqs *= z1;
	      vrs *= WORSE(r.objectives[i],s.objectives[i]);
	      vpqr *= p.objectives[i];
	      vpqs *= p.objectives[i];
	      vprs *= p.objectives[i];
	      vqrs *= z1;
	      vpqrs *= p.objectives[i];
	    }
    }
  return vp + vq + vr + vs - vpq - vpr - vps - vqr - vqs - vrs + vpqr + vpqs + vprs + vqrs - vpqrs;
}


static double exclhv(FRONT ps, int p)
// returns the exclusive hypervolume of ps[p] relative to ps[0 .. p-1] 
{
  makeDominatedBit(ps, p);
  double a=inclhv(ps.points[p]);
  double b=hv(fs[fr-1]);
  double volume = a-b;
  fr--;
  return volume;
}


static double hv(FRONT ps)
// returns the hypervolume of ps[0 ..] 
{
  // process small fronts with the IEA 
  switch (ps.nPoints)
    {case 1: return inclhv (ps.points[0]); 
     case 2: {
         double regreso=inclhv2(ps.points[0], ps.points[1]);
         return regreso;
     } 
     case 3: return inclhv3(ps.points[0], ps.points[1], ps.points[2]); 
     case 4: return inclhv4(ps.points[0], ps.points[1], ps.points[2], ps.points[3]);
     default: break;
    }

  // these points need sorting
 //FROM INDEX INCLUSIVE TO INDEX EXCLUSIVE POR LO TANTO ES CORRECTO
  Arrays.sort(ps.points, 0, ps.nPoints, new ComparadorGreater()); //ASI FUNCIONO EXCELENTE NO MOVER!!! Arrays.sort(ps.points, 0, ps.nPoints , new ComparadorGreater());

  // n = 2 implies that safe = 0 
  if (n == 2) return hv2(ps, ps.nPoints); 
  
  if (n == 3 && safe > 0) 
    {
      double volume = ps.points[0].objectives[2] * hv2(ps, safe); 
      n--;
        // we can ditch dominated points here, but they will be ditched anyway in makeDominatedBit
        volume += IntStream.range(safe, ps.nPoints).mapToDouble(i -> ps.points[i].objectives[n] * exclhv(ps, i)).sum();
      n++; 
      return volume;
    }
  else
    {
      double volume = inclhv4(ps.points[0], ps.points[1], ps.points[2], ps.points[3]);
      n--;
      for (int i = 4; i < ps.nPoints; i++)
      {// we can ditch dominated points here, but they will be ditched anyway in makeDominatedBit
        double a=ps.points[i].objectives[n];
        double b=exclhv(ps, i);
	volume += a*b;
      }
      n++; 
      return volume;
    }
}

  /**
   * Returns the hypervolume value of a front of points
   *
   * @param front        The front
   * @param referenceFront    The true pareto front
   */
  private double hypervolume(Front front, Front referenceFront) {

    Front invertedFront;
    invertedFront = FrontUtils.getInvertedFront(front);

    int numberOfObjectives = referenceFront.getPoint(0).getDimension() ;

    // STEP4. The hypervolume (control is passed to the Java version of Zitzler code)
    return CalculateHypervolume(FrontUtils.convertFrontToArray(invertedFront),
        invertedFront.getNumberOfPoints(), numberOfObjectives);
  }

  @Override public String getDescription() {
    return "PISA implementation of the hypervolume quality indicator" ;
  }



  @Override
  public List<S> computeHypervolumeContribution(List<S> solutionList, List<S> referenceFrontList) {
    if (solutionList.size() > 1) {
      Front front = new ArrayFront(solutionList) ;
      Front referenceFront = new ArrayFront(referenceFrontList) ;

      // STEP 1. Obtain the maximum and minimum values of the Pareto front
      double[] maximumValues = FrontUtils.getMaximumValues(referenceFront) ;
      double[] minimumValues = FrontUtils.getMinimumValues(referenceFront) ;

      // STEP 2. Get the normalized front
      FrontNormalizer frontNormalizer = new FrontNormalizer(minimumValues, maximumValues) ;
      Front normalizedFront = frontNormalizer.normalize(front) ;

      // compute offsets for reference point in normalized space
      double[] offsets = IntStream.range(0, maximumValues.length).mapToDouble(i -> offset / (maximumValues[i] - minimumValues[i])).toArray();
        // STEP 3. Inverse the pareto front. This is needed because the original
      // metric by Zitzler is for maximization problem
      Front invertedFront = FrontUtils.getInvertedFront(normalizedFront);

      // shift away from origin, so that boundary points also get a contribution > 0
      for (int i = 0; i < invertedFront.getNumberOfPoints(); i++) {
        Point point = invertedFront.getPoint(i) ;

        for (int j = 0; j < point.getDimension(); j++) {
          point.setValue(j, point.getValue(j)+ offsets[j]);
        }
      }

      HypervolumeContributionAttribute<S> hvContribution = new HypervolumeContributionAttribute<>() ;

      // calculate contributions and sort
      double[] contributions = hvContributions(FrontUtils.convertFrontToArray(invertedFront));
      for (int i = 0; i < contributions.length; i++) {
        hvContribution.setAttribute(solutionList.get(i), contributions[i]);
      }

      Collections.sort(solutionList, new HypervolumeContributionComparator<S>());

    }
    return solutionList ;
  }




  /**
   * Calculates how much hypervolume each point dominates exclusively. The points
   * have to be transformed beforehand, to accommodate the assumptions of Zitzler's
   * hypervolume code.
   *
   * @param front transformed objective values
   * @return HV contributions
   */
  private double[] hvContributions(double[][] front) {

    int numberOfObjectives = front[0].length ;
    double[] contributions = new double[front.length];
    double[][] frontSubset = new double[front.length - 1][front[0].length];
    LinkedList<double[]> frontCopy = new LinkedList<double[]>();
    Collections.addAll(frontCopy, front);
    double[][] totalFront = frontCopy.toArray(frontSubset);
    double totalVolume =
        CalculateHypervolume(totalFront, totalFront.length, numberOfObjectives);
    for (int i = 0; i < front.length; i++) {
      double[] evaluatedPoint = frontCopy.remove(i);
      frontSubset = frontCopy.toArray(frontSubset);
      // STEP4. The hypervolume (control is passed to java version of Zitzler code)
      double hv = CalculateHypervolume(frontSubset, frontSubset.length, numberOfObjectives);
      double contribution = totalVolume - hv;
      contributions[i] = contribution;
      // put point back
      frontCopy.add(i, evaluatedPoint);
    }
    return contributions;
  }

}
