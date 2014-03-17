package ch.bfh.ti.mobile.fankymeyer.sensor;

import java.util.ArrayDeque;
import java.util.Queue;

import ch.bfh.ti.mobile.fankymeyer.viewer.Viewer;

import com.tinkerforge.BrickIMU.AccelerationListener;

/**
 * A listener that handles all input and displays the result through the viewer.
 * 
 * @author Christian Meyer
 */
public class IMUListener implements AccelerationListener {
	private static int INIT_PHASE_LENGTH = 200;
	private static int AVERAGE_SIZE = 5;
	private static double UPPER_THRESHOLD = 5.0;
	private static double LOWER_THRESHOLD = 0.001;
	private Viewer viewer;

	private long lastTime = 0;

	private double zDeviation;
	private double initCount;

	private Queue<Double> zAverageQueue = new ArrayDeque<>(AVERAGE_SIZE);
	private double zAverage;

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

		// Acceleration in m/s^2
		double aX = x * 9.80665 / 1000;
		double aY = y * 9.80665 / 1000;
		double aZ = z * 9.80665 / 1000;

		// System.out.println("x = " + aX);
		// System.out.println("y = " + aY);
		// System.out.println("z = " + aZ);
		if (initCount < INIT_PHASE_LENGTH) {
			zDeviation += aZ;
			initCount++;
			return;
		} else if (initCount == INIT_PHASE_LENGTH) {
			zDeviation = zDeviation / initCount;
		}
		initCount++;
		aZ -= zDeviation;

		int size = zAverageQueue.size();
		if (size < AVERAGE_SIZE) {
			zAverageQueue.add(aZ / AVERAGE_SIZE);
			zAverage += aZ / AVERAGE_SIZE;
			return;
		}
		if (aZ < UPPER_THRESHOLD) {
			double removed = zAverageQueue.remove();
			zAverageQueue.add(aZ / AVERAGE_SIZE);
			zAverage = zAverage - removed + aZ / AVERAGE_SIZE;
			if (zAverage > LOWER_THRESHOLD)
				System.out.println(aZ + " ->\t" + zAverage);
		}
		aZ = (zAverage > LOWER_THRESHOLD ? zAverage : 0);

		viewer.showAcceleration(0, 0, aZ);

		if (lastTime != 0) {
			double t = (time - lastTime) / 1000.0;

			// Distance traveled in each direction
			// dX += vX * t + aX * t * t / 2;
			// dY += vY * t + aY * t * t / 2;
			dZ += vZ * t + aZ * t * t / 2.0;

			// Velocity in m/s after accelerating
			// vX += aX * t;
			// vY += aY * t;
			vZ += aZ * t;

			// System.out.println("dx = " + dX + "\ndy = " + dY + "\ndz = " +
			// dZ);
			// System.out.println("t = " + t);

			// viewer.showDistance(Math.sqrt(dX * dX + dY * dY + dZ * dZ));
			viewer.showDistance(dZ);
		}
		lastTime = time;
	}
}
