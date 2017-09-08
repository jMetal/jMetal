package org.uma.jmetal.algorithm.multiobjective.rnsgaii.util;

import org.uma.jmetal.algorithm.multiobjective.mombi.util.AbstractUtilityFunctionsSet;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.util.ReferencePoint;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.PreferenceDistanceComparator;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.*;

public class RNSGAIIRanking <S extends Solution<?>> extends GenericSolutionAttribute<S, Integer>
        implements Ranking<S> {

    protected List<Point> referencePoints;
    private static final Comparator<Solution<?>> DOMINANCE_COMPARATOR = new DominanceComparator<Solution<?>>();
    private static final Comparator<Solution<?>> CONSTRAINT_VIOLATION_COMPARATOR =
            new OverallConstraintViolationComparator<Solution<?>>();

    private List<ArrayList<S>> rankedSubPopulations;

    public RNSGAIIRanking(List<Point> referencePoints){
        this.referencePoints = referencePoints;
    }

    @Override
    public Ranking<S> computeRanking(List<S> solutionSet) {
        List<S> population = solutionSet;

        // dominateMe[i] contains the number of solutions dominating i
        int[] dominateMe = new int[population.size()];

        // iDominate[k] contains the list of solutions dominated by k
        List<List<Integer>> iDominate = new ArrayList<>(population.size());

        // front[i] contains the list of individuals belonging to the front i
        ArrayList<List<Integer>> front = new ArrayList<>(population.size() + 1);

        // Initialize the fronts
        for (int i = 0; i < population.size() + 1; i++) {
            front.add(new LinkedList<Integer>());
        }

        // Fast non dominated sorting algorithm
        // Contribution of Guillaume Jacquenot
        for (int p = 0; p < population.size(); p++) {
            // Initialize the list of individuals that i dominate and the number
            // of individuals that dominate me
            iDominate.add(new LinkedList<Integer>());
            dominateMe[p] = 0;
        }

        int flagDominate;
        for (int p = 0; p < (population.size() - 1); p++) {
            // For all q individuals , calculate if p dominates q or vice versa
            for (int q = p + 1; q < population.size(); q++) {
                flagDominate =
                        CONSTRAINT_VIOLATION_COMPARATOR.compare(solutionSet.get(p), solutionSet.get(q));
                if (flagDominate == 0) {
                    flagDominate = DOMINANCE_COMPARATOR.compare(solutionSet.get(p), solutionSet.get(q));
                }
                if (flagDominate == -1) {
                    iDominate.get(p).add(q);
                    dominateMe[q]++;
                } else if (flagDominate == 1) {
                    iDominate.get(q).add(p);
                    dominateMe[p]++;
                }
            }
        }

        for (int i = 0; i < population.size(); i++) {
            if (dominateMe[i] == 0) {
                front.get(0).add(i);
                solutionSet.get(i).setAttribute(getAttributeIdentifier(), 0);
            }
        }

        //Obtain the rest of fronts
        int i = 0;
        Iterator<Integer> it1, it2; // Iterators
        while (front.get(i).size() != 0) {
            i++;
            it1 = front.get(i - 1).iterator();
            while (it1.hasNext()) {
                it2 = iDominate.get(it1.next()).iterator();
                while (it2.hasNext()) {
                    int index = it2.next();
                    dominateMe[index]--;
                    if (dominateMe[index] == 0) {
                        front.get(i).add(index);
                        //RankingAndCrowdingAttr.getAttributes(solutionSet.get(index)).setRank(i);
                        solutionSet.get(index).setAttribute(getAttributeIdentifier(), i);
                    }
                }
            }
        }
        for (Point reference:referencePoints) {
            Collections.sort(solutionSet,new PreferenceDistanceComparator(reference));
        }
        rankedSubPopulations = new ArrayList<>();
        //0,1,2,....,i-1 are fronts, then i fronts
        for (int j = 0; j < i; j++) {
            rankedSubPopulations.add(j, new ArrayList<S>(front.get(j).size()));
            it1 = front.get(j).iterator();
            while (it1.hasNext()) {
                rankedSubPopulations.get(j).add(solutionSet.get(it1.next()));
            }
        }

        return this;
    }

    @Override
    public List<S> getSubfront(int rank) {
        if (rank >= rankedSubPopulations.size()) {
            throw new JMetalException("Invalid rank: " + rank + ". Max rank = " + (rankedSubPopulations.size() -1)) ;
        }
        return rankedSubPopulations.get(rank);
    }

    @Override
    public int getNumberOfSubfronts() {
        return rankedSubPopulations.size();
    }
}
