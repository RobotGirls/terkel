package team25core;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by Breanna Chan on 1/6/2018.
 */


public class TankMechanumControlSchemeBackwards implements JoystickDriveControlScheme {

    protected double fr;
    protected double fl;
    protected double rr;
    protected double rl;
    protected double leftX;
    protected double rightX;
    protected double leftY;
    protected double rightY;
    protected Gamepad gamepad;

    public TankMechanumControlSchemeBackwards(Gamepad gamepad)
    {
        this.gamepad = gamepad;
    }

    public MotorValues getMotorPowers()
    {
        leftX = - gamepad.left_stick_x;
        rightX = - gamepad.right_stick_x;
        leftY = - gamepad.left_stick_y;
        rightY = - gamepad.right_stick_y;

        // If joysticks are pointed left (negative joystick values), counter rotate wheels.
        // Threshold for joystick values in the x may vary.

        if (leftX > 0.5 && rightX > 0.5) {          // sideways left
            fl = -leftX;
            rl = leftX;
            fr = -rightX;
            rr = rightX;
        } else if (leftX < -0.5 && rightX < -0.5) { // sideways right
            fl = -leftX;
            rl = leftX;
            fr = -rightX;
            rr = rightX;
        }/* else if (gamepad.right_trigger > 0.5) {   // backward diagonal to the right
            fr = -1.0;
            rl = 1.0;
        } */ else if (gamepad.left_trigger > 0.5) {    // backward diagonal to the left
            fl = 1.0;
            rr = -1.0;
        } else if (gamepad.left_bumper) {           // forward diagonal to the left
            fr = 1.0;
            rl = -1.0;
        } /*else if (gamepad.right_bumper) {          // forward diagonal to the right
            rr = 1.0;
            fl = -1.0;
        }*/ else {                                    // forward or backward
            fl = leftY;
            rl = leftY;
            fr = -rightY;
            rr = -rightY;
        }

        return new MotorValues(fl, fr, rl, rr);
    }
}
