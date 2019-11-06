package team25core;

public class OdometryRobotPosition extends OdometryTask{

    private final static int GLOBAL_ORIENTATION = 0;
    private final static int ENC_TICK_PER_REV = 1440;
    private final static int WHEEL_DIAMETER = 4;

    public double getOdometryPose()
    {
        pose = GLOBAL_ORIENTATION + ((dL - dR) / (sLeft + sRight));
        return pose;
    }

    public void getOdometryPosition()
    {
        x = (dS / pose) + sSide;
        y = (dR / pose) + sRight;
        position_x = 2 * Math.sin(pose / 2) * x;
        position_y = 2 * Math.sin(pose / 2) * y;

    }

    public double getResetPose()
    {
        pose_r = GLOBAL_ORIENTATION + ((dLr - dRr) / (sLeft + sRight));

        return pose_r;
    }

    public double getLocalOffset()
    {
        localOffset = dS + dR;

        return localOffset;
    }

    public double getWheelDistance(double delta)
    {
        return (delta / ENC_TICK_PER_REV) * WHEEL_DIAMETER;
    }


}
