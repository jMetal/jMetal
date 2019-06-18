package org.uma.jmetal.auto.old.nsgaiib;

@SuppressWarnings("serial")
public class MissingParameterException extends RuntimeException {

	public MissingParameterException(String parameterName) {
		super("Missing parameter " + parameterName);
	}
}
