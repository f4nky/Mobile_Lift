package ch.bfh.ti.mobile.fankymeyer.viewer;

import java.text.DecimalFormat;

import ch.quantasy.tinkerforge.tinker.agent.implementation.TinkerforgeStackAgent;
import ch.quantasy.tinkerforge.tinker.application.implementation.AbstractTinkerforgeApplication;
import ch.quantasy.tinkerforge.tinker.core.implementation.TinkerforgeDevice;
import ch.quantasy.tinkerforge.tinker.util.implementation.LCD20x4Manager;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.Device;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class LiftViewer extends AbstractTinkerforgeApplication implements
		Viewer {

	private static final short ACC_HEADER_LINE = 0;
	private static final short ACC_DATA_LINE = 1;
	private static final short DIST_LINE = 2;
	private static final short HEIGHT_LINE = 3;
	private BrickletLCD20x4 lcd;

	private static final DecimalFormat DIST_FORMAT = new DecimalFormat("0.00");
	private static final DecimalFormat ACC_FORMAT = new DecimalFormat("#.00");

	@Override
	public void deviceConnected(TinkerforgeStackAgent tinkerforgeStackAgent,
			Device device) {
		if (TinkerforgeDevice.getDevice(device) == TinkerforgeDevice.LCD20x4) {
			if (!TinkerforgeDevice.areEqual(this.lcd, device)) {
				this.lcd = (BrickletLCD20x4) device;
			}
			this.initLCD();
		}
	}

	@Override
	public void deviceDisconnected(TinkerforgeStackAgent tinkerforgeStackAgent,
			Device device) {
		if (TinkerforgeDevice.areEqual(this.lcd, device)) {
			this.lcd = null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	public void showAcceleration(double x, double y, double z) {
		String accLine = "x=" + ACC_FORMAT.format(x) + ", y="
				+ ACC_FORMAT.format(y) + ", z=" + ACC_FORMAT.format(z)
				+ "        ";
		this.writeValueToLCD(accLine, LiftViewer.ACC_DATA_LINE);
	}

	public void showDistance(double distanceInMeters) {
		String distLine = DIST_FORMAT.format(distanceInMeters) + " m       ";
		this.writeValueToLCD(distLine, LiftViewer.DIST_LINE);
	}

	@Override
	public void showHeight(double heightInM) {
		String heightLine = DIST_FORMAT.format(heightInM) + " m       ";
		this.writeValueToLCD(heightLine, LiftViewer.HEIGHT_LINE);
	}

	private void initLCD() {
		if (this.lcd != null) {
			synchronized (this.lcd) {
				try {
					this.lcd.clearDisplay();
					this.lcd.writeLine(LiftViewer.ACC_HEADER_LINE, (short) 0,
							"Acceleration:");
					this.lcd.writeLine(LiftViewer.DIST_LINE, (short) 0,
							"Distance:");
					this.lcd.writeLine(LiftViewer.HEIGHT_LINE, (short) 0,
							"Height:");
					this.lcd.backlightOn();
				} catch (TimeoutException | NotConnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void writeValueToLCD(String text, short lineNr) {
		if (this.lcd != null) {
			synchronized (this.lcd) {
				try {
					// the text could possibly too long for the rest of the
					// line, not fixed yet.
					if (lineNr == LiftViewer.ACC_DATA_LINE) {
						LCD20x4Manager.write(this.lcd, lineNr, (short) 1, text);
					} else {
						LCD20x4Manager
								.write(this.lcd, lineNr, (short) 10, text);
					}
				} catch (TimeoutException | NotConnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
