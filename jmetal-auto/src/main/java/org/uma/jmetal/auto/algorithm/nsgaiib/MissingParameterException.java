package org.uma.jmetal.auto.algorithm.nsgaiib;

@SuppressWarnings("serial")
public class MissingParameterException extends RuntimeException {

	public MissingParameterException(String parameterName) {
		super("Missing parameter " + parameterName);
	}
}
