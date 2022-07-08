package org.uma.jmetal.auto.parameter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParameterTest {
  class FakeParameter extends Parameter<Double> {

    public FakeParameter(String name, String[] args) {
      super(name, args);
    }

    @Override
    public void check() {

    }

    @Override
    public Parameter<Double> parse() {
      return null;
    }
  }


  private Parameter<Double> parameter ;
  private String parameterName = "doubleParameter" ;
  private String parameterValue = "6.1" ;
  @BeforeEach
  void setup() {
    parameter = new FakeParameter(parameterName, new String[]{"--"+parameterName, parameterValue}) ;
  }

  @Test
  void theConstructorSetsProperlyTheNameAndArgsArguments() {
    assertThat(parameter.getName()).isEqualTo(parameterName) ;
    assertThat(parameter.getArgs()).containsExactly("--"+parameterName, parameterValue) ;
  }

  @Test
  void theConstructorCreatesEmptySubParameterStructures() {
    assertThat(parameter.getGlobalParameters()).isEmpty();
    assertThat(parameter.getSpecificParameters()).isEmpty();
    assertThat(parameter.getNonConfigurableParameters()).isEmpty();
  }

  @Test
  void addAGlobalParameterWorksProperly() {
    FakeParameter globalParameter = new FakeParameter("globalParameter", new String[]{"--globalParameter", "-234.5"}) ;
    parameter.addGlobalParameter(globalParameter);

    assertThat(parameter.getGlobalParameters()).hasSize(1) ;
    assertThat(parameter.findGlobalParameter("globalParameter")).isSameAs(globalParameter) ;
  }

  @Test
  void addSpecificParameterWorksProperly() {
    FakeParameter specificParameter = new FakeParameter("specificParameter", new String[]{"--specificParameter", "-234.5"}) ;
    parameter.addSpecificParameter(""+parameterValue, specificParameter);

    assertThat(parameter.getSpecificParameters()).hasSize(1) ;
    assertThat(parameter.findSpecificParameter("specificParameter")).isSameAs(specificParameter) ;
  }

  @Test
  void setANewValueWorksProperly() {
    parameter.setValue(-2345.0);
    assertThat(parameter.getValue()).isEqualTo(-2345.0) ;
  }
}