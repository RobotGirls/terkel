package team25core;

/*
 * FTC Team 25: cmacfarl, January 23, 2017
 */

public interface Drivetrain {

    enum PivotSide {
        LEFT,
        RIGHT,
    }

    void resetEncoders();
    void encodersOn();
    void straight(double speed);
    void turn(double speed);
    void pivotTurn(PivotSide side, double speed);
    void stop();
}
