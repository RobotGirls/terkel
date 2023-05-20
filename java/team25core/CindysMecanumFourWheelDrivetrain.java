package team25core;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class CindysMecanumFourWheelDrivetrain extends DrivetrainBaseImpl {

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    // this is the constructor and must be called in order to set aside memory for this
    // class and to do some initialization for the class
    public CindysMecanumFourWheelDrivetrain(DcMotor pfrontRight, DcMotor pbackRight,
                                            DcMotor pfrontLeft, DcMotor pbackLeft) {
        this.frontRight = pfrontRight;
        this.backRight = pbackRight;
        this.frontLeft = pfrontLeft;
        this.backLeft = pbackLeft;

        setCanonicalMotorDirection();

    }

    // Call this method only if the robot is going in the opposite
    // direction from which you expect it to go
    public void setCanonicalMotorDirection() {
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public void resetEncoders() {
        // Sets the motor encoder position to zero
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }

    @Override
    public void encodersOn() {
        // motor will try to run at targeted velocity
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void brakeOnZeroPower() {
        // sets the behavior when a power level of 0 is applied (i.e., the
        // motor is not moving) then we apply the brakes.
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }
}
