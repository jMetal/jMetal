package org.uma.jmetal.algorithm.multiobjective.agemoea.util;

import junit.framework.TestCase;
import org.junit.Test;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

import java.util.ArrayList;
import java.util.List;

public class AGEMOEAEnvironmentalSelectionTest extends TestCase {

    @Test
    public void testFindExtreme() {
        List<DefaultDoubleSolution> front = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            DefaultDoubleSolution s = new DefaultDoubleSolution(3, new ArrayList<>());
            s.objectives()[i] = 1;
            front.add(s);
        }
        DefaultDoubleSolution s = new DefaultDoubleSolution(3, new ArrayList<>());
        s.objectives()[0] = 1d / 3d;
        s.objectives()[1] = 1d / 3d;
        s.objectives()[2] = 1d / 3d;
        front.add(s);

        AGEMOEAEnvironmentalSelection es = new AGEMOEAEnvironmentalSelection(3);
        List<DefaultDoubleSolution> extremes = es.findExtremes(front);

        assertEquals(3, extremes.size());
        assertEquals(extremes.get(0), front.get(0));
        assertEquals(extremes.get(1), front.get(1));
        assertEquals(extremes.get(2), front.get(2));

    }

    @Test
    public void testMinkowskiDistance() {
        AGEMOEAEnvironmentalSelection es = new AGEMOEAEnvironmentalSelection(2);
        double value = es.minkowskiDistance(new double[]{0.5, 0.5}, new double[]{0, 0}, 2);
        assertEquals(Math.sqrt(2) / 2, value, 0.00001);

        value = es.minkowskiDistance(new double[]{0.5, 0.5}, new double[]{0, 0}, 1);
        assertEquals(1.0, value, 0.00001);

        value = es.minkowskiDistance(new double[]{0.5, 0.5}, new double[]{0, 0}, 4);
        assertEquals(Math.pow(Math.pow(0.5, 4) + Math.pow(0.5, 4), 1d / 4d), value, 0.00001);
    }

    @Test
    public void testComputeGeometry_flat_front() {
        List<DefaultDoubleSolution> front = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            DefaultDoubleSolution s = new DefaultDoubleSolution(3, new ArrayList<>());
            s.objectives()[i] = 1;
            front.add(s);
        }
        DefaultDoubleSolution s = new DefaultDoubleSolution(3, new ArrayList<>());
        s.objectives()[0] = 1d / 3d;
        s.objectives()[1] = 1d / 3d;
        s.objectives()[2] = 1d / 3d;
        front.add(s);

        AGEMOEAEnvironmentalSelection es = new AGEMOEAEnvironmentalSelection(3);
        List<Integer> extremes = new ArrayList<>();
        extremes.add(0);
        extremes.add(1);
        extremes.add(2);

        List<double[]> objectiveVectors = new ArrayList<>();
        for (DefaultDoubleSolution solution : front) {
            objectiveVectors.add(solution.objectives());
        }

        double p = es.computeGeometry(objectiveVectors, extremes);

        assertEquals(1.0, p);
    }

    @Test
    public void testComputeGeometry_spherical_front() {
        List<DefaultDoubleSolution> front = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            DefaultDoubleSolution s = new DefaultDoubleSolution(2, new ArrayList<>());
            s.objectives()[i] = 1;
            front.add(s);
        }
        DefaultDoubleSolution s = new DefaultDoubleSolution(2, new ArrayList<>());
        s.objectives()[0] = 1.d / Math.sqrt(2);
        s.objectives()[1] = 1.d / Math.sqrt(2);
        front.add(s);

        AGEMOEAEnvironmentalSelection es = new AGEMOEAEnvironmentalSelection(2);
        List<Integer> extremes = new ArrayList<>();
        extremes.add(0);
        extremes.add(1);

        List<double[]> objectiveVectors = new ArrayList<>();
        for (DefaultDoubleSolution solution : front) {
            objectiveVectors.add(solution.objectives());
        }

        double p = es.computeGeometry(objectiveVectors, extremes);

        assertEquals(2.0, p, 0.01);
    }

    @Test
    public void testComputeSurvivalScore() {
        List<DefaultDoubleSolution> front = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            DefaultDoubleSolution s = new DefaultDoubleSolution(3, new ArrayList<>());
            s.objectives()[i] = 1;
            front.add(s);
        }
        DefaultDoubleSolution s = new DefaultDoubleSolution(3, new ArrayList<>());
        s.objectives()[0] = 1d / 3d;
        s.objectives()[1] = 1d / 3d;
        s.objectives()[2] = 1d / 3d;
        front.add(s);

        AGEMOEAEnvironmentalSelection es = new AGEMOEAEnvironmentalSelection(3);
        es.computeSurvivalScore(front);

        assertTrue(Double.isInfinite(
                (Double) front.get(0).attributes().get(AGEMOEAEnvironmentalSelection.getAttributeId())));
        assertTrue(Double.isInfinite(
                (Double) front.get(1).attributes().get(AGEMOEAEnvironmentalSelection.getAttributeId())));
        assertTrue(Double.isInfinite(
                (Double) front.get(2).attributes().get(AGEMOEAEnvironmentalSelection.getAttributeId())));
        assertEquals(2 + 2d / 3d,
                (Double) front.get(3).attributes().get(AGEMOEAEnvironmentalSelection.getAttributeId()));
    }

    @Test
    public void testSurvivalScore() {
        List<DefaultDoubleSolution> front = new ArrayList<>();

        for (int i = 0; i <= 6; i++) {
            DefaultDoubleSolution s = new DefaultDoubleSolution(2, new ArrayList<>());
            s.objectives()[0] = 1.0 - i / 6.0;
            s.objectives()[1] = i / 6.0;
            front.add(s);
        }

        Ranking<DefaultDoubleSolution> ranking = new FastNonDominatedSortRanking<>();
        ranking.compute(front);

        AGEMOEAEnvironmentalSelection es = new AGEMOEAEnvironmentalSelection(2);
        List<DefaultDoubleSolution> selected = es.selectFromFronts(ranking, 4);

        assertEquals(4, selected.size());
        assertEquals(front.get(0).objectives(), selected.get(0).objectives());
        assertEquals(front.get(6).objectives(), selected.get(1).objectives());
        assertEquals(front.get(5).objectives(), selected.get(2).objectives());
        assertEquals(front.get(3).objectives(), selected.get(3).objectives());

    }

    @Test
    public void testPoint2LineDistance() {
        double[] P = new double[]{0.5, 0, 0};
        double[] A = new double[3];
        double[] B = new double[]{1., 0, 0};

        AGEMOEAEnvironmentalSelection es = new AGEMOEAEnvironmentalSelection(2);
        assertEquals(0.0, es.point2LineDistance(P, A, B));
    }
}