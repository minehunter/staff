package io.vedder.robotics.particlefilter;

public class Utils {
  public static final MersenneTwisterFast random =
      new MersenneTwisterFast(System.currentTimeMillis());

  public static double randomUniform(final double min, final double max) {
    return random.nextDouble(true, true) * (max - min) + min;
  }

  public static double randomN(final double mean, final double stddev) {
    // Uses Box-Muller transform to turn a pair of uniform random
    // numbers into a pair of gaussian random numbers
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

  public static double pSampleGuassian(final double sample, final double mean,
      final double stddev) {
    return 1.0 / Math.sqrt(2 * Math.pow(stddev, 2) * Math.PI)
        * Math.pow(Math.E, -Math.pow(sample - mean, 2) / (2 * Math.pow(stddev, 2)));
  }

  public static double pSampleExponential(final double sample, final double lambda) {
    if (sample > 0) {
      return lambda * Math.pow(Math.E, -lambda * sample);
    } else {
      return 0;
    }
  }

  public static double pSampleUniform(final double sample, final double min, final double max) {
    if (sample >= min && sample <= max) {
      return 1.0 / (max - min);
    } else {
      return 0;
    }
  }
}
