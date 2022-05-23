package org.uma.jmetal.algorithm.multiobjective.agemoeaii.util;

import junit.framework.TestCase;
import org.junit.Test;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;

import java.util.ArrayList;
import java.util.List;

public class AGEMOEA2EnvironmentalSelectionTest extends TestCase {

    @Test
    public void testComputeGeometry() {
        List<DefaultDoubleSolution> front = new ArrayList<>();

        for (int i=0; i<3; i++){
            DefaultDoubleSolution s = new DefaultDoubleSolution(3,  new ArrayList<>());
            s.objectives()[i] = 1;
            front.add(s);
        }
        DefaultDoubleSolution s = new DefaultDoubleSolution(3,  new ArrayList<>());
        s.objectives()[0] = 0.8; s.objectives()[1] = 0.1; s.objectives()[2] = 0.1;
        front.add(s);

        AGEMOEA2EnvironmentalSelection es = new AGEMOEA2EnvironmentalSelection(3);
        List<Integer> extremes = new ArrayList<>();
        extremes.add(0); extremes.add(1); extremes.add(2);

        List<double[]> objectiveVectors = new ArrayList<>();
        for(DefaultDoubleSolution solution : front)
            objectiveVectors.add(solution.objectives());

        double p = es.computeGeometry(objectiveVectors, extremes);

        assertEquals(1.0, p);
    }

    @Test
    public void testComputeGeometry_spherical_front() {
        List<DefaultDoubleSolution> front = new ArrayList<>();

        for (int i=0; i<2; i++){
            DefaultDoubleSolution s = new DefaultDoubleSolution(2,  new ArrayList<>());
            s.objectives()[i] = 1;
            front.add(s);
        }
        DefaultDoubleSolution s = new DefaultDoubleSolution(2,  new ArrayList<>());
        s.objectives()[0] = 1.d/Math.sqrt(2); s.objectives()[1] = 1.d/Math.sqrt(2);
        front.add(s);

        AGEMOEA2EnvironmentalSelection es = new AGEMOEA2EnvironmentalSelection( 2);
        List<Integer> extremes = new ArrayList<>();
        extremes.add(0); extremes.add(1);

        List<double[]> objectiveVectors = new ArrayList<>();
        for(DefaultDoubleSolution solution : front)
            objectiveVectors.add(solution.objectives());

        double p = es.computeGeometry(objectiveVectors, extremes);

        assertEquals(2.0, p, 0.01);
    }

    @Test
    public void testPairwiseDistances() {

    }

    public void testProjectPoint() {
        double[] point = new double[] {0.4, 0.4, 0.4};
        AGEMOEA2EnvironmentalSelection es = new AGEMOEA2EnvironmentalSelection( 3);

        double[] projection = es.projectPoint(point, 1);
        assertEquals(projection[0], 1d/3d);
        assertEquals(projection[1], 1d/3d);
        assertEquals(projection[1], 1d/3d);
    }

    public void testProjectPoint2() {
        double[] point = new double[] {0.8, 0.8};
        AGEMOEA2EnvironmentalSelection es = new AGEMOEA2EnvironmentalSelection( 2);

        double[] projection = es.projectPoint(point, 2);
        assertEquals(projection[0], 1d/Math.sqrt(2));
        assertEquals(projection[1], 1d/Math.sqrt(2));
    }

    public void testMidPoint() {
        double[] pointA = new double[] {0, 0, 1};
        double[] pointB = new double[] {1, 0, 0};

        AGEMOEA2EnvironmentalSelection es = new AGEMOEA2EnvironmentalSelection( 3);
        double[] midPoint = es.midPoint(pointA, pointB);
        assertEquals(midPoint[0], 0.5);
        assertEquals(midPoint[1], 0.0);
        assertEquals(midPoint[2], 0.5);
    }
}