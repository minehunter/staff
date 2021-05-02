package infra_rdps_lib;

import java.util.ArrayList;

/**
 * @author nsdc118
 *
 */
public class RadarDistancePositioningLib {

	/**
	 * Singleton instance of Radar Distance Positioning Library
	 */
	private static RadarDistancePositioningLib radarDistancePositioningLib;
	/**
	 * Number of particles will be used at Sequential Monte Carlo Simulation
	 */
	private int numberOfParticles;
	/**
	 * Center Geographical Latitude value in degree for Particle Filter Algorithm initialization
	 */
	private double initialLat;
	/**
	 * Center Geographical Longitude value in degree for Particle Filter Algorithm initialization
	 */
	private double initialLong;
	/**
	 * Radius(in meters) of initial random particles
	 */
	private double initialRadius;
	/**
	 * Particle List
	 */
	private ArrayList<Particle> particles;
	
	/**
	 * Constructor without parameters
	 */
	public RadarDistancePositioningLib() {
		super();
		this.particles = new ArrayList<Particle>();
	}

	/**
	 * @return Singleton instance of Radar Distance Positioning Library
	 */
	public static RadarDistancePositioningLib getInstance() {
		if(radarDistancePositioningLib == null) {
			radarDistancePositioningLib = new RadarDistancePositioningLib();
		}else{
			//do nothing
		}
		return radarDistancePositioningLib;
	}
	
	/**
	 * @param numberOfParticles 
	 * Number of particles will be used at Sequential Monte Carlo Simulation
	 * @param initialLat 
	 * Center Geographical Latitude value in degree for Particle Filter Algorithm initialization
	 * @param initialLong
	 * Center Geographical Longitude value in degree for Particle Filter Algorithm initialization 
	 * @param initialRadius
	 * Initial random particles distribution radius in meters 
	 * @param DigitalMapPath
	 * Path of the digital map data 
	 * @return 
	 * True : Initialization successful<br>
	 * False: Initialization failed
	 */
	public boolean initialize(int numberOfParticles, 
							  double initialLat, 
							  double initialLong,
							  double initialRadius,
							  String DigitalMapPath) {
		
		boolean initializeOK = true;
		
		this.numberOfParticles = numberOfParticles;
		this.initialLat = initialLat;
		this.initialLong = initialLong;
		this.initialRadius = initialRadius;
		
		/*
		 * TODO
		 * GeographicalPosition(initialLat, initialLong, 0).toECEF()
		 * after infra_support_lib import
		 */
		ECEFPosition initialEcefPos = new ECEFPosition(0, 0, 0);
		
		
		for(int index=0; index<this.numberOfParticles; index++) {
			
			boolean validParticle = false;
			ECEFPosition particlePos = new ECEFPosition(0, 0, 0);
			/*
			 * TODO 
			 * Use infra_time_lib
			 */
			Particle particle = new Particle(particlePos, 0, 0, 0, 0, System.currentTimeMillis());
			
			while(!validParticle) {
				/*
				 * TODO 
				 * All range calculations must be geodesic
				 */
				double minXPosition = initialEcefPos.xPosition - this.initialRadius; 
				double maxXPosition = initialEcefPos.xPosition + this.initialRadius;
				double minYPosition = initialEcefPos.yPosition - this.initialRadius; 
				double maxYPosition = initialEcefPos.yPosition + this.initialRadius;
				
				particlePos.xPosition = ProbabilisticUtility.randomUniform(minXPosition, maxXPosition);
				particlePos.yPosition = ProbabilisticUtility.randomUniform(minYPosition, maxYPosition);
				particlePos.zPosition = initialEcefPos.zPosition;
				
				particle.setECEFPosition(particlePos);
				
				validParticle = checkParticlePosValidity(particle, DigitalMapPath);
			}
			
			particle.setCrabAngle(ProbabilisticUtility.randomUniform(Constants.MIN_CRAB_ANGLE, Constants.MAX_CRAB_ANGLE));
			particle.setWeight(1.0D / this.numberOfParticles);
			
			this.particles.add(particle);
		}
		
		
		if(this.particles.size() == this.numberOfParticles) {
			initializeOK = true;
		}else {
			initializeOK = false;
		}
		
		return initializeOK;
	}
	
	/**
	 * Core algorithm runner for particle filter based localization
	 * It should be called by user application.
	 * Calling interval should be determined by user application 
	 *  
	 * @param radarScan
	 * Radar video azimuth data
	 * @param shipHeading
	 * Heading of the ownship
	 * @param speedOverGround
	 * Speed over water rectified by drift and set
	 * @return
	 */
	public PlatformPose runEstimator(ArrayList<RadarPulse> radarScan, 
									 double platformHeading, 
									 double speedOverGround) {
		
		calculateParticleWeights(radarScan);
		resampleParticles();
		applyMotionModelOnParticles(platformHeading, speedOverGround);
		
		return estimatePlatformPose(platformHeading);
	}
	
