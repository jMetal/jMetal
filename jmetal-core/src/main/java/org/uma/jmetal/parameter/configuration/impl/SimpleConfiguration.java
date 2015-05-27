package org.uma.jmetal.parameter.configuration.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.uma.jmetal.parameter.Parameter;
import org.uma.jmetal.parameter.Parameterable;
import org.uma.jmetal.parameter.configuration.Configuration;
import org.uma.jmetal.parameter.configuration.ConfigurationUnit;

public class SimpleConfiguration implements Configuration {

	Map<Parameter<?>, ConfigurationUnit<?>> unitMap = new HashMap<Parameter<?>, ConfigurationUnit<?>>();

	public SimpleConfiguration(ConfigurationUnit<?>... units) {
		this(Arrays.asList(units));
	}

	public SimpleConfiguration(Iterable<ConfigurationUnit<?>> units) {
		setAllUnits(units);
	}

	public SimpleConfiguration(Parameterable algorithm) {
		for (Parameter<?> parameter : algorithm.getParameterManager()) {
			storeValue(parameter);
		}
	}

	private <Value> void storeValue(Parameter<Value> parameter) {
		setUnit(new ImmutableConfigurationUnit<Value>(parameter,
				parameter.get()));
	}

	@Override
	public Collection<Parameter<?>> getParameters() {
		return new LinkedList<>(unitMap.keySet());
	}

	public <Value> Value getValue(Parameter<Value> parameter) {
		ConfigurationUnit<Value> unit = getUnit(parameter);
		if (unit == null) {
			return null;
		} else {
			return unit.getValue();
		}
	}

	public <Value> void setValue(Parameter<Value> parameter, Value value) {
		setUnit(new ImmutableConfigurationUnit<Value>(parameter, value));
	}

	public void removeValue(Parameter<?> parameter) {
		removeUnit(parameter);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Value> ConfigurationUnit<Value> getUnit(Parameter<Value> parameter) {
		return (ConfigurationUnit<Value>) unitMap.get(parameter);
	}

	public <Value> void setUnit(ConfigurationUnit<Value> unit) {
		unitMap.put(unit.getParameter(), unit);
	}

	public void removeUnit(Parameter<?> parameter) {
		unitMap.remove(parameter);
	}

	public Map<Parameter<?>, ConfigurationUnit<?>> getAllUnits(
			Iterable<Parameter<?>> parameters) {
		Map<Parameter<?>, ConfigurationUnit<?>> map = new HashMap<>();
		for (Parameter<?> parameter : parameters) {
			map.put(parameter, getUnit(parameter));
		}
		return map;
	}

	public void setAllUnits(Iterable<ConfigurationUnit<?>> units) {
		for (ConfigurationUnit<?> unit : units) {
			setUnit(unit);
		}
	}

	public void removeAllUnits(Iterable<Parameter<?>> parameters) {
		for (Parameter<?> parameter : parameters) {
			removeUnit(parameter);
		}
	}

	public Map<Parameter<?>, Object> getAllValues(
			Iterable<Parameter<?>> parameters) {
		Map<Parameter<?>, Object> map = new HashMap<>();
		for (Parameter<?> parameter : parameters) {
			map.put(parameter, getValue(parameter));
		}
		return map;
	}

	public <Value> void setAllValues(Map<Parameter<Value>, Value> map) {
		for (Entry<Parameter<Value>, Value> entry : map.entrySet()) {
			Parameter<Value> parameter = entry.getKey();
			Value value = entry.getValue();
			setValue(parameter, value);
		}
	}

	public void removeAllValues(Iterable<Parameter<?>> parameters) {
		removeAllUnits(parameters);
	}

	@Override
	public void applyAll() {
		for (ConfigurationUnit<?> unit : this) {
			unit.apply();
		}
	}

	@Override
	public boolean isAllApplied() {
		for (ConfigurationUnit<?> unit : this) {
			if (unit.isApplied()) {
				// applied so far
			} else {
				return false;
			}
		}
		return true;
	}

	@Override
	public Iterator<ConfigurationUnit<?>> iterator() {
		return unitMap.values().iterator();
	}

}
