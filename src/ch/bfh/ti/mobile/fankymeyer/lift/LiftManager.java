package ch.bfh.ti.mobile.fankymeyer.lift;

import ch.bfh.ti.mobile.fankymeyer.sensor.IMUApplication;
import ch.bfh.ti.mobile.fankymeyer.sensor.IMUListener;
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
	private final IMUApplication liftIt;
	private final LiftViewer showIt;
	private final TinkerforgeStackAgent agent;

	public LiftManager(String hostname) {
		showIt = new LiftViewer();
		liftIt = new IMUApplication(new IMUListener(showIt));
		agent = TinkerforgeStackAgency.getInstance().getStackAgent(
				new TinkerforgeStackAgentIdentifier(hostname));
	}

	public void start() {
		agent.addApplication(liftIt);
		agent.addApplication(showIt);
	}

	public void stop() {
		agent.removeApplication(liftIt);
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
