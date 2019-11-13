package org.uma.jmetal.util.comparator;

import org.uma.jmetal.solution.Solution;

import java.io.Serializable;
import java.util.Comparator;

/**
 * created at 10:34 pm, 2019/1/28
 * The comparator of DIR score used in D-NSGA-II
 * @author sunhaoran <nuaa_sunhr@yeah.net>
 */
@SuppressWarnings("serial")
public class DirScoreComparator<S extends Solution<?>> implements Comparator<S>, Serializable {

    @Override
    public int compare(S o1, S o2) {
        double score1 = Double.MAX_VALUE ;
        double score2 = Double.MAX_VALUE ;

        Object scoreObj1 = o1.getAttribute("dir-score");
        Object scoreObj2 = o2.getAttribute("dir-score");

        if(scoreObj1 != null){
            score1 = (double) scoreObj1 ;
        }

        if(scoreObj2 != null){
            score2 = (double) scoreObj2 ;
        }

        return Double.compare(score1, score2);
    }
}
