package team25core;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class KatelynsFourWheelDrivetrain extends DrivetrainBaseImpl implements Drivetrain {
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    public KatelynsFourWheelDrivetrain(DcMotor pfrontLeft, DcMotor pfrontRight, DcMotor pbackLeft, DcMotor pbackRight) {
        // this is the constructor & has to be called to set aside memory for the class and to initialize the class

        super();

        this.frontLeft = pfrontLeft;
        this.frontRight = pfrontRight;
        this.backLeft = pbackLeft;
        this.backRight = pbackRight;

        /**
         * Set a default master.  This is the wheel/motor that will be used to track distance
         * travelled when following a dead reckon path.
         */
        setMasterMotor(backRight);
    }

    // call this method only if the robot is going in the opposite direction from intended
    public void setCanonicalMotorDirection() {
        // switch motor direction
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public void resetEncoders() {
        // Sets motor encoder position to 0
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    public void encodersOn() {
        // motor will try to run at the targeted velocity
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void brakeOnZeroPower() {
        // Sets the behavior of the motor when a power level of zero is applied i.e. not moving - when we apply 0 power, the motor brakes
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    // this method is used to drive straight (either backwards or forwards, depending on speed, where speed is really the power applied to the motor. Valid values between -1.0 and 1.0)
    @Override
    public void straight(double speed) {
        frontRight.setPower(speed);
        frontLeft.setPower(speed);
        backRight.setPower(speed);
        backLeft.setPower(speed);
    }

    @Override
    public void strafe(double speed) {
        double adjSpeed;
    }

    public void leftDiagonal(double speed) {
        frontRight.setPower(-speed);
        backLeft.setPower(-speed);
    }
    @Override
    public void rightDiagonal(double speed) {
        frontLeft.setPower(-speed);
        backRight.setPower(-speed);
    }
    @Override
    public void turn(double speed) {

    }
    @Override
    public void pivotTurn(PivotSide side, double speed) {

    }
    @Override
    public void setPivotMultiplier(double pivotMultiplier) {

    }
    @Override
    public void setPowerLeft(double speed) {

    }
    @Override
    public void setPowerRight(double speed) {

    }
    @Override
    public void stop() {

    }
    @Override
    public void move(double axial, double lateral, double yaw) {

    }
    @Override
    public void logEncoderCounts() {

    }

}
