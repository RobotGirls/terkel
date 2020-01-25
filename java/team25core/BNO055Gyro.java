package team25core;

import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

/*
 * BNO055Gyro
 *
 * A simple interface to the gyro functionality of the BNO055 IMU, commonly
 * found within Expansion and Control Hubs.
 *
 * Note that extensive testing indicates that using the BNO055 to determine
 * heading via rotation around an axis only works around the Z axis when the
 * hub is laying flat.  If the hub is mounted vertically, inconsistent results
 * are obtained.
 *
 * WARNING: Do not use for competition if the hub is mounted vertically.
 */
public class BNO055Gyro implements RobotGyro {

    BNO055IMU imu;

    public BNO055Gyro(BNO055IMU imu)
    {
        this.imu = imu;
    }

    @Override
    public double getHeading()
    {
        Orientation angles   = imu.getAngularOrientation(AxesReference.EXTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return angles.firstAngle;
    }
}
