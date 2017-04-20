package team25core;

/*
 * Created by izzielau on 1/6/2017.
 */

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.RobotLog;

public class FourWheelPivotTurnDeadReckon extends DeadReckon {

    public enum TurningSide {
        LEFT,
        RIGHT,
    }

    private double pivotMultiplier;
    private TurningSide turningSide;

    DcMotor frontRightMotor;
    DcMotor frontLeftMotor;
    DcMotor rearRightMotor;
    DcMotor rearLeftMotor;
    MonitorMotorTask mmt;

    /*
     * Assumes that both motors are on the same controller.
     */
    public FourWheelPivotTurnDeadReckon(Robot robot, int encoderTicksPerInch, GyroSensor gyroSensor, DcMotor motorLeftFront, DcMotor motorRightFront, DcMotor motorLeftRear, DcMotor motorRightRear)
    {
        super(robot, encoderTicksPerInch, gyroSensor, motorLeftFront);

        this.frontRightMotor = motorRightFront;
        this.frontLeftMotor = motorLeftFront;
        this.rearRightMotor = motorRightRear;
        this.rearLeftMotor = motorLeftRear;
        this.mmt = null;

        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        rearRightMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    public FourWheelPivotTurnDeadReckon(Robot robot, int encoderTicksPerInch, int encoderTicksPerDegree, DcMotor motorRightFront, DcMotor motorRightRear, DcMotor motorLeftFront, DcMotor motorLeftRear)
    {
        super(robot, encoderTicksPerInch, encoderTicksPerDegree, motorLeftFront);

        this.frontRightMotor = motorRightFront;
        this.frontLeftMotor = motorLeftFront;
        this.rearRightMotor = motorRightRear;
        this.rearLeftMotor = motorLeftRear;
        this.mmt = null;

        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        rearRightMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    public void setMultiplierSide(double multiplier, TurningSide side)
    {
        turningSide = side;
        pivotMultiplier = multiplier;
    }

    @Override
    protected void resetEncoders()
    {
        frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rearLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rearRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    protected void encodersOn()
    {
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rearLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rearRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    protected void motorStraight(double speed)
    {
        frontLeftMotor.setPower(speed);
        frontRightMotor.setPower(speed);
        rearLeftMotor.setPower(speed);
        rearRightMotor.setPower(speed);
    }

    @Override
    protected void motorTurn(double speed)
    {
        if (turningSide == TurningSide.RIGHT) {
            frontLeftMotor.setPower(speed);
            rearLeftMotor.setPower(speed);
            frontRightMotor.setPower(-(1/pivotMultiplier) * speed);
            rearRightMotor.setPower(-(1/pivotMultiplier) * speed);
        } else if (turningSide == TurningSide.LEFT) {
            frontLeftMotor.setPower(-(1/pivotMultiplier) * speed);
            rearLeftMotor.setPower(-(1/pivotMultiplier) * speed);
            frontRightMotor.setPower(speed);
            rearRightMotor.setPower(speed);
        } else {
            RobotLog.e("251 Pivot turn, no side seected");
        }
    }

    @Override

    protected void motorSideways(double speed)
    {
        // Only supported on a Mecanum drive.
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
        if (mmt != null) {
            mmt.stop();
        }
        RobotLog.i("251 Stopping motors");
        motorStraight(0.0);
    }

    @Override
    protected void logEncoderPosition() {

    }

    @Override
    protected boolean isBusy()
    {
        boolean busy = frontLeftMotor.isBusy();

        if (!busy) {
            motorStop();
        }

        return (busy);
    }
}
