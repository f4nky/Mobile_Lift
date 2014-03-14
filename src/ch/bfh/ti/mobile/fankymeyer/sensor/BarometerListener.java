package ch.bfh.ti.mobile.fankymeyer.sensor;

import ch.bfh.ti.mobile.fankymeyer.viewer.Viewer;

import com.tinkerforge.BrickletBarometer.AltitudeListener;

public class BarometerListener implements AltitudeListener {
	private Viewer viewer;

	private int baseAltitude = Integer.MIN_VALUE;

	public BarometerListener(Viewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void altitude(int altitude) {
		if (baseAltitude == Integer.MIN_VALUE)
			baseAltitude = altitude;
		viewer.showHeight(altitude - baseAltitude);
	}
}
