package org.uma.jmetal.auto.pruebas.solution;

import java.util.List;
import org.uma.jmetal.util.bounds.Bounds;

public interface DoubleSolution2 extends Solution2 {
  List<Bounds<Double>> getBounds() ;

  @Override
  List<Double> variables() ;
}
