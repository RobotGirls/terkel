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
        frontLeft.setMode(DcMotor.RunMode.RESET_ENCODERS);
        frontRight.setMode(DcMotor.RunMode.RESET_ENCODERS);
    }

    @Override
    public void encodersOn()
    {
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);
    }

    @Override
    public void straight(double speed)
    {
        frontRight.setPower(speed);
        frontLeft.setPower(speed);
    }

    @Override
    public void turn(double speed)
    {
        frontRight.setPower(speed);
        frontLeft.setPower(-speed);
    }

    @Override
    public void pivotTurn(PivotSide side, double speed)
    {
        if (side == PivotSide.RIGHT) {
            frontLeft.setPower(speed);
            frontRight.setPower(-(1/multiplier) * speed);
        } else if (side == PivotSide.LEFT) {
            frontLeft.setPower(-(1/multiplier) * speed);
            frontRight.setPower(speed);
        }
    }

    @Override
    public void stop()
    {
        frontLeft.setPower(0.0);
        frontRight.setPower(0.0);
    }
}
