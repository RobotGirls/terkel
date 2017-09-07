package team25core;

import com.qualcomm.robotcore.hardware.DcMotor;

public interface Drivetrain {

    enum PivotSide {
        LEFT_OVER_RIGHT,
        LEFT_OVER_LEFT,
        RIGHT_OVER_RIGHT,
        RIGHT_OVER_LEFT,
    }

    void straight(double speed);

    /**
     * Postitive is to the right, negative is to the left
     */
    void strafe(double speed);

    /**
     * Move forward or back on a diagonal at 315 (forward) / 135 (backward) degrees
     *
     * Obviously only works for mechanum drivetrains
     */
    void leftDiagonal(double speed);

    /**
     * Move forward or back on a diagonal at 45 (forward) / 225 (backward) degrees
     *
     * Obviously only works for mechanum drivetrains
     */
    void rightDiagonal(double speed);

    /**
     * Postitive is to the right, negative is to the left
     */
    void turn(double speed);

    /**
     * Allows a turn around a pivot point that is not the dead center of the drivetrain.
     */
    void pivotTurn(PivotSide side, double speed);
    void setPivotMultiplier(double pivotMultiplier);

    void setPowerLeft(double speed);
    void setPowerRight(double speed);

    /**
     * Full stop, all motors off
     */
    void stop();

    void setMasterMotor(DcMotor motor);
    DcMotor getMasterMotor();

    /**
     * Move the robot according to axial, lateral, and yaw speeds.
     *
     * Note that this works best with Mecanum or Omni drivetrains.
     * YMMV when calling this on drivetrains that use regular wheels.
     */
    void move(double axial, double lateral, double yaw);

    void resetEncoders();
    void encodersOn();
    void logEncoderCounts();

    /**
     * Returns the current encoder count of whatever motor this drivetrain deems to be the master.
     */
    int getCurrentPosition();

    void setTargetInches(double inches);
    void setTargetRotation(double degrees);
    double percentComplete();
    boolean isBusy();
}
