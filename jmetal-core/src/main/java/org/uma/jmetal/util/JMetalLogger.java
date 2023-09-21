package org.uma.jmetal.util;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * This class provides a logger.
 * 
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class JMetalLogger implements Serializable {

	public static final Logger logger = Logger.getLogger(JMetalLogger.class
			.getName());
}
