package org.uma.jmetal.util.comparator;

import java.io.Serializable;
import java.util.Comparator;

public interface ConstraintViolationComparator<S> extends Comparator<S>, Serializable {
  int compare(S solution1, S solution2);
}
