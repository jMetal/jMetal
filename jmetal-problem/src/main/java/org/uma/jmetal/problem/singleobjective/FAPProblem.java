package org.uma.jmetal.problem.singleobjective;

import org.uma.jmetal.solution.integersolution.IntegerSolution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.problem.integerproblem.impl.AbstractIntegerProblem;

public class FAPProblem extends AbstractIntegerProblem {

    private final int n;
    private final List<FAPEdge> fapEdges;

    public FAPProblem(String instanceFile, int F) throws IOException {
        var problemData = readProblem(instanceFile);
        fapEdges = problemData.getKey();
        this.n = problemData.getValue();
        numberOfObjectives(1);
        name("FAP problem");

        // set variable bounds
        List<Integer> lowerBounds = new ArrayList<>(n);
        List<Integer> upperBounds = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            lowerBounds.add(0);
            upperBounds.add(F - 1);
        }
        variableBounds(lowerBounds, upperBounds);
    }

    @Override
    public IntegerSolution createSolution() {
        return super.createSolution();
    }

    @Override
    public IntegerSolution evaluate(IntegerSolution s) {
        long fitness = 0;
        for (var e : fapEdges) {
            if (Math.abs(s.variables().get(e.i) - s.variables().get(e.j)) <= e.dij) {
                fitness += e.pij;
            }
        }
        s.objectives()[0] = fitness;
        return s;
    }

    private Map.Entry<List<FAPEdge>, Integer> readProblem(String filePath) throws IOException {
        var edges = new ArrayList<FAPEdge>();
        var reader = new BufferedReader(new FileReader(filePath));
        int n = -1;
        String line;
        while ((line = reader.readLine()) != null) {
            var lineArr = line.trim().split("\\s+");
            int i = Integer.parseInt(lineArr[0]);
            int j = Integer.parseInt(lineArr[1]);
            int dij = Integer.parseInt(lineArr[4]);
            int pij = Integer.parseInt(lineArr[5]);
            FAPEdge edge = new FAPEdge(i, j, dij, pij);
            edges.add(edge);

            n = Math.max(n, i);
            n = Math.max(n, j);
        }
        reader.close();
        return Map.entry(edges, n + 1);
    }

    static class FAPEdge {

        private int i;
        private int j;
        private int dij;
        private long pij;

        public FAPEdge(int i, int j, int dij, long pij) {
            this.i = i;
            this.j = j;
            this.dij = dij;
            this.pij = pij;
        }

        public int i() {
            return i;
        }

        public int j() {
            return j;
        }

        public int getDij() {
            return dij;
        }

        public long getPij() {
            return pij;
        }
    }
}
