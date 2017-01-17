package org.uma.jmetal.algorithm.multiobjective.nsgaiii.util;


import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.SolutionAttribute;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class EnvironmentalSelection<S extends Solution<?>> implements SelectionOperator<List<S>, List<S>>,
											   SolutionAttribute<S, List<Double>> {

	private List<List<S>> fronts;
	private int solutionsToSelect;
	private List<ReferencePoint<S>> referencePoints;
	private int numberOfObjectives;
	
	public EnvironmentalSelection(Builder<S> builder) {
		fronts = builder.getFronts();
		solutionsToSelect = builder.getSolutionsToSelet();
		referencePoints = builder.getReferencePoints();
		numberOfObjectives = builder.getNumberOfObjectives();
	}
	
	
	public EnvironmentalSelection(List<List<S>> fronts, int solutionsToSelect, List<ReferencePoint<S>> referencePoints, int numberOfObjectives) {
		this.fronts 			= fronts;
		this.solutionsToSelect  = solutionsToSelect;
		this.referencePoints 	= referencePoints;
		this.numberOfObjectives = numberOfObjectives;
	}
	
	public List<Double> translateObjectives(List<S> population) {
	   List<Double> ideal_point;
	   ideal_point = new ArrayList<>(numberOfObjectives);
	   
	   for (int f=0; f<numberOfObjectives; f+=1){
			double minf = Double.MAX_VALUE;
			for (int i=0; i<fronts.get(0).size(); i+=1) // min values must appear in the first front
			{
				minf = Math.min(minf, fronts.get(0).get(i).getObjective(f));
			}
			ideal_point.add(minf);

			for (List<S> list : fronts) 
			{
				for (S s : list)
				{
					if (f==0) // in the first objective we create the vector of conv_objs
						setAttribute(s, new ArrayList<Double>());
					
					getAttribute(s).add(s.getObjective(f)-minf);
					
				}
			}
		}
	   
	   return ideal_point;
	}

	
	// ----------------------------------------------------------------------
	// ASF: Achivement Scalarization Function
	// I implement here a effcient version of it, which only receives the index
	// of the objective which uses 1.0; the rest will use 0.00001. This is 
	// different to the one impelemented in C++
	// ----------------------------------------------------------------------
	private double ASF(S s, int index) {
		double max_ratio = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < s.getNumberOfObjectives(); i++) {
			double weight = (index == i) ? 1.0 : 0.000001;
			max_ratio = Math.max(max_ratio, s.getObjective(i)/weight);
		}
		return max_ratio;
	}
	
	// ----------------------------------------------------------------------
	private List<S> findExtremePoints(List<S> population) {
		List<S> extremePoints = new ArrayList<>();
		S min_indv = null;
		for (int f=0; f < numberOfObjectives; f+=1)
		{
			double min_ASF = Double.MAX_VALUE;	
			for (S s : fronts.get(0)) { // only consider the individuals in the first front
				double asf = ASF(s, f);
				if ( asf < min_ASF ) {
					min_ASF = asf;
					min_indv = s;
				}
			}
			
			extremePoints.add(min_indv);
		}
		return extremePoints;
	}
	
	public List<Double> guassianElimination(List<List<Double>> A, List<Double> b) {
		List<Double> x = new ArrayList<>();

	    int N = A.size();
	    for (int i=0; i<N; i+=1)
	    {
	    	A.get(i).add(b.get(i));
	    }

	    for (int base=0; base<N-1; base+=1)
	    {
	        for (int target=base+1; target<N; target+=1)
	        {
	            double ratio = A.get(target).get(base)/A.get(base).get(base);
	            for (int term=0; term<A.get(base).size(); term+=1)
	            {
	                A.get(target).set(term, A.get(target).get(term) - A.get(base).get(term)*ratio);
	            }
	        }
	    }

	    for (int i = 0; i < N; i++)
	    	x.add(0.0);
	    
	    for (int i=N-1; i>=0; i-=1)
	    {
	        for (int known=i+1; known<N; known+=1)
	        {
	            A.get(i).set(N, A.get(i).get(N) - A.get(i).get(known)*x.get(known));
	        }
	        x.set(i, A.get(i).get(N)/A.get(i).get(i));
	    }
		return x;
	}
	
	public List<Double> constructHyperplane(List<S> population, List<S> extreme_points) {
		// Check whether there are duplicate extreme points.
		// This might happen but the original paper does not mention how to deal with it.
		boolean duplicate = false;
		for (int i=0; !duplicate && i< extreme_points.size(); i+=1)
		{
			for (int j=i+1; !duplicate && j<extreme_points.size(); j+=1)
			{
				duplicate = extreme_points.get(i).equals(extreme_points.get(j));
			}
		}

		List<Double> intercepts = new ArrayList<>();
		
		if (duplicate) // cannot construct the unique hyperplane (this is a casual method to deal with the condition)
		{
			for (int f=0; f<numberOfObjectives; f+=1)
			{
				// extreme_points[f] stands for the individual with the largest value of objective f
				intercepts.add(extreme_points.get(f).getObjective(f));
			}
		}
		else
		{
			// Find the equation of the hyperplane
			List<Double> b = new ArrayList<>(); //(pop[0].objs().size(), 1.0);
			for (int i =0; i < numberOfObjectives;i++)
				b.add(1.0);
			
			List<List<Double>> A=new ArrayList<>();
			for (S s : extreme_points)
			{
				List<Double> aux = new ArrayList<>();
				for (int i = 0; i < numberOfObjectives; i++)
					aux.add(s.getObjective(i));
				A.add(aux);
			}
			List<Double> x = guassianElimination(A, b);
		
			// Find intercepts
			for (int f=0; f<numberOfObjectives; f+=1)
			{
				intercepts.add(1.0/x.get(f));
				
			}
		}
		return intercepts;
	}
	
	public void normalizeObjectives(List<S> population, List<Double> intercepts, List<Double> ideal_point) {
		for (int t=0; t<fronts.size(); t+=1)
		{
			for (S s : fronts.get(t)) {
			
				for (int f = 0; f < numberOfObjectives; f++) {
					List<Double> conv_obj = (List<Double>)getAttribute(s);
					if (Math.abs(intercepts.get(f)-ideal_point.get(f))> 10e-10)
					{
						conv_obj.set(f,conv_obj.get(f) / (intercepts.get(f)-ideal_point.get(f)));
					}
					else
					{
						conv_obj.set(f,conv_obj.get(f) / (10e-10));
					}
						
				}
			}
		}
	}
	
	public double perpendicularDistance(List<Double> direction, List<Double> point) {
	    double numerator = 0, denominator = 0;
	    for (int i=0; i<direction.size(); i+=1)
	    {
	        numerator += direction.get(i)*point.get(i);
	        denominator += Math.pow(direction.get(i),2.0);
	    }
	    double k = numerator/denominator;

	    double d = 0;
	    for (int i=0; i<direction.size(); i+=1)
	    {
	        d += Math.pow(k*direction.get(i) - point.get(i),2.0);
	    }
	    return Math.sqrt(d);
	}
	
	
	public void associate(List<S> population) {


		for (int t = 0; t < fronts.size(); t++) {
			for (S s : fronts.get(t)) {
				int min_rp = -1;
				double min_dist = Double.MAX_VALUE;
				for (int r = 0; r < this.referencePoints.size(); r++) {
					double d = perpendicularDistance(this.referencePoints.get(r).position,
										             (List<Double>)getAttribute(s));
					if (d < min_dist) {
						min_dist=d;
						min_rp = r; 
					}
				}
				if (t+1 != fronts.size()) {
					this.referencePoints.get(min_rp).AddMember();
				} else {
					this.referencePoints.get(min_rp).AddPotentialMember(s, min_dist);
				}
			}
		}
		
	}
	
	int FindNicheReferencePoint()
	{
		// find the minimal cluster size
		int min_size = Integer.MAX_VALUE;
		for (ReferencePoint<S> referencePoint : this.referencePoints)
			min_size = Math.min(min_size,referencePoint.MemberSize());
		
		// find the reference points with the minimal cluster size Jmin
		List<Integer> min_rps=new ArrayList<>();
		
		
		for (int r=0; r<this.referencePoints.size(); r+=1)
		{
			if (this.referencePoints.get(r).MemberSize() == min_size)
			{
				min_rps.add(r);
			}
		}
		// return a random reference point (j-bar)
		return min_rps.get(min_rps.size() > 1 ? JMetalRandom.getInstance().nextInt(0, min_rps.size()-1):0);
	}
	
	// ----------------------------------------------------------------------
	// SelectClusterMember():
	//
	// Select a potential member (an individual in the front Fl) and associate
	// it with the reference point.
	//
	// Check the last two paragraphs in Section IV-E in the original paper.
	// ----------------------------------------------------------------------
	S SelectClusterMember(ReferencePoint<S> rp)
	{
		S chosen = null;
		if (rp.HasPotentialMember())
		{
			if (rp.MemberSize() == 0) // currently has no member
			{
				chosen =  rp.FindClosestMember();
			}
			else
			{
				chosen =  rp.RandomMember();
			}
		}
		return chosen;
	}
	
	@Override
	/* This method performs the environmental Selection indicated in the paper describing NSGAIII*/
	public List<S> execute(List<S> source) throws JMetalException {
		// The comments show the C++ code

		// ---------- Steps 9-10 in Algorithm 1 ----------
		if (source.size() == this.solutionsToSelect) return source;
		

		// ---------- Step 14 / Algorithm 2 ----------
		//vector<double> ideal_point = TranslateObjectives(&cur, fronts);
		List<Double>   ideal_point    = translateObjectives(source);
		List<S> extreme_points = 		findExtremePoints(source);
		List<Double>   intercepts     = constructHyperplane(source, extreme_points);
		
 	    normalizeObjectives(source, intercepts, ideal_point);
		// ---------- Step 15 / Algorithm 3, Step 16 ----------
		associate(source);

		// ---------- Step 17 / Algorithm 4 ----------
		while (source.size() < this.solutionsToSelect)
		{
			int min_rp = FindNicheReferencePoint();

			S chosen = SelectClusterMember(this.referencePoints.get(min_rp));
			if (chosen == null) // no potential member in Fl, disregard this reference point
			{
				this.referencePoints.remove(min_rp); 
			}
			else
			{
				this.referencePoints.get(min_rp).AddMember();
				this.referencePoints.get(min_rp).RemovePotentialMember(chosen);
				source.add(chosen);
			}
		}
		
		return source;
	}
	
	public static class Builder<S extends Solution<?>> {
		private List<List<S>> fronts;
		private int solutionsToSelect;
		private List<ReferencePoint<S>> referencePoints;
		private int numberOfObjctives;
		
		// the default constructor is generated by default
		
		public Builder<S> setSolutionsToSelect(int solutions) {
			solutionsToSelect = solutions;
			return this;
		}
		public Builder<S> setFronts(List<List<S>> f){
			fronts = f;
			return this;
		}
		
		public int getSolutionsToSelet() {
			return this.solutionsToSelect;
		}
		
		public List<List<S>> getFronts() {
			return this.fronts;
		}
		
		public EnvironmentalSelection<S> build() {
			return new EnvironmentalSelection<>(this);
		}
		
		public List<ReferencePoint<S>> getReferencePoints() {
			return referencePoints;
		}
		
		public Builder<S> setReferencePoints(List<ReferencePoint<S>> referencePoints){
			this.referencePoints = referencePoints;
			return this;
		}
		
		public Builder<S> setNumberOfObjectives(int n) {
			this.numberOfObjctives = n;
			return this;
		}
		
		public int getNumberOfObjectives() {
			return this.numberOfObjctives;
		}
	}

	  @Override
	  public void setAttribute(S solution, List<Double> value) {
	    solution.setAttribute(getAttributeIdentifier(), value);
	  }

	  @Override
	  @SuppressWarnings("unchecked")
	  public List<Double> getAttribute(S solution) {
	    return (List<Double>) solution.getAttribute(getAttributeIdentifier());
	  }

	  @Override
	  public Object getAttributeIdentifier() {
	    return this.getClass();
	  }

}
