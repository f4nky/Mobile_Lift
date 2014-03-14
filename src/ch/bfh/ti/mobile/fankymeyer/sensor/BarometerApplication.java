package ch.bfh.ti.mobile.fankymeyer.sensor;

import ch.quantasy.tinkerforge.tinker.agent.implementation.TinkerforgeStackAgent;
import ch.quantasy.tinkerforge.tinker.application.implementation.AbstractTinkerforgeApplication;
import ch.quantasy.tinkerforge.tinker.core.implementation.TinkerforgeDevice;

import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.Device;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class BarometerApplication extends AbstractTinkerforgeApplication {
	private BarometerListener listener;

	public BarometerApplication(BarometerListener listener) {
		this.listener = listener;
	}

	@Override
	public void deviceConnected(TinkerforgeStackAgent tinkerforgeStackAgent,
			Device device) {
		if (TinkerforgeDevice.getDevice(device) == TinkerforgeDevice.Barometer) {
			final BrickletBarometer baro = (BrickletBarometer) device;
			baro.addAltitudeListener(listener);

			try {
				baro.setAltitudeCallbackPeriod(500);
			} catch (TimeoutException e) {
				e.printStackTrace();
			} catch (NotConnectedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void deviceDisconnected(TinkerforgeStackAgent tinkerforgeStackAgent,
			Device device) {
		if (TinkerforgeDevice.getDevice(device) == TinkerforgeDevice.IMU) {
			final BrickletBarometer imu = (BrickletBarometer) device;
			imu.removeAltitudeListener(listener);
		}
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}

}
