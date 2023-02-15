package org.uma.jmetal.auto.parameter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParameterTest {
  class FakeParameter extends Parameter<Double> {

    public FakeParameter(String name) {
      super(name);
    }

    @Override
    public void check() {

    }
    @Override
    public Parameter<Double> parse(String[] arguments) {
      return null;
    }
  }

  private Parameter<Double> parameter ;
  private String parameterName = "doubleParameter" ;
  private String parameterValue = "6.1" ;
  @BeforeEach
  void setup() {
    parameter = new FakeParameter(parameterName) ;
  }

  @Test
  void theConstructorSetsProperlyTheNameAndArgsArguments() {
    assertThat(parameter.name()).isEqualTo(parameterName) ;
  }

  @Test
  void theConstructorCreatesEmptySubParameterStructures() {
    assertThat(parameter.globalParameters()).isEmpty();
    assertThat(parameter.specificParameters()).isEmpty();
    assertThat(parameter.nonConfigurableParameters()).isEmpty();
  }

  @Test
  void addAGlobalParameterWorksProperly() {
    FakeParameter globalParameter = new FakeParameter("globalParameter") ;
    parameter.addGlobalParameter(globalParameter);

    assertThat(parameter.globalParameters()).hasSize(1) ;
    assertThat(parameter.findGlobalParameter("globalParameter")).isSameAs(globalParameter) ;
  }

  @Test
  void addSpecificParameterWorksProperly() {
    FakeParameter specificParameter = new FakeParameter("specificParameter") ;
    parameter.addSpecificParameter(""+parameterValue, specificParameter);

    assertThat(parameter.specificParameters()).hasSize(1) ;
    assertThat(parameter.findSpecificParameter("specificParameter")).isSameAs(specificParameter) ;
  }

  @Test
  void setANewValueWorksProperly() {
    parameter.value(-2345.0);
    assertThat(parameter.value()).isEqualTo(-2345.0) ;
  }
}