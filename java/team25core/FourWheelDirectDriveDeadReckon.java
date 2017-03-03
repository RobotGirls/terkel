package team25core;

/*
 * FTC Team 25: cmacfarl, January 19, 2016
 */

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.RobotLog;

public class FourWheelDirectDriveDeadReckon extends DeadReckon {

    DcMotor rearLeft;
    DcMotor rearRight;
    DcMotor frontLeft;
    DcMotor frontRight;

    public FourWheelDirectDriveDeadReckon(Robot robot, int encoderTicksPerInch, GyroSensor gyro,
                                   DcMotor frontRight, DcMotor rearRight, DcMotor frontLeft, DcMotor rearLeft)
    {
        super(robot, encoderTicksPerInch, gyro, rearLeft);

        this.rearLeft = rearLeft;
        this.rearRight = rearRight;
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;

        this.frontRight.setDirection(DcMotor.Direction.REVERSE);
        this.rearRight.setDirection(DcMotor.Direction.REVERSE);
    }

    public FourWheelDirectDriveDeadReckon(Robot robot, int encoderTicksPerInch, double encoderTicksPerDegree,
                                   DcMotor frontRight, DcMotor rearRight, DcMotor frontLeft, DcMotor rearLeft)
    {
        super(robot, encoderTicksPerInch, encoderTicksPerDegree, rearLeft);

        this.rearLeft = rearLeft;
        this.rearRight = rearRight;
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;

        this.frontRight.setDirection(DcMotor.Direction.REVERSE);
        this.rearRight.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    protected void resetEncoders()
    {
        rearLeft.setMode(DcMotor.RunMode.RESET_ENCODERS);
        rearRight.setMode(DcMotor.RunMode.RESET_ENCODERS);
        frontLeft.setMode(DcMotor.RunMode.RESET_ENCODERS);
        frontRight.setMode(DcMotor.RunMode.RESET_ENCODERS);
    }

    @Override
    protected void encodersOn()
    {
        rearLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);
        rearRight.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);
    }

    protected void motorStraight(double speed)
    {
        frontRight.setPower(speed);
        frontLeft.setPower(speed);
        rearRight.setPower(speed);
        rearLeft.setPower(speed);
    }

    @Override
    protected void motorTurn(double speed)
    {
        frontRight.setPower(speed);
        rearRight.setPower(speed);
        frontLeft.setPower(-speed);
        rearLeft.setPower(-speed);
    }

    @Override
    protected void motorSideways(double speed)
    {
        // Unsupported operation.
    }

    @Override
    protected void motorBackLeftDiagonal(double speed) {

    }

    @Override
    protected void motorBackRightDiagonal(double speed) {

    }

    @Override
    protected void motorStop()
    {
        frontLeft.setPower(0.0);
        frontRight.setPower(0.0);
        rearLeft.setPower(0.0);
        rearRight.setPower(0.0);
    }

    @Override
    protected void logEncoderPosition()
    {
        RobotLog.i("Front Left Position: %f", frontLeft.getCurrentPosition());
        RobotLog.i("Rear Left Position: %f", rearLeft.getCurrentPosition());
        RobotLog.i("Front Right Position: %f", frontRight.getCurrentPosition());
        RobotLog.i("Rear Right Position: %f", rearRight.getCurrentPosition());
    }

    @Override
    protected boolean isBusy()
    {
        return (Math.abs(rearLeft.getCurrentPosition()) < Math.abs(rearLeft.getTargetPosition()));
    }
}
