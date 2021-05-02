package io.vedder.robotics.particlefilter;

public class Particle {
  private Pose pose;

  public Particle(final Pose pose) {
    this.pose = pose;
  }

  public Pose getPose() {
    return pose;
  }

  public void addPose(final Pose otherPose) {
    pose = pose.addPose(otherPose);
  }

  public void setPose(Pose pose) {
    this.pose = pose;
  }

  @Override
  public String toString() {
    return "Particle [pose=" + pose + "]";
  }

  public String serialize() {
    return pose.getX() + " " + pose.getY() + " " + pose.getTheta();
  }
}
