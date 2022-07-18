package org.uma.jmetal.algorithm.multiobjective.pesa2.util;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * This class implements a selection operator as the used in the PESA-II algorithm
 */
@SuppressWarnings("serial")
public class PESA2Selection<S extends Solution<?>> implements SelectionOperator<AdaptiveGridArchive<S>, S> {

  private JMetalRandom randomGenerator ;

  public PESA2Selection() {
    randomGenerator = JMetalRandom.getInstance() ;
	}

  @Override public S execute(@NotNull AdaptiveGridArchive<S> archive) {
    int selected;
    var hypercube1 = archive.getGrid().randomOccupiedHypercube();
    var hypercube2 = archive.getGrid().randomOccupiedHypercube();

    if (hypercube1 != hypercube2){
      if (archive.getGrid().getLocationDensity(hypercube1) <
          archive.getGrid().getLocationDensity(hypercube2)) {

        selected = hypercube1;

      } else if (archive.getGrid().getLocationDensity(hypercube2) <
          archive.getGrid().getLocationDensity(hypercube1)) {

        selected = hypercube2;
      } else {
        if (randomGenerator.nextDouble() < 0.5) {
          selected = hypercube2;
        } else {
          selected = hypercube1;
        }
      }
    } else {
      selected = hypercube1;
    }
    var base = randomGenerator.nextInt(0, archive.size() - 1);
    var cnt = 0;
    while (cnt < archive.size()){
      var individual = (S) archive.get((base + cnt)% archive.size());
      if (archive.getGrid().location(individual) != selected){
        cnt++;
      } else {
        return individual;
      }
    }
    return (S) archive.get((base + cnt) % archive.size());
  }
}
