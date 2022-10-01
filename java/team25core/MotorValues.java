package team25core;

/**
 * Created by Breanna Chan on 1/6/2018.
 */

public class MotorValues {
    public double fl;
    public double fr;
    public double rl;
    public double rr;

    public MotorValues(double frontLeft, double frontRight, double rearLeft, double rearRight)
    {
        this.fl = frontLeft;
        this.fr = frontRight;
        this.rl = rearLeft;
        this.rr = rearRight;
    }
}
