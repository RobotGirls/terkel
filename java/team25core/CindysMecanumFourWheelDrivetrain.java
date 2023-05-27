package team25core;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.Map;

public class CindysMecanumFourWheelDrivetrain extends DrivetrainBaseImpl implements Drivetrain {

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    // this is the constructor and must be called in order to set aside memory for this
    // class and to do some initialization for the class
    public CindysMecanumFourWheelDrivetrain(DcMotor pfrontRight, DcMotor pbackRight,
                                            DcMotor pfrontLeft, DcMotor pbackLeft)
    {
	// Calls the parent class constructor, in this case DrivetrainBaseImpl
        super();

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

    @Override
    public void logEncoderCounts()
    {
        //RobotLog.i("POS Counts FL %d, FR %d, RL %d, RR %d", frontLeft.getCurrentPosition(), frontRight.getCurrentPosition(), backLeft.getCurrentPosition(), backRight.getCurrentPosition());
    }

    @Override
    public void move(double axial, double lateral, double yaw)
    {
    }
    
    @Override
    public void stop()
    {
        frontLeft.setPower(0.0);
        frontRight.setPower(0.0);
        backLeft.setPower(0.0);
        backRight.setPower(0.0);
    }

    @Override
    public void setPowerRight(double speed)
    {
        frontRight.setPower(speed);
        backRight.setPower(speed);
    }

    @Override
    public void setPowerLeft(double speed)
    {
        frontLeft.setPower(speed);
        backLeft.setPower(speed);
    }

    @Override
    public void setPivotMultiplier(double pivotMultiplier)
    {

    }

    @Override
    public void pivotTurn(PivotSide side, double speed)
    {
    }
    
    @Override
    public void turn(double speed)
    {   
        frontRight.setPower(-speed);
        backRight.setPower(-speed);
        frontLeft.setPower(speed);
        backLeft.setPower(speed);
    }

    
    @Override
    public void leftDiagonal(double speed)
    {
        // Not implemented
        frontRight.setPower(-speed);
        backLeft.setPower(-speed);
    }
    
    @Override
    public void rightDiagonal(double speed)
    {   
        // Not implemented
        frontLeft.setPower(-speed);
        backRight.setPower(-speed);
    }

    @Override
    public void strafe(double speed)
    {
        double adjSpeed;

        // FIXME
        //adjSpeed = setAdjustedPower(motorMap.get(MotorPackage.MotorLocation.FRONT_RIGHT), -speed);
        //adjSpeed = setAdjustedPower(motorMap.get(MotorPackage.MotorLocation.BACK_RIGHT), speed);
        //adjSpeed = setAdjustedPower(motorMap.get(MotorPackage.MotorLocation.FRONT_LEFT), speed);
        //adjSpeed = setAdjustedPower(motorMap.get(MotorPackage.MotorLocation.BACK_LEFT), -speed);
    }


    @Override
    public void straight(double speed)
    {   
        frontRight.setPower(speed);
        frontLeft.setPower(speed);
        backRight.setPower(speed);
        backLeft.setPower(speed);
    }




}
