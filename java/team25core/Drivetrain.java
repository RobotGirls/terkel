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

    void resetEncoders();
    void encodersOn();
    void straight(double speed);
    void turnLeft(double speed);
    void turnRight(double speed);
    void pivotTurn(PivotSide side, double speed);
    void stop();
}
