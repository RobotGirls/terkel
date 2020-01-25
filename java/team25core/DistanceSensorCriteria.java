package team25core;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class DistanceSensorCriteria implements SensorCriteria {

    private int cm;
    private DistanceSensor distanceSensor;

    public DistanceSensorCriteria(DistanceSensor sensor, int cm)
    {
        this.distanceSensor = sensor;
        this.cm = cm;
    }

    @Override
    public boolean satisfied()
    {
        if (distanceSensor.getDistance(DistanceUnit.CM) < cm) {
            return true;
        } else {
            return false;
        }
    }
}
