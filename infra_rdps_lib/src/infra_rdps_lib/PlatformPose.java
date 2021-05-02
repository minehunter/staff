package infra_rdps_lib;

/**
 * @author nsdc118
 *
 */
public class PlatformPose {

	private ECEFPosition position;
	private double course;
	
	/**
	 * @param position
	 * @param course
	 */
	public PlatformPose(ECEFPosition position, double course) {
		super();
		this.position = position;
		this.course = course;
	}
	/**
	 * @return
	 */
	public ECEFPosition getPosition() {
		return position;
	}
	/**
	 * @param position
	 */
	public void setPosition(ECEFPosition position) {
		this.position = position;
	}
	/**
	 * @return
	 */
	public double getCourse() {
		return course;
	}
	/**
	 * @param course
	 */
	public void setCourse(double course) {
		this.course = course;
	}
	
}
