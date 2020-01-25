package team25core;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.RobotLog;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MotorStallTask extends RobotTask {

    public enum EventKind {
        STALLED,
    }

    public class MotorStallEvent extends RobotEvent {

        public EventKind kind;

        public MotorStallEvent(Robot robot, team25core.MotorStallTask.EventKind kind) {
            super(robot);
            this.kind = kind;
        }
    }

    protected int currentcount;
    protected int previouscount = -1;
    protected boolean motorStop = false;
    protected DcMotor motor;
    SingleShotTimerTask sst = null;
    Telemetry.Item status;
    Telemetry.Item encoder;
    Telemetry.Item previous;

    public MotorStallTask(Robot robot, DcMotor motor, Telemetry telemetry){
        super(robot);
        this.motor = motor;
        this.status = telemetry.addData("Status", "Moving");
        this.encoder = telemetry.addData("Encoder", "0");
        this.previous = telemetry.addData("Previous", "0");
    }

    public void start() {
        RobotLog.i("Motor stall task started!");
    }

    @Override
    public void stop() {
        RobotLog.i("Motor stall task stopped!");
        status.setValue("Stopped");
    }

    @Override
    public boolean timeslice() {

        currentcount = motor.getCurrentPosition();
        motorStop = Math.abs(currentcount - previouscount) < 2;

        encoder.setValue(currentcount);
        previous.setValue(previouscount);

        if (motorStop == true) {
            if (sst == null ) {
                RobotLog.i("Starting SST");
                sst = new SingleShotTimerTask(robot, 100) {
                    public void handleEvent (RobotEvent e) {
                        RobotLog.i("SST Expired");
                        robot.queueEvent(new MotorStallEvent(robot, MotorStallTask.EventKind.STALLED));
                    }
                };
                robot.addTask(sst);
                return false;
            } else {
                return false;
            }
        } else {
            // Is SingleShotTimerTask running?  If so, we need to stop it.
            if (sst != null) {
                RobotLog.i("Stop SST");
                sst.stop();
                sst = null;
            }
            previouscount = currentcount;
            return false;
        }
    }
}


