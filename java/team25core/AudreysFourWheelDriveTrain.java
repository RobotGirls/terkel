package team25core;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class AudreysFourWheelDriveTrain extends DrivetrainBaseImpl {

    private DcMotor frontLeft;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor frontRight;

    public AudreysFourWheelDriveTrain(DcMotor frontLeft, DcMotor frontRight, DcMotor backLeft, DcMotor backRight){

        this.frontLeft = frontLeft;
        this.backLeft = backLeft;
        this.backRight = backRight;
        this.frontRight = frontRight;

        /**
         * Set a default master.  This is the wheel/motor that will be used to track distance
         * travelled when following a dead reckon path.
         */
        setMasterMotor(backRight);
    }

    //method makes sure the motors are not reversed
    public void setCannonicalMotorDirection(){
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);

    }


    public void resetEncoders(){
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }


    public void encodersOn(){
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }


    public void brakeOnZeroPower(){
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    //@Override
    public void strafe(double speed)
    {
        frontRight.setPower(-speed);
        backRight.setPower(speed);
        frontLeft.setPower(speed);
        backLeft.setPower(-speed);
    }

    //@Override
    /*this method allows robot to drive backwards or forward upon the speed; where the speed
    is the power applied to the motors*/
    public void straight(double speed)
    {
        frontRight.setPower(speed);
        frontLeft.setPower(speed);
        backRight.setPower(speed);
        backLeft.setPower(speed);
    }
}
