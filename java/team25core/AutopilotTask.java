package team25core;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/*
 * Autopilot lanekeeping.
 *
 * Use rotation around an axis from an imu to ensure that a robot moves in a straight line.
 *
 * Clockwise produces negative values.
 * Counterclockwise produces positive values.
 *
 * PID
 *
 * yaw : The yaw angle
 * target : The desired yaw angle (most likely zero)
 * error : target - yaw
 * integral : integral + error
 * derivative : error - last_error
 *
 * Axis: For a Control Hub laying flat use the Z axis.
 */
public class AutopilotTask extends RobotTask {

    public enum TrackingAxis {
        AXIS_X,
        AXIS_Y,
        AXIS_Z,
    };

    public enum AutoPilotDirection {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT,
    };

    RobotGyro gyro;

    TrackingAxis axis;
    AutoPilotDirection direction;
    Map<MotorPackage.MotorLocation, MotorPackage> motorMap;

    private final static String TAG = "AutopilotTask";
    private final static boolean DEBUG = true;

    double yaw;
    double target;
    double error;
    double integral;
    double derivative;
    double last_error;
    double correction;

    double right;
    double left;
    double targetSpeed;
    double polarity;

    double kI;
    double kP;
    double kD;

    Telemetry.Item yawTelemetry;
    Telemetry.Item errorTelemetry;
    Telemetry.Item integralTelemetry;
    Telemetry.Item derivativeTelemetry;
    Telemetry.Item correctionTelemetry;
    Telemetry.Item lastSpeedTelemetry;

    Telemetry.Item rightTelemetry;
    Telemetry.Item leftTelemetry;

    public AutopilotTask(Robot robot, RobotGyro gyro, TrackingAxis axis, AutoPilotDirection direction, Map<MotorPackage.MotorLocation, MotorPackage> motorMap)
    {
        super(robot);

        this.gyro = gyro;
        this.axis = axis;
        this.direction = direction;
        this.motorMap = motorMap;
        this.target = 0.0;
        this.polarity = -1.0;

        if (DEBUG) {
            rightTelemetry = robot.telemetry.addData("right", 0);
            leftTelemetry = robot.telemetry.addData("left", 0);
            yawTelemetry = robot.telemetry.addData("yaw", 0);
            errorTelemetry = robot.telemetry.addData("error", 0);
            integralTelemetry = robot.telemetry.addData("integral", 0);
            derivativeTelemetry = robot.telemetry.addData("derivative", 0);
            correctionTelemetry = robot.telemetry.addData("correction", 0);
            lastSpeedTelemetry = robot.telemetry.addData("last speed", 0);
        }

        if (direction == AutoPilotDirection.RIGHT) {
            remapStrafeRight();
        } else if (direction == AutoPilotDirection.LEFT) {
            remapStrafeLeft();
        }
    }

    public void remapStrafeRight()
    {
        DcMotor frontRight = motorMap.get(MotorPackage.MotorLocation.FRONT_RIGHT).motor;
        DcMotor frontLeft = motorMap.get(MotorPackage.MotorLocation.FRONT_LEFT).motor;
        DcMotor backRight = motorMap.get(MotorPackage.MotorLocation.BACK_RIGHT).motor;
        DcMotor backLeft = motorMap.get(MotorPackage.MotorLocation.BACK_LEFT).motor;

        motorMap.put(MotorPackage.MotorLocation.FRONT_RIGHT, new MotorPackage(backRight));
        motorMap.put(MotorPackage.MotorLocation.FRONT_LEFT, new MotorPackage(frontRight));
        motorMap.put(MotorPackage.MotorLocation.BACK_RIGHT, new MotorPackage(backLeft));
        motorMap.put(MotorPackage.MotorLocation.BACK_LEFT, new MotorPackage(frontLeft));
    }

