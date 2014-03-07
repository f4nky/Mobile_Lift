package ch.bfh.ti.mobile.fankymeyer.sensor;

import ch.bfh.ti.mobile.fankymeyer.viewer.Viewer;

import com.tinkerforge.BrickIMU.AccelerationListener;

/**
 * A listener that handles all input and displays the result through the viewer.
 * 
 * @author Christian Meyer
 */
public class IMUListener implements AccelerationListener {
	private Viewer viewer;

	private long lastTime = 0;

	// the speeds
	private double vX, vY, vZ;
	// the distances
	private double dX, dY, dZ;

	public IMUListener(Viewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void acceleration(short x, short y, short z) {
		long time = System.currentTimeMillis();

		// Remove gravitational acceleration. This is crude, but interestingly
		// enough the z value seems to have the smallest error.
		z += 1000;

		// Acceleration in m/s^2
		double aX = x * 9.80665 / 1000;
		double aY = y * 9.80665 / 1000;
		double aZ = z * 9.80665 / 1000;

		viewer.showAcceleration(aX, aY, aZ);

		System.out.println("x = " + aX);
		System.out.println("y = " + aY);
		System.out.println("z = " + aZ);

		if (lastTime != 0) {
			double t = (time - lastTime) / 1000.0;

			// Distance traveled in each direction
			dX += vX * t + aX * t * t / 2;
			dY += vY * t + aY * t * t / 2;
			dZ += vZ * t + aZ * t * t / 2;

			// Velocity in m/s after accelerating
			vX += aX * t;
			vY += aY * t;
			vZ += aZ * t;

			System.out.println("dx = " + dX + "\ndy = " + dY + "\ndz = " + dZ);

			viewer.showDistance(Math.sqrt(dX * dX + dY * dY + dZ * dZ));
		}
		lastTime = time;
	}
}
