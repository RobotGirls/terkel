package team25core;

import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.util.RobotLog;

public class OpticalDistanceSensorCriteria implements SensorCriteria {

    public enum LightPolarity {
        BLACK,
        WHITE,
    }

    protected int light;

    protected double min;
    protected double max;
    protected double threshold;
    protected LightSensor sensor;
    protected LightPolarity polarity;
    protected String name;

    public OpticalDistanceSensorCriteria(LightSensor sensor, double min, double max)
    {
        this.sensor = sensor;
        this.polarity = LightPolarity.WHITE;
        this.min = min;
        this.max = max;
        this.threshold = (min + ((max - min)/2));
    }

    public OpticalDistanceSensorCriteria(LightSensor sensor, LightPolarity polarity, double min, double max)
    {
        this.sensor = sensor;
        this.polarity = polarity;
        this.min = min;
        this.max = max;
        this.threshold = (min + ((max - min)/2));
    }

    public void setThreshold(double percent) {
        this.threshold = (max - ((max - min) * percent));
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean satisfied()
    {
        RobotLog.v("Device: %s, Light: %f, Threshold: %f", sensor.getConnectionInfo(), sensor.getRawLightDetected(), threshold);

        if (polarity == LightPolarity.WHITE) {
            if (sensor.getRawLightDetected() > threshold) {
                RobotLog.i("Device: %s, %s, White: %f, Threshold: %f", sensor.getConnectionInfo(), name, sensor.getRawLightDetected(), threshold);
                return true;
            }
        } else if (polarity == LightPolarity.BLACK) {
            if (sensor.getRawLightDetected() < threshold) {
                RobotLog.i("Device: %s, %s, Black: %f, Threshold: %f", sensor.getConnectionInfo(), name, sensor.getRawLightDetected(), threshold);
                return true;
            }
        }
        return false;
    }
}

