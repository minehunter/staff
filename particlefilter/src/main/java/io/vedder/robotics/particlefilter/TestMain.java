package io.vedder.robotics.particlefilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.vedder.robotics.particlefilter.ParticleContainer.WeightParticle;
import io.vedder.robotics.particlefilter.messages.OdomMessage;
import io.vedder.robotics.particlefilter.messages.SensorMessage;

public class TestMain {

  public static void main(String[] args) {
    System.out.println("Starting Particle Filter Tester!");
    System.out.println("Working Directory = " + System.getProperty("user.dir"));
    String mapPath = "../reference-material/assignment-2-data/map-test.txt";
    String particleSavePath = "../reference-material/assignment-2-data/test-particles.txt";
    // String logPath = "../reference-material/assignment-2-data/robot-data.log";

    Map map = new Map(mapPath);
    SensorModel sensorModel = new SensorModel();
    MotionModel motionModel = new MotionModel();

    final boolean TEST_MOTION_MODEL = true;

    if (TEST_MOTION_MODEL) {
      ParticleContainer particleContainer = new ParticleContainer(10,
          new Pose(1, 1, -3 * Math.PI / 4), sensorModel, motionModel, map, false);
      List<String> updates = new ArrayList<>();
      for (int i = 0; i < 10; ++i) {
        OdomMessage odomMessage = new OdomMessage(0, 0.01, 0, 0);
        particleContainer.updateMotion(odomMessage);
        updates.add(particleContainer.serialize());
      }
      System.out.println("Serialized particle container: " + updates);

      try {
        Files.write(Paths.get(particleSavePath), updates);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      ParticleContainer particleContainer = new ParticleContainer(
          Arrays.asList(new Pose(5, 1, -Math.PI / 2), new Pose(5, 1, Math.PI / 2)), sensorModel,
          motionModel, map);
      SensorMessage sm = new SensorMessage(0, 1, -Math.PI / 4, Math.PI / 4, Math.PI / 4,
          Arrays.asList(4.0, 4.0, 4.0));

      for (WeightParticle p : particleContainer.getParticles()) {
        System.out.println(p);
      }
      for (int i = 0; i < 1; ++i) {
        particleContainer.updateAndResample(sm, 5);
      }

      for (WeightParticle p : particleContainer.getParticles()) {
        System.out.println(p);
      }
      System.out.println("Serialized particle container: " + particleContainer.serialize());

      try {
        Files.write(Paths.get(particleSavePath), Arrays.asList(particleContainer.serialize()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

}
