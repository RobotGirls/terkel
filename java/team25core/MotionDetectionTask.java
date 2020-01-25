package team25core;


import com.qualcomm.hardware.bosch.BNO055IMU;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;

public class MotionDetectionTask extends RobotTask {

    BNO055IMU imu;
    Acceleration linearaccel;
    public double xAccel;

    public enum EventType {
        MOVING,
        STOPPED,
    }

    public class MotionDetectionEvent extends RobotEvent {

        public EventType type;

        public MotionDetectionEvent(RobotTask task, EventType type) {
            super(task);
            this.type = type;
        }

        public String toString() {
            return type.toString();
        }
    }

    public MotionDetectionTask(Robot robot, BNO055IMU imu) {
        super(robot);
        this.imu = imu;
    }

    @Override
    public void start()
    {

    }

    @Override
    public void stop()
    {
    }

    @Override
    public boolean timeslice()
    {
        linearaccel = imu.getLinearAcceleration();
        xAccel = linearaccel.xAccel;

        if ((xAccel >= -0.150) && (xAccel <= 0.150)) {
            robot.queueEvent(new MotionDetectionEvent(this, EventType.STOPPED));
        } else {
            robot.queueEvent(new MotionDetectionEvent(this, EventType.MOVING));
        }
        return false;
    }
}

