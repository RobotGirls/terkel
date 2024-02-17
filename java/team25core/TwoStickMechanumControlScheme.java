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

    public enum StickOrientation {
        TRANSLATE_ON_LEFT,
        TRANSLATE_ON_RIGHT
    }

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
    protected double x;
    protected double y;
    protected double yaw;
    protected double rightY;
    protected Gamepad gamepad;
    protected MotorDirection motorDirection;

    protected StickOrientation stickOrientation;

    public TwoStickMechanumControlScheme(Gamepad gamepad)
    {
        this.gamepad = gamepad;
        this.motorDirection = MotorDirection.CANONICAL;
        this.stickOrientation = StickOrientation.TRANSLATE_ON_LEFT;
    }

    public TwoStickMechanumControlScheme(Gamepad gamepad, StickOrientation stickOrientation)
    {
        this.gamepad = gamepad;
        this.motorDirection = MotorDirection.CANONICAL;
        this.stickOrientation = stickOrientation;
    }

    public TwoStickMechanumControlScheme(Gamepad gamepad, MotorDirection motorDirection)
    {
        this.gamepad = gamepad;
        this.motorDirection = motorDirection;
    }

    public MotorValues getMotorPowers()
    {
        if (stickOrientation == StickOrientation.TRANSLATE_ON_LEFT) {
            x = gamepad.left_stick_x; // turning
            y = -gamepad.left_stick_y; // unused
            yaw = gamepad.right_stick_x; // strafing
            rightY = -gamepad.right_stick_y; // forward and back
        } else {
            x = gamepad.right_stick_x; // turning
            y = -gamepad.right_stick_y; // unused
            yaw = gamepad.left_stick_x; // strafing
            rightY = -gamepad.left_stick_y; // forward and back
        }

        yaw *= 1.1; // recommended by gm0 for strafing correction

        //  The left joystick controls the left and right turns
        //  The right joystick controls forward with the y axis and strafing with the x axis
        //  I then assigned positive and negative for each control and ordered it from forward,
        //  turn, and strafe
        //  lastly I assigned the controls to each wheel

        double scaleFactor = Math.max(Math.abs(y) + Math.abs(yaw) + Math.abs(x), 1);

        fl = (y + x + yaw) / scaleFactor;
        fr = (y - x - yaw) / scaleFactor;
        rl = (y - x + yaw) / scaleFactor;
        rr = (y + x - yaw) / scaleFactor;

        return new MotorValues(fl, fr, rl, rr);
    }
}
