/*
 * FTC Team 25: Created by Elizabeth Wu, December 20, 2018
 */

package team25core;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.RobotLog;

public class HoldPositionTask extends RobotTask {

    private static String TAG = "HoldPositionTask";

    private final static float SPEED = 0.02f;
    private final static float STOPPED = 0.0f;

    private double deadBand;

    private DcMotor motor;

    public HoldPositionTask(Robot robot, DcMotor motor, double deadBand)
    {
        super(robot);

        this.motor = motor;
        this.deadBand = deadBand;
    }

    @Override
    public void start()
    {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void stop()
    {
        robot.removeTask(this);
    }

    @Override
    public boolean timeslice()
    {
        int currPos = motor.getCurrentPosition();

        RobotLog.ii(TAG, "position " + currPos);
        if (Math.abs(currPos) < deadBand) {
            motor.setPower(STOPPED);
        } else if (currPos > deadBand) {
            motor.setPower(-SPEED);
        } else if (currPos < deadBand) {
            motor.setPower(SPEED);
        }

        return false;
    }
}
