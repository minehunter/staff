package infra_rdps_lib;

/**
 * @author nsdc118
 *
 */
public class Particle {
	
	/**
	 * ECEF Position
	 */
	private ECEFPosition ecefPosition;
	/**
	 * The angle(in degree) between the course and the heading of the ship
	 */
	private double crabAngle;
	/**
	 * Weight of the particle according to its accuracy 
	 */
	private double weight;
	/**
	 * Normalized Weight(0..1) of the particle according to its accuracy 
	 */
	private double normWeight;
	/**
	 * Calculated Likelihood value for recursive Bayesian estimation
	 */
	private double likelihood;
	/**
	 * Last position update time by motion model in milliseconds from epoch time
	 */
	private long lastPositionUpdateTime;
	
	/**
	 * @param ecefPosition
	 * ECEF Position of particle
	 * @param crabAngle
	 * The angle(in degree) between the course and the heading of the ship
	 * @param weight
	 * Weight of the particle according to its accuracy
	 * @param normWeight
	 * Normalized Weight(0..1) of the particle according to its accuracy
	 * @param likelihood
	 * Calculated Likelihood value for recursive Bayesian estimation
	 * @param lastPositionUpdateTime
	 * Last position update time by motion model in milliseconds from epoch time
	 */
	public Particle(ECEFPosition ecefPosition, 
					double crabAngle, 
					double weight, 
					double normWeight, 
					double likelihood,
					long lastPositionUpdateTime) {
		super();
		this.ecefPosition = ecefPosition;
		this.crabAngle = crabAngle;
		this.weight = weight;
		this.normWeight = normWeight;
		this.likelihood = likelihood;
		this.lastPositionUpdateTime = lastPositionUpdateTime;
	}

	/**
	 * Deep copy constructor
	 * @param copyParticle
	 */
	public Particle(Particle copyParticle) {
		super();
		this.ecefPosition = copyParticle.ecefPosition;
		this.crabAngle = copyParticle.crabAngle;
		this.weight = copyParticle.weight;
		this.normWeight = copyParticle.normWeight;
		this.likelihood = copyParticle.likelihood;
		this.lastPositionUpdateTime = copyParticle.lastPositionUpdateTime;
	}
	
	/**
	 * @return
	 */
	public ECEFPosition getECEFPosition() {
		return ecefPosition;
	}

	/**
	 * @param ecefPosition
	 */
	public void setECEFPosition(ECEFPosition ecefPosition) {
		this.ecefPosition = ecefPosition;
	}

	/**
	 * @return
	 */
	public double getCrabAngle() {
		return crabAngle;
	}

	/**
	 * @param crabAngle
	 */
	public void setCrabAngle(double crabAngle) {
		this.crabAngle = crabAngle;
	}

	/**
	 * @return
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * @param weight
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	/**
	 * @return
	 */
	public double getNormWeight() {
		return normWeight;
	}

	/**
	 * @param normWeight
	 */
	public void setNormWeight(double normWeight) {
		this.normWeight = normWeight;
	}

	/**
	 * @return
	 */
	public double getLikelihood() {
		return likelihood;
	}

	/**
	 * @param likelihood
	 */
	public void setLikelihood(double likelihood) {
		this.likelihood = likelihood;
	}

	/**
	 * @return
	 */
	public long getLastPositionUpdateTime() {
		return lastPositionUpdateTime;
	}

	/**
	 * @param lastPositionUpdateTime
	 */
	public void setLastPositionUpdateTime(long lastPositionUpdateTime) {
		this.lastPositionUpdateTime = lastPositionUpdateTime;
	}
}