    public void remapStrafeLeft()
    {
        DcMotor frontRight = motorMap.get(MotorPackage.MotorLocation.FRONT_RIGHT).motor;
        DcMotor frontLeft = motorMap.get(MotorPackage.MotorLocation.FRONT_LEFT).motor;
        DcMotor backRight = motorMap.get(MotorPackage.MotorLocation.BACK_RIGHT).motor;
        DcMotor backLeft = motorMap.get(MotorPackage.MotorLocation.BACK_LEFT).motor;

        motorMap.put(MotorPackage.MotorLocation.FRONT_RIGHT, new MotorPackage(frontLeft));
        motorMap.put(MotorPackage.MotorLocation.FRONT_LEFT, new MotorPackage(backLeft));
        motorMap.put(MotorPackage.MotorLocation.BACK_RIGHT, new MotorPackage(frontRight));
        motorMap.put(MotorPackage.MotorLocation.BACK_LEFT, new MotorPackage(backRight));
    }

    /*
     * start
     *
     * The drivetrain should be moving before we start the autopilot task such that
     * the target speed is correct.
     */
    @Override
    public void start()
    {
        targetSpeed = motorMap.get(MotorPackage.MotorLocation.FRONT_RIGHT).motor.getPower();
    }

    public void setKvalues(double kP, double kI, double kD)
    {
        RobotLog.ii(TAG, "PID Values kP %f, kI %f, kD %f", kP, kI, kD);

        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    /*
     * setTarget
     *
     * Sets the target heading.  Almost always zero.
     */
    public void setTarget(double target)
    {
        this.target = target;
    }

    @Override
    public void stop()
    {
        integral = 0;
        targetSpeed = 0;
        correction = 0;
        applyCorrection();
    }

    protected void refresh()
    {
        yaw = getYaw();

        error = target - yaw;
        error = BigDecimal.valueOf(error).setScale(2, RoundingMode.HALF_UP).doubleValue();
        integral = integral + error;
        derivative = error - last_error;
        last_error = error;

        correction = (error * kP) + (integral * kI) + (derivative * kD);
    }

    protected double getYaw()
    {
        return gyro.getHeading();
    }

    protected void sendTelemetry()
    {
        RobotLog.ii(TAG, "Y %f, E %f, I %f, D %f, Correction %f", yaw, error, integral, derivative, correction);
        rightTelemetry.setValue(right);
        leftTelemetry.setValue(left);
        yawTelemetry.setValue(yaw);
        errorTelemetry.setValue(error);
        integralTelemetry.setValue(integral);
        derivativeTelemetry.setValue(derivative);
        correctionTelemetry.setValue(correction);
        lastSpeedTelemetry.setValue(targetSpeed);
    }

    protected void applyCorrectionForward()
    {
        right = targetSpeed - (correction * polarity);
        left = targetSpeed + (correction * polarity);

        // RobotLog.ii(TAG, "Left %f, Right %f", left, right);

        motorMap.get(MotorPackage.MotorLocation.FRONT_RIGHT).motor.setPower(right);
        motorMap.get(MotorPackage.MotorLocation.BACK_RIGHT).motor.setPower(right);
        motorMap.get(MotorPackage.MotorLocation.FRONT_LEFT).motor.setPower(left);
        motorMap.get(MotorPackage.MotorLocation.BACK_LEFT).motor.setPower(left);
    }

    protected void applyCorrectionRight()
    {
        right = targetSpeed - (correction * polarity);
        left = targetSpeed + (correction * polarity);

        RobotLog.ii(TAG, "Left %f, Right %f", left, right);

        motorMap.get(MotorPackage.MotorLocation.FRONT_RIGHT).motor.setPower(right);
        motorMap.get(MotorPackage.MotorLocation.BACK_RIGHT).motor.setPower(-right);
        motorMap.get(MotorPackage.MotorLocation.FRONT_LEFT).motor.setPower(-left);
        motorMap.get(MotorPackage.MotorLocation.BACK_LEFT).motor.setPower(left);
    }

    protected void applyCorrection()
    {
        switch (direction) {
            case FORWARD:
                applyCorrectionForward();
                break;
            case BACKWARD:
                break;
            case LEFT:
                break;
            case RIGHT:
                applyCorrectionRight();
                break;
        }
    }

    @Override
    public boolean timeslice()
    {
        refresh();
        applyCorrection();
        if (DEBUG) sendTelemetry();
        return false;
    }

    public String toString()
    {
        return "AutopilotTask";
    }
}
