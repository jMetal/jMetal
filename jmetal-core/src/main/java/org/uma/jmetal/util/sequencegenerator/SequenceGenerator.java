package org.uma.jmetal.util.sequencegenerator;

public interface SequenceGenerator<T> {
    T getValue() ;
    void generateNext() ;
    int getSequenceLength() ;
}
