package io.vedder.robotics.particlefilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.vedder.robotics.particlefilter.messages.BaseMessage;
import io.vedder.robotics.particlefilter.messages.InitMessage;
import io.vedder.robotics.particlefilter.messages.OdomMessage;
import io.vedder.robotics.particlefilter.messages.SensorMessage;

public class Main {

	public static void main(String[] args) {
		System.out.println("Starting Particle Filter!");
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		String mapPath = "../reference-material/assignment-2-data/map.txt";
		String logPath = "../reference-material/assignment-2-data/robot-data.log";
		String particleSavePath = "../reference-material/assignment-2-data/test-particles.txt";
		boolean isInited = false;
		Map map = new Map(mapPath);
		MotionModel motionModel = new MotionModel();
		SensorModel sensorModel = new SensorModel();
		LogData logData = new LogData(logPath);
		ParticleContainer particleContainer = new ParticleContainer(100, new Pose(0, 0, 0), sensorModel, motionModel,
				map, false);
		List<String> particleInfo = new ArrayList<>();
		int i = 0;
		for (BaseMessage message : logData.getData()) {
			System.out.println("Input: " + i++);
			switch (message.getMessageType()) {
			case INIT: {
				isInited = true;
				InitMessage init = message.getInitMessage();
				System.out.println("Initing to (" + init.getX() + ", " + init.getY() + ") theta deg: "
						+ Math.toDegrees(init.getTheta()));
				particleContainer.initParticles(init);
			}
				break;
			case ODOM: {
				if (isInited) {
					OdomMessage odom = message.getOdomMessage();
					particleContainer.updateMotion(odom);
				}
			}
				break;
			case SENSOR: {
				if (isInited) {
					SensorMessage sense = message.getSensorMessage();
					particleContainer.updateAndResample(sense, 5);
					particleInfo.add(particleContainer.serialize());
				}
			}
				break;
			}
		}
		try {
			Files.write(Paths.get(particleSavePath), particleInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Complete!");
	}

}
