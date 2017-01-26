package team25core;
/*
 * FTC Team 25: cmacfarl, January 23, 2017
 */

import com.qualcomm.robotcore.hardware.DcMotor;

public class TwoWheelDirectDrivetrain implements Drivetrain {

    DcMotor frontLeft;
    DcMotor frontRight;

    int encoderTicksPerInch;
    double multiplier;

    public TwoWheelDirectDrivetrain(int encoderTicksPerInch, DcMotor frontRight, DcMotor frontLeft)
    {
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;

        this.encoderTicksPerInch = encoderTicksPerInch;
        this.multiplier = 1.0;

        frontRight.setDirection(DcMotor.Direction.REVERSE);
    }

    public TwoWheelDirectDrivetrain(int encoderTicksPerInch, double pivotMultiplier, DcMotor frontRight, DcMotor frontLeft)
    {
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;

        this.encoderTicksPerInch = encoderTicksPerInch;
        this.multiplier = pivotMultiplier;

        frontRight.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void resetEncoders()
    {
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    public void encodersOn()
    {
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void straight(double speed)
    {
        frontRight.setPower(speed);
        frontLeft.setPower(speed);
    }

    @Override
    public void turnLeft(double speed)
    {
        frontRight.setPower(speed);
        frontLeft.setPower(-speed);
    }

    @Override
    public void turnRight(double speed)
    {
        frontRight.setPower(-speed);
        frontLeft.setPower(speed);
    }

    @Override
    public void pivotTurn(PivotSide side, double speed)
    {
        switch (side) {
        case LEFT_OVER_RIGHT:
            frontLeft.setPower(-speed);
            frontRight.setPower((1/multiplier) * speed);
            break;
        case LEFT_OVER_LEFT:
            frontLeft.setPower((1/multiplier) * -speed);
            frontRight.setPower(speed);
            break;
        case RIGHT_OVER_RIGHT:
            frontLeft.setPower(speed);
            frontRight.setPower((1/multiplier) * -speed);
            break;
        case RIGHT_OVER_LEFT:
            frontLeft.setPower((1/multiplier) * speed);
            frontRight.setPower(-speed);
            break;
        }
    }

    @Override
    public void stop()
    {
        frontLeft.setPower(0.0);
        frontRight.setPower(0.0);
    }
}
