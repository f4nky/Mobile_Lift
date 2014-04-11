package ch.bfh.ti.mobile.fankymeyer.sensor;

import java.util.Timer;
import java.util.TimerTask;

import ch.bfh.ti.mobile.fankymeyer.viewer.Viewer;
import ch.quantasy.tinkerforge.tinker.agent.implementation.TinkerforgeStackAgent;
import ch.quantasy.tinkerforge.tinker.application.implementation.AbstractTinkerforgeApplication;
import ch.quantasy.tinkerforge.tinker.core.implementation.TinkerforgeDevice;

import com.tinkerforge.BrickIMU;
import com.tinkerforge.BrickIMU.Acceleration;
import com.tinkerforge.BrickIMU.Quaternion;
import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.Device;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import com.tinkerforge.TinkerforgeException;

public class IMUBarometerFusion extends AbstractTinkerforgeApplication {
	private static int FACTOR = 1;
	private static float KP1 = 0.55f * FACTOR; // PI observer velocity gain
	private static float KP2 = 1.0f * FACTOR; // PI observer position gain
	private static float KI = 0.001f / FACTOR; // PI observer integral gain
												// (bias cancellation)
	private Viewer viewer;
	private BrickIMU imu;
	private BrickletBarometer barometer;

	private Timer timer = new Timer();

	private boolean initialized = false;

	private float compensated_acceleration;
	private float first_altitude = 0.0f;
	private float altitude_error_i = 0.0f;
	private float estimated_velocity = 0.0f;
	private float estimated_altitude = 0.0f;
	private long start_time;
	private long last_time;
	private float last_orig_altitude = 0.0f;
	private float last_estimated_altitude = 0.0f;

	public IMUBarometerFusion(Viewer viewer) {
		this.viewer = viewer;
	}

