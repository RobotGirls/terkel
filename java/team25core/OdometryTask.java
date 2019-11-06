package team25core;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class OdometryTask extends RobotTask {

    protected int cL;
    protected int cR;
    protected int cS;
    protected int pL;
    protected int pR;
    protected int pS;
    protected double previousPose;
    protected double radius;

    protected double dLr;
    protected double dRr;
    protected double dL;
    protected double dR;
    protected double dS;
    protected double dEL;
    protected double dER;
    protected double dES;

    protected int sLeft;
    protected int sRight;
    protected int sSide;

    protected int thetaR;
    protected double theta;
    protected double theta1;
    protected double theta0;
    protected double thetaM;
    protected double dTheta;
    protected double dGlobalOffset;
    protected double translation0;

    protected double pose;
    protected double pose_r;
    protected double x;
    protected double y;
    protected double localOffset;
    protected double position_x;
    protected double position_y;
    protected double newAbsolutePosition;

    DcMotor twLeft;
    DcMotor twRight;
    DcMotor twSide;

    Telemetry.Item leftEncoderTelemetryItem;
    Telemetry.Item rightEncoderTelemetryItem;
    Telemetry.Item sideEncoderTelemetryItem;

    private final static int ENC_TICK_PER_REV = 1440;
    private final static int WHEEL_DIAMETER = 4;
    private final static int GLOBAL_ORIENTATION = 0;

    public OdometryTask(Robot robot, DcMotor twLeft, DcMotor twRight, DcMotor twBack, int sLeft, int sRight, int sSide)
    {
        super(robot);
        this.twLeft = twLeft;
        this.twRight = twRight;
        this.twSide = twBack;

        this.leftEncoderTelemetryItem = robot.telemetry.addData("Left Encoder", "0");
        this.rightEncoderTelemetryItem = robot.telemetry.addData("Right Encoder", "0");
        this.sideEncoderTelemetryItem = robot.telemetry.addData("Side Encoder", "0");

        this.sLeft = sLeft;
        this.sRight = sRight;
        this.sSide = sSide;

        // We are considering that the global orientation at the last reset is 0
        this.thetaR = 0;

        // We are considering that the global translation at the last reset is 0
        this.translation0 = 0.0;

        this.dTheta = 0.0;
    }

    private double calculateLocalOffset(double dS, double dR)
    {
        localOffset = dS + dR;

        return localOffset;
    }

    // Complete local translation vector (step 8)
    private void calculatePosition(double dS, double dR)
    {
        x = (dS / pose) + sSide;
        y = (dR / pose) + sRight;
        position_x = 2 * Math.sin(pose / 2) * x;
        position_y = 2 * Math.sin(pose / 2) * y;
    }

    private double calculatePose(double dL, double dR)
    {
        pose = GLOBAL_ORIENTATION + ((dL - dR) / (sLeft + sRight));

        return pose;
    }

    private double calculateResetPose(double dLr, double dRr)
    {
        pose_r = GLOBAL_ORIENTATION + ((dLr - dRr) / (sLeft + sRight));

        return pose_r;
    }

    private double calculateDistance(double delta)
    {
        return (delta / ENC_TICK_PER_REV) * WHEEL_DIAMETER;
    }

    private double calculatePolarCoord()
    {
        radius = Math.sqrt((Math.pow(x, 2) + Math.pow(y, 2)));
        theta = Math.atan(y / x);

        return theta;
    }

    private double calculateCartesianCoord()
    {
        x = WHEEL_DIAMETER * Math.cos(position_x);
        y = WHEEL_DIAMETER * Math.sin(position_y);

        return x;
    }

    private double calculateGlobalOffset()
    {
        calculatePolarCoord();
        dGlobalOffset = localOffset - thetaM;
        calculateCartesianCoord();

        return dGlobalOffset;
    }

    @Override
    public void start()
    {
    }

    @Override
    public void stop()
    {
        robot.removeTask(this);
    }

    @Override
    public boolean timeslice()
    {
        // Storing current encoder values in local variables (step 1)
        cL = twLeft.getCurrentPosition();
        cR = twRight.getCurrentPosition();
        cS = twSide.getCurrentPosition();

        leftEncoderTelemetryItem.setValue(cL);
        rightEncoderTelemetryItem.setValue(cR);
        sideEncoderTelemetryItem.setValue(cS);

        dEL = cL - pL;
        dER = cR - pR;
        dES = cS - pS;

        dR = calculateDistance(dER);
        dL = calculateDistance(dEL);
        dS = calculateDistance(dES);

        // Right wheel travel and Left wheel travel since last reset
        dRr = calculateDistance(cL);
        dLr = calculateDistance(cR);

        //dRr.getWheelDistance;

        //Arc Angle or change in orientation of the robot (step 2)
        pose  = calculatePose(dL, dR);

        pose_r = calculateResetPose(dLr, dRr);

        //New absolute orientation (step 6)
        theta1 = thetaR + pose_r;

        localOffset = calculateLocalOffset(dES, dER);

        // Local offset (step 7 - step 8)
        if (dL == dR) {
            calculateLocalOffset(dS, dR);
        } else {
            calculatePosition(dS, dR);
        }

        // We calculate thetaM to find the average orientation (step 9)
        thetaM = theta0 + (pose / 2);

        calculateGlobalOffset();

        // New absolute position (step 11)
        newAbsolutePosition = translation0 + localOffset;

        // We update the positions of the tracking wheels to update encoder values since the last reset (step 3)
        pL = cL;
        pR = cR;
        pS = cS;

        // We calculate deltaTheta by assigning theta0 to previousPose (step 6)
        previousPose = pose;
        pose = theta0;
        dTheta = theta1 - previousPose;

        robot.telemetry.update();

        return false;
    }
}


