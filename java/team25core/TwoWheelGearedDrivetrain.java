package team25core;

/*
 * FTC Team 25: cmacfarl, January 23, 2017
 */

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.RobotLog;

public class TwoWheelGearedDrivetrain implements Drivetrain {

    DcMotor rearLeft;
    DcMotor rearRight;

    int encoderTicksPerInch;
    int encoderTarget;
    double multiplier;

    public TwoWheelGearedDrivetrain(int encoderTicksPerInch, DcMotor rearRight, DcMotor rearLeft) {
        this.rearLeft = rearLeft;
        this.rearRight = rearRight;

        this.encoderTicksPerInch = encoderTicksPerInch;
        this.encoderTarget = 0;
        this.multiplier = 1.0;

        setCanonicalMotorDirection();
    }

    public TwoWheelGearedDrivetrain(int encoderTicksPerInch, double pivotMultiplier, DcMotor rearRight, DcMotor rearLeft) {
        this.rearLeft = rearLeft;
        this.rearRight = rearRight;

        this.encoderTicksPerInch = encoderTicksPerInch;
        this.encoderTarget = 0;
        this.multiplier = pivotMultiplier;

        setCanonicalMotorDirection();
    }

    public void setCanonicalMotorDirection()
    {
        rearLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        rearRight.setDirection(DcMotor.Direction.FORWARD);
    }

    public void setNoncanonicalMotorDirection()
    {
        // This reverses the direction of the drivetrain.
        rearLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        rearRight.setDirection(DcMotor.Direction.REVERSE);
    }
    @Override
    public void resetEncoders()
    {
        rearLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rearRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    public void encodersOn()
    {
        rearLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rearRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void logEncoderCounts()
    {
        RobotLog.i("POS Counts RL %d, RR %d", rearLeft.getCurrentPosition(), rearRight.getCurrentPosition());
    }

    @Override
    public int getCurrentPosition()
    {
        return 0;
    }

    @Override
    public void straight(double speed)
    {
        rearRight.setPower(speed);
        rearLeft.setPower(speed);
    }

    @Override
    public void strafe(double speed)
    {
        rearRight.setPower(speed);
        rearLeft.setPower(-speed);
    }

    @Override
    public void leftDiagonal(double speed)
    {
        // Not implemented
    }

    @Override
    public void rightDiagonal(double speed)
    {
        // Not implemented
    }

    @Override
    public void turn(double speed)
    {
        rearRight.setPower(-speed);
        rearLeft.setPower(speed);
    }

    @Override
    public void pivotTurn(PivotSide side, double speed)
    {
        switch (side) {
            case RIGHT_OVER_RIGHT:
                rearRight.setPower((1 / multiplier) * -speed);
                rearLeft.setPower(speed);
                break;
            case RIGHT_OVER_LEFT:
                rearLeft.setPower((1 / multiplier) * speed);
                rearRight.setPower(-speed);
                break;
            case LEFT_OVER_RIGHT:
                rearRight.setPower((1 / multiplier) * speed);
                rearLeft.setPower(-speed);
                break;
            case LEFT_OVER_LEFT:
                rearLeft.setPower((1 / multiplier) * -speed);
                rearRight.setPower(speed);
                break;
        }
    }

    @Override
    public void setPivotMultiplier(double pivotMultiplier)
    {

    }

    @Override
    public void setPowerLeft(double speed)
    {
        rearLeft.setPower(speed);
    }

    @Override
    public void setPowerRight(double speed)
    {
        rearRight.setPower(speed);
    }

    @Override
    public void stop()
    {
        rearLeft.setPower(0.0);
        rearRight.setPower(0.0);
    }

    @Override
    public void setMasterMotor(DcMotor motor)
    {

    }

    @Override
    public DcMotor getMasterMotor()
    {
        return null;
    }

    @Override
    public void move(double axial, double lateral, double yaw)
    {
        // calculate required motor speeds to achieve axis motions
        double backLeft;
        double backRight;
        double left;
        double right;

        backLeft = axial - lateral + yaw;
        backRight = axial + lateral - yaw;
        left = axial + lateral + yaw;
        right = axial - lateral - yaw;

        // normalize all motor speeds so no values exceeds 100%.
        double max = Math.max(Math.abs(backLeft), Math.abs(right));
        max = Math.max(max, Math.abs(backRight));
        max = Math.max(max, Math.abs(left));
        if (max > 1.0)
        {
            backLeft /= max;
            backRight /= max;
            right /= max;
            left /= max;
        }

        rearLeft.setPower(backLeft);
        rearRight.setPower(backRight);

        RobotLog.i("141 Axes A[%+5.2f], L[%+5.2f], Y[%+5.2f]", axial, lateral, yaw);
        RobotLog.i("141 Wheels L[%+5.2f], R[%+5.2f], BL[%+5.2f], BR[%+5.2f]", left, right, backLeft, backRight);
    }

    @Override
    public void setTargetInches(int inches)
    {
        encoderTarget = inches * encoderTicksPerInch;
    }

    @Override
    public double percentComplete()
    {
        if (encoderTarget != 0) {
            return (Math.abs(rearLeft.getCurrentPosition()) / encoderTarget);
        } else {
            return 1;
        }
    }

    @Override
    public boolean isBusy()
    {
        if (Math.abs(rearLeft.getCurrentPosition()) <= encoderTarget) {
            return true;
        } else {
            return false;
        }
    }
}
