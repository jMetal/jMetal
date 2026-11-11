package org.uma.jmetal.component.catalogue.common.termination.impl;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.uma.jmetal.component.catalogue.common.termination.Termination;

/**
 * Termination condition that is met when the user asks the algorithm to stop, for example with a
 * button of a graphical interface or an endpoint of a web application, which call {@link
 * #requestStop()} from their own thread while the algorithm runs in another one.
 *
 * <p>The algorithm checks the condition once per iteration, so it stops at the end of the
 * iteration in which the request arrives, and its result is the population at that point. The
 * request cannot be undone, so an instance cannot be used in two runs. It does not depend on any
 * user interface technology, and it can be combined with a budget, such as a maximum computing
 * time, with {@link TerminationByAnyOf}.
 */
public class TerminationByUserRequest implements Termination {
  private final AtomicBoolean stopRequested = new AtomicBoolean(false);

  /** Asks the algorithm to stop; it can be called from any thread, and more than once. */
  public void requestStop() {
    stopRequested.set(true);
  }

  /** Returns true if the stop has been requested. */
  public boolean stopRequested() {
    return stopRequested.get();
  }

  @Override
  public boolean isMet(Map<String, Object> algorithmStatusData) {
    return stopRequested.get();
  }
}
