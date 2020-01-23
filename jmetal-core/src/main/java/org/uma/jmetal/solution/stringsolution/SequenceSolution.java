package org.uma.jmetal.solution.stringsolution;

import org.uma.jmetal.solution.Solution;

public interface SequenceSolution<T> extends Solution<T> {
    int getLength();
}
