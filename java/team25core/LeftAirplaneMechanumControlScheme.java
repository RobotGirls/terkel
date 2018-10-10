package team25core;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by Breanna Chan on 1/13/2018.
 */

public class LeftAirplaneMechanumControlScheme implements JoystickDriveControlScheme {

    protected double fr;
    protected double fl;
    protected double rr;
    protected double rl;
    protected double leftX;
    protected double rightX;
    protected double leftY;
    protected double rightY;
    protected Gamepad gamepad;

    /**
     * Left joystick will be used for forward, backwards, and sideways,
     * while the right joystick will be used for turning.
     */

    public LeftAirplaneMechanumControlScheme(Gamepad gamepad)
    {
        this.gamepad = gamepad;
    }

    public MotorValues getMotorPowers()
    {
        leftX = gamepad.left_stick_x;
        rightX = gamepad.right_stick_x;
        leftY = gamepad.left_stick_y;
        rightY = gamepad.right_stick_y;

        // If joysticks are pointed left (negative joystick values), counter rotate wheels.
        // Threshold for joystick values in the x may vary.

        if (leftX == 1.0 && leftY <= 0.2 && leftY >= -0.2) {                   // sideways right
            fl = -leftX;
            rl = leftX;
            fr = -leftX;
            rr = leftX;
        } else if (leftX == -1.0 && leftY <= 0.2 && leftY >= -0.2) {           // sideways left
            fl = -leftX;
            rl = leftX;
            fr = -leftX;
            rr = leftX;
        } else if (leftX > 0 && leftX < 1.0 && leftY > 0.2 && leftY < 1.0) {   // backward diagonal to the right
            fr = -1.0;
            rl = 1.0;
        } else if (leftX > -1.0 && leftX < 0 && leftY > 0.2 && leftY < 1.0) {  // backward diagonal to the left
            fl = 1.0;
            rr = -1.0;
        } else if (leftX > -1.0 && leftX < 0 && leftY < 0.2 && leftY > -1.0) { // forward diagonal to the left
            fr = 1.0;
            rl = -1.0;
        } else if (leftX > 0 && leftX < 1.0 && leftY < 0.2 && leftY > -1.0) {  // forward diagonal to the right
            rr = 1.0;
            fl = -1.0;
        } else if (rightX > 0) {                                               // rotate right
            fl = -rightX;
            rl = -rightX;
            fr = -rightX;
            rr = -rightX;
        } else if (rightX < 0) {                                               // rotate left
            fl = -rightX;
            rl = -rightX;
            fr = -rightX;
            rr = -rightX;
        } else {                                                               // forward or backward
            fl = leftY;
            rl = leftY;
            fr = -leftY;
            rr = -leftY;
        }

        return new MotorValues(fl, fr, rl, rr);
    }
}
