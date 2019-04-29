package org.uma.jmetal.util.comparator;

import org.uma.jmetal.solution.Solution;

import java.io.Serializable;
import java.util.Comparator;

/**
 * created at 10:29 pm, 2019/1/28
 * Comparator combining dominance-ranking comparator and DIR-score comparator
 *
 * @see DirScoreComparator
 * @author sunhaoran <nuaa_sunhr@yeah.net>
 */
@SuppressWarnings("serial")
public class RankingAndDirScoreDistanceComparator<S extends Solution<?>> implements Comparator<S>, Serializable {
    private final Comparator<S> rankingComparator = new RankingComparator<>();
    private final Comparator<S> dirScoreComparator = new DirScoreComparator<>() ;


    @Override
    public int compare(S o1, S o2) {
        int result = rankingComparator.compare(o1, o2) ;
        if(result == 0){
            return dirScoreComparator.compare(o1, o2) ;
        }
        return result;
    }
}
