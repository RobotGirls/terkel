package team25core;
/*
 * FTC Team 25: elizabeth, December 20, 2018
 */

import com.qualcomm.robotcore.hardware.DcMotor;

public class HoldPositionTask extends RobotTask {

    private final static float SPEED = 0.02f;
    private final static float STOPPED = 0.0f;

    private double deadBand;

    private DcMotor motor;

    public HoldPositionTask(Robot robot, DcMotor motor, double deadBand) {
        super(robot);

        this.motor = motor;
        this.deadBand = deadBand;
    }


    @Override
    public void start() {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void stop() {
        robot.removeTask(this);
    }

    @Override
    public boolean timeslice() {

        int currPos = motor.getCurrentPosition();

        if (currPos > deadBand) {
            motor.setPower(-SPEED);
        } else if (currPos < deadBand) {
            motor.setPower(SPEED);
        } else {
            motor.setPower(STOPPED);
        }

        return false;

    }
}
