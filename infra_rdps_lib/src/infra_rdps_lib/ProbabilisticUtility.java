package infra_rdps_lib;

/**
 * @author nsdc118
 *
 */
public class ProbabilisticUtility {

	/**
	 * Initializing Pseudo Random Number Generator with current time
	 * TODO
	 * Use infra_time_lib
	 */
	public static final MersenneTwisterFast random = new MersenneTwisterFast(System.currentTimeMillis());

	/**
	 * @return double value between [0.0, 1.0)
	 */
	public static double randomDouble() {
		return random.nextDouble(true, false);
	}
	
	/**
	 * @param min
	 * @param max
	 * @return
	 */
	public static double randomUniform(final double min, final double max) {
		return random.nextDouble(true, true) * (max - min) + min;
	}

	/**
	 * Uses Box-Muller transform to turn a pair of uniform random
	 * numbers into a pair of gaussian random numbers
	 * 
	 * @param mean
	 * @param stddev
	 * @return
	 */
	public static double randomGaussian(final double mean, final double stddev) {
		int bleedAmount = random.nextInt(20);
		for (int i = 0; i < bleedAmount; ++i) {
			random.nextInt();
		}
		double u1 = ((double) random.nextInt(Integer.MAX_VALUE)) / ((double) Integer.MAX_VALUE);
		double u2 = ((double) random.nextInt(Integer.MAX_VALUE)) / ((double) Integer.MAX_VALUE);
		double z1 = Math.sqrt(-2.0 * Math.log(u1)) * Math.sin(2.0 * Math.PI * u2);
		double x1 = z1 * stddev + mean;
		return x1;
	}

	/**
	 * @param sample
	 * @param mean
	 * @param stddev
	 * @return
	 */
	public static double likelihoodGaussian(final double sample, final double mean, final double stddev) {
		return 1.0 / Math.sqrt(2 * Math.pow(stddev, 2) * Math.PI)
				* Math.pow(Math.E, -Math.pow(sample - mean, 2) / (2 * Math.pow(stddev, 2)));
	}

	/**
	 * @param sample
	 * @param lambda
	 * @return
	 */
	public static double likelihoodExponential(final double sample, final double lambda) {
		if (sample > 0) {
			return lambda * Math.pow(Math.E, -lambda * sample);
		} else {
			return 0;
		}
	}

	/**
	 * @param sample
	 * @param min
	 * @param max
	 * @return
	 */
	public static double likelihoodUniform(final double sample, final double min, final double max) {
		if (sample >= min && sample <= max) {
			return 1.0 / (max - min);
		} else {
			return 0;
		}
	}
}