	@Override
	@SuppressWarnings("incomplete-switch")
	public void deviceConnected(TinkerforgeStackAgent tinkerforgeStackAgent,
			Device device) {
		switch (TinkerforgeDevice.getDevice(device)) {
		case IMU:
			imu = (BrickIMU) device;

			try {
				// Turn leds and orientation calculation off, to save
				// calculation time for the IMU Brick. This makes sure that the
				// measurements are taken in equidistant 2ms intervals
				imu.ledsOff();
				imu.orientationCalculationOff();
			} catch (TimeoutException e) {
				e.printStackTrace();
			} catch (NotConnectedException e) {
				e.printStackTrace();
			}
			break;
		case Barometer:
			barometer = (BrickletBarometer) device;

			try {
				// Turn averaging off in the Barometer Bricklet to make sure
				// that the data is without delay
				barometer.setAveraging((short) 0, (short) 0, (short) 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		if (imu != null && barometer != null) {
			last_time = System.currentTimeMillis();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						update();

						if (last_time - start_time < 5000) {
							first_altitude = estimated_altitude;
						}
						viewer.showAcceleration(compensated_acceleration);
						viewer.showDistance(last_estimated_altitude
								- first_altitude);
						viewer.showHeight(last_estimated_altitude);
					} catch (TinkerforgeException e) {
						e.printStackTrace();
					}
				}
			}, 6, 6);
		}
	}

	@Override
	public void deviceDisconnected(TinkerforgeStackAgent tinkerforgeStackAgent,
			Device device) {
		timer.cancel();
		timer = new Timer();
		if (device == imu)
			imu = null;
		if (device == barometer)
			barometer = null;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Update measurements and compute new altitude every 6ms.
	 * 
	 * @throws TinkerforgeException
	 */
	private void update() throws TinkerforgeException {
		Quaternion q = imu.getQuaternion();
		Acceleration acc = imu.getAcceleration();
		float alt = barometer.getAltitude() / 100.0f;

		Quaternion compensatedAccQ = computeCompensatedAcc(q, acc);
		Quaternion compensatedAccQEarth = computeDynamicAccelerationVector(q,
				compensatedAccQ);

		compensated_acceleration = compensatedAccQEarth.z;
		last_orig_altitude = alt;
		last_estimated_altitude = computeAltitude(compensatedAccQEarth.z, alt);
	}

	/**
	 * Remove gravity from accelerometer measurements
	 * 
	 * @param q
	 * @param a
	 * @return
	 */
	private Quaternion computeCompensatedAcc(Quaternion q, Acceleration a) {
		Quaternion g = imu.new Quaternion();
		g.x = 2 * (q.x * q.z - q.w * q.y);
		g.y = 2 * (q.w * q.x + q.y * q.z);
		g.z = q.w * q.w - q.x * q.x - q.y * q.y + q.z * q.z;

		Quaternion result = imu.new Quaternion();
		result.x = (float) (a.x / 1000.0 - g.x);
		result.y = (float) (a.y / 1000.0 - g.y);
		result.z = (float) (a.z / 1000.0 - g.z);
		result.w = 0;
		return result;
	}

	/**
	 * Rotate dynamic acceleration vector from sensor frame to earth frame
	 * 
	 * @param q
	 * @param compensated_acc_q
	 * @return
	 */
	private Quaternion computeDynamicAccelerationVector(Quaternion q,
			Quaternion compensated_acc_q) {
		Quaternion tmp = q_mult(q, compensated_acc_q);
		return q_mult(tmp, q_conj(q));
	}

	private Quaternion q_conj(Quaternion q) {
		Quaternion r = imu.new Quaternion();
		r.x = -q.x;
		r.y = -q.y;
		r.z = -q.z;
		r.w = q.w;
		return r;
	}

	private Quaternion q_mult(Quaternion q1, Quaternion q2) {
		Quaternion r = imu.new Quaternion();
		r.w = q1.w * q2.w - q1.x * q2.x - q1.y * q2.y - q1.z * q2.z;
		r.x = q1.w * q2.x + q1.x * q2.w + q1.y * q2.z - q1.z * q2.y;
		r.y = q1.w * q2.y + q1.y * q2.w + q1.z * q2.x - q1.x * q2.z;
		r.z = q1.w * q2.z + q1.z * q2.w + q1.x * q2.y - q1.y * q2.x;
		return r;
	}

	/**
	 * Computes estimation of altitude based on barometer and accelerometer
	 * measurements. Code is based on blog post from Fabio Varesano:
	 * {@link http
	 * ://www.varesano.net/blog/fabio/complementary-filtering-high-res
	 * -barometer-and-accelerometer-reliable-altitude-estimation} He seems to
	 * have got the idea from the MultiWii project
	 * 
	 * @param compensated_acceleration
	 * @param altitude
	 * @return
	 */
	private float computeAltitude(float compensated_acceleration, float altitude) {
		long current_time = System.currentTimeMillis();

		System.out.println(current_time - start_time + ";"
				+ compensated_acceleration);

		// Initialization
		if (!initialized) {
			initialized = true;
			first_altitude = estimated_altitude = altitude;
			estimated_velocity = 0;
			altitude_error_i = 0;
			start_time = current_time;
		}

		// Estimation Error
		float altitude_error = altitude - estimated_altitude;
		altitude_error_i += altitude_error;
		if (altitude_error_i < -2500f)
			altitude_error_i = -2500f;
		if (altitude_error_i > 2500f)
			altitude_error_i = 2500f;

		float inst_acceleration = compensated_acceleration * 9.80665f
				+ altitude_error_i * KI;
		// time must be in s, not ms
		float dt = (current_time - last_time) / 1000f;

		// Integrators
		float delta = (inst_acceleration + KP1 * altitude_error) * dt;
		estimated_altitude += (estimated_velocity / 5.0f + delta) * (dt / 2)
				+ (KP2 * dt) * altitude_error;
		estimated_velocity += delta * 10.0f;

		last_time = current_time;

		return estimated_altitude;
	}
}
