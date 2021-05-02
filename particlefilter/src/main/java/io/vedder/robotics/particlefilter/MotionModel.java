package io.vedder.robotics.particlefilter;

import io.vedder.robotics.particlefilter.messages.OdomMessage;

public class MotionModel {
	private final double STDDEV_X;
	private final double STDDEV_Y;
	private final double STDDEV_THETA;

	public MotionModel() {
		STDDEV_X = 0.06;
		STDDEV_Y = 0.06;
		STDDEV_THETA = 0.01;
	}

	private double stdDevX(final double x) {
		return x * 10 * STDDEV_X + 0.005;
	}

	private double stdDevY(final double y) {
		return y * 10 * STDDEV_Y + 0.005;
	}

	private double stdDevAngle(final double angle) {
		return angle * 10 * STDDEV_THETA + 0.01;
	}

	/**
	 * Sample given the odometry.
	 */
	public void sampleOdometry(OdomMessage odomMessage, final Particle particle) {
		final double noiseyRobotFrameX = Utils.randomN(odomMessage.getXOdom(), stdDevX(odomMessage.getXOdom()));
		final double noiseyRobotFrameY = Utils.randomN(odomMessage.getYOdom(), stdDevY(odomMessage.getYOdom()));
		final double noiseyRobotFrameTheta = Utils.randomN(odomMessage.getAngleOdom(),
				stdDevAngle(odomMessage.getAngleOdom()));

		final Pose currentPose = particle.getPose();

		final double noiseyWorldFrameX = noiseyRobotFrameX * Math.cos(currentPose.getTheta())
				+ noiseyRobotFrameY * Math.sin(currentPose.getTheta()) + currentPose.getX();
		final double noiseyWorldFrameY = noiseyRobotFrameX * Math.sin(currentPose.getTheta())
				+ noiseyRobotFrameY * Math.cos(currentPose.getTheta()) + currentPose.getY();

		final double noiseyWorldFrameTheta = noiseyRobotFrameTheta + currentPose.getTheta();

		particle.setPose(new Pose(noiseyWorldFrameX, noiseyWorldFrameY, noiseyWorldFrameTheta));
	}

}
