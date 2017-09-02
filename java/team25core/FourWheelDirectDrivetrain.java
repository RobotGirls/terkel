package team25core;

/*
 * FTC Team 25: cmacfarl, January 23, 2017
 */

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.RobotLog;

import opmodes.BeaconHelper;
import opmodes.DaisyBeaconAutonomous;

public class FourWheelDirectDrivetrain implements Drivetrain {

    DcMotor rearLeft;
    DcMotor rearRight;
    DcMotor frontLeft;
    DcMotor frontRight;

    int encoderTicksPerInch;
    int encoderTarget;
    double multiplier;

    DaisyBeaconAutonomous.Alliance alliance;

    public FourWheelDirectDrivetrain(int encoderTicksPerInch, DcMotor frontRight, DcMotor rearRight, DcMotor frontLeft, DcMotor rearLeft) {
        this.rearLeft = rearLeft;
        this.rearRight = rearRight;
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;

        this.encoderTicksPerInch = encoderTicksPerInch;
        this.encoderTarget = 0;
        this.multiplier = 1.0;

        setCanonicalMotorDirection();
    }

    public FourWheelDirectDrivetrain(int encoderTicksPerInch, double pivotMultiplier, DcMotor frontRight, DcMotor rearRight, DcMotor frontLeft, DcMotor rearLeft) {
        this.rearLeft = rearLeft;
        this.rearRight = rearRight;
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;

        this.encoderTicksPerInch = encoderTicksPerInch;
        this.encoderTarget = 0;
        this.multiplier = pivotMultiplier;

        setCanonicalMotorDirection();
    }

    public void setAlliance(DaisyBeaconAutonomous.Alliance alliance)
    {
        this.alliance = alliance;
    }

    public void setCanonicalMotorDirection()
    {
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        rearLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        rearRight.setDirection(DcMotor.Direction.REVERSE);
    }

    public void setNoncanonicalMotorDirection()
    {
        // This reverses the direction of the drivetrain.
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        rearLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        rearRight.setDirection(DcMotor.Direction.FORWARD);
    }
    @Override
    public void resetEncoders()
    {
        rearLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rearRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    public void encodersOn()
    {
        rearLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rearRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void logEncoderCounts()
    {
        RobotLog.i("POS Counts FL %d, FR %d, RL %d, RR %d", frontLeft.getCurrentPosition(), frontRight.getCurrentPosition(), rearLeft.getCurrentPosition(), rearRight.getCurrentPosition());
    }

    @Override
    public void straight(double speed)
    {
        frontRight.setPower(speed);
        frontLeft.setPower(speed);
        rearRight.setPower(speed);
        rearLeft.setPower(speed);
    }

    @Override
    public void turnLeft(double speed)
    {
        frontRight.setPower(speed);
        rearRight.setPower(speed);
        frontLeft.setPower(-speed);
        rearLeft.setPower(-speed);
    }

    @Override
    public void turnRight(double speed)
    {
        frontRight.setPower(-speed);
        rearRight.setPower(-speed);
        frontLeft.setPower(speed);
        rearLeft.setPower(speed);
    }

    @Override
    public void pivotTurn(PivotSide side, double speed)
    {
        switch (side) {
            case RIGHT_OVER_RIGHT:
                frontRight.setPower((1 / multiplier) * -speed);
                rearRight.setPower((1 / multiplier) * -speed);
                frontLeft.setPower(speed);
                rearLeft.setPower(speed);
                break;
            case RIGHT_OVER_LEFT:
                frontLeft.setPower( (1 / multiplier) * speed);
                rearLeft.setPower((1 / multiplier) * speed);
                frontRight.setPower(-speed);
                rearRight.setPower(-speed);
                break;
            case LEFT_OVER_RIGHT:
                frontRight.setPower((1 / multiplier) * speed);
                rearRight.setPower((1 / multiplier) * speed);
                frontLeft.setPower(-speed);
                rearLeft.setPower(-speed);
                break;
            case LEFT_OVER_LEFT:
                frontLeft.setPower((1 / multiplier) * -speed);
                rearLeft.setPower((1 / multiplier) * -speed);
                frontRight.setPower(speed);
                rearRight.setPower(speed);
                break;
        }
    }

    @Override
    public void stop()
    {
        frontLeft.setPower(0.0);
        frontRight.setPower(0.0);
        rearLeft.setPower(0.0);
        rearRight.setPower(0.0);
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
        frontLeft.setPower(left);
        frontRight.setPower(right);

        RobotLog.i("141 Axes A[%+5.2f], L[%+5.2f], Y[%+5.2f]", axial, lateral, yaw);
        RobotLog.i("141 Wheels L[%+5.2f], R[%+5.2f], BL[%+5.2f], BR[%+5.2f]", left, right, backLeft, backRight);
    }

    @Override
    public void strafeLeft(double speed)
    {
        frontRight.setPower(speed);
        rearRight.setPower(-speed);
        frontLeft.setPower(-speed);
        rearLeft.setPower(speed);
    }

    @Override
    public void strafeRight(double speed)
    {
        frontRight.setPower(-speed);
        rearRight.setPower(speed);
        frontLeft.setPower(speed);
        rearLeft.setPower(-speed);
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
            return (Math.abs(frontLeft.getCurrentPosition()) / encoderTarget);
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
