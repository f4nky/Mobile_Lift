package ch.bfh.ti.mobile.fankymeyer.viewer;

/**
 * An interface to make the viewer interchangeable.
 * 
 * @author Christian Meyer
 */
public interface Viewer {
	void showAcceleration(double x, double y, double z);

	void showDistance(double distanceInMeters);

	void showHeight(double heightInM);
}
