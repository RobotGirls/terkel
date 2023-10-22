package team25core;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by Amelia Martinez on 12/10/2022
 */


public class TwoStickMechanumControlScheme implements JoystickDriveControlScheme {

    /*
     * An Andymark 40 native spin direction is counterclockwise.
     */
    public enum DriveType {
        DRIVE_GEARED,
        DRIVE_DIRECT,
    };

    public enum MotorPosition {
        OUTER_OPPOSED,
        INNER_OPPOSED,
    };

    public enum MotorDirection {
        CANONICAL,
        NONCANONICAL,
    };

    protected double fr;
    protected double fl;
    protected double rr;
    protected double rl;
    protected double leftX;
    protected double rightX;
    protected double leftY;
    protected double rightY;
    protected Gamepad gamepad;
    protected MotorDirection motorDirection;

    public TwoStickMechanumControlScheme(Gamepad gamepad)
    {
        this.gamepad = gamepad;
        this.motorDirection = MotorDirection.CANONICAL;
    }

    public TwoStickMechanumControlScheme(Gamepad gamepad, MotorDirection motorDirection)
    {
        this.gamepad = gamepad;
        this.motorDirection = motorDirection;
    }

    public MotorValues getMotorPowers()
    {
        leftX = gamepad.left_stick_x; // turning
        rightX = gamepad.right_stick_x; // strafing
        leftY = -gamepad.left_stick_y; // unused
        rightY = -gamepad.right_stick_y; // forward and back

        rightX *= 1.1; // recommended by gm0 for strafing correction

        //  The left joystick controls the left and right turns
        //  The right joystick controls forward with the y axis and strafing with the x axis
        //  I then assigned positive and negative for each control and ordered it from forward,
        //  turn, and strafe
        //  lastly I assigned the controls to each wheel

        double scaleFactor = Math.max(Math.abs(rightY) + Math.abs(rightX) + Math.abs(leftX), 1);

        fl = (rightY + leftX + rightX) / scaleFactor;
        fr = (rightY - leftX - rightX) / scaleFactor;
        rl = (rightY + leftX - rightX) / scaleFactor;
        rr = (rightY - leftX + rightX) / scaleFactor;

        return new MotorValues(fl, fr, rl, rr);
    }
}
