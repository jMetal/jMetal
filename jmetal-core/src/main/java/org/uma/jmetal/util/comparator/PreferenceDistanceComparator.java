package org.uma.jmetal.util.comparator;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;
import org.uma.jmetal.util.point.util.distance.EuclideanDistance;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class PreferenceDistanceComparator<S extends Solution<?>> implements Comparator<S>, Serializable {
    private ConstraintViolationComparator<S> constraintViolationComparator;
    private Point referencePoint;


    public PreferenceDistanceComparator(Point referencePoint ){
        constraintViolationComparator =new OverallConstraintViolationComparator<S>();
        this.referencePoint = referencePoint;
    }


    @Override
    public int compare(S solution1, S solution2) {
        if (solution1 == null) {
            throw new JMetalException("Solution1 is null") ;
        } else if (solution2 == null) {
            throw new JMetalException("Solution2 is null") ;
        } else if (solution1.getNumberOfObjectives() != solution2.getNumberOfObjectives()) {
            throw new JMetalException("Cannot compare because solution1 has " +
                    solution1.getNumberOfObjectives()+ " objectives and solution2 has " +
                    solution2.getNumberOfObjectives()) ;
        }
        int result ;
        result = constraintViolationComparator.compare(solution1, solution2) ;
        if (result == 0) {
            result = dominanceTest(solution1, solution2) ;
        }

        return result ;
    }

    private int dominanceTest(Solution<?> solution1, Solution<?> solution2) {
        int result=0;
        EuclideanDistance euclideanDistance = new EuclideanDistance();
        Point p1= getPointFromSolution(solution1);
        Point p2 = getPointFromSolution(solution2);
        double distance1 = Math.abs(euclideanDistance.compute(p1,referencePoint));
        double distance2 = Math.abs(euclideanDistance.compute(p2,referencePoint));
        if(distance1<distance2){
            result = -1;
        }else if(distance1>distance2){
            result = 1;
        }
        return result ;
    }

    private Point getPointFromSolution(Solution solution){
        Point result = null;
        if(solution!=null){
            result = new ArrayPoint(solution.getNumberOfObjectives());
            for (int i = 0; i < solution.getNumberOfObjectives() ; i++) {
                result.setDimensionValue(i,solution.getObjective(i));
            }
        }
        return result;
    }
}
