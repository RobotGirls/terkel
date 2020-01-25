package team25core;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;

/*
 * MRGyro
 *
 * A simple interface to the gyro functionality of the Modern Robotics gyro.
 *
 * NOTE: This gyro has a tendency to float.  Users SHOULD call resetZAxisIntegrator()
 *   from within start() to account to prolonged waiting time before a match on the field.
 *
 *   Users MUST call calibrate() when initializing the robot, and must
 *   not move the robot while calibration is being performed.
 */
public class MRGyro implements RobotGyro {

    ModernRoboticsI2cGyro modernRoboticsI2cGyro;

    public MRGyro(ModernRoboticsI2cGyro gyro)
    {
        this.modernRoboticsI2cGyro = gyro;
    }

    @Override
    public double getHeading()
    {
        return modernRoboticsI2cGyro.getIntegratedZValue();
    }
}
