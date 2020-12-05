
package team25core;

import com.qualcomm.robotcore.hardware.Gamepad;

import team25core.JoystickDriveControlScheme;
import team25core.MotorValues;

/**
 * Created by Breanna Chan on 1/6/2018.
 */


public class TankMechanumControlSchemeReverse implements JoystickDriveControlScheme {

    protected double fr;
    protected double fl;
    protected double rr;
    protected double rl;
    protected double leftX;
    protected double rightX;
    protected double leftY;
    protected double rightY;
    protected Gamepad gamepad;

    protected double leftWheelForward;
    protected double leftWheelBackward;
    protected double rightWheelForward;
    protected double rightWheelBackward;


    public TankMechanumControlSchemeReverse(Gamepad gamepad)
    {
        this.gamepad = gamepad;
    }

    public MotorValues getMotorPowers()
    {
        leftX = gamepad.left_stick_x;
        rightX = gamepad.right_stick_x;
        leftY = gamepad.left_stick_y;
        rightY = gamepad.right_stick_y;

        leftWheelForward   = -1;
        leftWheelBackward  = 1;
        rightWheelForward  = 1;
        rightWheelBackward = -1;

        // If joysticks are pointed left (negative joystick values), counter rotate wheels.
        // Threshold for joystick values in the x may vary.

        if (leftX > 0.5 && rightX > 0.5) {          // both joy-sticks sideways left
            //fl = -leftX;
            //rl = leftX;
            //fr = -rightX;
            //rr = rightX;
            fl = leftWheelBackward; //front left wheel goes backward
            rl = leftWheelForward; //rear left wheel goes forward
            fr = rightWheelForward; //front right wheel goes forward
            rr = rightWheelBackward; //rear right wheel goes backward
        } else if (leftX < -0.5 && rightX < -0.5) { // both joy-sticks sideways right
            //fl = -leftX;
            //rl = leftX;
            //fr = -rightX;
            //rr = rightX;
            fl = leftWheelForward; //front left goes forward
            rl = leftWheelBackward; //rear left goes backward
            fr = rightWheelBackward; //front right goes backward
            rr = rightWheelForward; //rear right goes forward
        } else if (gamepad.right_trigger > 0.5) {   // backward diagonal to the right
            //fr = -1.0;
            //rl = 1.0;
            fr = rightWheelBackward;
            rl = leftWheelBackward;
        } else if (gamepad.left_trigger > 0.5) {    // backward diagonal to the left
            //fl = 1.0;
            //rr = -1.0;
            fl = leftWheelBackward;
            rr = rightWheelBackward;
        } else if (gamepad.left_bumper) {           // forward diagonal to the left
            //fr = 1.0;
            //rl = -1.0;
            fr = rightWheelForward;
            rl = leftWheelForward;
        } else if (gamepad.right_bumper) {          // forward diagonal to the right
            //fl = -1.0;
            //rr = 1.0;
            fl = leftWheelForward;
            rr = rightWheelForward;
        } else {                                    // forward or backward
            fl = -leftY;
            rl = -leftY;
            //fr = -rightY;
            //rr = -rightY;
            fr = rightY;
            rr = rightY;
        }

        return new MotorValues(fl, fr, rl, rr);
    }
}
