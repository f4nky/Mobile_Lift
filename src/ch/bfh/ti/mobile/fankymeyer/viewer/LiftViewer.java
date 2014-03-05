package ch.bfh.ti.mobile.fankymeyer.viewer;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.Device;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import ch.quantasy.tinkerforge.tinker.agent.implementation.TinkerforgeStackAgent;
import ch.quantasy.tinkerforge.tinker.application.implementation.AbstractTinkerforgeApplication;
import ch.quantasy.tinkerforge.tinker.core.implementation.TinkerforgeDevice;
import ch.quantasy.tinkerforge.tinker.util.implementation.LCD20x4Manager;

public class LiftViewer extends AbstractTinkerforgeApplication {

	private static final short ACC_HEADER_LINE = 1;
	private static final short ACC_DATA_LINE = 2;
	private static final short DIST_LINE = 3;
	private BrickletLCD20x4 lcd;
	
	@Override
	public void deviceConnected(TinkerforgeStackAgent tinkerforgeStackAgent,
			Device device) {
		if (TinkerforgeDevice.getDevice(device)==TinkerforgeDevice.LCD20x4) {
			if(!TinkerforgeDevice.areEqual(this.lcd, device)) {
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
	
	public void showAcceleration(int x, int y, int z) {
		String accLine = "x=" + x + ", y=" + y + ", z=" + z;
		this.writeValueToLCD(accLine, LiftViewer.ACC_DATA_LINE);
	}
	
	public void showDistance(double distanceInMeters) {
		String distLine = distanceInMeters + " m";
		this.writeValueToLCD(distLine, LiftViewer.DIST_LINE);
	}
	
	private void initLCD() {
		if (this.lcd != null) {
			synchronized(this.lcd) {
				try {
					this.lcd.clearDisplay();
					this.lcd.writeLine(LiftViewer.ACC_HEADER_LINE, (short) 0, "Acceleration:");
					this.lcd.writeLine(LiftViewer.DIST_LINE, (short) 0, "Distance:");
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
					// the text could possibly too long for the rest of the line, not fixed yet.
					if (lineNr == LiftViewer.ACC_DATA_LINE) {
						LCD20x4Manager.write(this.lcd, lineNr, (short) 1, text);
					} else {
						LCD20x4Manager.write(this.lcd, lineNr, (short) 10, text);
					}					
				} catch (TimeoutException | NotConnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
