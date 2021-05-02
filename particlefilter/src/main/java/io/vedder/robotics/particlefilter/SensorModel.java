package io.vedder.robotics.particlefilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.vedder.robotics.particlefilter.messages.SensorMessage;

public class SensorModel {
	private final double SENSOR_SIGMA;
	private final double LAMBDA_SIGMA;
	private final double RAYMAX;

	public SensorModel() {
		SENSOR_SIGMA = 0.01;
		LAMBDA_SIGMA = 0.01;
		RAYMAX = 4;
	}

	private double getProbability(final double mapReading, final double sensorReading) {
		return Utils.pSampleExponential(sensorReading, LAMBDA_SIGMA)
				+ Utils.pSampleGuassian(sensorReading, mapReading, SENSOR_SIGMA) * 4
				+ Utils.pSampleUniform(sensorReading, 0, RAYMAX) * 0.1
				+ Utils.pSampleUniform(sensorReading, RAYMAX - 0.01, RAYMAX) * 0.02;
	}

	class MyPair<A, B> {
		A first;
		B second;

		public MyPair(A first, B second) {
			this.first = first;
			this.second = second;
		}

	}

	public double sampleSensor(final SensorMessage sensorMessage, final Particle belief, final Map map,
			final int nthSample) {

		double probabilitySum = 0;

		// System.out.println("=======Beginning ray scan=======");

		List<MyPair<Double, Double>> scanAnglePair = new ArrayList<>();

		double currentAng = sensorMessage.getMinAngle();
		double angleDelta = sensorMessage.getAngleIncrement();
		int angleCount = 0;
		for (double scanRay : sensorMessage.getScanRays()) {
//      System.out.println("Current Angle: " + currentAng + " ray: " + scanRay);
			if (angleCount >= nthSample || sensorMessage.getScanRays().size() < nthSample) {
				scanAnglePair.add(new MyPair<Double, Double>(scanRay, currentAng));
				angleCount = 0;
			} else {
				angleCount++;
			}
			currentAng = Pose.angleMod(currentAng + angleDelta);
		}

		probabilitySum = scanAnglePair.parallelStream().mapToDouble(pair -> {
			double rayDepth = pair.first;
			final double currentAngle = pair.second;
			if (rayDepth == 0) {
				rayDepth = RAYMAX;
			}

			Vector2d centerOfRobot = new Vector2d(belief.getPose().getX(), belief.getPose().getY());
			Vector2d rayEndpoint = new Vector2d(
					centerOfRobot.getX() + RAYMAX * Math.cos(belief.getPose().getTheta() + currentAngle),
					centerOfRobot.getY() + RAYMAX * Math.sin(belief.getPose().getTheta() + currentAngle));
			// System.out
			// .println("COR:" + centerOfRobot + " ang: " +
			// Math.toDegrees(belief.getPose().getTheta()));
			// System.out.println("Ray End:" + rayEndpoint);

			// Find the closest intersect.
			double closestIntersectDistance = map.getWalls().parallelStream().mapToDouble(mapwall -> {
				Optional<Vector2d> opt = Vector2d.getIntersection(mapwall.getPoint1(), mapwall.getPoint2(),
						centerOfRobot, rayEndpoint);
				if (opt.isPresent()) {
					Vector2d intersect = opt.get();
					return intersect.minus(centerOfRobot).norm();
				}
				return RAYMAX;
			}).min().getAsDouble();

			final double probability = getProbability(closestIntersectDistance, rayDepth);
			// System.out.println("Probability: " + probability);
			return probability;
		}).sum();

		return probabilitySum / sensorMessage.getScanRays().size();
	}
}