	/**
	 * @param currentParticles
	 * @param radarScan
	 */
	private void calculateParticleWeights(ArrayList<RadarPulse> radarScan) {
		
		double maxWeight = 0;
		/*
		 * Calculate weights of each particle
		 */
		for(int index=0; index<particles.size(); index++) {
			Particle particle = particles.get(index);
			double azimuthAngle = 0;
			double radarDistanceFromLand = 0;
			double mapDistanceFromLand = 0;
			double totalProbability = 0;
			
			for(RadarPulse radarPulse : radarScan) {
				azimuthAngle = radarPulse.getAzimuthAngle();
				radarDistanceFromLand = DistanceModel.detectLandDistance(particle, radarPulse);
				mapDistanceFromLand = DigitalMapModel.detectLandDistance(particle, azimuthAngle);
				totalProbability += ProbabilisticUtility.likelihoodGaussian(radarDistanceFromLand, mapDistanceFromLand, Constants.SENSOR_SIGMA);
			}
			double weight = totalProbability / radarScan.size();
			if(weight > maxWeight) {
				maxWeight = weight;
			}else {
				//do nothing
			}
			particle.setWeight(weight);
		}
		/*
		 * Calculate normalized weights of each particle
		 */
		for(int index=0; index<this.particles.size(); index++) {
			Particle estimatedParticle = this.particles.get(index);
			estimatedParticle.setNormWeight(estimatedParticle.getWeight() / maxWeight);
		}
		
	}
	
	
	/**
	 * Resampling particles based on normalized weight values
	 */
	private void resampleParticles() {
		
		ArrayList<Particle> resampledParticles = new ArrayList<>();
		double weightThreshold = 0;
		int numParticles = this.particles.size();
		int index = (int)(ProbabilisticUtility.randomDouble() * numParticles);
		double maxWeight = this.particles.parallelStream().mapToDouble(e -> e.getNormWeight()).max().getAsDouble();
		
        for (int i = 0; i < numParticles; i++) {
        	
        	weightThreshold += ProbabilisticUtility.randomDouble() * 2.0D * maxWeight;
            while (weightThreshold > this.particles.get(index).getNormWeight()) {
            	weightThreshold -= this.particles.get(index).getNormWeight();
                index = circle(index + 1, numParticles);
            }
            
            Particle resampledParticle = new Particle(this.particles.get(index));
            ECEFPosition ecefPos = resampledParticle.getECEFPosition(); 
            ECEFPosition noiseyParticlePos = new ECEFPosition(ecefPos.xPosition + ProbabilisticUtility.randomGaussian(0, 0.001),
            												  ecefPos.yPosition + ProbabilisticUtility.randomGaussian(0, 0.001),
            												  ecefPos.zPosition + ProbabilisticUtility.randomGaussian(0, 0.001));
            resampledParticle.setECEFPosition(noiseyParticlePos);
            resampledParticle.setCrabAngle(resampledParticle.getCrabAngle() + ProbabilisticUtility.randomGaussian(0, 0.001));
            resampledParticles.add(resampledParticle);
        }
        
        this.particles = resampledParticles;
	}
	
	
	/**
	 * @param shipHeading
	 * @param speedOverGround
	 */
	private void applyMotionModelOnParticles(double platformHeading, double speedOverGround) {
		
		for(Particle particle : this.particles) {
			
			double xPos = particle.getECEFPosition().xPosition;
			double yPos = particle.getECEFPosition().yPosition;
			double zPos = particle.getECEFPosition().zPosition;
			double crabAngle = particle.getCrabAngle();
			double shipCourse = Math.toRadians(platformHeading - crabAngle);
			/*
			 * TODO 
			 * Use infra_time_lib
			 */
			long deltaTime = (System.currentTimeMillis() - particle.getLastPositionUpdateTime()) / 1000;
			
			xPos += speedOverGround * deltaTime * Math.sin(shipCourse);
			yPos += speedOverGround * deltaTime * Math.cos(shipCourse);
			
			ECEFPosition updatedPos = new ECEFPosition(xPos + ProbabilisticUtility.randomGaussian(0, 0.001), 
													   yPos + ProbabilisticUtility.randomGaussian(0, 0.001), 
													   zPos);
			particle.setECEFPosition(updatedPos);
		}
	}
	
	/**
	 * @param particle
	 * @return
	 */
	private boolean checkParticlePosValidity(Particle particle, String DigitalMapPath) {
		
		boolean result = true;
		/*
		 * TODO : Check particle position with digital map
		 * 		  whether at sea or land
		 * 		  if particle is at sea return true, else return false
		 */
		return result;
	}

	/**
	 * @return
	 */
	public ArrayList<Particle> getParticles() {
		return particles;
	}
	
	/**
	 * @return
	 */
	public ArrayList<Particle> getParticlesCopy() {
		ArrayList<Particle> copyParticles = new ArrayList<Particle>();
		for(Particle particle : this.particles) {
			Particle copyParticle = new Particle(particle);
			copyParticles.add(copyParticle);
		}
		return copyParticles;
	}
	
	/**
	 * @param platformHeading
	 * @return
	 */
	public PlatformPose estimatePlatformPose(double platformHeading) {
		double xPos = 0, yPos = 0, zPos = 0, crabAngle = 0;
		
		for(Particle particle : this.particles) {
			xPos += particle.getECEFPosition().xPosition * particle.getNormWeight();
			yPos += particle.getECEFPosition().yPosition * particle.getNormWeight();
			zPos += particle.getECEFPosition().zPosition * particle.getNormWeight();
			crabAngle += particle.getCrabAngle() * particle.getNormWeight();
		}
		
		double platformCourse = platformHeading - crabAngle;
		platformCourse = (platformCourse < 0) ? platformCourse+=360.0D : platformCourse;
		
		return new PlatformPose(new ECEFPosition(xPos, yPos, zPos), platformCourse);
	}
	
	/**
	 * @param num
	 * @param length
	 * @return
	 */
	private int circle(int num, int length) {
        while (num > length - 1) {
            num -= length;
        }
        while (num < 0) {
            num += length;
        }
        return num;
    }
	
}
