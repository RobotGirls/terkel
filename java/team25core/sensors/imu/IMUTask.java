package team25core.sensors.imu;

import team25core.Robot;
import team25core.RobotTask;

public class IMUTask extends RobotTask {

    public IMUTask(Robot robot) {
        super(robot);
    }

    public void doTelemetry(boolean on) {

    }

    public int getYaw() {

    }

    @Override
    public boolean timeslice() {
        /*
         * Refresh the imu values.
         */

        /*
         * Display telemetry if on.
         */
        return false;
    }
}
