package org.uma.jmetal.component.catalogue.ea.selection;
import java.util.List;
import org.uma.jmetal.solution.Solution;

@FunctionalInterface
public interface Selection<S extends Solution<?>> {
  List<S> select(List<S> solutionList) ;
}
