package ch.bfh.ti.mobile.fankymeyer.sensor;

import ch.quantasy.tinkerforge.tinker.agent.implementation.TinkerforgeStackAgent;
import ch.quantasy.tinkerforge.tinker.application.implementation.AbstractTinkerforgeApplication;
import ch.quantasy.tinkerforge.tinker.core.implementation.TinkerforgeDevice;

import com.tinkerforge.BrickIMU;
import com.tinkerforge.Device;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class IMUApplication extends AbstractTinkerforgeApplication {
	private IMUListener listener;

	public IMUApplication(IMUListener listener) {
		this.listener = listener;
	}

	@Override
	public void deviceConnected(TinkerforgeStackAgent tinkerforgeStackAgent,
			Device device) {
		if (TinkerforgeDevice.getDevice(device) == TinkerforgeDevice.IMU) {
			final BrickIMU imu = (BrickIMU) device;
			imu.addAccelerationListener(listener);

			try {
				imu.setAccelerationPeriod(500);
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
			final BrickIMU imu = (BrickIMU) device;
			imu.removeAccelerationListener(listener);
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
