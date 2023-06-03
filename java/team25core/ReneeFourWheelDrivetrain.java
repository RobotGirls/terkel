package team25core;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class ReneeFourWheelDrivetrain extends DrivetrainBaseImpl implements Drivetrain {
    private DcMotor frontRight;
    private DcMotor backRight;
    private DcMotor frontLeft;
    private DcMotor backLeft;

    public ReneeFourWheelDrivetrain(DcMotor frontRight, DcMotor backRight, DcMotorEx frontLeft, DcMotor backLeft) {
        //this is the constructor and has to be called to set aside memory for the class and to initialize the class

        this.frontLeft = frontRight;
        this.frontRight = backRight;
        this.backLeft = frontLeft;
        this.backRight = backLeft;

        /**
         * Set a default master.  This is the wheel/motor that will be used to track distance
         * travelled when following a dead reckon path.
         */
        setMasterMotor(backRight);

    }

    public void setCanonicalMotorDirection(){
        //switches motor direction
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
    }

@Override
    public void resetEncoders() {
        //sets the motor encoder position to zero
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

@Override
    public void encodersOn() {
        //motor will try to run at target velocity
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }


    public void brakeOnZeroPower() {
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    //This method is used to drive straight either backwards or forwards depending upon the speed
    //where the speed is really the power applied to the motors and valid values are between
    //-1,0, and 1
    @Override
    public void straight(double speed) {
        frontRight.setPower(speed);
        frontLeft.setPower(speed);
        backRight.setPower(speed);
        backLeft.setPower(speed);
    }

@Override
public void strafe(double speed) {double adjSpeed;}

@Override
    public void leftDiagonal(double speed) {
        //Not implemented
        frontRight.setPower(-speed);
        backLeft.setPower(-speed);
    }

@Override
    public void rightDiagonal(double speed) {
        //Not implemented
        frontLeft.setPower(-speed);
        backRight.setPower(-speed);
    }
///**
// * Positive is to the right, negative is to the left
// */

    @Override
    public void turn(double speed) {
        //Not implemented
        frontRight.setPower(speed);
        backRight.setPower(speed);
        frontLeft.setPower(-speed);
        backLeft.setPower(-speed);
    }
/**
 * Allows a turn around a pivot point that is not the dead center of the drivetrain.
 */
@Override
public void pivotTurn(PivotSide side, double speed){

}
@Override
public void setPivotMultiplier(double pivotMultiplier){

}
@Override
public void setPowerLeft(double speed){

}
@Override
public void setPowerRight(double speed){

}
@Override
public void stop() {

    /**
     * Move the robot according to axial, lateral, and yaw speeds.
     *
     * Note that this works best with Mecanum or Omni drivetrains.
     * YMMV when calling this on drivetrains that use regular wheels.
     */
}
@Override
public void move(double axial, double lateral, double yaw){

}
@Override
public void logEncoderCounts() {
}
}
