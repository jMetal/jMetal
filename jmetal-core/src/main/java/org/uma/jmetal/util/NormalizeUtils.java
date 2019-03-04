package org.uma.jmetal.util;

/**
 * Class responsible for normalizing values
 * 
 * @author Thiago Ferreira
 * @version 1.0.0
 * @since 2018-12-16
 */
public class NormalizeUtils {
	
	private NormalizeUtils() throws InstantiationException {
		throw new InstantiationException("Instances of this type are forbidden");
	}

	/**
	 * It normalizes a {@code value} in a range [{@code a}, {@code b}] given a {@code min} and {@code max} value.
	 * The equation used here were based on the following links:
	 * 
	 * <ul>
	 * <li>{@link https://stats.stackexchange.com/a/281165}</li>
	 * <li>{@link https://stats.stackexchange.com/a/178629}</li>
	 * <li>{@link https://en.wikipedia.org/wiki/Normalization_(statistics)}</li>
	 * <li>{@link https://en.wikipedia.org/wiki/Feature_scaling}</li>
	 * </ul>
	 * 
	 * @param value value number to be normalized
	 * @param minRangeValue the minimum value for the range
	 * @param maxRangeValue the maximum value for the range
	 * @param min minimum value that {@code value} can take on
	 * @param max maximum value that {@code value} can take on
	 * @return the normalized number
	 */
	public static double normalize(double value, double minRangeValue, double maxRangeValue, double min, double max) {
		
		if (max == min) {
			throw new JMetalException("The max minus min should not be zero");
		}
		
		return minRangeValue + (((value - min) * (maxRangeValue - minRangeValue)) / (max - min));
	}

	/**
	 * It normalizes a {@code value} in [0,1] given a {@code min} and {@code max} value.
	 *  
	 * @param value number to be normalized
	 * @param min minimum value that {@code value} can take on
	 * @param max maximum value that {@code value} can take on
	 * @return the normalized number
	 */
	public static double normalize(double value, double min, double max) {
		return normalize(value, 0.0, 1.0, min, max);
	}
}
