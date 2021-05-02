package io.vedder.robotics.particlefilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.vedder.robotics.particlefilter.messages.InitMessage;
import io.vedder.robotics.particlefilter.messages.OdomMessage;
import io.vedder.robotics.particlefilter.messages.SensorMessage;

public class ParticleContainer {

	class WeightParticle {
		private Particle particle;
		private double weight;
		private double normWeight;

		public WeightParticle(final Pose defaultPose, double weight) {
			this.particle = new Particle(defaultPose);
			this.weight = weight;
			this.normWeight = weight;
		}

		public WeightParticle(final Pose defaultPose, final double weight, final double normWeight) {
			this.particle = new Particle(defaultPose);
			this.weight = weight;
			this.normWeight = normWeight;
		}

		public WeightParticle(final WeightParticle wp) {
			this.particle = wp.getParticle();
			this.weight = wp.getWeight();
			this.normWeight = wp.getNormWeight();
		}

		public Particle getParticle() {
			return particle;
		}

		public void setParticle(Particle particle) {
			this.particle = particle;
		}

		public double getWeight() {
			return weight;
		}

		public double getNormWeight() {
			return normWeight;
		}

		public void setWeight(double weight) {
			this.weight = weight;
		}

		public void setNormWeight(double normWeight) {
			this.normWeight = normWeight;
		}

		@Override
		public String toString() {
			return "WeightParticle [particle=" + particle + ", weight=" + weight + ", norm weight=" + normWeight + "]";
		}

		public String serialize() {
			return particle.serialize() + " " + weight + " " + normWeight;
		}

	}

	private List<WeightParticle> particles;
	private final SensorModel sensorModel;
	private final MotionModel motionModel;
	private final Map map;

	public ParticleContainer(final int numberParticles, final Pose defaultPose, final SensorModel sensorModel,
			final MotionModel motionModel, final Map map, boolean useNoise) {
		this.particles = new ArrayList<>();
		this.sensorModel = sensorModel;
		this.motionModel = motionModel;
		this.map = map;
		for (int i = 0; i < numberParticles; ++i) {
			if (useNoise) {
				final double noiseyX = Utils.randomN(0, 0.01) + defaultPose.getX();
				final double noiseyY = Utils.randomN(0, 0.01) + defaultPose.getY();
				final double noiseyTheta = Utils.randomN(0, 0.01) + defaultPose.getTheta();
				this.particles.add(new WeightParticle(new Pose(noiseyX, noiseyY, noiseyTheta), 1));
			} else {
				this.particles.add(new WeightParticle(defaultPose, 1));
			}
		}
	}

	public ParticleContainer(final List<Pose> poses, final SensorModel sensorModel, final MotionModel motionModel,
			final Map map) {
		this.particles = new ArrayList<>();
		this.sensorModel = sensorModel;
		this.motionModel = motionModel;
		this.map = map;
		for (final Pose pose : poses) {
			// final double noiseyX = Utils.randomUniform(-0.1, 0.1) + defaultPose.getX();
			// final double noiseyY = Utils.randomUniform(-0.1, 0.1) + defaultPose.getY();
			// final double noiseyTheta = Utils.randomN(0, 0.01) + defaultPose.getTheta();
			// this.particles.add(new WeightParticle(new Pose(noiseyX, noiseyY,
			// noiseyTheta), 1));
			this.particles.add(new WeightParticle(pose, 1));
		}
	}

	public void initParticles(final InitMessage init) {
		for (final WeightParticle weightParticle : particles) {
			weightParticle.getParticle().setPose(new Pose(init.getX(), init.getY(), init.getTheta()));
		}
	}

	List<WeightParticle> getParticles() {
		return particles;
	}

	public void updateMotion(final OdomMessage odomMessage) {
		for (final WeightParticle weightParticle : particles) {
			motionModel.sampleOdometry(odomMessage, weightParticle.getParticle());
		}
	}

	public void updateAndResample(final SensorMessage sensorMessage, final int nthSample) {

		// Update weight

		double maxWeight = 0;
		for (final WeightParticle weightParticle : particles) {
			final double newWeight = sensorModel.sampleSensor(sensorMessage, weightParticle.getParticle(), map,
					nthSample);
			// System.out.println("New weight: " + newWeight);
			if (newWeight > maxWeight) {
				maxWeight = newWeight;
			}
			weightParticle.setWeight(newWeight);
			// System.out.println("New Weight: " + newWeight);
		}

		System.out.println("Max weight: " + maxWeight);
		for (final WeightParticle weightParticle : particles) {
			weightParticle.setNormWeight(weightParticle.getWeight() / maxWeight);
			// System.out.println("Norm weight:" + weightParticle.getNormWeight());
			if (weightParticle.getNormWeight() > 1.0) {
				System.out.println("Norm calculation is wrong!");
			}
		}

		final double totalWeights = particles.parallelStream().mapToDouble(e -> e.weight).sum();

		// Resamples based on random selection. Does not use the low variance method of
		// sampling.
		particles = particles.parallelStream().map(e -> {
			double selection = Utils.random.nextDouble() * totalWeights;
			for (WeightParticle wp : particles) {
				if (wp.weight < selection) {
					selection -= wp.weight;
				} else {
					final Pose origPose = wp.getParticle().getPose();
					final Pose noiseyParticlePose = new Pose(origPose.getX() + Utils.randomN(0, 0.001),
							origPose.getY() + Utils.randomN(0, 0.001), origPose.getTheta() + Utils.randomN(0, 0.001));
					final double weight = wp.getWeight();
					final double normWeight = wp.getNormWeight();
					return new WeightParticle(noiseyParticlePose, weight, normWeight);
				}
			}
			return new WeightParticle(particles.get(particles.size() - 1));
		}).collect(Collectors.toList());
	}

	public String serialize() {
		return particles.stream().map(e -> e.serialize()).reduce(String.valueOf(particles.size()),
				(e1, e2) -> e1 + " " + e2);
	}
}
