package ch.bfh.ti.mobile.fankymeyer.lift;

import ch.bfh.ti.mobile.fankymeyer.sensor.BarometerApplication;
import ch.bfh.ti.mobile.fankymeyer.sensor.IMUApplication;
import ch.bfh.ti.mobile.fankymeyer.sensor.IMUBarometerFusion;
import ch.bfh.ti.mobile.fankymeyer.viewer.LiftViewer;
import ch.quantasy.tinkerforge.tinker.agency.implementation.TinkerforgeStackAgency;
import ch.quantasy.tinkerforge.tinker.agent.implementation.TinkerforgeStackAgent;
import ch.quantasy.tinkerforge.tinker.agent.implementation.TinkerforgeStackAgentIdentifier;

/**
 * This is the part where everything gets connected.
 * 
 * @author Christian Meyer
 */
public class LiftManager {
	private IMUApplication imuApplication;
	private IMUBarometerFusion fusion;
	private BarometerApplication barometerApplication;
	private LiftViewer showIt;
	private TinkerforgeStackAgent agent;

	public LiftManager(String hostname) {
		showIt = new LiftViewer();
		// imuApplication = new IMUApplication(new IMUListener(showIt));
		// barometerApplication = new BarometerApplication(new
		// BarometerListener(
		// showIt));
		fusion = new IMUBarometerFusion(showIt);
		agent = TinkerforgeStackAgency.getInstance().getStackAgent(
				new TinkerforgeStackAgentIdentifier(hostname));
	}

	public void start() {
		if (imuApplication != null)
			agent.addApplication(imuApplication);
		if (barometerApplication != null)
			agent.addApplication(barometerApplication);
		if (fusion != null)
			agent.addApplication(fusion);
		agent.addApplication(showIt);
	}

	public void stop() {
		if (imuApplication != null)
			agent.removeApplication(imuApplication);
		if (barometerApplication != null)
			agent.removeApplication(barometerApplication);
		if (fusion != null)
			agent.removeApplication(fusion);
		agent.removeApplication(showIt);
	}

	/**
	 * A simple boot-strap. The program will shut-down gracefully if one hits
	 * 'any' key on the console
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		// Use MasterBrick3676 to use our brick over WiFi
		String hostname;
		if (args.length > 0)
			hostname = args[0];
		else
			hostname = "localhost";

		final LiftManager manager = new LiftManager(hostname);
		manager.start();
		System.in.read();
		manager.stop();
	}
}
