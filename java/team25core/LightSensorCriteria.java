package team25core;

/*
 * FTC Team 25: cmacfarl, February 18, 2016
 */

import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.util.RobotLog;

/*
 * Looks for a white line.
 */
public class LightSensorCriteria implements SensorCriteria {

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

    public LightSensorCriteria(LightSensor sensor, double min, double max)
    {
        this.sensor = sensor;
        this.polarity = LightPolarity.WHITE;
        this.min = min;
        this.max = max;
        this.threshold = (min + ((max - min)/2));
    }

    public LightSensorCriteria(LightSensor sensor, LightPolarity polarity, double min, double max)
    {
        this.sensor = sensor;
        this.polarity = polarity;
        this.min = min;
        this.max = max;
        this.threshold = (min + ((max - min)/2));
    }

    public void setThreshold(double percent) {
        this.threshold = max - ((max - min) * percent);
    }

    @Override
    public boolean satisfied()
    {
        RobotLog.i("251 Light: %f, Threshold: %f", 100 * sensor.getRawLightDetected(), threshold);
        if (polarity == LightPolarity.WHITE) {
            if (sensor.getRawLightDetected() > threshold) {
                return true;
            }
        } else if (polarity == LightPolarity.BLACK) {
            if (sensor.getRawLightDetected() < threshold) {
                return true;
            }
        }
        return false;
    }
}

