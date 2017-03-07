package team25core;

/*
 * FTC Team 25: cmacfarl, January 23, 2017
 */

public interface Drivetrain {

    enum PivotSide {
        LEFT_OVER_RIGHT,
        LEFT_OVER_LEFT,
        RIGHT_OVER_RIGHT,
        RIGHT_OVER_LEFT,
    }

    void straight(double speed);
    void turnLeft(double speed);
    void turnRight(double speed);
    void pivotTurn(PivotSide side, double speed);
    void stop();

    /*
     * Move the robot according to axial, lateral, and yaw speeds.
     *
     * Note that this works best with Mecanum or Omni drivetrains.
     * YMMW when calling this on drivetrains that use regular wheels.
     */
    void move(double axial, double lateral, double yaw);

    /*
     * Move the robot sideways in either direction.  This will not
     * work correctly on drivetrains without Mecanum or Omni wheels.
     */
    void strafeLeft(double speed);
    void strafeRight(double speed);

    void resetEncoders();
    void encodersOn();
    void logEncoderCounts();

    void setTargetInches(int inches);
    double percentComplete();
    boolean isBusy();
}
