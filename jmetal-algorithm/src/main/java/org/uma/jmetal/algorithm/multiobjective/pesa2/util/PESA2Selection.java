package org.uma.jmetal.algorithm.multiobjective.pesa2.util;

import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.AdaptiveGridArchive;
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

  @Override public S execute(AdaptiveGridArchive<S> archive) {
    int selected;
    int hypercube1 = archive.getGrid().randomOccupiedHypercube();
    int hypercube2 = archive.getGrid().randomOccupiedHypercube();

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
    int base = randomGenerator.nextInt(0, archive.size() - 1);
    int cnt = 0;
    while (cnt < archive.size()){
      S individual = (S) archive.get((base + cnt)% archive.size());
      if (archive.getGrid().location(individual) != selected){
        cnt++;
      } else {
        return individual;
      }
    }
    return (S) archive.get((base + cnt) % archive.size());
  }
}
