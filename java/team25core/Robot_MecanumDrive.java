package team25core;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;

/**
 * This is NOT an opmode.
 *
 * This class defines all the specific hardware for a three wheel omni-bot.
 *
 * This hardware class assumes the following device names have been configured on the robot:
 * Note:  All names are lower case and some have single spaces between words.
 *
 * Motor channel:  Left  drive motor:        "left drive"
 * Motor channel:  Right drive motor:        "right drive"
 * Motor channel:  Rear  drive motor:        "back drive"
 *
 * These motors correspond to three drive locations spaced 120 degrees around a circular robot.
 * Each motor is attached to an omni-wheel. Two wheels are in front, and one is at the rear of the robot.
 *
 * Robot motion is defined in three different axis motions:
 * - Axial    Forward/Backwards      +ve = Forward
 * - Lateral  Side to Side strafing  +ve = Right
 * - Yaw      Rotating               +ve = CCW
 */


public class Robot_MecanumDrive implements Robot_Drivetrain
{
    // Private Members
    private Robot myOpMode;

    private DcMotor  leftFront      = null;
    private DcMotor  rightFront     = null;
    private DcMotor  leftRear       = null;
    private DcMotor  rightRear      = null;

    private double  driveAxial      = 0 ;   // Positive is forward
    private double  driveLateral    = 0 ;   // Positive is right
    private double  driveYaw        = 0 ;   // Positive is CCW

    /* Constructor */
    public Robot_MecanumDrive(DcMotor leftFront, DcMotor rightFront, DcMotor leftRear, DcMotor rightRear){
        this.leftFront = leftFront;
        this.rightFront = rightFront;
        this.leftRear = leftRear;
        this.rightRear = rightRear;
    }


    /* Initialize standard Hardware interfaces */
    public void initDrive(Robot opMode) {

        // Save reference to Hardware map
        myOpMode = opMode;

        // Define and Initialize Motors
        leftFront.setDirection(DcMotor.Direction.FORWARD); // Positive input rotates counter clockwise
        rightFront.setDirection(DcMotor.Direction.REVERSE);// Positive input rotates counter clockwise
        leftRear.setDirection(DcMotor.Direction.FORWARD); // Positive input rotates counter clockwise
        rightRear.setDirection(DcMotor.Direction.REVERSE); // Positive input rotates counter clockwise

        //use RUN_USING_ENCODERS because encoders are installed.
        setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Stop all robot motion by setting each axis value to zero
        moveRobot(0,0,0) ;
    }

    public void manualDrive()  {
        // In this mode the Left stick moves the robot fwd & back, and Right & Left.
        // The Right stick rotates CCW and CW.

        //  (note: The joystick goes negative when pushed forwards, so negate it)
        setAxial(-myOpMode.gamepad1.left_stick_y);
        setLateral(myOpMode.gamepad1.left_stick_x);
        setYaw(-myOpMode.gamepad1.right_stick_x);
    }


    /***
     * void moveRobot(double axial, double lateral, double yaw)
     * Set speed levels to motors based on axes requests
     * @param axial     Speed in Fwd Direction
     * @param lateral   Speed in lateral direction (+ve to right)
     * @param yaw       Speed of Yaw rotation.  (+ve is CCW)
     */
    public void moveRobot(double axial, double lateral, double yaw) {
        setAxial(axial);
        setLateral(lateral);
        setYaw(yaw);
        moveRobot();
    }

    public void moveAxially()
    {
        setLateral(0);
        setYaw(0);
        moveRobot();
    }

    public void moveLaterally()
    {
        setAxial(0);
        setYaw(0);
        moveRobot();
    }

    public void moveRotationally()
    {
        setLateral(0);
        setAxial(0);
        moveRobot();
    }

    /***
     * void moveRobot()
     * This method will calculate the motor speeds required to move the robot according to the
     * speeds that are stored in the three Axis variables: driveAxial, driveLateral, driveYaw.
     * This code is setup for a three wheeled OMNI-drive but it could be modified for any sort of omni drive.
     *
     * The code assumes the following conventions.
     * 1) Positive speed on the Axial axis means move FORWARD.
     * 2) Positive speed on the Lateral axis means move RIGHT.
     * 3) Positive speed on the Yaw axis means rotate COUNTER CLOCKWISE.
     *
     * This convention should NOT be changed.  Any new drive system should be configured to react accordingly.
     */
    public void moveRobot() {
        // calculate required motor speeds to acheive axis motions
        double backLeft = driveAxial - driveLateral - driveYaw;
        double backRight = driveAxial + driveLateral + driveYaw;
        double left = driveAxial + driveLateral - driveYaw;
        double right = driveAxial - driveLateral + driveYaw;

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
        // Set drive motor power levels.
        leftRear.setPower(backLeft);
        rightRear.setPower(backRight);
        leftFront.setPower(left);
        rightFront.setPower(right);

        // Display Telemetry
        RobotLog.i("141 Axes A[%+5.2f], L[%+5.2f], Y[%+5.2f]", driveAxial, driveLateral, driveYaw);
        RobotLog.i("141 Wheels L[%+5.2f], R[%+5.2f], BL[%+5.2f], BR[%+5.2f]", left, right, backLeft, backRight);
    }


    public void setAxial(double axial)      {driveAxial = Range.clip(axial, -1, 1);}
    public void setLateral(double lateral)  {driveLateral = Range.clip(lateral, -1, 1); }
    public void setYaw(double yaw)          {driveYaw = Range.clip(yaw, -1, 1); }


    /***
     * void setMode(DcMotor.RunMode mode ) Set all drive motors to same mode.
     * @param mode    Desired Motor mode.
     */
    public void setMode(DcMotor.RunMode mode ) {
        leftFront.setMode(mode);
        rightFront.setMode(mode);
        leftRear.setMode(mode);
        rightRear.setMode(mode);
    }

    public void rotateRobot(double speed)
    {
        if (driveYaw < 0) {
            leftRear.setPower(-speed);
            leftFront.setPower(-speed);
            rightRear.setPower(speed);
            rightFront.setPower(speed);

        } else {
            leftRear.setPower(speed);
            leftFront.setPower(speed);
            rightRear.setPower(-speed);
            rightFront.setPower(-speed);
        }
    }

    public void stopRobot()
    {
        rotateRobot(0);
    }

}

