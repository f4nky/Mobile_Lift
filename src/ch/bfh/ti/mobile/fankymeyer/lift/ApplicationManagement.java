package ch.bfh.ti.mobile.fankymeyer.lift;

import java.io.IOException;

import ch.quantasy.tinkerforge.tinker.agency.implementation.TinkerforgeStackAgency;
import ch.quantasy.tinkerforge.tinker.agent.implementation.TinkerforgeStackAgentIdentifier;
import ch.quantasy.tinkerforge.tinker.application.definition.TinkerforgeApplication;

public class ApplicationManagement {
	public static void main(final String[] args) throws IOException {
		System.out.println(ApplicationManagement.class.getPackage());
		final TinkerforgeStackAgentIdentifier identifier = new TinkerforgeStackAgentIdentifier(
				"localhost");

		final TinkerforgeApplication application = new MasterBrickApplication();
		TinkerforgeStackAgency.getInstance().getStackAgent(identifier).addApplication(application);
		System.out.println("Press key to exit");
		System.in.read();
		TinkerforgeStackAgency.getInstance().getStackAgent(identifier).removeApplication(application);
		System.out.println("Finished");
	}
}
