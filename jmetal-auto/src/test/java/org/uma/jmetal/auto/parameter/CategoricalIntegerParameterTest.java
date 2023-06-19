package org.uma.jmetal.auto.parameter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class CategoricalIntegerParameterTest {
  @Test
  void constructorWorksProperly() {
    String parameterName = "parameterName" ;
    CategoricalIntegerParameter parameter = new CategoricalIntegerParameter(parameterName, List.of(1, 2)) ;
    assertEquals(parameterName, parameter.name()) ;
    assertEquals(2, parameter.validValues().size());
    assertEquals(1, parameter.validValues().get(0));
    assertEquals(2, parameter.validValues().get(1));
  }

  @Test
  void parseRaisesAnExceptionIfTheValueIsNotInTheValidValuesList() {
    String parameterName = "parameterName" ;
    CategoricalIntegerParameter parameter = new CategoricalIntegerParameter(parameterName, List.of(1, 2)) ;
    String parameterString = "parameterName --5" ;

    assertThrows(RuntimeException.class, () -> parameter.parse(parameterString.split(" "))) ;
   }

  @Test
  void parseRaisesAnExceptionIfTheParameterNameIsNotInTheParameterString() {
    String parameterName = "parameterName" ;
    CategoricalIntegerParameter parameter = new CategoricalIntegerParameter(parameterName, List.of(1, 2)) ;
    String parameterString = "--parameter 2 " ;

    assertThrows(RuntimeException.class, () -> parameter.parse(parameterString.split(" "))) ;
  }

  @Test
  void parseWorksProperlyIfTheParameterNameIsInTheParameterString() {
    String parameterName = "parameterName" ;
    CategoricalIntegerParameter parameter = new CategoricalIntegerParameter(parameterName, List.of(1, 2)) ;
    String parameterString = "--parameterName 2 " ;

    assertEquals(2,  parameter.parse(parameterString.split(" ")).value()); ;
  }
}