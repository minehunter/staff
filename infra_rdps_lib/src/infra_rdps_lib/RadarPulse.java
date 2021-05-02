package infra_rdps_lib;

import java.util.ArrayList;

/**
 * @author nsdc118
 *
 */
public class RadarPulse {

	private double azimuthAngle;
	private ArrayList<Double> intensities;
	private int radarRange;
	private double distanceToFirstIntensity;
	private double distanceBetweenSamples;
	
	/**
	 * @param azimuthAngle
	 * @param intensities
	 * @param radarRange
	 * @param distanceToFirstIntensity
	 * @param distanceBetweenSamples
	 */
	public RadarPulse(double azimuthAngle,
				      ArrayList<Double> intensities, 
					  int radarRange, 
					  double distanceToFirstIntensity,
					  double distanceBetweenSamples) {
		super();
		this.azimuthAngle = azimuthAngle;
		this.intensities = intensities;
		this.radarRange = radarRange;
		this.distanceToFirstIntensity = distanceToFirstIntensity;
		this.distanceBetweenSamples = distanceBetweenSamples;
	}
	
	/**
	 * @return
	 */
	public double getAzimuthAngle() {
		return azimuthAngle;
	}

	/**
	 * @param azimuthAngle
	 */
	public void setAzimuthAngle(double azimuthAngle) {
		this.azimuthAngle = azimuthAngle;
	}

	/**
	 * @return
	 */
	public ArrayList<Double> getIntensities() {
		return intensities;
	}


	/**
	 * @param intensities
	 */
	public void setIntensities(ArrayList<Double> intensities) {
		this.intensities = intensities;
	}


	/**
	 * @return
	 */
	public int getRadarRange() {
		return radarRange;
	}


	/**
	 * @param radarRange
	 */
	public void setRadarRange(int radarRange) {
		this.radarRange = radarRange;
	}


	/**
	 * @return
	 */
	public double getDistanceToFirstIntensity() {
		return distanceToFirstIntensity;
	}


	/**
	 * @param distanceToFirstIntensity
	 */
	public void setDistanceToFirstIntensity(double distanceToFirstIntensity) {
		this.distanceToFirstIntensity = distanceToFirstIntensity;
	}


	/**
	 * @return
	 */
	public double getDistanceBetweenSamples() {
		return distanceBetweenSamples;
	}


	/**
	 * @param distanceBetweenSamples
	 */
	public void setDistanceBetweenSamples(double distanceBetweenSamples) {
		this.distanceBetweenSamples = distanceBetweenSamples;
	}
	
}
