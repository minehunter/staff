package rdpsTester;

import java.util.ArrayList;

import infra_rdps_lib.Particle;
import infra_rdps_lib.PlatformPose;
import infra_rdps_lib.RadarDistancePositioningLib;
import infra_rdps_lib.RadarPulse;

/**
 * @author nsdc118
 *
 */
public class rdpsTester {

	public static void main(String[] args) {
		
		RadarDistancePositioningLib rdpslib = RadarDistancePositioningLib.getInstance();
		/*
		 * NumberOfParticles, InitialPos, initialRadius from OI
		 * DigitalMap Data form NRTS
		 */
		if(rdpslib.initialize(1000, 0, 0, 500, "Path to digitalMap data")) {
			System.out.println("Initialization is successful");
			ArrayList<Particle> initialParticles = rdpslib.getParticles();
			
			for(int index=0;index<initialParticles.size();index++) {
				Particle particle = initialParticles.get(index);
				
				System.out.println("Index:"+(index+1) +
								   "\tPosX:"+particle.getECEFPosition().xPosition +
								   "\tPosY:"+particle.getECEFPosition().yPosition +
								   "\tPosZ:"+particle.getECEFPosition().zPosition +
								   "\tCrabAngle:"+particle.getCrabAngle() +
								   "\tWeight:"+particle.getWeight() );
			}
			
			/*
			 * Use scheduled job 
			 */
			int count = 1000;
			for(int index=0;index<count;index++) {
				/*
				 * RadarScan data from RVN
				 * ShipHeading, [SoW, set, drift] => SoG from INFOGRAMs
				 */
				ArrayList<RadarPulse> radarScan = new ArrayList<RadarPulse>();
				double platformHeading = 0;
				double speedOverGround = 0;
				
				PlatformPose platformPose =  rdpslib.runEstimator(radarScan, platformHeading, speedOverGround);
				
				System.out.println("Index:"+(index+1) +
						   		   "\tPosX:"+platformPose.getPosition().xPosition +
						   		   "\tPosY:"+platformPose.getPosition().yPosition +
						   		   "\tPosZ:"+platformPose.getPosition().zPosition +
						   		   "\tCourse:"+platformPose.getCourse());
			}
			
			
			
		}else {
			System.out.println("Initialization is failed!");
		}
		
		

	}

}
