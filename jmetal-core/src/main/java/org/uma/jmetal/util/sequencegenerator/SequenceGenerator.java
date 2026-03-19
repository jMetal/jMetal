package org.uma.jmetal.util.sequencegenerator;

/**
 * Interface representing a generator of sequences of elements of type T.
 * 
 * <p>Implementations of this interface provide a way to generate sequences of values
 * that can be iterated through using the {@link #generateNext()} and {@link #getValue()} methods.
 * The sequence can be of any length, as reported by {@link #getSequenceLength()}.
 * 
 * <p>Typical usage:
 * <pre>
 * {@code
 * SequenceGenerator<Integer> generator = ...;
 * for (int i = 0; i < generator.getSequenceLength(); i++) {
 *     int value = generator.getValue();
 *     // Use the value...
 *     generator.generateNext();
 * }
 * }
 * </pre>
 * 
 * @param <T> the type of elements in the sequence
 */
public interface SequenceGenerator<T> {
    
    /**
     * Returns the current value in the sequence.
     * 
     * @return the current value in the sequence
     */
    T getValue();
    
    /**
     * Advances the sequence to the next value.
     * The behavior when called after reaching the end of the sequence is implementation-dependent.
     * Some implementations may cycle back to the beginning, while others may generate new sequences.
     */
    void generateNext();
    
    /**
     * Returns the length of the sequence before it repeats or resets.
     * 
     * @return the number of unique values in the sequence before it repeats
     */
    int getSequenceLength();
}
