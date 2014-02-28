package ch.bfh.ti.mobile.fankymeyer.sensor;

import com.tinkerforge.BrickIMU;
import com.tinkerforge.BrickIMU.AccelerationListener;
import com.tinkerforge.Device;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import ch.quantasy.tinkerforge.tinker.agent.implementation.TinkerforgeStackAgent;
import ch.quantasy.tinkerforge.tinker.application.implementation.AbstractTinkerforgeApplication;
import ch.quantasy.tinkerforge.tinker.core.implementation.TinkerforgeDevice;

public class IMUApplication extends AbstractTinkerforgeApplication implements
		AccelerationListener {

	@Override
	public void deviceConnected(TinkerforgeStackAgent tinkerforgeStackAgent,
			Device device) {
		if (TinkerforgeDevice.getDevice(device) == TinkerforgeDevice.IMU) {
			final BrickIMU imu = (BrickIMU) device;
			imu.addAccelerationListener(this);

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
			imu.removeAccelerationListener(this);
		}
	}

	@Override
	public void acceleration(short x, short y, short z) {
		System.out.println("x = " + x);
		System.out.println("y = " + y);
		System.out.println("z = " + z);
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
