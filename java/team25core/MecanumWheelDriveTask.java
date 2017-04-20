
package team25core;

/*
 * FTC Team 25: Created by Katelyn Biesiadecki on 10/29/2016.
 */

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.RobotLog;

public class MecanumWheelDriveTask extends RobotTask {
    protected Robot robot;
    protected DcMotor frontLeft;
    protected DcMotor frontRight;
    protected DcMotor rearLeft;
    protected DcMotor rearRight;

    public double fr;
    public double fl;
    public double rr;
    public double rl;
    public double slowMultiplier = 1;
    public double leftX;
    public double rightX;
    public double leftY;
    public double rightY;

    public boolean yForward = true;
    public boolean isSuspended = false;

    public MecanumWheelDriveTask(Robot robot, DcMotor frontLeft, DcMotor frontRight, DcMotor rearLeft, DcMotor rearRight)
    {
        super(robot);

        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.rearLeft = rearLeft;
        this.rearRight = rearRight;
        this.robot = robot;
        this.isSuspended = false;

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        rearLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        rearRight.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    private void getJoystick() {
        Gamepad gamepad;
        gamepad = robot.gamepad1;

        if (yForward) {
            leftX = gamepad.left_stick_x;
            rightX = gamepad.right_stick_x;
            leftY = gamepad.left_stick_y;
            rightY = gamepad.right_stick_y;
        } else {
            leftX = -gamepad.left_stick_y;
            rightX = -gamepad.right_stick_y;
            leftY = -gamepad.left_stick_x;
            rightY = -gamepad.right_stick_x;
        }

        // If joysticks are pointed left (negative joystick values), counter rotate wheels.
        // Threshold for joystick values in the x may vary.

        if (leftX > 0.5 && rightX > 0.5) {
            fl = -leftX;
            rl = leftX;
            fr = -rightX;
            rr = rightX;
        } else if (leftX < -0.5 && rightX < -0.5) {
            fl = -leftX;
            rl = leftX;
            fr = -rightX;
            rr = rightX;
        } else if (gamepad.right_trigger > 0.5) {
            fr = -1.0;
            rl = 1.0;
        } else if (gamepad.left_trigger > 0.5) {
            fl = 1.0;
            rr = -1.0;
        } else if (gamepad.left_bumper) {
            fr = 1.0;
            rl = -1.0;
        } else if (gamepad.right_bumper) {
            rr = 1.0;
            fl = -1.0;
        } else {
            fl = leftY;
            rl = leftY;
            fr = -rightY;
            rr = -rightY;
        }
    }

    public void suspendTask(boolean isSuspended)
    {
        this.isSuspended = isSuspended;
    }

    public void changeDirection()
    {
       if (yForward) {
           yForward = false;
       } else {
           yForward = true;
       }
    }

    @Override
    public void start()
    {
        // Nothing.
    }

    public void slowDown(boolean slow)
    {
        if (slow) {
            slowMultiplier = 0.5;
        } else {
            slowMultiplier = 1;
        }
    }

    public void slowDown(double mult)
    {
        slowMultiplier = mult;
    }

    @Override
    public void stop()
    {
        robot.removeTask(this);
    }

    @Override
    public boolean timeslice()
    {
        if (isSuspended) {
            RobotLog.i("teleop timeslice suspended");
            return false;
        }

        RobotLog.i("teleop timeslice not suspended");
        getJoystick();

        frontLeft.setPower(fl * slowMultiplier);
        rearLeft.setPower(rl * slowMultiplier);
        frontRight.setPower(fr * slowMultiplier);
        rearRight.setPower(rr * slowMultiplier);

        return false;
    }

}
